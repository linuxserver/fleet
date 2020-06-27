/*
 * Copyright (c)  2020 LinuxServer.io
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.linuxserver.fleet.v2.db;

import io.linuxserver.fleet.v2.key.ImageKey;
import io.linuxserver.fleet.v2.types.Image;
import io.linuxserver.fleet.v2.types.docker.DockerCapability;
import io.linuxserver.fleet.v2.types.meta.template.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ImageTemplateFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageTemplateFactory.class);

    private static final String ClearTemplateData      = "{CALL Image_ClearTemplateData(?,?,?,?,?,?)}";
    private static final String StoreTemplateBase      = "{CALL Image_StoreTemplateBase(?,?,?,?,?,?)}";
    private static final String StoreTemplatePort      = "{CALL Image_StoreTemplatePort(?,?,?,?)}";
    private static final String StoreTemplateVolume    = "{CALL Image_StoreTemplateVolume(?,?,?,?)}";
    private static final String StoreTemplateEnv       = "{CALL Image_StoreTemplateEnv(?,?,?,?)}";
    private static final String StoreTemplateDevice    = "{CALL Image_StoreTemplateDevice(?,?,?)}";
    private static final String StoreTemplateExtra     = "{CALL Image_StoreTemplateExtra(?,?,?)}";

    private static final String GetImageTemplateBase   = "{CALL Image_GetTemplateBase(?)}";
    private static final String GetImageTemplates      = "{CALL Image_GetTemplates(?)}";

    public final ImageTemplateHolder makeTemplateHolder(final Connection connection, final ImageKey imageKey) throws SQLException {

        String  registryUrl            = null;
        String  restartPolicy          = null;
        boolean hostNetworkingEnabled  = false;
        boolean privilegedMode         = false;

        try (final CallableStatement call = connection.prepareCall(GetImageTemplateBase)) {

            call.setInt(1, imageKey.getId());
            final ResultSet results = call.executeQuery();

            if (results.next()) {

                registryUrl           = results.getString("RepositoryUrl");
                restartPolicy         = results.getString("RestartPolicy");
                hostNetworkingEnabled = results.getBoolean("HostNetworkEnabled");
                privilegedMode        = results.getBoolean("PrivilegedMode");
            }
        }

        final ImageTemplateHolder templateHolder =  new ImageTemplateHolder(registryUrl,
                                                                            restartPolicy,
                                                                            hostNetworkingEnabled,
                                                                            privilegedMode);

        enrichHolderWithTemplates(connection, imageKey, templateHolder);

        return templateHolder;
    }

    public final void storeImageTemplates(final Connection connection, final Image image) throws SQLException {

        CallableStatement clearTemplatesCall  = null;
        CallableStatement storePortCall       = null;
        CallableStatement storeVolumeCall     = null;
        CallableStatement storeEnvCall        = null;
        CallableStatement storeDeviceCall     = null;
        CallableStatement storeCapabilityCall = null;
        CallableStatement storeBaseCall       = null;

        try {

            connection.setAutoCommit(false);

            clearTemplatesCall  = connection.prepareCall(ClearTemplateData);
            storeBaseCall       = connection.prepareCall(StoreTemplateBase);
            storePortCall       = connection.prepareCall(StoreTemplatePort);
            storeVolumeCall     = connection.prepareCall(StoreTemplateVolume);
            storeEnvCall        = connection.prepareCall(StoreTemplateEnv);
            storeDeviceCall     = connection.prepareCall(StoreTemplateDevice);
            storeCapabilityCall = connection.prepareCall(StoreTemplateExtra);

            clearTemplates(           clearTemplatesCall,  image);
            storeTemplateBase(        storeBaseCall,       image);
            storeTemplatePorts(       storePortCall,       image);
            storeTemplateVolumes(     storeVolumeCall,     image);
            storeTemplateEnv(         storeEnvCall,        image);
            storeTemplateDevices(     storeDeviceCall,     image);
            storeTemplateCapabilities(storeCapabilityCall, image);

            connection.commit();

        } catch (SQLException e) {

            LOGGER.error("storeImageTemplates unable to complete transaction, rolling back", e);
            connection.rollback();

            throw new SQLException(e);

        } finally {

            Utils.safeClose(clearTemplatesCall);
            Utils.safeClose(storePortCall);
            Utils.safeClose(storeVolumeCall);
            Utils.safeClose(storeEnvCall);
            Utils.safeClose(storeDeviceCall);
            Utils.safeClose(storeCapabilityCall);

            connection.setAutoCommit(true);
        }
    }

    private void enrichHolderWithTemplates(final Connection connection, final ImageKey imageKey, final ImageTemplateHolder templateHolder) throws SQLException {

        try (final CallableStatement call = connection.prepareCall(GetImageTemplates)) {

            call.setInt(1, imageKey.getId());
            final ResultSet results = call.executeQuery();

            while (results.next()) {

                final String itemType = results.getString("ItemType");
                final String itemName = results.getString("ItemName");
                final String itemDesc = results.getString("ItemDescription");
                final String itemSec  = results.getString("ItemSecondary");

                switch (itemType) {

                    case "Port":
                        templateHolder.addPort(new PortTemplateItem(Integer.parseInt(itemName), itemDesc, PortTemplateItem.Protocol.fromName(itemSec)));
                        break;

                    case "Volume":
                        templateHolder.addVolume(new VolumeTemplateItem(itemName, itemDesc, "1".equalsIgnoreCase(itemSec)));
                        break;

                    case "Env":
                        templateHolder.addEnvironment(new EnvironmentTemplateItem(itemName, itemDesc, itemSec));
                        break;

                    case "Device":
                        templateHolder.addDevice(new DeviceTemplateItem(itemName, itemDesc));
                        break;

                    case "Extra":
                        templateHolder.addCapability(DockerCapability.valueOf(itemName));
                        break;

                    default:
                        LOGGER.warn("Found unknown template type " + itemType);
                }
            }
        }
    }

    private void clearTemplates(final CallableStatement clearTemplatesCall, final Image image) throws SQLException {

        final String volumes = joinStream(image.getMetaData().getTemplates().getVolumes(),      VolumeTemplateItem::getVolume);
        final String ports   = joinStream(image.getMetaData().getTemplates().getPorts(),        (p) -> String.valueOf(p.getPort()));
        final String env     = joinStream(image.getMetaData().getTemplates().getEnv(),          EnvironmentTemplateItem::getEnv);
        final String caps    = joinStream(image.getMetaData().getTemplates().getCapabilities(), DockerCapability::name);
        final String devices = joinStream(image.getMetaData().getTemplates().getDevices(),      DeviceTemplateItem::getDevice);

        int i = 1;
        clearTemplatesCall.setInt(   i++, image.getKey().getId());
        clearTemplatesCall.setString(i++, volumes);
        clearTemplatesCall.setString(i++, ports);
        clearTemplatesCall.setString(i++, env);
        clearTemplatesCall.setString(i++, caps);
        clearTemplatesCall.setString(i,   devices);

        clearTemplatesCall.executeUpdate();
    }

    private <T, R extends CharSequence> String joinStream(final List<T> list, final Function<? super T, ? extends R> getter) {
        return list.stream().map(getter).collect(Collectors.joining(","));
    }

    private void storeTemplateBase(final CallableStatement storeBaseCall, final Image image) throws SQLException {

        int i = 1;
        storeBaseCall.setInt(i++,     image.getKey().getId());
        storeBaseCall.setString(i++,  image.getMetaData().getTemplates().getRegistryUrl());
        storeBaseCall.setString(i++,  image.getMetaData().getTemplates().getRestartPolicy());
        storeBaseCall.setBoolean(i++, image.getMetaData().getTemplates().isHostNetworkingEnabled());
        storeBaseCall.setBoolean(i++, image.getMetaData().getTemplates().isPrivilegedMode());

        final int statusIndex = i;
        storeBaseCall.registerOutParameter(i, Types.VARCHAR);
        storeBaseCall.executeUpdate();

        final DbUpdateStatus status = DbUpdateStatus.valueOf(storeBaseCall.getString(statusIndex));
        LOGGER.info("storeTemplateBase update response=" + status);
    }

    private void storeTemplateCapabilities(final CallableStatement storeCapabilityCall, final Image image) throws SQLException {

        for (DockerCapability cap : image.getMetaData().getTemplates().getCapabilities()) {

            int i = 1;
            storeCapabilityCall.setInt(   i++, image.getKey().getId());
            storeCapabilityCall.setString(i++, cap.name());
            storeCapabilityCall.setNull(  i, Types.VARCHAR);
            storeCapabilityCall.addBatch();
        }

        storeCapabilityCall.executeBatch();
    }

    private void storeTemplateDevices(final CallableStatement storeDeviceCall, final Image image) throws SQLException {

        for (DeviceTemplateItem device : image.getMetaData().getTemplates().getDevices()) {

            int i = 1;
            storeDeviceCall.setInt(   i++, image.getKey().getId());
            storeDeviceCall.setString(i++, device.getDevice());
            storeDeviceCall.setString(i,   device.getDescription());
            storeDeviceCall.addBatch();
        }

        storeDeviceCall.executeBatch();
    }

    private void storeTemplateEnv(final CallableStatement storeEnvCall, final Image image) throws SQLException {

        for (EnvironmentTemplateItem env : image.getMetaData().getTemplates().getEnv()) {

            int i = 1;
            storeEnvCall.setInt(   i++, image.getKey().getId());
            storeEnvCall.setString(i++, env.getEnv());
            storeEnvCall.setString(i++, env.getDescription());
            storeEnvCall.setString(i,   env.getExampleValue());
            storeEnvCall.addBatch();
        }

        storeEnvCall.executeBatch();
    }

    private void storeTemplateVolumes(final CallableStatement storeVolumeCall, final Image image) throws SQLException {

        for (VolumeTemplateItem volume : image.getMetaData().getTemplates().getVolumes()) {

            int i = 1;
            storeVolumeCall.setInt(    i++, image.getKey().getId());
            storeVolumeCall.setString( i++, volume.getVolume());
            storeVolumeCall.setString( i++, volume.getDescription());
            storeVolumeCall.setBoolean(i,   volume.isReadonly());
            storeVolumeCall.addBatch();
        }

        storeVolumeCall.executeBatch();
    }

    private void storeTemplatePorts(final CallableStatement storePortCall, final Image image) throws SQLException {

        for (PortTemplateItem port : image.getMetaData().getTemplates().getPorts()) {

            int i = 1;
            storePortCall.setInt(   i++, image.getKey().getId());
            storePortCall.setInt(   i++, port.getPort());
            storePortCall.setString(i++, port.getDescription());
            storePortCall.setString(i,   port.getProtocol());
            storePortCall.addBatch();
        }

        storePortCall.executeBatch();
    }
}

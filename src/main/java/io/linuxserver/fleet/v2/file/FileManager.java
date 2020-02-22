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

package io.linuxserver.fleet.v2.file;

import io.linuxserver.fleet.core.FleetAppController;
import io.linuxserver.fleet.v2.key.ImageKey;
import io.linuxserver.fleet.v2.service.AbstractAppService;
import io.linuxserver.fleet.v2.types.FilePathDetails;
import io.linuxserver.fleet.v2.types.internal.ImageAppLogo;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileManager extends AbstractAppService {

    private static final String imageDirName = "images";

    private final String publicSafeImagesDir;
    private final Path   staticImagesDir;

    public FileManager(final FleetAppController controller) {
        super(controller);
        staticImagesDir     = Paths.get(getProperties().getStaticFilesPath().toString(), imageDirName);
        publicSafeImagesDir = "/" + imageDirName;
        makeImageUploadDir();
    }

    public final FilePathDetails saveImageLogo(final ImageAppLogo logo) {

        if (logo.getMimeType().startsWith("image/")) {

            try {

                final FilePathDetails filePathDetails = makeFilePathDetails(logo);

                final File logoFile = new File(filePathDetails.getFullAbsolutePathWithFileName());
                if (logoFile.exists()) {

                    final boolean deleted = logoFile.delete();
                    if (!deleted) {

                        getLogger().warn("Unable to delete file: " + logoFile);
                        return null;
                    }
                }

                boolean created = logoFile.createNewFile();
                if (created) {

                    writeDataToFile(logo, logoFile);
                    return filePathDetails;

                } else {
                    getLogger().warn("Unable to delete file: " + logoFile);
                    return null;
                }

            } catch (IOException e) {

                getLogger().error("Unable to create logo file.", e);
                throw new RuntimeException(e);
            }

        } else {
            throw new IllegalArgumentException("Disallowed mimeType for file: " + logo.getMimeType());
        }
    }

    private FilePathDetails makeFilePathDetails(final ImageAppLogo logo) {

        return new FilePathDetails(makePathSafeFileName(logo.getImageKey()) + logo.getFileExtension(),
                                   staticImagesDir.toString(),
                                   publicSafeImagesDir);
    }

    private String makePathSafeFileName(final ImageKey key) {
        return key.getAsRepositoryAndImageName().replace("/", "_");
    }

    private void writeDataToFile(final ImageAppLogo logo, final File logoFile) throws IOException {

        try (final InputStream initialStream = logo.getRawDataStream();
             final OutputStream out = new FileOutputStream(logoFile)) {

            byte[] buffer = new byte[8 * 1024];
            int bytesRead;
            while ((bytesRead = initialStream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }

    private void makeImageUploadDir() {

        final File imageDir = new File(staticImagesDir.toString());
        if (!imageDir.exists()) {

            getLogger().info("Creating new image directory for uploaded logos");
            final boolean created = imageDir.mkdir();
            if (!created) {
                throw new RuntimeException("Unable to create uploaded file dir. Check permissions");
            }
        }
    }
}

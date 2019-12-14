package io.linuxserver.fleet.v2.client.rest.marshalling;

import java.io.IOException;

public interface MarshallingStrategy {

    /**
     * <p>
     * Converts a given string value into its representative object type.
     * </p>
     * @param value
     *      The value to convert to an object
     * @param classType
     *      The object class definition
     * @return
     *      The converted object
     */
    <T> T unmarshall(String value, Class<T> classType) throws IOException;

    /**
     * <p>
     * Converts an object into a single representative string value.
     * </p>
     * @param value
     *      The object to convert
     * @return
     *      The result of the conversion
     */
    String marshall(Object value) throws IOException;

    /**
     * <p>
     * The content type of the payloads represented by this strategy.
     * </p>
     */
    String getContentType();
}

package net.orbyfied.carbon.config;

/**
 * An exception related to loading
 * or saving a configuration.
 */
public class ConfigurationException extends RuntimeException {

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Exception e) {
        super(message, e);
    }

    public ConfigurationException(Exception e) {
        super(e);
    }

}

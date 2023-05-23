package io.github.pleuvoir.web3.config;

import io.github.pleuvoir.web3.utils.Propertiesable;
import lombok.Data;

/**
 * @author <a href="mailto:pleuvior@foxmail.com">pleuvoir</a>
 */
@Data
public class AppConfig implements Propertiesable {

    private String infuraApiKey;

    private static AppConfig INSTANCE = new AppConfig();

    public static AppConfig getInstance() {
        return INSTANCE;
    }

    public static void init() {
        INSTANCE.load("config.properties");
    }
}

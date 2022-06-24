package framework;

import utils.LoggerManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Environment {
    private Properties properties;
    private static final LoggerManager log = LoggerManager.getInstance();
    private static Environment instance;

    private Environment() {
        initialize();
    }

    public static Environment getInstance() {
        if(instance == null) {
            instance = new Environment();
        }
        return instance;
    }

    private void initialize() {
        log.info("Reading Properties File...");
        properties = new Properties();
        try {
            File file = new File("gradle.properties");
            FileReader fileReader = new FileReader(file);
            properties.load(fileReader);
            fileReader.close();
        } catch (IOException e) {
            log.error("Unable to read properties file");
        }
    }

    public String getEnvironmentSetting(String setting) {
        return (String) getInstance().properties.get(setting);
    }

    public String getBaseURL() {
        return getEnvironmentSetting("baseURL");
    }

    public String getUserName() {
        return getEnvironmentSetting("userName");
    }

    public String getPassword() {
        return getEnvironmentSetting("password");
    }

    public String getBasePath() {
        return getEnvironmentSetting("basePath");
    }

    public String getProjectEndPoint() {
        return getEnvironmentSetting("projectEndPoint");
    }

    public String getProjectByIdEndpoint() {
        return getEnvironmentSetting("projectByIdEndpoint");
    }
}

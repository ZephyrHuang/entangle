package me.zephyr.entangle.application.configurator;

import me.zephyr.entangle.EntangleBootApplication;
import me.zephyr.entangle.config.BaseConfig;
import me.zephyr.entangle.util.ConfigurationUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.core.env.ConfigurableEnvironment;

import java.io.File;

public class ConfigurationLocationConfigurator {
  private static final Logger logger = LoggerFactory.getLogger(ConfigurationLocationConfigurator.class);

  public static void config(ConfigurableEnvironment environment) {
    String configLocation = environment.getProperty(BaseConfig.KEY_CONFIGURE_PATH);
    if (StringUtils.isNotBlank(configLocation)) {
      logger.info("启动环境中已有参数：{}，将以它作为配置文件。", BaseConfig.KEY_CONFIGURE_PATH);
      return;
    }
    configLocation = deducePathOfConfig();
    ConfigurationUtils.addPropertyToEnvironment(environment, BaseConfig.KEY_CONFIGURE_PATH, configLocation);
    logger.info("启动环境中没有参数：{}，将以 {} 作为配置文件。", BaseConfig.KEY_CONFIGURE_PATH, configLocation);
  }

  //~ private methods --------------------------------------------------------------------------------------------------

  private static String deducePathOfConfig() {
    String pathOfJar = new ApplicationHome(EntangleBootApplication.class).toString();//jar包同级目录
    String pathOfConfig = pathOfJar + "\\" + BaseConfig.DEFAULT_CONFIGURATION_NAME;//配置文件路径，一般放在jar包同级目录
    if (isConfigurationFileValid(pathOfConfig)) {
      pathOfConfig = "file:\\" + pathOfConfig;
    } else {
      pathOfConfig = BaseConfig.FALLBACK_PATH_OF_CONFIGURE;//缺省配置文件
    }
    return pathOfConfig;
  }

  private static boolean isConfigurationFileValid(String pathOfConfig) {
    File file = new File(pathOfConfig);
    return file.exists() && file.isFile();
  }

}

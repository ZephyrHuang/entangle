package me.zephyr.entangle.application.configurator;

import me.zephyr.entangle.EntangleBootApplication;
import me.zephyr.entangle.config.BaseConfig;
import me.zephyr.entangle.exception.FileCreationFailedException;
import me.zephyr.entangle.exception.FileMissingException;
import me.zephyr.entangle.util.ConfigurationUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 在环境中存入配置文件的路径，供 {@link PropertySourcesPlaceholderConfigurer} 使用。
 */
public class ConfigurationLocationConfigurator {
  private static final Logger logger = LoggerFactory.getLogger(ConfigurationLocationConfigurator.class);

  public static void config(ConfigurableEnvironment environment) {
    String configLocation = environment.getProperty(BaseConfig.KEY_CONFIGURE_PATH);
    if (StringUtils.isNotBlank(configLocation)) {
      logger.info("启动环境中已有参数：{}，将以它作为配置文件。", BaseConfig.KEY_CONFIGURE_PATH);
      return;
    }
    configLocation = initializeConfigIfNecessary();
    ConfigurationUtils.addPropertyToEnvironment(environment, BaseConfig.KEY_CONFIGURE_PATH, configLocation);
    logger.info("启动环境中没有参数：{}，将以 {} 作为配置文件。", BaseConfig.KEY_CONFIGURE_PATH, configLocation);
  }

  //~ private methods --------------------------------------------------------------------------------------------------

  /**
   * 返回 jar 包同级目录下的配置文件的全路径。若没有则创建默认的配置文件。
   */
  private static String initializeConfigIfNecessary() {
    String pathOfJar = new ApplicationHome(EntangleBootApplication.class).toString();//jar包同级目录
    String pathOfConfig = pathOfJar + "\\" + BaseConfig.DEFAULT_CONFIGURATION_NAME;//配置文件路径，一般放在jar包同级目录
    if (isConfigurationFileNotValid(pathOfConfig)) {
      createInitialConfiguration(pathOfConfig);//尝试在jar包同级目录下创建默认的配置文件
    }
    return ResourceUtils.FILE_URL_PREFIX + pathOfConfig;
  }

  private static boolean isConfigurationFileNotValid(String pathOfConfig) {
    File file = new File(pathOfConfig);
    return !file.exists() || !file.isFile();
  }

  private static void createInitialConfiguration(String pathOfConfigToCreate) {
    InputStream is = ClassUtils.getDefaultClassLoader().getResourceAsStream(BaseConfig.CONFIG_EXAMPLE_PATH);
    if (is == null) {
      throw new FileMissingException("类路径下缺少文件：" + BaseConfig.CONFIG_EXAMPLE_PATH);
    }
    File configToCreate = new File(pathOfConfigToCreate);

    try {
      FileUtils.copyToFile(is, configToCreate);
    } catch (IOException e) {
      throw new FileCreationFailedException("文件创建失败：" + configToCreate.getPath(), e);
    }
  }
}

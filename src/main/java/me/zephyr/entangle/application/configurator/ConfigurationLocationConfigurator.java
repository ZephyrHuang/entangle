package me.zephyr.entangle.application.configurator;

import me.zephyr.entangle.EntangleBootApplication;
import me.zephyr.entangle.config.BaseConfig;
import me.zephyr.entangle.exception.FileCreationFailedException;
import me.zephyr.entangle.exception.FileMissingException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 在环境中存入配置文件的路径，供 {@link PropertySourcesPlaceholderConfigurer} 使用。
 */
public class ConfigurationLocationConfigurator {
  private static final Logger logger = LoggerFactory.getLogger(ConfigurationLocationConfigurator.class);

  public static void config(SpringApplication application, String[] args) {
    String configLocation = getConfigLocationFromArgs(args);
    if (StringUtils.isNotBlank(configLocation)) {
      logger.info("启动环境中已有参数：{}，将以 {} 作为配置文件。", BaseConfig.KEY_CONFIGURE_PATH, configLocation);
    } else {
      configLocation = initializeConfigIfNecessary();
      logger.info("启动环境中没有参数：{}，将以 {} 作为配置文件。", BaseConfig.KEY_CONFIGURE_PATH, configLocation);
    }
    customizeLocationOfConfigFile(configLocation, application);
  }

  //~ private methods --------------------------------------------------------------------------------------------------

  /**
   * 在启动参数中搜索 {@linkplain BaseConfig#KEY_CONFIGURE_PATH 自定义的配置路径}。
   */
  private static String getConfigLocationFromArgs(String[] args) {
    String result = "";
    if (ArrayUtils.isEmpty(args)) {
      return result;
    }
    for (String arg : args) {
      if (!arg.contains(BaseConfig.KEY_CONFIGURE_PATH)) {
        continue;
      }
      if (!arg.contains("=")) {
        throw new IllegalArgumentException("启动参数 " + BaseConfig.KEY_CONFIGURE_PATH + " 格式有误，缺少等号。");
      }
      result = arg.substring(arg.indexOf("=") + 1);
      break;
    }
    return result;
  }

  private static void customizeLocationOfConfigFile(String fullPathOfFile, SpringApplication application) {
    int last4wrdSlash = fullPathOfFile.lastIndexOf("/");
    int lastBackSlash = fullPathOfFile.lastIndexOf("\\");
    int lastSlash = Math.max(last4wrdSlash, lastBackSlash);
    String parentPath = fullPathOfFile.substring(0, lastSlash + 1);//不含文件名的路径
    String configFileName = fullPathOfFile.substring(lastSlash + 1);//不含路径的文件名
    //去掉后缀
    int idxOfDot = configFileName.lastIndexOf(".");
    if (idxOfDot != -1) {
      configFileName = configFileName.substring(0, idxOfDot);
    }

    Map<String, Object> properties = new HashMap<>(2);
    properties.put(ConfigFileApplicationListener.CONFIG_NAME_PROPERTY, "application," + configFileName);
    if (StringUtils.isNotBlank(parentPath)) {
      properties.put(ConfigFileApplicationListener.CONFIG_ADDITIONAL_LOCATION_PROPERTY, parentPath);
    }
    application.setDefaultProperties(properties);
  }

  /**
   * 返回 jar 包同级目录下的配置文件的全路径。若没有则创建默认的配置文件。
   */
  private static String initializeConfigIfNecessary() {
    String pathOfJar = new ApplicationHome(EntangleBootApplication.class).toString();//jar包同级目录
    String pathOfConfig = pathOfJar + "\\" + BaseConfig.DEFAULT_CONFIGURATION;//配置文件路径，一般放在jar包同级目录
    if (isConfigurationFileNotValid(pathOfConfig)) {
      logger.info("jar 包同级目录下没有配置文件，现在自动创建默认的配置文件。");
      createInitialConfiguration(pathOfConfig);//尝试在jar包同级目录下创建默认的配置文件
    } else {
      logger.info("jar 包同级目录下已有配置文件。");
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

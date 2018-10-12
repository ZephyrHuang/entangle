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
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

/**
 * 在环境中存入配置文件的路径，供容器使用。
 */
public class ConfigurationLocationConfigurator {
  private static final Logger logger = LoggerFactory.getLogger(ConfigurationLocationConfigurator.class);
  /**
   * ip 和端口，形如：
   *
   * <ul>
   *   <li>1.2.3.4:5</li>
   *   <li>1.2.3.4 5</li>
   * </ul>
   *
   * 前后可以有空格。
   */
  private static final Pattern hostPattern = Pattern.compile(
      "^\\s*(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(?:\\s+|:)(\\d+)(?:\\s*)$");


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
      result = arg.substring(arg.indexOf("=") + 1);//“=”号后面的内容
      break;
    }
    return result;
  }

  /**
   * 在 Spring 上下文中设置配置文件的文件名和路径
   * @param fullPathOfFile 配置文件的完整路径
   * @param application    {@link SpringApplication}
   */
  private static void customizeLocationOfConfigFile(String fullPathOfFile, SpringApplication application) {
    int last4wrdSlash = fullPathOfFile.lastIndexOf("/");
    int lastBackSlash = fullPathOfFile.lastIndexOf("\\");
    int lastSlash = Math.max(last4wrdSlash, lastBackSlash);//支持两种斜杠混用
    String parentPath = fullPathOfFile.substring(0, lastSlash + 1);//不含文件名的路径
    String configFileName = fullPathOfFile.substring(lastSlash + 1);//不含路径的文件名
    //去掉后缀
    int idxOfDot = configFileName.lastIndexOf(".");
    if (idxOfDot != -1) {
      configFileName = configFileName.substring(0, idxOfDot);
    }

    Map<String, Object> properties = new HashMap<>(2);
    //优先取自定义的配置，若取不到再从 application.yml 中取。
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
    String pathOfJar = new ApplicationHome(EntangleBootApplication.class).toString();//jar 包同级目录
    String pathOfConfig = pathOfJar + "\\" + BaseConfig.DEFAULT_CONFIGURATION;//配置文件路径，一般放在 jar 包同级目录
    if (isConfigurationFileNotValid(pathOfConfig)) {
      logger.info("jar 包同级目录下没有配置文件，现在自动创建默认的配置文件。");
      createInitialConfiguration(pathOfConfig);//尝试在 jar 包同级目录下创建默认的配置文件
    } else {
      logger.info("jar 包同级目录下已有配置文件。");
    }
    return ResourceUtils.FILE_URL_PREFIX + pathOfConfig;// file:C:\example\path.yml
  }

  private static boolean isConfigurationFileNotValid(String pathOfConfig) {
    File file = new File(pathOfConfig);
    return !file.exists() || !file.isFile();
  }

  /**
   * 创建初始的配置文件。
   * @param pathOfConfigToCreate 待创建的配置文件的路径
   */
  private static void createInitialConfiguration(String pathOfConfigToCreate) {
    InputStream is = ClassUtils.getDefaultClassLoader().getResourceAsStream(BaseConfig.CONFIG_EXAMPLE_PATH);
    if (is == null) {
      throw new FileMissingException("类路径下缺少文件：" + BaseConfig.CONFIG_EXAMPLE_PATH);
    }

    String hostFromUser = askForHost();//初始化配置时要求用户输入对方端点的 ip 和端口
    File configToCreate = new File(pathOfConfigToCreate);
    try {
      //复制文件
      FileUtils.copyToFile(is, configToCreate);
      //将 IP 端口写进去
      List<String> newLines = Files.readAllLines(configToCreate.toPath(), StandardCharsets.UTF_8).stream()
          .map(line -> line.contains("targetHost") ? (line + " " + hostFromUser) : line)
          .collect(toList());
      Files.write(configToCreate.toPath(), newLines);
    } catch (IOException e) {
      throw new FileCreationFailedException("文件创建失败：" + configToCreate.getPath(), e);
    }
  }

  /**
   * 要求用户从控制台输入必要参数：IP 和端口。
   *
   * @return 用户输入的"IP:端口"。
   */
  private static String askForHost() {
    System.out.println("正在初始化配置，请输入对方端点的 ip 和端口。输入 exit 终止...");
    Scanner scanner = new Scanner(System.in);
    String[] ipAndPort = null;
    while (ipAndPort == null) {
      ipAndPort = doScanInput(scanner);
    }
    return ipAndPort[0] + ":" + ipAndPort[1];
  }

  private static String[] doScanInput(Scanner scanner) {
    String input = scanner.nextLine();
    if ("exit".equals(input) || "quit".equals(input)) {
      throw new RuntimeException("用户终止。");
    }
    Matcher matcher = hostPattern.matcher(input);
    if (!matcher.matches()) {
      return null;
    }
    return new String[] {matcher.group(1), matcher.group(2)};
  }
}

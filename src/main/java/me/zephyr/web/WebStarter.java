package me.zephyr.web;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Properties;
import java.util.function.Consumer;

public class WebStarter {
  public static String KEY_ENV = "env";
  public static String KEY_PORT = "port";
  public static String KEY_WAR_PATH = "warpath";
  public static final String KEY_CONFIGURE_PROPERTIES_PATH = "configure.properties.path";
  private static final Logger logger = LoggerFactory.getLogger(WebStarter.class);
  private static String ENV; // 启动环境（是否在 IDE 中启动）
  private static String VALUE_DEV = "dev"; // 在 IDE 中启动
  private static int PORT;
  private static String WAR_PATH;

  public static void main(String[] args) {
    parseProperties();
    parseArgs(args);
    Server server = new Server(getPort());
    server.setHandler(getHandler());
    try {
      server.start();
    } catch (Exception e) {
      logger.error("服务启动出现异常：", e);
    }
  }

  /**
   * 查找并解析 war 包同级目录下的 {@code configure.properties} 文件。
   */
  private static void parseProperties() {
    try {
      Properties properties = new Properties();
      String propertyPath = resolvePropertiesPath();
      properties.load(new FileInputStream(propertyPath));

      setEnv(properties.getProperty(KEY_ENV, ""));
      setPort(Integer.valueOf(properties.getProperty(KEY_PORT, "8079")));
      setWarPath(properties.getProperty(KEY_WAR_PATH, ""));
    } catch (IOException e) {
      logger.error("未在 war 包同级目录下找到 configure.properties！将优先采用启动时传入的参数，其次采用默认参数。");
    }
  }

  private static String resolvePropertiesPath() throws UnsupportedEncodingException {
    String path = WebStarter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    path = path.substring(1, path.lastIndexOf("/"));
    path = URLDecoder.decode(path, "UTF-8");
    logger.info("在{}下加载 configure.properties", path);
    path = path + "/configure.properties";
    Properties prop = System.getProperties();
    prop.put(KEY_CONFIGURE_PROPERTIES_PATH, "file:" + path);
    return path;
  }

  /**
   * 解析启动时传入的参数。
   *
   * @param args
   */
  private static void parseArgs(String[] args) {
    if (ArrayUtils.isEmpty(args)) {
      return;
    }
    for (String arg : args) {
      subStrAndSet(arg, KEY_ENV, WebStarter::setEnv);
      subStrAndSet(arg, KEY_PORT, (s) -> setPort(Integer.valueOf(s)));
      subStrAndSet(arg, KEY_WAR_PATH, WebStarter::setWarPath);
    }
  }

  /**
   * 若带解析的参数 {@code rawArg} 以 {@code {argKey}=} 开头，且等号后的值非空，则使用 {@code setVariable} 进行赋值。
   *
   * @param rawArg 待解析的参数
   * @param argKey 期望的参数的键值
   * @param setVariable 赋值方法
   */
  private static void subStrAndSet(String rawArg, String argKey, Consumer<String> setVariable) {
    String prefix = argKey + "=";
    if (rawArg.startsWith(prefix)) {
      String tmp = rawArg.substring(prefix.length());
      if (StringUtils.isNotBlank(tmp)) {
        setVariable.accept(tmp);
      }
    }
  }

  private static Handler getHandler() {
    WebAppContext webAppContext = new WebAppContext();
    webAppContext.setClassLoader(Thread.currentThread().getContextClassLoader());
    webAppContext.setContextPath("/transfer");

    if (VALUE_DEV.equalsIgnoreCase(getEnv())) {
      // 从开发环境启动
      logger.debug("正在从 IDE 环境启动...");
      webAppContext.setResourceBase("./src/main/webapp");
      webAppContext.setDescriptor("./src/main/webapp/WEB-INF/web.xml");
    } else {
      // 用 war 包启动
      String warpath = getWarPath();
      logger.info("正在从 war 包启动：{} ...", warpath);
      webAppContext.setWar(warpath);
    }
    return webAppContext;
  }

  //~ getter and setter ------------------------------------------------------------------------------------------------

  public static String getEnv() {
    return ENV;
  }

  public static void setEnv(String ENV) {
    WebStarter.ENV = ENV;
  }

  public static int getPort() {
    return PORT;
  }

  public static void setPort(int port) {
    PORT = port;
  }

  public static String getWarPath() {
    return WAR_PATH;
  }

  public static void setWarPath(String WarPath) {
    WAR_PATH = WarPath;
  }
}

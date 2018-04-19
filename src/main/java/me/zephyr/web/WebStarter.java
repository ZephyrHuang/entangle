package me.zephyr.web;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebStarter {
  private static final Logger logger = LoggerFactory.getLogger(WebStarter.class);
  private static int PORT = 8079;
  private static String ARG_PORT = "port=";

  public static void main(String[] args) {
    parseArgs(args);
    Server server = new Server(PORT);
    server.setHandler(getHandler());
    try {
      server.start();
      //server.join();
    } catch (Exception e) {
      logger.error("服务启动出现异常：", e);
    }
  }

  public static void launch(String[] args) {

  }

  public static int getPort() {
    return PORT;
  }

  public static void setPort(int port) {
    PORT = port;
  }

  private static void parseArgs(String[] args) {
    if (ArrayUtils.isEmpty(args)) {
      return;
    }
    for (String arg : args) {
      if (arg.startsWith(ARG_PORT)) {
        String tmp = arg.substring(ARG_PORT.length());
        PORT = StringUtils.isBlank(tmp) ? PORT : Integer.valueOf(tmp);
      }
    }
  }

  private static Handler getHandler() {
    WebAppContext webAppContext = new WebAppContext();
    webAppContext.setClassLoader(Thread.currentThread().getContextClassLoader());
    webAppContext.setContextPath("/transfer");
    webAppContext.setResourceBase("./src/main/webapp");
    webAppContext.setDescriptor("./src/main/webapp/WEB-INF/web.xml");
    return webAppContext;
  }
}

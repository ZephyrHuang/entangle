package me.zephyr.entangle;

import me.zephyr.entangle.config.BaseConfig;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationHome;

import java.io.File;

import static me.zephyr.util.collection.CollectionUtil.anyStartWith;

/**
 * @see me.zephyr.entangle.config.BaseConfig
 */
@SpringBootApplication
public class EntangleBootApplication {
  private static final Logger logger = LoggerFactory.getLogger(EntangleBootApplication.class);

  public static void main(String[] args) {
    doWithArgs(args);
    SpringApplication.run(EntangleBootApplication.class, args);
  }

  /**
   * 对启动参数做一些处理，这里逻辑如果变多的话考虑重构一下。
   */
  private static void doWithArgs(String[] args) {
    if (!anyStartWith(args, BaseConfig.KEY_CONFIGURE_PATH)) {
      String pathOfJar = new ApplicationHome(EntangleBootApplication.class).toString();//jar包同级目录
      String pathOfConfig = pathOfJar + "\\configure.properties";
      File file = new File(pathOfConfig);
      if (!file.exists() || !file.isFile()) {
        pathOfConfig = BaseConfig.FALLBACK_PATH_OF_CONFIGURE;
      }
      ArrayUtils.add(args, BaseConfig.KEY_CONFIGURE_PATH + "=" + pathOfConfig);
      logger.info("启动环境中没有参数：{}，将以{}作为配置文件。", BaseConfig.KEY_CONFIGURE_PATH, pathOfConfig);
    } else {
      logger.info("启动环境中已有参数：{}，将以它作为配置文件。", BaseConfig.KEY_CONFIGURE_PATH);
    }
  }
}

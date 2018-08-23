package me.zephyr.entangle;

import me.zephyr.entangle.config.BaseConfig;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationHome;

import java.io.File;

import static me.zephyr.util.collection.CollectionUtil.any;
import static me.zephyr.util.collection.CollectionUtil.startsWith;

/**
 * @see me.zephyr.entangle.config.BaseConfig
 */
@SpringBootApplication
public class EntangleBootApplication {
  private static final Logger logger = LoggerFactory.getLogger(EntangleBootApplication.class);

  public static void main(String[] args) {
    String[] newArgs = doWithArgs(args);
    SpringApplication.run(EntangleBootApplication.class, newArgs);
  }

  /**
   * 对启动参数做一些处理，这里逻辑如果变多的话考虑重构一下。
   */
  private static String[] doWithArgs(String[] args) {
    String[] newArgs = args;
    String key = BaseConfig.KEY_CONFIGURE_PATH;
    if (!any(args, startsWith(key, "-D" + key, "--" + key))) {
      String pathOfConfig = deducePathOfConfig();
      newArgs = ArrayUtils.add(args, "--" + key + "=" + pathOfConfig);
      logger.info("启动环境中没有参数：{}，将以{}作为配置文件。", BaseConfig.KEY_CONFIGURE_PATH, pathOfConfig);
    } else {
      logger.info("启动环境中已有参数：{}，将以它作为配置文件。", BaseConfig.KEY_CONFIGURE_PATH);
    }
    return newArgs;
  }

  private static String deducePathOfConfig() {
    String pathOfJar = new ApplicationHome(EntangleBootApplication.class).toString();//jar包同级目录
    String pathOfConfig = pathOfJar + "\\configure.properties";//配置文件路径，一般放在jar包同级目录
    if (isConfigureValid(pathOfConfig)) {
      pathOfConfig = "file:\\" + pathOfConfig;
    } else {
      pathOfConfig = BaseConfig.FALLBACK_PATH_OF_CONFIGURE;//缺省配置文件
    }
    return pathOfConfig;
  }

  private static boolean isConfigureValid(String pathOfConfig) {
    File file = new File(pathOfConfig);
    return file.exists() && file.isFile();
  }
}

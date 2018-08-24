package me.zephyr.entangle.application.configurator;

import me.zephyr.entangle.util.ConfigurationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Random;

public class PickaBannerConfigurator {
  private static final Logger logger = LoggerFactory.getLogger(PickaBannerConfigurator.class);

  public static void config(ConfigurableEnvironment environment) {
    String location = "classpath:banner/";

    //保存所有 banner 的目录
    File dir;
    try {
      dir = ResourceUtils.getFile(ResourceUtils.getURL(location));
    } catch (FileNotFoundException e) {
      logger.info("未找到 {} 目录，不做额外处理。", location);
      return;
    }

    //所有 banner 文件
    File[] files = dir.listFiles();
    if (files == null || files.length == 0) {
      return;
    }

    //随机挑一个 banner
    Arrays.sort(files);
    int pick = new Random().nextInt(files.length);
    File thePicked = files[pick];

    ConfigurationUtils.addPropertyToEnvironment(environment,
        SpringApplication.BANNER_LOCATION_PROPERTY, location + thePicked.getName());
  }
}

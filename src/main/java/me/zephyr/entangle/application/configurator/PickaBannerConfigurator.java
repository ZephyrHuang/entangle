package me.zephyr.entangle.application.configurator;

import me.zephyr.entangle.util.ConfigurationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class PickaBannerConfigurator {
  private static final Logger logger = LoggerFactory.getLogger(PickaBannerConfigurator.class);

  public static void config(ConfigurableEnvironment environment) {
    String dir = "classpath:banner/";
    String pattern = dir + "*.txt";

    //保存所有 banner 的目录
    List<String> banners;
    try {
      PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
      banners = Stream.of(resolver.getResources(pattern)).map(Resource::getFilename).collect(toList());
    } catch (IOException ioe) {
      logger.info("从 {} 目录下读取 banner 文件发生异常，不做额外处理。", dir);
      return;
    }

    //所有 banner 文件
    if (CollectionUtils.isEmpty(banners)) {
      return;
    }

    //随机挑一个 banner
    Collections.sort(banners);
    int pick = new Random().nextInt(banners.size());
    String thePicked = banners.get(pick);

    ConfigurationUtils.addPropertyToEnvironment(environment,
        SpringApplication.BANNER_LOCATION_PROPERTY, dir + thePicked);
  }
}

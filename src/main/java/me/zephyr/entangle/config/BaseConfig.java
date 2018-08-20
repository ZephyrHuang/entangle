package me.zephyr.entangle.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@PropertySource(value = "${base.configure.path}")
public class BaseConfig {
  /**
   * 参数的键值，用来指定 configure.properties 文件的路径
   */
  public static final String KEY_CONFIGURE_PATH = "base.configure.path";
  /**
   * 若 jar 包同级目录下没有 configure.properties 文件，则用这个配置文件
   */
  public static final String FALLBACK_PATH_OF_CONFIGURE = "classpath:configure.properties";

  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }
}

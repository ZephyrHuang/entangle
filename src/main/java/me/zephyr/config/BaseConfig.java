package me.zephyr.config;

import me.zephyr.web.WebStarter;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@ComponentScan(basePackages = "me.zephyr")
@PropertySource(value = "${" + WebStarter.KEY_CONFIGURE_PROPERTIES_PATH + "}")
public class BaseConfig {
  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }
}

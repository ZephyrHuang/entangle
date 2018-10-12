package me.zephyr.entangle.application;

import me.zephyr.entangle.application.configurator.PickaBannerConfigurator;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EnvironmentPreparedListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {
  private List<Consumer<ConfigurableEnvironment>> configurators = new ArrayList<>();

  public EnvironmentPreparedListener() {
    this.configurators.add(PickaBannerConfigurator::config);
  }

  /**
   * 对环境变量做一些自定义处理
   */
  @Override
  public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
    ConfigurableEnvironment environment = event.getEnvironment();
    configurators.forEach(configurator -> configurator.accept(environment));
  }
}

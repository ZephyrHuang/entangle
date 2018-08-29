package me.zephyr.entangle.application;

import me.zephyr.entangle.application.configurator.ConfigurationLocationConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class ApplicationStartingListener implements ApplicationListener<ApplicationStartingEvent> {
  private List<BiConsumer<SpringApplication, String[]>> configurators = new ArrayList<>();

  public ApplicationStartingListener() {
    this.configurators.add(ConfigurationLocationConfigurator::config);
  }

  @Override
  public void onApplicationEvent(ApplicationStartingEvent event) {
    for (BiConsumer<SpringApplication, String[]> configurator : configurators) {
      configurator.accept(event.getSpringApplication(), event.getArgs());
    }
  }
}

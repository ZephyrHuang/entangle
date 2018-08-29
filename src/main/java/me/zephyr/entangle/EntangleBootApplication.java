package me.zephyr.entangle;

import me.zephyr.entangle.application.ApplicationStartingListener;
import me.zephyr.entangle.application.EnvironmentPreparedListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @see me.zephyr.entangle.config.BaseConfig
 */
@SpringBootApplication
public class EntangleBootApplication {
  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(EntangleBootApplication.class);
    app.addListeners(new EnvironmentPreparedListener(), new ApplicationStartingListener());
    app.run(args);
  }
}

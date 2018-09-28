package me.zephyr.entangle;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.awt.*;
import java.awt.datatransfer.Clipboard;

@Configuration
public class BaseConfigTest {
  @Bean
  public Clipboard clipboard() {
    String key = "java.awt.headless";
    String headless = System.getProperty(key);
    System.setProperty(key, "false");//解决无法获取剪贴板的问题
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    if (headless != null) {
      System.setProperty(key, headless);
    } else {
      System.clearProperty(key);
    }
    return clipboard;
  }
}

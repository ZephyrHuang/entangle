package me.zephyr.entangle.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.util.Objects;

import static me.zephyr.entangle.config.BaseConfig.FALLBACK_PATH_OF_CONFIGURE;
import static me.zephyr.entangle.config.BaseConfig.KEY_CONFIGURE_PATH;

@Configuration
@EnableScheduling
@PropertySource(value = "${"+KEY_CONFIGURE_PATH+":"+FALLBACK_PATH_OF_CONFIGURE+"}")
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

  /**
   * 系统剪贴板，本身就是单例的，声明为 bean 方便自动注入。
   * @param clipboardOwner {@link me.zephyr.entangle.clipboard.ClipboardMonitor}
   */
  @Bean
  public Clipboard clipboard(ClipboardOwner clipboardOwner) {
    Objects.requireNonNull(clipboardOwner, "There should be at least one ClipboardOwner instance!");
    String key = "java.awt.headless";
    String headless = System.getProperty(key);
    System.setProperty(key, "false");//解决无法获取剪贴板的问题
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    if (headless != null) {
      System.setProperty(key, headless);
    } else {
      System.clearProperty(key);
    }
    clipboard.setContents(clipboard.getContents(null), clipboardOwner);
    return clipboard;
  }
}

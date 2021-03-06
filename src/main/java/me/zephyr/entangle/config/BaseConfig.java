package me.zephyr.entangle.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableScheduling
public class BaseConfig {
  //~ constants --------------------------------------------------------------------------------------------------------

  /**
   * 参数的键值，用来指定 configure.yml 文件的路径
   */
  public static final String KEY_CONFIGURE_PATH = "entangle.configure.path";
  /**
   * 缺省的配置文件名
   */
  public static final String DEFAULT_CONFIGURATION_NAME = "entangle-configuration";
  /**
   * 缺省的配置文件后缀
   */
  public static final String DEFAULT_CONFIGURATION_SUFFIX = ".yml";
  /**
   * 缺省的配置文件完整名称
   */
  public static final String DEFAULT_CONFIGURATION = DEFAULT_CONFIGURATION_NAME + DEFAULT_CONFIGURATION_SUFFIX;
  /**
   * 用来在 jar 包同级目录下初始化配置文件
   */
  public static final String CONFIG_EXAMPLE_PATH = "configuration-example.yml";

  //~-------------------------------------------------------------------------------------------------------------------


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

  @Bean(AbstractApplicationContext.APPLICATION_EVENT_MULTICASTER_BEAN_NAME)
  public ApplicationEventMulticaster applicationEventMulticaster(@Qualifier("commonExecutor") TaskExecutor taskExecutor) {
    SimpleApplicationEventMulticaster multicaster = new SimpleApplicationEventMulticaster();
    multicaster.setTaskExecutor(taskExecutor);
    return multicaster;
  }

  /**
   * 公用线程池
   */
  @Bean
  public TaskExecutor commonExecutor() {
    ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
    pool.setCorePoolSize(5);
    pool.setQueueCapacity(10);
    pool.setMaxPoolSize(15);
    pool.setKeepAliveSeconds(30);
    pool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    pool.setThreadNamePrefix("commonExecutor");
    return pool;
  }
}

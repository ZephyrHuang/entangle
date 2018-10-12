package me.zephyr.entangle.util;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

public class ConfigurationUtils {
  private ConfigurationUtils() {}

  /**
   * 向 {@linkplain ConfigurableEnvironment environment} 中添加属性
   */
  public static void addPropertyToEnvironment(ConfigurableEnvironment env, String key, String value) {
    Assert.notNull(env, "ConfigurableEnvironment must not be null!");
    Assert.hasText(key, "key must not be blank!");
    Assert.hasText(value, "value must not be blank!");

    PropertySource<?> existingPropSource = initPropertySourceIfRequired(env, "entangleAppProperties");
    Object source = existingPropSource.getSource();
    //只要应用的 PropertySource 要统一管理，类型就不会有问题
    Assert.isInstanceOf(Map.class, source, "应用自定义的 PropertySource 类型不对。");
    @SuppressWarnings("unchecked")
    Map<String, Object> map = (Map<String, Object>) source;
    map.put(key, value);
  }

  /**
   * 从 {@linkplain ConfigurableEnvironment environment} 中获取指定名称的 {@link PropertySource}，
   * 若没有就新建一个，并放入 {@linkplain ConfigurableEnvironment environment}。
   */
  private static PropertySource<?> initPropertySourceIfRequired(ConfigurableEnvironment env, String propertySourceName) {
    PropertySource<?> existingPropSource = env.getPropertySources().get(propertySourceName);
    if (existingPropSource == null) {
      existingPropSource = new MapPropertySource(propertySourceName, new HashMap<>());
      env.getPropertySources().addFirst(existingPropSource);
    }
    return existingPropSource;
  }
}

package me.zephyr.entangle.config.property;

import me.zephyr.entangle.config.WebSocketConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@ConfigurationProperties(prefix = "entangle")
public class EntangleProperties implements InitializingBean {
  /**
   * 根据 targetHost 拼接成的
   */
  private String targetUrl;
  /**
   * 对方 ip
   */
  private String targetHost;
  /**
   * 创建 ws 端点时用到的源白名单
   */
  private String allowedOrigins;
  /**
   * 连超时时间
   */
  private Long connectTimeOut;
  /**
   * 传输文本的最大尺寸
   */
  private Integer maxTextBuffer;

  @Override
  public void afterPropertiesSet() {
    Assert.hasText(targetHost, "目标 IP 端口未配置");
    targetUrl = "ws://" + targetHost + "/entangle" + WebSocketConfig.STOMP_ENDPOINT;
  }

  //~-------------------------------------------------------------------------------------------------------------------

  /**
   * 根据 targetHost 拼接成的完整 url
   */
  public String getTargetUrl() {
    return targetUrl;
  }

  public String getTargetHost() {
    return targetHost;
  }

  public void setTargetHost(String targetHost) {
    this.targetHost = targetHost;
  }

  public String getAllowedOrigins() {
    return allowedOrigins;
  }

  public void setAllowedOrigins(String allowedOrigins) {
    this.allowedOrigins = allowedOrigins;
  }

  public Long getConnectTimeOut() {
    return connectTimeOut;
  }

  public void setConnectTimeOut(Long connectTimeOut) {
    this.connectTimeOut = connectTimeOut;
  }

  public Integer getMaxTextBuffer() {
    return maxTextBuffer;
  }

  public void setMaxTextBuffer(Integer maxTextBuffer) {
    this.maxTextBuffer = maxTextBuffer;
  }
}

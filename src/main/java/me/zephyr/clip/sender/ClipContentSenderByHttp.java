package me.zephyr.clip.sender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.SocketTimeoutException;


@Component
@Qualifier("httpSender")
public class ClipContentSenderByHttp implements ClipContentSender {
  private static final Logger logger = LoggerFactory.getLogger(ClipContentSenderByHttp.class);

  @Autowired
  private RestTemplate restTemplate;
  @Value("${http.send.targetUrl:http://10.191.196.183:8079/transfer/clip/set}")
  private String targetUrl;

  @Override
  public <T> void send(T content) {
    if (content.getClass().equals(String.class)) {
      try {
        restTemplate.postForObject(targetUrl, content, String.class);
      } catch (ResourceAccessException e) {
        if (e.getCause() != null && SocketTimeoutException.class.equals(e.getCause().getClass())) {
          logger.error("请求{}超时。", targetUrl);
        } else {
          logger.error("请求{}异常。", targetUrl);
        }
      } catch (Exception e) {
        logger.error("向{}发送请求时出现异常：", targetUrl);
        logger.error("原异常：", e);
      }
    }
  }
}

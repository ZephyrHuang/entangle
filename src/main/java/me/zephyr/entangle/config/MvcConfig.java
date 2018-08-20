package me.zephyr.entangle.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer {
  @Override
  public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
    configurer.enable();
  }

  @Bean
  public ClientHttpRequestFactory clientHttpRequestFactory(
      @Value("${http.connectTimeOut:1000}") String connectTimeOut,
      @Value("${http.readTimeOut:1000}") String readTimeOut) {

    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(Integer.valueOf(connectTimeOut));
    factory.setReadTimeout(Integer.valueOf(readTimeOut));
    return factory;
  }

  @Bean
  public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
    return new RestTemplate(factory);
  }
}

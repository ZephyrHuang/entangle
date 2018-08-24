package me.zephyr.entangle.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "clipboard")
public class ClipboardProperties {
  private boolean sendSwitch;
  private boolean receiveSwitch;
  public Retry retry;

  public boolean getSendSwitch() {
    return sendSwitch;
  }

  public void setSendSwitch(boolean sendSwitch) {
    this.sendSwitch = sendSwitch;
  }

  public boolean getReceiveSwitch() {
    return receiveSwitch;
  }

  public void setReceiveSwitch(boolean receiveSwitch) {
    this.receiveSwitch = receiveSwitch;
  }

  public void setRetry(Retry retry) {
    this.retry = retry;
  }

  public static class Retry {
    private int times;
    private long interval;

    public int getTimes() {
      return times;
    }

    public void setTimes(int times) {
      this.times = times;
    }

    public long getInterval() {
      return interval;
    }

    public void setInterval(long interval) {
      this.interval = interval;
    }
  }
}

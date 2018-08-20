package me.zephyr.entangle.clipboard.listener;

import me.zephyr.entangle.clipboard.ClipboardOperator;
import me.zephyr.entangle.clipboard.event.ClipboardEvent;
import me.zephyr.entangle.clipboard.sender.ClipContentSender;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

@Component
public class ClipboardContentSyncer implements ClipboardListener {
  private static final Logger logger = LoggerFactory.getLogger(ClipboardContentSyncer.class);
  @Autowired
  @Qualifier("webSocketSender")
  private ClipContentSender sender;

  /**
   * 开关。true=开，false=关。
   */
  @Value("${clipboard.send.switch:false}")
  private volatile boolean switchh;

  public void switchOff() {
    this.switchh = false;
  }

  public void switchOn() {
    this.switchh = true;
  }

  @Override
  public void onClipboardChange(ClipboardEvent event) {
    Transferable transferable = event.getSource();
    String content = ClipboardOperator.getStringData(transferable);
    if (StringUtils.isNotBlank(content)) {
      sender.send(content);
    }
  }

  @Override
  public boolean isAcceptable(ClipboardEvent event) {
    logger.debug("是否发送剪贴板内容：{}", switchh);
    return switchh && event != null && ArrayUtils.contains(event.getDataFlavors(), DataFlavor.stringFlavor);
  }
}

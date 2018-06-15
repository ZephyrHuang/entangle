package me.zephyr.web.clip;

import me.zephyr.clip.ClipBoardService;
import me.zephyr.clip.ClipboardStatusManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import static me.zephyr.clip.ClipboardStatusManager.ClipboardStatus;

@Controller
public class ClipWebSocketController {
  private static final Logger logger = LoggerFactory.getLogger(ClipWebSocketController.class);

  @Autowired
  private ClipBoardService clipBoardService;

  @SubscribeMapping("/clipboard/status")
  public ClipboardStatus queryClipboardStatus() {
    ClipboardStatus status = ClipboardStatusManager.getClipboardStatus();
    logger.debug("剪贴本状态被查询：{}", status.toString());
    return status;
  }

  @SubscribeMapping("/clipboard/pull")
  public String pullClipboardContent() {
    String content = clipBoardService.pullClipContent();
    logger.debug("剪贴板内容被拉取：{}", content);
    return content;
  }
}

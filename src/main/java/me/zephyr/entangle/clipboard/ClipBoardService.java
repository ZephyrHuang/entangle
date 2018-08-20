package me.zephyr.entangle.clipboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClipBoardService {
  @Autowired
  private ClipboardOperator clipboardOperator;

  /**
   * 保存到系统剪贴板
   * @param content 待保存的文本内容
   * @return 保存成功的文本内容
   */
  public String saveClipContent(String content) {
    clipboardOperator.pushIntoSystemClipboard(content);
    return content;
  }

  /**
   * 获取系统剪贴版的当前文本内容
   * @return 系统剪贴版的当前文本内容
   */
  public String pullClipContent() {
    String result = clipboardOperator.pullSystemClipboardContent();
    ClipboardStatusManager.resetUpdatedFlag();
    return result;
  }
}

package me.zephyr.clip;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClipBoardService {
  @Autowired
  private ClipboardAcceptor clipboardAcceptor;

  public String saveClipContent(String content) {
    clipboardAcceptor.pushIntoSystemClipboard(content);
    return content;
  }
}

package me.zephyr.entangle.clipboard;

import me.zephyr.entangle.BaseConfigTest;
import me.zephyr.util.thread.ThreadUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = BaseConfigTest.class)
public class ClipboardMonitorTest {
  private static final Logger logger = LoggerFactory.getLogger(ClipboardMonitorTest.class);

  @Autowired
  private Clipboard clipboard;

  @Test
  public void testClipboardOwner() {
    ClipboardOwnerForTest monitorTest = new ClipboardOwnerForTest();
    clipboard.setContents(clipboard.getContents(null), monitorTest);
    ThreadUtil.sleepWithThrow(1_000_000);
  }

  //~ inner class ------------------------------------------------------------------------------------------------------

  private class ClipboardOwnerForTest implements ClipboardOwner {
    private final int retryTimes = 10;
    private final int retryInterval = 50;

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
      int timesLeft = retryTimes;
      do {
        try {
          Transferable transferable = clipboard.getContents(null);
          clipboard.setContents(transferable, this);
          logger.info("剪贴板内容：{}", transferable.toString());
          Object obj = transferable.getTransferData(DataFlavor.javaFileListFlavor);
          logger.info(obj.getClass().getName());
          break;
        } catch (IllegalStateException e) {
          logger.error("出现异常");
          ThreadUtil.sleepWithoutThrow(retryInterval);
          timesLeft--;
        } catch (IOException | UnsupportedFlavorException e) {
          logger.error("读取剪贴板异常!", e);
        }
      } while (timesLeft > 0);
    }
  }
}

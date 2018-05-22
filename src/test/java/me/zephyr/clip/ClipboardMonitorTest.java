package me.zephyr.clip;

import me.zephyr.clip.event.ClipboardEvent;
import me.zephyr.clip.listener.ClipboardListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class ClipboardMonitorTest {
  private static final Logger logger = LoggerFactory.getLogger(ClipboardMonitorTest.class);
  @Autowired
  private ClipboardMonitor clipboardMonitor;

  @Before
  public void setUp() {
    List<ClipboardListener> listeners = new ArrayList<ClipboardListener>();
    listeners.add(new MockClipboardListener());
    this.clipboardMonitor.setListeners(listeners);
  }

  @Test
  public void testLostOwnership() {
    logger.info("Start ......");
    for (;;) {

    }
  }

  //~ inner classes ----------------------------------------------------------------------------------------------------

  class MockClipboardListener implements ClipboardListener {
    //private final Logger logger = LoggerFactory.getLogger(MockClipboardListener.class);

    @Override
    public <T> void onClipboardChange(ClipboardEvent<T> event) {
      logger.info("Now in onClipboardChange()......");
      logger.info(event.getSource().toString());
    }

    @Override
    public <T> boolean isAcceptable(ClipboardEvent<T> event) {
      return event != null && String.class.equals(event.getSourceType());
    }
  }

}

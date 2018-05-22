package me.zephyr.clip;

public class ClipboardStatusManager {
  private static volatile boolean CLIPBOARD_UPDATED_FROM_LOCAL = false; // 剪贴板是否已被本地操作所更新

  public static boolean hasClipboardBeenUpdated() {
    return CLIPBOARD_UPDATED_FROM_LOCAL;
  }

  public static void clipboardHasBeenUpdated() {
    CLIPBOARD_UPDATED_FROM_LOCAL = true;
  }

  public static void resetClipboardUpdatedFlag() {
    CLIPBOARD_UPDATED_FROM_LOCAL = false;
  }
}

package me.zephyr.entangle.clipboard;

public class ClipboardStatusManager {
  private static volatile boolean CLIPBOARD_UPDATED_FROM_LOCAL = false; // 剪贴板是否已被本地操作所更新

  public static boolean hasBeenUpdated() {
    return CLIPBOARD_UPDATED_FROM_LOCAL;
  }

  public static void setUpdated() {
    CLIPBOARD_UPDATED_FROM_LOCAL = true;
  }

  public static void resetUpdatedFlag() {
    CLIPBOARD_UPDATED_FROM_LOCAL = false;
  }

  public static ClipboardStatus getClipboardStatus() {
    return CLIPBOARD_UPDATED_FROM_LOCAL ? ClipboardStatus.UPDATED : ClipboardStatus.NOT_UPDATED;
  }

  public enum ClipboardStatus {
    UPDATED("updated"), NOT_UPDATED("notUpdated");

    private String desc;

    ClipboardStatus(String desc) {
      this.desc = desc;
    }

    @Override
    public String toString() {
      return this.desc;
    }
  }
}

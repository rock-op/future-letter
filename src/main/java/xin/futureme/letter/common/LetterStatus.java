package xin.futureme.letter.common;

/**
 * Created by rockOps on 2017-02-03.
 */
public enum LetterStatus {
  // 从 未发送 到 待发送，可能会有多种策略，比如是否付费等
  UNSENT(0, "未发送"), NEED_SENT(1, "待发送"), SENDING(2, "发送中"), SENT_FAILURE(3, "发送失败"), SENT_SUCCESS(4, "发送成功");

  public int getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  private int code;
  private String name;

  LetterStatus(int code, String name) {
    this.code = code;
    this.name = name;
  }
}

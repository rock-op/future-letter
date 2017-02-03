package xin.futureme.letter.common;

/**
 * Created by rockOps on 2017-02-03.
 */
public enum LetterStatus {
  UNPAID(0, "未付款"), UNSENT(1, "未发送"), SENDING(2, "发送中"), SENT_FAILURE(3, "发送失败"), SENT_SUCCESS(4, "发送成功");

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

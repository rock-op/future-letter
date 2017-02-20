package xin.futureme.letter.common;

/**
 * Created by rockops on 2017-02-04.
 */
public enum LetterPrivacy {
  PRIVATE(0), PUBLIC_ANONYMOUS(1), PUBLIC(2);
  private int code;

  LetterPrivacy(int code) {
    this.code = code;
  }

  public int getCode() {
    return code;
  }
}

package xin.futureme.letter.entity;

import java.io.Serializable;

/**
 * Created by rockOps on 2017-01-23.
 */
public class Letter implements Serializable {
  private static final long serialVersionUID = -7749804721944401370L;

  private int id = 0;
  private String recipient;
  private String subject = "";
  private String body = "";
  private long createTime;
  private long sendTime;
  private int privacyType = 0;
  private int status = 0;

  private long updateTime = 0;

  public Letter() {
  }

  public Letter(String recipient, String subject, String body, long createTime, long sendTime, int privacyType, int status) {
    this.id = 0;
    this.recipient = recipient;
    this.subject = subject;
    this.body = body;
    this.createTime = createTime;
    this.sendTime = sendTime;
    this.privacyType = privacyType;
    this.status = status;
    this.updateTime = 0;
  }

  public Letter(int id, String recipient, String subject, String body, long createTime, long sendTime, int privacyType, int status) {
    this.id = id;
    this.recipient = recipient;
    this.subject = subject;
    this.body = body;
    this.createTime = createTime;
    this.sendTime = sendTime;
    this.privacyType = privacyType;
    this.status = status;
    this.updateTime = 0;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getRecipient() {
    return recipient;
  }

  public void setRecipient(String recipient) {
    this.recipient = recipient;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(long createTime) {
    this.createTime = createTime;
  }

  public long getSendTime() {
    return sendTime;
  }

  public void setSendTime(long sendTime) {
    this.sendTime = sendTime;
  }

  public int getPrivacyType() {
    return privacyType;
  }

  public void setPrivacyType(int privacyType) {
    this.privacyType = privacyType;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public int getStatus() {
    return status;
  }

  public long getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(long updateTime) {
    this.updateTime = updateTime;
  }

  @Override
  public String toString() {
    return "Letter{" +
        "id=" + id +
        ", recipient='" + recipient + '\'' +
        ", subject='" + subject + '\'' +
        ", body='" + body + '\'' +
        ", createTime=" + createTime +
        ", sendTime=" + sendTime +
        ", privacyType=" + privacyType +
        ", status=" + status +
        ", updateTime=" + updateTime +
        '}';
  }
}

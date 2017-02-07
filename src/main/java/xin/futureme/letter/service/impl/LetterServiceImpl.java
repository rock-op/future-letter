package xin.futureme.letter.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import xin.futureme.letter.common.LetterStatus;
import xin.futureme.letter.common.StorageConfig;
import xin.futureme.letter.dao.LetterMapper;
import xin.futureme.letter.entity.Letter;
import xin.futureme.letter.service.EmailService;
import xin.futureme.letter.service.LetterService;
import xin.futureme.letter.service.StorageService;
import xin.futureme.letter.utils.JedisUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.mail.MessagingException;

/**
 * Created by rockOps on 2017-01-23.
 * todo, 增加事务控制
 */
@Service
public class LetterServiceImpl implements LetterService{

  private final static Logger logger = LoggerFactory.getLogger(LetterServiceImpl.class);

  @Autowired
  private EmailService emailService;

  @Autowired
  private LetterMapper letterMapper;

  @Autowired
  private StorageService storageService;

  /**
   * 将信件内容写入storage，并将bucket:key写入数据库
   * @param letter
   * @return
   * @throws IOException
   */
  @Override
  public int insert(Letter letter) throws IOException {
    String key = generateKey(letter);
    storageService.upload(letter.getBody().getBytes(), StorageConfig.DEFAULT_BUCKET_NAME, key);
    letter.setBody(key);
    return letterMapper.insert(letter);
  }

  private String generateKey(Letter letter) {
    String prefix = "oss:";
    long timestamp = System.currentTimeMillis();
    String username = letter.getRecipient();

    return prefix + username + "_" + timestamp;
  }

  @Override
  public Letter getLetterByPrimaryKey(int id) {
    Letter letter = letterMapper.getLetterByPrimaryKey(id);
    if (letter == null) {
      letter = new Letter();
    }

    return letter;
  }

  @Override
  public List<Letter> getLettersByRecipient(String recipient) {
    List<Letter> letters = letterMapper.getLettersByRecipient(recipient);
    if (CollectionUtils.isEmpty(letters)) {
      letters = new ArrayList<>();
    }

    return letters;
  }

  @Override
  public Letter getLetterByRecipientAndSendTime(String recipient, long sendTime) {
    Letter letter = letterMapper.getLetterByRecipientAndSendTime(recipient, sendTime);
    if (null == letter) {
      letter = new Letter();
    }
    return letter;
  }

  /**
   * 投递邮件
   * @param letter
   * @return
   * @throws IOException
   */
  @Override
  public void send(Letter letter) throws IOException, MessagingException {
    logger.info("send letter, letterId: {}, to:{}, createTime:{}",
        letter.getId(), letter.getRecipient(), letter.getCreateTime());
    String body = storageService.getBucketKeyContent(StorageConfig.DEFAULT_BUCKET_NAME, letter.getBody());
    letter.setBody(body);

    try {
      emailService.send(letter.getRecipient(), letter.getSubject(), letter.getBody());
      updateStatusByPrimaryKey(letter.getId(), LetterStatus.SENT_SUCCESS.getCode());
    } catch (MessagingException e) {
      updateStatusByPrimaryKey(letter.getId(), LetterStatus.SENT_FAILURE.getCode());
      logger.error("send mail error, id:{}, to:{}, msg:{}", letter.getId(), letter.getRecipient(), e.getMessage());
      throw new MessagingException(e.getMessage());
    }
  }

  @Override
  public List<Letter> getLettersReadyToSend(long sendTime) {
    List<Letter> letters = null;
    letters = letterMapper.getUnsentLetters(sendTime);
    if (letters == null) {
      letters = new ArrayList<>();
    }
    return letters;
  }

  @Override
  public void sendVerificationCode(String recipient) throws MessagingException {
    String code = generateVerificationCode(recipient);
    JedisUtils.set(recipient, code, 0);

    String subject = "来自futureme.xin的校验码";
    String body = "您在futureme.xin的校验码为" + code + "，如非本人操作请忽略。";
    emailService.send(recipient, subject, body);
  }

  /**
   * 产生校验码，随机数字
   * @param recipient
   * @return
   */
  private String generateVerificationCode(String recipient) {
    String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    int codeLength = 8;
    Random random = new Random();
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < codeLength; i++) {
      int num = random.nextInt(str.length());
      buf.append(str.charAt(num));
    }
    return buf.toString();
  }

  @Override
  public int deleteByPrimaryKey(int id) {
    return letterMapper.deleteByPrimaryKey(id);
  }

  @Override
  public int updateStatusByPrimaryKey(int letterId, int status) {
    return letterMapper.updateStatusByPrimaryKey(letterId, status);
  }

  private int updateStatusByRecipientAndSendTime(String recipient, long sendTime, int status) {
    return letterMapper.updateStatusByRecipientAndSendTime(recipient, sendTime, status);
  }
}

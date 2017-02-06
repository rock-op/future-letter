package xin.futureme.letter.service.impl;

import com.qiniu.common.QiniuException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import xin.futureme.letter.common.LetterStatus;
import xin.futureme.letter.dao.LetterMapper;
import xin.futureme.letter.entity.Letter;
import xin.futureme.letter.service.EmailService;
import xin.futureme.letter.service.LetterService;
import xin.futureme.letter.utils.QiNiuStorageUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

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

  /**
   * 将信件内容写入qiniu，并将bucket:key写入数据库
   * @param letter
   * @return
   * @throws QiniuException
   */
  @Override
  public int insert(Letter letter) throws IOException {
    String key = generateKey(letter);
    QiNiuStorageUtils.upload2DefaultBucket(letter.getBody().getBytes(), key);
    letter.setBody(key);
    return letterMapper.insert(letter);
  }

  private String generateKey(Letter letter) {
    String prefix = "qiniu:";
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
    String body = QiNiuStorageUtils.getDefaultBucketKeyContent(letter.getBody());
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

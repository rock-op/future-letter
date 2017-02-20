package xin.futureme.letter.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import xin.futureme.letter.service.EmailService;
import javax.mail.internet.MimeMessage;

import javax.mail.MessagingException;

/**
 * Created by rockops on 2017-02-03.
 */
@Service
public class EmailServiceImpl implements EmailService {

  @Autowired
  private JavaMailSender mailSender;

  @Value("${email.systemEmail}")
  private String from;

  @Override
  public void send(String to, String subject, String body) throws MessagingException {
    try {
      MimeMessage mimeMessage = mailSender.createMimeMessage();
      // 邮件格式编码设定为UTF-8, 防止中文内容乱码
      MimeMessageHelper msgHelper = new MimeMessageHelper(mimeMessage, true, "utf-8");

      msgHelper.setFrom(from);
      msgHelper.setTo(to);
      msgHelper.setSubject(subject);
      msgHelper.setText(body, true);

      mailSender.send(mimeMessage);
    } catch (MessagingException e) {
      throw new MessagingException("Faild to send mail.", e);

    }
  }
}

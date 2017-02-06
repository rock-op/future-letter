package xin.futureme.letter.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Created by rockops on 2017-02-03.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext.xml")
public class EmailServiceTest {
  @Autowired
  private EmailService emailService;

  @Test
  public void send() throws Exception {
    String to = "guofzhao@126.com";
    String subject = "这是一封来自java的邮件";
    String body = "有点意思啊word天 哈哈";

    emailService.send(to, subject, body);
  }

}
package xin.futureme.letter.service;

import javax.mail.MessagingException;

/**
 * Created by rockops on 2017-02-03.
 */
public interface EmailService {
  void send(String to, String subject, String body) throws MessagingException;
}

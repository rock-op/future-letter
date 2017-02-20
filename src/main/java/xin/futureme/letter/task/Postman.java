package xin.futureme.letter.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import xin.futureme.letter.entity.Letter;
import xin.futureme.letter.service.LetterService;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

/**
 * Created by rockops on 2017-02-04.
 */
public class Postman {
  private final static Logger logger = LoggerFactory.getLogger(Postman.class);

  @Autowired
  private LetterService letterService;

  public void deliver() {
    long now = System.currentTimeMillis();
    List<Letter> letters = letterService.getLettersReadyToSend(now);

    for (Letter letter : letters) {
      try {
        letterService.send(letter);
      } catch (Exception e) {
        logger.error("send mail error, currentTime:{}, letter id:{}, to:{}",
            now, letter.getId(), letter.getRecipient());
        e.printStackTrace();
      }
    }

  }
}

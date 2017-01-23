package xin.futureme.letter.service;

import xin.futureme.letter.entity.Letter;
import xin.futureme.letter.entity.LetterExample;

import java.util.List;

/**
 * Created by rockops on 2017-01-23.
 */
public interface LetterService {

  int insert(Letter letter);

  int deleteByPrimaryKey(int id);

  int updateStatusByRecipientAndSendTime(String recipient, long sendTime, int status);

  Letter getLetterByPrimaryKey(int id);

  List<Letter> getLettersByRecipient(String recipient);

  Letter getLetterByRecipientAndSendTime(String recipient, long sendTime);
}

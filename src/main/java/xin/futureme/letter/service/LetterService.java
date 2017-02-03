package xin.futureme.letter.service;

import com.qiniu.common.QiniuException;
import xin.futureme.letter.entity.Letter;

import java.io.IOException;
import java.util.List;

/**
 * Created by rockops on 2017-01-23.
 */
public interface LetterService {

  int insert(Letter letter) throws IOException;

  int deleteByPrimaryKey(int id);

  int updateStatusByPrimaryKey(int id, int status);

  Letter getLetterByPrimaryKey(int id);

  List<Letter> getLettersByRecipient(String recipient);

  Letter getLetterByRecipientAndSendTime(String recipient, long sendTime);

  void send(Letter letter) throws IOException;
}

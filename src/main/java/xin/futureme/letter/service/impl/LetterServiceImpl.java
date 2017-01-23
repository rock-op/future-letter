package xin.futureme.letter.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import xin.futureme.letter.dao.LetterMapper;
import xin.futureme.letter.entity.Letter;
import xin.futureme.letter.entity.LetterExample;
import xin.futureme.letter.service.LetterService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rockOps on 2017-01-23.
 */
@Service
public class LetterServiceImpl implements LetterService{

  @Autowired
  private LetterMapper letterMapper;

  @Override
  public int insert(Letter letter) {
    // TODO, 写body到分布式存储，并获取到对象的url
    String mailBodyUrl = "";

    letter.setBody(mailBodyUrl);
    return letterMapper.insert(letter);
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

  // TODO: complete it
  @Override
  public List<Letter> getLettersByExample(LetterExample letterExample) {
    return null;
  }

  @Override
  public int deleteByPrimaryKey(int id) {
    return letterMapper.deleteByPrimaryKey(id);
  }
}

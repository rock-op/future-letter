package xin.futureme.letter.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import xin.futureme.letter.entity.Letter;
import xin.futureme.letter.entity.LetterExample;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by rockOps on 2017-01-23.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext.xml")
public class LetterServiceTest {

  @Autowired
  private LetterService letterService;

  @Test
  public void insert() throws Exception {
    Letter myLetter = new Letter();
    long createTime = System.currentTimeMillis();
    long sendTime = System.currentTimeMillis();
    String recipient = "test@futureme.xin";

    myLetter.setCreateTime(createTime);
    myLetter.setSendTime(sendTime);
    myLetter.setRecipient(recipient);

    int result = letterService.insert(myLetter);
    assertEquals(result, 1);
  }

  @Test
  public void getLetterByPrimaryKey() throws Exception {
    int id = 1;
    Letter letter = letterService.getLetterByPrimaryKey(id);
    //System.out.println(letter);
    assertNotNull(letter);
    assertEquals(id, letter.getId());
  }

  @Test
  public void getLettersByRecipient() throws Exception {
    String recipient = "guofzhao@163.com";
    List<Letter> letters = letterService.getLettersByRecipient(recipient);
    assertNotNull(letters);
    assertEquals(recipient, letters.get(0).getRecipient());
  }

  @Test
  public void deleteByPrimaryKey() throws Exception {
    LetterExample example = new LetterExample();
    List<Letter> letters = letterService.getLettersByExample(example);
//    assertNotNull(letters);
//    for (Letter letter : letters) {
//      letterService.deleteByPrimaryKey(letter.getId());
//    }
  }

}
package xin.futureme.letter.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import xin.futureme.letter.entity.Letter;
import xin.futureme.letter.entity.LetterExample;

import java.util.List;

/**
 * Created by rockOps on 2017-01-23.
 */
@Repository
public interface LetterMapper {
  int insert(Letter letter);
  Letter getLetterByPrimaryKey(@Param("id") int id);
  List<Letter> getLettersByRecipient(@Param("recipient") String recipient);
  int deleteByPrimaryKey(@Param("id") int id);
  List<Letter> getLettersByExample(LetterExample example);
}

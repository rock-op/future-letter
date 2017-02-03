package xin.futureme.letter.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import xin.futureme.letter.entity.Letter;

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

  Letter getLetterByRecipientAndSendTime(@Param("recipient") String recipient, @Param("sendTime") long sendTime);

  int updateStatusByRecipientAndSendTime(@Param("recipient") String recipient, @Param("sendTime") long sendTime, @Param("status") int status);

  int updateStatusByPrimaryKey(@Param("id") int id , @Param("status") int status);
}

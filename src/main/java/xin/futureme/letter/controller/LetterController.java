package xin.futureme.letter.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import xin.futureme.letter.common.LetterPrivacy;
import xin.futureme.letter.common.LetterStatus;
import xin.futureme.letter.entity.Letter;
import xin.futureme.letter.service.LetterService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Timestamp;

/**
 * Created by rockops on 2017-02-04.
 */
@Controller
@RequestMapping("/letter")
public class LetterController {

  private final static Logger logger = LoggerFactory.getLogger(LetterController.class);

  @Autowired
  private LetterService letterService;

  @RequestMapping(value = "/edit", method = RequestMethod.GET)
  public String edit(HttpServletRequest request, ModelMap modelMap) {
    return "letter/edit";
  }

  @RequestMapping(value = "/save", method = RequestMethod.POST)
  public String save(HttpServletRequest request, ModelMap modelMap) {
    String recipient = request.getParameter("recipient");
    String subject = request.getParameter("subject");
    String body = request.getParameter("body");
    String sendYear = request.getParameter("sendYear");
    String sendMonth = request.getParameter("sendMonth");
    String sendDate = request.getParameter("sendDate");
    String privacyType = request.getParameter("privacyType");

    if (!validateParams(recipient, subject, body, sendYear, sendMonth, sendDate, privacyType)) {
      return "letter/edit";
    }

    Letter letter = new Letter();
    letter.setCreateTime(System.currentTimeMillis());
    letter.setUpdateTime(System.currentTimeMillis());
    letter.setRecipient(recipient);
    letter.setSubject(subject);
    letter.setBody(body.trim());
    letter.setStatus(LetterStatus.UNSENT.getCode());

    if (privacyType.equals("false")) {
      letter.setPrivacyType(LetterPrivacy.PRIVATE.getCode());
    } else {
      letter.setPrivacyType(LetterPrivacy.PUBLIC_ANONYMOUS.getCode());
    }
    String sendTime = sendYear + "-" + sendMonth + "-" + sendDate + " 00:00:00";
    letter.setSendTime(Timestamp.valueOf(sendTime).getTime());

    logger.info("save letter, letter:{}", letter.toString());
    try {
      letterService.insert(letter);
    } catch (IOException e) {
      logger.error("save letter error, letter:{}", letter.toString());
      e.printStackTrace();
      return "letter/error";
    }

    modelMap.put("sendTime", letter.getSendTime());
    return "letter/save";
  }

  /**
   * 校验参数是否非空
   * @param params
   * @return
   */
  private boolean validateParams(String... params) {
    for (String p : params) {
      if (StringUtils.isEmpty(p)) {
        return false;
      }
    }
    return true;
  }
}

package xin.futureme.letter.controller;

import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import xin.futureme.letter.common.LetterPrivacy;
import xin.futureme.letter.common.LetterStatus;
import xin.futureme.letter.entity.Letter;
import xin.futureme.letter.service.LetterService;
import xin.futureme.letter.utils.JedisUtils;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Random;

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

  @RequestMapping(value = "/sendVCode", method = RequestMethod.POST, produces="application/json;charset=utf-8")
  @ResponseBody
  public String sendVerificationCode(HttpServletRequest request) {
    JSONObject result = new JSONObject();
    result.put("success", false);

    String recipient = request.getParameter("recipient");
    String code = generateVerificationCode(recipient);
    JedisUtils.set(recipient, code, 0);

    try {
      letterService.sendVerificationCode(recipient, code);
      result.put("success", true);
      result.put("msg", "发送成功");
    } catch (Exception e) {
      logger.error("send vcode error, recipient:{}", recipient);
      e.printStackTrace();
      result.put("success", false);
      result.put("msg", "邮箱输入有误，请重新输入!");
    }

    return result.toString();
  }

  @RequestMapping(value = "/save", method = RequestMethod.POST)
  public String save(HttpServletRequest request, ModelMap modelMap) {
    String recipient = request.getParameter("recipient");
    String subject = request.getParameter("subject");
    String body = request.getParameter("body");
    String sendDate = request.getParameter("sendDate");
    String vCode = request.getParameter("vCode");

    if (!validateParams(recipient, sendDate, vCode)) {
      return "letter/edit";
    }

    if (!vCodeIsValid(recipient, vCode)) {
      return "";
    }

    Letter letter = new Letter();
    letter.setCreateTime(System.currentTimeMillis());
    letter.setUpdateTime(System.currentTimeMillis());
    letter.setRecipient(recipient);
    letter.setSubject(subject);
    letter.setBody(body.trim());
    letter.setStatus(LetterStatus.UNSENT.getCode());
    letter.setPrivacyType(LetterPrivacy.PRIVATE.getCode());
    String sendTime = sendDate + " 00:00:00";
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
   * 身份认证，校验邮箱是否属于本人
   * @return
   */
  private boolean vCodeIsValid(String recipient, String vCode) {
    String code = JedisUtils.get(recipient);
    if (code == null || !(code.equals(vCode))) {
      return false;
    }
    return true;
  }

  /**
   * 校验参数是否合法
   * @param params
   * @return
   */
  private boolean validateParams(String... params) {
    boolean flag = true;

    for (String p : params) {
      if (StringUtils.isEmpty(p)) {
        flag = false;
        break;
      }
    }
    return flag;
  }

    /**
   * 产生校验码，随机数字
   * @param recipient
   * @return
   */
  private String generateVerificationCode(String recipient) {
    String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    int codeLength = 8;
    Random random = new Random();
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < codeLength; i++) {
      int num = random.nextInt(str.length());
      buf.append(str.charAt(num));
    }
    return buf.toString();
  }

}

package xin.futureme.letter.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import xin.futureme.letter.service.LetterService;

/**
 * Created by rockops on 2017-02-04.
 */
@Controller
public class LetterController {

  private final static Logger logger = LoggerFactory.getLogger(LetterController.class);

  @Autowired
  private LetterService letterService;

  @RequestMapping(value = "/", method = RequestMethod.GET)
  public String index() {
    logger.debug("hello, index");
    return "index";
  }

}

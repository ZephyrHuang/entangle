package me.zephyr.web.clip;

import me.zephyr.clip.ClipBoardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Controller
@RequestMapping("/clip")
public class ClipHttpController {
  private static final Logger logger = LoggerFactory.getLogger(ClipHttpController.class);
  @Autowired
  private ClipBoardService clipBoardService;

  @RequestMapping(value = "/set", method = {POST, PUT})
  @ResponseBody
  public String setClipContent(@RequestBody String content) {
    logger.debug("clipboard ==> " + content);
    return clipBoardService.saveClipContent(content);
  }

  @RequestMapping(value = "/set/{content}", method = {GET})
  @ResponseBody
  public String setClipContentByGet(@PathVariable("content") String content) {
    return setClipContent(content);
  }
}

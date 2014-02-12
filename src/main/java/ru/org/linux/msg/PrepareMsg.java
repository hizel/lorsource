package ru.org.linux.msg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.org.linux.spring.SiteConfig;
import ru.org.linux.topic.TopicPermissionService;
import ru.org.linux.util.bbcode.LorCodeService;
import ru.org.linux.util.formatter.ToLorCodeFormatter;
import ru.org.linux.util.formatter.ToLorCodeTexFormatter;

@Service
public class PrepareMsg {

  @Autowired
  private LorCodeService lorCodeService;

  @Autowired
  private ToLorCodeFormatter toLorCodeFormatter;

  @Autowired
  private ToLorCodeTexFormatter toLorCodeTexFormatter;

  @Autowired
  private SiteConfig siteConfig;

  @Autowired
  private TopicPermissionService permissionService;


  public String prepareComment(Msg msg, boolean secure, boolean nofollow) {
    String ret;
    if(msg.getMarkup() == MsgMarkup.PLAIN) {
      return "<p>" + msg.getText() + "</p>";
    }
    if(msg.getMarkup() == MsgMarkup.MARKDOWN) {
      return msg.getText();
    }
    switch(msg.getMarkup()) {
      case BBCODE_ULB:
        ret = toLorCodeFormatter.format(msg.getText(), true);
        break;
      default:
        ret = toLorCodeTexFormatter.format(msg.getText());
        break;
    }
    return lorCodeService.parseComment(ret, secure, nofollow);
  }

  public String prepareTopic(Msg msg, boolean secure, boolean nofollow, boolean min, String url, boolean follow) {
    String ret;
    if(msg.getMarkup() == MsgMarkup.PLAIN) {
      return "<p>" + msg.getText() + "</p>";
    }
    if(msg.getMarkup() == MsgMarkup.MARKDOWN) {
      return msg.getText();
    }

    if(msg.getMarkup() == MsgMarkup.BBCODE_ULB) {
      ret = toLorCodeFormatter.format(msg.getText(), false);
    } else {
      ret = msg.getText();
    }

    if(min) {
      return lorCodeService.parseTopicWithMinimizedCut(ret, url, secure, follow);
    } else {
      return lorCodeService.parseTopic(ret, secure, follow);
    }
  }

}

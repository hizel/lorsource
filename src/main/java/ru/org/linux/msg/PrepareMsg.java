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

  public Msg prepareTopic(Msg msg, boolean secure, boolean nofollow, boolean min, String url) {
    String ret;
    if(msg.getMarkup() == MsgMarkup.PLAIN) {
      return new Msg("<p>" + msg.getText() + "</p>", msg.getMarkup());
    }
    if(msg.getMarkup() == MsgMarkup.MARKDOWN) {
      return new Msg(msg.getText(), msg.getMarkup());
    }

    if(msg.getMarkup() == MsgMarkup.BBCODE_ULB) {
      ret = toLorCodeFormatter.format(msg.getText(), false);
    } else {
      ret = msg.getText();
    }

    if(min) {
      return new Msg(lorCodeService.parseTopicWithMinimizedCut(ret, url, secure, nofollow), msg.getMarkup());
    } else {
      return new Msg(lorCodeService.parseTopic(ret, secure, nofollow), msg.getMarkup());
    }
  }

}

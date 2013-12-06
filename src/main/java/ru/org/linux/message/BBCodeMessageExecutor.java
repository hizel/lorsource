/*
 * Copyright 1998-2013 Linux.org.ru
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package ru.org.linux.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.org.linux.user.User;
import ru.org.linux.user.UserDao;
import ru.org.linux.util.bbcode.DefaultParserParameters;
import ru.org.linux.util.bbcode.Parser;
import ru.org.linux.util.bbcode.nodes.RootNode;
import ru.org.linux.util.formatter.ToHtmlFormatter;

import java.util.Map;
import java.util.Set;

public class BBCodeMessageExecutor implements MessageExecutor{
  private static final Logger logger = LoggerFactory.getLogger(BBCodeMessageExecutor.class);
  private static final Parser parser = new Parser(new DefaultParserParameters());

  private final RootNode rootNode;
  private UserDao userDao;
  private ToHtmlFormatter toHtmlFormatter;
  private UriComponentsBuilder cutUri;
  private boolean minimize=false;
  private boolean secure=false;
  private boolean follow=false;
  private boolean rss=false;
  private boolean comment;

  public BBCodeMessageExecutor(String text) {
    this.rootNode = parser.parseRoot(parser.getRootNode(), text);
  }

  private void prepareCut(boolean comment, boolean minimize, UriComponentsBuilder cutUri) {
    if(comment) {
      rootNode.setCommentCutOptions();
    } else {
      if(minimize) {
        rootNode.setMinimizedTopicCutOptions((secure) ? cutUri.scheme("https") : cutUri.scheme("http"));
      } else {
        rootNode.setMaximizedTopicCutOptions();
      }
    }
  }

  @Override
  public String desc() {
    rootNode.setNofollow(!follow);
    rootNode.setSecure(secure);
    rootNode.setUserDao(userDao);
    rootNode.setRss(rss);
    rootNode.setToHtmlFormatter(toHtmlFormatter);
    prepareCut(comment, minimize, cutUri);
    return rootNode.renderXHtml();
  }

  @Override
  public String html() {
    rootNode.setNofollow(!follow);
    rootNode.setSecure(secure);
    rootNode.setUserDao(userDao);
    rootNode.setToHtmlFormatter(toHtmlFormatter);
    prepareCut(comment, minimize, cutUri);
    return rootNode.renderXHtml();
  }

  @Override
  public Set<User> repliers() {
    rootNode.setNofollow(!follow);
    rootNode.setSecure(secure);
    rootNode.setUserDao(userDao);
    rootNode.setToHtmlFormatter(toHtmlFormatter);
    prepareCut(comment, minimize, cutUri);
    rootNode.renderXHtml();
    return rootNode.getReplier();
  }

  @Override
  public void setRenderParam(Map<String, Object> param) {
    for(String k : param.keySet()) {
      switch(k) {
        case "userDao":
          this.userDao = (UserDao) param.get(k);
          break;
        case "toHtmlFormatter":
          this.toHtmlFormatter = (ToHtmlFormatter) param.get(k);
        case "follow":
          this.follow = (Boolean) param.get(k);
          break;
        case "secure":
          this.secure = (Boolean) param.get(k);
          break;
        case "minimize":
          this.minimize = (Boolean) param.get(k);
          break;
        case "comment":
          this.comment = (Boolean) param.get(k);
          break;
        case "rss":
          this.rss = (Boolean) param.get(k);
          break;
        case "cutUri":
          this.cutUri = (UriComponentsBuilder) param.get(k);
          break;
        default:
          logger.debug("Invalid BBCodeMessageExecutor param %s", k);
      }
    }
  }

  @Override
  public MessageType getMarkupType() {
    return MessageType.BBCODE_TEX;
  }
}

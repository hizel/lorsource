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

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import ru.org.linux.comment.Comment;
import ru.org.linux.spring.SiteConfig;
import ru.org.linux.topic.Topic;
import ru.org.linux.topic.TopicPermissionService;
import ru.org.linux.user.User;
import ru.org.linux.user.UserDao;
import ru.org.linux.util.formatter.ToHtmlFormatter;
import ru.org.linux.util.formatter.ToLorCodeFormatter;
import ru.org.linux.util.formatter.ToLorCodeTexFormatter;

import java.util.Set;

@Service
public class MessageService {

  @Autowired
  private MessageDao messageDao;

  @Autowired
  private ToLorCodeFormatter ULBFormat;

  @Autowired
  private ToLorCodeTexFormatter TeXFormat;

  @Autowired
  private UserDao userDao;

  @Autowired
  private ToHtmlFormatter toHtmlFormatter;

  @Autowired
  private SiteConfig siteConfig;

  @Autowired
  private TopicPermissionService topicPermissionService;

  @Cacheable(value = "topicMessages", key = "#p0", unless = "#p0 != 0")
  public MessageExecutor prepareMessageTopic(int id, Message msg) {
    Message message = (id == 0) ? msg : messageDao.getMessage(id);
    MessageType type = message.getType();
    MessageExecutor ex;
    switch (type) {
      case BBCODE_TEX:
        ex = new BBCodeMessageExecutor(TeXFormat.format(message.getText()));
        break;
      case BBCODE_ULB:
        ex= new BBCodeMessageExecutor(ULBFormat.format(message.getText(), false));
        break;
      case MARKDOWN:
      case PLAIN:
      default:
        ex = new PlainMessageExecutor(message.getText());
    }
    ex.setRenderParam(ImmutableMap.<String, Object>of("comment", false));
    return ex;
  }

  @Cacheable(value = "commentMessages", key = "#p0", unless = "#p0 != 0")
  public MessageExecutor prepareMessageComment(int id, Message msg) {
    Message message = (id == 0) ? msg : messageDao.getMessage(id);
    MessageType type = message.getType();
    MessageExecutor ex;
    switch (type) {
      case BBCODE_TEX:
        ex = new BBCodeMessageExecutor(TeXFormat.format(message.getText()));
        break;
      case BBCODE_ULB:
        ex= new BBCodeMessageExecutor(ULBFormat.format(message.getText(), true));
        break;
      case MARKDOWN:
      case PLAIN:
      default:
        ex = new PlainMessageExecutor(message.getText());
    }
    ex.setRenderParam(ImmutableMap.<String, Object>of("comment", true));
    return ex;
  }

  public String getTopicHtmlMessage(Topic topic, boolean secure, boolean minimize) {
    MessageExecutor ex = prepareMessageTopic(topic.getId(), null);
    User author = userDao.getUserCached(topic.getUid());
    boolean follow = topicPermissionService.followInTopic(topic, author);
    if(minimize) {
      UriComponentsBuilder urlBuild = UriComponentsBuilder.fromHttpUrl(siteConfig.getMainUrl()).path(topic.getLink());
      ex.setRenderParam(ImmutableMap.<String, Object>of(
          "cutUri", (secure) ? urlBuild.scheme("https") : urlBuild.scheme("http"),
          "minimize", true
      ));
    }
    ex.setRenderParam(ImmutableMap.<String, Object>of(
        "secure", secure,
        "userDao", userDao,
        "toHtmlFormatter", toHtmlFormatter,
        "follow", follow
    ));
    return ex.html();
  }

  public String getTopicHtmlPreviewMessage(Message msg, boolean secure) {
    MessageExecutor ex = prepareMessageTopic(0, msg);
    ex.setRenderParam(ImmutableMap.<String, Object>of(
        "secure", secure,
        "userDao", userDao,
        "toHtmlFormatter", toHtmlFormatter
    ));
    return ex.html();
  }

  public String getCommentHtmlMessage(Comment comment, boolean secure) {
    MessageExecutor ex = prepareMessageComment(comment.getId(), null);
    User author = userDao.getUserCached(comment.getUserid());
    boolean follow = topicPermissionService.followAuthorLinks(author);
    ex.setRenderParam(ImmutableMap.<String, Object>of(
        "secure", secure,
        "userDao", userDao,
        "toHtmlFormatter", toHtmlFormatter,
        "follow", follow
    ));
    return ex.html();
  }

  public String getCommentHtmlPreviewMessage(Message msg, boolean secure) {
    MessageExecutor ex = prepareMessageComment(0, msg);
    ex.setRenderParam(ImmutableMap.<String, Object>of(
        "secure", secure,
        "userDao", userDao,
        "toHtmlFormatter", toHtmlFormatter
    ));
    return ex.html();
  }


  public String getTopicOgDescription(Topic topic, boolean secure) {
    MessageExecutor ex = prepareMessageTopic(topic.getId(), null);
    User author = userDao.getUserCached(topic.getUid());
    boolean follow = topicPermissionService.followInTopic(topic, author);
    ex.setRenderParam(ImmutableMap.<String, Object>of(
        "secure", secure,
        "userDao", userDao,
        "toHtmlFormatter", toHtmlFormatter,
        "follow", follow
    ));
    return ex.desc();
  }

  public Set<User> getTopicReplier(Topic topic) {
    MessageExecutor ex = prepareMessageTopic(topic.getId(), null);
    User author = userDao.getUserCached(topic.getUid());
    boolean follow = topicPermissionService.followInTopic(topic, author);
    ex.setRenderParam(ImmutableMap.<String, Object>of(
        "userDao", userDao,
        "toHtmlFormatter", toHtmlFormatter,
        "follow", follow
    ));
    return ex.repliers();
  }

  public Set<User> getCommentReplier(Comment comment) {
    MessageExecutor ex = prepareMessageComment(comment.getId(), null);
    ex.setRenderParam(ImmutableMap.<String, Object>of(
        "userDao", userDao,
        "toHtmlFormatter", toHtmlFormatter
    ));
    return ex.repliers();
  }
}

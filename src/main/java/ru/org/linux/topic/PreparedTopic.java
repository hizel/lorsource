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

package ru.org.linux.topic;

import com.google.common.collect.ImmutableList;
import ru.org.linux.edithistory.EditHistoryDto;
import ru.org.linux.group.Group;
import ru.org.linux.msg.Msg;
import ru.org.linux.poll.PreparedPoll;
import ru.org.linux.section.Section;
import ru.org.linux.site.DeleteInfo;
import ru.org.linux.tag.TagRef;
import ru.org.linux.user.Remark;
import ru.org.linux.user.User;

import java.util.List;

public final class PreparedTopic {
  private final Topic message;
  private final User author;
  private final DeleteInfo deleteInfo;
  private final User deleteUser;
  private final Msg preparedMsg;
  private final PreparedPoll poll;
  private final User commiter;
  private final ImmutableList<TagRef> tags;
  private final Group group;
  private final Section section;

  private final EditHistoryDto lastHistoryDto;
  private final User lastEditor;
  private final int editCount;

  private final PreparedImage image;
  
  private final String postscoreInfo;

  private final Remark remark;

  public PreparedTopic(
          Topic message,
          User author,
          DeleteInfo deleteInfo,
          User deleteUser,
          Msg preparedMsg,
          PreparedPoll poll,
          User commiter,
          List<TagRef> tags,
          Group group,
          Section section,
          EditHistoryDto lastHistoryDto,
          User lastEditor,
          int editorCount,
          PreparedImage image,
          String postscoreInfo,
          Remark remark) {
    this.message = message;
    this.author = author;
    this.deleteInfo = deleteInfo;
    this.deleteUser = deleteUser;
    this.preparedMsg = preparedMsg;
    this.poll = poll;
    this.commiter = commiter;
    this.postscoreInfo = postscoreInfo;
    if (tags!=null) {
      this.tags=ImmutableList.copyOf(tags);
    } else {
      this.tags=ImmutableList.of();
    }
    this.group = group;
    this.section = section;
    this.lastHistoryDto = lastHistoryDto;
    this.lastEditor = lastEditor;
    editCount = editorCount;
    this.image = image;
    this.remark = remark;
  }

  public Topic getMessage() {
    return message;
  }

  public User getAuthor() {
    return author;
  }

  public DeleteInfo getDeleteInfo() {
    return deleteInfo;
  }

  public User getDeleteUser() {
    return deleteUser;
  }

  public Msg getPreparedMsg() {
    return preparedMsg;
  }

  public PreparedPoll getPoll() {
    return poll;
  }

  public User getCommiter() {
    return commiter;
  }

  public EditHistoryDto getLastHistoryDto() {
    return lastHistoryDto;
  }

  public User getLastEditor() {
    return lastEditor;
  }

  public int getEditCount() {
    return editCount;
  }

  public int getId() {
    return message.getId();
  }

  public ImmutableList<TagRef> getTags() {
    return tags;
  }

  public Group getGroup() {
    return group;
  }

  public Section getSection() {
    return section;
  }

  public PreparedImage getImage() {
    return image;
  }

  public String getPostscoreInfo() {
    return postscoreInfo;
  }

  public Remark getRemark() {
    return remark;
  }
}

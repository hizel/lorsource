/*
 * Copyright 1998-2015 Linux.org.ru
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

package ru.org.linux.spring.dao;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Repository
public class MsgbaseDao {
  /**
   * Запрос тела сообщения и признака bbcode для сообщения
   */
  private static final String QUERY_MESSAGE_TEXT = "SELECT message, markup FROM msgbase WHERE id=?";
  private static final String QUERY_MESSAGE_LIST = "SELECT message, markup, id FROM msgbase WHERE id IN (:list)";

  private static final String QUERY_MESSAGE_TEXT_FROM_WIKI =
      "    select jam_topic_version.version_content " +
          "    from jam_topic, jam_topic_version " +
          "    where jam_topic.current_version_id = jam_topic_version.topic_version_id " +
          "    and jam_topic.topic_id = ?";

  private JdbcTemplate jdbcTemplate;
  private NamedParameterJdbcTemplate namedJdbcTemplate;

  private SimpleJdbcInsert insertMsgbase;

  @Autowired
  public void setDataSource(DataSource dataSource) {
    jdbcTemplate = new JdbcTemplate(dataSource);
    namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

    insertMsgbase = new SimpleJdbcInsert(dataSource)
        .withTableName("msgbase")
        .usingColumns("id", "message", "markup");
  }

  @Transactional(rollbackFor = Exception.class, propagation = Propagation.MANDATORY)
  public void saveNewMessage(String message, int msgid) {
    SqlParameterSource parameters = new MapSqlParameterSource()
        .addValue("id", msgid)
        .addValue("message", message)
        .addValue("markup", MarkupTextType.BBCODE_TEX.toString());
    insertMsgbase.execute(parameters);
  }

  @Transactional(rollbackFor = Exception.class, propagation = Propagation.MANDATORY)
  public void saveNewMarkupText(MarkupText message, int msgid) {
    SqlParameterSource parameters = new MapSqlParameterSource()
        .addValue("id", msgid)
        .addValue("message", message.getText())
        .addValue("markup", message.getType().toString());
    insertMsgbase.execute(parameters);
  }

  public String getMessageTextFromWiki(int topicId) {
    return jdbcTemplate.queryForObject(QUERY_MESSAGE_TEXT_FROM_WIKI, String.class, topicId);
  }

  public MessageText getMessageText(int msgid) {
    return jdbcTemplate.queryForObject(QUERY_MESSAGE_TEXT, new RowMapper<MessageText>() {
      @Override
      public MessageText mapRow(ResultSet resultSet, int i) throws SQLException {
        String text = resultSet.getString("message");
        String markup = resultSet.getString("markup");
        boolean lorcode = !"PLAIN".equals(markup);

        return new MessageText(text, lorcode);
      }
    }, msgid);
  }

  public Map<Integer, MessageText> getMessageText(Collection<Integer> msgids) {
    if (msgids.isEmpty()) {
      return ImmutableMap.of();
    }

    final Map<Integer, MessageText> out = Maps.newHashMapWithExpectedSize(msgids.size());

    namedJdbcTemplate.query(QUERY_MESSAGE_LIST,
            ImmutableMap.of("list", msgids),
            new RowCallbackHandler() {
              @Override
              public void processRow(ResultSet resultSet) throws SQLException {
                String text = resultSet.getString("message");
                String markup = resultSet.getString("markup");
                boolean lorcode = !"PLAIN".equals(markup);

                out.put(resultSet.getInt("id"), new MessageText(text, lorcode));
              }
            });

    return out;
  }

  public MarkupText getMarkupText(int msgid) {
    List<MarkupText> msgs = jdbcTemplate.query(QUERY_MESSAGE_TEXT, (ResultSet resultSet, int i) -> {
      return new MarkupText(resultSet.getString("message"), MarkupTextType.valueOf(resultSet.getString("markup")));
    }, msgid);
    if(msgs.isEmpty()) {
      return null;
    } else {
      return msgs.get(0);
    }
  }

  public Map<Integer, MarkupText> getMarkupText(Collection<Integer> msgids) {
    if (msgids.isEmpty()) {
      return ImmutableMap.of();
    }
    final Map<Integer, MarkupText> out = Maps.newHashMapWithExpectedSize(msgids.size());
    namedJdbcTemplate.query(QUERY_MESSAGE_LIST,
        ImmutableMap.of("list", msgids), (resultSet) -> {
          out.put(resultSet.getInt("id"), new MarkupText(resultSet.getString("message"), MarkupTextType.valueOf(resultSet.getString("markup"))));
        });

    return out;
  }

  public void updateMarkup(int msgid, MarkupText text) {
    namedJdbcTemplate.update(
        "UPDATE msgbase SET message=:message,markup=:markup WHERE id=:msgid",
        ImmutableMap.of("message", text.getText(), "markup", text.getType(), "msgid", msgid)
    );
  }

  public void updateMessage(int msgid, String text) {
    namedJdbcTemplate.update(
      "UPDATE msgbase SET message=:message WHERE id=:msgid",
      ImmutableMap.of("message", text, "msgid", msgid)
    );
  }

  public void appendMessage(int msgid, String text) {
    jdbcTemplate.update(
            "UPDATE msgbase SET message=message||? WHERE id=?",
            text,
            msgid
    );
  }
}
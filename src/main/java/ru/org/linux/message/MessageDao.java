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
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.org.linux.spring.dao.MessageText;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

@Repository
public class MessageDao {
  /**
   * Запрос тела сообщения и признака bbcode для сообщения
   */
  private static final String QUERY_MESSAGE_TEXT = "SELECT message, markup FROM msgbase WHERE id=?";
  private static final String QUERY_MESSAGE_LIST = "SELECT message, markup, id FROM msgbase WHERE id IN (?)";

  private JdbcTemplate jdbcTemplate;
  private NamedParameterJdbcTemplate namedJdbcTemplate;

  @Autowired
  public void setDataSource(DataSource dataSource) {
    jdbcTemplate = new JdbcTemplate(dataSource);
    namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
  }

  public Message getMessage(int id) {
    return jdbcTemplate.queryForObject(QUERY_MESSAGE_TEXT, new RowMapper<Message>() {
      @Override
      public Message mapRow(ResultSet resultSet, int i) throws SQLException {
        return new Message(resultSet.getString("message"), MessageType.valueOf(resultSet.getString("markup")));
      }
    }, id);
  }

  @Deprecated
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

  public Map<Integer, Message> getMessageList(Collection<Integer> ids) {
    if (ids.isEmpty()) {
      return ImmutableMap.of();
    }
    final Map<Integer, Message> out = Maps.newHashMapWithExpectedSize(ids.size());

    jdbcTemplate.query(QUERY_MESSAGE_LIST, new RowCallbackHandler() {
      @Override
      public void processRow(ResultSet resultSet) throws SQLException {
        out.put(resultSet.getInt("id"), new Message(resultSet.getString("message"), MessageType.valueOf(resultSet.getString("markup"))));
      }
    }, ids);
    return out;
  }

  @Deprecated
  public Map<Integer, MessageText> getMessageText(Collection<Integer> msgids) {
    if (msgids.isEmpty()) {
      return ImmutableMap.of();
    }

    final Map<Integer, MessageText> out = Maps.newHashMapWithExpectedSize(msgids.size());

    namedJdbcTemplate.query(
            "SELECT message, markup, id FROM msgbase WHERE id IN (:list)",
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

  public void updateMessage(int id, Message message) {
    jdbcTemplate.update(
        "UPDATE msgbase SET message=?, markup=? WHERE id=?",
        message.getText(), message.getType().toString(), id
    );
  }

  public void addMessage(int id, Message message) {
    jdbcTemplate.update(
        "INSERT INTO msgbase (id, message, markup) values (?,?,?)",
        id, message.getText(), message.getType().toString()
    );
  }

  @Deprecated
  public void updateMessage(int msgid, String text) {
    namedJdbcTemplate.update(
      "UPDATE msgbase SET message=:message WHERE id=:msgid",
      ImmutableMap.of("message", text, "msgid", msgid)
    );
  }

  @Deprecated
  public void appendMessage(int msgid, String text) {
    jdbcTemplate.update(
            "UPDATE msgbase SET message=message||? WHERE id=?",
            text,
            msgid
    );
  }
}
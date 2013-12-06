package ru.org.linux.message;

import com.google.common.collect.ImmutableSet;
import ru.org.linux.user.User;

import java.util.Map;
import java.util.Set;

public class PlainMessageExecutor implements MessageExecutor {
  private final String message;

  public PlainMessageExecutor(String message) {
    this.message = message;
  }

  @Override
  public String desc() {
    return "";
  }

  @Override
  public String html() {
    return message;
  }

  @Override
  public Set<User> repliers() {
    return ImmutableSet.of();
  }

  @Override
  public void setRenderParam(Map<String, Object> param) {
    // noop
  }

  @Override
  public MessageType getMarkupType() {
    return MessageType.PLAIN;
  }
}

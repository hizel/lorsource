package ru.org.linux.message;

import ru.org.linux.user.User;

import java.util.Map;
import java.util.Set;

public interface MessageExecutor {

  public String desc();
  public String html();
  public Set<User> repliers();
  public void setRenderParam(Map<String, Object> param);
  public MessageType getMarkupType();
}

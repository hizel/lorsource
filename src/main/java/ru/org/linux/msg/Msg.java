package ru.org.linux.msg;

public class Msg {
  final private String text;
  final private MsgMarkup markup;

  public Msg(String text, MsgMarkup markup) {
    this.text = text;
    this.markup = markup;
  }

  public String getText() {
    return text;
  }

  public MsgMarkup getMarkup() {
    return markup;
  }
}

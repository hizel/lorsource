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

  public boolean isBBCode() {
    return markup == MsgMarkup.BBCODE_TEX || markup == MsgMarkup.BBCODE_ULB;
  }
}

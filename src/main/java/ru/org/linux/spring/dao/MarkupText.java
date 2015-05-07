package ru.org.linux.spring.dao;

/**
 */
public class MarkupText {
  private final String text;
  private final MarkupTextType type;

  public MarkupText(String text, MarkupTextType type) {
    this.text = text;
    this.type = type;
  }

  public String getText() {
    return text;
  }

  public MarkupTextType getType() {
    return type;
  }

  public boolean isBBCode() {
    return (this.type == MarkupTextType.BBCODE_TEX)
        || (this.type == MarkupTextType.BBCODE_ULB);
  }
}

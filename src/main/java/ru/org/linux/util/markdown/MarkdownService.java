package ru.org.linux.util.markdown;

import org.pegdown.PegDownProcessor;
import org.springframework.stereotype.Service;

/**
 */
@Service
public class MarkdownService {
  private static final PegDownProcessor processor = new PegDownProcessor();

  public String parseComment(String text) {
    return processor.markdownToHtml(text);
  }
}

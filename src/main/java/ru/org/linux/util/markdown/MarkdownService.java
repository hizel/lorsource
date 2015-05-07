package ru.org.linux.util.markdown;

import static org.pegdown.Extensions.*;
import org.pegdown.PegDownProcessor;
import org.springframework.stereotype.Service;

/**
 */
@Service
public class MarkdownService {
  private static final int PegExtensions =
      AUTOLINKS | SUPPRESS_ALL_HTML | FENCED_CODE_BLOCKS | SMARTYPANTS;
  private static final PegDownProcessor processor = new PegDownProcessor(PegExtensions);

  public String parseComment(String text) {
    return processor.markdownToHtml(text);
  }
}

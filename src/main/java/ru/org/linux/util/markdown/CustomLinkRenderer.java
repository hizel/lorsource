package ru.org.linux.util.markdown;

import org.parboiled.common.StringUtils;
import org.pegdown.LinkRenderer;
import org.pegdown.ast.AutoLinkNode;
import org.pegdown.ast.RefLinkNode;

import static org.pegdown.FastEncoder.encode;

/**
 */
public class CustomLinkRenderer extends LinkRenderer {

  private final boolean nofollow;

  public CustomLinkRenderer(boolean nofollow) {
    this.nofollow = nofollow;
  }

  @Override
  public Rendering render(RefLinkNode node, String url, String title, String text) {
    Rendering rendering = new Rendering(url, text).withAttribute(Attribute.NO_FOLLOW);
    if (nofollow) {
      rendering = rendering.withAttribute(Attribute.NO_FOLLOW);
    }
    return StringUtils.isEmpty(title) ? rendering : rendering.withAttribute("title", encode(title));
  }

  @Override
  public Rendering render(AutoLinkNode node) {
    Rendering rendering = new Rendering(node.getText(), node.getText());
    if (nofollow) {
      rendering = rendering.withAttribute(Attribute.NO_FOLLOW);
    }
    return rendering;
  }
}

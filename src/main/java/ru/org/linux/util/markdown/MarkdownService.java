package ru.org.linux.util.markdown;

import org.pegdown.PegDownProcessor;
import org.pegdown.ToHtmlSerializer;
import org.pegdown.ast.RootNode;
import org.pegdown.plugins.PegDownPlugins;
import org.pegdown.plugins.ToHtmlSerializerPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.org.linux.user.UserDao;
import ru.org.linux.util.formatter.ToHtmlFormatter;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

import static org.pegdown.Extensions.*;

/**
 */
@Service
public class MarkdownService {


  @Autowired
  private UserDao userDao;

  @Autowired
  private ToHtmlFormatter formatter;

  private PegDownProcessor processor;
  private List<ToHtmlSerializerPlugin> secureSerializePlugins;
  private List<ToHtmlSerializerPlugin> insecureSerializePlugins;

  @PostConstruct
  public void init() {
    final int PegExtensions =
        AUTOLINKS | SUPPRESS_ALL_HTML | FENCED_CODE_BLOCKS | SMARTYPANTS;
    final PegDownPlugins plugins = new PegDownPlugins.Builder()
        .withPlugin(UserTagParser.class).build();

    processor = new PegDownProcessor(PegExtensions, plugins);

    secureSerializePlugins =
        Collections.singletonList((ToHtmlSerializerPlugin) (new UserTagSerializer(userDao, formatter, true)));
    insecureSerializePlugins =
        Collections.singletonList((ToHtmlSerializerPlugin) (new UserTagSerializer(userDao, formatter, false)));
  }

  public String parseComment(String text, boolean secure, boolean noref) {
    RootNode ast = processor.parseMarkdown(text.toCharArray());
    return secure ? (new ToHtmlSerializer(new CustomLinkRenderer(noref), secureSerializePlugins)).toHtml(ast) :
        (new ToHtmlSerializer(new CustomLinkRenderer(noref), insecureSerializePlugins)).toHtml(ast);
  }
}

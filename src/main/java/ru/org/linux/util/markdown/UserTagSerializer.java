package ru.org.linux.util.markdown;

import org.pegdown.Parser;
import org.pegdown.Printer;
import org.pegdown.ast.Node;
import org.pegdown.ast.Visitor;
import org.pegdown.plugins.ToHtmlSerializerPlugin;
import ru.org.linux.user.UserDao;
import ru.org.linux.util.formatter.ToHtmlFormatter;

/**
 */
public class UserTagSerializer extends Parser implements ToHtmlSerializerPlugin {

  private final UserDao userDao;
  private final ToHtmlFormatter formater;
  private final boolean secure;

  public UserTagSerializer(UserDao userDao, ToHtmlFormatter formatter, boolean secure) {
    super(ALL, 1000L, Parser.DefaultParseRunnerProvider);
    this.userDao = userDao;
    this.formater = formatter;
    this.secure = secure;
  }

  @Override
  public boolean visit(Node node, Visitor visitor, Printer printer) {
    if (node instanceof UserTagNode) {
      UserTagNode n = (UserTagNode) node;
      UserDao.FindUser findUser = userDao.findUser(n.getUsername());

      String result;
      if (findUser.isFound()) {
        if (!findUser.getUser().isBlocked()) {
          result = String.format(" <span style=\"white-space: nowrap\"><img src=\"/img/tuxlor.png\"><a style=\"text-decoration: none\" href=\"%s\">%s</a></span>",
              formater.memberURL(findUser.getUser(), secure), n.getUsername());
        } else {
          result = String.format(" <span style=\"white-space: nowrap\"><img src=\"/img/tuxlor.png\"><s><a style=\"text-decoration: none\" href=\"%s\">%s</a></s></span>",
              formater.memberURL(findUser.getUser(), secure), n.getUsername());
        }
      } else {
        result = '@' + n.getUsername();
      }
      printer.print(result);
      return true;
    }
    return false;
  }
}

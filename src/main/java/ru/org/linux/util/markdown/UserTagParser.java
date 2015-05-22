package ru.org.linux.util.markdown;

import org.parboiled.Rule;
import org.pegdown.Parser;
import org.pegdown.plugins.InlinePluginParser;

/**
 */
public class UserTagParser extends Parser implements InlinePluginParser {

  public UserTagParser() {
    super(ALL, 1000L, Parser.DefaultParseRunnerProvider);
  }

  public Rule UserTag() {
    return Sequence('@', OneOrMore(Alphanumeric()), push(new UserTagNode(match())));
  }

  @Override
  public Rule[] inlinePluginRules() {
    return new Rule[]{UserTag()};
  }
}

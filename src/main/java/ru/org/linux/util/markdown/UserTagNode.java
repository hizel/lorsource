package ru.org.linux.util.markdown;

import com.google.common.collect.ImmutableList;
import org.pegdown.ast.AbstractNode;
import org.pegdown.ast.Node;
import org.pegdown.ast.Visitor;

import java.util.List;

/**
 */
public class UserTagNode extends AbstractNode {
  private final String username;

  public UserTagNode(String username) {
    this.username = username;
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  @Override
  public List<Node> getChildren() {
    return ImmutableList.of();
  }

  public String getUsername() {
    return username;
  }
}

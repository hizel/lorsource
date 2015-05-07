package ru.org.linux.spring.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.*;

/**
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MsgbaseDaoIntegrationTestConfiguration.class)
@Transactional
public class MsgbaseDaoIntegrationTest {
  @Autowired
  MsgbaseDao msgbaseDao;

  @Test
  public void messageTextPlainTest() {
    MessageText msg = msgbaseDao.getMessageText(1934619);
    assertNotNull(msg);
    assertFalse(msg.isLorcode());
  }

  @Test
  public void messageTextLorcodeTest() {
    MessageText msg = msgbaseDao.getMessageText(1948655);
    assertNotNull(msg);
    assertTrue(msg.isLorcode());
  }

  @Test
  public void messageTextListTest() {
    Map<Integer, MessageText> msgs = msgbaseDao.getMessageText(Arrays.asList(new Integer[]{1934619, 1948655}));
    assertFalse(msgs.isEmpty());
    assertEquals(2, msgs.size());
    assertFalse(msgs.get(1934619).isLorcode());
    assertTrue(msgs.get(1948655).isLorcode());
  }

  @Test
  public void markupTextPlainTest() {
    MarkupText msg = msgbaseDao.getMarkupText(1934619);
    assertNotNull(msg);
    assertTrue(msg.getType() == MarkupTextType.PLAIN);
  }

  @Test
  public void markupTextLorcodeTest() {
    MarkupText msg = msgbaseDao.getMarkupText(1948655);
    assertNotNull(msg);
    assertTrue(msg.getType() == MarkupTextType.BBCODE_TEX);
  }

  @Test
  public void markupTextListTest() {
    Map<Integer, MarkupText> msgs = msgbaseDao.getMarkupText(Arrays.asList(new Integer[]{1934619, 1948655}));
    assertFalse(msgs.isEmpty());
    assertEquals(2, msgs.size());
    assertTrue(msgs.get(1934619).getType() == MarkupTextType.PLAIN);
    assertTrue(msgs.get(1948655).getType() == MarkupTextType.BBCODE_TEX);
  }

}

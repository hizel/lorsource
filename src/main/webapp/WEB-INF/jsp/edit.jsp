<%@ page contentType="text/html; charset=utf-8"%>
<%@ page
    import="java.util.SortedSet" %>
<%@ page import="ru.org.linux.site.Message" %>
<%@ page import="ru.org.linux.site.PreparedMessage" %>
<%@ page import="ru.org.linux.site.Tags" %>
<%@ page import="ru.org.linux.site.Template" %>
<%@ page import="ru.org.linux.util.HTMLFormatter" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="lor" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  ~ Copyright 1998-2010 Linux.org.ru
  ~    Licensed under the Apache License, Version 2.0 (the "License");
  ~    you may not use this file except in compliance with the License.
  ~    You may obtain a copy of the License at
  ~
  ~        http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~    Unless required by applicable law or agreed to in writing, software
  ~    distributed under the License is distributed on an "AS IS" BASIS,
  ~    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~    See the License for the specific language governing permissions and
  ~    limitations under the License.
  --%>

<%--@elvariable id="message" type="ru.org.linux.site.Message"--%>
<%--@elvariable id="preparedMessage" type="ru.org.linux.site.PreparedMessage"--%>
<%--@elvariable id="newMsg" type="ru.org.linux.site.Message"--%>
<%--@elvariable id="newPreparedMessage" type="ru.org.linux.site.PreparedMessage"--%>
<%--@elvariable id="group" type="ru.org.linux.site.Group"--%>
<%--@elvariable id="info" type="java.lang.String"--%>
<%--@elvariable id="editInfo" type="ru.org.linux.site.EditInfoDTO"--%>
<%--@elvariable id="commit" type="java.lang.Boolean"--%>
<%--@elvariable id="groups" type="java.util.List<ru.org.linux.site.Group>"--%>
<%--@elvariable id="topTags" type="java.util.SortedSet<String>"--%>
<%--@elvariable id="template" type="ru.org.linux.site.Template"--%>

<jsp:include page="/WEB-INF/jsp/head.jsp"/>
<title>Редактирование сообщения</title>
<script src="/js/jquery.validate.pack.js" type="text/javascript"></script>
<script src="/js/jquery.validate.ru.js" type="text/javascript"></script>

<script type="text/javascript" src="/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="/js/markitup/jquery.markitup.js"></script>
<script type="text/javascript" src="/js/markitup/sets/lor/set.js"></script>
<link rel="stylesheet" type="text/css" href="/js/markitup/skins/lor/style.css" />
<link rel="stylesheet" type="text/css" href="/js/markitup/sets/lor/style.css" />
<script type="text/javascript" >
	$(document).ready(function() {
			$("#newmsg").markItUp(myLORSettings);
			});
</script>

<script type="text/javascript">
  $(document).ready(function() {
    $("#messageForm").validate({
      messages : {
        title : "Введите заголовок"
      }
    });
  });
</script>
<jsp:include page="/WEB-INF/jsp/header.jsp"/>
<%
    Message newMsg = (Message) request.getAttribute("newMsg");
    PreparedMessage newPreparedMsg = (PreparedMessage) request.getAttribute("newPreparedMessage");
    SortedSet<String> topTags = (SortedSet<String>) request.getAttribute("topTags");
%>
<c:if test="${info!=null}">
  <h1>${info}</h1>
  <h2>Текущая версия сообщения</h2>
  <div class=messages>
    <lor:message messageMenu="<%= null %>" preparedMessage="${preparedMessage}" message="${message}" showMenu="false" user="${template.nick}"/>
  </div>
  <h2>Ваше сообщение</h2>
</c:if>
<c:if test="${info==null}">
  <h1>Редактирование</h1>
</c:if>

<div class=messages>
  <lor:message messageMenu="<%= null %>" preparedMessage="${newPreparedMessage}" message="${newMsg}" showMenu="false" user="${template.nick}"/>
</div>

<form action="edit.jsp" name="edit" method="post" id="messageForm">
  <input type="hidden" name="msgid" value="${message.id}">
  <c:if test="${editInfo!=null}">
    <input type="hidden" name="lastEdit" value="${editInfo.editdate.time}">
  </c:if>

  <c:if test="${not message.expired}">
  <label>Заголовок:
  <input type=text name=title class="required" size=40 value="<%= newMsg.getTitle()==null?"":HTMLFormatter.htmlSpecialChars(newMsg.getTitle()) %>" ></label><br>

  <br>
  <textarea id="newmsg" name="newmsg" cols="70" rows="20"><c:out escapeXml="true" value="${newMsg.message}"/></textarea>
  <br><br>
    <c:if test="${message.haveLink}">
      <label>Текст ссылки:
      <input type=text name=linktext size=60
             value="<%= newMsg.getLinktext()==null?"":HTMLFormatter.htmlSpecialChars(newMsg.getLinktext()) %>"></label><br>
      <label>Ссылка :
      <input type=text name=url size=70
             value="<%= newMsg.getUrl()==null?"":HTMLFormatter.htmlSpecialChars(newMsg.getUrl()) %>"></label><br>
    </c:if>
  </c:if>

  <c:if test="${group.moderated}">
  <label>Теги:
  <input type="text" size="70" name="tags" id="tags" value="<%= Tags.toString(newPreparedMsg.getTags()) %>"><br>
  Популярные теги: <%= Tags.getEditTags(topTags) %></label> <br>
    </c:if>
  <br>
  <input type="submit" value="Отредактировать">
  &nbsp;
  <input type=submit name=preview value="Предпросмотр">
  <c:if test="${commit}">
    <br><br>
    <label>Группа:
    <select name="chgrp">
      <c:forEach var="group" items="${groups}">
        <c:if test="${group.id != message.groupId}">
          <option value="${group.id}">${group.title}</option>
        </c:if>
        <c:if test="${group.id == message.groupId}">
          <option value="${group.id}" selected="selected">${group.title}</option>
        </c:if>
      </c:forEach>
    </select></label><br>
    <label>Bonus score (от 0 до 20):
    <input type=text name=bonus size=40 value="3"></label><br>
    <label>Мини-новость:
      <c:if test="${message.minor}">
        <input type="checkbox" checked="checked" name="minor">
      </c:if>
      <c:if test="${not message.minor}">
        <input type="checkbox" name="minor">
      </c:if>
    </label> <br>
    <input type=submit name=commit value="Подтвердить">
  </c:if>
</form>
<jsp:include page="/WEB-INF/jsp/footer.jsp"/>

<%--
  Created by IntelliJ IDEA.
  User: nastyaergenova
  Date: 22.01.2025
  Time: 18:46
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Title</title>
  </head>
  <body>
    <h1>JSP</h1>
    <h2>Вирази</h2>
    <%= 2 + 3 %>
    <h2>Змінні</h2>
    <%
      int x = 10;
    %>
    <%= x %>
    <h2>Инструкції управління</h2>
    <% if ( x % 2 == 0) { %>
      <b>Число <%= x%> парне</b>
    <%} else {%>
      <i>Число <%= x%> непарне</i>
    <%} %>

    <ul>
  <% for (int i = 0; i < 10; i++) {%>
    <li><%= i + 1 %></li>
    <%} %>
    </ul>
  </body>
</html>

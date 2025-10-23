<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="javax.servlet.http.HttpSession"%>
<%
    session = request.getSession(false);
    if (session != null) {
        session.invalidate();
    }
%>
<script>
    alert("Deslogado!");
    window.location.href = "../index.html";
</script>
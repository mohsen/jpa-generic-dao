<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://oldworld.genericdao.googlecode.com/tags" prefix="m" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>A Citizen</title>
</head>
<body>
	<h1>A Citizen</h1>
	<spring:hasBindErrors name="citizen">
		<h3>You have errors in your input!</h3>
		<font color="red"> <c:forEach items="${errors.allErrors}"
			var="error">
			<spring:message code="${error.code}" text="${error.defaultMessage}" />
		</c:forEach> </font>
	</spring:hasBindErrors>
	
	<%--<c:forEach items="${requestScope}" var="entry">
		${entry.key} : ${entry.value} <br/>
	</c:forEach>--%>
	
	<form:form commandName="citizen" method="POST" action="edit.do">  
	    <form:hidden path="id"/>
	    Name: <form:input path="name" /> <form:errors path="name" /> <br/>
	    Occupation: <form:input path="job" /> <form:errors path="job" /> <br/>
	    Town: <form:select path="town.id">
	    	<c:forEach items="${towns}" var="town">
	    		<form:option value="${town.id}" label="${town.name}"/>
	    	</c:forEach>
	    </form:select> <form:errors path="town.id" /> <br/>
		<m:searchToInputs/>
		<input type="submit" title="Save" />
		<m:a href="list.do">Cancel</m:a>
	</form:form>
</body>
</html>
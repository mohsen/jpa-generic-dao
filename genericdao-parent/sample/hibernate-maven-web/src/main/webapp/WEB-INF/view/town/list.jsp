<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://oldworld.genericdao.googlecode.com/tags" prefix="m" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css"/>
</head>
<body>
	<p class="navigation">
		Navigation: <span>Towns</span> / <a href="../citizen/list.do">Citizens</a> / ((<a href="loadSampleData.do">Load Sample Data</a>))
	</p>
	<h1>These are the Towns</h1>
	<form class="filters" action="" method="GET">
		<m:searchToInputs includeFilters="false"/>
		Name <input type="text" name="f-name" value="${param['f-name']}"/><br/>
		Population 
		<select name="fo-population">
			<option value="${m:const('com.googlecode.genericdao.search.Filter.OP_EQUAL')}" ${param['fo-population'] == m:const('com.googlecode.genericdao.search.Filter.OP_EQUAL') ? 'selected="selected"' : ''}>=</option>
			<option value="${m:const('com.googlecode.genericdao.search.Filter.OP_LESS_OR_EQUAL')}" ${param['fo-population'] == m:const('com.googlecode.genericdao.search.Filter.OP_LESS_OR_EQUAL') ? 'selected="selected"' : ''}>&lt;=</option>
			<option value="${m:const('com.googlecode.genericdao.search.Filter.OP_GREATER_OR_EQUAL')}" ${param['fo-population'] == m:const('com.googlecode.genericdao.search.Filter.OP_GREATER_OR_EQUAL') ? 'selected="selected"' : ''}>&gt;=</option>
		</select>
		<input type="text" name="f-population" value="${param['f-population']}"/><br/>
		<input type="submit" value="Filter"/>
		<m:a href="list.do" includeFilters="false"><button type="button">Clear Filter</button></m:a>
	</form>
	<table cellspacing="0" cellpadding="0" border="0">
		<tr>
			<th><m:sort path="name">Name</m:sort></th>
			<th><m:sort path="population">Population</m:sort></th>
			<th>&nbsp;</th>
			<th>&nbsp;</th>
		</tr>
		<c:forEach items="${townList}" var="town">
			<tr>
				<td><c:out value="${town.name}"/></td>
				<td><c:out value="${town.population}"/></td>
				<td><m:a href="edit.do?id=${town.id}">Edit</m:a></td>
				<td><m:a href="delete.do?id=${town.id}">Delete</m:a></td>
			</tr>
		</c:forEach>
	</table>
	<m:a href="edit.do">Add a Town</m:a>
</body>
</html>

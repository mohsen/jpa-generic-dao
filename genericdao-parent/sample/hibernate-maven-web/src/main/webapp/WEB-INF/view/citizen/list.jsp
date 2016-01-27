<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://oldworld.genericdao.googlecode.com/tags" prefix="m" %>
<html>
<head>
	<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css"/>
</head>
<body>
	<p class="navigation">
		Navigation: <a href="../town/list.do">Towns</a> / <span>Citizens</span>
	</p>
	<h1>These are the Citizens</h1>
	<form class="filters" id="filterForm" action="" method="GET">
		<m:searchToInputs includeFilters="false"/>
		Name <input type="text" name="f-name" value="${param['f-name']}"/><br/>
		Occupation <input type="text" name="f-job" value="${param['f-job']}"/><br/>
		Town <input type="text" name="f-town.name" value="${param['f-town.name']}"/><br/>
		<input type="submit" title="Filter"/>
		<m:a href="list.do" includeFilters="false"><button type="button">Clear Filter</button></m:a>
	</form>
	<table cellpadding="0" cellspacing="0" border="0">
		<tr>
			<th><m:sort path="name">Name</m:sort></th>
			<th><m:sort path="job">Occupation</m:sort></th>
			<th><m:sort path="town.name">Town</m:sort></th>
			<th>&nbsp;</th>
			<th>&nbsp;</th>
		</tr>
		
		<c:forEach items="${citizenList}" var="citizen">
			<tr>
				<td><c:out value="${citizen.name}"/></td>
				<td><c:out value="${citizen.job}"/></td>
				<td><c:out value="${citizen.town.name}"/></td>
				<td><m:a href="edit.do?id=${citizen.id}">Edit</m:a></td>
				<td><m:a href="delete.do?id=${citizen.id}">Delete</m:a></td>
			</tr>
		</c:forEach>
	</table>
	<p>
		<m:a href="list.do?page=${page - 1}" disabled="${page <= 1}"  includePaging="false">Previous</m:a>
		Page ${page} of ${pageCount}
		<m:a href="list.do?page=${page + 1}" disabled="${page >= pageCount}" includePaging="false">Next</m:a>
	</p>
	<m:a href="edit.do">Add a Citizen</m:a>
</body>
</html>

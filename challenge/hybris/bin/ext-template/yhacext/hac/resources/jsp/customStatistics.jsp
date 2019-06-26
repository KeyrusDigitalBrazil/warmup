<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<% pageContext.setAttribute("newLineChar", "\n"); %>
<html>
<head>
	<title>Statistics</title>
	<link rel="stylesheet" href="<c:url value="/static/css/table.css"/>" type="text/css" media="screen, projection" />
	<link rel="stylesheet" href="<c:url value="/static/css/customStatistics.css"/>" type="text/css" media="screen, projection" />

	<script type="text/javascript" src="<c:url value="/static/js/jquery.dataTables.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/static/js/history.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/static/js/customStatistics.js"/>"></script>
</head>
<body>
	<div class="prepend-top span-17 colborder" id="content">
		<button id="toggleSidebarButton">&gt;</button>
		<div class="marginLeft" id="inner">
			<h2>Statistics</h2>
			
			<table id="statistics">
				<thead>
					<tr>
						<th>Test</th>
						<th>Duration</th>
						<th>Message</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${monitor.results}" var="result">
						<tr>
							<td width="25%">${result.name}</td>
							<td class="status-OK">${result.duration}</td>
							<td>${fn:replace(result.message, newLineChar, '<br/>')}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
	<div id="dialogContainer"></div>
</body>
</html>


<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<c:set var="contextPath" value="${fn:split(pageContext.request.contextPath, '/')[0]}" />
<title><spring:message code="title.consignment.tracking.mock" /></title>
<link rel="stylesheet" href="/${contextPath}/_ui/responsive/common/css/jquery.datetimepicker.css">
<script type="text/javascript" src="/${contextPath}/_ui/responsive/common/js/jquery-3.2.1.min.js"></script>
<script type="text/javascript" src="/${contextPath}/_ui/responsive/common/js/jquery.datetimepicker.min.js"></script>

<script type="text/javascript">
$(function(){
	$("#eventDate").datetimepicker();
	$("form").submit(function(){
		if(!$("#trackingId").val() || !$("#eventDate").val() || !$("#eventLocation").val() || !$("#eventDetail").val()){
			return false;
		}
		return true;
	});
});
</script>
</head>
<body>
	<img alt="logo" src="/${contextPath}/_ui/responsive/common/images/logo-hybris-responsive.png" />
	<form:form method="post">
		<h2 align="center">
			<spring:message code="title.consignment.tracking.mock" />
		</h2>
		<hr/>
		<br/>
		<table border="0" style="width:60%" cellpadding="5" cellspacing="5" align="center">
			<tr>
				<td width="15%"  align="right">
					<label for="carrier">
						<spring:message code="label.consignment.carrier.name" />:
					</label>
				</td>
				<td width="85%" align="left">
					<span id="carrier">${carrier}</span>
				</td>
			</tr>
			<tr>
				<td width="15%" align="right">
					<label for="trackingId">
						<spring:message code="label.tracking.id" />:
					</label>
				</td>
				<td align="left">
					<input id="trackingId" style="width:100%" type="text" name="trackingId" />
				</td>
			</tr>
			<tr>
				<td style="width:15%" align="right">
					<label for="consignmentStatus">
						<spring:message code="label.tracking.status" />:
					</label>
				</td>
				<td width="85%" align="left">
					<select style="width:100%" name="consignmentStatus">
						<c:forEach var="status" items="${statuses}">
							<option value="${status.code}">${status.code}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td style="width:15%" align="right">
					<label>
						<spring:message code="label.consignment.event.date" />:
					</label>
				</td>
				<td width="85%" align="left">
					<input id="eventDate" style="width:100%" type="text" name="eventDate" />
				</td>
			</tr>
			<tr>
				<td style="width:15%" align="right">
					<label for="eventLocation">
						<spring:message code="label.consignment.tracking.location" />:
					</label>
				</td>
				<td width="85%" align="left">
					<input id="eventLocation" style="width:100%" type="text" name="eventLocation" />
				</td>
			</tr>
			<tr>
				<td style="width:15%" align="right" valign="top">
					<label for="eventDetail">
						<spring:message code="label.consignment.tracking.details" />:
					</label>
				</td>
				<td style="width:85%" align="left">
					<textarea id="eventDetail" rows="5" style="width:100%" name="eventDetail"></textarea>
				</td>
			</tr>
			<tr>
				<td align="right" colspan="2">
					<input id="save" type="submit" name="action" value="<spring:message code='button.save' />" />
				</td>
			</tr>
		</table>
	</form:form>
</html>
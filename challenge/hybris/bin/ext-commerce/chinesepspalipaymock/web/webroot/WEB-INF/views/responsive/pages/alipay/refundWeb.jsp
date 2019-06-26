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
<title>Chinese PSP Alipay Payment Service Mock</title>
<script type="text/javascript" src="/${contextPath}/_ui/shared/js/jquery-3.2.1.min.js"></script>
<script type="text/javascript" src="/${contextPath}/_ui/shared/js/chinesepspalipaymock.js"></script>

</head>
<body style="font-family: arial;">

	<img alt="logo" src="/${contextPath}/_ui/shared/images/mock/logo-hybris-responsive.png" />

	<h2>Chinese PSP Alipay Payment Service Mock</h2>
	<h4>Transferred parameters and values</h4>

	<table border="0" cellspacing="2" cellpadding="1">
		<c:forEach var="var" items="${params}">
			<tr>
				<c:if test = "${var.key != 'detail_data'}">
					<td nowrap="nowrap" align="right">${var.key}:</td>
					<td nowrap="nowrap">${var.value}</td>
				</c:if>
			</tr>
		</c:forEach>
		<c:if test = "${not empty detail_data}">
			<tr>
				<td nowrap="nowrap" align="right">detail_data:</td>
				<td nowrap="nowrap">${detail_data}</td>
			</tr>
		</c:if>
	</table>


	<p />
	<h4>
		Signature:
		<c:choose>
			<c:when test="${signIsValid}">
				<span style="color: green; font-size: 200%">VALID</span>
			</c:when>
			<c:otherwise>
				<span style="color: red; font-size: 200%">INVALID</span>
			</c:otherwise>
		</c:choose>
	</h4>

	<form id="mock" action="${baseGateWay}/doRefund" method="POST">
	<c:forEach var="var" items="${params}">
			<input name="${var.key}" value="${var.value}" readonly style="display: none;"/>
		</c:forEach>
		<table>
			<tr>
				<td>
					<h4>Transaction Status:</h4>
				</td>
				<td><select id="tradeStatus" title="trade_status" name="trade_status">
						<option value="" />
						<option value="SUCCESS">SUCCESS</option>
						<option value="FAILED">FAILED</option>
				</select></td>
			<tr>
			<tr>
				<td>
					<h4>Error Code:</h4>
				</td>
				<td><select id="errorCode" title="error_Code" name="error_code">
						<option value="" />
						<option value="SUCCESS">SUCCESS</option>
						<option value="ILLEGAL_SIGN">ILLEGAL_SIGN</option>
						<option value="ILLEGAL_DYN_MD5_KEY">ILLEGAL_DYN_MD5_KEY</option>
						<option value="ILLEGAL_ENCRYPT">ILLEGAL_ENCRYPT</option>
						<option value="ILLEGAL_ARGUMENT">ILLEGAL_ARGUMENT</option>
						<option value="ILLEGAL_SERVICE">ILLEGAL_SERVICE</option>
						<option value="ILLEGAL_USER">ILLEGAL_USER</option>
						<option value="ILLEGAL_PARTNER">ILLEGAL_PARTNER</option>
						<option value="ILLEGAL_EXTERFACE">ILLEGAL_EXTERFACE</option>
						<option value="ILLEGAL_PARTNER_EXTERFACE">ILLEGAL_PARTNER_EXTERFACE</option>
						<option value="ILLEGAL_SECURITY_PROFILE">ILLEGAL_SECURITY_PROFILE</option>
						<option value="ILLEGAL_AGENT">ILLEGAL_AGENT</option>
						<option value="ILLEGAL_SIGN_TYPE">ILLEGAL_SIGN_TYPE</option>
						<option value="ILLEGAL_CHARSET">ILLEGAL_CHARSET</option>
						<option value="ILLEGAL_CLIENT_IP">ILLEGAL_CLIENT_IP</option>
						<option value="HAS_NO_PRIVILEGE">HAS_NO_PRIVILEGE</option>
						<option value="SESSION_TIMEOUT">SESSION_TIMEOUT</option>
						<option value="ILLEGAL_DIGEST_TYPE">ILLEGAL_DIGEST_TYPE</option>
						<option value="ILLEGAL_DIGEST">ILLEGAL_DIGEST</option>
						<option value="ILLEGAL_FILE_FORMAT">ILLEGAL_FILE_FORMAT</option>
						<option value="ILLEGAL_TARGET_SERVICE">ILLEGAL_TARGET_SERVICE</option>
						<option value="ILLEGAL_ACCESS_SWITCH_SYSTEM">ILLEGAL_ACCESS_SWITCH_SYSTEM</option>
						<option value="ILLEGAL_ENCODING">ILLEGAL_ENCODING</option>
						<option value="EXTERFACE_IS_CLOSED">EXTERFACE_IS_CLOSED</option>
						<option value="ILLEGAL_REQUEST_REFERER">ILLEGAL_REQUEST_REFERER</option>
						<option value="ILLEGAL_ANTI_PHISHING_KEY">ILLEGAL_ANTI_PHISHING_KEY</option>
						<option value="ANTI_PHISHING_KEY_TIMEOUT">ANTI_PHISHING_KEY_TIMEOUT</option>
						<option value="ILLEGAL_EXTER_INVOKE_IP">ILLEGAL_EXTER_INVOKE_IP</option>
						<option value="BATCH_NUM_EXCEED_LIMIT">BATCH_NUM_EXCEED_LIMIT</option>
						<option value="REFUND_DATE_ERROR">REFUND_DATE_ERROR</option>
						<option value="BATCH_NUM_ERROR">BATCH_NUM_ERROR</option>
						<option value="BATCH_NUM_NOT_EQUAL_TOTAL">BATCH_NUM_NOT_EQUAL_TOTAL</option>
						<option value="SINGLE_DETAIL_DATA_EXCEED_LIMIT">SINGLE_DETAIL_DATA_EXCEED_LIMIT</option>
						<option value="NOT_THIS_SELLER_TRADE">NOT_THIS_SELLER_TRADE</option>
						<option value="DUBL_TRADE_NO_IN_SAME_BATCH">DUBL_TRADE_NO_IN_SAME_BATCH</option>
						<option value="DUPLICATE_BATCH_NO">DUPLICATE_BATCH_NO</option>
						<option value="TRADE_STATUS_ERROR">TRADE_STATUS_ERROR</option>
						<option value="BATCH_NO_FORMAT_ERROR">BATCH_NO_FORMAT_ERROR</option>
						<option value="SELLER_INFO_NOT_EXIST">SELLER_INFO_NOT_EXIST</option>
						<option value="PARTNER_NOT_SIGN_PROTOCOL">PARTNER_NOT_SIGN_PROTOCOL</option>
						<option value="NOT_THIS_PARTNERS_TRADE">NOT_THIS_PARTNERS_TRADE</option>
						<option value="DETAIL_DATA_FORMAT_ERROR">DETAIL_DATA_FORMAT_ERROR</option>
						<option value="PWD_REFUND_NOT_ALLOW_ROYALTY">PWD_REFUND_NOT_ALLOW_ROYALTY</option>
						<option value="NANHANG_REFUND_CHARGE_AMOUNT_ERROR">NANHANG_REFUND_CHARGE_AMOUNT_ERROR</option>
						<option value="REFUND_AMOUNT_NOT_VALID">REFUND_AMOUNT_NOT_VALID</option>
						<option value="TRADE_PRODUCT_TYPE_NOT_ALLOW_REFUND">TRADE_PRODUCT_TYPE_NOT_ALLOW_REFUND</option>
						<option value="RESULT_FACE_AMOUNT_NOT_VALID">RESULT_FACE_AMOUNT_NOT_VALID</option>
						<option value="REFUND_CHARGE_FEE_ERROR">REFUND_CHARGE_FEE_ERROR</option>
						<option value="REASON_REFUND_CHARGE_ERR">REASON_REFUND_CHARGE_ERR</option>
						<option value="RESULT_AMOUNT_NOT_VALID">RESULT_AMOUNT_NOT_VALID</option>
						<option value="RESULT_ACCOUNT_NO_NOT_VALID">RESULT_ACCOUNT_NO_NOT_VALID</option>
						<option value="REASON_TRADE_REFUND_FEE_ERR">REASON_TRADE_REFUND_FEE_ERR</option>
						<option value="REASON_HAS_REFUND_FEE_NOT_MATCH">REASON_HAS_REFUND_FEE_NOT_MATCH</option>
						<option value="TXN_RESULT_ACCOUNT_STATUS_NOT_VALID">TXN_RESULT_ACCOUNT_STATUS_NOT_VALID</option>
						<option value="TXN_RESULT_ACCOUNT_BALANCE_NOT_ENOUGH">TXN_RESULT_ACCOUNT_BALANCE_NOT_ENOUGH</option>
						<option value="REASON_REFUND_AMOUNT_LESS_THAN_COUPON_FEE">REASON_REFUND_AMOUNT_LESS_THAN_COUPON_FEE</option>
						<option value="SYSTEM_ERROR">SYSTEM_ERROR</option>
						<option value="BATCH_REFUND_STATUS_ERROR">BATCH_REFUND_STATUS_ERROR</option>
						<option value="BATCH_REFUND_DATA_ERROR">BATCH_REFUND_DATA_ERROR</option>
						<option value="REFUND_TRADE_FAILED">REFUND_TRADE_FAILED</option>
						<option value="REFUND_FAIL">REFUND_FAIL</option>
				</select></td>
			</tr>
			</table>
			<input id="notifyRefundBtn" type="button" name="action" value="notify" /> 

	</form>
</body>
</html>
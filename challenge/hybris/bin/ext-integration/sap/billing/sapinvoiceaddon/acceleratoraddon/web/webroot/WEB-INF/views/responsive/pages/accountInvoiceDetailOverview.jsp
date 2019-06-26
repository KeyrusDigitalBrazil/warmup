<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="invoice" tagdir="/WEB-INF/tags/addons/sapinvoiceaddon/responsive/invoice" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<div class="well-lg well well-tertiary clearfix">
    <ycommerce:testId code="orderDetail_overview_section">
        <invoice:accountInvoiceDetailsOverview invoiceData="${invoiceData}"/>
    </ycommerce:testId>
</div>
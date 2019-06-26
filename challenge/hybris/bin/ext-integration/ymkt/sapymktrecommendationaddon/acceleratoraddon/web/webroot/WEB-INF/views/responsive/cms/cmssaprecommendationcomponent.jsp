<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>
<%@ taglib prefix="component" tagdir="/WEB-INF/tags/shared/component"%>

<jsp:useBean id="random" class="java.util.Random" scope="application" />
<c:set var="cid" value="reco${random.nextInt(1000)}" />

<div class="carousel-component" id="${cid}"
	data-prodcode="${productCode}" data-componentId="${componentId}"
	data-base-url="${request.contextPath}"
	data-smartedit-component-type="CMSSAPRecommendationComponent"
	data-smartedit-component-id="${componentId}"></div>

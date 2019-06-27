<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<template:page pageTitle="${pageTitle}">
	<spring:htmlEscape defaultHtmlEscape="true" />
	
	<c:set var="searchUrl" value="/v/${vendor.code}/reviews?sort=${ycommerce:encodeUrl(searchPageData.pagination.sort)}"/>
	
	<div class="row reviews-container">
		<div class="container-lg col-md-12 col-lg-10">
		
			<div class="vendor-index-title"><spring:theme code="text.vendor.reviews.page.headline" arguments="${vendor.name}" text="Reviews for Vendor3" /></div>
			<c:choose>
					<c:when test="${not empty searchPageData.results}">
			<div class="account-orderhistory-pagination">
				 <nav:pagination top="true" msgKey="text.vendor.reviews.page" showCurrentPageInfo="true" hideRefineButton="true" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" searchUrl="${searchUrl}"  numberPagesShown="${numberPagesShown}"/>
			</div>
		
			<div class="reviews_container col-md-12">
			
				<c:forEach items="${searchPageData.results}" var="review">
				
					<div class="review-container col-md-12">
					
						<div class="review-lt col-md-2 col-lg-3">
							<div class="reviewer col-md-12" >
								<label class="reviewer-label">${fn:escapeXml(review.customer.firstName)}&nbsp;${fn:escapeXml(review.customer.lastName)}</label>
							</div>
							<div class="review-date col-md-12">
								<fmt:formatDate value="${review.createDate}" dateStyle="medium" timeStyle="short" type="both" />
							</div>
						</div>
						
						<div class="review-gt col-md-10 col-lg-9">
							<div class="review-rating col-md-12">
								<div class="reviews-rating col-md-4 col-sm-4">
									<div class="rating-header col-md-7">
										<label><spring:theme code="text.vendor.review.satisfaction"/></label>
									</div>
									<div class="stars rating col-md-5">
										<div class="rating-stars pull-left js-ratingCalc" data-rating='{"rating":${review.satisfaction},"total":5}'>
											<div class="greyStars">
												<c:forEach begin="1" end="5">
													<span class="glyphicon glyphicon-star"></span>
												</c:forEach>
											</div>
											<div class="greenStars js-greenStars">
												<c:forEach begin="1" end="5">
													<span class="glyphicon glyphicon-star active"></span>
												</c:forEach>
											</div>
										</div>
									</div>
								</div>
								<div class="reviews-rating col-md-4 col-sm-4">
									<div class="rating-header col-md-7">
										<label><spring:theme code="text.vendor.review.delivery"/></label>
									</div>
									<div class="stars rating col-md-5">
										<div class="rating-stars pull-left js-ratingCalc" data-rating='{"rating":${review.delivery},"total":5}'>
											<div class="greyStars">
												<c:forEach begin="1" end="5">
													<span class="glyphicon glyphicon-star"></span>
												</c:forEach>
											</div>
											<div class="greenStars js-greenStars">
												<c:forEach begin="1" end="5">
													<span class="glyphicon glyphicon-star active"></span>
												</c:forEach>
											</div>
										</div>
									</div>
								</div>
								<div class="reviews-rating col-md-4 col-sm-4">
									<div class="rating-header col-md-7">
										<label><spring:theme code="text.vendor.review.communication"/></label>
									</div>
									<div class="stars rating col-md-5">
										<div class="rating-stars pull-left js-ratingCalc" data-rating='{"rating":${review.communication},"total":5}'>
											<div class="greyStars">
												<c:forEach begin="1" end="5">
													<span class="glyphicon glyphicon-star"></span>
												</c:forEach>
											</div>
											<div class="greenStars js-greenStars">
												<c:forEach begin="1" end="5">
													<span class="glyphicon glyphicon-star active"></span>
												</c:forEach>
											</div>
										</div>
									</div>
								</div>
							</div>
							<div class="review-comment col-md-12">
								${fn:escapeXml(review.comment)}
							</div>
						</div>
					</div>
				</c:forEach>
			</div>
			
			<div class="account-orderhistory-pagination review-pagination-bottom col-md-12">
				 <nav:pagination top="false" msgKey="text.vendor.reviews.page" showCurrentPageInfo="true" hideRefineButton="true" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" searchUrl="${searchUrl}"  numberPagesShown="${numberPagesShown}"/>
			</div>
			</c:when>
			<c:otherwise>
				<div class="vendor_no_reviews">
					<spring:theme code="text.vendor.noreviews" text="No reviews found" />
				</div>
			</c:otherwise>
			</c:choose>	
		</div>
	</div>
	
</template:page>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<spring:htmlEscape defaultHtmlEscape="true" />
<template:page pageTitle="${pageTitle}">
<div class="row">
	<div class="container-lg col-md-6">
		<div class="order-review-head">
			<p>
				<c:if test="${consignment.entries.size() eq 1}">
					<spring:theme var="headline" code="text.order.review.item.headline" htmlEscape="false" >
						<spring:argument value="${consignment.entries.size()}" />
						<spring:argument value="${consignment.vendor.name}" />
					</spring:theme>
					${ycommerce:sanitizeHTML(headline)}
				</c:if>
				<c:if test="${consignment.entries.size() gt 1}">
					<spring:theme var="headline" code="text.order.review.items.headline" htmlEscape="false" >
						<spring:argument value="${consignment.entries.size()}" />
						<spring:argument value="${consignment.vendor.name}" />
					</spring:theme>
					${ycommerce:sanitizeHTML(headline)}
				</c:if>			
			</p>
		</div>
        <div class="order-review-form">
			<form:form method="post" id="order-review-form" commandName="orderReviewForm">
				<c:forEach items="${consignment.entries}" var="entry" varStatus="status">
					<div class="order-review-product_list">
						<div class="headline">
							<spring:theme code="text.order.review.item" >
								<spring:argument value="${status.count}" />
								<spring:argument value="${entry.orderEntry.product.name}" />
							</spring:theme>
						</div>
						<div class="row">
							<template:errorSpanField path="productReviewForms[${status.index}].rating">
								<div class="col-md-3 order-review-form-label">
									<label class="order-review-label">
										<spring:theme code="text.order.review.rating"/>
										<span class="skip"><form:errors path="productReviewForms[${status.index}].rating"/></span>
									</label>
								</div>
								<div class="col-md-9">
									<div class="review-rating review-rating-set review-js-ratingCalcSet">
										<div class="review-rating-stars review-js-writeReviewStars">
					                        <c:forEach  begin="1" end="10" varStatus="loop">
	                            				<span class="review-js-ratingIcon glyphicon glyphicon-star ${loop.index % 2 == 0 ? 'lh' : 'fh'}"></span>
	                        				</c:forEach>
					                    </div>
					                    <input type="hidden" class="review-rating-input" name="productReviewForms[${fn:escapeXml(status.index)}].rating" value="${fn:escapeXml(orderReviewForm.productReviewForms[status.index].rating)}"/>
									</div>
								</div>
							</template:errorSpanField>
						</div>
						<div class="row">
							<template:errorSpanField path="productReviewForms[${status.index}].comment">
								<div class="col-md-3 order-review-form-label">
									<label class="order-review-label">
										<spring:theme code="text.order.review.comment"/>
										<span class="skip"><form:errors path="productReviewForms[${status.index}].comment"/></span>
									</label>
								</div>
								<div class="col-md-9">
									<textarea name="productReviewForms[${fn:escapeXml(status.index)}].comment" class="form-control order-review-textarea" rows="5">${fn:escapeXml(orderReviewForm.productReviewForms[status.index].comment)}</textarea>
								</div>
							</template:errorSpanField>
						</div>
						<input type="hidden" name="productReviewForms[${fn:escapeXml(status.index)}].productCode" value="${fn:escapeXml(entry.orderEntry.product.code)}"/>
					</div>
				</c:forEach>
				
				<div class="order-review-product_list">
					<div class="headline">
						<spring:theme code="text.order.review.vendor.headline" arguments="${consignment.vendor.name}"/>
					</div>
					<div class="row">
						<template:errorSpanField path="satisfaction">
							<div class="col-md-3 order-review-form-label">
								<label class="order-review-label">
									<spring:theme code="text.order.review.satisfaction"/>
									<span class="skip"><form:errors path="satisfaction"/></span>
								</label>
							</div>
							<div class="col-md-9">
								<div class="review-rating review-rating-set review-js-ratingCalcSet">
									<div class="review-rating-stars review-js-writeReviewStars">
				                        <c:forEach  begin="1" end="10" varStatus="loop">
	                           				<span class="review-js-ratingIcon glyphicon glyphicon-star ${loop.index % 2 == 0 ? 'lh' : 'fh'}"></span>
	                       				</c:forEach>
				                    </div>
				                    <input type="hidden" class="review-rating-input" name="satisfaction" value="${fn:escapeXml(orderReviewForm.satisfaction)}"/>
								</div>
							</div>
						</template:errorSpanField>
					</div>
					<div class="row">
						<template:errorSpanField path="delivery">
							<div class="col-md-3 order-review-form-label">
								<label class="order-review-label">
									<spring:theme code="text.order.review.delivery"/>
									<span class="skip"><form:errors path="delivery"/></span>
								</label>
							</div>
							<div class="col-md-9">
								<div class="review-rating review-rating-set review-js-ratingCalcSet">
									<div class="review-rating-stars review-js-writeReviewStars">
				                        <c:forEach  begin="1" end="10" varStatus="loop">
	                           				<span class="review-js-ratingIcon glyphicon glyphicon-star ${loop.index % 2 == 0 ? 'lh' : 'fh'}"></span>
	                       				</c:forEach>
				                    </div>
				                    <input type="hidden" class="review-rating-input" name="delivery" value="${fn:escapeXml(orderReviewForm.delivery)}"/>
								</div>
							</div>
						</template:errorSpanField>
					</div>
					<div class="row">
						<template:errorSpanField path="communication">
							<div class="col-md-3 order-review-form-label">
								<label class="order-review-label">
									<spring:theme code="text.order.review.communication"/>
									<span class="skip"><form:errors path="communication"/></span>
								</label>
							</div>
							<div class="col-md-9">
								<div class="review-rating review-rating-set review-js-ratingCalcSet">
									<div class="review-rating-stars review-js-writeReviewStars">
				                        <c:forEach  begin="1" end="10" varStatus="loop">
	                           				<span class="review-js-ratingIcon glyphicon glyphicon-star ${loop.index % 2 == 0 ? 'lh' : 'fh'}"></span>
	                       				</c:forEach>
				                    </div>
				                    <input type="hidden" class="review-rating-input" name="communication" value="${fn:escapeXml(orderReviewForm.communication)}"/>
								</div>
							</div>
						</template:errorSpanField>
					</div>
					<div class="row">
							<template:errorSpanField path="comment">
								<div class="col-md-3 order-review-form-label">
									<label class="order-review-label">
										<spring:theme code="text.order.review.comment"/>
										<span class="skip"><form:errors path="comment"/></span>
									</label>
								</div>
								<div class="col-md-9">
									<textarea name="comment" class="form-control order-review-textarea" rows="5">${fn:escapeXml(orderReviewForm.comment)}</textarea>
								</div>
							</template:errorSpanField>
					</div>
				</div>
				
				<div class="row">
					<div class="col-xs-12 col-sm-8 col-md-8 col-lg-9"></div>
					<div class="col-xs-12 col-sm-4 col-md-4 col-lg-3">
						<button id="review-submit-btn" type="submit" class="btn btn-primary btn-block">
							<spring:theme code="text.order.review.submit" text="Submit"/>
						</button>
					</div>
				</div>
			</form:form>
		</div>
	</div>
</div>
    
</template:page>
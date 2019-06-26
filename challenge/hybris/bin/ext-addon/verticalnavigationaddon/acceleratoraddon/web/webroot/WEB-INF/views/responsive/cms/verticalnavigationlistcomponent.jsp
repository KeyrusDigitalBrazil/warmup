<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<spring:htmlEscape defaultHtmlEscape="true" />

<style type="text/css">div.nav-bottom{display:none}</style>
<c:choose>
  	<c:when test="${empty component.wrapAfter}">
          <c:set var="extendThreshold" value="5" />
     </c:when>
     <c:otherwise>
          <c:set var="extendThreshold" value="${component.wrapAfter}" />
     </c:otherwise>
</c:choose>
<div id="vertical_navigation_bar">
    <c:choose>
      	<c:when test="${fn:length(component.navigationNode.children) > extendThreshold }">
              <ul id="banner_menu_wrap" class="make-ul-fill-height">
         </c:when>
         <c:otherwise>
               <ul id="banner_menu_wrap">
         </c:otherwise>
    </c:choose>
		<c:forEach var="navNode" items="${component.navigationNode.children}">
		 	<li>
		 		<spring:url var="navNodeEntryUrl" value="${navNode.entries[0].item.url}" htmlEscape="false" />
				<a href="${fn:escapeXml(navNodeEntryUrl)}">
					<c:choose>
						<c:when test="${empty navNode.title}">
				            <c:out value="${navNode.entries[0].item.linkName}"/>
						</c:when>
						<c:otherwise>
							<c:out value="${navNode.title}" />
						</c:otherwise>
					</c:choose>
				</a>
				<div class="banner_menu_content">
					<ul>
						<c:forEach var="subNavNode" items="${navNode.children}">
							<c:choose>
								<c:when test="${not empty subNavNode.children}">
									<c:forEach items="${subNavNode.children}" var="subNavNodeChild">
										<c:forEach items="${subNavNodeChild.entries}" var="subNavNodeEntry">
											<c:set var="subNavNodeLink" value="${subNavNodeEntry.item}" />
											<c:set var="isThumbnailOnly" value="${subNavNodeLink.thumbnailOnly}" />
											<li <c:if test="${isThumbnailOnly}">class="thumbnail_only"</c:if>>
												<spring:url var="subNavNodeLinkUrl" value="${subNavNodeLink.url}" htmlEscape="false" />
												<a href="${fn:escapeXml(subNavNodeLinkUrl)}" title="${fn:escapeXml(subNavNodeLink.linkName)}">
													<c:choose>
														<c:when test="${empty subNavNodeLink.thumbnail.downloadURL}">
															<c:out value="${subNavNodeLink.linkName}" />
														</c:when>
														<c:otherwise>
															<img alt="${fn:escapeXml(subNavNodeLink.linkName)}" src="${fn:escapeXml(subNavNodeLink.thumbnail.downloadURL)}" />
															<c:if test="${not isThumbnailOnly}">
																<c:out value="${subNavNodeLink.linkName}" />
															</c:if>
														</c:otherwise>
													</c:choose>
												</a>
											</li>
										</c:forEach>
									</c:forEach>
								</c:when>
								<c:when test="${not empty subNavNode.entries}">
									<c:forEach items="${subNavNode.entries}" var="subNavNodeEntry">
										<c:set var="subNavNodeLink" value="${subNavNodeEntry.item}" />
										<c:set var="isThumbnailOnly" value="${subNavNodeLink.thumbnailOnly}" />
										<li <c:if test="${isThumbnailOnly}">class="thumbnail_only"</c:if>>
											<spring:url var="subNavNodeLinkUrl" value="${subNavNodeLink.url}" htmlEscape="false" />
											<a href="${fn:escapeXml(subNavNodeLinkUrl)}" title="${fn:escapeXml(subNavNodeLink.linkName)}">
												<c:choose>
													<c:when test="${empty subNavNodeLink.thumbnail.downloadURL}">
														<c:out value="${subNavNodeLink.linkName}" />
													</c:when>
													<c:otherwise>
														<img alt="${fn:escapeXml(subNavNodeLink.linkName)}" src="${fn:escapeXml(subNavNodeLink.thumbnail.downloadURL)}" />
														<c:if test="${not isThumbnailOnly}">
															<c:out value="${subNavNodeLink.linkName}" />
														</c:if>
													</c:otherwise>
												</c:choose>
											</a>
										</li>
									</c:forEach>
								</c:when>
							</c:choose>
						</c:forEach>
					</ul>
				</div>
			</li>
		</c:forEach>
	</ul>
</div>
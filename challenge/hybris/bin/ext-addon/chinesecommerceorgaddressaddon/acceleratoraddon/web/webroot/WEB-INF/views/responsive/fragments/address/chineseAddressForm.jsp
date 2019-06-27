<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<spring:htmlEscape defaultHtmlEscape="true" />

<c:if test="${not empty unitdata}">
                    <c:forEach items="${unitdata.addresses}" var="address">
                        <div class="col-xs-12 col-sm-6 col-md-4 card">
                            <spring:url value="/my-company/organization-management/manage-units/edit-address/" var="editUnitAddressUrl" htmlEscape="false">
                                <spring:param name="unit" value="${unitdata.uid}"/>
                                <spring:param name="addressId" value="${address.id}"/>
                            </spring:url>
                            <spring:url value="/my-company/organization-management/manage-units/edit-address/" var="chineseAddressEditUnitAddressUrl" htmlEscape="false">
                                <spring:param name="unit" value="${unitdata.uid}"/>
                                <spring:param name="addressId" value="${address.id}"/>
                                <spring:param name="countryIso" value="CN"/>
                            </spring:url>
                            <spring:url value="/my-company/organization-management/manage-units/remove-address/" var="removeUnitAddressUrl" htmlEscape="false">
                                <spring:param name="unit" value="${unitdata.uid}"/>
                                <spring:param name="addressId" value="${address.id}"/>
                            </spring:url>
		                     <ul class="pull-left">
								<c:choose>
									<c:when test="${fn:toUpperCase(currentLanguage.getIsocode()) eq 'ZH'}">
										<li>${fn:escapeXml(address.country.name)}&nbsp;
											<c:if test="${not empty address.region.name}">
												${fn:escapeXml(address.region.name)}
											</c:if></li>
										<li><c:if test="${not empty address.city.name}">
												${fn:escapeXml(address.city.name)}&nbsp;
											</c:if>
											<c:if test="${empty address.city.name}">
												${fn:escapeXml(address.town)}&nbsp;
											</c:if>
											<c:if test="${not empty address.district.name}">
												&nbsp;${fn:escapeXml(address.district.name)}
											</c:if></li>
										<li>${fn:escapeXml(address.line1)}</li>
										<c:if test="${not empty address.line2}">
											<li>${fn:escapeXml(address.line2)}</li>
										</c:if>
										<c:if test="${not empty address.postalCode}">
											<li>${fn:escapeXml(address.postalCode)}</li>
										</c:if>
										<c:if test="${not empty address.cellphone}">
											<li>${fn:escapeXml(address.cellphone)}</li>
										</c:if>
										<c:if test="${not empty address.phone}">
											<li>${fn:escapeXml(address.phone)}</li>
										</c:if>
									</c:when>
									<c:otherwise>
										<li>${fn:escapeXml(address.line1)}</li>
										<c:if test="${not empty address.line2}">
											<li>${fn:escapeXml(address.line2)}</li>
										</c:if>
										<li><c:if test="${not empty address.district.name}">
												${fn:escapeXml(address.district.name)}&nbsp;
											</c:if>
											<c:if test="${not empty address.city.name}">
												${fn:escapeXml(address.city.name)}&nbsp;
											</c:if>
											<c:if test="${empty address.city.name}">
												${fn:escapeXml(address.town)}&nbsp;
											</c:if></li>
										<li><c:if test="${not empty address.region.name}">
												${fn:escapeXml(address.region.name)}&nbsp;
											</c:if>
											${fn:escapeXml(address.country.name)}
										</li>
										<c:if test="${not empty address.postalCode}">
											<li>${fn:escapeXml(address.postalCode)}</li>
										</c:if>
										<c:if test="${not empty address.cellphone}">
											<li>${fn:escapeXml(address.cellphone)}</li>
										</c:if>
										<c:if test="${not empty address.phone}">
											<li>${fn:escapeXml(address.phone)}</li>
										</c:if>
									</c:otherwise>
								</c:choose>
					        </ul>
                            <div class="account-cards-actions pull-left">
                            	<c:choose>
                            		<c:when test="${fn:toUpperCase(address.country.isocode) eq 'CN'}">
	                            		<a href="${fn:escapeXml(chineseAddressEditUnitAddressUrl)}" class="edit-item" title="<spring:theme code='text.company.manage.units.edit'/>">
	                                    	<span class="glyphicon glyphicon-pencil"></span>
	                               		</a>
                            		</c:when>
                            		<c:otherwise>
	                            		<a href="${fn:escapeXml(editUnitAddressUrl)}" class="edit-item" title="<spring:theme code='text.company.manage.units.edit'/>">
	                                    	<span class="glyphicon glyphicon-pencil"></span>
	                               		</a>
                            		</c:otherwise>
                            	</c:choose>
                                
                                <span class="js-action-confirmation-modal">
                                    <a href="${fn:escapeXml(removeUnitAddressUrl)}" class="remove-item" title="<spring:theme code='text.company.manage.units.remove'/>"
                                       data-action-confirmation-modal-title="<spring:theme code='text.company.manage.units.delete.address'/>"
                                       data-action-confirmation-modal-id="delete-address">
                                        <span class="glyphicon glyphicon-remove"></span>
                                    </a>
                                </span>
                            </div>
                        </div>
                    </c:forEach>
</c:if>

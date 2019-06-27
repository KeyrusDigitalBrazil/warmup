<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<spring:htmlEscape defaultHtmlEscape="true" />
<c:set var="noBorder" value=""/>
<c:if test="${not empty addressData}">
    <c:set var="noBorder" value="no-border"/>
</c:if>

<div class="account-section-header ${fn:escapeXml(noBorder)}">
	<spring:theme code="text.account.addressBook" />

	<ycommerce:testId code="addressBook_addNewAddress_button">
		<div class="account-section-header-add pull-right">
			<a href="add-address"> <spring:theme
					code="text.account.addressBook.addAddress" />
			</a>
		</div>
	</ycommerce:testId>
</div>

<div class="account-addressbook account-list">
    <c:if test="${empty addressData}">
        <div class="account-section-content content-empty">
            <spring:theme code="text.account.addressBook.noSavedAddresses" />
        </div>
    </c:if>
    <c:if test="${not empty addressData}">
	    <div class="account-cards card-select">
	    	<div class="row">
			    <c:forEach items="${addressData}" var="address">
			        <div class="card col-xs-12 col-sm-6 col-md-4">
				        <ul class="pull-left">
				            <li>
							 	<strong>${fn:escapeXml(address.fullnameWithTitle)}&nbsp;
								 	<c:if test="${address.defaultAddress}">
								 		(<spring:theme code="text.default" text="Default" />)
								 	</c:if>
							 	</strong>
				        	</li>
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

				        <c:if test="${not address.defaultAddress}">
				        	<spring:url var="setDefaultAddressUrl" value="set-default-address/{/addressId}" htmlEscape="false">
                            	<spring:param name="addressId" value="${address.id}" />
                            </spring:url>
				            <ycommerce:testId code="addressBook_isDefault_button">
				                <a class="account-set-default-address" href="${fn:escapeXml(setDefaultAddressUrl)}">
				                	<spring:theme code="text.setDefault"/>
				                </a>
				            </ycommerce:testId>
				        </c:if>
				        <div class="account-cards-actions pull-left">
				        	<spring:url var="editAddressUrl" value="edit-address/{/addressId}" htmlEscape="false">
                            	<spring:param name="addressId" value="${address.id}" />
                            </spring:url>
							<ycommerce:testId code="addressBook_editAddress_button">
								<a class="action-links" href="${fn:escapeXml(editAddressUrl)}">
									<span class="glyphicon glyphicon-pencil"></span>
								</a>
							</ycommerce:testId>
							<ycommerce:testId code="addressBook_removeAddress_button">
								<a href="#" class="action-links removeAddressFromBookButton" data-address-id="${fn:escapeXml(address.id)}" data-popup-title="<spring:theme code="text.address.delete.popup.title" />">
									<span class="glyphicon glyphicon-remove"></span>
								</a>
							</ycommerce:testId>
						</div>
			        </div>
			    </c:forEach>
		    </div>
		    <c:forEach items="${addressData}" var="address">
		        <div class="display-none">
		       	 	<div id="popup_confirm_address_removal_${fn:escapeXml(address.id)}" class="account-address-removal-popup">
		        		<div class="addressItem">
		        			<spring:theme code="text.address.remove.following" />
		       				
		       				<div class="address">
						        <strong>${fn:escapeXml(address.fullnameWithTitle)}&nbsp;</strong>
							<c:choose>
								<c:when test="${fn:toUpperCase(currentLanguage.getIsocode()) eq 'ZH'}">
									<br>${fn:escapeXml(address.country.name)}&nbsp;
										<c:if test="${not empty address.region.name}">
											${fn:escapeXml(address.region.name)}
										</c:if>
									<br><c:if test="${not empty address.city.name}">
											${fn:escapeXml(address.city.name)}&nbsp;
										</c:if>
										<c:if test="${empty address.city.name}">
											${fn:escapeXml(address.town)}&nbsp;
										</c:if>
										<c:if test="${not empty address.district.name}">
											&nbsp;${fn:escapeXml(address.district.name)}
										</c:if>
							        <br>${fn:escapeXml(address.line1)}
							        <c:if test="${not empty address.line2}">
							            <br>${fn:escapeXml(address.line2)}
							        </c:if>
							        <c:if test="${not empty address.postalCode}">
							            <br>${fn:escapeXml(address.postalCode)}
							        </c:if>
								    <c:if test="${not empty address.cellphone}">
							            <br>${fn:escapeXml(address.cellphone)}
							        </c:if>
								    <c:if test="${not empty address.phone}">
							            <br>${fn:escapeXml(address.phone)}
							        </c:if>
								</c:when>
								<c:otherwise>
							        <br>${fn:escapeXml(address.line1)}
							        <c:if test="${not empty address.line2}">
							            <br>${fn:escapeXml(address.line2)}
							        </c:if>
							        <br><c:if test="${not empty address.district.name}">
											${fn:escapeXml(address.district.name)}&nbsp;
										</c:if>
										<c:if test="${not empty address.city.name}">
											${fn:escapeXml(address.city.name)}&nbsp;
										</c:if>
										<c:if test="${empty address.city.name}">
											${fn:escapeXml(address.town)}&nbsp;
										</c:if>
									<br><c:if test="${not empty address.region.name}">
											${fn:escapeXml(address.region.name)}&nbsp;
										</c:if>
							        	${fn:escapeXml(address.country.name)}
							        <c:if test="${not empty address.postalCode}">
							            <br>${fn:escapeXml(address.postalCode)}
							        </c:if>
								    <c:if test="${not empty address.cellphone}">
							            <br>${fn:escapeXml(address.cellphone)}
							        </c:if>
								    <c:if test="${not empty address.phone}">
							            <br>${fn:escapeXml(address.phone)}
							        </c:if>
								</c:otherwise>
							</c:choose>
		       				</div>
					        
					        <div class="modal-actions">
                                <div class="row">
						        	<spring:url var="removeAddressUrl" value="remove-address/{/addressId}" htmlEscape="false">
		                            	<spring:param name="addressId" value="${address.id}" />
		                            </spring:url>
                                    <ycommerce:testId code="addressRemove_delete_button">
                                        <div class="col-xs-12 col-sm-6 col-sm-push-6">
                                            <a class="btn btn-primary btn-block" data-address-id="${fn:escapeXml(address.id)}" href="${fn:escapeXml(removeAddressUrl)}">
                                                <spring:theme code="text.address.delete" />
                                            </a>
                                        </div>
                                    </ycommerce:testId>
                                    <div class="col-xs-12 col-sm-6 col-sm-pull-6">
                                        <a class="btn btn-default btn-block closeColorBox" data-address-id="${fn:escapeXml(address.id)}">
                                            <spring:theme code="text.button.cancel"/>
                                        </a>
                                    </div>
					       	    </div>
					       	</div>
		        		</div>
		        	</div>
		        </div>
			</c:forEach>
	    </div>
    </c:if>
</div>

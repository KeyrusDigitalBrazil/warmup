<%@ attribute name="regions" required="false" type="java.util.List"%>
<%@ attribute name="country" required="false" type="java.lang.String"%>
<%@ attribute name="tabIndex" required="false" type="java.lang.Integer"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<c:choose>
	<c:when test="${country == 'CN'}">  
        		 	<div class="col-xs-12 col-sm-6">
                        <formElement:formSelectBox idKey="address.title" labelKey="address.title" path="titleCode"
                                                mandatory="true" skipBlank="false" selectCSSClass="form-control"
                                                skipBlankMessageKey="address.title.pleaseSelect" items="${titleData}"
                                                selectedValue="${addressForm.titleCode}"/>
                    </div>
                    <div class="col-xs-12 col-sm-6">
                    	<formElement:formInputBox idKey="address.fullname" labelKey="address.fullname" path="fullname" 
                    	inputCSS="form-control" mandatory="true" />           	
                    </div>
                    <div class="col-xs-12 col-sm-6">
                    	<formElement:formSelectBox idKey="unitaddress.region" labelKey="address.province" path="regionIso"
                    							mandatory="true" skipBlank="false" skipBlankMessageKey="address.selectProvince" 
                    							items="${regions}" itemValue="${useShortRegionIso ? 'isocodeShort' : 'isocode'}" 
                    							selectedValue="${addressForm.regionIso}" selectCSSClass="form-control"/>                   	
                    </div>
                    <div class="col-xs-12 col-sm-6">
                    	<formElement:formSelectBox idKey="unitaddress.townCity" labelKey="address.townCity" path="cityIso" 
                    							mandatory="true" skipBlank="false" skipBlankMessageKey="address.selectCity" items="${cities}" 
                    							itemValue="${'code'}" selectedValue="${addressForm.cityIso}" selectCSSClass="form-control"/>	
                    </div>
                    <div class="col-xs-12 col-sm-6">
                    	<formElement:formSelectBox idKey="unitaddress.district" labelKey="address.district" path="districtIso" 
                    							mandatory="true" skipBlank="false" skipBlankMessageKey="address.selectDistrict" items="${districts}" 
                    							itemValue="${'code'}" selectedValue="${addressForm.districtIso}" selectCSSClass="form-control"/>                  	
                    </div>
               		<div class="col-xs-12 col-sm-6">
                        <formElement:formInputBox idKey="address.line1" labelKey="address.line1" path="line1"
                                                inputCSS="text" mandatory="true"/>
                    </div>
                    <div class="col-xs-12 col-sm-6">
                        <formElement:formInputBox idKey="address.line2" labelKey="address.line2" path="line2"
                                                inputCSS="text" mandatory="false"/>
                    </div>
                    <div class="col-xs-12 col-sm-6">
                        <formElement:formInputBox idKey="address.postcode" labelKey="address.postcode" path="postcode"
                                                inputCSS="text" mandatory="false"/>
                    </div>
                    <div class="col-xs-12 col-sm-6">
                   	    <formElement:formInputBox idKey="address.cellphone" labelKey="address.cellphone" path="cellphone" 
                   	    						inputCSS="form-control" mandatory="true" />                   		
                    </div>
                    <div class="col-xs-12 col-sm-6">
                   		<formElement:formInputBox idKey="address.phone" labelKey="address.phone" path="phone" 
                   								inputCSS="form-control" mandatory="false" />                  		
                    </div>
	</c:when>
	<c:otherwise>
		 			<div class="col-xs-12 col-sm-6">
                        <formElement:formSelectBox idKey="address.title" labelKey="address.title" path="titleCode"
                                                mandatory="true" skipBlank="false" selectCSSClass="form-control"
                                                skipBlankMessageKey="address.title.pleaseSelect" items="${titleData}"
                                                selectedValue="${addressForm.titleCode}"/>
                    </div>
                    <div class="col-xs-12 col-sm-6">
                        <formElement:formInputBox idKey="address.firstName" labelKey="address.firstName" path="firstName"
                                                inputCSS="text" mandatory="true"/>
                    </div>
                    <div class="col-xs-12 col-sm-6">
                        <formElement:formInputBox idKey="address.surname" labelKey="address.surname" path="lastName"
                                                inputCSS="text" mandatory="true"/>
                    </div>
                    <div class="col-xs-12 col-sm-6">
                        <formElement:formInputBox idKey="address.line1" labelKey="address.line1" path="line1"
                                                inputCSS="text" mandatory="true"/>
                    </div>
                    <div class="col-xs-12 col-sm-6">
                        <formElement:formInputBox idKey="address.line2" labelKey="address.line2" path="line2"
                                                inputCSS="text" mandatory="false"/>
                    </div>
                    <div class="col-xs-12 col-sm-6">
                        <formElement:formInputBox idKey="address.townCity" labelKey="address.townCity" path="townCity"
                                                inputCSS="text" mandatory="true"/>
                    </div>
                    <div class="col-xs-12 col-sm-6">
                        <formElement:formInputBox idKey="address.postcode" labelKey="address.postcode" path="postcode"
                                                inputCSS="text" mandatory="true"/>
                    </div>
	</c:otherwise>
</c:choose>



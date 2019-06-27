/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.saprevenuecloudorder.pojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BillItem
{
	
	private String id;
    private String subscriptionId;
    private Integer subscriptionDocumentNumber;
    private String subscriptionItemId;
    private String productId;
    private String productCode;
    private String ratePlanId;
    private Object groupingElement ;
    private Boolean createInvoice;
    private Integer totalAmount ;
    private List<Object> customReferences;
    private List<Charge> charges;

    public List<Charge> getCharges() {
		return charges;
	}
	public void setCharges(List<Charge> charges) {
		this.charges = charges;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSubscriptionId() {
		return subscriptionId;
	}
	public void setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
	}
	public Integer getSubscriptionDocumentNumber() {
		return subscriptionDocumentNumber;
	}
	public void setSubscriptionDocumentNumber(Integer subscriptionDocumentNumber) {
		this.subscriptionDocumentNumber = subscriptionDocumentNumber;
	}
	public String getSubscriptionItemId() {
		return subscriptionItemId;
	}
	public void setSubscriptionItemId(String subscriptionItemId) {
		this.subscriptionItemId = subscriptionItemId;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getRatePlanId() {
		return ratePlanId;
	}
	public void setRatePlanId(String ratePlanId) {
		this.ratePlanId = ratePlanId;
	}
	public Object getGroupingElement() {
		return groupingElement;
	}
	public void setGroupingElement(Object groupingElement) {
		this.groupingElement = groupingElement;
	}
	public Boolean getCreateInvoice() {
		return createInvoice;
	}
	public void setCreateInvoice(Boolean createInvoice) {
		this.createInvoice = createInvoice;
	}
	public Integer getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(Integer totalAmount) {
		this.totalAmount = totalAmount;
	}
	public List<Object> getCustomReferences() {
		return customReferences;
	}
	public void setCustomReferences(List<Object> customReferences) {
		this.customReferences = customReferences;
	}

}

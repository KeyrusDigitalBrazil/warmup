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
public class Bills 
{
    private String id;
    private Integer documentNumber;
    private Boolean createInvoice;
    private String createdAt;
    private Object createdBy;
    private String billingDate;
    private Integer totalAmount;
    private String customerId;
    private String customerType;
    private String marketId;
    private String marketTimezone;
    private Object splitElement;
    private String currency;
    private String paymentMethod;
    private Object paymentCardToken;
    private List<Object> successorDocuments;
    private List<BillItem> billItems;
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getDocumentNumber() {
		return documentNumber;
	}
	public void setDocumentNumber(Integer documentNumber) {
		this.documentNumber = documentNumber;
	}
	public Boolean getCreateInvoice() {
		return createInvoice;
	}
	public void setCreateInvoice(Boolean createInvoice) {
		this.createInvoice = createInvoice;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public Object getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(Object createdBy) {
		this.createdBy = createdBy;
	}
	public String getBillingDate() {
		return billingDate;
	}
	public void setBillingDate(String billingDate) {
		this.billingDate = billingDate;
	}
	public Integer getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(Integer totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getCustomerType() {
		return customerType;
	}
	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}
	public String getMarketId() {
		return marketId;
	}
	public void setMarketId(String marketId) {
		this.marketId = marketId;
	}
	public String getMarketTimezone() {
		return marketTimezone;
	}
	public void setMarketTimezone(String marketTimezone) {
		this.marketTimezone = marketTimezone;
	}
	public Object getSplitElement() {
		return splitElement;
	}
	public void setSplitElement(Object splitElement) {
		this.splitElement = splitElement;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	public Object getPaymentCardToken() {
		return paymentCardToken;
	}
	public void setPaymentCardToken(Object paymentCardToken) {
		this.paymentCardToken = paymentCardToken;
	}
	public List<Object> getSuccessorDocuments() {
		return successorDocuments;
	}
	public void setSuccessorDocuments(List<Object> successorDocuments) {
		this.successorDocuments = successorDocuments;
	}
	public List<BillItem> getBillItems() {
		return billItems;
	}
	public void setBillItems(List<BillItem> billItems) {
		this.billItems = billItems;
	}
}
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

@JsonIgnoreProperties(ignoreUnknown = true)
public class Item {

    private String itemId;
    private String parentItemId;
    private String subscriptionType;
    private boolean createBill;
    private boolean createInvoice;
    private boolean createRating;

    private BusinessPartner provider;
    private Product product;
    private RatePlan ratePlan;
    private List<TechnicalResource> technicalResources;


    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getParentItemId() {
        return parentItemId;
    }

    public void setParentItemId(String parentItemId) {
        this.parentItemId = parentItemId;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public boolean isCreateBill() {
        return createBill;
    }

    public void setCreateBill(boolean createBill) {
        this.createBill = createBill;
    }

    public boolean isCreateInvoice() {
        return createInvoice;
    }

    public void setCreateInvoice(boolean createInvoice) {
        this.createInvoice = createInvoice;
    }

    public boolean isCreateRating() {
        return createRating;
    }

    public void setCreateRating(boolean createRating) {
        this.createRating = createRating;
    }

    public BusinessPartner getProvider() {
        return provider;
    }

    public void setProvider(BusinessPartner provider) {
        this.provider = provider;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public RatePlan getRatePlan() {
        return ratePlan;
    }

    public void setRatePlan(RatePlan ratePlan) {
        this.ratePlan = ratePlan;
    }

    public List<TechnicalResource> getTechnicalResources() {
        return technicalResources;
    }

    public void setTechnicalResources(List<TechnicalResource> technicalResources) {
        this.technicalResources = technicalResources;
    }

    @Override
    public String toString() {
        return "Item{" +
                "itemId='" + itemId + '\'' +
                ", parentItemId='" + parentItemId + '\'' +
                ", subscriptionType='" + subscriptionType + '\'' +
                ", createBill=" + createBill +
                ", createInvoice=" + createInvoice +
                ", createRating=" + createRating +
                ", provider=" + provider +
                ", product=" + product +
                ", ratePlan=" + ratePlan +
                ", technicalResources=" + technicalResources +
                '}';
    }
}
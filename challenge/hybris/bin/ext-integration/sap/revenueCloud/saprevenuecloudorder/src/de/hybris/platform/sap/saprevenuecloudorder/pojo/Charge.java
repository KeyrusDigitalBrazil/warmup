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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Charge 
{
    private String metricId;
    private String blockSize;
    private IncludedQuantity includedQuantity;
    private ConsumedQuantity consumedQuantity;
	private String amount;
    private String tierFrom;
    private String tierTo;
    private RatingPeriod ratingPeriod;

    public String getMetricId() {
		return metricId;
	}
	public void setMetricId(String metricId) {
		this.metricId = metricId;
	}
	public String getBlockSize() {
		return blockSize;
	}
	public void setBlockSize(String blockSize) {
		this.blockSize = blockSize;
	}
	public IncludedQuantity getIncludedQuantity() {
		return includedQuantity;
	}
	public void setIncludedQuantity(IncludedQuantity includedQuantity) {
		this.includedQuantity = includedQuantity;
	}
	public ConsumedQuantity getConsumedQuantity() {
		return consumedQuantity;
	}
	public void setConsumedQuantity(ConsumedQuantity consumedQuantity) {
		this.consumedQuantity = consumedQuantity;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getTierFrom() {
		return tierFrom;
	}
	public void setTierFrom(String tierFrom) {
		this.tierFrom = tierFrom;
	}
	public String getTierTo() {
		return tierTo;
	}
	public void setTierTo(String tierTo) {
		this.tierTo = tierTo;
	}
	public RatingPeriod getRatingPeriod() {
		return ratingPeriod;
	}
	public void setRatingPeriod(RatingPeriod ratingPeriod) {
		this.ratingPeriod = ratingPeriod;
	}

}

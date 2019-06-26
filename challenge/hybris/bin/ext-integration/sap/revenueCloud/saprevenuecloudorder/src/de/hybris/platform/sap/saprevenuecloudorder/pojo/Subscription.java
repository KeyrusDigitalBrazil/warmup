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
public class Subscription
{

	private String subscriptionId;
	private String validFrom;
	private String validUntil;
	private String createdAt;
	private String changedAt;
	private Customer customer;
	private Market market;
	private List<Snapshot> snapshots;
	private MetaData metaData;
	private String documentNumber;
	private String cancellationReason;
	private String effectiveExpirationDate;

	public String getEffectiveExpirationDate() {
		return effectiveExpirationDate;
	}

	public void setEffectiveExpirationDate(String effectiveExpirationDate) {
		this.effectiveExpirationDate = effectiveExpirationDate;
	}

	public String getCancellationReason()
	{
		return cancellationReason;
	}

	public void setCancellationReason(final String cancellationReason)
	{
		this.cancellationReason = cancellationReason;
	}

	public String getDocumentNumber()
	{
		return documentNumber;
	}

	public void setDocumentNumber(final String documentNumber)
	{
		this.documentNumber = documentNumber;
	}

	public MetaData getMetaData()
	{
		return metaData;
	}

	public void setMetaData(final MetaData metaData)
	{
		this.metaData = metaData;
	}

	public String getValidFrom()
	{
		return validFrom;
	}

	public void setValidFrom(final String validFrom)
	{
		this.validFrom = validFrom;
	}

	public String getValidUntil()
	{
		return validUntil;
	}

	public void setValidUntil(final String validUntil)
	{
		this.validUntil = validUntil;
	}

	public String getCreatedAt()
	{
		return createdAt;
	}

	public void setCreatedAt(final String createdAt)
	{
		this.createdAt = createdAt;
	}

	public String getChangedAt()
	{
		return changedAt;
	}

	public void setChangedAt(final String changedAt)
	{
		this.changedAt = changedAt;
	}

	public Customer getCustomer()
	{
		return customer;
	}

	public void setCustomer(final Customer customer)
	{
		this.customer = customer;
	}

	public Market getMarket()
	{
		return market;
	}

	public void setMarket(final Market market)
	{
		this.market = market;
	}

	public List<Snapshot> getSnapshots()
	{
		return snapshots;
	}

	public void setSnapshots(final List<Snapshot> snapshots)
	{
		this.snapshots = snapshots;
	}

	public String getSubscriptionId()
	{
		return subscriptionId;
	}

	public void setSubscriptionId(final String subscriptionId)
	{
		this.subscriptionId = subscriptionId;
	}

	@Override
	public String toString()
	{
		return "Subscription{" + "subscriptionId='" + subscriptionId + '\'' + ", validFrom='" + validFrom + '\'' + ", validUntil='"
				+ validUntil + '\'' + ", createdAt='" + createdAt + '\'' + ", changedAt='" + changedAt + '\'' + ", customer="
				+ customer + ", market=" + market + ", snapshots=" + snapshots + '}';
	}
}
/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.sap.hybris.c4c.customer.event;

import de.hybris.platform.servicelayer.event.ClusterAwareEvent;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;

import com.sap.hybris.c4c.customer.dto.C4CCustomerData;


/**
 * Definition for Customer Update Event
 */
public class SapC4cCustomerUpdateEvent extends AbstractEvent implements ClusterAwareEvent
{

	private C4CCustomerData customerData;

	@Override
	public boolean publish(final int sourceNodeId, final int targetNodeId)
	{
		return sourceNodeId == targetNodeId;
	}

	/**
	 * @return the customerData
	 */
	public C4CCustomerData getCustomerData()
	{
		return customerData;
	}

	/**
	 * @param customerData
	 *           the customerData to set
	 */
	public void setCustomerData(final C4CCustomerData customerData)
	{
		this.customerData = customerData;
	}

}

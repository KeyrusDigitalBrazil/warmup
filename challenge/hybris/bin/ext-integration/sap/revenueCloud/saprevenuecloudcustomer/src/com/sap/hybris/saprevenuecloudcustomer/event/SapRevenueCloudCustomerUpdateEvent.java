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
package com.sap.hybris.saprevenuecloudcustomer.event;

import de.hybris.platform.servicelayer.event.ClusterAwareEvent;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;

import com.sap.hybris.saprevenuecloudcustomer.dto.Customer;


/**
 *
 */
public class SapRevenueCloudCustomerUpdateEvent extends AbstractEvent implements ClusterAwareEvent
{
	Customer customerJson;

	@Override
	public boolean publish(final int sourceNodeId, final int targetNodeId)
	{
		return sourceNodeId == targetNodeId;
	}

	/**
	 * @return the customerJson
	 */
	public Customer getCustomerJson()
	{
		return customerJson;
	}

	/**
	 * @param customerJson
	 *           the customerJson to set
	 */
	public void setCustomerJson(final Customer customerJson)
	{
		this.customerJson = customerJson;
	}


}

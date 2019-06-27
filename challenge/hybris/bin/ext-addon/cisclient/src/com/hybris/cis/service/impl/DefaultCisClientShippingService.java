/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package com.hybris.cis.service.impl;

import java.net.URI;

import javax.ws.rs.core.Response.Status;

import com.hybris.cis.client.shipping.models.CisShipment;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.cis.client.shipping.ShippingClient;
import com.hybris.cis.service.CisClientShippingService;


/**
 * Default implementation for {@link CisClientShippingService}
 */
public class DefaultCisClientShippingService implements CisClientShippingService
{
	private ShippingClient shippingClient;

	@Override
	public CisShipment createShipment(final String xClientRef, final String tenantId, final CisShipment cisShipment)
	{
		return getShippingClient().createShipment(xClientRef, tenantId, cisShipment);
	}

	@Override
	public byte[] getLabel(final String xClientRef, final String tenantId, final URI labelLocation)
	{
		final String[] labelLocationPath = labelLocation.getPath().split("/");
		final String shipmentId = labelLocationPath[7];
		final String labelId = labelLocationPath[9];
		return getShippingClient().getLabel(xClientRef, tenantId, shipmentId, labelId);
	}

	@Override
	public boolean ping(final String xCisClientRef, final String tenantId)
	{
		return getShippingClient().doPing(xCisClientRef, tenantId).status().equals(Status.CREATED) ? true : false;
	}

	protected ShippingClient getShippingClient()
	{
		return shippingClient;
	}

	@Required
	public void setShippingClient(final ShippingClient shippingClient)
	{
		this.shippingClient = shippingClient;
	}
}

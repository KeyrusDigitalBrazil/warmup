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
package com.hybris.cis.service;


import com.hybris.cis.client.shipping.models.CisShipment;

import java.net.URI;


/**
 * Interface proving Shipping services.
 */
public interface CisClientShippingService extends CisClientService
{
	/**
	 * Creates a shipment.
	 *
	 * @param xClientRef
	 * 			client ref to pass in the header
	 * @param tenantId
	 * 			tenantId to pass in the header
	 * @param cisShipment
	 * 			shipment to create
	 *
	 * @return the shipment
	 */
	CisShipment createShipment(final String xClientRef, final String tenantId, final CisShipment cisShipment);

	/**
	 * Gets the shipment label.
	 *
	 * @param xClientRef
	 * 			client ref to pass in the header
	 * @param tenantId
	 * 			tenantId to pass in the header
	 * @param labelLocation
	 * 			location of the label
	 *
	 * @return the label as a stream (can be different files format, see the media type of the rest response)
	 */
	byte[] getLabel(final String xClientRef, final String tenantId, final URI labelLocation);
}

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
package com.hybris.cis.client.shipping;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.hybris.charon.annotations.Control;
import com.hybris.charon.annotations.Http;
import com.hybris.cis.client.CisClient;
import com.hybris.cis.client.shipping.models.CisShipment;


/**
 * Charon Client to the {@link com.hybris.cis.api.shipping.service.ShippingService}.
 */
@Http("shipping")
public interface ShippingClient extends CisClient
{
	/**
	 * Creates a shipment.
	 *
	 * @param xClientRef
	 *           client ref to pass in the header
	 * @param tenantId
	 *           tenantId to pass in the header
	 * @param cisShipment
	 *           shipment to create
	 *
	 * @return the shipment
	 */
	@POST
	@Produces("application/json")
	@Consumes("application/json")
	@Path("/shipments")
	@Control(retries = "3", retriesInterval = "500")
	CisShipment createShipment(@HeaderParam(value = "X-CIS-Client-ref") final String xClientRef,
							   @HeaderParam(value = "X-tenantId") final String tenantId, final CisShipment cisShipment);

	/**
	 * Gets the shipment label.
	 *
	 * @param xClientRef
	 *           client ref to pass in the header
	 * @param tenantId
	 *           tenantId to pass in the header
	 * @param shipmentId
	 *           the shipment id
	 * @param labelId
	 *           the label id
	 *
	 * @return the label as a stream (can be different files format, see the media type of the rest response)
	 */
	@GET
	@Produces("application/json")
	@Consumes("application/json")
	@Path("/shipments/{shipmentId}/labels/{labelId}")
	@Control(retries = "3", retriesInterval = "500")
	byte[] getLabel(@HeaderParam(value = "X-CIS-Client-ref") final String xClientRef,
			@HeaderParam(value = "X-tenantId") final String tenantId, @PathParam(value = "shipmentId") final String shipmentId,
			@PathParam(value = "labelId") final String labelId);
}

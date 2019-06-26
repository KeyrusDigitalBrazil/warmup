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
package com.hybris.cis.client.avs;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.hybris.charon.annotations.Control;
import com.hybris.charon.annotations.Http;
import com.hybris.cis.client.CisClient;
import com.hybris.cis.client.avs.models.AvsResult;
import com.hybris.cis.client.shared.models.CisAddress;


/**
 * Charon Client to the {@link de.hybris.platform.commerceservices.address.AddressVerificationService}.
 */
@Http("avs")
public interface AvsClient extends CisClient
{
	/**
	 * Verifies the given address
	 *
	 * @param xCisClientRef
	 *           client ref to pass in the header
	 * @param tenantId
	 *           tenantId to pass in the header
	 * @param address
	 *           address that you want to verify
	 * @return {@link AvsResult}
	 */
	@POST
	@Produces("application/json")
	@Consumes("application/json")
	@Path("/results")
	@Control(retries = "3", retriesInterval = "500")
	AvsResult verifyAddress(@HeaderParam(value = "X-CIS-Client-ref") String xCisClientRef,
							@HeaderParam(value = "X-tenantId") String tenantId, CisAddress address);

}

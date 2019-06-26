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
package com.hybris.cis.client;

import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.hybris.charon.RawResponse;


/**
 * Defines the common clients amongst different services
 */
public interface CisClient
{
	/**
	 * Checks if service is functioning and connecting with current credentials.
	 *
	 * @param xCisClientRef
	 *           client ref to pass in the header
	 * @param tenantId
	 *           tenantId to pass in the header
	 * @return flag if the service is accessible or not
	 */
	@HEAD
	@Produces("application/json")
	@Path("/ping")
	RawResponse doPing(@HeaderParam(value = "X-CIS-Client-ref") String xCisClientRef,
			@HeaderParam(value = "X-tenantId") String tenantId);
}

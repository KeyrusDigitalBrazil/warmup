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

/**
 * Base interface to check if service is accessible
 */
public interface CisClientService
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
	boolean ping(final String xCisClientRef, final String tenantId);

}

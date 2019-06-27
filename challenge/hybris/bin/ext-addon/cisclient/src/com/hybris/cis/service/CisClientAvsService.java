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

import com.hybris.cis.client.shared.exception.AbstractCisServiceException;
import com.hybris.cis.client.shared.models.CisAddress;
import com.hybris.cis.client.avs.models.AvsResult;


/**
 * Interface providing AVS services
 */
public interface CisClientAvsService extends CisClientService
{
	/**
	 * See {@link de.hybris.platform.commerceservices.address.AddressVerificationService#verifyAddress(CisAddress)}.
	 *
	 * @param xCisClientRef
	 * 		client ref to pass in the header
	 * @param tenantId
	 * 		tenantId to pass in the header
	 * @param address
	 * 		address to verify
	 * @return see {@link AvsResult}
	 * @throws AbstractCisServiceException exception
	 * @see de.hybris.platform.commerceservices.address.AddressVerificationService
	 */
	AvsResult verifyAddress(final String xCisClientRef, final String tenantId, final CisAddress address) throws AbstractCisServiceException;


}

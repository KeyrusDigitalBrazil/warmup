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
package com.hybris.cis.client.mock;

import com.hybris.cis.client.avs.AvsClient;
import com.hybris.cis.client.avs.models.AvsResult;
import com.hybris.cis.client.shared.exception.AbstractCisServiceException;
import com.hybris.cis.client.shared.exception.ServiceErrorResponseException;
import com.hybris.cis.client.shared.exception.ServiceNotAvailableException;
import com.hybris.cis.client.shared.exception.ServiceTimeoutException;
import com.hybris.cis.client.shared.exception.codes.UnknownServiceExceptionDetail;
import com.hybris.cis.client.shared.models.CisAddress;
import com.hybris.cis.client.shared.models.CisDecision;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;


/**
 * Mock implementation of {@link AvsClient}
 */
public class AvsClientMock extends SharedClientMock implements AvsClient
{

	/**
	 * The mock implementation is used for testing purposes. In case either one of the following field
	 * is missing in the CisAddress object, an {@link AvsResult} with REJECT is returned:
	 * - AddressLine1
	 * - City
	 * - Zipcode
	 * - Country
	 * <p>
	 * If the city is set to something else than "review" and the previously mentioned fields are set, an AvsResult with
	 * Accept is
	 * returned.
	 * Otherwise, an AvsResult with the decision type CisDecision.REVIEW is returned
	 * <p>
	 * <p>
	 * If AddressLine4 is set with the value of 503, a ServiceNotAvailableException is thrown.
	 * If AddressLine4 is set with the value of 502, a ServiceErrorResponseException with UnknownServiceExceptionDetail
	 * is thrown.
	 * If AddressLine4 is set with the value of 504, a ServiceTimeoutException is thrown.
	 * If AddressLine4 is set with the value of 500, a IllegalStateException is thrown.
	 *
	 * @param address
	 * 		the test address.
	 * @return AvsResult an result object containing the simulated decision.
	 */
	@Override
	public AvsResult verifyAddress(String xCisClientRef, String tenantId, CisAddress address) throws AbstractCisServiceException
	{
		try
		{
			this.checkAddressField("addressLine1", address.getAddressLine1());
			this.checkAddressField("city", address.getCity());
			this.checkAddressField("zip", address.getZipCode());
			this.checkAddressField("country", address.getCountry());
		}
		catch (final IllegalArgumentException ex)
		{
			return new AvsResult(CisDecision.REJECT);
		}

		if ("503".equals(address.getAddressLine4()))
		{
			throw new ServiceNotAvailableException(new IllegalStateException("just a test"));
		}
		else if ("502".equals(address.getAddressLine4()))
		{
			throw new ServiceErrorResponseException(new UnknownServiceExceptionDetail("Simulating server error"));
		}
		else if ("504".equals(address.getAddressLine4()))
		{
			throw new ServiceTimeoutException(new TimeoutException("Simulating server timeout")); // NOPMD
		}
		else if ("500".equals(address.getAddressLine4()))
		{
			throw new IllegalStateException("Simulating other error");
		}

		if ("review".equalsIgnoreCase(address.getCity()))
		{
			final CisAddress suggestedAddress = new CisAddress();
			suggestedAddress.setType(address.getType());
			suggestedAddress.setFirstName(address.getFirstName());
			suggestedAddress.setCountry(address.getCountry());
			suggestedAddress.setState(address.getState());
			suggestedAddress.setAddressLine1(String.format("%s corrected", address.getAddressLine1()));
			suggestedAddress.setAddressLine2(address.getAddressLine2());
			suggestedAddress.setCity(address.getCity());
			suggestedAddress.setZipCode(address.getZipCode());

			final AvsResult avsResult = new AvsResult(CisDecision.REVIEW);
			avsResult.setSuggestedAddresses(new ArrayList<CisAddress>());
			avsResult.getSuggestedAddresses().add(suggestedAddress);
			return avsResult;
		}
		else if ("reject".equals(address.getCity()))
		{
			return new AvsResult(CisDecision.REJECT);
		}
		else
		{
			return new AvsResult(CisDecision.ACCEPT);
		}
	}

	/**
	 * Checks that a specific field is not null or empty.
	 *
	 * @param name
	 * 		of the field to check
	 * @param value
	 * 		of the field to check
	 */
	private void checkAddressField(final String name, final String value)
	{
		if (value == null || value.isEmpty())
		{
			throw new IllegalArgumentException(name + " must not be blank!");
		}
	}
}

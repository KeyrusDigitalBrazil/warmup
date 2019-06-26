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

import com.hybris.charon.RawResponse;
import com.hybris.cis.client.shared.exception.ServiceErrorResponseException;
import com.hybris.cis.client.shared.exception.codes.UnknownServiceExceptionDetail;
import com.hybris.cis.client.shared.models.CisOrder;
import com.hybris.cis.client.tax.TaxClient;
import com.hybris.cis.client.tax.models.CisTaxDoc;
import com.hybris.cis.client.tax.util.MockTaxUtils;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.core.UriBuilder;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.GregorianCalendar;
import java.util.Optional;

import static org.mockito.BDDMockito.given;


/**
 * Mock implementation of {@link TaxClient}.
 */
public class TaxClientMock extends SharedClientMock implements TaxClient
{
	private static Logger LOGGER = LoggerFactory.getLogger(TaxClient.class);

	public static final String PING_FAIL = "PING_FAIL";
	private MockTaxUtils taxUtils;

	public TaxClientMock()
	{
		LOGGER.info("Using MOCK Client to simulate Tax.");
	}

	/**
	 * Simulates quoting of taxes.
	 *
	 * @param order
	 *           an example order
	 * @return a tax doc with set id and decision accept
	 */
	@Override
	public CisTaxDoc quote(@HeaderParam(value = "X-CIS-Client-ref") final String xClientRef,
						   @HeaderParam(value = "X-tenantId") final String tenantId, final CisOrder order)
	{
		LOGGER.info("Using MOCK Client - quote()");

		return getTaxUtils().getCisTaxDoc(order);
	}


	/**
	 * Simulates posting of taxes.
	 *
	 * @param order
	 *           an example order
	 * @return an accepted tax doc with the document id equaling the cart id
	 */
	@Override
	public CisTaxDoc post(@HeaderParam(value = "X-CIS-Client-ref") final String xClientRef,
			@HeaderParam(value = "X-tenantId") final String tenantId, final CisOrder order)
	{
		LOGGER.info("Using MOCK Client - post()");

		return getTaxUtils().getCisTaxDoc(order);
	}

	/**
	 * Simulates invoicing of taxes.
	 *
	 * @param order
	 *           an example order
	 * @return an accepted tax document with document id equaling cart id
	 * @throws ServiceErrorResponseException
	 *            with UnkownServiceExceptionDetail if the cart date is equals 1996\01\23
	 */
	@Override
	public CisTaxDoc invoice(@HeaderParam(value = "X-CIS-Client-ref") final String xClientRef,
			@HeaderParam(value = "X-tenantId") final String tenantId, final CisOrder order)
	{
		LOGGER.info("Using MOCK Client - invoice()");

		if (order.getDate() != null && new GregorianCalendar(1996, 1, 23).getTimeInMillis() == order.getDate().getTime())
		{
			// magic number that triggers an exception
			throw new ServiceErrorResponseException(new UnknownServiceExceptionDetail("mock test exception"));
		}


		return getTaxUtils().getCisTaxDoc(order);
	}

	/**
	 * Simulates cancellation of a tax entry.
	 *
	 * @param taxDocId
	 *           the location URI of the document
	 */
	@Override
	public RawResponse<String> cancel(@HeaderParam(value = "X-CIS-Client-ref") final String xClientRef,
			@HeaderParam(value = "X-tenantId") final String tenantId, final String taxDocId)
	{
		LOGGER.info("Using MOCK Client - cancel()");

		final RawResponse<String> cancelResponse = Mockito.mock(RawResponse.class);
		given(cancelResponse.location()).willReturn(getLocation(taxDocId));
		return cancelResponse;
	}

	/**
	 * Simulates adjustment of a tax entry.
	 *
	 * @param order
	 *           example order to be adjusted
	 * @return an accepted tax doc with doc id equaling cart id
	 */
	@Override
	public CisTaxDoc adjust(@HeaderParam(value = "X-CIS-Client-ref") final String xClientRef,
			@HeaderParam(value = "X-tenantId") final String tenantId, final String taxDocId, final CisOrder order)
	{
		LOGGER.info("Using MOCK Client - adjust()");

		return getTaxUtils().getCisTaxDoc(order);
	}

	/**
	 * Simulates check if the tax document exists
	 *
	 * @param taxDocId
	 *           the location URI of the document
	 */
	@Override
	public RawResponse<String> exists(final String xClientRef, @HeaderParam(value = "X-tenantId") final String tenantId,
			final String taxDocId)
	{
		LOGGER.info("Using MOCK Client - exists()");

		final RawResponse<String> existsResponse = Mockito.mock(RawResponse.class);
		given(existsResponse.location()).willReturn(getLocation(taxDocId));
		return existsResponse;
	}

	/**
	 * Transforms a {@link URI} into {@link URL}
	 *
	 * @param location
	 *           the {@link URI} to be transformed
	 * @return the {@link Optional}
	 */
	protected Optional<URL> getLocation(final String location)
	{
		final Optional<URL> optionalUrl = null;
		try
		{
			return Optional.of(UriBuilder.fromPath(location).build().toURL());
		}
		catch (final MalformedURLException e)//NOSONAR
		{
			LOGGER.error("Invalid location for the document");//NOSONAR
		}
		return optionalUrl;
	}

	public void setTaxUtils(final MockTaxUtils taxUtils)
	{
		this.taxUtils = taxUtils;
	}

	public MockTaxUtils getTaxUtils()
	{
		return taxUtils;
	}

}

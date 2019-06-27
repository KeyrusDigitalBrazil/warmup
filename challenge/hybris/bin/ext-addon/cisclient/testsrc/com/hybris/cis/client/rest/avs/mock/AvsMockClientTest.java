/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.cis.client.rest.avs.mock;


import com.hybris.cis.client.avs.models.AvsResult;
import com.hybris.cis.client.mock.AvsClientMock;
import com.hybris.cis.client.shared.exception.ServiceErrorResponseException;
import com.hybris.cis.client.shared.exception.ServiceNotAvailableException;
import com.hybris.cis.client.shared.exception.ServiceTimeoutException;
import com.hybris.cis.client.shared.models.CisAddress;
import com.hybris.cis.client.shared.models.CisDecision;
import de.hybris.platform.servicelayer.ServicelayerTest;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static junit.framework.Assert.assertEquals;


/**
 * Validates that the "out-of-the-box" spring configuration will wire in the mock client if mock mode is set.
 */
public class AvsMockClientTest extends ServicelayerTest
{
	private static final String CLIENT_ID = "TEST-ID";
	private static final String TENANT_ID = "single";

	private AvsClientMock avsClientMock;

	@Before
	public void setup()
	{
		avsClientMock = new AvsClientMock();
	}

	@Test
	public void shouldPingSuccess()
	{
		assertEquals(Response.Status.CREATED, avsClientMock.doPing(CLIENT_ID, TENANT_ID).status());
	}

	@Test
	public void shouldPingFail()
	{
		assertEquals(Response.Status.FORBIDDEN, avsClientMock.doPing(AvsClientMock.PING_FAIL, TENANT_ID).status());
	}

	@Test
	public void shouldAcceptAddress()
	{
		final CisAddress address = new CisAddress("1700 Broadway  Fl 26", "10019", "New York", "NY", "US");
		final AvsResult avsResult = avsClientMock.verifyAddress(CLIENT_ID, TENANT_ID, address);
		assertEquals(CisDecision.ACCEPT, avsResult.getDecision());
	}

	@Test
	public void shouldRejectAddress()
	{
		final CisAddress address = new CisAddress("1700 Broadway  Fl 26", "10019", "reject", "NY", "US");
		final AvsResult avsResult = avsClientMock.verifyAddress(CLIENT_ID, TENANT_ID, address);
		assertEquals(CisDecision.REJECT, avsResult.getDecision());
	}

	@Test
	public void shouldReviewAddress()
	{
		final CisAddress address = new CisAddress("1700 Broadway  Fl 26", "10019", "review", "NY", "US");
		final AvsResult avsResult = avsClientMock.verifyAddress(CLIENT_ID, TENANT_ID, address);
		assertEquals(CisDecision.REVIEW, avsResult.getDecision());
	}

	@Test
	public void shouldRejectAddressFiedNull()
	{
		final CisAddress address = new CisAddress("1700 Broadway  Fl 26", "10019", null, "NY", "US");
		final AvsResult avsResult = avsClientMock.verifyAddress(CLIENT_ID, TENANT_ID, address);
		assertEquals(CisDecision.REJECT, avsResult.getDecision());
	}

	@Test
	public void shouldRejectAddressFiedEmpty()
	{
		final CisAddress address = new CisAddress("", "10019", "New York", "NY", "US");
		final AvsResult avsResult = avsClientMock.verifyAddress(CLIENT_ID, TENANT_ID, address);
		assertEquals(CisDecision.REJECT, avsResult.getDecision());
	}

	@Test(expected = ServiceNotAvailableException.class)
	public void shouldFindServiceNotAvailable()
	{
		final CisAddress address = new CisAddress("1700 Broadway  Fl 26", "10019", "New York", "NY", "US");
		address.setAddressLine4("503");
		avsClientMock.verifyAddress(CLIENT_ID, TENANT_ID, address);
	}

	@Test(expected = ServiceErrorResponseException.class)
	public void shouldFindServiceError()
	{
		final CisAddress address = new CisAddress("1700 Broadway  Fl 26", "10019", "New York", "NY", "US");
		address.setAddressLine4("502");
		avsClientMock.verifyAddress(CLIENT_ID, TENANT_ID, address);
	}

	@Test(expected = ServiceTimeoutException.class)
	public void shouldFindServiceTimeout()
	{
		final CisAddress address = new CisAddress("1700 Broadway  Fl 26", "10019", "New York", "NY", "US");
		address.setAddressLine4("504");
		avsClientMock.verifyAddress(CLIENT_ID, TENANT_ID, address);
	}

	@Test(expected = IllegalStateException.class)
	public void shouldFindIllegalState()
	{
		final CisAddress address = new CisAddress("1700 Broadway  Fl 26", "10019", "New York", "NY", "US");
		address.setAddressLine4("500");
		avsClientMock.verifyAddress(CLIENT_ID, TENANT_ID, address);
	}

}

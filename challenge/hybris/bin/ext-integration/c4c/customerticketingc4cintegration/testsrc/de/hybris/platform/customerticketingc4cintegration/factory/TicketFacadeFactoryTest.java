/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.customerticketingc4cintegration.factory;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.customerticketingc4cintegration.facade.C4CTicketFacadeImpl;
import de.hybris.platform.customerticketingc4cintegration.facade.C4CTicketFacadeMock;
import de.hybris.platform.customerticketingfacades.TicketFacade;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.util.Config;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Test cases for TicketFacadeFactory class.
 */
@IntegrationTest
public class TicketFacadeFactoryTest extends ServicelayerTest
{
	@Mock
	private C4CTicketFacadeImpl c4cTicketFacadeImpl;
	@Mock
	private C4CTicketFacadeMock c4cTicketFacadeMock;
	@InjectMocks
	private TicketFacadeFactory facadeFactory;
	private static final String C4C_INTEGRATION_FACADE_MOCK_FLAG = "customerticketingc4cintegration.facade.mock";

	/**
	 * Setup.
	 */
	@Before
	public void setup()
	{
		facadeFactory = new TicketFacadeFactory();
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Should return non-mock facade implantation if mock flag set to false.
	 *
	 * @throws Exception
	 */
	@Test
	public void shouldReturnFacadeImplWhenMockIsFalse() throws Exception
	{
		Config.setParameter(C4C_INTEGRATION_FACADE_MOCK_FLAG, "false");
		TicketFacade ticketFacade = facadeFactory.getTicketFacade();

		Assert.assertEquals(c4cTicketFacadeImpl, ticketFacade);
	}

	/**
	 * Should return a mock facade implantation if mock flag set to true.
	 *
	 * @throws Exception
	 */
	@Test
	public void shouldReturnMockFacadeWhenMockIsTrue() throws Exception
	{
		Config.setParameter(C4C_INTEGRATION_FACADE_MOCK_FLAG, "true");
		TicketFacade ticketFacade = facadeFactory.getTicketFacade();

		Assert.assertEquals(c4cTicketFacadeMock, ticketFacade);
	}

	/**
	 * Should return a mock facade implantation if mock flag is not set.
	 *
	 * @throws Exception
	 */
	@Test
	public void shouldReturnMockFacadeWhenNoParameterSet() throws Exception
	{
		Config.setParameter(C4C_INTEGRATION_FACADE_MOCK_FLAG, null);
		TicketFacade ticketFacade = facadeFactory.getTicketFacade();

		Assert.assertEquals(c4cTicketFacadeMock, ticketFacade);
	}
}

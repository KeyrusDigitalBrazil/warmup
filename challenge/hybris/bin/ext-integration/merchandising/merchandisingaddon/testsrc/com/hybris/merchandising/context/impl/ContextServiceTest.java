/**
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.merchandising.context.impl;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.hybris.merchandising.constants.MerchandisingConstants;
import com.hybris.merchandising.context.ContextRepository;
import com.hybris.merchandising.context.ContextService;

import de.hybris.platform.servicelayer.session.SessionService;


/**
 * Test class for {@link ContextService}
 *
 */
public class ContextServiceTest
{
	SessionService sessionService;
	DefaultContextService contextService;

	@Before
	public void setUp()
	{
		contextService = new DefaultContextService();
		sessionService = Mockito.mock(SessionService.class);
		Mockito.when(sessionService.getAttribute(MerchandisingConstants.CONTEXT_STORE_KEY)).thenAnswer(new Answer<Object>()
		{
			private int count = 0;

			@Override
			public Object answer(final InvocationOnMock invocation) throws Throwable
			{
				if (count++ == 0)
					return null;
				return new DefaultContextRepository();
			}			});

		contextService.setSessionService(sessionService);
	}

	@Test
	public void testContextService()
	{
		final ContextRepository contextRepository = contextService.getContextRepository();
		assertNotNull(contextRepository);
		assertNotNull(contextService.getContextRepository());
	}
}

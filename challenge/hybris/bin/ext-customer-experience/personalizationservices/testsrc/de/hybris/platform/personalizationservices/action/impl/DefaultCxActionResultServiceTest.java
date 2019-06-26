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
package de.hybris.platform.personalizationservices.action.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.personalizationservices.data.CxAbstractActionResult;
import de.hybris.platform.servicelayer.session.MockSessionService;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultCxActionResultServiceTest
{
	private DefaultCxActionResultService service;
	private UserModel user;
	private CatalogVersionModel catalogVersion;

	@Mock
	private UserService userService;

	@Before
	public void setup()
	{

		final Session session = Mockito.mock(Session.class);
		MockitoAnnotations.initMocks(this);

		final SessionService sessionService = new TestDefaultSessionService(session);

		service = new DefaultCxActionResultService();
		service.setSessionService(sessionService);
		service.setUserService(userService);
		user = new UserModel();
		catalogVersion = new CatalogVersionModel();
	}

	@Test
	public void testEmptyResult()
	{
		final List<CxAbstractActionResult> actual = service.getActionResults(user, catalogVersion);

		Assert.assertNotNull(actual);
		Assert.assertTrue(actual.isEmpty());
	}

	@Test
	public void testStoreResults()
	{
		final List<CxAbstractActionResult> expected = new ArrayList<CxAbstractActionResult>();
		service.setActionResultsInSession(user, catalogVersion, expected);
		final List<CxAbstractActionResult> actual = service.getActionResults(user, catalogVersion);

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testOverrideResults()
	{
		final List<CxAbstractActionResult> first = new ArrayList<CxAbstractActionResult>();
		service.setActionResultsInSession(user, catalogVersion, first);
		final List<CxAbstractActionResult> second = new ArrayList<CxAbstractActionResult>();
		service.setActionResultsInSession(user, catalogVersion, second);
		final List<CxAbstractActionResult> actual = service.getActionResults(user, catalogVersion);

		Assert.assertEquals(second, actual);
	}

	@Test
	public void testClearResults()
	{
		final List<CxAbstractActionResult> expected = new ArrayList<CxAbstractActionResult>();
		service.setActionResultsInSession(user, catalogVersion, expected);
		service.clearActionResultsInSession(user, catalogVersion);
		final List<CxAbstractActionResult> actual = service.getActionResults(user, catalogVersion);

		Assert.assertNotNull(actual);
		Assert.assertTrue(actual.isEmpty());
	}

	class TestDefaultSessionService extends MockSessionService
	{
		private final Session sessionMock;

		TestDefaultSessionService(final Session sessionMock)
		{
			super();
			this.sessionMock = sessionMock;
		}

		@Override
		public Session getCurrentSession()
		{
			return sessionMock;
		}
	}
}

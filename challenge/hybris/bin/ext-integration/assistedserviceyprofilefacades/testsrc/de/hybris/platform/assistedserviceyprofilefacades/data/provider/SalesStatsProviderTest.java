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
package de.hybris.platform.assistedserviceyprofilefacades.data.provider;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.assistedserviceyprofilefacades.data.SalesStatsData;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@UnitTest
public class SalesStatsProviderTest
{
	private static final String EXPECTED_USER = "EXPECTED_USER";

	@InjectMocks
	private final SalesStatsProvider provider = new SalesStatsProvider();

	@Mock
	private UserService userService;

	@Mock
	private UserModel user;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		Mockito.when(userService.getCurrentUser()).thenReturn(user);
		Mockito.when(user.getName()).thenReturn(EXPECTED_USER);
	}

	@Test
	public void getModelTest()
	{
		final SalesStatsData data = provider.getModel(null);

		Assert.assertNotNull(data);
		Assert.assertEquals(EXPECTED_USER, data.getName());
	}
}

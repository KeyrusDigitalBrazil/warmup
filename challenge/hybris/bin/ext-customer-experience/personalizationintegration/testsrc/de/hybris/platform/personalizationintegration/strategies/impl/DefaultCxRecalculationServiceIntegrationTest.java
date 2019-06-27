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
/**
 *
 */
package de.hybris.platform.personalizationintegration.strategies.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.personalizationintegration.segment.UserSegmentsProvider;
import de.hybris.platform.personalizationservices.AbstractCxServiceTest;
import de.hybris.platform.personalizationservices.RecalculateAction;
import de.hybris.platform.personalizationservices.model.CxUserToSegmentModel;
import de.hybris.platform.personalizationservices.service.CxRecalculationService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultCxRecalculationServiceIntegrationTest extends AbstractCxServiceTest
{

	private static final String USER = "customer2@hybris.com";

	@Resource
	CxRecalculationService cxRecalculationService;

	@Resource
	UserService userService;

	@Resource
	DefaultCxUpdateUserSegmentStrategy defaultCxUpdateUserSegmentStrategy;

	List<UserSegmentsProvider> providers;

	List<RecalculateAction> actions = Arrays.asList(RecalculateAction.UPDATE, RecalculateAction.RECALCULATE);

	@Before
	public void setup()
	{
		providers = defaultCxUpdateUserSegmentStrategy.getProviders();
		final List<UserSegmentsProvider> newProviders = new ArrayList<>(providers);

		newProviders.add(u -> Collections.emptyList());

		defaultCxUpdateUserSegmentStrategy.setProviders(Optional.of(newProviders));
	}

	@After
	public void tearDown()
	{
		defaultCxUpdateUserSegmentStrategy.setProviders(Optional.of(providers));
	}


	@Test
	public void userUpdateAndRecalculateTest()
	{
		final UserModel user = userService.getUserForUID(USER);

		final Collection<CxUserToSegmentModel> userToSegments = user.getUserToSegments();
		Assert.assertEquals(1, userToSegments.size());
		final String code = userToSegments.iterator().next().getSegment().getCode();
		Assert.assertEquals("segment2", code);

		cxRecalculationService.recalculate(user, actions);

		final Collection<CxUserToSegmentModel> userToSegments2 = user.getUserToSegments();
		Assert.assertEquals(1, userToSegments2.size());
		final String code2 = userToSegments2.iterator().next().getSegment().getCode();
		Assert.assertEquals("segment1", code2);
	}
}

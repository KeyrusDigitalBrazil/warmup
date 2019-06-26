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
package de.hybris.platform.cms2.registry.impl;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.containers.AbstractCMSComponentContainerModel;
import de.hybris.platform.cms2.strategies.CMSComponentContainerStrategy;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCMSComponentContainerRegistryTest
{
	private String DUMMY_CONTAINER_CODE = "DummyContainerCode";
	private String AB_TEST_CONTAINER_CODE = "AbTestContainerCode";

	@InjectMocks
	private DefaultCMSComponentContainerRegistry defaultCMSComponentContainerRegistry;

	@Mock
	private CMSComponentContainerStrategy defaultCMSComponentContainerStrategy;
	@Mock
	private TypeService typeService;
	@Mock
	private CMSComponentContainerStrategy strategy1;
	@Mock
	private CMSComponentContainerStrategy strategy2;
	@Mock
	private AbstractCMSComponentContainerModel container;
	@Mock
	private ComposedTypeModel containerComposedTypeModel;

	private Map<String, CMSComponentContainerStrategy> strategies;

	@Before
	public void setUp()
	{
		strategies = new HashMap<>();
		strategies.put(DUMMY_CONTAINER_CODE, strategy1);
		strategies.put(AB_TEST_CONTAINER_CODE, strategy2);

		defaultCMSComponentContainerRegistry.setStrategies(strategies);

		when(typeService.getComposedTypeForClass(container.getClass())).thenReturn(containerComposedTypeModel);
	}

	@Test
	public void shouldReturnStrategyForContainer()
	{
		// GIVEN
		when(containerComposedTypeModel.getCode()).thenReturn(DUMMY_CONTAINER_CODE);

		// WHEN
		final CMSComponentContainerStrategy strategy = defaultCMSComponentContainerRegistry
				.getStrategy(container);

		// THEN
		assertThat(strategy, is(strategy1));
	}

	@Test
	public void shouldReturnDefaultStrategy()
	{
		// GIVEN
		when(containerComposedTypeModel.getCode()).thenReturn("FAKE");

		// WHEN
		final CMSComponentContainerStrategy strategy = defaultCMSComponentContainerRegistry
				.getStrategy(container);

		// THEN
		assertThat(strategy, is(defaultCMSComponentContainerStrategy));
	}
}

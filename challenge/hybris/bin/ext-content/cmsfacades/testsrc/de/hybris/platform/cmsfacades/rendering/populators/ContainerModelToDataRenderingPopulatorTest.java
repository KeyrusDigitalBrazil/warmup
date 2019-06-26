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
package de.hybris.platform.cmsfacades.rendering.populators;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_COMPONENTS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.containers.AbstractCMSComponentContainerModel;
import de.hybris.platform.cms2.registry.CMSComponentContainerRegistry;
import de.hybris.platform.cms2.strategies.CMSComponentContainerStrategy;
import de.hybris.platform.core.model.ItemModel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ContainerModelToDataRenderingPopulatorTest
{
	private static String COMPONENT1_UID = "componentUid1";
	private static String COMPONENT2_UID = "componentUid2";

	@InjectMocks
	private ContainerModelToDataRenderingPopulator populator;

	@Mock
	private CMSComponentContainerRegistry cmsComponentContainerRegistry;

	@Mock
	private AbstractCMSComponentContainerModel container;

	@Mock
	private ItemModel notContainer;

	@Mock
	private CMSComponentContainerStrategy strategy;

	@Mock
	private AbstractCMSComponentModel component1;
	@Mock
	private AbstractCMSComponentModel component2;

	private Map<String, Object> targetMap;

	@Before
	public void setUp()
	{
		targetMap = new HashMap<>();

		when(cmsComponentContainerRegistry.getStrategy(container)).thenReturn(strategy);
		when(strategy.getDisplayComponentsForContainer(container)).thenReturn(Arrays.asList(component1, component2));
		when(component1.getUid()).thenReturn(COMPONENT1_UID);
		when(component2.getUid()).thenReturn(COMPONENT2_UID);
	}

	@Test
	public void shouldNotPopulateComponentsFieldIfNotContainer()
	{
		// WHEN
		populator.populate(notContainer, targetMap);

		// THEN
		Assert.assertNull(targetMap.get(FIELD_COMPONENTS));
	}

	@Test
	public void shouldPopulateListOfComponentsIfContainerIsGiven()
	{
		// WHEN
		populator.populate(container, targetMap);

		// THEN
		List<Object> components = (List<Object>) targetMap.get(FIELD_COMPONENTS);
		assertThat(components, hasSize(2));
		assertThat(components, hasItems(COMPONENT1_UID, COMPONENT2_UID));
	}
}


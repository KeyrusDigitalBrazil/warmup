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
package de.hybris.platform.cmsfacades.navigations.populator.model;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;

import java.util.Arrays;
import java.util.OptionalInt;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class NavigationNodeModelToDataPopulatorTest
{

	@Mock
	private CMSNavigationNodeModel model;

	@Mock
	private CMSNavigationNodeModel sibling;
	
	@Mock
	private CMSNavigationNodeModel parent;

	@Before
	public void setup() 
	{
		when(model.getParent()).thenReturn(parent);
		when(parent.getChildren()).thenReturn(Arrays.asList(sibling, model));
	}
	
	@Test
	public void testPositionOfNodeWhenParentIsNotNull() 
	{
		final NavigationNodeModelToDataPopulator populator = new NavigationNodeModelToDataPopulator();
		final OptionalInt position = populator.getPosition(model);
		assertThat(position.getAsInt(), is(1));
	}

	@Test
	public void testPositionOfNodeWhenParentIsNull()
	{
		when(model.getParent()).thenReturn(null);
		final NavigationNodeModelToDataPopulator populator = new NavigationNodeModelToDataPopulator();
		final OptionalInt position = populator.getPosition(model);
		assertThat(position.isPresent(), is(false));
	}
}

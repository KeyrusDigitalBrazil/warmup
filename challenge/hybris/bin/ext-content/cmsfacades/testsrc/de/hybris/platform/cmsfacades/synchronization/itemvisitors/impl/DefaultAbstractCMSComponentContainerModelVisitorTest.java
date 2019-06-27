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
package de.hybris.platform.cmsfacades.synchronization.itemvisitors.impl;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.components.SimpleCMSComponentModel;
import de.hybris.platform.cms2.model.contents.containers.AbstractCMSComponentContainerModel;
import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;
import de.hybris.platform.core.model.ItemModel;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAbstractCMSComponentContainerModelVisitorTest
{

	@Mock
	private AbstractCMSComponentContainerModel component;
	@Mock
	private AbstractRestrictionModel restriction1;
	@Mock
	private AbstractRestrictionModel restriction2;
	@Mock
	private SimpleCMSComponentModel simpleCMSComponent1;
	@Mock
	private SimpleCMSComponentModel simpleCMSComponent2;
	@Mock
	private SimpleCMSComponentModel simpleCMSComponent3;
	
	@InjectMocks
	private DefaultAbstractCMSComponentContainerModelVisitor visitor;
	
	
	@Before
	public void setUp()
	{
		when(component.getRestrictions()).thenReturn(asList(restriction1, restriction2));
		when(component.getSimpleCMSComponents()).thenReturn(asList(simpleCMSComponent1, simpleCMSComponent2));
		when(component.getCurrentCMSComponents()).thenReturn(asList(simpleCMSComponent3));
	}

	@Test
	public void willCollectRestritionsAndSimpleCMSComponents()
	{
		
		List<ItemModel> visit = visitor.visit(component, null, null);
		
		assertThat(visit, containsInAnyOrder(restriction1, restriction2, simpleCMSComponent1, simpleCMSComponent2, simpleCMSComponent3));
		
		
	}

}

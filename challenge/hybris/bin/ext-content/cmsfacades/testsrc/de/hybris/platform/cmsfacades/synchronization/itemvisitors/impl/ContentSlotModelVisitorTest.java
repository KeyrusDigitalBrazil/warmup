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
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
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
public class ContentSlotModelVisitorTest
{

	@Mock
	private ContentSlotModel slot;
	@Mock
	private AbstractCMSComponentModel component1;
	@Mock
	private AbstractCMSComponentModel component2;
	@InjectMocks
	private ContentSlotModelVisitor visitor;
	
	
	@Before
	public void setUp()
	{
		when(slot.getCmsComponents()).thenReturn(asList(component1, component2));

	}

	@Test
	public void willCollectCmsComponents()
	{
		
		List<ItemModel> visit = visitor.visit(slot, null, null);
		
		assertThat(visit, containsInAnyOrder(component1, component2));
		
		
	}

}

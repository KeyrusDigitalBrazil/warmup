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
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationEntryModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
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
public class CMSNavigationNodeModelVisitorTest
{

	@Mock
	private CMSNavigationNodeModel node;
	@Mock
	private CMSNavigationNodeModel child1;
	@Mock
	private CMSNavigationNodeModel child2;
	@Mock
	private CMSNavigationEntryModel entry1;
	@Mock
	private CMSNavigationEntryModel entry2;
	@Mock
	private CMSLinkComponentModel link1;
	@Mock
	private CMSLinkComponentModel link2;
	@InjectMocks
	private CMSNavigationNodeModelVisitor visitor;
	
	
	@Before
	public void setUp()
	{
		when(node.getChildren()).thenReturn(asList(child1, child2));
		when(node.getEntries()).thenReturn(asList(entry1, entry2));
		when(node.getLinks()).thenReturn(asList(link1, link2));

	}

	@Test
	public void willCollectCmsComponents()
	{
		
		List<ItemModel> visit = visitor.visit(node, null, null);
		
		assertThat(visit, containsInAnyOrder(child1, child2, entry1, entry2, link1, link2));
		
		
	}

}

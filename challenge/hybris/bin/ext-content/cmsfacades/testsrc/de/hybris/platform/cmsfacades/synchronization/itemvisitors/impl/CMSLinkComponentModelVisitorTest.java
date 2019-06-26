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
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CMSLinkComponentModelVisitorTest
{

	@Mock
	private CMSLinkComponentModel component;
	@Mock
	private AbstractRestrictionModel restriction1;
	@Mock
	private AbstractRestrictionModel restriction2;
	@Mock
	private ContentPageModel page;
	
	@InjectMocks
	private CMSLinkComponentModelVisitor visitor;
	
	
	@Before
	public void setUp()
	{
		when(component.getRestrictions()).thenReturn(asList(restriction1, restriction2));
		when(component.getContentPage()).thenReturn(page);
	}

	@Test
	public void willCollectRestrictions()
	{
		
		List<ItemModel> visit = visitor.visit(component, null, null);
		
		assertThat(visit, containsInAnyOrder(restriction1, restriction2, page));
		
		
	}

}

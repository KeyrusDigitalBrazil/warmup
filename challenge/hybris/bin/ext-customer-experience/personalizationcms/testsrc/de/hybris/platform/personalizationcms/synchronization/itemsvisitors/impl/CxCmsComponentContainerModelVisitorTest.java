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
package de.hybris.platform.personalizationcms.synchronization.itemsvisitors.impl;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.components.SimpleCMSComponentModel;
import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.personalizationcms.model.CxCmsComponentContainerModel;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;



@UnitTest
public class CxCmsComponentContainerModelVisitorTest
{
	private CxCmsComponentContainerModelVisitor visitor;

	@Mock
	private CxCmsComponentContainerModel source;

	@Mock
	private SimpleCMSComponentModel defaultComponent;

	@Mock
	private SimpleCMSComponentModel currentComponent;

	@Mock
	private SimpleCMSComponentModel simpleCMSComponent;

	@Mock
	private AbstractRestrictionModel restriction;

	@Before
	public void initTest()
	{
		visitor = new CxCmsComponentContainerModelVisitor();
		MockitoAnnotations.initMocks(this);
		Mockito.when(source.getDefaultCmsComponent()).thenReturn(defaultComponent);
		Mockito.when(source.getCurrentCMSComponents()).thenReturn(Arrays.asList(currentComponent));
		Mockito.when(source.getSimpleCMSComponents()).thenReturn(Arrays.asList(simpleCMSComponent));
		Mockito.when(source.getRestrictions()).thenReturn(Arrays.asList(restriction));
	}

	@Test
	public void shouldVisitAllTheFields()
	{
		final List<ItemModel> visited = visitor.visit(source, null, null);
		assertThat(visited, containsInAnyOrder(defaultComponent, currentComponent, simpleCMSComponent, restriction));
	}

}

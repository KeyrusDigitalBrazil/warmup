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
package de.hybris.platform.acceleratorfacades.component.synchronization.itemvisitors.impl;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorcms.model.components.AbstractMediaContainerComponentModel;
import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;
import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.media.MediaContainerModel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAbstractMediaContainerComponentModelVisitorTest
{

	@Mock
	private AbstractMediaContainerComponentModel component;
	@Mock
	private AbstractRestrictionModel restriction1;
	@Mock
	private AbstractRestrictionModel restriction2;
	@Mock
	private MediaContainerModel media;

	@InjectMocks
	private DefaultAbstractMediaContainerComponentModelVisitor visitor;

	@Before
	public void setUp()
	{
		when(component.getRestrictions()).thenReturn(asList(restriction1, restriction2));
		when(component.getMedia(Locale.ENGLISH)).thenReturn(media);
	}

	@Test
	public void willCollectRestrictionsAndMedia()
	{
		final Map<String, Object> context = new HashMap<>();
		context.put(CmsfacadesConstants.VISITORS_CTX_LOCALES, Arrays.asList(Locale.ENGLISH));

		final List<ItemModel> visit = visitor.visit(component, null, context);

		assertThat(visit, containsInAnyOrder(restriction1, restriction2, media));
	}
}

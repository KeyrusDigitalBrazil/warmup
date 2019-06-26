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
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MediaContainerModelVisitorTest
{

	@Mock
	private MediaContainerModel mc;
	@Mock
	private MediaModel media1;
	@Mock
	private MediaModel media2;
	@InjectMocks
	private MediaContainerModelVisitor visitor;

	@Before
	public void setUp()
	{
		when(mc.getMedias()).thenReturn(asList(media1, media2));
	}

	@Test
	public void willCollectAllMedia()
	{
		final List<ItemModel> visit = visitor.visit(mc, null, null);

		assertThat(visit, containsInAnyOrder(media1, media2));
	}
}

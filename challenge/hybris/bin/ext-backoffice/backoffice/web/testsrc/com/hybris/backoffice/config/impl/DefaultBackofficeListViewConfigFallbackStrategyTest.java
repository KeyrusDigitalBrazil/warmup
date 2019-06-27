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
package com.hybris.backoffice.config.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;

import de.hybris.platform.servicelayer.type.TypeService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.cockpitng.core.config.impl.DefaultConfigContext;
import com.hybris.cockpitng.core.config.impl.jaxb.listview.ListView;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.testing.AbstractCockpitngUnitTest;
import com.hybris.cockpitng.testing.annotation.NullSafeWidget;


@NullSafeWidget
@RunWith(MockitoJUnitRunner.class)
public class DefaultBackofficeListViewConfigFallbackStrategyTest
		extends AbstractCockpitngUnitTest<DefaultBackofficeListViewConfigFallbackStrategy>
{

	@Spy
	@InjectMocks
	private DefaultBackofficeListViewConfigFallbackStrategy strategy;

	@Mock
	private TypeService typeService;

	@Mock
	private TypeFacade typeFacade;

	@Test
	public void loadFallbackConfiguration()
	{
		//given
		doThrow(IllegalArgumentException.class).when(typeService).getTypeForCode(null);

		//when
		final ListView listView = strategy.loadFallbackConfiguration(new DefaultConfigContext(), ListView.class);

		//then
		assertThat(listView).isNotNull();
	}
}

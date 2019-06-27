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
package de.hybris.platform.adaptivesearch.strategies.impl;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.adaptivesearch.context.AsSearchProfileContext;
import de.hybris.platform.adaptivesearch.data.AsSimpleSearchProfile;
import de.hybris.platform.adaptivesearch.model.AsSimpleSearchProfileModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class AsSimpleSearchProfileLoadStrategyTest
{
	private AsSimpleSearchProfileLoadStrategy strategy;

	@Mock
	private Converter<AsSimpleSearchProfileModel, AsSimpleSearchProfile> asSimpleSearchProfileConverter;

	@Mock
	private AsSearchProfileContext context;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		strategy = new AsSimpleSearchProfileLoadStrategy();
		strategy.setAsSimpleSearchProfileConverter(asSimpleSearchProfileConverter);
	}

	@Test
	public void load()
	{
		//given
		final AsSimpleSearchProfileModel searchProfileModel = new AsSimpleSearchProfileModel();
		final AsSimpleSearchProfile searchProfileData = new AsSimpleSearchProfile();
		when(asSimpleSearchProfileConverter.convert(searchProfileModel)).thenReturn(searchProfileData);

		//when
		final AsSimpleSearchProfile loadedSearchProfileData = strategy.load(context, searchProfileModel);

		//then
		assertSame(loadedSearchProfileData, searchProfileData);
	}


}

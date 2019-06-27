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
import de.hybris.platform.adaptivesearch.data.AsCategoryAwareSearchProfile;
import de.hybris.platform.adaptivesearch.model.AsCategoryAwareSearchProfileModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class AsCategoryAwareSearchProfileLoadStrategyTest
{
	private AsCategoryAwareSearchProfileLoadStrategy strategy;

	@Mock
	private Converter<AsCategoryAwareSearchProfileModel, AsCategoryAwareSearchProfile> asCategoryAwareSearchProfileConverter;

	@Mock
	private AsSearchProfileContext context;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		strategy = new AsCategoryAwareSearchProfileLoadStrategy();
		strategy.setAsCategoryAwareSearchProfileConverter(asCategoryAwareSearchProfileConverter);
	}

	@Test
	public void load()
	{
		//given
		final AsCategoryAwareSearchProfileModel searchProfileModel = new AsCategoryAwareSearchProfileModel();
		final AsCategoryAwareSearchProfile searchProfileData = new AsCategoryAwareSearchProfile();
		when(asCategoryAwareSearchProfileConverter.convert(searchProfileModel)).thenReturn(searchProfileData);

		//when
		final AsCategoryAwareSearchProfile loadedSearchProfileData = strategy.load(context, searchProfileModel);

		//then
		assertSame(loadedSearchProfileData, searchProfileData);
	}
}

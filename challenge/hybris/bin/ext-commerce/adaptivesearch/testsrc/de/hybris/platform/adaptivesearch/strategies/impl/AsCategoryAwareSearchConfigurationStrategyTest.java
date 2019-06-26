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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.adaptivesearch.context.AsSearchProfileContext;
import de.hybris.platform.adaptivesearch.daos.AsSearchConfigurationDao;
import de.hybris.platform.adaptivesearch.model.AsCategoryAwareSearchConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AsCategoryAwareSearchProfileModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class AsCategoryAwareSearchConfigurationStrategyTest
{
	private AsCategoryAwareSearchConfigurationStrategy strategy;

	@Mock
	private ModelService modelService;

	@Mock
	private AsSearchConfigurationDao asSearchConfigurationDao;

	@Mock
	private AsSearchProfileContext context;

	@Mock
	private AsCategoryAwareSearchProfileModel searchProfile;

	@Mock
	private AsCategoryAwareSearchConfigurationModel searchConfiguration;

	@Mock
	private CatalogVersionModel catalogVersion;

	@Mock
	private CategoryModel category;

	@Mock
	private final CategoryModel category10 = new CategoryModel();

	@Mock
	private final CategoryModel category20 = new CategoryModel();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		// YTODO remove after CT-1544 is done
		when(searchConfiguration.getCategory()).thenReturn(category);
		when(searchConfiguration.getUniqueIdx()).thenReturn("176231_1");
		when(category.getPk()).thenReturn(PK.parse("1"));

		when(category10.getCode()).thenReturn("cat10");
		when(category20.getCode()).thenReturn("cat20");


		strategy = new AsCategoryAwareSearchConfigurationStrategy();
		strategy.setModelService(modelService);
		strategy.setAsSearchConfigurationDao(asSearchConfigurationDao);
	}

	@Test
	public void getSearchConfiguration()
	{
		// given
		final Map<String, Object> filters = new HashMap<>();
		filters.put(AsCategoryAwareSearchConfigurationModel.SEARCHPROFILE, searchProfile);
		filters.put(AsCategoryAwareSearchConfigurationModel.CATEGORY, category);

		when(context.getCategoryPath()).thenReturn(Collections.singletonList(category));
		when(asSearchConfigurationDao.findSearchConfigurations(AsCategoryAwareSearchConfigurationModel.class, filters))
				.thenReturn(Collections.singletonList(searchConfiguration));

		// when
		final Optional<AsCategoryAwareSearchConfigurationModel> returnedSearchConfiguration = strategy.getForContext(context,
				searchProfile);

		// then
		assertTrue(returnedSearchConfiguration.isPresent());
		assertSame(searchConfiguration, returnedSearchConfiguration.get());
	}

	@Test
	public void getOrCreateExistingSearchConfiguration()
	{
		// given
		final Map<String, Object> filters = new HashMap<>();
		filters.put(AsCategoryAwareSearchConfigurationModel.SEARCHPROFILE, searchProfile);
		filters.put(AsCategoryAwareSearchConfigurationModel.CATEGORY, category);

		when(context.getCategoryPath()).thenReturn(Collections.singletonList(category));
		when(asSearchConfigurationDao.findSearchConfigurations(AsCategoryAwareSearchConfigurationModel.class, filters))
				.thenReturn(Collections.singletonList(searchConfiguration));

		// when
		final AsCategoryAwareSearchConfigurationModel returnedSearchConfiguration = strategy.getOrCreateForContext(context,
				searchProfile);

		// then
		assertSame(searchConfiguration, returnedSearchConfiguration);
	}

	@Test
	public void getOrCreateNonExistingSearchConfiguration()
	{
		// given
		final Map<String, Object> filters = new HashMap<>();
		filters.put(AsCategoryAwareSearchConfigurationModel.SEARCHPROFILE, searchProfile);
		filters.put(AsCategoryAwareSearchConfigurationModel.CATEGORY, category);

		when(context.getCategoryPath()).thenReturn(Collections.singletonList(category));
		when(asSearchConfigurationDao.findSearchConfigurations(AsCategoryAwareSearchConfigurationModel.class, filters))
				.thenReturn(Collections.emptyList());
		when(modelService.create(AsCategoryAwareSearchConfigurationModel.class)).thenReturn(searchConfiguration);

		when(searchProfile.getCatalogVersion()).thenReturn(catalogVersion);

		// when
		final AsCategoryAwareSearchConfigurationModel returnedSearchConfiguration = strategy.getOrCreateForContext(context,
				searchProfile);

		// then
		assertSame(searchConfiguration, returnedSearchConfiguration);
		verify(searchConfiguration).setSearchProfile(searchProfile);
		verify(searchConfiguration).setCatalogVersion(catalogVersion);
		verify(searchConfiguration).setCategory(category);
	}

	@Test
	public void getQualifiers()
	{
		//given
		final AsCategoryAwareSearchProfileModel searchProfile = new AsCategoryAwareSearchProfileModel();
		final AsCategoryAwareSearchConfigurationModel globalCS = new AsCategoryAwareSearchConfigurationModel();
		final AsCategoryAwareSearchConfigurationModel cat10CS = new AsCategoryAwareSearchConfigurationModel();
		cat10CS.setCategory(category10);
		final AsCategoryAwareSearchConfigurationModel cat20CS = new AsCategoryAwareSearchConfigurationModel();
		cat20CS.setCategory(category20);

		final List<AsCategoryAwareSearchConfigurationModel> searchConfigurations = new ArrayList<>();
		searchConfigurations.add(globalCS);
		searchConfigurations.add(cat10CS);
		searchConfigurations.add(cat20CS);

		searchProfile.setSearchConfigurations(searchConfigurations);

		//when
		final Set<String> qualifiers = strategy.getQualifiers(searchProfile);

		//then
		assertEquals(3, qualifiers.size());
		assertTrue(qualifiers.contains(null));
		assertTrue(qualifiers.contains(category10.getCode()));
		assertTrue(qualifiers.contains(category20.getCode()));

	}
}

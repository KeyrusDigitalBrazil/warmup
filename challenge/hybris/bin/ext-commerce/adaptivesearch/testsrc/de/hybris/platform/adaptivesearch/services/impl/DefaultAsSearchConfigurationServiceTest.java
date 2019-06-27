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
package de.hybris.platform.adaptivesearch.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.adaptivesearch.context.AsSearchProfileContext;
import de.hybris.platform.adaptivesearch.daos.AsSearchConfigurationDao;
import de.hybris.platform.adaptivesearch.model.AbstractAsSearchConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AbstractAsSearchProfileModel;
import de.hybris.platform.adaptivesearch.strategies.AsCloneStrategy;
import de.hybris.platform.adaptivesearch.strategies.AsSearchConfigurationStrategy;
import de.hybris.platform.adaptivesearch.strategies.AsSearchProfileMapping;
import de.hybris.platform.adaptivesearch.strategies.AsSearchProfileRegistry;
import de.hybris.platform.catalog.model.CatalogVersionModel;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultAsSearchConfigurationServiceTest
{
	private static final String SEARCH_CONFIGURATION_UID = "searchConfiguration";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Mock
	private AsSearchConfigurationDao asSearchConfigurationDao;

	@Mock
	private AsSearchProfileRegistry asSearchProfileRegistry;

	@Mock
	private AsCloneStrategy asCloneStrategy;

	private DefaultAsSearchConfigurationService defaultAsSearchConfigurationService;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		defaultAsSearchConfigurationService = new DefaultAsSearchConfigurationService();
		defaultAsSearchConfigurationService.setAsSearchConfigurationDao(asSearchConfigurationDao);
		defaultAsSearchConfigurationService.setAsSearchProfileRegistry(asSearchProfileRegistry);
		defaultAsSearchConfigurationService.setAsCloneStrategy(asCloneStrategy);

	}

	@Test
	public void getAll()
	{
		// given
		final AbstractAsSearchConfigurationModel expectedSearchConfiguration1 = mock(AbstractAsSearchConfigurationModel.class);
		final AbstractAsSearchConfigurationModel expectedSearchConfiguration2 = mock(AbstractAsSearchConfigurationModel.class);

		when(asSearchConfigurationDao.findAllSearchConfigurations())
				.thenReturn(Arrays.asList(expectedSearchConfiguration1, expectedSearchConfiguration2));

		// when
		final List<AbstractAsSearchConfigurationModel> searchConfigurations = defaultAsSearchConfigurationService
				.getAllSearchConfigurations();

		// then
		assertEquals(2, searchConfigurations.size());
		assertTrue(searchConfigurations.contains(expectedSearchConfiguration1));
		assertTrue(searchConfigurations.contains(expectedSearchConfiguration2));
	}

	@Test
	public void getByCatalogVersion()
	{
		// given
		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);
		final AbstractAsSearchConfigurationModel expectedSearchConfiguration1 = mock(AbstractAsSearchConfigurationModel.class);
		final AbstractAsSearchConfigurationModel expectedSearchConfiguration2 = mock(AbstractAsSearchConfigurationModel.class);

		when(asSearchConfigurationDao.findSearchConfigurationsByCatalogVersion(catalogVersion))
				.thenReturn(Arrays.asList(expectedSearchConfiguration1, expectedSearchConfiguration2));

		// when
		final List<AbstractAsSearchConfigurationModel> searchConfigurations = defaultAsSearchConfigurationService
				.getSearchConfigurationsForCatalogVersion(catalogVersion);

		// then
		assertEquals(2, searchConfigurations.size());
		assertTrue(searchConfigurations.contains(expectedSearchConfiguration1));
		assertTrue(searchConfigurations.contains(expectedSearchConfiguration2));
	}

	@Test
	public void getByUid()
	{
		// given
		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);
		final Optional<AbstractAsSearchConfigurationModel> expectedSearchConfiguration = Optional
				.of(mock(AbstractAsSearchConfigurationModel.class));

		when(asSearchConfigurationDao.findSearchConfigurationByUid(catalogVersion, SEARCH_CONFIGURATION_UID))
				.thenReturn(expectedSearchConfiguration);

		// when
		final Optional<AbstractAsSearchConfigurationModel> searchConfiguration = defaultAsSearchConfigurationService
				.getSearchConfigurationForUid(catalogVersion, SEARCH_CONFIGURATION_UID);

		// then
		assertSame(expectedSearchConfiguration, searchConfiguration);
	}

	@Test
	public void cannotGetByUid()
	{
		// given
		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);

		when(asSearchConfigurationDao.findSearchConfigurationByUid(catalogVersion, SEARCH_CONFIGURATION_UID))
				.thenReturn(Optional.empty());

		// when
		final Optional<AbstractAsSearchConfigurationModel> searchConfiguration = defaultAsSearchConfigurationService
				.getSearchConfigurationForUid(catalogVersion, SEARCH_CONFIGURATION_UID);

		// then
		assertFalse(searchConfiguration.isPresent());
	}


	@Test
	public void getForContext()
	{
		// given
		final AsSearchProfileMapping mapping = mock(AsSearchProfileMapping.class);
		final AsSearchConfigurationStrategy searchConfigurationStrategy = mock(AsSearchConfigurationStrategy.class);

		final AsSearchProfileContext context = mock(AsSearchProfileContext.class);
		final AbstractAsSearchProfileModel searchProfile = mock(AbstractAsSearchProfileModel.class);
		final AbstractAsSearchConfigurationModel searchConfiguration = mock(AbstractAsSearchConfigurationModel.class);

		final Optional<AbstractAsSearchConfigurationModel> expectedResult = Optional.of(searchConfiguration);

		when(asSearchProfileRegistry.getSearchProfileMapping(searchProfile)).thenReturn(mapping);
		when(mapping.getSearchConfigurationStrategy()).thenReturn(searchConfigurationStrategy);
		when(searchConfigurationStrategy.getForContext(context, searchProfile)).thenReturn(expectedResult);

		// when
		final Optional<AbstractAsSearchConfigurationModel> result = defaultAsSearchConfigurationService
				.getSearchConfigurationForContext(context, searchProfile);

		// then
		assertSame(expectedResult, result);
	}

	@Test
	public void getOrCreateForContext()
	{
		// given
		final AsSearchProfileMapping mapping = mock(AsSearchProfileMapping.class);
		final AsSearchConfigurationStrategy searchConfigurationStrategy = mock(AsSearchConfigurationStrategy.class);

		final AsSearchProfileContext context = mock(AsSearchProfileContext.class);
		final AbstractAsSearchProfileModel searchProfile = mock(AbstractAsSearchProfileModel.class);
		final AbstractAsSearchConfigurationModel searchConfiguration = mock(AbstractAsSearchConfigurationModel.class);

		final AbstractAsSearchConfigurationModel expectedResult = searchConfiguration;

		when(asSearchProfileRegistry.getSearchProfileMapping(searchProfile)).thenReturn(mapping);
		when(mapping.getSearchConfigurationStrategy()).thenReturn(searchConfigurationStrategy);
		when(searchConfigurationStrategy.getOrCreateForContext(context, searchProfile)).thenReturn(expectedResult);

		// when
		final AbstractAsSearchConfigurationModel result = defaultAsSearchConfigurationService
				.getOrCreateSearchConfigurationForContext(context, searchProfile);

		// then
		assertSame(expectedResult, result);
	}

	@Test
	public void cloneSearchConfiguration()
	{
		//given
		final AbstractAsSearchConfigurationModel searchConfiguration = mock(AbstractAsSearchConfigurationModel.class);
		final AbstractAsSearchConfigurationModel clonedSearchConfiguration = mock(AbstractAsSearchConfigurationModel.class);
		when(asCloneStrategy.clone(searchConfiguration)).thenReturn(clonedSearchConfiguration);

		//when
		final AbstractAsSearchConfigurationModel clone = asCloneStrategy.clone(searchConfiguration);

		//then
		assertSame(clonedSearchConfiguration, clone);

	}

}

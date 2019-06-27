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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.adaptivesearch.daos.AsSearchProfileDao;
import de.hybris.platform.adaptivesearch.model.AbstractAsSearchProfileModel;
import de.hybris.platform.adaptivesearch.model.AsSearchProfileActivationSetModel;
import de.hybris.platform.adaptivesearch.strategies.AsCloneStrategy;
import de.hybris.platform.catalog.model.CatalogVersionModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultAsSearchProfileServiceTest
{
	private static final String DEFAULT_SEARCH_PROFILE_CODE = "profile";
	private static final String INDEX_TYPE = "indexType";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Mock
	private AsSearchProfileDao asSearchProfileDao;

	@Mock
	private AsCloneStrategy asCloneStrategy;

	private DefaultAsSearchProfileService defaultAsSearchProfileService;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		defaultAsSearchProfileService = new DefaultAsSearchProfileService();
		defaultAsSearchProfileService.setAsSearchProfileDao(asSearchProfileDao);
		defaultAsSearchProfileService.setAsCloneStrategy(asCloneStrategy);

	}

	@Test
	public void getAll()
	{
		// given
		final AbstractAsSearchProfileModel expectedSearchProfile1 = mock(AbstractAsSearchProfileModel.class);
		final AbstractAsSearchProfileModel expectedSearchProfile2 = mock(AbstractAsSearchProfileModel.class);

		when(asSearchProfileDao.findAllSearchProfiles()).thenReturn(Arrays.asList(expectedSearchProfile1, expectedSearchProfile2));

		// when
		final List<AbstractAsSearchProfileModel> searchProfiles = defaultAsSearchProfileService.getAllSearchProfiles();

		// then
		assertEquals(2, searchProfiles.size());
		assertTrue(searchProfiles.contains(expectedSearchProfile1));
		assertTrue(searchProfiles.contains(expectedSearchProfile2));
	}

	@Test
	public void getByIndexTypesAndCatalogVersions()
	{
		// given
		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);
		final AbstractAsSearchProfileModel expectedSearchProfile1 = mock(AbstractAsSearchProfileModel.class);
		final AbstractAsSearchProfileModel expectedSearchProfile2 = mock(AbstractAsSearchProfileModel.class);

		when(asSearchProfileDao.findSearchProfilesByIndexTypesAndCatalogVersions(Collections.singletonList(INDEX_TYPE),
				Collections.singletonList(catalogVersion))).thenReturn(Arrays.asList(expectedSearchProfile1, expectedSearchProfile2));

		// when
		final List<AbstractAsSearchProfileModel> searchProfiles = defaultAsSearchProfileService
				.getSearchProfilesForIndexTypesAndCatalogVersions(Collections.singletonList(INDEX_TYPE),
						Collections.singletonList(catalogVersion));

		// then
		assertEquals(2, searchProfiles.size());
		assertTrue(searchProfiles.contains(expectedSearchProfile1));
		assertTrue(searchProfiles.contains(expectedSearchProfile2));
	}

	@Test
	public void getByCatalogVersion()
	{
		// given
		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);
		final AbstractAsSearchProfileModel expectedSearchProfile1 = mock(AbstractAsSearchProfileModel.class);
		final AbstractAsSearchProfileModel expectedSearchProfile2 = mock(AbstractAsSearchProfileModel.class);

		when(asSearchProfileDao.findSearchProfilesByCatalogVersion(catalogVersion))
				.thenReturn(Arrays.asList(expectedSearchProfile1, expectedSearchProfile2));

		// when
		final List<AbstractAsSearchProfileModel> searchProfiles = defaultAsSearchProfileService
				.getSearchProfilesForCatalogVersion(catalogVersion);

		// then
		assertEquals(2, searchProfiles.size());
		assertTrue(searchProfiles.contains(expectedSearchProfile1));
		assertTrue(searchProfiles.contains(expectedSearchProfile2));
	}

	@Test
	public void getByCode()
	{
		// given
		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);
		final Optional<AbstractAsSearchProfileModel> expectedSearchProfile = Optional.of(mock(AbstractAsSearchProfileModel.class));

		when(asSearchProfileDao.findSearchProfileByCode(catalogVersion, DEFAULT_SEARCH_PROFILE_CODE))
				.thenReturn(expectedSearchProfile);

		// when
		final Optional<AbstractAsSearchProfileModel> searchProfile = defaultAsSearchProfileService
				.getSearchProfileForCode(catalogVersion, DEFAULT_SEARCH_PROFILE_CODE);

		// then
		assertSame(expectedSearchProfile, searchProfile);
	}

	@Test
	public void cannotGetByCode()
	{
		// given
		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);

		when(asSearchProfileDao.findSearchProfileByCode(catalogVersion, DEFAULT_SEARCH_PROFILE_CODE)).thenReturn(Optional.empty());

		// when
		final Optional<AbstractAsSearchProfileModel> searchProfile = defaultAsSearchProfileService
				.getSearchProfileForCode(catalogVersion, DEFAULT_SEARCH_PROFILE_CODE);

		// then
		assertFalse(searchProfile.isPresent());
	}


	@Test
	public void cloneSearchProfile()
	{
		//given
		final AbstractAsSearchProfileModel searchProfile = mock(AbstractAsSearchProfileModel.class);
		final AbstractAsSearchProfileModel clonedSearchProfile = mock(AbstractAsSearchProfileModel.class);
		when(asCloneStrategy.clone(searchProfile)).thenReturn(clonedSearchProfile);

		//when
		final AbstractAsSearchProfileModel clone = asCloneStrategy.clone(searchProfile);

		//then
		assertSame(clonedSearchProfile, clone);

	}

	@Test
	public void cloneSearchProfileWithActivationSet()
	{
		//given
		final AbstractAsSearchProfileModel searchProfile = mock(AbstractAsSearchProfileModel.class);
		final AsSearchProfileActivationSetModel activationSet = mock(AsSearchProfileActivationSetModel.class);
		searchProfile.setActivationSet(activationSet);
		final AbstractAsSearchProfileModel clonedSearchProfile = mock(AbstractAsSearchProfileModel.class);
		clonedSearchProfile.setActivationSet(activationSet);
		when(asCloneStrategy.clone(searchProfile)).thenReturn(clonedSearchProfile);

		//when
		final AbstractAsSearchProfileModel clone = asCloneStrategy.clone(searchProfile);

		//then
		assertSame(clonedSearchProfile, clone);

		assertNull(clone.getActivationSet());

	}

}
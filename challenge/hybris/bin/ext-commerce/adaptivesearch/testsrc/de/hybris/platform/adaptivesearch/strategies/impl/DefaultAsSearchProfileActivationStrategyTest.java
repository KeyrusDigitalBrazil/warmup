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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.adaptivesearch.context.AsSearchProfileContext;
import de.hybris.platform.adaptivesearch.daos.AsSearchProfileActivationSetDao;
import de.hybris.platform.adaptivesearch.data.AsSearchProfileActivationGroup;
import de.hybris.platform.adaptivesearch.model.AbstractAsSearchProfileModel;
import de.hybris.platform.adaptivesearch.model.AsSearchProfileActivationSetModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultAsSearchProfileActivationStrategyTest
{
	private static final String INDEX_TYPE = "indexType";

	@Mock
	private AsSearchProfileActivationSetDao asSearchProfileActivationSetDao;

	@Mock
	private AsSearchProfileContext context;

	@Mock
	private CatalogVersionModel catalogVersion;

	private DefaultAsSearchProfileActivationStrategy strategy;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		when(context.getIndexType()).thenReturn(INDEX_TYPE);
		when(context.getCatalogVersions()).thenReturn(Collections.singletonList(catalogVersion));

		strategy = new DefaultAsSearchProfileActivationStrategy();
		strategy.setAsSearchProfileActivationSetDao(asSearchProfileActivationSetDao);
	}

	@Test
	public void getEmptyActiveSearchProfiles()
	{
		// given
		when(asSearchProfileActivationSetDao.findSearchProfileActivationSetByIndexType(catalogVersion, INDEX_TYPE))
				.thenReturn(Optional.empty());

		// when
		final List<AbstractAsSearchProfileModel> searchProfiles = strategy.getActiveSearchProfiles(context);

		// then
		assertNotNull(searchProfiles);
		assertEquals(0, searchProfiles.size());
	}

	@Test
	public void getActiveSearchProfiles()
	{
		// given
		final AbstractAsSearchProfileModel searchProfile1 = mock(AbstractAsSearchProfileModel.class);
		final AbstractAsSearchProfileModel searchProfile2 = mock(AbstractAsSearchProfileModel.class);
		final AsSearchProfileActivationSetModel searchProfileActivationSet = mock(AsSearchProfileActivationSetModel.class);

		when(searchProfileActivationSet.getSearchProfiles()).thenReturn(Arrays.asList(searchProfile1, searchProfile2));
		when(asSearchProfileActivationSetDao.findSearchProfileActivationSetByIndexType(catalogVersion, INDEX_TYPE))
				.thenReturn(Optional.of(searchProfileActivationSet));

		// when
		final List<AbstractAsSearchProfileModel> searchProfiles = strategy.getActiveSearchProfiles(context);

		// then
		assertNotNull(searchProfiles);
		assertEquals(2, searchProfiles.size());
		assertEquals(searchProfiles.get(0), searchProfile1);
		assertEquals(searchProfiles.get(1), searchProfile2);
	}

	@Test
	public void getEmptyActiveSearchProfileGroup()
	{
		// given
		when(asSearchProfileActivationSetDao.findSearchProfileActivationSetByIndexType(catalogVersion, INDEX_TYPE))
				.thenReturn(Optional.empty());

		// when
		final AsSearchProfileActivationGroup activeSearchProfileGroup = strategy.getSearchProfileActivationGroup(context);

		// then
		assertNotNull(activeSearchProfileGroup);
		assertTrue(CollectionUtils.isEmpty(activeSearchProfileGroup.getSearchProfiles()));
	}

	@Test
	public void getActiveSearchProfileGroup()
	{
		// given
		final AbstractAsSearchProfileModel searchProfile1 = mock(AbstractAsSearchProfileModel.class);
		final AbstractAsSearchProfileModel searchProfile2 = mock(AbstractAsSearchProfileModel.class);
		final AsSearchProfileActivationSetModel searchProfileActivationSet = mock(AsSearchProfileActivationSetModel.class);

		when(searchProfileActivationSet.getSearchProfiles()).thenReturn(Arrays.asList(searchProfile1, searchProfile2));
		when(asSearchProfileActivationSetDao.findSearchProfileActivationSetByIndexType(catalogVersion, INDEX_TYPE))
				.thenReturn(Optional.of(searchProfileActivationSet));

		// when
		final AsSearchProfileActivationGroup activeSearchProfileGroup = strategy.getSearchProfileActivationGroup(context);


		// then
		assertNotNull(activeSearchProfileGroup);

		final List<AbstractAsSearchProfileModel> searchProfiles = activeSearchProfileGroup.getSearchProfiles();
		assertNotNull(searchProfiles);
		assertEquals(2, searchProfiles.size());
		assertEquals(searchProfiles.get(0), searchProfile1);
		assertEquals(searchProfiles.get(1), searchProfile2);
	}
}

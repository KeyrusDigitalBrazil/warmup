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
package de.hybris.platform.adaptivesearch.integration.synchronization;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.adaptivesearch.model.AsSimpleSearchConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AsSimpleSearchProfileModel;
import de.hybris.platform.adaptivesearch.services.AsSearchProfileService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.synchronization.CatalogSynchronizationService;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collections;
import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.lang.CharEncoding;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@IntegrationTest
public class AsSimpleSearchProfileSynchronizationTest extends AbstractAsSynchronizationTest
{
	private final static String CATALOG_ID = "hwcatalog";
	private final static String VERSION_STAGED = "Staged";
	private final static String VERSION_ONLINE = "Online";

	private static final String UID1 = "c5be51d4-5649-4a7f-b27d-c18758c5dfff";

	private final static String CODE1 = "searchProfile1";

	private final static String NAME1 = "name1";
	private final static String NAME2 = "name2";

	private final static String INDEX_TYPE1 = "testIndex1";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Resource
	private ModelService modelService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private CatalogSynchronizationService catalogSynchronizationService;

	@Resource
	private AsSearchProfileService asSearchProfileService;

	private CatalogVersionModel onlineCatalogVersion;
	private CatalogVersionModel stagedCatalogVersion;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/adaptivesearch/test/integration/asBase.impex", CharEncoding.UTF_8);

		stagedCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);
		onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);
	}

	@Test
	public void searchProfileNotFoundBeforeSynchronization() throws Exception
	{
		// given
		final AsSimpleSearchProfileModel searchProfile = modelService.create(AsSimpleSearchProfileModel.class);
		searchProfile.setCatalogVersion(stagedCatalogVersion);
		searchProfile.setCode(CODE1);
		searchProfile.setName(NAME1);
		searchProfile.setIndexType(INDEX_TYPE1);

		// when
		modelService.save(searchProfile);

		final Optional<AsSimpleSearchProfileModel> searchProfileOptional = asSearchProfileService
				.getSearchProfileForCode(onlineCatalogVersion, CODE1);

		// then
		assertFalse(searchProfileOptional.isPresent());
	}

	@Test
	public void synchronizeNewSearchProfile() throws Exception
	{
		// given
		final AsSimpleSearchProfileModel searchProfile = modelService.create(AsSimpleSearchProfileModel.class);
		searchProfile.setCatalogVersion(stagedCatalogVersion);
		searchProfile.setCode(CODE1);
		searchProfile.setName(NAME1);
		searchProfile.setIndexType(INDEX_TYPE1);

		final AsSimpleSearchConfigurationModel searchConfiguration = modelService.create(AsSimpleSearchConfigurationModel.class);
		searchConfiguration.setCatalogVersion(stagedCatalogVersion);
		searchConfiguration.setSearchProfile(searchProfile);
		searchConfiguration.setUid(UID1);

		// when
		modelService.save(searchProfile);
		modelService.save(searchConfiguration);
		modelService.refresh(searchProfile);
		catalogSynchronizationService.synchronizeFully(stagedCatalogVersion, onlineCatalogVersion);

		final Optional<AsSimpleSearchProfileModel> searchProfileOptional = asSearchProfileService
				.getSearchProfileForCode(onlineCatalogVersion, CODE1);

		// then
		assertTrue(searchProfileOptional.isPresent());

		final AsSimpleSearchProfileModel synchronizedSearchProfile = searchProfileOptional.get();
		assertSynchronized(searchProfile, synchronizedSearchProfile);
	}

	@Test
	public void synchronizeUpdatedSearchProfile() throws Exception
	{
		// given
		final AsSimpleSearchProfileModel searchProfile = modelService.create(AsSimpleSearchProfileModel.class);
		searchProfile.setCatalogVersion(stagedCatalogVersion);
		searchProfile.setCode(CODE1);
		searchProfile.setName(NAME1);
		searchProfile.setIndexType(INDEX_TYPE1);

		final AsSimpleSearchConfigurationModel searchConfiguration = modelService.create(AsSimpleSearchConfigurationModel.class);
		searchConfiguration.setCatalogVersion(stagedCatalogVersion);
		searchConfiguration.setSearchProfile(searchProfile);
		searchConfiguration.setUid(UID1);

		// when
		modelService.save(searchProfile);
		modelService.save(searchConfiguration);
		modelService.refresh(searchProfile);
		catalogSynchronizationService.synchronizeFully(stagedCatalogVersion, onlineCatalogVersion);

		searchProfile.setName(NAME2);
		searchProfile.setSearchConfigurations(Collections.emptyList());

		modelService.save(searchProfile);
		catalogSynchronizationService.synchronizeFully(stagedCatalogVersion, onlineCatalogVersion);

		final Optional<AsSimpleSearchProfileModel> searchProfileOptional = asSearchProfileService
				.getSearchProfileForCode(onlineCatalogVersion, CODE1);

		// then
		assertTrue(searchProfileOptional.isPresent());

		final AsSimpleSearchProfileModel synchronizedSearchProfile = searchProfileOptional.get();
		assertSynchronized(searchProfile, synchronizedSearchProfile);
	}

	@Test
	public void synchronizeRemovedSearchProfile() throws Exception
	{
		// given
		final AsSimpleSearchProfileModel searchProfile = modelService.create(AsSimpleSearchProfileModel.class);
		searchProfile.setCatalogVersion(stagedCatalogVersion);
		searchProfile.setCode(CODE1);
		searchProfile.setName(NAME1);
		searchProfile.setIndexType(INDEX_TYPE1);

		// when
		modelService.save(searchProfile);
		catalogSynchronizationService.synchronizeFully(stagedCatalogVersion, onlineCatalogVersion);

		modelService.remove(searchProfile);
		catalogSynchronizationService.synchronizeFully(stagedCatalogVersion, onlineCatalogVersion);

		final Optional<AsSimpleSearchProfileModel> searchProfileOptional = asSearchProfileService
				.getSearchProfileForCode(onlineCatalogVersion, CODE1);

		// then
		assertFalse(searchProfileOptional.isPresent());
	}
}

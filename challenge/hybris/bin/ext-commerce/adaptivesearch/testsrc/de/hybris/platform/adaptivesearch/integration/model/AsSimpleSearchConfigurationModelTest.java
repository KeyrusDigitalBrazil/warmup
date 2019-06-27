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
package de.hybris.platform.adaptivesearch.integration.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.adaptivesearch.model.AsSimpleSearchConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AsSimpleSearchProfileModel;
import de.hybris.platform.adaptivesearch.services.AsConfigurationService;
import de.hybris.platform.adaptivesearch.services.AsSearchConfigurationService;
import de.hybris.platform.adaptivesearch.services.AsSearchProfileService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;

import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.lang.CharEncoding;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@IntegrationTest
public class AsSimpleSearchConfigurationModelTest extends ServicelayerTest
{
	private final static String CATALOG_ID = "hwcatalog";
	private final static String VERSION_STAGED = "Staged";
	private final static String VERSION_ONLINE = "Online";

	private final static String SIMPLE_SEARCH_PROFILE_CODE = "simpleProfile";
	private final static String CAT_AWARE_SEARCH_PROFILE_CODE = "categoryAwareProfile";

	private final static String UID1 = "b413b620-a4b8-4b3c-9234-7fc88c6d3eb1";
	private final static String UID2 = "016090d9-e5d7-4c1f-ad9e-6fb96e36d0c0";

	private final static String CAT_10_CODE = "cat10";
	private final static String CAT_20_CODE = "cat20";

	private static final String FACET_VALUE1 = "FacetValue1";
	private static final String FACET_VALUE2 = "FacetValue2";

	private static final String VALUE1 = "value1";
	private static final Float BOOST1 = Float.valueOf(1.1f);

	private static final String SORT1_CODE = "sort1code";
	private static final String SORT1_NAME = "sort1name";

	private static final String INDEX_PROPERTY1 = "property1";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private CategoryService categoryService;

	@Resource
	private AsSearchProfileService asSearchProfileService;

	@Resource
	private AsSearchConfigurationService asSearchConfigurationService;

	@Resource
	private AsConfigurationService asConfigurationService;

	private CatalogVersionModel onlineCatalogVersion;
	private CatalogVersionModel stagedCatalogVersion;
	private AsSimpleSearchProfileModel simpleSearchProfile;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/adaptivesearch/test/integration/model/asSearchConfigurationModelTest.impex", CharEncoding.UTF_8);

		onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		stagedCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);

		final Optional<AsSimpleSearchProfileModel> simpleSearchProfileOptional = asSearchProfileService
				.getSearchProfileForCode(onlineCatalogVersion, SIMPLE_SEARCH_PROFILE_CODE);
		simpleSearchProfile = simpleSearchProfileOptional.get();
	}

	@Test
	public void createSimpleSearchConfigurationWithoutUid() throws Exception
	{
		// given
		final AsSimpleSearchConfigurationModel searchConfiguration = asConfigurationService
				.createConfiguration(AsSimpleSearchConfigurationModel.class);
		searchConfiguration.setCatalogVersion(onlineCatalogVersion);
		searchConfiguration.setSearchProfile(simpleSearchProfile);

		// when
		asConfigurationService.saveConfiguration(searchConfiguration);

		// then
		assertNotNull(searchConfiguration.getUid());
		assertFalse(searchConfiguration.getUid().isEmpty());
	}

	@Test
	public void createSimpleSearchConfiguration() throws Exception
	{
		// given
		final AsSimpleSearchConfigurationModel searchConfiguration = asConfigurationService
				.createConfiguration(AsSimpleSearchConfigurationModel.class);
		searchConfiguration.setCatalogVersion(onlineCatalogVersion);
		searchConfiguration.setSearchProfile(simpleSearchProfile);
		searchConfiguration.setUid(UID1);

		// when
		asConfigurationService.saveConfiguration(searchConfiguration);
		final Optional<AsSimpleSearchConfigurationModel> createdSearchConfigurationOptional = asSearchConfigurationService
				.getSearchConfigurationForUid(onlineCatalogVersion, UID1);

		// then
		assertTrue(createdSearchConfigurationOptional.isPresent());

		final AsSimpleSearchConfigurationModel createdSearchConfiguration = createdSearchConfigurationOptional.get();
		assertEquals(onlineCatalogVersion, createdSearchConfiguration.getCatalogVersion());
		assertEquals(simpleSearchProfile, createdSearchConfiguration.getSearchProfile());
		assertEquals(UID1, createdSearchConfiguration.getUid());
	}

	@Test
	public void failToCreateSimpleSearchConfigurationWithoutSearchProfile() throws Exception
	{
		// given
		final AsSimpleSearchConfigurationModel searchConfiguration = asConfigurationService
				.createConfiguration(AsSimpleSearchConfigurationModel.class);
		searchConfiguration.setCatalogVersion(onlineCatalogVersion);
		searchConfiguration.setUid(UID1);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(searchConfiguration);
	}

	@Test
	public void failToCreateSimpleSearchConfigurationWithWrongCatalogVersion() throws Exception
	{
		// given
		final AsSimpleSearchConfigurationModel searchConfiguration = asConfigurationService
				.createConfiguration(AsSimpleSearchConfigurationModel.class);
		searchConfiguration.setCatalogVersion(stagedCatalogVersion);
		searchConfiguration.setUid(UID1);
		searchConfiguration.setSearchProfile(simpleSearchProfile);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(searchConfiguration);
	}

	@Test
	public void failToCreateMultipleSimpleSearchConfigurations() throws Exception
	{
		// given
		final AsSimpleSearchConfigurationModel searchConfiguration1 = asConfigurationService
				.createConfiguration(AsSimpleSearchConfigurationModel.class);
		searchConfiguration1.setCatalogVersion(onlineCatalogVersion);
		searchConfiguration1.setSearchProfile(simpleSearchProfile);
		searchConfiguration1.setUid(UID1);

		final AsSimpleSearchConfigurationModel searchConfiguration2 = asConfigurationService
				.createConfiguration(AsSimpleSearchConfigurationModel.class);
		searchConfiguration2.setCatalogVersion(onlineCatalogVersion);
		searchConfiguration2.setSearchProfile(simpleSearchProfile);
		searchConfiguration2.setUid(UID2);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(searchConfiguration1);
		asConfigurationService.saveConfiguration(searchConfiguration2);
	}

	@Test
	public void simpleSearchConfigurationIsNotCorrupted() throws Exception
	{
		// given
		final AsSimpleSearchConfigurationModel searchConfiguration = asConfigurationService
				.createConfiguration(AsSimpleSearchConfigurationModel.class);
		searchConfiguration.setCatalogVersion(onlineCatalogVersion);
		searchConfiguration.setSearchProfile(simpleSearchProfile);
		searchConfiguration.setUid(UID1);

		// when
		final boolean corrupted = searchConfiguration.isCorrupted();

		// then
		assertFalse(corrupted);
	}
}

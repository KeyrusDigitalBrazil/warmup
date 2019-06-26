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
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.adaptivesearch.model.AsCategoryAwareSearchConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AsCategoryAwareSearchProfileModel;
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
public class AsSearchConfigurationModelTest extends ServicelayerTest
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
	private AsCategoryAwareSearchProfileModel categoryAwareSearchProfile;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/adaptivesearch/test/integration/model/asSearchConfigurationModelTest.impex", CharEncoding.UTF_8);

		onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		stagedCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);

		final Optional<AsSimpleSearchProfileModel> simpleSearchProfileOptional = asSearchProfileService
				.getSearchProfileForCode(onlineCatalogVersion, SIMPLE_SEARCH_PROFILE_CODE);
		simpleSearchProfile = simpleSearchProfileOptional.get();

		final Optional<AsCategoryAwareSearchProfileModel> categoryAwareSearchProfileOptional = asSearchProfileService
				.getSearchProfileForCode(onlineCatalogVersion, CAT_AWARE_SEARCH_PROFILE_CODE);
		categoryAwareSearchProfile = categoryAwareSearchProfileOptional.get();
	}

	@Test
	public void createMultipleSearchConfigurations() throws Exception
	{
		// given
		final AsSimpleSearchConfigurationModel simpleSearchConfiguration = asConfigurationService
				.createConfiguration(AsSimpleSearchConfigurationModel.class);
		simpleSearchConfiguration.setCatalogVersion(onlineCatalogVersion);
		simpleSearchConfiguration.setSearchProfile(simpleSearchProfile);
		simpleSearchConfiguration.setUid(UID1);

		final AsCategoryAwareSearchConfigurationModel categoryAwareSearchConfiguration = asConfigurationService
				.createConfiguration(AsCategoryAwareSearchConfigurationModel.class);
		categoryAwareSearchConfiguration.setCatalogVersion(onlineCatalogVersion);
		categoryAwareSearchConfiguration.setSearchProfile(categoryAwareSearchProfile);
		categoryAwareSearchConfiguration.setUid(UID2);

		// when
		asConfigurationService.saveConfiguration(simpleSearchConfiguration);
		asConfigurationService.saveConfiguration(categoryAwareSearchConfiguration);

		final Optional<AsSimpleSearchConfigurationModel> createdSimpleSearchConfigurationOptional = asSearchConfigurationService
				.getSearchConfigurationForUid(onlineCatalogVersion, UID1);
		final Optional<AsCategoryAwareSearchConfigurationModel> categoryAwareSearchConfigurationOptional = asSearchConfigurationService
				.getSearchConfigurationForUid(onlineCatalogVersion, UID2);

		// then
		assertTrue(createdSimpleSearchConfigurationOptional.isPresent());

		final AsSimpleSearchConfigurationModel createdSimpleSearchConfiguration = createdSimpleSearchConfigurationOptional.get();
		assertEquals(onlineCatalogVersion, createdSimpleSearchConfiguration.getCatalogVersion());
		assertEquals(simpleSearchProfile, createdSimpleSearchConfiguration.getSearchProfile());
		assertEquals(UID1, createdSimpleSearchConfiguration.getUid());

		assertTrue(categoryAwareSearchConfigurationOptional.isPresent());

		final AsCategoryAwareSearchConfigurationModel createdCategoryAwareSearchConfiguration = categoryAwareSearchConfigurationOptional
				.get();
		assertEquals(onlineCatalogVersion, createdCategoryAwareSearchConfiguration.getCatalogVersion());
		assertEquals(categoryAwareSearchProfile, createdCategoryAwareSearchConfiguration.getSearchProfile());
		assertEquals(UID2, createdCategoryAwareSearchConfiguration.getUid());
	}

	@Test
	public void failToCreateMultipleSearchConfigurationsWithSameUid() throws Exception
	{
		// given
		final AsSimpleSearchConfigurationModel simpleSearchConfiguration = asConfigurationService
				.createConfiguration(AsSimpleSearchConfigurationModel.class);
		simpleSearchConfiguration.setCatalogVersion(onlineCatalogVersion);
		simpleSearchConfiguration.setSearchProfile(simpleSearchProfile);
		simpleSearchConfiguration.setUid(UID1);

		final AsCategoryAwareSearchConfigurationModel categoryAwareSearchConfiguration = asConfigurationService
				.createConfiguration(AsCategoryAwareSearchConfigurationModel.class);
		categoryAwareSearchConfiguration.setCatalogVersion(onlineCatalogVersion);
		categoryAwareSearchConfiguration.setSearchProfile(categoryAwareSearchProfile);
		categoryAwareSearchConfiguration.setUid(UID1);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(simpleSearchConfiguration);
		asConfigurationService.saveConfiguration(categoryAwareSearchConfiguration);
	}
}

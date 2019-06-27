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
import de.hybris.platform.adaptivesearch.daos.AsSearchProfileActivationSetDao;
import de.hybris.platform.adaptivesearch.model.AsCategoryAwareSearchProfileModel;
import de.hybris.platform.adaptivesearch.model.AsSimpleSearchProfileModel;
import de.hybris.platform.adaptivesearch.services.AsSearchProfileService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.lang.CharEncoding;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@IntegrationTest
public class AsSearchProfileModelTest extends ServicelayerTransactionalTest
{
	private final static String CATALOG_ID = "hwcatalog";
	private final static String VERSION_STAGED = "Staged";
	private final static String VERSION_ONLINE = "Online";

	private final static String UID1 = "b413b620-a4b8-4b3c-9234-7fc88c6d3eb1";

	private final static String CODE1 = "searchProfile1";
	private final static String CODE2 = "searchProfile2";

	private final static String INDEX_TYPE1 = "testIndex1";
	private final static String INDEX_TYPE2 = "testIndex2";

	private final static String CAT_10_CODE = "cat10";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Resource
	private ModelService modelService;

	@Resource
	private CategoryService categoryService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private AsSearchProfileService asSearchProfileService;

	@Resource
	private AsSearchProfileActivationSetDao asSearchProfileActivationSetDao;

	private CatalogVersionModel onlineCatalogVersion;
	private CatalogVersionModel stagedCatalogVersion;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/adaptivesearch/test/integration/model/asSearchProfileModelTest.impex", CharEncoding.UTF_8);

		onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		stagedCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);
	}

	@Test
	public void createMultipleSearchProfiles() throws Exception
	{
		// given
		final AsSimpleSearchProfileModel searchProfile1 = modelService.create(AsSimpleSearchProfileModel.class);
		searchProfile1.setCatalogVersion(onlineCatalogVersion);
		searchProfile1.setCode(CODE1);
		searchProfile1.setIndexType(INDEX_TYPE1);

		final AsCategoryAwareSearchProfileModel searchProfile2 = modelService.create(AsCategoryAwareSearchProfileModel.class);
		searchProfile2.setCatalogVersion(onlineCatalogVersion);
		searchProfile2.setCode(CODE2);
		searchProfile2.setIndexType(INDEX_TYPE2);

		// when
		modelService.save(searchProfile1);
		modelService.save(searchProfile2);

		final Optional<AsSimpleSearchProfileModel> createdSearchProfile1Optional = asSearchProfileService
				.getSearchProfileForCode(onlineCatalogVersion, CODE1);
		final Optional<AsCategoryAwareSearchProfileModel> createdSearchProfile2Optional = asSearchProfileService
				.getSearchProfileForCode(onlineCatalogVersion, CODE2);

		// then
		assertTrue(createdSearchProfile1Optional.isPresent());
		final AsSimpleSearchProfileModel createdSearchProfile1 = createdSearchProfile1Optional.get();
		assertEquals(onlineCatalogVersion, createdSearchProfile1.getCatalogVersion());
		assertEquals(CODE1, createdSearchProfile1.getCode());
		assertEquals(INDEX_TYPE1, createdSearchProfile1.getIndexType());

		assertTrue(createdSearchProfile2Optional.isPresent());
		final AsCategoryAwareSearchProfileModel createdSearchProfile2 = createdSearchProfile2Optional.get();
		assertEquals(onlineCatalogVersion, createdSearchProfile2.getCatalogVersion());
		assertEquals(CODE2, createdSearchProfile2.getCode());
		assertEquals(INDEX_TYPE2, createdSearchProfile2.getIndexType());
	}

	@Test
	public void failToCreateMultipleSearchProfilesWithSameCode() throws Exception
	{
		// given
		final AsCategoryAwareSearchProfileModel searchProfile1 = modelService.create(AsCategoryAwareSearchProfileModel.class);
		searchProfile1.setCatalogVersion(onlineCatalogVersion);
		searchProfile1.setCode(CODE1);
		searchProfile1.setIndexType(INDEX_TYPE1);

		final AsCategoryAwareSearchProfileModel searchProfile2 = modelService.create(AsCategoryAwareSearchProfileModel.class);
		searchProfile2.setCatalogVersion(onlineCatalogVersion);
		searchProfile2.setCode(CODE1);
		searchProfile2.setIndexType(INDEX_TYPE2);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		modelService.save(searchProfile1);
		modelService.save(searchProfile2);
	}

	@Test
	public void createMultipleSearchProfilesWithSameCodeButDifferentCatalogVersions() throws Exception
	{
		// given
		final AsSimpleSearchProfileModel searchProfile1 = modelService.create(AsSimpleSearchProfileModel.class);
		searchProfile1.setCatalogVersion(stagedCatalogVersion);
		searchProfile1.setCode(CODE1);
		searchProfile1.setIndexType(INDEX_TYPE1);

		final AsCategoryAwareSearchProfileModel searchProfile2 = modelService.create(AsCategoryAwareSearchProfileModel.class);
		searchProfile2.setCatalogVersion(onlineCatalogVersion);
		searchProfile2.setCode(CODE1);
		searchProfile2.setIndexType(INDEX_TYPE1);

		// when
		modelService.save(searchProfile1);
		modelService.save(searchProfile2);

		final Optional<AsSimpleSearchProfileModel> createdSearchProfile1Optional = asSearchProfileService
				.getSearchProfileForCode(stagedCatalogVersion, CODE1);
		final Optional<AsCategoryAwareSearchProfileModel> createdSearchProfile2Optional = asSearchProfileService
				.getSearchProfileForCode(onlineCatalogVersion, CODE1);

		// then
		assertTrue(createdSearchProfile1Optional.isPresent());
		final AsSimpleSearchProfileModel createdSearchProfile1 = createdSearchProfile1Optional.get();
		assertEquals(stagedCatalogVersion, createdSearchProfile1.getCatalogVersion());
		assertEquals(CODE1, createdSearchProfile1.getCode());
		assertEquals(INDEX_TYPE1, createdSearchProfile1.getIndexType());

		assertTrue(createdSearchProfile2Optional.isPresent());
		final AsCategoryAwareSearchProfileModel createdSearchProfile2 = createdSearchProfile2Optional.get();
		assertEquals(onlineCatalogVersion, createdSearchProfile2.getCatalogVersion());
		assertEquals(CODE1, createdSearchProfile2.getCode());
		assertEquals(INDEX_TYPE1, createdSearchProfile2.getIndexType());
	}
}

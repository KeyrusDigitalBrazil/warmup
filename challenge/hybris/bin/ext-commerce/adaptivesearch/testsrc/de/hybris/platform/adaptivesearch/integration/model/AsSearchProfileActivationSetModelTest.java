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

import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.adaptivesearch.model.AsSearchProfileActivationSetModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import org.apache.commons.lang.CharEncoding;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@IntegrationTest
public class AsSearchProfileActivationSetModelTest extends ServicelayerTransactionalTest
{
	private final static String CATALOG_ID = "hwcatalog";
	private final static String VERSION_STAGED = "Staged";
	private final static String VERSION_ONLINE = "Online";

	private final static String INDEX_TYPE_1 = "testIndex1";
	private final static String INDEX_TYPE_2 = "testIndex2";
	private final static String WRONG_INDEX_TYPE = "testIndexError";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Resource
	private ModelService modelService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/adaptivesearch/test/integration/model/asSearchProfileActivationSetModelTest.impex", CharEncoding.UTF_8);
	}

	@Test
	public void createActivationSetWithoutCatalogVersion() throws Exception
	{
		// given
		final AsSearchProfileActivationSetModel searchProfileActivationSet = modelService
				.create(AsSearchProfileActivationSetModel.class);
		searchProfileActivationSet.setIndexType(INDEX_TYPE_1);

		// when
		modelService.save(searchProfileActivationSet);

		// then
		assertNotNull(searchProfileActivationSet.getPk());
	}

	@Test
	public void createActivationSetWithCatalogVersion() throws Exception
	{
		// given
		final CatalogVersionModel onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);

		final AsSearchProfileActivationSetModel searchProfileActivationSet = modelService
				.create(AsSearchProfileActivationSetModel.class);
		searchProfileActivationSet.setCatalogVersion(onlineCatalogVersion);
		searchProfileActivationSet.setIndexType(INDEX_TYPE_1);

		// when
		modelService.save(searchProfileActivationSet);

		// then
		assertNotNull(searchProfileActivationSet.getPk());
	}

	@Test
	public void failToCreateActivationSetWithoutIndexType() throws Exception
	{
		// given
		final CatalogVersionModel onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);

		final AsSearchProfileActivationSetModel searchProfileActivationSet = modelService
				.create(AsSearchProfileActivationSetModel.class);
		searchProfileActivationSet.setCatalogVersion(onlineCatalogVersion);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		modelService.save(searchProfileActivationSet);
	}

	@Test
	public void failToCreateActivationSetWithWrongIndexType() throws Exception
	{
		// given
		final CatalogVersionModel onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);

		final AsSearchProfileActivationSetModel searchProfileActivationSet = modelService
				.create(AsSearchProfileActivationSetModel.class);
		searchProfileActivationSet.setCatalogVersion(onlineCatalogVersion);
		searchProfileActivationSet.setIndexType(WRONG_INDEX_TYPE);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		modelService.save(searchProfileActivationSet);
	}

	@Test
	public void createMultipleActivationSetsSameIndexTypeDifferentCatalogVersions() throws Exception
	{
		// given
		final CatalogVersionModel stagedCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);
		final CatalogVersionModel onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);

		final AsSearchProfileActivationSetModel searchProfileActivationSet1 = modelService
				.create(AsSearchProfileActivationSetModel.class);
		searchProfileActivationSet1.setCatalogVersion(stagedCatalogVersion);
		searchProfileActivationSet1.setIndexType(INDEX_TYPE_1);

		final AsSearchProfileActivationSetModel searchProfileActivationSet2 = modelService
				.create(AsSearchProfileActivationSetModel.class);
		searchProfileActivationSet2.setCatalogVersion(onlineCatalogVersion);
		searchProfileActivationSet2.setIndexType(INDEX_TYPE_1);

		// when
		modelService.save(searchProfileActivationSet1);
		modelService.save(searchProfileActivationSet2);

		// then
		assertNotNull(searchProfileActivationSet1.getPk());
		assertNotNull(searchProfileActivationSet2.getPk());
	}

	@Test
	public void createMultipleActivationSetsDifferentIndexTypesSameCatalogVersion() throws Exception
	{
		// given
		final CatalogVersionModel onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);

		final AsSearchProfileActivationSetModel searchProfileActivationSet1 = modelService
				.create(AsSearchProfileActivationSetModel.class);
		searchProfileActivationSet1.setCatalogVersion(onlineCatalogVersion);
		searchProfileActivationSet1.setIndexType(INDEX_TYPE_1);

		final AsSearchProfileActivationSetModel searchProfileActivationSet2 = modelService
				.create(AsSearchProfileActivationSetModel.class);
		searchProfileActivationSet2.setCatalogVersion(onlineCatalogVersion);
		searchProfileActivationSet2.setIndexType(INDEX_TYPE_2);

		// when
		modelService.save(searchProfileActivationSet1);
		modelService.save(searchProfileActivationSet2);

		// then
		assertNotNull(searchProfileActivationSet1.getPk());
		assertNotNull(searchProfileActivationSet2.getPk());
	}

	@Test
	public void failToCreateMultipleActivationSetsSameIndexTypeSameCatalogVersion() throws Exception
	{
		// given
		final CatalogVersionModel onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);

		final AsSearchProfileActivationSetModel searchProfileActivationSet1 = modelService
				.create(AsSearchProfileActivationSetModel.class);
		searchProfileActivationSet1.setCatalogVersion(onlineCatalogVersion);
		searchProfileActivationSet1.setIndexType(INDEX_TYPE_1);

		final AsSearchProfileActivationSetModel searchProfileActivationSet2 = modelService
				.create(AsSearchProfileActivationSetModel.class);
		searchProfileActivationSet2.setCatalogVersion(onlineCatalogVersion);
		searchProfileActivationSet2.setIndexType(INDEX_TYPE_1);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		modelService.save(searchProfileActivationSet1);
		modelService.save(searchProfileActivationSet2);
	}
}

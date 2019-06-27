/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cms2.servicelayer.daos.impl;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.CMSVersionModel;
import de.hybris.platform.cms2.servicelayer.daos.CMSVersionDao;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultCMSVersionDaoIntegrationTest extends ServicelayerTransactionalTest
{
	private static final String VERSION_UID1 = "versionUid1";
	private static final String VERSION_UID2 = "versionUid2";
	private static final String VERSION_UID3 = "versionUid3";

	private static final String ITEM_UID1 = "itemUid1";
	private static final String ITEM_UID2 = "itemUid2";
	private static final String ITEM_UID3 = "itemUid3";

	private static final String TRANSACTION_ID1 = "transactionId1";
	private static final String TRANSACTION_ID2 = "transactionId2";

	private static final String TEST_LABEL = "someLabel";
	private static final String TEST_DESCRIPTION = "someDescription";

	@Resource
	private CMSVersionDao cmsVersionDao;
	@Resource
	private ModelService modelService;
	@Resource
	private CatalogVersionService catalogVersionService;

	private CMSVersionModel cmsVersionModel;

	private CatalogVersionModel itemCatalogVersion;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();

		importCsv("/test/cmsCatalogVersionTestData.csv", "windows-1252");
		itemCatalogVersion = catalogVersionService.getCatalogVersion("cms_Catalog", "CatalogVersion1");

		cmsVersionModel = createCMSVersion(VERSION_UID1, TRANSACTION_ID1, ITEM_UID1, itemCatalogVersion);
	}

	@Test
	public void shouldFindByUid()
	{
		final Optional<CMSVersionModel> fetchedCMSVersionModel = cmsVersionDao.findByUid(VERSION_UID1);

		assertThat(fetchedCMSVersionModel.isPresent(), is(Boolean.TRUE));
		assertThat(fetchedCMSVersionModel.get().getUid(), is(VERSION_UID1));
		assertThat(fetchedCMSVersionModel.get().getTransactionId(), is(TRANSACTION_ID1));
		assertThat(fetchedCMSVersionModel.get().getItemUid(), is(ITEM_UID1));
		assertThat(fetchedCMSVersionModel.get().getItemCatalogVersion(), is(itemCatalogVersion));
		assertThat(fetchedCMSVersionModel.get().getLabel(), is(TEST_LABEL));
		assertThat(fetchedCMSVersionModel.get().getDescription(), is(TEST_DESCRIPTION));
	}

	@Test
	public void shouldFindByItemUidAndLabel()
	{
		final Optional<CMSVersionModel> fetchedCMSVersionModel = cmsVersionDao
				.findByItemUidAndLabel(ITEM_UID1, TEST_LABEL, itemCatalogVersion);

		assertThat(fetchedCMSVersionModel.isPresent(), is(Boolean.TRUE));
		assertThat(fetchedCMSVersionModel.get().getUid(), is(VERSION_UID1));
		assertThat(fetchedCMSVersionModel.get().getTransactionId(), is(TRANSACTION_ID1));
		assertThat(fetchedCMSVersionModel.get().getItemUid(), is(ITEM_UID1));
		assertThat(fetchedCMSVersionModel.get().getItemCatalogVersion(), is(itemCatalogVersion));
		assertThat(fetchedCMSVersionModel.get().getLabel(), is(TEST_LABEL));
		assertThat(fetchedCMSVersionModel.get().getDescription(), is(TEST_DESCRIPTION));
	}

	@Test
	public void shouldFindAllVersionsByItemUidAndCatalogVersion()
	{
		// GIVEN
		createCMSVersion(VERSION_UID2, TRANSACTION_ID2, ITEM_UID1, itemCatalogVersion);

		// WHEN
		List<CMSVersionModel> fetchCMSVersionModels = cmsVersionDao.findAllByItemUidAndItemCatalogVersion(ITEM_UID1,
				itemCatalogVersion);

		// THEN
		assertThat(fetchCMSVersionModels, hasSize(2));

		CMSVersionModel cmsVersionModel0 = fetchCMSVersionModels.stream()
				.filter(e -> e.getUid() == VERSION_UID1)
				.findFirst().get();
		assertThat(cmsVersionModel0.getItemCatalogVersion(), is(itemCatalogVersion));

		CMSVersionModel cmsVersionModel1 = fetchCMSVersionModels.stream()
				.filter(e -> e.getUid() == VERSION_UID2)
				.findFirst().get();
		assertThat(cmsVersionModel1.getItemCatalogVersion(), is(itemCatalogVersion));
	}

	@Test
	public void shouldReturnEmptyListWhenFindAllVersionsByItemUidAndCatalogVersionIsEmpty()
	{
		// WHEN
		List<CMSVersionModel> fetchCMSVersionModels = cmsVersionDao
				.findAllByItemUidAndItemCatalogVersion("Unknown", itemCatalogVersion);

		// THEN
		assertThat(fetchCMSVersionModels.size(), is(0));
	}

	protected CMSVersionModel createCMSVersion(final String uid, final String transactionId, final String itemUid,
			final CatalogVersionModel itemCatalogVersion)
	{
		final CMSVersionModel cmsVersionModel = modelService.create(CMSVersionModel._TYPECODE);
		cmsVersionModel.setUid(uid);
		cmsVersionModel.setTransactionId(transactionId);
		cmsVersionModel.setItemUid(itemUid);
		cmsVersionModel.setItemCatalogVersion(itemCatalogVersion);
		cmsVersionModel.setLabel(TEST_LABEL);
		cmsVersionModel.setDescription(TEST_DESCRIPTION);

		modelService.save(cmsVersionModel);

		return cmsVersionModel;
	}
}

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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cms2.model.CMSVersionModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.ObjectFactory;


@IntegrationTest
public class DefaultCMSVersionGCDaoIntegrationTest extends ServicelayerTransactionalTest
{
	private static final String VERSION_UID1 = "versionUid1";
	private static final String VERSION_UID2 = "versionUid2";
	private static final String VERSION_UID3 = "versionUid3";
	private static final String VERSION_UID4 = "versionUid4";
	private static final String VERSION_UID5 = "versionUid5";

	private static final String ITEM_UID = "itemUid";

	private static final String TRANSACTION_ID1 = "transactionId1";
	private static final String TRANSACTION_ID2 = "transactionId2";
	private static final String TRANSACTION_ID3 = "transactionId3";
	private static final String TRANSACTION_ID4 = "transactionId4";
	private static final String TRANSACTION_ID5 = "transactionId5";

	private static final String TEST_LABEL = "someLabel";

	@Resource
	private DefaultCMSVersionGCDao cmsVersionGCDao;

	@Resource
	private ModelService modelService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private ObjectFactory<PageableData> pageableDataFactory;

	private CMSVersionModel cmsVersionModel1;
	private CMSVersionModel cmsVersionModel2;
	private CMSVersionModel cmsVersionModel3;
	private CMSVersionModel cmsVersionModel4;
	private CMSVersionModel cmsVersionModel5;

	private CatalogVersionModel itemCatalogVersion;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();

		importCsv("/test/cmsCatalogVersionTestData.csv", "windows-1252");
		itemCatalogVersion = catalogVersionService.getCatalogVersion("cms_Catalog", "CatalogVersion1");

		cmsVersionModel1 = createTaggedCMSVersion(VERSION_UID1, TRANSACTION_ID1, false, DateTime.now().toDate());
		cmsVersionModel2 = createTaggedCMSVersion(VERSION_UID2, TRANSACTION_ID2, false, DateTime.now().minusDays(15).toDate());
		cmsVersionModel3 = createTaggedCMSVersion(VERSION_UID3, TRANSACTION_ID3, false, DateTime.now().minusDays(30).toDate());
		cmsVersionModel4 = createTaggedCMSVersion(VERSION_UID4, TRANSACTION_ID4, true, DateTime.now().minusYears(1).toDate());
		cmsVersionModel5 = createCMSVersion(VERSION_UID5, TRANSACTION_ID5, null, false, DateTime.now().minusYears(1).toDate());
	}

	@Test
	public void shouldFindRetainableVersions()
	{
		final List<CMSVersionModel> retainableVersions = cmsVersionGCDao.findRetainableVersions(DateTime.now().minusDays(20).toDate());

		assertThat(retainableVersions, hasSize(3));

		assertTrue(retainableVersions.stream()
				.anyMatch(e -> e.getUid().equals(VERSION_UID1)));

		assertTrue(retainableVersions.stream()
				.anyMatch(e -> e.getUid().equals(VERSION_UID2)));

		assertTrue(retainableVersions.stream()
				.noneMatch(e -> e.getUid().equals(VERSION_UID3)));

		assertTrue(retainableVersions.stream()
				.anyMatch(e -> e.getUid().equals(VERSION_UID4)));

		assertTrue(retainableVersions.stream()
				.noneMatch(e -> e.getUid().equals(VERSION_UID5)));
	}

	@Test
	public void shouldFindOnlyTaggedCMSVersionsOrWithRetainTrueWhenMaxAgeIsNull()
	{
		final List<CMSVersionModel> retainableVersions = cmsVersionGCDao.findRetainableVersions(null);

		assertThat(retainableVersions, hasSize(4));

		assertTrue(retainableVersions.stream()
				.anyMatch(e -> e.getUid().equals(VERSION_UID1)));

		assertTrue(retainableVersions.stream()
				.anyMatch(e -> e.getUid().equals(VERSION_UID2)));

		assertTrue(retainableVersions.stream()
				.anyMatch(e -> e.getUid().equals(VERSION_UID3)));

		assertTrue(retainableVersions.stream()
				.anyMatch(e -> e.getUid().equals(VERSION_UID4)));

		assertTrue(retainableVersions.stream()
				.noneMatch(e -> e.getUid().equals(VERSION_UID5)));
	}

	@Test
	public void shouldFindAllExcludedBy()
	{
		final PageableData pageableData = pageableDataFactory.getObject();
		pageableData.setCurrentPage(0);
		pageableData.setPageSize(5);

		final SearchResult<CMSVersionModel> searchResult = cmsVersionGCDao.findAllExcludedBy(Arrays.asList(cmsVersionModel1, cmsVersionModel2), pageableData);

		assertThat(searchResult.getCount(), equalTo(3));

		assertThat(searchResult.getResult(), hasSize(3));

		assertTrue(searchResult.getResult().stream()
				.noneMatch(e -> e.getUid().equals(VERSION_UID1)));

		assertTrue(searchResult.getResult().stream()
				.noneMatch(e -> e.getUid().equals(VERSION_UID2)));

		assertTrue(searchResult.getResult().stream()
				.anyMatch(e -> e.getUid().equals(VERSION_UID3)));

		assertTrue(searchResult.getResult().stream()
				.anyMatch(e -> e.getUid().equals(VERSION_UID4)));

		assertTrue(searchResult.getResult().stream()
				.anyMatch(e -> e.getUid().equals(VERSION_UID5)));
	}

	@Test
	public void shouldFindAllWhenExcludedByIsEmpty()
	{
		final PageableData pageableData = pageableDataFactory.getObject();
		pageableData.setCurrentPage(0);
		pageableData.setPageSize(5);

		final SearchResult<CMSVersionModel> searchResult = cmsVersionGCDao.findAllExcludedBy(Collections.emptyList(), pageableData);

		assertThat(searchResult.getCount(), equalTo(5));

		assertThat(searchResult.getResult(), hasSize(5));

		assertTrue(searchResult.getResult().stream()
				.anyMatch(e -> e.getUid().equals(VERSION_UID1)));

		assertTrue(searchResult.getResult().stream()
				.anyMatch(e -> e.getUid().equals(VERSION_UID2)));

		assertTrue(searchResult.getResult().stream()
				.anyMatch(e -> e.getUid().equals(VERSION_UID3)));

		assertTrue(searchResult.getResult().stream()
				.anyMatch(e -> e.getUid().equals(VERSION_UID4)));

		assertTrue(searchResult.getResult().stream()
				.anyMatch(e -> e.getUid().equals(VERSION_UID5)));
	}

	protected CMSVersionModel createTaggedCMSVersion(final String uid, final String transactionId, final boolean retain, final Date creationTime)
	{
		return createCMSVersion(uid, transactionId, TEST_LABEL, retain, creationTime);
	}

	protected CMSVersionModel createCMSVersion(final String uid, final String transactionId, final String label, final boolean retain, final Date creationTime)
	{
		final CMSVersionModel cmsVersionModel = modelService.create(CMSVersionModel._TYPECODE);
		cmsVersionModel.setUid(uid);
		cmsVersionModel.setTransactionId(transactionId);
		cmsVersionModel.setItemCatalogVersion(itemCatalogVersion);
		cmsVersionModel.setItemUid(ITEM_UID);
		cmsVersionModel.setLabel(label);
		cmsVersionModel.setRetain(retain);
		cmsVersionModel.setCreationtime(creationTime);

		modelService.save(cmsVersionModel);

		return cmsVersionModel;
	}
}

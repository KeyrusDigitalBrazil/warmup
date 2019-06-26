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
package de.hybris.platform.solrfacetsearch.integration;

import static org.junit.Assert.assertEquals;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.solrfacetsearch.search.Document;
import de.hybris.platform.solrfacetsearch.search.OrderField.SortOrder;
import de.hybris.platform.solrfacetsearch.search.SearchResult;

import java.util.Arrays;
import java.util.Collections;

import javax.annotation.Resource;

import org.junit.Test;


public class SearchQueryCatalogVersionsTest extends AbstractSearchQueryTest
{
	@Resource
	private CatalogVersionService catalogVersionService;

	@Override
	protected void loadData() throws Exception
	{
		importConfig("/test/integration/SearchQueryCatalogVersionsTest.csv");
	}

	@Test
	public void searchOnCatalogVersion() throws Exception
	{
		// given
		final CatalogVersionModel onlineCatalogVersion = catalogVersionService.getCatalogVersion(HW_CATALOG + getTestId(),
				ONLINE_CATALOG_VERSION + getTestId());

		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.setCatalogVersions(Arrays.asList(onlineCatalogVersion));
		});

		// then
		assertEquals(1, searchResult.getNumberOfResults());

		final Document document = searchResult.getDocuments().get(0);
		assertDocumentField(PRODUCT1_CODE, document, PRODUCT_CODE_FIELD);
		assertDocumentField(PRODUCT1_NAME, document, PRODUCT_NAME_FIELD);
	}

	@Test
	public void searchOnMultipleCatalogVersions() throws Exception
	{
		// given
		final CatalogVersionModel onlineCatalogVersion = catalogVersionService.getCatalogVersion(HW_CATALOG + getTestId(),
				ONLINE_CATALOG_VERSION + getTestId());
		final CatalogVersionModel stagedCatalogVersion = catalogVersionService.getCatalogVersion(HW_CATALOG + getTestId(),
				STAGED_CATALOG_VERSION + getTestId());

		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addSort(PRODUCT_CODE_FIELD, SortOrder.ASCENDING);
			searchQuery.setCatalogVersions(Arrays.asList(onlineCatalogVersion, stagedCatalogVersion));
		});

		// then
		assertEquals(2, searchResult.getNumberOfResults());

		final Document document1 = searchResult.getDocuments().get(0);
		assertDocumentField(PRODUCT1_CODE, document1, PRODUCT_CODE_FIELD);
		assertDocumentField(PRODUCT1_NAME, document1, PRODUCT_NAME_FIELD);

		final Document document2 = searchResult.getDocuments().get(1);
		assertDocumentField(PRODUCT2_CODE, document2, PRODUCT_CODE_FIELD);
		assertDocumentField(PRODUCT2_NAME, document2, PRODUCT_NAME_FIELD);
	}

	@Test
	public void searchOnCatalogVersionFromSession() throws Exception
	{
		// given
		final CatalogVersionModel onlineCatalogVersion = catalogVersionService.getCatalogVersion(HW_CATALOG + getTestId(),
				ONLINE_CATALOG_VERSION + getTestId());

		// when
		catalogVersionService.setSessionCatalogVersions(Arrays.asList(onlineCatalogVersion));
		final SearchResult searchResult = executeSearchQuery();

		// then
		assertEquals(1, searchResult.getNumberOfResults());

		final Document document = searchResult.getDocuments().get(0);
		assertDocumentField(PRODUCT1_CODE, document, PRODUCT_CODE_FIELD);
		assertDocumentField(PRODUCT1_NAME, document, PRODUCT_NAME_FIELD);
	}

	@Test
	public void searchOnMultipleCatalogVersionsFromSession() throws Exception
	{
		// given
		final CatalogVersionModel onlineCatalogVersion = catalogVersionService.getCatalogVersion(HW_CATALOG + getTestId(),
				ONLINE_CATALOG_VERSION + getTestId());
		final CatalogVersionModel stagedCatalogVersion = catalogVersionService.getCatalogVersion(HW_CATALOG + getTestId(),
				STAGED_CATALOG_VERSION + getTestId());

		// when
		catalogVersionService.setSessionCatalogVersions(Arrays.asList(onlineCatalogVersion, stagedCatalogVersion));
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addSort(PRODUCT_CODE_FIELD, SortOrder.ASCENDING);
		});

		// then
		assertEquals(2, searchResult.getNumberOfResults());

		final Document document1 = searchResult.getDocuments().get(0);
		assertDocumentField(PRODUCT1_CODE, document1, PRODUCT_CODE_FIELD);
		assertDocumentField(PRODUCT1_NAME, document1, PRODUCT_NAME_FIELD);

		final Document document2 = searchResult.getDocuments().get(1);
		assertDocumentField(PRODUCT2_CODE, document2, PRODUCT_CODE_FIELD);
		assertDocumentField(PRODUCT2_NAME, document2, PRODUCT_NAME_FIELD);
	}

	@Test
	public void searchOnNotIndexedCatalogVersion() throws Exception
	{
		// given
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(HW_CATALOG + getTestId(),
				"Test" + getTestId());

		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.setCatalogVersions(Collections.singletonList(catalogVersion));
		});

		// then
		assertEquals(0, searchResult.getNumberOfResults());
	}

	@Test
	public void searchOnCatalogVersionWithEscaping() throws Exception
	{
		// given
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(HW_CATALOG + getTestId() + ", 2",
				STAGED_CATALOG_VERSION + getTestId() + ", 2");

		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.setCatalogVersions(Collections.singletonList(catalogVersion));
		});

		// then
		assertEquals(1, searchResult.getNumberOfResults());

		final Document document = searchResult.getDocuments().get(0);
		assertDocumentField(PRODUCT2_CODE, document, PRODUCT_CODE_FIELD);
		assertDocumentField(PRODUCT2_NAME + ", 2", document, PRODUCT_NAME_FIELD);
	}

	@Test
	public void searchOnCatalogVersionForLegacyMode() throws Exception
	{
		// given
		enabledSearchLegacyMode();

		final CatalogVersionModel onlineCatalogVersion = catalogVersionService.getCatalogVersion(HW_CATALOG + getTestId(),
				ONLINE_CATALOG_VERSION + getTestId());

		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.setCatalogVersions(Arrays.asList(onlineCatalogVersion));
		});

		// then
		assertEquals(1, searchResult.getNumberOfResults());

		final Document document = searchResult.getDocuments().get(0);
		assertDocumentField(PRODUCT1_CODE, document, PRODUCT_CODE_FIELD);
		assertDocumentField(PRODUCT1_NAME, document, PRODUCT_NAME_FIELD);
	}

	@Test
	public void searchOnMultipleCatalogVersionsForLegacyMode() throws Exception
	{
		// given
		enabledSearchLegacyMode();

		final CatalogVersionModel onlineCatalogVersion = catalogVersionService.getCatalogVersion(HW_CATALOG + getTestId(),
				ONLINE_CATALOG_VERSION + getTestId());
		final CatalogVersionModel stagedCatalogVersion = catalogVersionService.getCatalogVersion(HW_CATALOG + getTestId(),
				STAGED_CATALOG_VERSION + getTestId());

		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addSort(PRODUCT_CODE_FIELD, SortOrder.ASCENDING);
			searchQuery.setCatalogVersions(Arrays.asList(onlineCatalogVersion, stagedCatalogVersion));
		});

		// then
		assertEquals(2, searchResult.getNumberOfResults());

		final Document document1 = searchResult.getDocuments().get(0);
		assertDocumentField(PRODUCT1_CODE, document1, PRODUCT_CODE_FIELD);
		assertDocumentField(PRODUCT1_NAME, document1, PRODUCT_NAME_FIELD);

		final Document document2 = searchResult.getDocuments().get(1);
		assertDocumentField(PRODUCT2_CODE, document2, PRODUCT_CODE_FIELD);
		assertDocumentField(PRODUCT2_NAME, document2, PRODUCT_NAME_FIELD);
	}

	@Test
	public void searchOnCatalogVersionFromSessionForLegacyMode() throws Exception
	{
		// given
		enabledSearchLegacyMode();

		final CatalogVersionModel onlineCatalogVersion = catalogVersionService.getCatalogVersion(HW_CATALOG + getTestId(),
				ONLINE_CATALOG_VERSION + getTestId());

		// when
		catalogVersionService.setSessionCatalogVersions(Arrays.asList(onlineCatalogVersion));
		final SearchResult searchResult = executeSearchQuery();

		// then
		assertEquals(1, searchResult.getNumberOfResults());

		final Document document = searchResult.getDocuments().get(0);
		assertDocumentField(PRODUCT1_CODE, document, PRODUCT_CODE_FIELD);
		assertDocumentField(PRODUCT1_NAME, document, PRODUCT_NAME_FIELD);
	}

	@Test
	public void searchOnMultipleCatalogVersionsFromSessionForLegacyMode() throws Exception
	{
		// given
		enabledSearchLegacyMode();

		final CatalogVersionModel onlineCatalogVersion = catalogVersionService.getCatalogVersion(HW_CATALOG + getTestId(),
				ONLINE_CATALOG_VERSION + getTestId());
		final CatalogVersionModel stagedCatalogVersion = catalogVersionService.getCatalogVersion(HW_CATALOG + getTestId(),
				STAGED_CATALOG_VERSION + getTestId());

		// when
		catalogVersionService.setSessionCatalogVersions(Arrays.asList(onlineCatalogVersion, stagedCatalogVersion));
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addSort(PRODUCT_CODE_FIELD, SortOrder.ASCENDING);
		});

		// then
		assertEquals(2, searchResult.getNumberOfResults());

		final Document document1 = searchResult.getDocuments().get(0);
		assertDocumentField(PRODUCT1_CODE, document1, PRODUCT_CODE_FIELD);
		assertDocumentField(PRODUCT1_NAME, document1, PRODUCT_NAME_FIELD);

		final Document document2 = searchResult.getDocuments().get(1);
		assertDocumentField(PRODUCT2_CODE, document2, PRODUCT_CODE_FIELD);
		assertDocumentField(PRODUCT2_NAME, document2, PRODUCT_NAME_FIELD);
	}

	@Test
	public void searchOnNotIndexedCatalogVersionForLegacyMode() throws Exception
	{
		// given
		enabledSearchLegacyMode();

		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(HW_CATALOG + getTestId(),
				"Test" + getTestId());

		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.setCatalogVersions(Collections.singletonList(catalogVersion));
		});

		// then
		assertEquals(0, searchResult.getNumberOfResults());
	}

	@Test
	public void searchOnCatalogVersionWithEscapingForLegacyMode() throws Exception
	{
		// given
		enabledSearchLegacyMode();

		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(HW_CATALOG + getTestId() + ", 2",
				STAGED_CATALOG_VERSION + getTestId() + ", 2");

		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.setCatalogVersions(Collections.singletonList(catalogVersion));
		});

		// then
		assertEquals(1, searchResult.getNumberOfResults());

		final Document document = searchResult.getDocuments().get(0);
		assertDocumentField(PRODUCT2_CODE, document, PRODUCT_CODE_FIELD);
		assertDocumentField(PRODUCT2_NAME + ", 2", document, PRODUCT_NAME_FIELD);
	}
}

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

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.solrfacetsearch.config.FacetType;
import de.hybris.platform.solrfacetsearch.search.Document;
import de.hybris.platform.solrfacetsearch.search.Facet;
import de.hybris.platform.solrfacetsearch.search.FacetValue;
import de.hybris.platform.solrfacetsearch.search.SearchResult;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;


public class SearchQueryFacetsTest extends AbstractSearchQueryTest
{
	@Override
	protected void loadData() throws Exception
	{
		importConfig("/test/integration/SearchQueryFacetsTest.csv");
	}

	@Test
	public void addFacet() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addSort(PRODUCT_CODE_FIELD);
			searchQuery.addFacet(PRODUCT_APPROVAL_STATUS_FIELD, FacetType.REFINE);
		});

		// then
		assertEquals(4, searchResult.getNumberOfResults());

		final Facet facet = searchResult.getFacet(PRODUCT_APPROVAL_STATUS_FIELD);
		assertNotNull(facet);
		assertNotNull(facet.getFacetValues());
		assertEquals(3, facet.getFacetValues().size());

		final List<String> facetValues = facet.getFacetValues().stream().map(FacetValue::getName).collect(Collectors.toList());
		assertThat(facetValues, contains(ArticleApprovalStatus.APPROVED.getCode(), ArticleApprovalStatus.CHECK.getCode(),
				ArticleApprovalStatus.UNAPPROVED.getCode()));
	}

	@Test
	public void addMultipleFacets() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addSort(PRODUCT_CODE_FIELD);
			searchQuery.addFacet(PRODUCT_APPROVAL_STATUS_FIELD, FacetType.REFINE);
			searchQuery.addFacet(PRODUCT_CODE_FIELD, FacetType.REFINE);
		});

		// then
		assertEquals(4, searchResult.getNumberOfResults());

		final Facet facet = searchResult.getFacet(PRODUCT_APPROVAL_STATUS_FIELD);
		assertNotNull(facet);
		assertNotNull(facet.getFacetValues());
		assertEquals(3, facet.getFacetValues().size());

		final List<String> facetValues = facet.getFacetValues().stream().map(FacetValue::getName).collect(Collectors.toList());
		assertThat(facetValues, contains(ArticleApprovalStatus.APPROVED.getCode(), ArticleApprovalStatus.CHECK.getCode(),
				ArticleApprovalStatus.UNAPPROVED.getCode()));

		final Facet facet2 = searchResult.getFacet(PRODUCT_CODE_FIELD);
		assertNotNull(facet2);
		assertNotNull(facet2.getFacetValues());
		assertEquals(2, facet2.getFacetValues().size());

		final List<String> facetValues2 = facet2.getFacetValues().stream().map(FacetValue::getName).collect(Collectors.toList());
		assertThat(facetValues2, contains(PRODUCT1_CODE, PRODUCT2_CODE));
	}

	@Test
	public void addFacetValue() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addSort(PRODUCT_CODE_FIELD);
			searchQuery.addFacet(PRODUCT_APPROVAL_STATUS_FIELD, FacetType.REFINE);
			searchQuery.addFacetValue(PRODUCT_APPROVAL_STATUS_FIELD, ArticleApprovalStatus.APPROVED.getCode());
		});

		// then
		assertEquals(2, searchResult.getNumberOfResults());

		final Document document1 = searchResult.getDocuments().get(0);
		assertDocumentField(PRODUCT1_CODE, document1, PRODUCT_CODE_FIELD);
		assertDocumentField(PRODUCT1_NAME, document1, PRODUCT_NAME_FIELD);
		assertDocumentField(ArticleApprovalStatus.APPROVED.getCode(), document1, PRODUCT_APPROVAL_STATUS_FIELD);

		final Document document2 = searchResult.getDocuments().get(1);
		assertDocumentField(PRODUCT2_CODE, document2, PRODUCT_CODE_FIELD);
		assertDocumentField(PRODUCT2_NAME, document2, PRODUCT_NAME_FIELD);
		assertDocumentField(ArticleApprovalStatus.APPROVED.getCode(), document2, PRODUCT_APPROVAL_STATUS_FIELD);

		final Facet facet = searchResult.getFacet(PRODUCT_APPROVAL_STATUS_FIELD);
		assertNotNull(facet);
		assertNotNull(facet.getFacetValues());
	}

	@Test
	public void addMultipleFacetValues() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addSort(PRODUCT_CODE_FIELD);
			searchQuery.addFacet(PRODUCT_APPROVAL_STATUS_FIELD, FacetType.REFINE);
			searchQuery.addFacetValue(PRODUCT_APPROVAL_STATUS_FIELD, ArticleApprovalStatus.APPROVED.getCode());
			searchQuery.addFacet(PRODUCT_CODE_FIELD, FacetType.REFINE);
			searchQuery.addFacetValue(PRODUCT_CODE_FIELD, PRODUCT2_CODE);
		});

		// then
		assertEquals(1, searchResult.getNumberOfResults());

		final Document document = searchResult.getDocuments().get(0);
		assertDocumentField(PRODUCT2_CODE, document, PRODUCT_CODE_FIELD);
		assertDocumentField(PRODUCT2_NAME, document, PRODUCT_NAME_FIELD);
		assertDocumentField(ArticleApprovalStatus.APPROVED.getCode(), document, PRODUCT_APPROVAL_STATUS_FIELD);

		final Facet facet1 = searchResult.getFacet(PRODUCT_APPROVAL_STATUS_FIELD);
		assertNotNull(facet1);
		assertNotNull(facet1.getFacetValues());

		final Facet facet2 = searchResult.getFacet(PRODUCT_CODE_FIELD);
		assertNotNull(facet2);
		assertNotNull(facet2.getFacetValues());
	}

	@Test
	public void addFacetWithEscaping() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addSort(PRODUCT_CODE_FIELD);
			searchQuery.addFacet(PRODUCT_NAME_WITH_RESERVED_CHARS_FIELD, FacetType.REFINE);
		});

		// then
		assertEquals(4, searchResult.getNumberOfResults());

		final Facet facet = searchResult.getFacet(PRODUCT_NAME_WITH_RESERVED_CHARS_FIELD);
		assertNotNull(facet);
		assertNotNull(facet.getFacetValues());
		assertEquals(2, facet.getFacetValues().size());

		final List<String> facetValues = facet.getFacetValues().stream().map(FacetValue::getName).collect(Collectors.toList());
		assertThat(facetValues, contains(PRODUCT1_NAME, PRODUCT2_NAME));
	}

	@Test
	public void addFacetValueWithEscaping() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addSort(PRODUCT_CODE_FIELD);
			searchQuery.addSort(PRODUCT_APPROVAL_STATUS_FIELD);
			searchQuery.addFacet(PRODUCT_NAME_WITH_RESERVED_CHARS_FIELD, FacetType.REFINE);
			searchQuery.addFacetValue(PRODUCT_NAME_WITH_RESERVED_CHARS_FIELD, PRODUCT1_NAME);
		});

		// then
		assertEquals(2, searchResult.getNumberOfResults());

		final Document document1 = searchResult.getDocuments().get(0);
		assertDocumentField(PRODUCT1_CODE, document1, PRODUCT_CODE_FIELD);
		assertDocumentField(PRODUCT1_NAME, document1, PRODUCT_NAME_FIELD);
		assertDocumentField(ArticleApprovalStatus.APPROVED.getCode(), document1, PRODUCT_APPROVAL_STATUS_FIELD);

		final Document document2 = searchResult.getDocuments().get(1);
		assertDocumentField(PRODUCT1_CODE, document2, PRODUCT_CODE_FIELD);
		assertDocumentField(PRODUCT1_NAME, document2, PRODUCT_NAME_FIELD);
		assertDocumentField(ArticleApprovalStatus.CHECK.getCode(), document2, PRODUCT_APPROVAL_STATUS_FIELD);

		final Facet facet = searchResult.getFacet(PRODUCT_NAME_WITH_RESERVED_CHARS_FIELD);
		assertNotNull(facet);
		assertNotNull(facet.getFacetValues());
	}

	@Test
	public void addFacetForLegacyMode() throws Exception
	{
		// given
		enabledSearchLegacyMode();

		// when
		importConfig("/test/integration/SearchQueryFacetsTest_enableFacetForApprovalStatusField.csv");

		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addSort(PRODUCT_CODE_FIELD);
		});

		// then
		assertEquals(4, searchResult.getNumberOfResults());

		final Facet facet = searchResult.getFacet(PRODUCT_APPROVAL_STATUS_FIELD);
		assertNotNull(facet);
		assertNotNull(facet.getFacetValues());
		assertEquals(3, facet.getFacetValues().size());

		final List<String> facetValues = facet.getFacetValues().stream().map(FacetValue::getName).collect(Collectors.toList());
		assertThat(facetValues, contains(ArticleApprovalStatus.APPROVED.getCode(), ArticleApprovalStatus.CHECK.getCode(),
				ArticleApprovalStatus.UNAPPROVED.getCode()));
	}

	@Test
	public void addMultipleFacetsForLegacyMode() throws Exception
	{
		// given
		enabledSearchLegacyMode();

		// when
		importConfig("/test/integration/SearchQueryFacetsTest_enableFacetForApprovalStatusField.csv");
		importConfig("/test/integration/SearchQueryFacetsTest_enableFacetForCodeField.csv");

		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addSort(PRODUCT_CODE_FIELD);
		});

		// then
		assertEquals(4, searchResult.getNumberOfResults());

		final Facet facet = searchResult.getFacet(PRODUCT_APPROVAL_STATUS_FIELD);
		assertNotNull(facet);
		assertNotNull(facet.getFacetValues());
		assertEquals(3, facet.getFacetValues().size());

		final List<String> facetValues = facet.getFacetValues().stream().map(FacetValue::getName).collect(Collectors.toList());
		assertThat(facetValues, contains(ArticleApprovalStatus.APPROVED.getCode(), ArticleApprovalStatus.CHECK.getCode(),
				ArticleApprovalStatus.UNAPPROVED.getCode()));

		final Facet facet2 = searchResult.getFacet(PRODUCT_CODE_FIELD);
		assertNotNull(facet2);
		assertNotNull(facet2.getFacetValues());
		assertEquals(2, facet2.getFacetValues().size());

		final List<String> facetValues2 = facet2.getFacetValues().stream().map(FacetValue::getName).collect(Collectors.toList());
		assertThat(facetValues2, contains(PRODUCT1_CODE, PRODUCT2_CODE));
	}

	@Test
	public void addFacetValueForLegacyMode() throws Exception
	{
		// given
		enabledSearchLegacyMode();

		// when
		importConfig("/test/integration/SearchQueryFacetsTest_enableFacetForApprovalStatusField.csv");

		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addSort(PRODUCT_CODE_FIELD);
			searchQuery.addFacetValue(PRODUCT_APPROVAL_STATUS_FIELD, ArticleApprovalStatus.APPROVED.getCode());
		});

		// then
		assertEquals(2, searchResult.getNumberOfResults());

		final Document document1 = searchResult.getDocuments().get(0);
		assertDocumentField(PRODUCT1_CODE, document1, PRODUCT_CODE_FIELD);
		assertDocumentField(PRODUCT1_NAME, document1, PRODUCT_NAME_FIELD);
		assertDocumentField(ArticleApprovalStatus.APPROVED.getCode(), document1, PRODUCT_APPROVAL_STATUS_FIELD);

		final Document document2 = searchResult.getDocuments().get(1);
		assertDocumentField(PRODUCT2_CODE, document2, PRODUCT_CODE_FIELD);
		assertDocumentField(PRODUCT2_NAME, document2, PRODUCT_NAME_FIELD);
		assertDocumentField(ArticleApprovalStatus.APPROVED.getCode(), document2, PRODUCT_APPROVAL_STATUS_FIELD);

		final Facet facet = searchResult.getFacet(PRODUCT_APPROVAL_STATUS_FIELD);
		assertNotNull(facet);
		assertNotNull(facet.getFacetValues());
	}

	@Test
	public void addMultipleFacetValuesForLegacyMode() throws Exception
	{
		// given
		enabledSearchLegacyMode();

		// when
		importConfig("/test/integration/SearchQueryFacetsTest_enableFacetForApprovalStatusField.csv");
		importConfig("/test/integration/SearchQueryFacetsTest_enableFacetForCodeField.csv");

		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addSort(PRODUCT_CODE_FIELD);
			searchQuery.addFacetValue(PRODUCT_APPROVAL_STATUS_FIELD, ArticleApprovalStatus.APPROVED.getCode());
			searchQuery.addFacetValue(PRODUCT_CODE_FIELD, PRODUCT2_CODE);
		});

		// then
		assertEquals(1, searchResult.getNumberOfResults());

		final Document document = searchResult.getDocuments().get(0);
		assertDocumentField(PRODUCT2_CODE, document, PRODUCT_CODE_FIELD);
		assertDocumentField(PRODUCT2_NAME, document, PRODUCT_NAME_FIELD);
		assertDocumentField(ArticleApprovalStatus.APPROVED.getCode(), document, PRODUCT_APPROVAL_STATUS_FIELD);

		final Facet facet1 = searchResult.getFacet(PRODUCT_APPROVAL_STATUS_FIELD);
		assertNotNull(facet1);
		assertNotNull(facet1.getFacetValues());

		final Facet facet2 = searchResult.getFacet(PRODUCT_CODE_FIELD);
		assertNotNull(facet2);
		assertNotNull(facet2.getFacetValues());
	}

	@Test
	public void addFacetWithEscapingForLegacyMode() throws Exception
	{
		// given
		enabledSearchLegacyMode();

		// when
		importConfig("/test/integration/SearchQueryFacetsTest_enableFacetForNameField.csv");

		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addSort(PRODUCT_CODE_FIELD);
		});

		// then
		assertEquals(4, searchResult.getNumberOfResults());

		final Facet facet = searchResult.getFacet(PRODUCT_NAME_WITH_RESERVED_CHARS_FIELD);
		assertNotNull(facet);
		assertNotNull(facet.getFacetValues());
		assertEquals(2, facet.getFacetValues().size());

		final List<String> facetValues = facet.getFacetValues().stream().map(FacetValue::getName).collect(Collectors.toList());
		assertThat(facetValues, contains(PRODUCT1_NAME, PRODUCT2_NAME));
	}

	@Test
	public void addFacetValueWithEscapingForLegacyMode() throws Exception
	{
		// given
		enabledSearchLegacyMode();

		// when
		importConfig("/test/integration/SearchQueryFacetsTest_enableFacetForNameField.csv");

		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addSort(PRODUCT_CODE_FIELD);
			searchQuery.addSort(PRODUCT_APPROVAL_STATUS_FIELD);
			searchQuery.addFacetValue(PRODUCT_NAME_WITH_RESERVED_CHARS_FIELD, PRODUCT1_NAME);
		});

		// then
		assertEquals(2, searchResult.getNumberOfResults());

		final Document document1 = searchResult.getDocuments().get(0);
		assertDocumentField(PRODUCT1_CODE, document1, PRODUCT_CODE_FIELD);
		assertDocumentField(PRODUCT1_NAME, document1, PRODUCT_NAME_FIELD);
		assertDocumentField(ArticleApprovalStatus.APPROVED.getCode(), document1, PRODUCT_APPROVAL_STATUS_FIELD);

		final Document document2 = searchResult.getDocuments().get(1);
		assertDocumentField(PRODUCT1_CODE, document2, PRODUCT_CODE_FIELD);
		assertDocumentField(PRODUCT1_NAME, document2, PRODUCT_NAME_FIELD);
		assertDocumentField(ArticleApprovalStatus.CHECK.getCode(), document2, PRODUCT_APPROVAL_STATUS_FIELD);

		final Facet facet = searchResult.getFacet(PRODUCT_NAME_WITH_RESERVED_CHARS_FIELD);
		assertNotNull(facet);
		assertNotNull(facet.getFacetValues());
	}
}

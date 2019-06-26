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
package de.hybris.platform.accountsummaryaddon.document.service.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.accountsummaryaddon.document.AccountSummaryDocumentQuery;
import de.hybris.platform.accountsummaryaddon.document.B2BDocumentQueryBuilder;
import de.hybris.platform.accountsummaryaddon.document.criteria.DefaultCriteria;
import de.hybris.platform.accountsummaryaddon.document.criteria.FilterByCriteriaData;
import de.hybris.platform.accountsummaryaddon.document.dao.B2BDocumentDao;
import de.hybris.platform.accountsummaryaddon.document.dao.PagedB2BDocumentDao;
import de.hybris.platform.accountsummaryaddon.model.B2BDocumentModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.impl.SearchResultImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


public class DefaultB2BDocumentServiceMockTest
{
	private static final int NUMBER_OF_DOCUMENTS = 25;
	private static final int DEFAULT_PAGE_SIZE = 10;

	private DefaultB2BDocumentService defaultB2BDocumentService;

	private List<B2BDocumentModel> b2bDocumentModels;

	private List<DefaultCriteria> criteriaList;

	private PageableData pageSize10FirstPage;
	private PageableData pageSize10SecondPage;
	private PageableData pageSize10ThirdPage;

	private DefaultCriteria filterByCriteria;

	@Mock
	private PagedB2BDocumentDao pagedB2BDocumentDao;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		b2bDocumentModels = createDocumentResults();

		pageSize10FirstPage = createPageableData(DEFAULT_PAGE_SIZE, 0);
		pageSize10SecondPage = createPageableData(DEFAULT_PAGE_SIZE, 1);
		pageSize10ThirdPage = createPageableData(DEFAULT_PAGE_SIZE, 2);

		filterByCriteria = new DefaultCriteria(StringUtils.EMPTY);
		final FilterByCriteriaData filterByCriteriaData = new FilterByCriteriaData();
		filterByCriteriaData.setDocumentStatus("OPEN");
		filterByCriteria.setCriteriaValues(filterByCriteriaData);
		criteriaList = new ArrayList<DefaultCriteria>();
		criteriaList.add(filterByCriteria);

		// set up pagedB2BDocumentDao mock
		BDDMockito.given(pagedB2BDocumentDao.getPagedDocumentsForUnit("test-doc-0", pageSize10FirstPage, criteriaList)).willReturn(
				createSearchPageData(pageSize10FirstPage));
		BDDMockito.given(pagedB2BDocumentDao.getPagedDocumentsForUnit("test-doc-0", pageSize10SecondPage, criteriaList))
				.willReturn(createSearchPageData(pageSize10SecondPage));
		BDDMockito.given(pagedB2BDocumentDao.getPagedDocumentsForUnit("test-doc-0", pageSize10ThirdPage, criteriaList)).willReturn(
				createSearchPageData(pageSize10ThirdPage));

		defaultB2BDocumentService = new DefaultB2BDocumentService();
		defaultB2BDocumentService.setPagedB2BDocumentDao(pagedB2BDocumentDao);
	}

	@Test
	public void shouldReturnPagedDocumentResult()
	{

		final AccountSummaryDocumentQuery query = new B2BDocumentQueryBuilder(0, 10, B2BDocumentModel.AMOUNT, true).build();
		final B2BDocumentModel document = mock(B2BDocumentModel.class);
		final PagedB2BDocumentDao pagedB2BDocumentDao = mock(PagedB2BDocumentDao.class);
		final SearchPageData<B2BDocumentModel> result = new SearchPageData<B2BDocumentModel>();
		result.setResults(Arrays.asList(document));

		when(pagedB2BDocumentDao.findDocuments(Mockito.any(AccountSummaryDocumentQuery.class))).thenReturn(result);
		when(document.getDocumentNumber()).thenReturn("PUR-001");

		final DefaultB2BDocumentService defaultB2BDocumentService = new DefaultB2BDocumentService();
		defaultB2BDocumentService.setPagedB2BDocumentDao(pagedB2BDocumentDao);

		final SearchPageData<B2BDocumentModel> finalResult = defaultB2BDocumentService.findDocuments(query);

		TestCase.assertEquals(1, finalResult.getResults().size());
		TestCase.assertEquals("PUR-001", finalResult.getResults().get(0).getDocumentNumber());

		verify(pagedB2BDocumentDao, Mockito.times(1)).findDocuments(query);
	}

	@Test
	public void shouldResultOpenDocuments()
	{
		final B2BDocumentModel document = mock(B2BDocumentModel.class);
		final SearchResult<B2BDocumentModel> value = new SearchResultImpl<>(Arrays.asList(document), 1, 1, 1);

		final B2BUnitModel unit = mock(B2BUnitModel.class);
		final B2BDocumentDao b2bDocumentDao = mock(B2BDocumentDao.class);

		when(b2bDocumentDao.getOpenDocuments(unit)).thenReturn(value);

		final DefaultB2BDocumentService defaultB2BDocumentService = new DefaultB2BDocumentService();
		defaultB2BDocumentService.setB2bDocumentDao(b2bDocumentDao);

		final SearchResult<B2BDocumentModel> result = defaultB2BDocumentService.getOpenDocuments(unit);

		TestCase.assertEquals(1, result.getTotalCount());

		verify(b2bDocumentDao, Mockito.times(1)).getOpenDocuments(unit);
	}

	@Test
	public void testGetPagedB2BDocumentsPageSize10FirstPage()
	{
		final SearchPageData<B2BDocumentModel> pagedB2BDocuments = defaultB2BDocumentService.getPagedDocumentsForUnit("test-doc-0",
				pageSize10FirstPage, criteriaList);

		Assert.assertNotNull("Returned SearchPageData may not be null", pagedB2BDocuments);
		assertPagination(pagedB2BDocuments, pageSize10FirstPage);
		assertResults(pagedB2BDocuments, 10);
	}

	@Test
	public void testGetPagedB2BDocumentsPageSize10SecondPage()
	{
		final SearchPageData<B2BDocumentModel> pagedB2BDocuments = defaultB2BDocumentService.getPagedDocumentsForUnit("test-doc-0",
				pageSize10SecondPage, criteriaList);

		Assert.assertNotNull("Returned SearchPageData may not be null", pagedB2BDocuments);
		assertPagination(pagedB2BDocuments, pageSize10SecondPage);
		assertResults(pagedB2BDocuments, 10);
	}

	@Test
	public void testGetPagedB2BDocumentsPageSize10ThirdPage()
	{
		final SearchPageData<B2BDocumentModel> pagedB2BDocuments = defaultB2BDocumentService.getPagedDocumentsForUnit("test-doc-0",
				pageSize10ThirdPage, criteriaList);

		Assert.assertNotNull("Returned SearchPageData may not be null", pagedB2BDocuments);
		assertPagination(pagedB2BDocuments, pageSize10ThirdPage);
		assertResults(pagedB2BDocuments, 5);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetPagedB2BdocumentsForUnitNull()
	{
		defaultB2BDocumentService.getPagedDocumentsForUnit(null, pageSize10ThirdPage, criteriaList);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetPagedB2BdocumentsForPageableDataNull()
	{
		defaultB2BDocumentService.getPagedDocumentsForUnit("test-doc-0", null, criteriaList);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void testGetPagedB2BdocumentsForCriteriaNull()
	{
		defaultB2BDocumentService.getPagedDocumentsForUnit("test-doc-0", pageSize10ThirdPage, null);
	}

	protected PageableData createPageableData(final int pageSize, final int currentPage)
	{
		final PageableData pageableData = new PageableData();
		pageableData.setPageSize(pageSize);
		pageableData.setCurrentPage(currentPage);
		return pageableData;
	}

	protected SearchPageData<B2BDocumentModel> createSearchPageData(final PageableData pageableData)
	{
		final SearchPageData<B2BDocumentModel> searchPageData = new SearchPageData<B2BDocumentModel>();
		searchPageData.setPagination(createPaginationData(pageableData));
		searchPageData.setResults(createResults(pageableData));
		return searchPageData;
	}

	protected PaginationData createPaginationData(final PageableData pageableData)
	{
		final PaginationData paginationData = new PaginationData();
		paginationData.setCurrentPage(pageableData.getCurrentPage());
		paginationData.setPageSize(pageableData.getPageSize());
		paginationData.setNumberOfPages((NUMBER_OF_DOCUMENTS + pageableData.getPageSize() - 1) / pageableData.getPageSize());
		paginationData.setTotalNumberOfResults(NUMBER_OF_DOCUMENTS);
		return paginationData;
	}

	protected List<B2BDocumentModel> createResults(final PageableData pageableData)
	{
		final int fromIndex = pageableData.getCurrentPage() * pageableData.getPageSize();
		final int rest = NUMBER_OF_DOCUMENTS - pageableData.getCurrentPage() * pageableData.getPageSize();
		final int itemsOnPage = rest < pageableData.getPageSize() ? rest : pageableData.getPageSize();
		final List<B2BDocumentModel> results = b2bDocumentModels.subList(fromIndex, fromIndex + itemsOnPage);
		return results;
	}

	protected List<B2BDocumentModel> createDocumentResults()
	{
		final List<B2BDocumentModel> documentsList = new ArrayList<B2BDocumentModel>();
		for (int i = 0; i < NUMBER_OF_DOCUMENTS; i++)
		{
			final B2BDocumentModel documentModel = new B2BDocumentModel();
			documentModel.setDocumentNumber("test-doc-" + i);
			documentsList.add(documentModel);
		}
		return documentsList;
	}

	protected void assertResults(final SearchPageData<B2BDocumentModel> pagedB2BDocuments, final int expectedSize)
	{
		Assert.assertNotNull("Result list may not be null", pagedB2BDocuments.getResults());
		Assert.assertEquals("Number of returned results doesn't match the expected value", expectedSize, pagedB2BDocuments
				.getResults().size());
	}

	protected void assertPagination(final SearchPageData<B2BDocumentModel> pagedB2BDocuments, final PageableData pageableData)
	{
		Assert.assertNotNull("Pagination may not be null", pagedB2BDocuments.getPagination());
		Assert.assertEquals("Current Page does not match the expected value", pageableData.getCurrentPage(), pagedB2BDocuments
				.getPagination().getCurrentPage());
		Assert.assertEquals("Page Size does not match the expected value", pageableData.getPageSize(), pagedB2BDocuments
				.getPagination().getPageSize());
		Assert.assertEquals("Number of pages does not match the expected value", 3, pagedB2BDocuments.getPagination()
				.getNumberOfPages());
		Assert.assertEquals("Total number of results does not match the expected value", NUMBER_OF_DOCUMENTS, pagedB2BDocuments
				.getPagination().getTotalNumberOfResults());
	}

}

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
package de.hybris.platform.webservicescommons.pagination;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.servicelayer.data.PaginationData;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.core.servicelayer.data.SortData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.webservicescommons.dto.PaginationWsDTO;
import de.hybris.platform.webservicescommons.dto.SearchPageWsDTO;
import de.hybris.platform.webservicescommons.dto.SortWsDTO;
import de.hybris.platform.webservicescommons.pagination.converters.PaginationDataPopulator;
import de.hybris.platform.webservicescommons.pagination.converters.SortDataPopulator;
import de.hybris.platform.webservicescommons.pagination.impl.DefaultWebPaginationUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.google.common.collect.Lists;


public class WebPaginationUtilsTest
{
	private WebPaginationUtils webPaginationUtils;

	@Before
	public void setup()
	{
		final DefaultWebPaginationUtils util = new DefaultWebPaginationUtils();

		final PaginationDataPopulator paginationDataPopulator = new PaginationDataPopulator();
		util.setPaginationDataConverter(createConverter(paginationDataPopulator, PaginationWsDTO.class));
		util.setSortDataConverter(createConverter(new SortDataPopulator(), SortWsDTO.class));
		webPaginationUtils = util;
	}

	private <S, T> Converter<S, T> createConverter(final Populator<S, T> populator, final Class<T> targetClass)
	{
		final AbstractPopulatingConverter<S, T> result = new AbstractPopulatingConverter<>();
		result.setTargetClass(targetClass);
		result.setPopulators(Arrays.asList(populator));
		return result;
	}

	@Test
	public void testPaginationOnEmptyResult()
	{
		final TestSearchResult searchResult = new TestSearchResult(0, 0, 0, 100);

		final PaginationWsDTO pagination = webPaginationUtils.buildPagination(searchResult);

		Assert.assertEquals(Integer.valueOf(0), pagination.getCount());
		Assert.assertEquals(Integer.valueOf(0), pagination.getPage());
		Assert.assertEquals(Integer.valueOf(0), pagination.getTotalPages());
		Assert.assertEquals(Long.valueOf(0), pagination.getTotalCount());
	}

	@Test
	public void testPaginationOnSinglePage()
	{
		final TestSearchResult searchResult = new TestSearchResult(3, 3, 0, 100);

		final PaginationWsDTO pagination = webPaginationUtils.buildPagination(searchResult);

		Assert.assertEquals(Integer.valueOf(3), pagination.getCount());
		Assert.assertEquals(Integer.valueOf(0), pagination.getPage());
		Assert.assertEquals(Integer.valueOf(1), pagination.getTotalPages());
		Assert.assertEquals(Long.valueOf(3), pagination.getTotalCount());
	}

	@Test
	public void testPaginationForFirstOnMultiplePages()
	{
		final TestSearchResult searchResult = new TestSearchResult(100, 973, 0, 100);

		final PaginationWsDTO pagination = webPaginationUtils.buildPagination(searchResult);

		Assert.assertEquals(Integer.valueOf(100), pagination.getCount());
		Assert.assertEquals(Integer.valueOf(0), pagination.getPage());
		Assert.assertEquals(Integer.valueOf(10), pagination.getTotalPages());
		Assert.assertEquals(Long.valueOf(973), pagination.getTotalCount());
	}


	@Test
	public void testPaginationForMiddleOnMultiplePages()
	{
		final TestSearchResult searchResult = new TestSearchResult(100, 973, 500, 100);

		final PaginationWsDTO pagination = webPaginationUtils.buildPagination(searchResult);

		Assert.assertEquals(Integer.valueOf(100), pagination.getCount());
		Assert.assertEquals(Integer.valueOf(5), pagination.getPage());
		Assert.assertEquals(Integer.valueOf(10), pagination.getTotalPages());
		Assert.assertEquals(Long.valueOf(973), pagination.getTotalCount());
	}

	@Test
	public void testPaginationForLastOnMultiplePages()
	{
		final TestSearchResult searchResult = new TestSearchResult(73, 973, 900, 100);

		final PaginationWsDTO pagination = webPaginationUtils.buildPagination(searchResult);

		Assert.assertEquals(Integer.valueOf(73), pagination.getCount());
		Assert.assertEquals(Integer.valueOf(9), pagination.getPage());
		Assert.assertEquals(Integer.valueOf(10), pagination.getTotalPages());
		Assert.assertEquals(Long.valueOf(973), pagination.getTotalCount());
	}

	@Test
	public void testPaginationForAboveLastOnMultiplePages()
	{
		final TestSearchResult searchResult = new TestSearchResult(0, 973, 1000, 100);

		final PaginationWsDTO pagination = webPaginationUtils.buildPagination(searchResult);

		Assert.assertEquals(Integer.valueOf(0), pagination.getCount());
		Assert.assertEquals(Integer.valueOf(10), pagination.getPage());
		Assert.assertEquals(Integer.valueOf(10), pagination.getTotalPages());
		Assert.assertEquals(Long.valueOf(973), pagination.getTotalCount());
	}

	private static class TestSearchResult implements SearchResult<String>
	{

		private final int count, totalCount, requStart, requCount;

		public TestSearchResult(final int count, final int totalCount, final int requStart, final int requCount)
		{
			this.count = count;
			this.totalCount = totalCount;
			this.requStart = requStart;
			this.requCount = requCount;
		}

		@Override
		public int getCount()
		{
			return count;
		}

		@Override
		public int getTotalCount()
		{
			return totalCount;
		}

		@Override
		public List<String> getResult()
		{
			return null;
		}

		@Override
		public int getRequestedStart()
		{
			return requStart;
		}

		@Override
		public int getRequestedCount()
		{
			return requCount;
		}

	}

	@Test
	public void testRequestToPagination()
	{
		//given
		final int currentPage = 1;
		final int pageSize = 7;

		final HttpServletRequest request = mockRequest(currentPage, pageSize);
		final PaginationData expected = buildPagination(currentPage, pageSize, null);
		//when
		final PaginationData actual = webPaginationUtils.buildPaginationData(request);
		//then
		assertPaginationData(expected, actual);
	}


	@Test
	public void testMapToPagination()
	{
		//given
		final int currentPage = 1;
		final int pageSize = 7;
		final Boolean needsTotal = Boolean.TRUE;

		final Map<String, String> map = mockMap(currentPage, pageSize, needsTotal, null);
		final PaginationData expected = buildPagination(currentPage, pageSize, needsTotal);
		//when
		final PaginationData actual = webPaginationUtils.buildPaginationData(map);
		//then
		assertPaginationData(expected, actual);
	}

	@Test
	public void testParamsToPagination()
	{
		//given
		final int currentPage = 1;
		final int pageSize = 7;
		final Boolean needsTotal = Boolean.TRUE;

		final PaginationData expected = buildPagination(currentPage, pageSize, needsTotal);
		//when
		final PaginationData actual = webPaginationUtils.buildPaginationData(currentPage, pageSize);
		//then
		assertPaginationData(expected, actual);
	}

	@Test
	public void testRequestToSlicePagination()
	{
		//given
		final int currentPage = 1;
		final int pageSize = 7;
		final Boolean needsTotal = Boolean.TRUE;

		final HttpServletRequest request = mockRequest(currentPage, pageSize, needsTotal);
		final PaginationData expected = buildPagination(currentPage, pageSize, needsTotal);
		//when
		final PaginationData actual = webPaginationUtils.buildPaginationData(request);
		//then
		assertPaginationData(expected, actual);
	}

	@Test
	public void testRequestToSort()
	{
		//given
		final String sorts = "key1,key2";

		final HttpServletRequest request = mockRequest(sorts);
		final List<SortData> expected = Lists.newArrayList(buildSort("key1"), buildSort("key2"));
		//when
		final List<SortData> actual = webPaginationUtils.buildSortData(request);
		//then
		assertSortData(expected, actual);
	}

	@Test
	public void testMapToSort()
	{
		//given
		final String sorts = "key1:asc,key2:desc,key3";

		final Map<String, String> map = mockMap(sorts);
		final List<SortData> expected = Lists.newArrayList(buildSort("key1", true), buildSort("key2", false),
				buildSort("key3", true));
		//when
		final List<SortData> actual = webPaginationUtils.buildSortData(map);
		//then
		assertSortData(expected, actual);
	}

	@Test
	public void testParamsToSort()
	{
		//given
		final String sorts = "key1:asc,key2:desc,key3";

		final List<SortData> expected = Lists.newArrayList(buildSort("key1", true), buildSort("key2", false),
				buildSort("key3", true));
		//when
		final List<SortData> actual = webPaginationUtils.buildSortData(sorts);
		//then
		assertSortData(expected, actual);
	}

	@Test
	public void testRequestToSearchPage()
	{
		//given
		final int currentPage = 2;
		final int pageSize = 7;
		final Boolean needsTotal = Boolean.TRUE;
		final String sorts = "key1:asc,key2:desc,key3";

		final HttpServletRequest request = mockRequest(currentPage, pageSize, needsTotal, sorts);
		final SearchPageData<Object> expected = buildSearchPage(currentPage, pageSize, needsTotal, buildSort("key1", true),
				buildSort("key2", false), buildSort("key3", true));

		//when
		final SearchPageData<Object> actual = webPaginationUtils.buildSearchPageData(request);

		//then
		assertSearchPageData(expected, actual);
	}

	@Test
	public void testMapToSearchPage()
	{
		//given
		final int currentPage = 2;
		final int pageSize = 7;
		final Boolean needsTotal = Boolean.TRUE;
		final String sorts = "key1:asc,key2:desc,key3";

		final Map<String, String> request = mockMap(currentPage, pageSize, needsTotal, sorts);
		final SearchPageData<Object> expected = buildSearchPage(currentPage, pageSize, needsTotal, buildSort("key1", true),
				buildSort("key2", false), buildSort("key3", true));

		//when
		final SearchPageData<Object> actual = webPaginationUtils.buildSearchPageData(request);

		//then
		assertSearchPageData(expected, actual);
	}

	@Test
	public void testParamsToSearchPage()
	{
		//given
		final int currentPage = 2;
		final int pageSize = 7;
		final Boolean needsTotal = Boolean.TRUE;
		final String sorts = "key1:asc,key2:desc,key3";

		final SearchPageData<Object> expected = buildSearchPage(currentPage, pageSize, needsTotal, buildSort("key1", true),
				buildSort("key2", false), buildSort("key3", true));

		//when
		final SearchPageData<Object> actual = webPaginationUtils.buildSearchPageData(sorts, currentPage, pageSize,
				needsTotal.booleanValue());

		//then
		assertSearchPageData(expected, actual);
	}

	@Test
	public void testSearchPageToWsDto()
	{
		//given
		final SearchPageData<Object> searchPage = buildSearchPage(1, 2, Boolean.TRUE, 3, 4, buildSort("key1"),
				buildSort("key2", false));
		final SearchPageWsDTO<Object> expected = buildSearchPageWsDto(1, 2, Boolean.TRUE, 3, 4, buildSortWsDto("key1", true),
				buildSortWsDto("key2", false));

		//when
		final SearchPageWsDTO<Object> actual = webPaginationUtils.buildSearchPageWsDto(searchPage);

		//then
		assertSearchPageWsDto(expected, actual);
	}

	private void assertPaginationData(final PaginationData expected, final PaginationData actual)
	{
		Assert.assertNotNull("pagination object shouldn't be null", actual);
		Assert.assertEquals(expected.getCurrentPage(), actual.getCurrentPage());
		Assert.assertEquals(expected.getPageSize(), actual.getPageSize());
		Assert.assertEquals(Boolean.valueOf(expected.isNeedsTotal()), Boolean.valueOf(actual.isNeedsTotal()));
	}

	private boolean compareSortData(final SortData expected, final SortData actual)
	{
		final Comparator<SortData> comparator = Comparator.comparing(SortData::getCode).thenComparing(SortData::isAsc);
		return comparator.compare(expected, actual) == 0;
	}

	private void assertSortData(final List<SortData> expected, final List<SortData> actual)
	{
		if (actual == expected)
		{
			return;
		}
		final ListIterator<SortData> e1 = expected.listIterator();
		final ListIterator<SortData> e2 = actual.listIterator();
		while (e1.hasNext() && e2.hasNext())
		{
			final SortData o1 = e1.next();
			final SortData o2 = e2.next();
			if (!(o1 == null ? o2 == null : compareSortData(o1, o2)))
			{
				Assert.fail("Expected " + printSortData(o1) + ", actual " + printSortData(o2));
			}
		}
		Assert.assertTrue("Sort list has different length.", !(e1.hasNext() || e2.hasNext()));
	}

	private String printSortData(final SortData sortData)
	{
		return "[code:" + sortData.getCode() + ", " + "asc:" + sortData.isAsc() + "]";
	}

	private void assertSearchPageData(final SearchPageData<?> expected, final SearchPageData<?> actual)
	{
		Assert.assertNotNull("Search Page object shouldn't be null", actual);
		assertPaginationData(expected.getPagination(), actual.getPagination());
		assertSortData(expected.getSorts(), actual.getSorts());
	}

	private void assertSearchPageWsDto(final SearchPageWsDTO<Object> expected, final SearchPageWsDTO<Object> actual)
	{
		Assert.assertNotNull("Search Page object shouldn't be null", actual);

		final PaginationWsDTO ePagination = expected.getPagination();
		final PaginationWsDTO aPagination = actual.getPagination();

		Assert.assertNotNull("pagination object shouldn't be null", actual);
		Assert.assertEquals(ePagination.getClass(), aPagination.getClass());
		Assert.assertEquals(ePagination.getTotalCount(), aPagination.getTotalCount());
		Assert.assertEquals(ePagination.getCount(), aPagination.getCount());
		Assert.assertEquals(ePagination.getPage(), aPagination.getPage());
		Assert.assertEquals(ePagination.getTotalPages(), aPagination.getTotalPages());
		Assert.assertEquals(ePagination.getHasNext(), aPagination.getHasNext());
		Assert.assertEquals(ePagination.getHasPrevious(), aPagination.getHasPrevious());

		final List<SortWsDTO> eList = expected.getSorts();
		final List<SortWsDTO> aList = actual.getSorts();
		Assert.assertEquals(eList.size(), aList.size());
		for (int i = 0; i < eList.size(); ++i)
		{
			Assert.assertEquals(eList.get(i).getCode(), aList.get(i).getCode());
			Assert.assertEquals(Boolean.valueOf(eList.get(i).isAsc()), Boolean.valueOf(aList.get(i).isAsc()));
		}
	}

	private PaginationData buildPagination(final int currentPage, final int pageSize, final Boolean needsTotal)
	{
		final PaginationData result = new PaginationData();
		result.setCurrentPage(currentPage);
		result.setPageSize(pageSize);
		if (needsTotal != null)
		{
			result.setNeedsTotal(needsTotal.booleanValue());
		}
		else
		{
			result.setNeedsTotal(true);
		}
		return result;
	}

	private PaginationData buildPagination(final int currentPage, final int pageSize, final Boolean needsTotal, final int total,
			final int totalPages)
	{
		final PaginationData result = buildPagination(currentPage, pageSize, needsTotal);

		result.setTotalNumberOfResults(total);
		result.setNumberOfPages(totalPages);

		return result;
	}

	private SortData buildSort(final String code)
	{
		return buildSort(code, true);
	}

	private SortData buildSort(final String code, final boolean asc)
	{
		final SortData result = new SortData();

		result.setCode(code);
		result.setAsc(asc);

		return result;
	}

	private SortWsDTO buildSortWsDto(final String code, final boolean asc)
	{
		final SortWsDTO result = new SortWsDTO();

		result.setCode(code);
		result.setAsc(asc);

		return result;
	}

	private SearchPageData<Object> buildSearchPage(final int currentPage, final int pageSize, final Boolean needsTotal,
			final SortData... sorts)
	{
		final SearchPageData<Object> result = new SearchPageData<>();

		result.setPagination(buildPagination(currentPage, pageSize, needsTotal));
		result.setSorts(Lists.newArrayList(sorts));
		return result;
	}

	private SearchPageData<Object> buildSearchPage(final int currentPage, final int pageSize, final Boolean needsTotal,
			final int total, final int totalPages, final SortData... sorts)
	{
		final SearchPageData<Object> result = new SearchPageData<>();

		result.setPagination(buildPagination(currentPage, pageSize, needsTotal, total, totalPages));
		result.setSorts(Lists.newArrayList(sorts));
		return result;
	}

	private SearchPageWsDTO<Object> buildSearchPageWsDto(final int currentPage, final int pageSize, final Boolean needsTotal,
			final int total, final int totalPages, final SortWsDTO... sorts)
	{
		final SearchPageWsDTO<Object> result = new SearchPageWsDTO<>();

		final PaginationWsDTO pagination = new PaginationWsDTO();
		pagination.setPage(Integer.valueOf(currentPage));
		pagination.setCount(Integer.valueOf(pageSize));
		pagination.setTotalCount(Long.valueOf(total));
		pagination.setTotalPages(Integer.valueOf(totalPages));

		result.setPagination(pagination);
		result.setSorts(Arrays.asList(sorts));

		return result;
	}

	private HttpServletRequest mockRequest(final String sorts)
	{
		return mockRequest(0, 0, Boolean.FALSE, sorts);
	}

	private HttpServletRequest mockRequest(final int currentPage, final int pageSize)
	{
		return mockRequest(currentPage, pageSize, null, null);
	}

	private HttpServletRequest mockRequest(final int currentPage, final int pageSize, final Boolean needsTotal)
	{
		return mockRequest(currentPage, pageSize, needsTotal, null);
	}

	private HttpServletRequest mockRequest(final int currentPage, final int pageSize, final Boolean needsTotal, final String sorts)
	{
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setParameters(mockMap(currentPage, pageSize, needsTotal, sorts));
		return request;
	}

	private Map<String, String> mockMap(final int currentPage, final int pageSize, final Boolean needsTotal, final String sorts)
	{
		final Map<String, String> result = new HashMap<>();

		result.put("pageSize", String.valueOf(pageSize));
		result.put("currentPage", String.valueOf(currentPage));
		if (needsTotal != null)
		{
			result.put("needsTotal", String.valueOf(needsTotal));
		}
		if (sorts != null)
		{
			result.put("sort", sorts);
		}

		return result;
	}

	private Map<String, String> mockMap(final String sorts)
	{
		final Map<String, String> result = new HashMap<>();

		if (sorts != null)
		{
			result.put("sort", sorts);
		}

		return result;
	}
}

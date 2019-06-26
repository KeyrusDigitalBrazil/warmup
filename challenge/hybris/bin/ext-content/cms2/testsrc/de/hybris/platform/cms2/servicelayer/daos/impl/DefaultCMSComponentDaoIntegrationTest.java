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
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.containers.ABTestCMSComponentContainerModel;
import de.hybris.platform.cms2.servicelayer.daos.CMSComponentDao;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.paginated.constants.SearchConstants;
import de.hybris.platform.servicelayer.search.paginated.util.PaginatedSearchUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;



@IntegrationTest
public class DefaultCMSComponentDaoIntegrationTest extends ServicelayerTransactionalTest
{
	@Resource
	private ModelService modelService;
	@Resource
	private CMSComponentDao cmsComponentDao;
	@Resource
	private CatalogVersionService catalogVersionService;

	private ABTestCMSComponentContainerModel component1;
	private ABTestCMSComponentContainerModel component2;
	private ABTestCMSComponentContainerModel component3;
	private CatalogVersionModel catalogVersion1;
	private CatalogVersionModel catalogVersion2;

	@Before
	public void setUp() throws Exception
	{

		importCsv("/test/cmsCatalogVersionTestData.csv", "windows-1252");
		catalogVersion1 = catalogVersionService.getCatalogVersion("cms_Catalog", "CatalogVersion1");
		catalogVersion2 = catalogVersionService.getCatalogVersion("cms_Catalog", "CatalogVersion2");

		component1 = new ABTestCMSComponentContainerModel();
		component2 = new ABTestCMSComponentContainerModel();
		component3 = new ABTestCMSComponentContainerModel();

		component1.setCatalogVersion(catalogVersion1);
		component1.setUid("testComponent1");
		component1.setName("test component");
		modelService.save(component1);
		component2.setCatalogVersion(catalogVersion1);
		component2.setUid("testComponent2");
		component2.setName("My component");
		modelService.save(component2);
		component3.setCatalogVersion(catalogVersion2);
		component3.setUid("testComponent3");
		modelService.save(component3);
	}

	@Test
	public void shouldFindCmsComponentsByCatalogVersionOnly_CatalogVersion1()
	{
		final List<AbstractCMSComponentModel> components = cmsComponentDao.findAllCMSComponentsByCatalogVersion(catalogVersion1);
		Assert.assertEquals(2, components.size());
		assertContainsComponentUid(components, "testComponent1");
		assertContainsComponentUid(components, "testComponent2");
		assertNotContainsComponentUid(components, "testComponent3");
	}


	@Test
	public void shouldFindCmsComponentsByCatalogVersionOnly_CatalogVersion2()
	{

		final List<AbstractCMSComponentModel> components = cmsComponentDao.findAllCMSComponentsByCatalogVersion(catalogVersion2);
		Assert.assertEquals(1, components.size());
		assertContainsComponentUid(components, "testComponent3");
		assertNotContainsComponentUid(components, "testComponent1");
		assertNotContainsComponentUid(components, "testComponent2");

	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotFindCmsComponents_NoCatalogVersion()
	{
		cmsComponentDao.findAllCMSComponentsByCatalogVersion(null);
	}

	@Test
	public void shouldFindCmsComponentsByMask()
	{
		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(0);
		pageableData.setPageSize(2);
		pageableData.setSort("name");

		final SearchResult<AbstractCMSComponentModel> components = cmsComponentDao.findByCatalogVersionAndMask(catalogVersion1,
				"comp", pageableData);

		assertThat(components.getCount(), equalTo(2));
		assertThat(components.getTotalCount(), equalTo(2));
		assertThat(components.getResult().get(0).getUid(), equalTo("testComponent2"));
		assertThat(components.getResult().get(1).getUid(), equalTo("testComponent1"));
	}

	protected void assertContainsComponentUid(final List<AbstractCMSComponentModel> components1, final String componentUid)
	{
		Assert.assertTrue(components1.stream().map(component -> component.getUid()).anyMatch(uid -> uid.equals(componentUid)));
	}

	protected void assertNotContainsComponentUid(final List<AbstractCMSComponentModel> components1, final String componentUid)
	{
		Assert.assertTrue(components1.stream().map(component -> component.getUid()).noneMatch(uid -> uid.equals(componentUid)));
	}

	@Test
	public void shouldfindCMSComponentsByIdListAndCatalogVersion1()
	{
		final List<String> idList = new ArrayList<String>();
		idList.add("testComponent1");
		idList.add("testComponent2");

		// Set the pagination data with pageSize of 5, currentPage at 0 and needsTotal to true
		final SearchPageData searchPageData = PaginatedSearchUtils.createSearchPageDataWithPagination(5, 0, true);
		final SearchPageData<AbstractCMSComponentModel> searchPageResult = cmsComponentDao
				.findCMSComponentsByIdsAndCatalogVersions(idList, Arrays.asList(catalogVersion1), searchPageData);
		final List<AbstractCMSComponentModel> components = searchPageResult.getResults();
		assertPaginationResults(2, 2, 0, searchPageResult);
		assertContainsComponentUid(components, "testComponent1");
		assertContainsComponentUid(components, "testComponent2");
		assertNotContainsComponentUid(components, "testComponent3");
	}

	@Test
	public void shouldReturnEmptyResultsForNonExistingCMSComponentsByIdList()
	{
		final List<String> idList = new ArrayList<String>();
		idList.add("NonExistingComponentA");
		idList.add("NonExistingComponentB");

		// Set the pagination data with pageSize of 5, currentPage at 0 and needsTotal to true
		final SearchPageData searchPageData = PaginatedSearchUtils.createSearchPageDataWithPagination(5, 0, true);
		final SearchPageData<AbstractCMSComponentModel> searchPageResult = cmsComponentDao
				.findCMSComponentsByIdsAndCatalogVersions(idList, Arrays.asList(catalogVersion1), searchPageData);
		final List<AbstractCMSComponentModel> components = searchPageResult.getResults();
		assertPaginationResults(0, 0, 0, searchPageResult);
	}

	@Test
	public void shouldfindCMSComponentsByIdListAndCatalogVersion1and2()
	{
		final List<String> idList = new ArrayList<String>();
		idList.add("testComponent1");
		idList.add("testComponent3");

		// Set the pagination data with pageSize of 5, currentPage at 0 and needsTotal to true with sort by uid ascending
		final Map<String, String> sortMap = new LinkedHashMap();
		sortMap.put(AbstractCMSComponentModel.UID, SearchConstants.ASCENDING);
		final SearchPageData searchPageData = PaginatedSearchUtils.createSearchPageDataWithPaginationAndSorting(5, 0, true,
				sortMap);
		final SearchPageData<AbstractCMSComponentModel> searchPageResult = cmsComponentDao
				.findCMSComponentsByIdsAndCatalogVersions(idList, Arrays.asList(catalogVersion1, catalogVersion2), searchPageData);
		final List<AbstractCMSComponentModel> components = searchPageResult.getResults();
		assertPaginationResults(2, 2, 1, searchPageResult);
		assertContainsComponentUid(components, "testComponent1");
		assertNotContainsComponentUid(components, "testComponent2");
		assertContainsComponentUid(components, "testComponent3");
	}

	@Test
	public void shouldfindCMSComponentsByIdListAndCatalogVersion1and2withPagination()
	{
		final List<String> idList = new ArrayList<String>();
		idList.add("testComponent1");
		idList.add("testComponent2");
		idList.add("testComponent3");

		// Set the pagination data with pageSize of 2, currentPage at 1 and needsTotal to true with sort by uid ascending
		final Map<String, String> sortMap = new LinkedHashMap();
		sortMap.put(AbstractCMSComponentModel.UID, SearchConstants.ASCENDING);
		final SearchPageData searchPageData = PaginatedSearchUtils.createSearchPageDataWithPaginationAndSorting(2, 1, true,
				sortMap);
		final SearchPageData<AbstractCMSComponentModel> searchPageResult = cmsComponentDao
				.findCMSComponentsByIdsAndCatalogVersions(idList, Arrays.asList(catalogVersion1, catalogVersion2), searchPageData);
		final List<AbstractCMSComponentModel> components = searchPageResult.getResults();
		assertPaginationResults(1, 3, 1, searchPageResult);
		assertNotContainsComponentUid(components, "testComponent1");
		assertNotContainsComponentUid(components, "testComponent2");
		assertContainsComponentUid(components, "testComponent3");
	}

	@Test
	public void shouldfindCMSComponentsByIdListAndCatalogVersion1and2withPaginationAndSorting()
	{
		final List<String> idList = new ArrayList<String>();
		idList.add("testComponent1");
		idList.add("testComponent2");
		idList.add("testComponent3");

		// Set the pagination data with pageSize of 2, currentPage at 1 and needsTotal to true with sort by uid ascending
		final Map<String, String> sortMap = new LinkedHashMap();
		sortMap.put(AbstractCMSComponentModel.UID, SearchConstants.DESCENDING);
		final SearchPageData searchPageData = PaginatedSearchUtils.createSearchPageDataWithPaginationAndSorting(2, 1, true,
				sortMap);
		final SearchPageData<AbstractCMSComponentModel> searchPageResult = cmsComponentDao
				.findCMSComponentsByIdsAndCatalogVersions(idList, Arrays.asList(catalogVersion1, catalogVersion2), searchPageData);
		final List<AbstractCMSComponentModel> components = searchPageResult.getResults();
		assertPaginationResults(1, 3, 1, searchPageResult);
		assertContainsComponentUid(components, "testComponent1");
		assertNotContainsComponentUid(components, "testComponent2");
		assertNotContainsComponentUid(components, "testComponent3");
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotFindCmsComponents_NullIdList()
	{
		final SearchPageData searchPageData = PaginatedSearchUtils.createSearchPageDataWithPagination(5, 0, true);
		final SearchPageData<AbstractCMSComponentModel> searchPageResult = cmsComponentDao
				.findCMSComponentsByIdsAndCatalogVersions(null, Arrays.asList(catalogVersion1), searchPageData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotFindCmsComponents_EmptyIdList()
	{
		final SearchPageData searchPageData = PaginatedSearchUtils.createSearchPageDataWithPagination(5, 0, true);
		final SearchPageData<AbstractCMSComponentModel> searchPageResult = cmsComponentDao
				.findCMSComponentsByIdsAndCatalogVersions(Collections.EMPTY_LIST, Arrays.asList(catalogVersion1), searchPageData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotFindCmsComponents_NullCatalogVersion()
	{
		final List<String> idList = new ArrayList<String>();
		idList.add("testComponent1");
		final SearchPageData searchPageData = PaginatedSearchUtils.createSearchPageDataWithPagination(5, 0, true);
		final SearchPageData<AbstractCMSComponentModel> searchPageResult = cmsComponentDao
				.findCMSComponentsByIdsAndCatalogVersions(idList, null, searchPageData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotFindCmsComponents_NullSearchPageData()
	{
		final List<String> idList = new ArrayList<String>();
		idList.add("testComponent1");
		final SearchPageData<AbstractCMSComponentModel> searchPageResult = cmsComponentDao
				.findCMSComponentsByIdsAndCatalogVersions(idList, Arrays.asList(catalogVersion1), null);
	}

	/**
	 * Asserts the given searchPageData results to see if it has the expected result count based on requested pagination
	 * pageSize. Then also checks for the total results count and sort list count.
	 *
	 * @param expectedPaginatedResultSize
	 *           the expected result count
	 * @param expectedTotalResultSize
	 *           the expected total results of the search results
	 * @param expectedSortSize
	 *           the expected sort list size
	 * @param searchResult
	 *           the searchResult
	 */
	private void assertPaginationResults(final int expectedPaginatedResultSize, final int expectedTotalResultSize,
			final int expectedSortSize, final SearchPageData<AbstractCMSComponentModel> searchResult)
	{
		Assert.assertNotNull("Search page data is null", searchResult);
		Assert.assertNotNull("Search results are null", searchResult.getResults());
		Assert.assertEquals("Unexpected number of results", expectedPaginatedResultSize, searchResult.getResults().size());
		Assert.assertNotNull("Search page data pagination is null", searchResult.getPagination());
		Assert.assertEquals("Unexpected total number of results", expectedTotalResultSize,
				searchResult.getPagination().getTotalNumberOfResults());
		Assert.assertEquals("Unexpected number of result sorts", expectedSortSize, searchResult.getSorts().size());
	}
}

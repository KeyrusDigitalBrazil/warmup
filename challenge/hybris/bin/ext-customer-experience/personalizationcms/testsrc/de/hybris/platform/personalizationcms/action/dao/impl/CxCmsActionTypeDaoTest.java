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
package de.hybris.platform.personalizationcms.action.dao.impl;

import static de.hybris.platform.personalizationcms.action.dao.impl.CxCmsActionTypeDao.PAGE_ID_QUERY;
import static de.hybris.platform.personalizationcms.action.dao.impl.CxCmsActionTypeDao.Parameters.ACTION_CODE;
import static de.hybris.platform.personalizationcms.action.dao.impl.CxCmsActionTypeDao.Parameters.COMPONENT_CATALOG;
import static de.hybris.platform.personalizationcms.action.dao.impl.CxCmsActionTypeDao.Parameters.COMPONENT_ID;
import static de.hybris.platform.personalizationcms.action.dao.impl.CxCmsActionTypeDao.Parameters.CONTAINER_ID;
import static de.hybris.platform.personalizationcms.action.dao.impl.CxCmsActionTypeDao.Parameters.CUSTOMIZATION_CODE;
import static de.hybris.platform.personalizationcms.action.dao.impl.CxCmsActionTypeDao.Parameters.CUSTOMIZATION_NAME;
import static de.hybris.platform.personalizationcms.action.dao.impl.CxCmsActionTypeDao.Parameters.CUSTOMIZATION_STATUS;
import static de.hybris.platform.personalizationcms.action.dao.impl.CxCmsActionTypeDao.Parameters.PAGE_ID;
import static de.hybris.platform.personalizationcms.action.dao.impl.CxCmsActionTypeDao.Parameters.VARIATION_CODE;
import static de.hybris.platform.personalizationcms.action.dao.impl.CxCmsActionTypeDao.Parameters.VARIATION_NAME;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.daos.CatalogDao;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.core.servicelayer.data.PaginationData;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.personalizationcms.model.CxCmsActionModel;
import de.hybris.platform.personalizationservices.enums.CxItemStatus;
import de.hybris.platform.personalizationservices.exceptions.EmptyResultParameterCombinationException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Sets;


@UnitTest
public class CxCmsActionTypeDaoTest
{
	private static final String CATALOG_VERSION = "myVersion";


	private static final String SELECT = " SELECT {a.pk}";
	private static final String FROM = " FROM {CxCmsAction AS a JOIN CxVariation AS v ON {v.pk} = {a.variation} JOIN CxCustomization AS c ON {c.pk} = {v.customization} ";
	private static final String WHERE = "} WHERE {a.catalogVersion}  = ?catalogVersion ";
	private static final String MULTI_WHERE = "} WHERE {a.catalogVersion}  IN (?catalogVersions) ";
	private static final String ORDER_BY_PART = " {c.groupPOS} ASC, {v.customizationPOS} ASC, {a.variationPOS} ASC";
	private static final String ORDER_BY = " ORDER BY" + ORDER_BY_PART;
	private static final String MULTI_ORDER = " ORDER BY  (CASE {c.catalogVersion}  WHEN ?catalogVersions0 THEN 0  WHEN ?catalogVersions1 THEN 1  WHEN ?catalogVersions2 THEN 2  WHEN ?catalogVersions3 THEN 3  END)  ASC,"
			+ ORDER_BY_PART;


	private CxCmsActionTypeDao dao;
	private CatalogVersionModel catalogVersion;
	private Map<String, Object> expectedParams;
	private Map<String, ContentCatalogModel> catalogMap;
	private CatalogDao catalogDao;

	@Before
	public void setUp()
	{
		dao = new CxCmsActionTypeDao();
		catalogDao = Mockito.mock(CatalogDao.class);

		catalogMap = new HashMap<>();
		catalogVersion = buildCatalogHierarchy(3, false);

		expectedParams = new HashMap<>();
		expectedParams.put("catalogVersion", catalogVersion);

		dao.setCatalogDao(catalogDao);

	}

	@Test
	public void buildParamsEmptyTest()
	{
		//given

		//when
		final Map<String, Object> actual = dao.buildParams(catalogVersion, Collections.emptyMap());

		//then
		Assert.assertEquals(expectedParams, actual);
	}

	@Test
	public void buildParamsSingleCodeTest()
	{
		//given
		final String actionCode = "action";
		final Map<String, String> params = new HashMap<>();
		params.put(ACTION_CODE.paramName, actionCode);

		expectedParams.put(ACTION_CODE.paramName, actionCode);

		//when
		final Map<String, Object> actual = dao.buildParams(catalogVersion, params);

		//then
		Assert.assertEquals(expectedParams, actual);
	}

	@Test
	public void buildParamsMultipleCodesTest()
	{
		//given
		final String actionCode = "action";
		final String variationCode = "variation";
		final String customizationCode = "customization";
		final Map<String, String> params = new HashMap<>();
		params.put(ACTION_CODE.paramName, actionCode);
		params.put(VARIATION_CODE.paramName, variationCode);
		params.put(CUSTOMIZATION_CODE.paramName, customizationCode);


		expectedParams.put(ACTION_CODE.paramName, actionCode);
		expectedParams.put(VARIATION_CODE.paramName, variationCode);
		expectedParams.put(CUSTOMIZATION_CODE.paramName, customizationCode);

		//when
		final Map<String, Object> actual = dao.buildParams(catalogVersion, params);

		//then
		Assert.assertEquals(expectedParams, actual);
	}

	@Test
	public void buildParamsMultipleNameTest()
	{
		//given
		final String variationCode = "variation";
		final String customizationCode = "customization";
		final Map<String, String> params = new HashMap<>();
		params.put(VARIATION_NAME.paramName, variationCode);
		params.put(CUSTOMIZATION_NAME.paramName, customizationCode);


		expectedParams.put(VARIATION_NAME.paramName, "%" + variationCode + "%");
		expectedParams.put(CUSTOMIZATION_NAME.paramName, "%" + customizationCode + "%");

		//when
		final Map<String, Object> actual = dao.buildParams(catalogVersion, params);

		//then
		Assert.assertEquals(expectedParams, actual);
	}

	@Test
	public void buildParamsStatusTest()
	{
		//given
		final String statuses = "ENABLED";

		final Map<String, String> params = new HashMap<>();
		params.put(CUSTOMIZATION_STATUS.paramName, statuses);

		expectedParams.put(CUSTOMIZATION_STATUS.paramName, Sets.newHashSet(CxItemStatus.ENABLED));

		//when
		final Map<String, Object> actual = dao.buildParams(catalogVersion, params);

		//then
		Assert.assertEquals(expectedParams, actual);
	}

	@Test
	public void buildParamsStatusesTest()
	{
		//given
		final String statuses = "ENABLED,DISABLED,DELETED";

		final Map<String, String> params = new HashMap<>();
		params.put(CUSTOMIZATION_STATUS.paramName, statuses);

		expectedParams.put(CUSTOMIZATION_STATUS.paramName,
				Sets.newHashSet(CxItemStatus.ENABLED, CxItemStatus.DISABLED, CxItemStatus.DELETED));

		//when
		final Map<String, Object> actual = dao.buildParams(catalogVersion, params);

		//then
		Assert.assertEquals(expectedParams, actual);
	}

	@Test
	public void buildParamsActionFieldsTest()
	{
		//given
		final String componentId = "component";
		final String componentCatalog = "catalog";
		final String containerId = "container";
		final Map<String, String> params = new HashMap<>();
		params.put(COMPONENT_ID.paramName, componentId);
		params.put(COMPONENT_CATALOG.paramName, componentCatalog);
		params.put(CONTAINER_ID.paramName, containerId);


		expectedParams.put(COMPONENT_ID.paramName, componentId);
		expectedParams.put(COMPONENT_CATALOG.paramName, componentCatalog);
		expectedParams.put(CONTAINER_ID.paramName, containerId);

		//when
		final Map<String, Object> actual = dao.buildParams(catalogVersion, params);

		//then
		Assert.assertEquals(expectedParams, actual);
	}

	@Test
	public void buildParamsCurrentCatalogTest()
	{
		//given
		final Map<String, String> params = new HashMap<>();
		params.put("catalogs", "current");

		//when
		final Map<String, Object> actual = dao.buildParams(catalogVersion, params);

		//then
		Assert.assertEquals(expectedParams, actual);
	}

	@Test
	public void buildParamsParentsCatalogTest()
	{
		//given
		final Map<String, String> params = new HashMap<>();
		params.put("catalogs", "Parents");

		buildExpectedCatalogHierarchy(getParentCatalogVersion(catalogVersion));

		//when
		final Map<String, Object> actual = dao.buildParams(catalogVersion, params);

		//then
		Assert.assertEquals(expectedParams, actual);
	}

	@Test(expected = EmptyResultParameterCombinationException.class)
	public void buildParamsParentsCatalogNoParentsTest()
	{
		//given
		final Map<String, String> params = new HashMap<>();
		params.put("catalogs", "Parents");
		final CatalogVersionModel singleCatalogVersion = buildSingleCatalog("catalog", CATALOG_VERSION, true);

		//when
		final Map<String, Object> actual = dao.buildParams(singleCatalogVersion, params);
	}

	@Test
	public void buildParamsAllCatalogTest()
	{
		//given
		final Map<String, String> params = new HashMap<>();
		params.put("catalogs", "ALL");

		buildExpectedCatalogHierarchy(catalogVersion);

		//when
		final Map<String, Object> actual = dao.buildParams(catalogVersion, params);

		//then
		Assert.assertEquals(expectedParams, actual);
	}

	@Test
	public void buildParamsPageIdTest()
	{
		//given
		final String pageId = "page";
		final Map<String, String> params = new HashMap<>();
		params.put(PAGE_ID.paramName, pageId);

		expectedParams.put(PAGE_ID.paramName, pageId);
		expectedParams.put("pageCatalogVersion", catalogVersion);

		//when
		final Map<String, Object> actual = dao.buildParams(catalogVersion, params);

		//then
		Assert.assertEquals(expectedParams, actual);
	}

	@Test
	public void buildParamsPageIdAndCurrentCatalogTest()
	{
		//given
		final String pageId = "page";
		final String pageCatalogId = "catalog";
		final Map<String, String> params = new HashMap<>();
		params.put(PAGE_ID.paramName, pageId);
		params.put("pageCatalogId", pageCatalogId);


		expectedParams.put(PAGE_ID.paramName, pageId);
		expectedParams.put("pageCatalogVersion", catalogVersion);

		//when
		final Map<String, Object> actual = dao.buildParams(catalogVersion, params);

		//then
		Assert.assertEquals(expectedParams, actual);
	}

	@Test
	public void buildParamsPageIdAndParentCatalogTest()
	{
		//given
		final String pageId = "page";
		final String pageCatalogId = "catalog2";
		final Map<String, String> params = new HashMap<>();
		params.put(PAGE_ID.paramName, pageId);
		params.put("pageCatalogId", pageCatalogId);


		expectedParams.put(PAGE_ID.paramName, pageId);
		expectedParams.put("pageCatalogVersion", catalogMap.get(pageCatalogId).getActiveCatalogVersion());

		//when
		final Map<String, Object> actual = dao.buildParams(catalogVersion, params);

		//then
		Assert.assertEquals(expectedParams, actual);
	}

	@Test
	public void buildQueryNoParamTest()
	{
		//given
		final String expectedQuery = SELECT + FROM + WHERE + ORDER_BY;

		final Map<String, String> searchCriteria = new HashMap<>();

		//when
		final Map<String, Object> params = dao.buildParams(catalogVersion, searchCriteria);
		final String actualQuery = dao.buildQuery(params);

		//then
		Assert.assertEquals(expectedQuery, actualQuery);
	}

	@Test
	public void buildQuerySingleParamTest()
	{
		//given
		final String expectedQuery = SELECT + FROM + WHERE + " AND {a.code} = ?actionCode" + ORDER_BY;

		final Map<String, String> searchCriteria = new HashMap<>();
		searchCriteria.put(ACTION_CODE.paramName, "myAction");


		//when
		final Map<String, Object> params = dao.buildParams(catalogVersion, searchCriteria);
		final String actualQuery = dao.buildQuery(params);

		//then
		Assert.assertEquals(expectedQuery, actualQuery);
	}

	@Test
	public void buildQueryPageIdParamTest()
	{
		//given
		final String expectedQuery = SELECT + FROM + WHERE + " AND {a.pk} IN (" + PAGE_ID_QUERY + ")" + ORDER_BY;

		final Map<String, String> searchCriteria = new HashMap<>();
		searchCriteria.put(PAGE_ID.paramName, "myPage");


		//when
		final Map<String, Object> params = dao.buildParams(catalogVersion, searchCriteria);
		final String actualQuery = dao.buildQuery(params);

		//then
		Assert.assertEquals(expectedQuery, actualQuery);
	}

	@Test
	public void buildQueryPageIdMultipleParamTest()
	{
		//given
		final String expectedQuery = SELECT + FROM + MULTI_WHERE + " AND LOWER({c.name}) LIKE LOWER(?customizationName)"
				+ " AND {a.pk} IN (" + PAGE_ID_QUERY + ")" + MULTI_ORDER;

		final Map<String, String> searchCriteria = new HashMap<>();
		searchCriteria.put(PAGE_ID.paramName, "myPage");
		searchCriteria.put(CUSTOMIZATION_NAME.paramName, "myCust");
		searchCriteria.put("catalogs", "ALL");


		//when
		final Map<String, Object> params = dao.buildParams(catalogVersion, searchCriteria);
		final String actualQuery = dao.buildQuery(params);

		//then
		Assert.assertEquals(expectedQuery, actualQuery);
	}

	@Test
	public void buildParamsParentsCatalogNoParentsEmptyResultTest()
	{
		//given
		final Map<String, String> params = new HashMap<>();
		params.put("catalogs", "Parents");
		final CatalogVersionModel singleCatalogVersion = buildSingleCatalog("catalog", CATALOG_VERSION, true);
		final SearchPageData<?> searchPageData = buildPagination(0, 10);

		//when
		final SearchPageData<CxCmsActionModel> actual = dao.getActions(singleCatalogVersion, params, searchPageData);

		Assert.assertTrue(actual.getResults().isEmpty());
		Assert.assertEquals(actual.getPagination().getPageSize(), 10);
	}

	private void buildExpectedCatalogHierarchy(final CatalogVersionModel initialCv)
	{
		final List<CatalogVersionModel> catalogList = new ArrayList<>();

		CatalogVersionModel cv = initialCv;
		int index = 0;
		while (cv != null)
		{
			expectedParams.put("catalogVersions" + index, cv);
			catalogList.add(cv);
			cv = getParentCatalogVersion(cv);
			index += 1;
		}
		expectedParams.put("catalogVersions", catalogList);
	}

	private CatalogVersionModel getParentCatalogVersion(final CatalogVersionModel version)
	{
		final ContentCatalogModel catalog = (ContentCatalogModel) version.getCatalog();
		if (catalog.getSuperCatalog() != null)
		{
			return catalog.getSuperCatalog().getActiveCatalogVersion();
		}
		else
		{
			return null;
		}
	}

	private CatalogVersionModel buildCatalogHierarchy(final int depth, final boolean active)
	{
		final CatalogVersionModel result = buildSingleCatalog("catalog", CATALOG_VERSION, active);

		ContentCatalogModel catalog = (ContentCatalogModel) result.getCatalog();
		for (int i = 0; i < depth; ++i)
		{
			final CatalogVersionModel level = buildSingleCatalog("catalog" + i, "version", true);
			final ContentCatalogModel superCatalog = (ContentCatalogModel) level.getCatalog();
			catalog.setSuperCatalog(superCatalog);
			catalog = superCatalog;
		}

		Mockito.when(catalogDao.findCatalogById(Mockito.any())).then(invocationOnMock -> {
			final Object name = invocationOnMock.getArguments()[0];
			final ContentCatalogModel catalogModel = catalogMap.get(name);
			if (catalogModel == null)
			{
				throw new UnknownIdentifierException("no catalog with id " + name);
			}
			else
			{
				return catalogModel;
			}
		});


		return result;
	}

	private CatalogVersionModel buildSingleCatalog(final String name, final String version, final boolean active)
	{
		final ContentCatalogModel catalog = new ContentCatalogModel()
		{
			@Override
			public String toString()
			{
				return getId();
			}
		};

		catalog.setId(name);

		final CatalogVersionModel result = new CatalogVersionModel()
		{
			@Override
			public String toString()
			{
				return getCatalog().getId() + ":" + getVersion();
			}
		};
		result.setVersion(version);
		result.setActive(active);
		result.setCatalog(catalog);

		catalog.setCatalogVersions(Sets.newHashSet(result));
		catalogMap.put(name, catalog);

		if (active)
		{
			catalog.setActiveCatalogVersion(result);
		}

		return result;
	}

	private SearchPageData<?> buildPagination(final int page, final int pageSize)
	{
		final SearchPageData<?> searchPageData = new SearchPageData<>();
		searchPageData.setPagination(new PaginationData());
		searchPageData.getPagination().setPageSize(pageSize);
		searchPageData.getPagination().setCurrentPage(page);
		searchPageData.getPagination().setNeedsTotal(true);
		return searchPageData;
	}
}

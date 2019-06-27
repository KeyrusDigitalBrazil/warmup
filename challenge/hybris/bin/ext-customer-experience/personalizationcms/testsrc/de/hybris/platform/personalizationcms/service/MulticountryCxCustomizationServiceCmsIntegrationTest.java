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
package de.hybris.platform.personalizationcms.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.servicelayer.data.PaginationData;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.personalizationservices.customization.CxCustomizationService;
import de.hybris.platform.personalizationservices.enums.CxItemStatus;
import de.hybris.platform.personalizationservices.model.CxCustomizationModel;
import de.hybris.platform.personalizationservices.service.CxService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;


@IntegrationTest
public class MulticountryCxCustomizationServiceCmsIntegrationTest extends ServicelayerTransactionalTest
{

	private static final String NEGATE_PAGE = "negatePageId";
	private static final String PAGE_ID = "pageId";
	private static final String STATUSES = "statuses";
	private static final String CATALOGS = "catalogs";
	private static final String NAME = "name";

	private static final String TOP_CATALOG = "multiCatalog1";
	private static final String MID_CATALOG = "multiCatalog2";
	private static final String LEAF_1_CATALOG = "multiCatalog3a";
	private static final String LEAF_2_CATALOG = "multiCatalog3b";
	private static final String VERSION = "Online";

	private static final String CUSTOMIZATION1 = "customization1";
	private static final String CUSTOMIZATION2 = "customization2";
	private static final String CUSTOMIZATION3 = "customization3";
	private static final String CUSTOMIZATION4 = "random name";
	private static final String CUSTOMIZATION5 = "customization5";
	private static final String CUSTOMIZATION6 = "customization6";



	@Resource
	private CxService cxService;
	@Resource
	private CxCustomizationService cxCustomizationService;
	@Resource
	private UserService userService;
	@Resource
	private CatalogVersionService catalogVersionService;

	private SearchPageData<?> searchPage;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		importCsv("/personalizationcms/test/testdata_personalizationcms_multicountry.impex", "utf-8");

		searchPage = new SearchPageData<>();
		searchPage.setPagination(new PaginationData());
		searchPage.getPagination().setCurrentPage(0);
		searchPage.getPagination().setPageSize(100);
	}

	private CatalogVersionModel getCatalogVersion(final String catalog)
	{
		return catalogVersionService.getCatalogVersion(catalog, VERSION);
	}

	@Test
	public void findEnabledCustomizationsTopCatalog()
	{
		//given
		final List<String> expected = Lists.newArrayList(CUSTOMIZATION1);

		final Map<String, String> params = new HashMap<String, String>();
		params.put(CATALOGS, "current");
		params.put(STATUSES, CxItemStatus.ENABLED.getCode());

		final CatalogVersionModel catalogVersion = getCatalogVersion(TOP_CATALOG);

		//when
		final SearchPageData<CxCustomizationModel> actual = cxCustomizationService.getCustomizations(catalogVersion, params,
				searchPage);

		//then
		assertSearchPage(expected, actual);
	}

	@Test
	public void findDisabledCustomizationsTopCatalog()
	{
		//given
		final List<String> expected = Lists.newArrayList(CUSTOMIZATION2);

		final Map<String, String> params = new HashMap<String, String>();
		params.put(CATALOGS, "all");
		params.put(STATUSES, CxItemStatus.DISABLED.getCode());

		final CatalogVersionModel catalogVersion = getCatalogVersion(TOP_CATALOG);

		//when
		final SearchPageData<CxCustomizationModel> actual = cxCustomizationService.getCustomizations(catalogVersion, params,
				searchPage);

		//then
		assertSearchPage(expected, actual);
	}

	@Test
	public void findEnabledCustomizationsLeafCatalog()
	{
		//given
		final List<String> expected = Lists.newArrayList(CUSTOMIZATION3, CUSTOMIZATION4, CUSTOMIZATION5, CUSTOMIZATION1);

		final Map<String, String> params = new HashMap<String, String>();
		params.put(CATALOGS, "all");
		params.put(STATUSES, CxItemStatus.ENABLED.getCode());

		final CatalogVersionModel catalogVersion = getCatalogVersion(LEAF_1_CATALOG);

		//when
		final SearchPageData<CxCustomizationModel> actual = cxCustomizationService.getCustomizations(catalogVersion, params,
				searchPage);

		//then
		assertSearchPage(expected, actual);
	}

	@Test
	public void findDisabledCustomizationsLeafCatalog()
	{
		//given
		final List<String> expected = Lists.newArrayList(CUSTOMIZATION2);

		final Map<String, String> params = new HashMap<String, String>();
		params.put(CATALOGS, "parents");
		params.put(STATUSES, CxItemStatus.DISABLED.getCode());

		final CatalogVersionModel catalogVersion = getCatalogVersion(MID_CATALOG);

		//when
		final SearchPageData<CxCustomizationModel> actual = cxCustomizationService.getCustomizations(catalogVersion, params,
				searchPage);

		//then
		assertSearchPage(expected, actual);
	}

	// ----  ----  ---- //
	@Test
	public void findCustomizationsByNameLeafCatalog()
	{
		//given
		final List<String> expected = Lists.newArrayList(CUSTOMIZATION3, CUSTOMIZATION5, CUSTOMIZATION1);

		final Map<String, String> params = new HashMap<String, String>();
		params.put(CATALOGS, "all");
		params.put(STATUSES, CxItemStatus.ENABLED.getCode());
		params.put(NAME, "ust");

		final CatalogVersionModel catalogVersion = getCatalogVersion(LEAF_1_CATALOG);

		//when
		final SearchPageData<CxCustomizationModel> actual = cxCustomizationService.getCustomizations(catalogVersion, params,
				searchPage);

		//then
		assertSearchPage(expected, actual);
	}


	// ----  ----  ---- //
	@Test
	public void findCustomizationsByPageIdLeafCatalog()
	{
		//given
		final List<String> expected = Lists.newArrayList(CUSTOMIZATION3, CUSTOMIZATION4, CUSTOMIZATION1);

		final Map<String, String> params = new HashMap<String, String>();
		params.put(CATALOGS, "ALL");
		params.put(STATUSES, CxItemStatus.ENABLED.getCode());
		params.put(PAGE_ID, "page3");

		final CatalogVersionModel catalogVersion = getCatalogVersion(LEAF_1_CATALOG);

		//when
		final SearchPageData<CxCustomizationModel> actual = cxCustomizationService.getCustomizations(catalogVersion, params,
				searchPage);

		//then
		assertSearchPage(expected, actual);
	}

	@Test
	public void findCustomizationsByPageIdAndNameLeafCatalog()
	{
		//given
		final List<String> expected = Lists.newArrayList(CUSTOMIZATION3, CUSTOMIZATION1);

		final Map<String, String> params = new HashMap<String, String>();
		params.put(CATALOGS, "all");
		params.put(STATUSES, CxItemStatus.ENABLED.getCode());
		params.put(NAME, "ust");
		params.put(PAGE_ID, "page3");

		final CatalogVersionModel catalogVersion = getCatalogVersion(LEAF_1_CATALOG);

		//when
		final SearchPageData<CxCustomizationModel> actual = cxCustomizationService.getCustomizations(catalogVersion, params,
				searchPage);

		//then
		assertSearchPage(expected, actual);
	}

	@Test
	public void findCustomizationsNotOnPageLeafCatalog()
	{
		//given
		final List<String> expected = Lists.newArrayList(CUSTOMIZATION3, CUSTOMIZATION4, CUSTOMIZATION5, CUSTOMIZATION1);

		final Map<String, String> params = new HashMap<String, String>();
		params.put(CATALOGS, "ALL");
		params.put(STATUSES, CxItemStatus.ENABLED.getCode());
		params.put(PAGE_ID, "page2");
		params.put(NEGATE_PAGE, "true");

		final CatalogVersionModel catalogVersion = getCatalogVersion(LEAF_1_CATALOG);

		//when
		final SearchPageData<CxCustomizationModel> actual = cxCustomizationService.getCustomizations(catalogVersion, params,
				searchPage);

		//then
		assertSearchPage(expected, actual);
	}

	@Test
	public void findCustomizationsNotOnPageOnlyLeafCatalog()
	{
		//given
		final List<String> expected = Lists.newArrayList(CUSTOMIZATION3, CUSTOMIZATION4, CUSTOMIZATION5);

		final Map<String, String> params = new HashMap<String, String>();
		params.put(CATALOGS, "current");
		params.put(STATUSES, CxItemStatus.ENABLED.getCode());
		params.put(PAGE_ID, "page2");
		params.put(NEGATE_PAGE, "true");

		final CatalogVersionModel catalogVersion = getCatalogVersion(LEAF_1_CATALOG);

		//when
		final SearchPageData<CxCustomizationModel> actual = cxCustomizationService.getCustomizations(catalogVersion, params,
				searchPage);

		//then
		assertSearchPage(expected, actual);
	}

	// ----  ----  ---- //
	private void assertSearchPage(final List<String> expectedCustomizations,
			final SearchPageData<CxCustomizationModel> actualResults)
	{
		assertNotNull("result page should not be null", actualResults);
		assertNotNull("result should not be null", actualResults.getResults());

		final List<CxCustomizationModel> results = actualResults.getResults();
		final List<String> actualCustomizations = results.stream().map(m -> m.getName()).collect(Collectors.toList());
		assertEquals("actual list of customizations should match expected list", expectedCustomizations, actualCustomizations);
	}


}

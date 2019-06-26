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
package de.hybris.platform.cms2.servicelayer.services.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.exceptions.RestrictionEvaluationException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.restrictions.CMSCatalogRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSCategoryRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSInverseRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSProductRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSTimeRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSUserGroupRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSUserRestrictionModel;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cms2.servicelayer.data.impl.DefaultCMSDataFactory;
import de.hybris.platform.cms2.servicelayer.services.CMSRestrictionService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultCMSRestrictionServiceIntegrationTest extends ServicelayerTest
{
	private final String USER_A = "userA";
	private final String USER_B = "userB";
	private final String USER_GROUP_A = "userAGroup";
	private final String CATALOG_ID = "sampleCatalog";
	private final String CATEGORY_A = "sampleCategoryA";
	private final String CATEGORY_B = "sampleCategoryB";
	private final String PRODUCT_A = "productA";
	private final String PRODUCT_B = "productB";

	@Resource
	private ModelService modelService;
	@Resource
	private FlexibleSearchService flexibleSearchService;
	@Resource
	private CMSRestrictionService cmsRestrictionService;
	@Resource
	private UserService userService;
	@Resource
	private CatalogService catalogService;
	@Resource
	private CategoryService categoryService;
	@Resource
	private ProductService productService;
	@Resource
	private DefaultCMSDataFactory cmsDataFactory;


	private ContentPageModel homepageGlobal;
	private ContentPageModel homepageRegion;
	private ContentPageModel homepageLocal;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		createDefaultUsers();
		importCsv("/test/cms2TestData.csv", "UTF-8");
	}

	public void multiCountrySetUp() throws ImpExException
	{
		importCsv("/test/cmsMultiCountryTestData.csv", "UTF-8");

		homepageGlobal = flexibleSearchService.<ContentPageModel>search("SELECT {pk} FROM {ContentPage} WHERE {uid} = ?uid",
				Collections.singletonMap("uid", "TestHomePageGlobal")).getResult().iterator().next();

		homepageRegion = flexibleSearchService.<ContentPageModel>search("SELECT {pk} FROM {ContentPage} WHERE {uid} = ?uid",
				Collections.singletonMap("uid", "TestHomePageRegionEU")).getResult().iterator().next();

		homepageLocal = flexibleSearchService.<ContentPageModel>search("SELECT {pk} FROM {ContentPage} WHERE {uid} = ?uid",
				Collections.singletonMap("uid", "TestHomePageLocalIT")).getResult().iterator().next();
	}

	protected CMSCategoryRestrictionModel getCategoryRestriction(final List<CategoryModel> categories)
	{
		final CMSCategoryRestrictionModel categoryRestriction = modelService.create(CMSCategoryRestrictionModel.class);
		categoryRestriction.setCategories(categories);
		categoryRestriction.setName("FooBar");
		categoryRestriction.setUid("FooBar");
		modelService.save(categoryRestriction);
		return categoryRestriction;
	}

	protected CMSTimeRestrictionModel getTimeRestriction(final int before, final int after)
	{
		final CMSTimeRestrictionModel timeRestriction = modelService.create(CMSTimeRestrictionModel.class);
		timeRestriction.setActiveFrom(DateUtils.addDays(new Date(), before));
		timeRestriction.setActiveUntil(DateUtils.addDays(new Date(), after));
		timeRestriction.setUseStoreTimeZone(true);
		timeRestriction.setUid("timeRestriction");
		modelService.save(timeRestriction);
		return timeRestriction;
	}

	protected CMSInverseRestrictionModel getInverseTimeRestriction(final int before, final int after)
	{
		final CMSTimeRestrictionModel timeRestriction = getTimeRestriction(before, after);
		final CMSInverseRestrictionModel inverseRestriction = modelService.create(CMSInverseRestrictionModel.class);
		inverseRestriction.setOriginalRestriction(timeRestriction);
		inverseRestriction.setUid("inverseTimeRestriction");
		modelService.save(inverseRestriction);
		return inverseRestriction;
	}

	protected CMSUserGroupRestrictionModel getUserGroupRestriction(final UserGroupModel userGroupModel)
	{
		final CMSUserGroupRestrictionModel userGroupRestriction = modelService.create(CMSUserGroupRestrictionModel.class);
		userGroupRestriction.setUserGroups(Arrays.asList(userGroupModel));
		userGroupRestriction.setIncludeSubgroups(false);
		userGroupRestriction.setUid("userGroupRestriction");
		modelService.save(userGroupRestriction);
		return userGroupRestriction;
	}

	protected CMSUserRestrictionModel getUserRestriction(final UserModel userModel)
	{
		final CMSUserRestrictionModel userRestriction = modelService.create(CMSUserRestrictionModel.class);
		userRestriction.setUsers(Arrays.asList(userModel));
		userRestriction.setUid("userRestriction");
		modelService.save(userRestriction);
		return userRestriction;
	}

	protected RestrictionData getRestrictionData(final CatalogModel catalog, final CategoryModel category,
			final ProductModel product)
	{
		final RestrictionData restrictionData = cmsDataFactory.createRestrictionData();
		restrictionData.setCatalog(catalog);
		restrictionData.setCategory(category);
		restrictionData.setProduct(product);
		return restrictionData;
	}

	protected CMSCatalogRestrictionModel getCatalogRestriction(final List<CatalogModel> catalogs)
	{
		final CMSCatalogRestrictionModel catalogRestriction = modelService.create(CMSCatalogRestrictionModel.class);
		catalogRestriction.setCatalogs(catalogs);
		catalogRestriction.setUid("catalogRestriction");
		modelService.save(catalogRestriction);
		return catalogRestriction;
	}

	protected CMSProductRestrictionModel getProductRestriction(final ProductModel productModel)
	{
		final CMSProductRestrictionModel productRestriction = modelService.create(CMSProductRestrictionModel.class);
		productRestriction.setProducts(Arrays.asList(productModel));
		productRestriction.setUid("productRestriction");
		modelService.save(productRestriction);
		return productRestriction;
	}

	protected CategoryModel getCategoryByCode(final String code)
	{
		final CategoryModel example = new CategoryModel();
		example.setCode(code);
		return flexibleSearchService.getModelByExample(example);
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSRestrictionService#getCategoryCodesForRestriction(de.hybris.platform.cms2.model.restrictions.CMSCategoryRestrictionModel)}
	 * .
	 */
	@Test
	public void testGetCategoryCodesForRestriction()
	{
		final List<CategoryModel> categories = new ArrayList<>();
		categories.add(getCategoryByCode("testCategory0"));
		categories.add(getCategoryByCode("testCategory1"));
		categories.add(getCategoryByCode("testCategory2"));

		final CMSCategoryRestrictionModel restriction = getCategoryRestriction(categories);
		final Collection<String> categoryCodes = cmsRestrictionService.getCategoryCodesForRestriction(restriction);
		assertThat(categoryCodes).hasSize(3);
		assertThat(categoryCodes).contains("testCategory0", "testCategory1", "testCategory2");
	}

	@Test
	public void shouldFindGlobalPage() throws ImpExException
	{
		multiCountrySetUp();

		final AbstractPageModel[] data = new AbstractPageModel[]
				{ homepageGlobal };
		final Collection<AbstractPageModel> pages = Arrays.asList(data);

		final Collection<AbstractPageModel> evaluatePages = cmsRestrictionService.evaluatePages(pages, null);

		assertThat(evaluatePages, hasSize(1));
	}

	@Test
	public void shouldFindRegionPage() throws ImpExException
	{
		multiCountrySetUp();

		final AbstractPageModel[] data = new AbstractPageModel[]
				{ homepageRegion };
		final Collection<AbstractPageModel> pages = Arrays.asList(data);

		final Collection<AbstractPageModel> evaluatePages = cmsRestrictionService.evaluatePages(pages, null);

		assertThat(evaluatePages, hasSize(1));
	}

	@Test
	public void shouldFindLocalPage() throws ImpExException
	{
		multiCountrySetUp();

		final AbstractPageModel[] data = new AbstractPageModel[]
				{ homepageLocal };
		final Collection<AbstractPageModel> pages = Arrays.asList(data);

		final Collection<AbstractPageModel> evaluatePages = cmsRestrictionService.evaluatePages(pages, null);

		assertThat(evaluatePages, hasSize(1));
	}

	@Test
	public void shouldEvaluateTimeRestriction2True() throws RestrictionEvaluationException
	{
		// GIVEN
		final CMSTimeRestrictionModel timeRestriction = getTimeRestriction(-1, 1);

		// WHEN
		final boolean result = cmsRestrictionService.evaluate(timeRestriction, null);

		// THEN
		assertTrue(result);
	}

	@Test
	public void shouldEvaluateTimeRestriction2False() throws RestrictionEvaluationException
	{
		// GIVEN
		final CMSTimeRestrictionModel timeRestriction = getTimeRestriction(1, 2);

		// WHEN
		final boolean result = cmsRestrictionService.evaluate(timeRestriction, null);

		// THEN
		assertFalse(result);
	}

	@Test
	public void shouldEvaluateUserRestriction2False() throws Exception
	{
		// GIVEN
		final UserModel userA = userService.getUserForUID(USER_A);
		final UserModel userB = userService.getUserForUID(USER_B);
		userService.setCurrentUser(userB);
		final CMSUserRestrictionModel userRestriction = getUserRestriction(userA);

		// WHEN
		final boolean result = cmsRestrictionService.evaluate(userRestriction, null);

		// THEN
		assertFalse(result);
	}

	@Test
	public void shouldEvaluateUserRestriction2True() throws Exception
	{
		// GIVEN
		final UserModel userA = userService.getUserForUID(USER_A);
		userService.setCurrentUser(userA);
		final CMSUserRestrictionModel userRestriction = getUserRestriction(userA);

		// WHEN
		final boolean result = cmsRestrictionService.evaluate(userRestriction, null);

		// THEN
		assertTrue(result);
	}

	@Test
	public void shouldEvaluateUserGroupRestriction2True() throws RestrictionEvaluationException
	{
		// GIVEN
		final UserModel userA = userService.getUserForUID(USER_A);
		final UserGroupModel userGroupA = userService.getUserGroupForUID(USER_GROUP_A);
		final CMSUserGroupRestrictionModel userGroupRestriction = getUserGroupRestriction(userGroupA);
		userService.setCurrentUser(userA);

		// WHEN
		final boolean result = cmsRestrictionService.evaluate(userGroupRestriction, null);

		// THEN
		assertTrue(result);
	}

	@Test
	public void shouldEvaluateUserGroupRestriction2False() throws RestrictionEvaluationException
	{
		// GIVEN
		final UserModel userB = userService.getUserForUID(USER_B);
		final UserGroupModel userGroupA = userService.getUserGroupForUID(USER_GROUP_A);
		final CMSUserGroupRestrictionModel userGroupRestriction = getUserGroupRestriction(userGroupA);
		userService.setCurrentUser(userB);

		// WHEN
		final boolean result = cmsRestrictionService.evaluate(userGroupRestriction, null);

		// THEN
		assertFalse(result);
	}

	@Test
	public void shouldEvaluateCatalogRestriction2False() throws RestrictionEvaluationException
	{
		// GIVEN
		final CatalogModel catalog = catalogService.getCatalogForId(CATALOG_ID);
		final CMSCatalogRestrictionModel catalogRestriction = getCatalogRestriction(Collections.EMPTY_LIST);
		final RestrictionData restrictionData = getRestrictionData(catalog, null, null);

		// WHEN
		final boolean result = cmsRestrictionService.evaluate(catalogRestriction, restrictionData);

		// THEN
		assertFalse(result);
	}

	@Test
	public void shouldEvaluateCatalogRestriction2True() throws RestrictionEvaluationException
	{
		// GIVEN
		final CatalogModel catalog = catalogService.getCatalogForId(CATALOG_ID);
		final CMSCatalogRestrictionModel catalogRestriction = getCatalogRestriction(Arrays.asList(catalog));
		final RestrictionData restrictionData = getRestrictionData(catalog, null, null);

		// WHEN
		final boolean result = cmsRestrictionService.evaluate(catalogRestriction, restrictionData);

		// THEN
		assertTrue(result);
	}

	@Test
	public void shouldEvaluateCategoryRestriction2True() throws RestrictionEvaluationException
	{
		// GIVEN
		final CategoryModel categoryA = categoryService.getCategoryForCode(CATEGORY_A);
		final CMSCategoryRestrictionModel categoryRestriction = getCategoryRestriction(Arrays.asList(categoryA));
		final RestrictionData restrictionData = getRestrictionData(null, categoryA, null);

		// WHEN
		final boolean result = cmsRestrictionService.evaluate(categoryRestriction, restrictionData);

		// THEN
		assertTrue(result);
	}

	@Test
	public void shouldEvaluateCategoryRestriction2False() throws RestrictionEvaluationException
	{
		// GIVEN
		final CategoryModel categoryA = categoryService.getCategoryForCode(CATEGORY_A);
		final CategoryModel categoryB = categoryService.getCategoryForCode(CATEGORY_B);
		final CMSCategoryRestrictionModel categoryRestriction = getCategoryRestriction(Arrays.asList(categoryA));
		final RestrictionData restrictionData = getRestrictionData(null, categoryB, null);

		// WHEN
		final boolean result = cmsRestrictionService.evaluate(categoryRestriction, restrictionData);

		// THEN
		assertFalse(result);
	}

	@Test
	public void shouldEvaluateProductRestriction2True() throws RestrictionEvaluationException
	{
		// GIVEN
		final ProductModel productA = productService.getProductForCode(PRODUCT_A);
		final RestrictionData restrictionData = getRestrictionData(null, null, productA);
		final CMSProductRestrictionModel productRestriction = getProductRestriction(productA);

		// WHEN
		final boolean result = cmsRestrictionService.evaluate(productRestriction, restrictionData);

		// THEN
		assertTrue(result);
	}

	@Test
	public void shouldEvaluateProductRestriction2False() throws RestrictionEvaluationException
	{
		// GIVEN
		final ProductModel productA = productService.getProductForCode(PRODUCT_A);
		final ProductModel productB = productService.getProductForCode(PRODUCT_B);
		final RestrictionData restrictionData = getRestrictionData(null, null, productB);
		final CMSProductRestrictionModel productRestriction = getProductRestriction(productA);

		// WHEN
		final boolean result = cmsRestrictionService.evaluate(productRestriction, restrictionData);

		// THEN
		assertFalse(result);
	}

	@Test
	public void shouldEvaluateInverseTimeRestriction2False() throws RestrictionEvaluationException
	{
		// GIVEN
		final CMSInverseRestrictionModel inverseRestriction = getInverseTimeRestriction(-1, 1);

		// WHEN
		final boolean result = cmsRestrictionService.evaluate(inverseRestriction, null);

		// THEN
		assertFalse(result);
	}

	@Test
	public void shouldEvaluateInverseTimeRestriction2True() throws RestrictionEvaluationException
	{
		// GIVEN
		final CMSInverseRestrictionModel inverseRestriction = getInverseTimeRestriction(1, 2);

		// WHEN
		final boolean result = cmsRestrictionService.evaluate(inverseRestriction, null);

		// THEN
		assertTrue(result);
	}
}

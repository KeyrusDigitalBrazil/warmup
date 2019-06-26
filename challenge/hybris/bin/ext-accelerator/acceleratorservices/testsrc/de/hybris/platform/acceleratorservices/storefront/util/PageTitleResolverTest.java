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
package de.hybris.platform.acceleratorservices.storefront.util;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commerceservices.category.CommerceCategoryService;
import de.hybris.platform.commerceservices.search.facetdata.BreadcrumbData;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.fest.assertions.Assertions;
import org.jgroups.protocols.pbcast.STATE;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class PageTitleResolverTest
{
	private static final String PRODUCT_CODE = "productCode";
	private static final String PRODUCT_NAME = "productName";
	private static final String TITLE = "title";
	private static final String CATEGORY_NAME = "categoryName";
	private static final String SITE_NAME = "siteName";
	private static final String SEARCH_PHRASE = "searchPhrase";
	private static final String FACET_NAME = "facetName";
	PageTitleResolver resolver;
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Mock
	CMSSiteService cmsSiteService;
	@Mock
	CMSSiteModel cmsSiteModel;
	@Mock
	CategoryModel categoryCode;
	@Mock
	CommerceCategoryService commerceCategoryService;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		resolver = new PageTitleResolver();
		resolver.setCmsSiteService(cmsSiteService);
		resolver.setCommerceCategoryService(commerceCategoryService);
		BDDMockito.given(cmsSiteService.getCurrentSite()).willReturn(cmsSiteModel);
		BDDMockito.given(categoryCode.getName()).willReturn(CATEGORY_NAME);
		BDDMockito.given(cmsSiteModel.getName()).willReturn(SITE_NAME);
	}

	@Test
	public void testResolveContentPageTitle()
	{
		final String result = resolver.resolveContentPageTitle(TITLE);
		Assertions.assertThat(result.equalsIgnoreCase(TITLE + PageTitleResolver.TITLE_WORD_SEPARATOR + SITE_NAME)).isTrue();
	}

	@Test
	public void testResolveHomePageTitle()
	{
		final String result = resolver.resolveHomePageTitle(TITLE);
		Assertions.assertThat(result.equalsIgnoreCase(SITE_NAME + PageTitleResolver.TITLE_WORD_SEPARATOR + TITLE)).isTrue();
	}

	@Test
	public void testResolveCategoryPageTitle()
	{
		List<BreadcrumbData<STATE>> appliedFacets = null;
		String result = resolver.resolveCategoryPageTitle(categoryCode, appliedFacets);
		Assertions.assertThat(result.equalsIgnoreCase(CATEGORY_NAME + PageTitleResolver.TITLE_WORD_SEPARATOR + SITE_NAME)).isTrue();
		appliedFacets = new ArrayList<>();
		result = resolver.resolveCategoryPageTitle(categoryCode, appliedFacets);
		Assertions.assertThat(result.equalsIgnoreCase(CATEGORY_NAME + PageTitleResolver.TITLE_WORD_SEPARATOR + SITE_NAME)).isTrue();
		final BreadcrumbData<STATE> facet = new BreadcrumbData<>();
		facet.setFacetValueName(FACET_NAME);
		appliedFacets.add(facet);
		result = resolver.resolveCategoryPageTitle(categoryCode, appliedFacets);
		Assertions.assertThat(result.equalsIgnoreCase(FACET_NAME + PageTitleResolver.TITLE_WORD_SEPARATOR + SITE_NAME)).isTrue();
	}

	@Test
	public void testResolveProductPageTitle()
	{
		final ProductModel product = Mockito.mock(ProductModel.class);
		BDDMockito.given(product.getName()).willReturn(PRODUCT_NAME);
		BDDMockito.given(product.getCode()).willReturn(PRODUCT_CODE);

		String result = resolver.resolveProductPageTitle(product);
		Assertions.assertThat(result.equalsIgnoreCase(PRODUCT_NAME + PageTitleResolver.TITLE_WORD_SEPARATOR + SITE_NAME)).isTrue();

		BDDMockito.given(product.getName()).willReturn("");
		BDDMockito.given(product.getCode()).willReturn(PRODUCT_CODE);

		result = resolver.resolveProductPageTitle(product);
		Assertions.assertThat(result.equalsIgnoreCase(PRODUCT_CODE + PageTitleResolver.TITLE_WORD_SEPARATOR + SITE_NAME)).isTrue();

		BDDMockito.given(product.getName()).willReturn(PRODUCT_NAME);
		BDDMockito.given(product.getCode()).willReturn(PRODUCT_CODE);
		final List<CategoryModel> categoryList = new ArrayList<>();
		final CategoryModel categoryModel = Mockito.mock(CategoryModel.class);
		categoryList.add(categoryModel);
		final Collection<List<CategoryModel>> categoryCollection = new LinkedList<>();
		categoryCollection.add(categoryList);
		BDDMockito.given(categoryModel.getName()).willReturn(CATEGORY_NAME);
		BDDMockito.given(commerceCategoryService.getPathsForCategory(categoryModel)).willReturn(categoryCollection);
		BDDMockito.given(product.getSupercategories()).willReturn(categoryList);

		result = resolver.resolveProductPageTitle(product);
		Assertions.assertThat(result.equalsIgnoreCase(PRODUCT_NAME + PageTitleResolver.TITLE_WORD_SEPARATOR + CATEGORY_NAME
				+ PageTitleResolver.TITLE_WORD_SEPARATOR + SITE_NAME)).isTrue();
	}

	@Test
	public void testResolveSearchPageTitle()
	{
		thrown.expect(NullPointerException.class);
		String result = resolver.resolveSearchPageTitle(SEARCH_PHRASE, null);
		final List<BreadcrumbData<STATE>> appliedFacets = new ArrayList<>();
		result = resolver.resolveSearchPageTitle(SEARCH_PHRASE, appliedFacets);
		Assertions.assertThat(result.equalsIgnoreCase(SEARCH_PHRASE + PageTitleResolver.TITLE_WORD_SEPARATOR + SITE_NAME)).isTrue();

		final BreadcrumbData<STATE> facet = new BreadcrumbData<>();
		facet.setFacetValueName(FACET_NAME);
		appliedFacets.add(facet);
		result = resolver.resolveSearchPageTitle(SEARCH_PHRASE, appliedFacets);
		Assertions.assertThat(result.equalsIgnoreCase(SEARCH_PHRASE + PageTitleResolver.TITLE_WORD_SEPARATOR + FACET_NAME
				+ PageTitleResolver.TITLE_WORD_SEPARATOR + SITE_NAME)).isTrue();
	}
}

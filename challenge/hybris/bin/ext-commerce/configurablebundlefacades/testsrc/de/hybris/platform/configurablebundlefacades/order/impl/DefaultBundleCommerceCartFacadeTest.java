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


package de.hybris.platform.configurablebundlefacades.order.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.ProductSearchFacade;
import de.hybris.platform.commercefacades.search.data.SearchFilterQueryData;
import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.search.facetdata.BreadcrumbData;
import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.commerceservices.search.facetdata.FacetValueData;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.service.data.RemoveEntryGroupParameter;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.enums.GroupType;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.EntryGroupService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

import org.apache.commons.collections.CollectionUtils;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static de.hybris.platform.configurablebundleservices.constants.ConfigurableBundleServicesConstants.NEW_BUNDLE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * JUnit test suite for {@link DefaultBundleCommerceCartFacade}
 */
@UnitTest
public class DefaultBundleCommerceCartFacadeTest
{
	@Mock
	private EntryGroupService entryGroupService;
	@Mock
	private CartService cartService;
	@Mock
	private ProductSearchFacade productSearchFacade;
	@Mock
	private BundleTemplateService bundleTemplateService;
	@Mock
	private CommerceCartService commerceCartService;
	@Mock
	private Converter<CommerceCartModification, CartModificationData> cartModificationConverter;
	@Mock
	private ProductService productService;
	@Mock
	private ModelService modelService;
	@InjectMocks
	private DefaultBundleCommerceCartFacade defaultBundleCommerceCartFacade;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private PageableData pageableData;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		final EntryGroup entryGroup = new EntryGroup();
		entryGroup.setGroupNumber(Integer.valueOf(1));
		entryGroup.setGroupType(GroupType.CONFIGURABLEBUNDLE);
		entryGroup.setExternalReferenceId("bundleId");
		final CartModel cartModel = new CartModel();
		final ProductSearchPageData<SearchStateData, ProductData> productSearchPageData = new ProductSearchPageData<>();
		productSearchPageData.setBreadcrumbs(Collections.emptyList());
		productSearchPageData.setFacets(Collections.emptyList());
		final SearchStateData searchStateData = new SearchStateData();
		final SearchQueryData searchQuery = new SearchQueryData();
		searchQuery.setValue("");
		searchStateData.setQuery(searchQuery);
		productSearchPageData.setCurrentQuery(searchStateData);
		pageableData = new PageableData();

		given(cartService.getSessionCart()).willReturn(cartModel);
		given(entryGroupService.getGroup(any(CartModel.class), any(Integer.class))).willReturn(entryGroup);
		given(productSearchFacade.textSearch(any(SearchStateData.class), any(PageableData.class)))
				.willReturn(productSearchPageData);
		given(entryGroupService.getGroupOfType(any(CartModel.class), anyCollectionOf(Integer.class), any(GroupType.class))).willReturn(entryGroup);
	}

	@Test
	public void shouldCreateBundleStructureForOldCart() throws CommerceCartModificationException
	{
		final CartModel cart = new CartModel();
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		entry.setOrder(cart);
		entry.setBundleNo(Integer.valueOf(2));
		final BundleTemplateModel component = new BundleTemplateModel();
		component.setId("BUNDLE");
		entry.setBundleTemplate(component);
		cart.setEntries(Collections.singletonList(entry));
		given(cartService.getSessionCart()).willReturn(cart);
		final EntryGroup group = new EntryGroup();
		group.setExternalReferenceId("BUNDLE");
		group.setGroupNumber(Integer.valueOf(10));
		given(bundleTemplateService.getBundleEntryGroup(entry)).willReturn(group);
		given(entryGroupService.getRoot(any(), any())).willReturn(group);
		given(entryGroupService.getLeaves(any())).willReturn(Collections.singletonList(group));

		defaultBundleCommerceCartFacade.addToCart("PRODUCT", 1L, 2, "BUNDLE", false);

		verify(bundleTemplateService).createBundleTree(any(), any());
		assertThat(entry.getEntryGroupNumbers(), hasItem(group.getGroupNumber()));
	}

	@Test
	public void shouldFailIfEntryGroupIsNotBundle()
	{
		final EntryGroup testTypeEntryGroup = new EntryGroup();
		testTypeEntryGroup.setGroupNumber(Integer.valueOf(1));
		testTypeEntryGroup.setGroupType(GroupType.valueOf("TEST"));

		given(entryGroupService.getGroupOfType(any(CartModel.class), anyCollectionOf(Integer.class), eq(GroupType.CONFIGURABLEBUNDLE))).willThrow(IllegalArgumentException.class);
		thrown.expect(IllegalArgumentException.class);

		defaultBundleCommerceCartFacade.getAllowedProducts(Integer.valueOf(1), "query", new PageableData());
	}

	@Test
	public void shouldCreateFilterQueryForBundleWhenInitQueryEmpty()
	{
		defaultBundleCommerceCartFacade.getAllowedProducts(Integer.valueOf(1), "", pageableData);

		final ArgumentCaptor<SearchStateData> searchStateDataCaptor = ArgumentCaptor.forClass(SearchStateData.class);
		verify(productSearchFacade).textSearch(searchStateDataCaptor.capture(), eq(pageableData));
		Assertions.assertThat(searchStateDataCaptor.getValue().getQuery().getValue()).isEqualTo("");
		Assertions.assertThat(searchStateDataCaptor.getValue().getQuery().getFilterQueries()).hasSize(1);

		final SearchFilterQueryData searchFilterQueryData = searchStateDataCaptor.getValue().getQuery().getFilterQueries().get(0);
		Assertions.assertThat(searchFilterQueryData.getKey()).isEqualTo("bundleTemplates");
		Assertions.assertThat(searchFilterQueryData.getValues().iterator().next()).isEqualTo("bundleId");
	}

	@Test
	public void shouldCreateFilterQueryForBundleWhenInitQueryNotEmpty()
	{
		defaultBundleCommerceCartFacade.getAllowedProducts(Integer.valueOf(1), ":oldQuery", pageableData);

		final ArgumentCaptor<SearchStateData> searchStateDataCaptor = ArgumentCaptor.forClass(SearchStateData.class);
		verify(productSearchFacade).textSearch(searchStateDataCaptor.capture(), eq(pageableData));
		Assertions.assertThat(searchStateDataCaptor.getValue().getQuery().getValue()).isEqualTo(":oldQuery");
		Assertions.assertThat(searchStateDataCaptor.getValue().getQuery().getFilterQueries()).hasSize(1);

		final SearchFilterQueryData searchFilterQueryData = searchStateDataCaptor.getValue().getQuery().getFilterQueries().get(0);
		Assertions.assertThat(searchFilterQueryData.getKey()).isEqualTo("bundleTemplates");
		Assertions.assertThat(searchFilterQueryData.getValues().iterator().next()).isEqualTo("bundleId");
	}

	@Test
	public void cartWithNullGroupsShouldBeValid()
	{
		given(cartService.getSessionCart()).willReturn(new CartModel());
		assertTrue(defaultBundleCommerceCartFacade.isCartValid());
	}

	@Test
	public void cartWithEmptyGroupsShouldBeValid()
	{
		final CartModel cart = new CartModel();
		cart.setEntryGroups(Collections.emptyList());
		given(cartService.getSessionCart()).willReturn(cart);
		given(entryGroupService.getNestedGroups(any(EntryGroup.class))).willAnswer(
				invocationOnMock -> Collections.singletonList((EntryGroup) invocationOnMock.getArguments()[0]));
		assertTrue(defaultBundleCommerceCartFacade.isCartValid());
	}

	@Test
	public void cartShouldBeValidIfAllGroupsAreValid()
	{
		final CartModel cart = new CartModel();
		final EntryGroup validGroup = new EntryGroup();
		validGroup.setErroneous(Boolean.FALSE);
		cart.setEntryGroups(Arrays.asList(new EntryGroup(), validGroup));
		given(cartService.getSessionCart()).willReturn(cart);
		given(entryGroupService.getNestedGroups(any(EntryGroup.class))).willAnswer(
				invocationOnMock -> Collections.singletonList((EntryGroup) invocationOnMock.getArguments()[0]));
		assertTrue(defaultBundleCommerceCartFacade.isCartValid());
	}

	@Test
	public void cartShouldBeInvalidIfAnyOfTheGroupsIsInvalid()
	{
		final CartModel cart = new CartModel();
		final EntryGroup validGroup = new EntryGroup();
		validGroup.setErroneous(Boolean.FALSE);
		final EntryGroup invalidGroup = new EntryGroup();
		invalidGroup.setErroneous(Boolean.TRUE);
		cart.setEntryGroups(Arrays.asList(new EntryGroup(), validGroup, invalidGroup));
		given(cartService.getSessionCart()).willReturn(cart);
		given(entryGroupService.getNestedGroups(any(EntryGroup.class))).willAnswer(
				invocationOnMock -> Collections.singletonList((EntryGroup) invocationOnMock.getArguments()[0]));
		assertFalse(defaultBundleCommerceCartFacade.isCartValid());
	}

	@Test
	public void testStartBundle() throws CommerceCartModificationException
	{
		final ArgumentCaptor<CommerceCartParameter> captor = ArgumentCaptor.forClass(CommerceCartParameter.class);
		when(commerceCartService.addToCart(captor.capture())).thenReturn(null);
		when(bundleTemplateService.getBundleTemplateForCode(any(String.class))).thenAnswer(invocationOnMock -> {
			final BundleTemplateModel result = new BundleTemplateModel();
			result.setId((String) invocationOnMock.getArguments()[0]);
			return result;
		});
		when(productService.getProductForCode(any(String.class))).thenAnswer(invocationOnMock -> {
			final ProductModel product = new ProductModel();
			product.setCode((String) invocationOnMock.getArguments()[0]);
			return product;
		});

		defaultBundleCommerceCartFacade.startBundle("bundleTemplate", "code", 1L);

		assertEquals("bundleTemplate", captor.getValue().getBundleTemplate().getId());
		assertEquals("code", captor.getValue().getProduct().getCode());
		assertEquals(1L, captor.getValue().getQuantity());
		assertTrue(captor.getValue().isEnableHooks());
		assertTrue(CollectionUtils.isEmpty(captor.getValue().getEntryGroupNumbers()));
	}

	@Test
	public void testAddToCart() throws CommerceCartModificationException
	{
		final ArgumentCaptor<CommerceCartParameter> captor = ArgumentCaptor.forClass(CommerceCartParameter.class);
		when(commerceCartService.addToCart(captor.capture())).thenReturn(null);
		when(bundleTemplateService.getBundleTemplateForCode(any(String.class))).thenAnswer(
				invocationOnMock -> new BundleTemplateModel());
		when(productService.getProductForCode(any(String.class))).thenAnswer(invocationOnMock -> {
			final ProductModel product = new ProductModel();
			product.setCode((String) invocationOnMock.getArguments()[0]);
			return product;
		});

		defaultBundleCommerceCartFacade.addToCart("code", 1L, 100);

		assertNull(captor.getValue().getBundleTemplate());
		assertEquals("code", captor.getValue().getProduct().getCode());
		assertEquals(1L, captor.getValue().getQuantity());
		assertTrue(captor.getValue().isEnableHooks());
		assertThat(captor.getValue().getEntryGroupNumbers(), contains(Integer.valueOf(100)));
		verify(bundleTemplateService, never()).getBundleTemplateForCode(any(String.class));
	}

	@Test
	public void shouldAssignBundleTemplateForNewBundle() throws CommerceCartModificationException
	{
		final ArgumentCaptor<CommerceCartParameter> captor = ArgumentCaptor.forClass(CommerceCartParameter.class);
		when(commerceCartService.addToCart(captor.capture())).thenReturn(null);
		when(bundleTemplateService.getBundleTemplateForCode(any(String.class))).thenAnswer(invocationOnMock -> {
			final BundleTemplateModel result = new BundleTemplateModel();
			result.setId((String) invocationOnMock.getArguments()[0]);
			return result;
		});

		defaultBundleCommerceCartFacade.addToCart("product", 1L, NEW_BUNDLE, "bundleTemplate", false);

		assertEquals("bundleTemplate", captor.getValue().getBundleTemplate().getId());
	}

	@Test
	public void shouldRemoveExistingEntries() throws CommerceCartModificationException
	{
		final CartModel cart = new CartModel();
		final CartEntryModel entry = new CartEntryModel();
		entry.setBundleNo(Integer.valueOf(2));
		entry.setEntryGroupNumbers(new HashSet<>(Collections.singletonList(Integer.valueOf(3))));
		entry.setEntryNumber(Integer.valueOf(4));
		cart.setEntries(Collections.singletonList(entry));
		final EntryGroup group = new EntryGroup();
		group.setGroupNumber(Integer.valueOf(3));
		group.setExternalReferenceId("bundleTemplate");
		given(bundleTemplateService.getBundleEntryGroup(entry)).willReturn(group);
		given(entryGroupService.getGroup(cart, Integer.valueOf(3))).willReturn(group);
		given(entryGroupService.getLeaves(any())).willReturn(Collections.singletonList(group));
		given(cartService.getSessionCart()).willReturn(cart);
		final ArgumentCaptor<CommerceCartParameter> captor = ArgumentCaptor.forClass(CommerceCartParameter.class);
		when(commerceCartService.updateQuantityForCartEntry(captor.capture())).thenReturn(null);

		defaultBundleCommerceCartFacade.addToCart("product", 1L, 2, "bundleTemplate", true);

		assertEquals(cart, captor.getValue().getCart());
		assertEquals(0L, captor.getValue().getQuantity());
		assertEquals(entry.getEntryNumber().intValue(), captor.getValue().getEntryNumber());
		verify(commerceCartService).updateQuantityForCartEntry(any());
	}

	@Test
	public void shouldCheckBundleIntegrity() throws CommerceCartModificationException
	{
		final CartModel cart = new CartModel();
		final CartEntryModel entry = new CartEntryModel();
		entry.setBundleNo(Integer.valueOf(2));
		entry.setEntryGroupNumbers(new HashSet<>(Collections.singletonList(Integer.valueOf(3))));
		entry.setEntryNumber(Integer.valueOf(4));
		cart.setEntries(Collections.singletonList(entry));
		final EntryGroup group = new EntryGroup();
		group.setGroupNumber(Integer.valueOf(3));
		group.setExternalReferenceId("bundleTemplate");
		given(cartService.getSessionCart()).willReturn(cart);
		given(bundleTemplateService.getBundleEntryGroup(entry)).willReturn(group);
		given(entryGroupService.getGroup(cart, Integer.valueOf(3))).willReturn(group);
		given(entryGroupService.getLeaves(any())).willReturn(Collections.singletonList(group));
		thrown.expect(CommerceCartModificationException.class);
		thrown.expectMessage("Bundle #2 does not contain component 'component'");

		defaultBundleCommerceCartFacade.addToCart("product", 1L, 2, "component", false);
	}

	@Test
	public void deleteShouldCheckBundleNo() throws CommerceCartModificationException
	{
		final CartModel cart = new CartModel();
		cart.setCode("A");
		cart.setEntries(Collections.emptyList());
		given(cartService.getSessionCart()).willReturn(cart);
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Bundle #1 was not found in cart A");

		defaultBundleCommerceCartFacade.deleteCartBundle(1);
	}

	@Test
	public void shouldDeleteBundleByBundleNo() throws CommerceCartModificationException
	{
		final CartModel cart = new CartModel();
		final CartEntryModel entry = new CartEntryModel();
		entry.setBundleNo(Integer.valueOf(2));
		entry.setEntryGroupNumbers(new HashSet<>(Collections.singletonList(Integer.valueOf(3))));
		entry.setEntryNumber(Integer.valueOf(4));
		entry.setBundleTemplate(new BundleTemplateModel());
		cart.setEntries(Collections.singletonList(entry));
		final EntryGroup group = new EntryGroup();
		group.setGroupNumber(Integer.valueOf(3));
		final EntryGroup root = new EntryGroup();
		root.setGroupNumber(Integer.valueOf(1));
		given(cartService.getSessionCart()).willReturn(cart);
		given(bundleTemplateService.getBundleEntryGroup(entry)).willReturn(group);
		given(entryGroupService.getRoot(cart, Integer.valueOf(3))).willReturn(root);
		final ArgumentCaptor<RemoveEntryGroupParameter> captor = ArgumentCaptor.forClass(RemoveEntryGroupParameter.class);
		given(commerceCartService.removeEntryGroup(captor.capture())).willReturn(null);

		defaultBundleCommerceCartFacade.deleteCartBundle(2);

		assertEquals(cart, captor.getValue().getCart());
		assertEquals(root.getGroupNumber(), captor.getValue().getEntryGroupNumber());
		assertTrue(captor.getValue().isEnableHooks());
	}

	@Test
	public void testProcessFacetData() throws UnsupportedEncodingException
	{
		final FacetData<SearchStateData> facetData = new FacetData<>();
		final FacetValueData<SearchStateData> topValue = new FacetValueData<>();
		final SearchStateData topQuery = new SearchStateData();
		final SearchQueryData topQData = new SearchQueryData();
		topQData.setValue("<test>:<div>");
		topQuery.setQuery(topQData);
		topValue.setQuery(topQuery);
		facetData.setTopValues(Collections.singletonList(topValue));
		final FacetValueData<SearchStateData> value = new FacetValueData<>();
		facetData.setValues(Collections.singletonList(value));
		facetData.setValues(Collections.emptyList());

		defaultBundleCommerceCartFacade.processFacetData(Collections.singletonList(facetData));

		assertEquals("&lt;test&gt;:<div>", topValue.getQuery().getQuery().getValue());
	}

	@Test
	public void shouldPathURLs()
	{
		final FacetData<SearchStateData> facetData = new FacetData<>();
		final FacetValueData<SearchStateData> topValue = new FacetValueData<>();
		final SearchStateData topQuery = new SearchStateData();
		final SearchQueryData topQData = new SearchQueryData();
		topQData.setValue("test:value");
		topQuery.setQuery(topQData);
		topValue.setQuery(topQuery);
		facetData.setTopValues(Collections.singletonList(topValue));
		final FacetValueData<SearchStateData> value = new FacetValueData<>();
		facetData.setValues(Collections.singletonList(value));
		facetData.setValues(Collections.emptyList());
		final ProductSearchPageData<SearchStateData, ProductData> searchPageData = new ProductSearchPageData<>();
		searchPageData.setFacets(Collections.singletonList(facetData));
		final BreadcrumbData<SearchStateData> breadcrumbData = new BreadcrumbData<>();
		final SearchStateData removeQueryData = new SearchStateData();
		breadcrumbData.setRemoveQuery(removeQueryData);
		final SearchQueryData removeSearchQuery = new SearchQueryData();
		removeSearchQuery.setValue("check:1");
		removeQueryData.setQuery(removeSearchQuery);
		searchPageData.setBreadcrumbs(Collections.singletonList(breadcrumbData));
		final SearchStateData currentQuery = new SearchStateData();
		final SearchQueryData query = new SearchQueryData();
		query.setValue("");
		currentQuery.setQuery(query);
		searchPageData.setCurrentQuery(currentQuery);

		defaultBundleCommerceCartFacade.patchURLs("prefix/", searchPageData);

		assertEquals("prefix/check%3a1", breadcrumbData.getRemoveQuery().getUrl());
		assertEquals("prefix/test%3avalue", topQuery.getUrl());
	}

	@Test
	public void pathURLsShouldSurviveNullFacetList()
	{
		final ProductSearchPageData<SearchStateData, ProductData> searchPageData = new ProductSearchPageData<>();
		final SearchStateData currentQuery = new SearchStateData();
		final SearchQueryData query = new SearchQueryData();
		query.setValue("");
		currentQuery.setQuery(query);
		searchPageData.setCurrentQuery(currentQuery);
		searchPageData.setBreadcrumbs(Collections.emptyList());
		defaultBundleCommerceCartFacade.patchURLs("prefix", searchPageData);
	}

	@Test
	public void pathURLsShouldSurviveNullBreadcrumbs()
	{
		final ProductSearchPageData<SearchStateData, ProductData> searchPageData = new ProductSearchPageData<>();
		final SearchStateData currentQuery = new SearchStateData();
		final SearchQueryData query = new SearchQueryData();
		query.setValue("");
		currentQuery.setQuery(query);
		searchPageData.setCurrentQuery(currentQuery);
		searchPageData.setFacets(Collections.emptyList());
		defaultBundleCommerceCartFacade.patchURLs("prefix", searchPageData);
	}

	@Test
	public void allowedProductsShouldAcceptNullQuery()
	{
		defaultBundleCommerceCartFacade.getAllowedProducts(Integer.valueOf(1), null, new PageableData());
	}

	@Test
	public void allowedProductsShouldCheckRefId()
	{
		final EntryGroup group = new EntryGroup();
		group.setGroupType(GroupType.CONFIGURABLEBUNDLE);
		when(entryGroupService.getGroupOfType(any(), anyCollectionOf(Integer.class), eq(GroupType.CONFIGURABLEBUNDLE))).thenReturn(group);
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("componentId");
		defaultBundleCommerceCartFacade.getAllowedProducts(Integer.valueOf(1), "test", new PageableData());
	}

	@Test
	public void allowedProductsShouldCheckGroupType()
	{
		final EntryGroup group = new EntryGroup();
		group.setGroupType(GroupType.STANDALONE);
		when(entryGroupService.getGroup(any(), any())).thenReturn(group);
		given(entryGroupService.getGroupOfType(any(CartModel.class), anyCollectionOf(Integer.class), eq(GroupType.CONFIGURABLEBUNDLE))).willThrow(
				IllegalArgumentException.class);
		thrown.expect(IllegalArgumentException.class);
		defaultBundleCommerceCartFacade.getAllowedProducts(Integer.valueOf(1), "test", new PageableData());
	}

}

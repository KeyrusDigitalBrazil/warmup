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


package de.hybris.platform.configurablebundlefacades.order;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.search.ProductSearchFacade;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.configurablebundlefacades.order.impl.DefaultBundleCartFacade;
import de.hybris.platform.configurablebundleservices.bundle.BundleCommerceCartService;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.EntryGroupService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collections;

import de.hybris.platform.subscriptionfacades.order.SubscriptionCartFacade;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultBundleCartFacadeTest
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
	@Mock
	private BundleCommerceCartService bundleCommerceCartService;
	@Mock
	private SubscriptionCartFacade subscriptionCartFacade;
	@InjectMocks
	private DefaultBundleCartFacade defaultBundleCartFacade;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldCreateBundleStructureForOldCart() throws CommerceCartModificationException
	{
		final CartModel cart = new CartModel();
		final CartEntryModel entry = new CartEntryModel();
		entry.setOrder(cart);
		entry.setBundleNo(Integer.valueOf(2));
		final BundleTemplateModel component = new BundleTemplateModel();
		component.setId("BUNDLE");
		component.setParentTemplate(component);
		entry.setBundleTemplate(component);
		cart.setEntries(Collections.singletonList(entry));
		when(cartService.getSessionCart()).thenReturn(cart);
		final EntryGroup group = new EntryGroup();
		group.setExternalReferenceId("BUNDLE");
		group.setGroupNumber(Integer.valueOf(10));
		when(bundleTemplateService.getBundleEntryGroup(entry)).thenReturn(group);
		when(entryGroupService.getRoot(any(), any())).thenReturn(group);
		when(entryGroupService.getLeaves(any())).thenReturn(Collections.singletonList(group));
		when(bundleCommerceCartService.getCartEntriesForBundle(cart, 2)).thenReturn(Collections.singletonList(entry));
		when(bundleTemplateService.createBundleTree(any(), any())).thenReturn(group);
		when(productService.getProductForCode(any())).thenReturn(new ProductModel());

		defaultBundleCartFacade.addToCart("PRODUCT", 1L, 2, "BUNDLE", false);

		assertThat(entry.getEntryGroupNumbers(), hasItem(group.getGroupNumber()));
	}

}

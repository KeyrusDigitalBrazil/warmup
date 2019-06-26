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
package de.hybris.platform.selectivecartservices.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.selectivecartservices.daos.impl.DefaultSelectiveCartDao;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.wishlist2.Wishlist2Service;
import de.hybris.platform.wishlist2.enums.Wishlist2EntryPriority;
import de.hybris.platform.wishlist2.model.Wishlist2EntryModel;
import de.hybris.platform.wishlist2.model.Wishlist2Model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultSelectiveCartServiceTest extends ServicelayerTransactionalTest
{
	private static final String UID = "test";
	private static final String USER = "user";
	private static final String WISHLIST_NAME = "WishlistForSelectiveCart";
	private static final String WISHLIST_DESC = "Special wishlist for selective cart for current user";
	private static final String PRODUCT_CODE = "p00001";
	private static final String CATALOAG_ID = "testCatalog";
	private static final String CATALOG_VERSION = "test";
	private static final String CART_CODE = "testcart";
	private static final int ENTRY_NUMBER = 11;

	private Wishlist2Model wishlist;
	private ProductModel product;
	private CustomerModel customer;
	private CartModel cart;
	private CartEntryModel cartEntry;

	@Resource(name = "selectiveCartService")
	private DefaultSelectiveCartService selectiveCartService;

	@Resource(name = "selectiveCartDao")
	private DefaultSelectiveCartDao selectiveCartDao;

	@Resource
	private ModelService modelService;

	@Resource
	private SessionService sessionService;

	@Resource(name = "wishlistService")
	private Wishlist2Service wishlistService;

	@Resource(name = "flexibleSearchService")
	private FlexibleSearchService flexibleSearchService;

	@Before
	public void prepare()
	{
		final CatalogModel catalog = modelService.create(CatalogModel.class);
		catalog.setId(CATALOAG_ID);
		final CatalogVersionModel version = modelService.create(CatalogVersionModel.class);
		version.setActive(true);
		version.setCatalog(catalog);
		version.setVersion(CATALOG_VERSION);
		modelService.save(version);

		product = modelService.create(ProductModel.class);
		product.setCode(PRODUCT_CODE);
		product.setCatalogVersion(version);
		modelService.save(product);

		customer = modelService.create(CustomerModel.class);
		customer.setUid(UID);
		modelService.save(customer);
		sessionService.setAttribute(USER, customer);
	}

	@Test
	public void testGetWishlistForSelectiveCart()
	{
		wishlist = wishlistService.createWishlist(WISHLIST_NAME, WISHLIST_DESC);
		final Wishlist2Model result = selectiveCartService.getWishlistForSelectiveCart();

		Assert.assertEquals(wishlist, result);
	}

	@Test
	public void testGetWishlistEntryForProduct()
	{
		final Wishlist2EntryModel result = selectiveCartService.getWishlistEntryForProduct(product);
		Assert.assertNull(result);
	}

	@Test
	public void testGetWishlistEntryForProduct2()
	{
		wishlist = wishlistService.createWishlist(WISHLIST_NAME, WISHLIST_DESC);

		final Wishlist2EntryModel entry = new Wishlist2EntryModel();
		entry.setWishlist(wishlist);
		entry.setProduct(product);
		entry.setAddedDate(Calendar.getInstance().getTime());
		entry.setPriority(Wishlist2EntryPriority.MEDIUM);
		entry.setQuantity(1);
		entry.setAddToCartTime(new Date());
		wishlistService.addWishlistEntry(wishlist, entry);

		final Wishlist2EntryModel result = selectiveCartService.getWishlistEntryForProduct(product, wishlist);
		Assert.assertEquals(entry.getProduct().getCode(), result.getProduct().getCode());
		Assert.assertEquals(entry.getQuantity(), result.getQuantity());
		Assert.assertEquals(entry.getAddToCartTime(), result.getAddToCartTime());
	}

	@Test
	public void testRemoveWishlistEntryForProduct()
	{
		wishlist = wishlistService.createWishlist(WISHLIST_NAME, WISHLIST_DESC);

		final Wishlist2EntryModel entry = new Wishlist2EntryModel();
		entry.setWishlist(wishlist);
		entry.setProduct(product);
		entry.setAddedDate(Calendar.getInstance().getTime());
		entry.setPriority(Wishlist2EntryPriority.MEDIUM);
		entry.setAddToCartTime(new Date());
		wishlistService.addWishlistEntry(wishlist, entry);

		selectiveCartService.removeWishlistEntryForProduct(product, wishlist);
		final Wishlist2Model result = selectiveCartService.getWishlistForSelectiveCart();
		Assert.assertEquals(null, result);
	}

	@Test
	public void testUpdateQuantityForWishlistEntry()
	{
		wishlist = wishlistService.createWishlist(WISHLIST_NAME, WISHLIST_DESC);

		final Wishlist2EntryModel entry = new Wishlist2EntryModel();
		entry.setWishlist(wishlist);
		entry.setProduct(product);
		entry.setAddedDate(Calendar.getInstance().getTime());
		entry.setPriority(Wishlist2EntryPriority.MEDIUM);
		entry.setQuantity(1);
		entry.setAddToCartTime(new Date());
		wishlistService.addWishlistEntry(wishlist, entry);

		selectiveCartService.updateQuantityForWishlistEntry(entry, 2);
		final Wishlist2EntryModel result = selectiveCartService.getWishlistEntryForProduct(product);
		Assert.assertEquals(2, result.getQuantity().intValue());
	}

	@Test
	public void testCreateWishlist()
	{
		final Wishlist2Model result = selectiveCartService.createWishlist();

		Assert.assertEquals(customer, result.getUser());
		Assert.assertEquals(WISHLIST_NAME, result.getName());
	}

	@Test
	public void testSaveWishlistEntryForProduct()
	{
		wishlist = wishlistService.createWishlist(WISHLIST_NAME, WISHLIST_DESC);
		final Date now = new Date();

		final Wishlist2EntryModel result = selectiveCartService.saveWishlistEntryForProduct(product, wishlist, now);
		Assert.assertNotNull(result);
		Assert.assertEquals(now, result.getAddToCartTime());
	}

	@Test
	public void testUpdateCartTimeForOrderEntry()
	{
		final Date date = new Date();

		final CurrencyModel currencyModel = new CurrencyModel();
		currencyModel.setIsocode("zh");
		currencyModel.setSymbol("$");

		cart = modelService.create(CartModel.class);
		cart.setCode(CART_CODE);
		cart.setCurrency(currencyModel);
		cart.setTotalPrice(Double.valueOf(1.0));
		cart.setSubtotal(Double.valueOf(2.0));
		cart.setDeliveryCost(Double.valueOf(3.0));
		cart.setDate(date);
		cart.setNet(Boolean.FALSE);
		cart.setUser(customer);

		final UnitModel unitModel = modelService.create(UnitModel.class);
		unitModel.setUnitType("testUnit");
		unitModel.setCode("pandaUnit");

		cartEntry = modelService.create(CartEntryModel.class);
		cartEntry.setProduct(product);
		cartEntry.setOrder(cart);
		cartEntry.setBasePrice(Double.valueOf(2.2));
		cartEntry.setTotalPrice(Double.valueOf(3.9));
		cartEntry.setQuantity(Long.valueOf(1));
		cartEntry.setUnit(unitModel);
		cartEntry.setEntryNumber(ENTRY_NUMBER);
		cartEntry.setAddToCartTime(null);

		final List<AbstractOrderEntryModel> entryList = new ArrayList<AbstractOrderEntryModel>();
		entryList.add(cartEntry);
		cart.setEntries(entryList);
		modelService.save(cart);

		selectiveCartService.updateCartTimeForOrderEntry(CART_CODE, ENTRY_NUMBER, date);

		final CartEntryModel result = modelService.get(cartEntry.getPk());
		Assert.assertEquals(date, result.getAddToCartTime());
	}
}

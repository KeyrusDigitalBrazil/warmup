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
package de.hybris.platform.selectivecartservices.daos.impl;

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
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.wishlist2.Wishlist2Service;
import de.hybris.platform.wishlist2.model.Wishlist2Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 *
 */
@IntegrationTest
public class DefaultSelectiveCartDaoTest extends ServicelayerTransactionalTest
{

	private static final String UID = "test";
	private static final String USER = "user";
	private static final String WISHLIST_NAME = "test_wishlist";
	private static final String WISHLIST_DESC = "test_wishlist_description";
	private static final String CART_UID = "testcart";
	private static final String PRODUCT_UID = "testproduct";
	private static final int ENTRY_NUMBER = 11;

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

	private CustomerModel customer;
	private Wishlist2Model wishlist;
	private CartModel cart;
	private CartEntryModel cartEntry;

	@Before
	public void prepare() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		customer = modelService.create(CustomerModel.class);
		customer.setUid(UID);

		modelService.save(customer);
		sessionService.setAttribute(USER, customer);

		wishlist = wishlistService.createWishlist(WISHLIST_NAME, WISHLIST_DESC);

		final Date now = new Date();

		final CurrencyModel currencyModel = (CurrencyModel) flexibleSearchService
				.search("SELECT {PK} FROM {Currency} WHERE {isocode}='USD'").getResult().get(0);

		cart = modelService.create(CartModel.class);
		cart.setCode(CART_UID);
		cart.setCurrency(currencyModel);
		cart.setTotalPrice(Double.valueOf(1.0));
		cart.setSubtotal(Double.valueOf(2.0));
		cart.setDeliveryCost(Double.valueOf(3.0));
		cart.setDate(now);
		cart.setNet(Boolean.FALSE);
		cart.setUser(customer);

		final CatalogModel catalog = flexibleSearchService
				.<CatalogModel> search("SELECT {PK} FROM {Catalog} WHERE {id}='testCatalog'").getResult().get(0);

		final ProductModel productModel = modelService.create(ProductModel.class);
		productModel.setCode(PRODUCT_UID);
		productModel.setCatalogVersion(flexibleSearchService
				.<CatalogVersionModel> search("SELECT {PK} FROM {CatalogVersion} WHERE {version}='Online' AND {catalog}=?catalog",
						Collections.singletonMap("catalog", catalog)).getResult().get(0));

		final UnitModel unitModel = modelService.create(UnitModel.class);
		unitModel.setUnitType("testUnit");
		unitModel.setCode("pandaUnit");

		cartEntry = modelService.create(CartEntryModel.class);
		cartEntry.setProduct(productModel);
		cartEntry.setOrder(cart);
		cartEntry.setBasePrice(Double.valueOf(2.2));
		cartEntry.setTotalPrice(Double.valueOf(3.9));
		cartEntry.setQuantity(Long.valueOf(1));
		cartEntry.setUnit(unitModel);
		cartEntry.setEntryNumber(ENTRY_NUMBER);
		cartEntry.setAddToCartTime(now);

		final List<AbstractOrderEntryModel> entryList = new ArrayList<AbstractOrderEntryModel>();
		entryList.add(cartEntry);
		cart.setEntries(entryList);
		modelService.save(cart);

	}

	@Test
	public void testFindWishlistByName()
	{
		final Wishlist2Model result = selectiveCartDao.findWishlistByName(customer, WISHLIST_NAME);

		Assert.assertEquals(wishlist.getPk(), result.getPk());
		Assert.assertEquals(wishlist.getName(), result.getName());
		Assert.assertEquals(wishlist.getDescription(), result.getDescription());
	}

	@Test
	public void testFindCartEntryByCartCodeAndEntryNumber()
	{
		final CartEntryModel result = selectiveCartDao.findCartEntryByCartCodeAndEntryNumber(CART_UID, ENTRY_NUMBER);

		Assert.assertEquals(cartEntry.getEntryNumber(), result.getEntryNumber());
		Assert.assertEquals(cartEntry.getAddToCartTime(), result.getAddToCartTime());
		Assert.assertEquals(cartEntry.getProduct().getCode(), result.getProduct().getCode());
	}
}

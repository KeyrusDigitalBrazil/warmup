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
package de.hybris.platform.stocknotificationfacades;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerinterestsservices.model.ProductInterestModel;
import de.hybris.platform.customerinterestsservices.productinterest.ProductInterestService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.stocknotificationfacades.impl.DefaultStockNotificationFacade;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for {@link DefaultStockNotificationTest}
 */
@UnitTest
public class DefaultStockNotificationTest
{

	private DefaultStockNotificationFacade stockNotificationFacade;

	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private UserService userService;
	@Mock
	private ProductInterestService productInterestService;
	@Mock
	private ProductService productService;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);

		stockNotificationFacade = new DefaultStockNotificationFacade();
		stockNotificationFacade.setBaseSiteService(baseSiteService);
		stockNotificationFacade.setBaseStoreService(baseStoreService);

		final BaseStoreModel baseStore = new BaseStoreModel();
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);

		final BaseSiteModel baseSite = new BaseSiteModel();
		Mockito.when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testIsWatchingProductNullArg()
	{
		stockNotificationFacade.isWatchingProduct(null);
	}


	@Test
	public void testIsWatchingProduct_true()
	{
		stockNotificationFacade.setUserService(userService);
		Mockito.when(userService.getCurrentUser()).thenReturn(new CustomerModel());
		final Optional<ProductInterestModel> productInterest = Optional.of(new ProductInterestModel());
		Mockito.when(
				productInterestService.getProductInterest(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(productInterest);
		stockNotificationFacade.setProductInterestService(productInterestService);
		Mockito.when(productService.getProductForCode(Mockito.any())).thenReturn(new ProductModel());
		stockNotificationFacade.setProductService(productService);

		Assert.assertTrue(stockNotificationFacade.isWatchingProduct(new ProductData()));
	}

	@Test
	public void testIsWatchingProduct_false()
	{
		stockNotificationFacade.setUserService(userService);
		Mockito.when(userService.getCurrentUser()).thenReturn(new UserModel());

		Assert.assertFalse(stockNotificationFacade.isWatchingProduct(new ProductData()));
	}



}

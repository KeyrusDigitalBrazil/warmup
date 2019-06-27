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
package de.hybris.platform.customerinterestsservices.productinterest.impl;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.customerinterestsservices.model.ProductInterestModel;
import de.hybris.platform.customerinterestsservices.productinterest.daos.ProductInterestDao;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.notificationservices.enums.NotificationType;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;





/**
 *
 */
@UnitTest
public class DefaultProductInterestServiceTest
{
	private DefaultProductInterestService productInterestService;

	private ProductModel product;

	private ProductInterestModel productInterestModel;

	private List<ProductInterestModel> productInterests = new ArrayList<>();

	@Mock
	private UserService userService;

	@Mock
	private BaseSiteService baseSiteService;

	@Mock
	private BaseStoreService baseStoreService;

	@Mock
	private ProductService productService;

	@Mock
	private ProductInterestDao productInterestDao;

	@Mock
	private ModelService modelService;


	@Before
	public void prepare() throws ImpExException
	{
		MockitoAnnotations.initMocks(this);
		productInterestService = new DefaultProductInterestService();
		productInterestService.setBaseSiteService(baseSiteService);
		productInterestService.setBaseStoreService(baseStoreService);
		productInterestService.setUserService(userService);
		productInterestService.setProductInterestDao(productInterestDao);
		productInterestService.setModelService(modelService);

		product = new ProductModel();
		product.setCode("1111111");

		productInterestModel = new ProductInterestModel();
		productInterestModel.setProduct(product);

		productInterests = new ArrayList<>();
		productInterests.add(productInterestModel);
	}

	@Test
	public void test_Remove_All_Product_Interests()
	{
		Mockito.doReturn(productInterests).when(productInterestDao).findProductInterestsByCustomer(Mockito.any(), Mockito.any(),
				Mockito.any());
		Mockito.doReturn(new BaseStoreModel()).when(baseStoreService).getCurrentBaseStore();
		Mockito.doReturn(new CustomerModel()).when(userService).getCurrentUser();
		Mockito.doReturn(new BaseSiteModel()).when(baseSiteService).getCurrentBaseSite();
		Mockito.doNothing().when(productInterestService.getModelService()).remove(Mockito.any());

		productInterestService.removeAllProductInterests("1111111");
		verify(productInterestService.getModelService(), times(1)).remove(productInterestModel);
	}

	@Test
	public void test_Get_Products_By_Customer_Interests()
	{
		final Map<String, String> notificationMap = new LinkedHashMap<>();
		notificationMap.put("8796233826305", "2012-01-02 01:23:34.3");
		final Map<String, Map<String, String>> productPkMap = new LinkedHashMap<>();
		productPkMap.put("8796230746113", notificationMap);
		Mockito.doReturn(productPkMap).when(productInterestDao).findProductsByCustomerInterests(Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any());

		final ProductModel product = new ProductModel();

		Mockito.doReturn(product).doReturn(NotificationType.NOTIFICATION).when(modelService).get(Mockito.any(PK.class));

		final Map<ProductModel, Map<NotificationType, Date>> productMap = productInterestService
				.getProductsByCustomerInterests(new PageableData());

		Assert.assertEquals(product, productMap.keySet().iterator().next());

		Assert.assertEquals(NotificationType.NOTIFICATION, productMap.values().iterator().next().keySet().iterator().next());

	}

	@Test
	public void test_Get_Products_Count_By_Customer_Interests()
	{
		final int expectedCount = 5;
		Mockito.doReturn(expectedCount).when(productInterestDao).findProductsCountByCustomerInterests(Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any());
		final int acturalCount = productInterestService.getProductsCountByCustomerInterests(new PageableData());
		Assert.assertEquals(expectedCount, acturalCount);
	}

}

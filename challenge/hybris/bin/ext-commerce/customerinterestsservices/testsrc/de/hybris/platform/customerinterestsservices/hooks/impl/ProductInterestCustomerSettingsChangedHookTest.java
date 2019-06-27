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
package de.hybris.platform.customerinterestsservices.hooks.impl;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.customerinterestsservices.model.ProductInterestModel;
import de.hybris.platform.customerinterestsservices.productinterest.daos.ProductInterestDao;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.notificationservices.enums.NotificationChannel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductInterestCustomerSettingsChangedHookTest
{
	@Mock
	private ProductInterestDao productInterestDao;
	@Mock
	private ModelService modelService;
	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private BaseSiteService baseSiteService;

	private BaseSiteModel baseSiteModel;

	private BaseStoreModel baseStoreModel;

	private ProductInterestCustomerSettingsChangedHook hook;

	private Set<NotificationChannel> channels;

	private final ProductInterestModel productInterest = new ProductInterestModel();

	private final List<ProductInterestModel> productInterestList = new ArrayList<>();

	private final CustomerModel customer = new CustomerModel();
	@Before
	public void prepare() throws ImpExException
	{
		MockitoAnnotations.initMocks(this);

		channels = new HashSet<NotificationChannel>();
		channels.add(NotificationChannel.SMS);
		
		hook = new ProductInterestCustomerSettingsChangedHook();
		hook.setBaseSiteService(baseSiteService);
		hook.setBaseStoreService(baseStoreService);
		hook.setModelService(modelService);
		hook.setProductInterestDao(productInterestDao);

		Mockito.doNothing().when(modelService).remove(Mockito.any());
		Mockito.doNothing().when(modelService).save(Mockito.any());
		Mockito.doReturn(baseStoreModel).when(baseStoreService).getCurrentBaseStore();
		Mockito.doReturn(baseSiteModel).when(baseSiteService).getCurrentBaseSite();
		Mockito.doReturn(productInterestList).when(productInterestDao).findProductInterestsByCustomer(Mockito.anyObject(),
				Mockito.anyObject(), Mockito.anyObject());
	}

	@Test
	public void testAfterUnbindMobileNumber()
	{
		productInterest.setNotificationChannels(channels);
		productInterestList.add(productInterest);
		Mockito.doReturn(productInterestList).when(productInterestDao).findProductInterestsByCustomer(Mockito.anyObject(),
				Mockito.anyObject(), Mockito.anyObject());
		hook.afterUnbindMobileNumber(customer);

		verify(modelService, times(1)).remove(productInterest);
	}

	@Test
	public void testAfterUnbindMobileNumber_mutilchannel()
	{
		channels.add(NotificationChannel.EMAIL);
		productInterest.setNotificationChannels(channels);
		productInterestList.add(productInterest);
		Mockito.doReturn(productInterestList).when(productInterestDao).findProductInterestsByCustomer(Mockito.anyObject(),
				Mockito.anyObject(), Mockito.anyObject());
		hook.afterUnbindMobileNumber(customer);

		verify(modelService, times(0)).remove(productInterest);
	}
}

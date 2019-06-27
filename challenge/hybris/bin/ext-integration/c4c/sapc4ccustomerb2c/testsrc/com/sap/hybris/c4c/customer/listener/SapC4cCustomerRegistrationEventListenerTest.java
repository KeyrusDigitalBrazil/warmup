/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.sap.hybris.c4c.customer.listener;


import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.RegisterEvent;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 *
 */
@UnitTest
public class SapC4cCustomerRegistrationEventListenerTest
{

	@InjectMocks
	private final SapC4cCustomerRegistrationEventListener eventListener = new SapC4cCustomerRegistrationEventListener();

	@Mock
	private ModelService modelService;

	@Mock
	private BaseStoreService baseStoreService;

	@Mock
	private BusinessProcessService businessProcessService;



	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testOnEvent()
	{

		final StoreFrontCustomerProcessModel storeFrontCustomerProcessModel = new StoreFrontCustomerProcessModel();
		when(businessProcessService.createProcess(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(storeFrontCustomerProcessModel);
		final BaseSiteModel baseSite = Mockito.mock(BaseSiteModel.class);
		final BaseStoreModel currentBaseStore = Mockito.mock(BaseStoreModel.class);
		final CustomerModel customerModel = Mockito.mock(CustomerModel.class);
		final RegisterEvent registerEvent = new RegisterEvent();
		registerEvent.setSite(baseSite);
		registerEvent.setCustomer(customerModel);

		when(baseStoreService.getCurrentBaseStore()).thenReturn(currentBaseStore);
		doNothing().when(modelService).save(Mockito.any(StoreFrontCustomerProcessModel.class));
		doNothing().when(businessProcessService).startProcess(storeFrontCustomerProcessModel);

		eventListener.onEvent(registerEvent);

		verify(modelService, times(1)).save(Mockito.any(StoreFrontCustomerProcessModel.class));
		verify(businessProcessService, times(1)).startProcess(Mockito.any());
	}
}

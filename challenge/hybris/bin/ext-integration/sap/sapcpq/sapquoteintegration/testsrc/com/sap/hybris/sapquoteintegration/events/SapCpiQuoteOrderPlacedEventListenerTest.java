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
package com.sap.hybris.sapquoteintegration.events;


import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.commerceservices.order.CommerceQuoteService;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;

@UnitTest
public class SapCpiQuoteOrderPlacedEventListenerTest
{
	@InjectMocks
	private SapCpiQuoteOrderPlacedEventListener buyerOrderPlacedEventListener = new SapCpiQuoteOrderPlacedEventListener();
	
	@Mock
	private ModelService modelService;
	
	@Mock
	private CommerceQuoteService commerceQuoteService;
	
	@Mock
	private BusinessProcessService businessProcessService;

	@Before
   public void setUp() {
       MockitoAnnotations.initMocks(this);
   }
	
	@SuppressWarnings("unchecked")
	@Test
	public void testOnEvent()
	{
		BaseStoreModel baseStore = new BaseStoreModel();
		baseStore.setUid("powertools");
		QuoteModel quote = new QuoteModel();
		quote.setCode("12345");
		quote.setStore(baseStore);
		OrderModel order = new OrderModel();
		order.setCode("123456");
		
		SapCpiQuoteOrderPlacedEvent event  =  new SapCpiQuoteOrderPlacedEvent(order, quote);
		QuoteProcessModel quoteOrderedProcessModel = new QuoteProcessModel();
		when(commerceQuoteService.createQuoteSnapshotWithState(Mockito.any(QuoteModel.class),Mockito.any(QuoteState.class))).thenReturn(quote);
		doNothing().when(modelService).refresh(Mockito.any());
		doNothing().when(modelService).save(Mockito.any());
		when(businessProcessService.createProcess(Mockito.anyString(), Mockito.anyString(), Mockito.anyMap())).thenReturn(quoteOrderedProcessModel);
		
		
		doNothing().when(businessProcessService).startProcess(Mockito.any(QuoteProcessModel.class));
		
		buyerOrderPlacedEventListener.onEvent(event);
		
		Assert.assertEquals(order.getCode(), quote.getOrderCode());
		verify(businessProcessService, times(1)).createProcess(Mockito.anyString(), Mockito.anyString(), Mockito.anyMap());
		verify(businessProcessService, times(1)).startProcess(Mockito.any(QuoteProcessModel.class));
		
	}
	
}

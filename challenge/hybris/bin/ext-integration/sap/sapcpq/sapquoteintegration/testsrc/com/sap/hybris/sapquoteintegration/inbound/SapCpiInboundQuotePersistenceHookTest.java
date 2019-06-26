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
package com.sap.hybris.sapquoteintegration.inbound;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sap.hybris.sapquoteintegration.inbound.helper.InboundQuoteHelper;
import com.sap.hybris.sapquoteintegration.inbound.helper.impl.DefaultInboundQuoteHelper;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceQuoteService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;

@UnitTest
public class SapCpiInboundQuotePersistenceHookTest {
	@InjectMocks
	private SapCpiInboundQuotePersistenceHook sapCpiInboundQuotePersistenceHook = new SapCpiInboundQuotePersistenceHook();

	@Mock
	private ModelService modelService;

	@Mock
	private CommerceQuoteService commerceQuoteService;

	@Mock
	private BusinessProcessService businessProcessService;

	@Mock
	private InboundQuoteHelper inboundQuoteHelper;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		List<InboundQuoteHelper> inboundQuoteHelpers = new ArrayList<InboundQuoteHelper>();
		inboundQuoteHelpers.add(inboundQuoteHelper);
		sapCpiInboundQuotePersistenceHook.setSapInboundQuoteHelpers(inboundQuoteHelpers);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testExecute() {
		String code = UUID.randomUUID().toString();
		BaseStoreModel baseStore = new BaseStoreModel();
		baseStore.setUid("powertools");
		QuoteModel quote = new QuoteModel();
		quote.setCode(code);
		quote.setExternalQuoteId(code);
		quote.setStore(baseStore);
		OrderModel order = new OrderModel();
		order.setCode("123456");

		when(inboundQuoteHelper.processInboundQuote(Mockito.any(QuoteModel.class))).thenReturn(quote);

		Optional<ItemModel> resultQuote = sapCpiInboundQuotePersistenceHook.execute(quote);
		if (resultQuote != null) {
			Assert.assertTrue(true);

		} else {
			Assert.assertTrue(false);
		}
	}

}

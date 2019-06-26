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
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hybris.sapquoteintegration.inbound.helper.InboundQuoteEntryHelper;
import com.sap.hybris.sapquoteintegration.inbound.helper.impl.DefaultInboundQuoteEntryHelper;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceQuoteService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.QuoteEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;

@UnitTest
public class SapCpiInboundQuoteEntryPersistenceHookTest {
	private static final Logger LOG = LoggerFactory.getLogger(DefaultInboundQuoteEntryHelper.class);
	@InjectMocks
	private SapCpiInboundQuoteEntryPersistenceHook sapCpiInboundQuoteEntryPersistenceHook = new SapCpiInboundQuoteEntryPersistenceHook();

	@Mock
	private ModelService modelService;

	@Mock
	private CommerceQuoteService commerceQuoteService;

	@Mock
	private BusinessProcessService businessProcessService;

	@Mock
	private InboundQuoteEntryHelper inboundQuoteEntryHelper;
	
	@Mock
	private QuoteService quoteService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		List<InboundQuoteEntryHelper> inboundQuoteEntryHelpersLocal = new ArrayList<InboundQuoteEntryHelper>();
		inboundQuoteEntryHelpersLocal.add(inboundQuoteEntryHelper);
		sapCpiInboundQuoteEntryPersistenceHook.setSapInboundQuoteEntryHelpers(inboundQuoteEntryHelpersLocal);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testExecute() {
		BaseStoreModel baseStore = new BaseStoreModel();
		baseStore.setUid("powertools");
		QuoteModel quote = new QuoteModel();
		quote.setCode("12345");
		quote.setStore(baseStore);
		QuoteEntryModel quoteEntry = new QuoteEntryModel();
		quoteEntry.setCreationtime(new Date());
		quoteEntry.setEntryNumber(1);
		quoteEntry.setOrder(quote);
		quoteEntry.setQuantity(1L);
		quoteEntry.setBasePrice(10.0d);
		quoteEntry.setRank("1");
		when(quoteService.getCurrentQuoteForCode(Mockito.any(String.class))).thenReturn(quote);

		when(inboundQuoteEntryHelper.processInboundQuoteEntry(Mockito.any(QuoteEntryModel.class)))
				.thenReturn(quoteEntry);
		Optional<ItemModel> resultQuoteEntry = sapCpiInboundQuoteEntryPersistenceHook.execute(quoteEntry);
		LOG.info("SapCpiInboundQuoteEntryPersistenceHookTest ----   resultQuoteEntry ===  " + resultQuoteEntry);
		if (resultQuoteEntry != null) {
			Assert.assertTrue(true);
		} else {
			Assert.assertTrue(false);
		}
	}

}

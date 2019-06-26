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
package com.sap.hybris.sapquoteintegration.inbound.helper.impl;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import com.sap.hybris.sapquoteintegration.inbound.helper.InboundQuoteHelper;
import com.sap.hybris.sapquoteintegration.inbound.helper.impl.DefaultInboundQuoteEntryHelper;
import com.sap.hybris.sapquoteintegration.inbound.helper.impl.DefaultInboundQuoteHelper;
import com.sap.hybris.sapquoteintegration.service.SapQuoteService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.comments.services.CommentService;
import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.commerceservices.order.CommerceQuoteService;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.QuoteEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

@UnitTest
public class DefaultInboundQuoteHelperTest {
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultInboundQuoteHelperTest.class);
	@InjectMocks
	private DefaultInboundQuoteHelper defaultInboundQuoteHelper = new DefaultInboundQuoteHelper();
	@Mock
	private QuoteService quoteService;
	@Mock
	private UserService userService;
	@Mock
	private KeyGenerator keyGenerator;
	@Mock
	private CommentService commentService;
	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private CatalogService catalogService;
	@Mock
	private ModelService modelService;
	@Mock
	private MediaService mediaService;
	@Mock
	private SapQuoteService sapQuoteService;	
	

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testProcessInboundQuote() {
		BaseStoreModel baseStore = new BaseStoreModel();
		baseStore.setUid("powertools");
		BaseSiteModel baseSite = new BaseSiteModel();
		baseSite.setUid("powertools");
		QuoteModel quote = new QuoteModel();
		quote.setCode("12345");
		quote.setExternalQuoteId("12345");
		quote.setStore(baseStore);

		//BaseStoreModel baseStoreForUid = baseStoreService.getBaseStoreForUid("powertools");
		when(baseStoreService.getBaseStoreForUid(Mockito.any(String.class))).thenReturn(baseStore);
		when(sapQuoteService.getSiteAndStoreFromSalesArea(Mockito.any(String.class),Mockito.any(String.class),Mockito.any(String.class))).thenReturn("123");
		when(baseSiteService.getBaseSiteForUID(Mockito.any(String.class))).thenReturn(baseSite);
		

		//BaseSiteModel baseSiteForUID = baseSiteService.getBaseSiteForUID("powertools");
		when(baseSiteService.getBaseSiteForUID(Mockito.any(String.class))).thenReturn(baseSite);
		QuoteModel resultQuote = null;
		try {
			resultQuote = defaultInboundQuoteHelper.processInboundQuote(quote);
			LOG.info("DefaultInboundQuoteHelperTest#testProcessInboundQuote resultQuote = " + resultQuote);
			if (resultQuote == null) {
				Assert.assertTrue(false);
			} else {
				Assert.assertTrue(true);
			}
		} catch (Exception e) {
			LOG.error("Excetion in testProcessInboundQuote = " + e);
			Assert.assertTrue(false);
		}
	}

}

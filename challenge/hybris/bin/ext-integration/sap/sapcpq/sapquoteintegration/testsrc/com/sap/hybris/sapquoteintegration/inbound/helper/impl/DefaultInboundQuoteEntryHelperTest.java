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
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sap.hybris.sapquoteintegration.inbound.helper.InboundQuoteEntryHelper;
import com.sap.hybris.sapquoteintegration.inbound.helper.InboundQuoteHelper;
import com.sap.hybris.sapquoteintegration.inbound.helper.impl.DefaultInboundQuoteEntryHelper;
import com.sap.hybris.sapquoteintegration.inbound.helper.impl.DefaultInboundQuoteHelper;
import com.sap.hybris.sapquoteintegration.service.SapQuoteService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.comments.services.CommentService;
import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.commerceservices.order.CommerceQuoteService;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.QuoteEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.StubLocaleProvider;
import de.hybris.platform.servicelayer.internal.model.impl.LocaleProvider;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ItemModelContextImpl;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

@UnitTest
public class DefaultInboundQuoteEntryHelperTest {
	@InjectMocks
	private DefaultInboundQuoteEntryHelper defaultInboundQuoteEntryHelper = new DefaultInboundQuoteEntryHelper();
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
	private ProductService productService;
	

	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testProcessInboundQuoteEntry() {
	    ClassificationAttributeModel classificationAttributeModel = new ClassificationAttributeModel();
	    LocaleProvider localeProvider = new StubLocaleProvider(Locale.ENGLISH);
	    ItemModelContextImpl itemModelContext = (ItemModelContextImpl) classificationAttributeModel.getItemModelContext();
	    itemModelContext.setLocaleProvider(localeProvider);
	    classificationAttributeModel.setCode("Procedure");
	    classificationAttributeModel.setName("Procedure");
	    
		List<CatalogModel> catalogs = new ArrayList<CatalogModel>();
		CatalogModel cm = new CatalogModel();
		CatalogVersionModel cvm  = new CatalogVersionModel();
		//cvm.setCategorySystemName("powertoolsCatalogModel");
		cvm.setActive(true);
		cvm.setCategorySystemName("powertoolsCatalogModel", Locale.ENGLISH);
		cm.setActiveCatalogVersion(cvm);
		catalogs.add(cm);
		BaseStoreModel baseStore = new BaseStoreModel();
		baseStore.setUid("powertools");
		baseStore.setCatalogs(catalogs);
		QuoteModel quote = new QuoteModel();
		quote.setCode("12345");
		quote.setStore(baseStore);
		quote.setHeaderDiscount(10.0d);
		ProductModel productModel=new ProductModel();
		productModel.setCode("7890");
		CurrencyModel cm1 = new CurrencyModel();
		cm1.setIsocode("USD");
		quote.setCurrency(cm1);
		QuoteEntryModel quoteEntryModel = new QuoteEntryModel();
		quoteEntryModel.setOrder(quote);
		quoteEntryModel.setBasePrice(10.0d);
		quoteEntryModel.setQuantity(1L);
		quoteEntryModel.setEntryDiscount(0.5d);
		quoteEntryModel.setRank("1");
		when(productService.getProductForCode(Mockito.any(String.class))).thenReturn(productModel);
		when(quoteService.getCurrentQuoteForCode(Mockito.any(String.class))).thenReturn(quote);

		QuoteEntryModel resultQuoteEntry = null;
		try {
			resultQuoteEntry = defaultInboundQuoteEntryHelper.processInboundQuoteEntry(quoteEntryModel);
			if (resultQuoteEntry == null) {
				Assert.assertTrue(false);
			} else {
				Assert.assertTrue(true);
			}
		} catch (Exception e) {
			System.out.println("INSIDE DefaultInboundQuoteEntryHelperTest --- EXCEPTION = "+ e);
			Assert.assertTrue(false);
		}
	}

}

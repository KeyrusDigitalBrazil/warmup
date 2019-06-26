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
package de.hybris.platform.sap.productconfig.cpiquoteexchange.service.inbound;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.core.model.order.QuoteEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ProductConfigurationRelatedObjectType;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPersistenceService;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationDeepCopyHandler;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;


@UnitTest
public class ProductConfigSCPIQuoteEntryInboundHookImplTest
{
	private static final String CONFIG_ID = "c123";
	private static final String NEW_CONFIG_ID = "c456";
	private static final String P_CODE = "p_code";

	@InjectMocks
	private ProductConfigSCPIQuoteEntryInboundHookImpl classUnderTest = new ProductConfigSCPIQuoteEntryInboundHookImpl();
	private QuoteModel quoteModel = new QuoteModel();
	private QuoteEntryModel quoteEntry = new QuoteEntryModel();
	private ProductConfigurationModel productConfigurationModel = new ProductConfigurationModel();
	private ProductConfigurationModel newProductConfigurationModel = new ProductConfigurationModel();
	private ProductModel productModel = new ProductModel();
	private BaseSiteModel baseSiteModel = new BaseSiteModel();
	private UserModel userModel = new UserModel();

	@Mock
	private ConfigurationDeepCopyHandler copyHandler;
	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private ModelService modelService;
	@Mock
	private ProductConfigurationPersistenceService persistenceService;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		quoteEntry.setProductConfiguration(productConfigurationModel);
		productConfigurationModel.setConfigurationId(CONFIG_ID);
		quoteEntry.setProduct(productModel);
		quoteEntry.setOrder(quoteModel);
		productModel.setCode(P_CODE);
		quoteModel.setSite(baseSiteModel);
		quoteModel.setUser(userModel);

		given(copyHandler.deepCopyConfiguration(anyString(), anyString(), anyString(), anyBoolean(),
				any(ProductConfigurationRelatedObjectType.class))).willReturn(NEW_CONFIG_ID);
		given(persistenceService.getByConfigId(NEW_CONFIG_ID)).willReturn(newProductConfigurationModel);
		newProductConfigurationModel.setConfigurationId(NEW_CONFIG_ID);
		given(modelService.create(CPQOrderEntryProductInfoModel.class)).willReturn(new CPQOrderEntryProductInfoModel());
	}
	
	@Test
	public void testProcessInboundQuoteEntryReturnsSameQuoteEntry()
	{
		assertSame(quoteEntry, classUnderTest.processInboundQuoteEntry(quoteEntry));
	}
	
	@Test
	public void testProcessInboundQuoteEntryWithoutConfig()
	{
		quoteEntry.setProductConfiguration(null);
		classUnderTest.processInboundQuoteEntry(quoteEntry);
		assertNull(quoteEntry.getProductConfiguration());
		verifyZeroInteractions(copyHandler);
	}

	@Test
	public void testProcessInboundQuoteEntryWithConfig()
	{
		classUnderTest.processInboundQuoteEntry(quoteEntry);
		assertEquals(NEW_CONFIG_ID, quoteEntry.getProductConfiguration().getConfigurationId());
		assertSame(userModel,  quoteEntry.getProductConfiguration().getUser());
		verify(copyHandler).deepCopyConfiguration(CONFIG_ID, P_CODE, null, true, ProductConfigurationRelatedObjectType.QUOTE_ENTRY);
		verify(modelService).detach(productConfigurationModel);
	}
	
	

	@Test
	public void testProcessInboundQuoteSetsBaseSite()
	{
		classUnderTest.processInboundQuoteEntry(quoteEntry);
		verify(baseSiteService).setCurrentBaseSite(same(baseSiteModel), anyBoolean());
	}


	@Test 
	public void testCreateBasicConfigurationInfo() {
		classUnderTest.createBasicConfigurationInfo(quoteEntry);
		assertFalse(CollectionUtils.isEmpty(quoteEntry.getProductInfos()));
		assertEquals(ConfiguratorType.CPQCONFIGURATOR, quoteEntry.getProductInfos().get(0).getConfiguratorType());
		assertSame(quoteEntry, quoteEntry.getProductInfos().get(0).getOrderEntry());
	}

	@Test
	public void testEnsureBaseSiteIsAvailableAlreadySet()
	{
		given(baseSiteService.getCurrentBaseSite()).willReturn(baseSiteModel);
		classUnderTest.ensureBaseSiteIsAvailable(quoteModel);
		verify(baseSiteService, times(0)).setCurrentBaseSite(any(BaseSiteModel.class), anyBoolean());
	}

	@Test
	public void testEnsureBaseSiteIsAvailableNotSet()
	{
		classUnderTest.ensureBaseSiteIsAvailable(quoteModel);
		verify(baseSiteService).setCurrentBaseSite(same(baseSiteModel), anyBoolean());
	}
}

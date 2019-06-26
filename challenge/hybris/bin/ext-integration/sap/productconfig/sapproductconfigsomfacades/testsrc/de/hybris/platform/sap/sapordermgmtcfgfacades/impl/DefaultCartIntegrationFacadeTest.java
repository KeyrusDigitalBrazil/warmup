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
package de.hybris.platform.sap.sapordermgmtcfgfacades.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.productconfig.facades.ConfigPricing;
import de.hybris.platform.sap.productconfig.facades.ConfigurationCartIntegrationFacade;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationMessageMapper;
import de.hybris.platform.sap.productconfig.facades.KBKeyData;
import de.hybris.platform.sap.productconfig.facades.UniqueUIKeyGenerator;
import de.hybris.platform.sap.productconfig.facades.populator.SolvableConflictPopulator;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.services.ConfigurationVariantUtil;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.analytics.intf.AnalyticsService;
import de.hybris.platform.sap.productconfig.services.intf.PricingService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationClassificationCacheStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationProductLinkStrategy;
import de.hybris.platform.sap.sapordermgmtb2bfacades.cart.CartRestorationFacade;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtservices.BackendAvailabilityService;
import de.hybris.platform.sap.sapproductconfigsomservices.cart.CPQCartService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;



@UnitTest
public class DefaultCartIntegrationFacadeTest
{
	DefaultCartIntegrationFacade classUnderTest;
	@Mock
	private ProductConfigurationService productConfigurationService;
	@Mock
	private CPQCartService cartService;
	@Mock
	private ProductService productService;
	@Mock
	private BackendAvailabilityService backendAvailabilityService;
	@Mock
	private ConfigurationCartIntegrationFacade configurationCartIntegrationFacade;
	@Mock
	private SessionAccessService sessionAccessService;
	@Mock
	private CartRestorationFacade cartRestorationFacade;
	@Mock
	private ConfigurationVariantUtil configurationVariantUtil;
	@Mock
	private ConfigurationData configurationData;
	@Mock
	private ProductModel productModel;

	@Mock
	private ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy;
	@Mock
	private ConfigurationProductLinkStrategy configurationProductLinkStrategy;

	@Mock
	private Item businessItem;

	private final ConfigurationData draftConfigurationData = new ConfigurationData();
	private final ConfigModel defaultConfigurationModel = new ConfigModelImpl();
	@Mock
	private ConfigurationClassificationCacheStrategy configurationClassificationCacheStrategy;
	private final InstanceModel rootInstance = new InstanceModelImpl();
	@Mock
	private UniqueUIKeyGenerator uiKeyGenerator;
	@Mock
	private SolvableConflictPopulator conflictsPopulator;
	@Mock
	private PricingService pricingService;
	@Mock
	private AnalyticsService analyticsService;
	@Mock
	private ConfigPricing configPricing;
	@Mock
	private ConfigurationMessageMapper messagesMapper;
	private static final String ITEM_KEY = "123";
	private static final String PRODUCT_CODE = "PRODUCT";
	private static final String CONFIG_ID = "configId";
	private static final String DRAFT_CONFIG_ID = "draft configId";
	private static final String DEFAULT_CONFIG_ID = "default configId";
	private static final String EXTERNAL_CONFIGURATION = "external configuration";
	private static final String DEFAULT_EXTERNAL_CONFIGURATION = "default external configuration";
	private static final String UNKNOWN = "Unknown";

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new DefaultCartIntegrationFacade()
		{
			@Override
			protected boolean isSapOrderMgmtEnabled()
			{

				return true;
			}
		};

		classUnderTest.setSessionAccessService(sessionAccessService);
		classUnderTest.setBackendAvailabilityService(backendAvailabilityService);
		classUnderTest.setProductConfigDefaultCartIntegrationFacade(configurationCartIntegrationFacade);
		classUnderTest.setConfigurationService(productConfigurationService);
		classUnderTest.setCartService(cartService);
		classUnderTest.setCartRestorationFacade(cartRestorationFacade);
		classUnderTest.setBackendAvailabilityService(backendAvailabilityService);
		classUnderTest.setAbstractOrderEntryLinkStrategy(configurationAbstractOrderEntryLinkStrategy);
		classUnderTest.setProductLinkStrategy(configurationProductLinkStrategy);
		classUnderTest.setConfigurationVariantUtil(configurationVariantUtil);
		classUnderTest.setProductService(productService);
		classUnderTest.setProductLinkStrategy(configurationProductLinkStrategy);
		classUnderTest.setClassificationCacheStrategy(configurationClassificationCacheStrategy);
		classUnderTest.setUiKeyGenerator(uiKeyGenerator);
		classUnderTest.setConflictPopulator(conflictsPopulator);
		classUnderTest.setPricingService(pricingService);
		classUnderTest.setAnalyticsService(analyticsService);
		classUnderTest.setConfigPricing(configPricing);
		classUnderTest.setMessagesMapper(messagesMapper);

		draftConfigurationData.setConfigId(DRAFT_CONFIG_ID);
		draftConfigurationData.setKbKey(new KBKeyData());
		draftConfigurationData.getKbKey().setProductCode(PRODUCT_CODE);

		defaultConfigurationModel.setId(DEFAULT_CONFIG_ID);
		defaultConfigurationModel.setRootInstance(rootInstance);

		Mockito.when(cartService.getItemByKey(ITEM_KEY)).thenReturn(businessItem);
		Mockito.when(businessItem.getProductId()).thenReturn(PRODUCT_CODE);
		Mockito.when(configurationAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry(ITEM_KEY)).thenReturn(CONFIG_ID);
		Mockito.when(productConfigurationService.retrieveExternalConfiguration(CONFIG_ID)).thenReturn(EXTERNAL_CONFIGURATION);
		Mockito.when(productConfigurationService.retrieveExternalConfiguration(DEFAULT_CONFIG_ID))
				.thenReturn(DEFAULT_EXTERNAL_CONFIGURATION);
		Mockito
				.when(configurationCartIntegrationFacade.draftConfig(Mockito.eq(ITEM_KEY), Mockito.any(KBKeyData.class),
						Mockito.eq(CONFIG_ID), Mockito.eq(true), Mockito.eq(EXTERNAL_CONFIGURATION)))
				.thenReturn(draftConfigurationData);
		Mockito.when(productService.getProductForCode(PRODUCT_CODE)).thenReturn(productModel);
		Mockito.when(configurationVariantUtil.isCPQVariantProduct(productModel)).thenReturn(false);
		Mockito.when(productConfigurationService.createDefaultConfiguration(Mockito.any())).thenReturn(defaultConfigurationModel);
	}

	/**
	 * Expect that the asynchronous facade is called
	 *
	 * @throws CommerceCartModificationException
	 */
	@Test
	public void testAddToCartNoBackend() throws CommerceCartModificationException
	{
		given(Boolean.valueOf(backendAvailabilityService.isBackendDown())).willReturn(Boolean.TRUE);
		given(configurationCartIntegrationFacade.addConfigurationToCart(configurationData)).willReturn(ITEM_KEY);
		final String key = classUnderTest.addConfigurationToCart(configurationData);

		assertEquals(key, ITEM_KEY);
	}


	/**
	 * Expect that both configuration integration facades are called
	 *
	 * @throws CommerceCartModificationException
	 */
	@Test
	public void testAddToCartBackendAvailable() throws CommerceCartModificationException
	{
		given(cartService.addConfigurationToCart(null)).willReturn(ITEM_KEY);
		final String key = classUnderTest.addConfigurationToCart(configurationData);

		assertEquals(key, ITEM_KEY);
	}

	/**
	 * Expect that an update and and add is done
	 *
	 * @throws CommerceCartModificationException
	 */
	@Test
	public void testAddToCartUpdateConfigurationAndBackendAvailable() throws CommerceCartModificationException
	{
		given(configurationAbstractOrderEntryLinkStrategy.getCartEntryForConfigId("123")).willReturn("PK");
		given(configurationData.getConfigId()).willReturn("123");
		given(Boolean.valueOf(cartService.isItemAvailable("PK"))).willReturn(Boolean.TRUE);
		given(cartService.updateConfigurationInCart(eq("PK"), any())).willReturn("PK");
		final String key = classUnderTest.addConfigurationToCart(configurationData);
		assertEquals(key, "PK");
	}

	@Test
	public void testProductLinkStrategy()
	{
		assertEquals(configurationProductLinkStrategy, classUnderTest.getProductLinkStrategy());
	}

	@Test
	public void testRemoveConfigurationLink()
	{
		classUnderTest.removeConfigurationLink(PRODUCT_CODE);
		verify(configurationProductLinkStrategy).removeConfigIdForProduct(PRODUCT_CODE);
	}

	@Test(expected = IllegalStateException.class)
	public void testAddProductConfigurationToCart() throws CommerceCartModificationException
	{
		classUnderTest.addProductConfigurationToCart(PRODUCT_CODE, 1L, CONFIG_ID);
	}

	@Test
	public void testConfigureCartItemUsingOldApiForBackwardCompatibility()
	{

		final ConfigurationData result = classUnderTest.configureCartItem(ITEM_KEY);

		assertNotNull(result);
		Mockito.verify(configurationAbstractOrderEntryLinkStrategy).getConfigIdForCartEntry(ITEM_KEY);
		Mockito.verify(configurationCartIntegrationFacade).draftConfig(Mockito.eq(ITEM_KEY), Mockito.any(KBKeyData.class),
				Mockito.eq(CONFIG_ID), Mockito.eq(true), Mockito.eq(EXTERNAL_CONFIGURATION));
	}

	@Test
	public void testConfigureCartItem()
	{
		final ConfigurationData result = classUnderTest.configureCartItem(ITEM_KEY);

		assertNotNull(result);
		Mockito.verify(configurationProductLinkStrategy, times(0)).setConfigIdForProduct(PRODUCT_CODE, DRAFT_CONFIG_ID);
		Mockito.verify(configurationCartIntegrationFacade).draftConfig(Mockito.eq(ITEM_KEY), Mockito.any(KBKeyData.class),
				Mockito.eq(CONFIG_ID), Mockito.eq(true), Mockito.eq(EXTERNAL_CONFIGURATION));
	}

	@Test
	public void testConfigureCartItemDefaultConfig()
	{
		Mockito.when(configurationAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry(ITEM_KEY)).thenReturn(null);
		Mockito
				.when(configurationCartIntegrationFacade.draftConfig(Mockito.eq(ITEM_KEY), Mockito.any(KBKeyData.class),
						Mockito.eq(DEFAULT_CONFIG_ID), Mockito.eq(false), Mockito.eq(DEFAULT_EXTERNAL_CONFIGURATION)))
				.thenReturn(draftConfigurationData);
		final ConfigurationData result = classUnderTest.configureCartItem(ITEM_KEY);

		assertNotNull(result);
		Mockito.verify(configurationProductLinkStrategy, times(0)).setConfigIdForProduct(PRODUCT_CODE, DRAFT_CONFIG_ID);
		Mockito.verify(configurationCartIntegrationFacade).draftConfig(Mockito.eq(ITEM_KEY), Mockito.any(KBKeyData.class),
				Mockito.eq(DEFAULT_CONFIG_ID), Mockito.eq(false), Mockito.eq(DEFAULT_EXTERNAL_CONFIGURATION));
	}

	@Test
	public void testConfigureCartItemOnExistingDraft()
	{
		Mockito.when(configurationAbstractOrderEntryLinkStrategy.getDraftConfigIdForCartEntry(ITEM_KEY))
				.thenReturn(DEFAULT_CONFIG_ID);
		Mockito.when(productConfigurationService.retrieveConfigurationModel(DEFAULT_CONFIG_ID))
				.thenReturn(defaultConfigurationModel);
		final ConfigurationData configurationDataFromDraft = classUnderTest.configureCartItemOnExistingDraft(ITEM_KEY);
		assertNotNull(configurationDataFromDraft);
		assertEquals(DEFAULT_CONFIG_ID, configurationDataFromDraft.getConfigId());
	}

	@Test(expected = IllegalStateException.class)
	public void testConfigureCartItemOnExistingDraftNoDraft()
	{
		Mockito.when(configurationAbstractOrderEntryLinkStrategy.getDraftConfigIdForCartEntry(ITEM_KEY)).thenReturn(null);
		classUnderTest.configureCartItemOnExistingDraft(ITEM_KEY);
	}

	@Test
	public void testConfigureCartItemOnExistingDraftNoEntry()
	{
		assertNull(classUnderTest.configureCartItemOnExistingDraft(UNKNOWN));
	}

}

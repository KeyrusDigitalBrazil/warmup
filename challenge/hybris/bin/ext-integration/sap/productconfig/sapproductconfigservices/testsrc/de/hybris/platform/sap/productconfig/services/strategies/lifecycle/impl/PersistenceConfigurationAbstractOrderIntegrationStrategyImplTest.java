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
package de.hybris.platform.sap.productconfig.services.strategies.lifecycle.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.ConfigurationVariantUtil;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPersistenceService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationLifecycleStrategy;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class PersistenceConfigurationAbstractOrderIntegrationStrategyImplTest
{
	private static final String CONFIG_ID = "configId";
	private static final String PRODUCT_CODE = "productCode";
	private static final PK CART_ENTRY_PK = PK.fromLong(1);
	private static final String EXT_CONFIG = "<xml/>";
	private static final String BASE_PRODUCT_CODE = "base";
	private static final String KB_NAME = "kbName";
	private static final String KB_VERSION = "kbVersion";
	private static final String KB_LOGSYS = "kbLogsys";

	private PersistenceConfigurationAbstractOrderIntegrationStrategyImpl classUnderTest;
	private final ProductConfigurationModel productConfigModel = new ProductConfigurationModel();

	@Mock
	private AbstractOrderEntryModel mockOrderEntry;
	@Mock
	private final AbstractOrderEntryModel mockOrderEntryWithPk = new AbstractOrderEntryModel();
	@Mock
	private ProductConfigurationPersistenceService mockPersistenceService;
	@Mock
	private ModelService mockModelService;
	@Mock
	private SessionAccessService mockSessionAccessServiceMock;
	@Mock
	private ProductConfigurationService mockConfigurationService;
	@Mock
	private ConfigModel mockConfigModel;
	@Mock
	private VariantProductModel mockProductModel;
	@Mock
	private ProductConfigurationModel mockProductConfigDraftModel;
	@Mock
	private CommerceCartParameter mockCommerceCartParameter;
	@Mock
	private ConfigurationVariantUtil mockConfigurationVariantUtil;
	@Mock
	private ProductService mockProductService;
	@Mock
	private ProductModel mockBaseProduct;
	@Mock
	private ConfigurationLifecycleStrategy mockConfigurationLifecycleStrategy;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new PersistenceConfigurationAbstractOrderIntegrationStrategyImpl();
		classUnderTest.setModelService(mockModelService);
		classUnderTest.setPersistenceService(mockPersistenceService);
		classUnderTest.setSessionAccessService(mockSessionAccessServiceMock);
		classUnderTest.setConfigurationService(mockConfigurationService);
		classUnderTest.setConfigurationVariantUtil(mockConfigurationVariantUtil);
		classUnderTest.setConfigurationLifecycleStrategy(mockConfigurationLifecycleStrategy);

		productConfigModel.setConfigurationId(CONFIG_ID);
		productConfigModel.setKbName(KB_NAME);
		productConfigModel.setKbVersion(KB_VERSION);
		productConfigModel.setKbLogsys(KB_LOGSYS);
		productConfigModel.setOwner(mockOrderEntry);

		given(mockPersistenceService.getByConfigId(CONFIG_ID)).willReturn(productConfigModel);
		given(mockPersistenceService.getOrderEntryByConfigId(CONFIG_ID, false)).willReturn(mockOrderEntryWithPk);
		given(mockOrderEntryWithPk.getPk()).willReturn(PK.fromLong(1));
		given(mockConfigurationService.retrieveConfigurationModel(CONFIG_ID)).willReturn(mockConfigModel);
		given(mockConfigurationService.createDefaultConfiguration(Mockito.any())).willReturn(mockConfigModel);
		given(mockConfigurationService.createConfigurationForVariant(any(), any())).willReturn(mockConfigModel);
		given(mockConfigurationService.isKbVersionValid(Mockito.any(KBKeyImpl.class))).willReturn(true);
		given(mockConfigurationService.retrieveExternalConfiguration(CONFIG_ID)).willReturn(EXT_CONFIG);
		given(mockOrderEntry.getProduct()).willReturn(mockProductModel);
		given(mockOrderEntry.getProductConfiguration()).willReturn(productConfigModel);
		given(mockOrderEntry.getProductConfigurationDraft()).willReturn(mockProductConfigDraftModel);
		given(mockOrderEntry.getPk()).willReturn(CART_ENTRY_PK);
		given(mockProductConfigDraftModel.getConfigurationId()).willReturn(CONFIG_ID);
		given(mockProductModel.getCode()).willReturn(PRODUCT_CODE);
		given(mockProductModel.getBaseProduct()).willReturn(mockBaseProduct);
		given(mockProductService.getProductForCode(PRODUCT_CODE)).willReturn(mockProductModel);
		given(mockBaseProduct.getCode()).willReturn(BASE_PRODUCT_CODE);
		given(mockConfigurationVariantUtil.getBaseProductCode(mockProductModel)).willReturn(BASE_PRODUCT_CODE);
	}

	@Test
	public void testVariantUtil()
	{
		assertEquals(mockConfigurationVariantUtil, classUnderTest.getConfigurationVariantUtil());
	}

	@Test
	public void testGetConfigurationForAbstractOrderEntry()
	{
		assertEquals(mockConfigModel, classUnderTest.getConfigurationForAbstractOrderEntry(mockOrderEntry));
	}

	@Test
	public void testGetConfigurationForAbstractOrderEntryForOneTimeAccess()
	{
		assertEquals(mockConfigModel, classUnderTest.getConfigurationForAbstractOrderEntryForOneTimeAccess(mockOrderEntry));
	}

	@Test
	public void testGetConfigurationForAbstractOrderEntryNoConfiguration()
	{
		mockOrderEntry.setProductConfiguration(null);
		assertNotNull(classUnderTest.getConfigurationForAbstractOrderEntry(mockOrderEntry));
	}

	@Test
	public void testCreateDefaultConfiguration()
	{
		assertNotNull(classUnderTest.createDefaultConfiguration(mockOrderEntry));
		verify(mockConfigurationService).createDefaultConfiguration(any());
	}

	@Test
	public void testCreateDefaultConfigurationIsVariant()
	{
		given(mockConfigurationVariantUtil.isCPQVariantProduct(mockProductModel)).willReturn(true);
		assertNotNull(classUnderTest.createDefaultConfiguration(mockOrderEntry));
		verify(mockConfigurationService).createConfigurationForVariant(any(), any());
	}

	@Test
	public void testIsKbVersionForEntryExisting()
	{
		final boolean result = classUnderTest.isKbVersionForEntryExisting(mockOrderEntry);
		final ArgumentCaptor<KBKeyImpl> argument = ArgumentCaptor.forClass(KBKeyImpl.class);
		verify(mockConfigurationService).isKbVersionValid(argument.capture());
		assertEquals(KB_NAME, argument.getValue().getKbName());
		assertEquals(PRODUCT_CODE, argument.getValue().getProductCode());
		assertTrue(result);
	}

	@Test
	public void testIsKbVersionForEntryExistingChanagebaleVariant()
	{
		given(mockConfigurationVariantUtil.isCPQChangeableVariantProduct(mockProductModel)).willReturn(true);
		final boolean result = classUnderTest.isKbVersionForEntryExisting(mockOrderEntry);
		final ArgumentCaptor<KBKeyImpl> argument = ArgumentCaptor.forClass(KBKeyImpl.class);
		verify(mockConfigurationService).isKbVersionValid(argument.capture());
		assertEquals(KB_NAME, argument.getValue().getKbName());
		assertEquals(BASE_PRODUCT_CODE, argument.getValue().getProductCode());
		assertTrue(result);
	}

	@Test
	public void testIsKbVersionForEntryExistingNotExisting()
	{
		given(mockConfigurationService.isKbVersionValid(Mockito.any(KBKeyImpl.class))).willReturn(false);
		assertFalse(classUnderTest.isKbVersionForEntryExisting(mockOrderEntry));
	}

	@Test(expected = NullPointerException.class)
	public void testIsKbVersionForEntryExistingNoConfig()
	{
		given(mockOrderEntry.getProductConfiguration()).willReturn(null);
		given(mockOrderEntry.getProductConfigurationDraft()).willReturn(null);
		classUnderTest.isKbVersionForEntryExisting(mockOrderEntry);
	}

	@Test
	public void testFinalizeCartEntry()
	{
		classUnderTest.finalizeCartEntry(mockOrderEntry);
		verify(mockOrderEntry).setProductConfigurationDraft(null);
		verify(mockOrderEntry, never()).setProductConfiguration(null);
	}

	@Test(expected = NullPointerException.class)
	public void testFinalizeCartEntryNoConfiguration()
	{
		given(mockOrderEntry.getProductConfiguration()).willReturn(null);
		classUnderTest.finalizeCartEntry(mockOrderEntry);
	}

	@Test
	public void testGetExternalConfigurationForAbstractOrderEntry()
	{
		assertEquals(EXT_CONFIG, classUnderTest.getExternalConfigurationForAbstractOrderEntry(mockOrderEntry));
	}

	@Test
	public void testInvalidateCartEntryConfiguration()
	{
		classUnderTest.invalidateCartEntryConfiguration(mockOrderEntry);
		verify(mockOrderEntry, atLeastOnce()).setProductConfiguration(null);
	}

	@Test
	public void testUpdateOrderEntryOnUpdate()
	{
		classUnderTest.updateAbstractOrderEntryOnUpdate(CONFIG_ID, mockOrderEntry);
		verify(mockOrderEntry, never()).setExternalConfiguration(EXT_CONFIG);
	}

	@Test
	public void testUpdateOrderEntryOnLink()
	{
		classUnderTest.updateAbstractOrderEntryOnLink(mockCommerceCartParameter, mockOrderEntry);
		verify(mockOrderEntry, never()).setExternalConfiguration(EXT_CONFIG);
	}

	@Test
	public void testPrepareForOrderReplication()
	{
		classUnderTest.prepareForOrderReplication(mockOrderEntry);
		verify(mockOrderEntry, atLeastOnce()).setExternalConfiguration(EXT_CONFIG);
	}

	@Test(expected = NullPointerException.class)
	public void testPrepareForOrderReplicationNoConfig()
	{
		when(mockOrderEntry.getProductConfiguration()).thenReturn(null);
		when(mockOrderEntry.getProductConfigurationDraft()).thenReturn(null);
		classUnderTest.prepareForOrderReplication(mockOrderEntry);
	}

	@Test
	public void testConfigurationLifecycleStrategy()
	{
		assertEquals(mockConfigurationLifecycleStrategy, classUnderTest.getConfigurationLifecycleStrategy());
	}

	@Test
	public void testReleaseDraft()
	{
		classUnderTest.releaseDraft(this.mockOrderEntry);
		verify(mockConfigurationLifecycleStrategy).releaseSession(CONFIG_ID);
	}

	@Test
	public void testReleaseDraftNoDraft()
	{
		given(mockOrderEntry.getProductConfigurationDraft()).willReturn(null);
		classUnderTest.releaseDraft(this.mockOrderEntry);
		verify(mockConfigurationLifecycleStrategy, times(0)).releaseSession(CONFIG_ID);
	}

	@Test
	public void testIsRuntimeConfigForEntryExistingTrue()
	{
		assertTrue(classUnderTest.isRuntimeConfigForEntryExisting(mockOrderEntry));
	}

	@Test
	public void testIsRuntimeConfigForEntryExistingFalse()
	{
		given(mockOrderEntry.getProductConfiguration()).willReturn(null);
		assertFalse(classUnderTest.isRuntimeConfigForEntryExisting(mockOrderEntry));
	}

	@Test
	public void testReleaseCartEntryProductRelationWithoutProductList()
	{
		final AbstractOrderEntryModel cartEntry = new AbstractOrderEntryModel();
		productConfigModel.setProduct(null);
		cartEntry.setProductConfiguration(productConfigModel);
		classUnderTest.releaseCartEntryProductRelation(cartEntry);
		verify(mockSessionAccessServiceMock, times(0)).removeUiStatusForCartEntry(any());
		verify(mockModelService, times(0)).save(productConfigModel);
	}

	@Test
	public void testReleaseCartEntryProductRelation()
	{
		final AbstractOrderEntryModel cartEntry = new AbstractOrderEntryModel();
		final Collection<ProductModel> productsList = new ArrayList<>();
		final ProductModel product = new ProductModel();
		product.setCode(PRODUCT_CODE);
		productsList.add(product);
		productConfigModel.setProduct(productsList);
		cartEntry.setProductConfiguration(productConfigModel);
		classUnderTest.releaseCartEntryProductRelation(cartEntry);
		assertTrue(productConfigModel.getProduct().isEmpty());
		verify(mockModelService, times(1)).save(productConfigModel);
	}
}




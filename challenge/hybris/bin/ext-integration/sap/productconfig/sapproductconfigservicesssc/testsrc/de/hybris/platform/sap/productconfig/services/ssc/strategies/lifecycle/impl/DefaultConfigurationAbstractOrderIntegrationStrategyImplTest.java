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
package de.hybris.platform.sap.productconfig.services.ssc.strategies.lifecycle.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.ConfigurationVariantUtil;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.tracking.TrackingRecorder;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.variants.model.VariantProductModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class DefaultConfigurationAbstractOrderIntegrationStrategyImplTest
{
	private static final String CONFIG_ID = "123";

	private static final String PRODUCT_ID = "CPQ_PRODUCT";

	private static final String EXTERNAL_CFG = "<xml/>";

	private static final String CONFIG_ID_NEW = "345";

	private static final String CONFIG_ID_DEFAULT = "default config id";

	private static final String BASE_PRODUCT_ID = "base";

	DefaultConfigurationAbstractOrderIntegrationStrategyImpl classUnderTest = new DefaultConfigurationAbstractOrderIntegrationStrategyImpl();


	@Mock
	private CommerceCartParameter parameters;

	@Mock
	private AbstractOrderEntryModel entry;

	@Mock
	private ProductConfigurationService configurationService;

	@Mock
	private ModelService modelService;

	@Mock
	private TrackingRecorder recorder;


	private final PK entryKey = PK.fromLong(1);

	@Mock
	private VariantProductModel productModel;

	@Mock
	private ProductModel baseProductModel;

	@Mock
	private ConfigModel configModelFromExternal;

	@Mock
	private ConfigModel configModelDefault;

	@Mock
	private SessionAccessService sessionAccessService;

	@Mock
	private ConfigModel configModelFromSession;

	@Mock
	private ConfigurationVariantUtil configurationVariantUtil;

	@Before
	public void initialize()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest.setConfigurationService(configurationService);
		classUnderTest.setModelService(modelService);
		classUnderTest.setRecorder(recorder);
		classUnderTest.setSessionAccessService(sessionAccessService);
		classUnderTest.setConfigurationVariantUtil(configurationVariantUtil);
		when(entry.getPk()).thenReturn(entryKey);
		when(entry.getProduct()).thenReturn(productModel);
		when(entry.getExternalConfiguration()).thenReturn(EXTERNAL_CFG);
		when(productModel.getCode()).thenReturn(PRODUCT_ID);
		when(productModel.getBaseProduct()).thenReturn(baseProductModel);
		when(baseProductModel.getCode()).thenReturn(BASE_PRODUCT_ID);
		when(configurationService.createConfigurationFromExternal(Mockito.any(), Mockito.any()))
				.thenReturn(configModelFromExternal);
		when(configurationService.createDefaultConfiguration(Mockito.any())).thenReturn(configModelDefault);
		when(configModelFromExternal.getId()).thenReturn(CONFIG_ID_NEW);
		when(configModelDefault.getId()).thenReturn(CONFIG_ID_DEFAULT);
		when(configModelFromSession.getId()).thenReturn(CONFIG_ID);
		when(configurationService.retrieveConfigurationModel(CONFIG_ID)).thenReturn(configModelFromSession);
		when(configurationService.retrieveExternalConfiguration(CONFIG_ID)).thenReturn(EXTERNAL_CFG);
		when(configurationService.isKbVersionValid(Mockito.any(KBKeyImpl.class))).thenReturn(true);
		when(configurationService.extractKbKey(PRODUCT_ID, entry.getExternalConfiguration())).thenReturn(new KBKeyImpl(PRODUCT_ID));
		when(configurationService.extractKbKey(BASE_PRODUCT_ID, entry.getExternalConfiguration()))
				.thenReturn(new KBKeyImpl(BASE_PRODUCT_ID));
		when(sessionAccessService.getConfigIdForCartEntry(entryKey.toString())).thenReturn(CONFIG_ID);
		when(configurationService.createConfigurationForVariant(BASE_PRODUCT_ID, PRODUCT_ID)).thenReturn(configModelFromExternal);
		when(configurationVariantUtil.getBaseProductCode(productModel)).thenReturn(BASE_PRODUCT_ID);

	}


	@Test
	public void testProductConfigurationService()
	{
		assertEquals(configurationService, classUnderTest.getConfigurationService());
	}

	@Test
	public void testRecorder()
	{
		assertEquals(recorder, classUnderTest.getRecorder());
	}

	@Test
	public void testModelService()
	{
		assertEquals(modelService, classUnderTest.getModelService());
	}

	@Test
	public void testUpdateAbstractOrderEntryOnLink()
	{
		classUnderTest.updateAbstractOrderEntryOnLink(parameters, entry);
		Mockito.verify(modelService, Mockito.times(1)).save(entry);
	}

	@Test
	public void testUpdateAbstractOrderEntryOnUpdate()
	{
		classUnderTest.updateAbstractOrderEntryOnUpdate(CONFIG_ID, entry);
		Mockito.verify(configurationService, Mockito.times(1)).retrieveExternalConfiguration(CONFIG_ID);
		Mockito.verify(modelService).save(entry);
	}

	@Test
	public void testGetConfigurationForAbstractOrderEntry()
	{
		Mockito.when(configurationService.retrieveConfigurationModel(CONFIG_ID)).thenReturn(configModelFromExternal);
		Mockito.when(sessionAccessService.getConfigIdForCartEntry(entryKey.toString())).thenReturn(null);
		assertEquals(configModelFromExternal, classUnderTest.getConfigurationForAbstractOrderEntry(entry));
		Mockito.verify(sessionAccessService).setConfigIdForCartEntry(entryKey.toString(), CONFIG_ID_NEW);
	}

	@Test
	public void testGetConfigurationForAbstractOrderEntryConfigurationInSession()
	{
		assertEquals(configModelFromSession, classUnderTest.getConfigurationForAbstractOrderEntry(entry));
	}

	@Test
	public void testEnsureExternalConfigurationIsPresentAreadyPresent()
	{
		classUnderTest.ensureExternalConfigurationIsPresent(CONFIG_ID, entry);
		Mockito.verify(entry, Mockito.times(0)).setExternalConfiguration(EXTERNAL_CFG);
	}

	@Test
	public void testEnsureExternalConfigurationIsNotPresent()
	{
		Mockito.when(entry.getExternalConfiguration()).thenReturn(null);
		classUnderTest.ensureExternalConfigurationIsPresent(CONFIG_ID, entry);
		Mockito.verify(entry, Mockito.times(1)).setExternalConfiguration(EXTERNAL_CFG);
		Mockito.verify(modelService).save(entry);
	}

	@Test
	public void testIsKbVersionForEntryExisting()
	{
		assertTrue(classUnderTest.isKbVersionForEntryExisting(entry));
		final ArgumentCaptor<KBKeyImpl> argument = ArgumentCaptor.forClass(KBKeyImpl.class);
		verify(configurationService).isKbVersionValid(argument.capture());
		assertEquals(PRODUCT_ID, argument.getValue().getProductCode());
	}

	@Test
	public void testIsKbVersionForEntryExistingChangeableVariant()
	{
		given(configurationVariantUtil.isCPQChangeableVariantProduct(productModel)).willReturn(true);
		assertTrue(classUnderTest.isKbVersionForEntryExisting(entry));
		final ArgumentCaptor<KBKeyImpl> argument = ArgumentCaptor.forClass(KBKeyImpl.class);
		verify(configurationService).isKbVersionValid(argument.capture());
		assertEquals(BASE_PRODUCT_ID, argument.getValue().getProductCode());
	}

	@Test
	public void testIsKbVersionForEntryExistingNotExisting()
	{
		when(configurationService.isKbVersionValid(Mockito.any(KBKeyImpl.class))).thenReturn(false);
		assertFalse(classUnderTest.isKbVersionForEntryExisting(entry));
	}

	@Test
	public void testFinalizeCartEntry()
	{
		classUnderTest.finalizeCartEntry(entry);
		verify(sessionAccessService).removeSessionArtifactsForCartEntry(entryKey.toString());
		verify(configurationService).releaseSession(CONFIG_ID);
	}

	@Test
	public void testGetExternalConfigurationForAbstractOrderEntry()
	{
		assertEquals(EXTERNAL_CFG, classUnderTest.getExternalConfigurationForAbstractOrderEntry(entry));
	}

	@Test
	public void testGetConfigurationForAbstractOrderEntryForOneTimeAccess()
	{
		assertEquals(configModelFromSession, classUnderTest.getConfigurationForAbstractOrderEntryForOneTimeAccess(entry));
		verify(configurationService).releaseSession(CONFIG_ID, true);
		verify(sessionAccessService).removeConfigIdForCartEntry(entryKey.toString());
	}

	@Test
	public void testInvalidateCartEntryConfiguration()
	{
		classUnderTest.invalidateCartEntryConfiguration(entry);
		verify(entry, atLeastOnce()).setExternalConfiguration(null);
	}

	@Test
	public void testPrepareForOrderReplication()
	{
		classUnderTest.prepareForOrderReplication(entry);
		verify(entry, never()).setExternalConfiguration(EXTERNAL_CFG);
	}

	@Test
	public void testCreateConfiguration()
	{
		assertNotNull(classUnderTest.createConfiguration(entry, entryKey.toString()));
		verify(configurationService).createConfigurationFromExternal(any(), any());
		verify(sessionAccessService).setConfigIdForCartEntry(any(), any());
	}

	@Test
	public void testCreateConfigurationForVariant()
	{
		when(configurationVariantUtil.isCPQNotChangeableVariantProduct(productModel)).thenReturn(true);
		assertNotNull(classUnderTest.createConfiguration(entry, entryKey.toString()));
		verify(configurationService).createConfigurationForVariant(any(), any());
		verify(sessionAccessService, Mockito.times(0)).setConfigIdForCartEntry(any(), any());
	}

	@Test
	public void testCreateConfigurationExternalConfigNull()
	{
		Mockito.when(entry.getExternalConfiguration()).thenReturn(null);
		when(configurationVariantUtil.isCPQNotChangeableVariantProduct(productModel)).thenReturn(false);
		assertNotNull(classUnderTest.createConfiguration(entry, entryKey.toString()));
		verify(configurationService).createDefaultConfiguration(any());
	}

	@Test
	public void testIsRuntimeConfigForEntryExistingTrueDueToExtConfig()
	{
		given(entry.getExternalConfiguration()).willReturn(EXTERNAL_CFG);
		given(sessionAccessService.getConfigIdForCartEntry(entryKey.toString())).willReturn(null);
		assertTrue(classUnderTest.isRuntimeConfigForEntryExisting(entry));
	}


	@Test
	public void testIsRuntimeConfigForEntryExistingTrueDueToSessionConfig()
	{
		given(entry.getExternalConfiguration()).willReturn(null);
		given(sessionAccessService.getConfigIdForCartEntry(entryKey.toString())).willReturn(CONFIG_ID);
		assertTrue(classUnderTest.isRuntimeConfigForEntryExisting(entry));
	}


	@Test
	public void testIsRuntimeConfigForEntryExistingFalse()
	{
		given(entry.getExternalConfiguration()).willReturn(null);
		given(sessionAccessService.getConfigIdForCartEntry(entryKey.toString())).willReturn(null);
		assertFalse(classUnderTest.isRuntimeConfigForEntryExisting(entry));
	}
}

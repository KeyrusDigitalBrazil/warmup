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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPersistenceService;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class PersistenceConfigurationAbstractOrderEntryLinkStrategyImplTest
{

	private static final String SESSION_ID = "session123";
	private static final String CONFIG_ID = "configId";
	private static final String PRODUCT_CODE = "productCode";
	private static final String CART_ENTRY_KEY = "cartEntry1";
	private static final PK CART_ENTRY_PK = PK.fromLong(1);

	private PersistenceConfigurationAbstractOrderEntryLinkStrategyImpl classUnderTest;
	private final ProductConfigurationModel productConfigModel = new ProductConfigurationModel();

	@Mock
	private AbstractOrderEntryModel orderEntry;
	@Mock
	private final AbstractOrderEntryModel orderEntryWithPk = new AbstractOrderEntryModel();
	@Mock
	private ConfigurationAbstractOrderEntryLinkStrategy defaultStrategy;
	@Mock
	private ProductConfigurationPersistenceService persistenceService;
	@Mock
	private ModelService modelService;
	@Mock
	private SessionAccessService sessionAccessServiceMock;
	@Mock
	private ProductConfigurationModel productConfigDraftModel;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new PersistenceConfigurationAbstractOrderEntryLinkStrategyImpl();
		classUnderTest.setModelService(modelService);
		classUnderTest.setPersistenceService(persistenceService);
		classUnderTest.setSessionAccessService(sessionAccessServiceMock);

		productConfigModel.setConfigurationId(CONFIG_ID);
		productConfigModel.setOwner(orderEntry);

		given(persistenceService.getByConfigId(CONFIG_ID)).willReturn(productConfigModel);
		given(persistenceService.getOrderEntryByPK(CART_ENTRY_KEY)).willReturn(orderEntry);
		given(persistenceService.getOrderEntryByConfigId(CONFIG_ID, false)).willReturn(orderEntryWithPk);
		given(orderEntryWithPk.getPk()).willReturn(PK.fromLong(1));
		given(orderEntry.getProductConfiguration()).willReturn(productConfigModel);
		given(orderEntry.getProductConfigurationDraft()).willReturn(productConfigDraftModel);
		given(orderEntry.getPk()).willReturn(CART_ENTRY_PK);
		given(productConfigDraftModel.getConfigurationId()).willReturn(CONFIG_ID);
	}


	@Test
	public void testRremoveSessionArtifactsForCartEntry()
	{
		final ProductModel product = new ProductModel();
		product.setCode(PRODUCT_CODE);
		productConfigModel.setProduct(Collections.singletonList(product));

		classUnderTest.removeSessionArtifactsForCartEntry(CART_ENTRY_KEY);

		assertTrue(productConfigModel.getProduct().isEmpty());
		verify(modelService).save(orderEntry);
		verify(modelService).save(productConfigModel);
		verify(sessionAccessServiceMock).removeUiStatusForCartEntry(CART_ENTRY_KEY);
		verify(sessionAccessServiceMock).removeUiStatusForProduct(PRODUCT_CODE);

	}

	@Test
	public void testRemoveSessionArtifactsForCartEntryNoProductLink()
	{
		classUnderTest.removeSessionArtifactsForCartEntry(CART_ENTRY_KEY);
		verify(modelService).save(orderEntry);
		verify(sessionAccessServiceMock).removeUiStatusForCartEntry(CART_ENTRY_KEY);
	}

	@Test
	public void testRemoveSessionArtifactsForCartEntryWithDraft()
	{
		classUnderTest.removeSessionArtifactsForCartEntry(CART_ENTRY_KEY);
		verify(modelService).save(orderEntry);
		verify(sessionAccessServiceMock).removeUiStatusForCartEntry(CART_ENTRY_KEY);
	}


	@Test
	public void testSetConfigIdForCartEntry()
	{
		classUnderTest.setConfigIdForCartEntry(CART_ENTRY_KEY, CONFIG_ID);
		verify(modelService).save(orderEntry);
		assertEquals(CONFIG_ID, productConfigModel.getConfigurationId());
	}

	@Test
	public void testGetConfigIdForCartEntry()
	{
		final String configId = classUnderTest.getConfigIdForCartEntry(CART_ENTRY_KEY);
		assertEquals(CONFIG_ID, configId);

	}

	@Test
	public void testGetConfigIdForCartEntryReturnsNull()
	{
		Mockito.when(orderEntry.getProductConfiguration()).thenReturn(null);
		final String configId = classUnderTest.getConfigIdForCartEntry(CART_ENTRY_KEY);
		assertNull(configId);
	}

	@Test
	public void testGetDraftConfigIdForCartEntry()
	{
		orderEntry.setProductConfiguration(null);
		orderEntry.setProductConfigurationDraft(productConfigModel);
		final String configId = classUnderTest.getDraftConfigIdForCartEntry(CART_ENTRY_KEY);
		assertEquals(CONFIG_ID, configId);
	}

	@Test
	public void testGetDraftConfigIdForCartEntryReturnsNull()
	{
		Mockito.when(orderEntry.getProductConfigurationDraft()).thenReturn(null);
		final String configId = classUnderTest.getDraftConfigIdForCartEntry(CART_ENTRY_KEY);
		assertNull(configId);
	}


	@Test
	public void testRemoveConfigIdForCartEntry()
	{
		classUnderTest.removeConfigIdForCartEntry(CART_ENTRY_KEY);
		verify(modelService).save(orderEntry);
		verify(orderEntry).setProductConfiguration(null);
	}

	@Test
	public void testGetCartEntryForConfigId()
	{
		final String cartKey = classUnderTest.getCartEntryForConfigId(CONFIG_ID);
		assertEquals(CART_ENTRY_PK.toString(), cartKey);
	}

	@Test
	public void testGetCartEntryForDraftConfigId()
	{
		given(persistenceService.getOrderEntryByConfigId(CONFIG_ID, false)).willReturn(null);
		given(persistenceService.getOrderEntryByConfigId(CONFIG_ID, true)).willReturn(orderEntryWithPk);
		final String cartKey = classUnderTest.getCartEntryForDraftConfigId(CONFIG_ID);
		assertEquals(CART_ENTRY_PK.toString(), cartKey);
	}

	@Test
	public void testGetCartEntryForConfigIdReturnsNull()
	{
		final String cartKey = classUnderTest.getCartEntryForConfigId("bla");
		assertNull(cartKey);
	}

	@Test
	public void testGetCartEntryForDraftConfigIdReturnsNull()
	{
		final String cartKey = classUnderTest.getCartEntryForDraftConfigId("bla");
		assertNull(cartKey);
	}


	@Test
	public void testSetDraftConfigIdForCartEntry()
	{
		classUnderTest.setDraftConfigIdForCartEntry(CART_ENTRY_KEY, CONFIG_ID);
		verify(modelService).save(orderEntry);
		assertEquals(CONFIG_ID, productConfigModel.getConfigurationId());
	}


	@Test
	public void testRemoveDraftConfigIdForCartEntry()
	{
		classUnderTest.removeConfigIdForCartEntry(CART_ENTRY_KEY);
		verify(modelService).save(orderEntry);
		verify(orderEntry).setProductConfiguration(null);
	}

	@Test
	public void testIsDocumentRelatedRetursTrue()
	{
		given(persistenceService.getAllOrderEntriesByConfigId(CONFIG_ID)).willReturn(Collections.singletonList(orderEntry));
		assertTrue(classUnderTest.isDocumentRelated(CONFIG_ID));
	}


	@Test
	public void testIsDocumentRelatedRetursFalse()
	{
		given(persistenceService.getAllOrderEntriesByConfigId(CONFIG_ID)).willReturn(Collections.emptyList());
		assertFalse(classUnderTest.isDocumentRelated(CONFIG_ID));
	}

}

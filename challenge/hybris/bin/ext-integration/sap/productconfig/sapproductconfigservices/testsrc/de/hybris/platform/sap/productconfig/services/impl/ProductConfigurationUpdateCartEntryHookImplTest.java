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
package de.hybris.platform.sap.productconfig.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.tracking.TrackingRecorder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductConfigurationUpdateCartEntryHookImplTest
{
	private static final String ENTRY_KEY = "100";
	private static final int ENTRY_NUM = 1;
	private static final String CONFIG_ID = "A";
	private static final String CONFIG_ID_DRAFT = "B";

	private final ProductConfigurationUpdateCartEntryHookImpl classUnderTest = new ProductConfigurationUpdateCartEntryHookImpl();
	private CartModel cart;
	private CommerceCartParameter parameter;
	private CommerceCartModification result;

	@Mock
	private ProductConfigurationService productConfigurationService;
	@Mock
	private ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy;
	@Mock
	private TrackingRecorder recorder;
	@Mock
	private AbstractOrderEntryModel entry;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest.setProductConfigurationService(productConfigurationService);
		classUnderTest.setAbstractOrderEntryLinkStrategy(configurationAbstractOrderEntryLinkStrategy);
		classUnderTest.setRecorder(recorder);

		cart = new CartModel();
		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		given(entry.getPk()).willReturn(PK.parse(ENTRY_KEY));
		given(entry.getEntryNumber()).willReturn(ENTRY_NUM);
		entries.add(entry);
		cart.setEntries(entries);

		parameter = new CommerceCartParameter();
		parameter.setCart(cart);
		parameter.setEntryNumber(ENTRY_NUM);
		parameter.setQuantity(0);

		result = new CommerceCartModification();
	}

	@Test
	public void testAfterUpdateCartEntryNoConfigToBeDeleted()
	{
		classUnderTest.afterUpdateCartEntry(parameter, result);
		verifyZeroInteractions(productConfigurationService);
	}

	@Test
	public void testAfterUpdateCartEntryConfigToBeDeleted()
	{
		parameter.setConfigToBeDeleted(CONFIG_ID);
		classUnderTest.afterUpdateCartEntry(parameter, result);
		verify(productConfigurationService).releaseSession(CONFIG_ID);
	}

	@Test
	public void testAfterUpdateCartEntryDraftConfigToBeDeleted()
	{
		parameter.setConfigToBeDeleted(CONFIG_ID);
		parameter.setDraftConfigToBeDeleted(CONFIG_ID_DRAFT);
		classUnderTest.afterUpdateCartEntry(parameter, result);
		verify(productConfigurationService).releaseSession(CONFIG_ID);
		verify(productConfigurationService).releaseSession(CONFIG_ID_DRAFT);
	}

	@Test
	public void testAfterUpdateCartEntryUpdateConfigurable()
	{
		parameter.setQuantity(5);
		parameter.setConfigToBeDeleted(CONFIG_ID);
		classUnderTest.afterUpdateCartEntry(parameter, result);
		verifyZeroInteractions(productConfigurationService);
	}

	@Test
	public void testBeforeUpdateCartEntryNoConfigurableEntries()
	{
		classUnderTest.beforeUpdateCartEntry(parameter);
		assertNull(parameter.getConfigToBeDeleted());
	}

	@Test
	public void testBeforeUpdateCartEntryDeleteConfigurable()
	{
		given(configurationAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry(ENTRY_KEY)).willReturn(CONFIG_ID);
		classUnderTest.beforeUpdateCartEntry(parameter);
		assertEquals(CONFIG_ID, parameter.getConfigToBeDeleted());
		verify(configurationAbstractOrderEntryLinkStrategy).removeSessionArtifactsForCartEntry(ENTRY_KEY);
		verify(recorder).recordDeleteCartEntry(entry, parameter);
	}

	@Test
	public void testBeforeUpdateCartNoEntries()
	{
		cart.setEntries(Collections.emptyList());
		given(configurationAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry(ENTRY_KEY)).willReturn(CONFIG_ID);
		classUnderTest.beforeUpdateCartEntry(parameter);
		assertNull(parameter.getConfigToBeDeleted());
		verifyZeroInteractions(configurationAbstractOrderEntryLinkStrategy);
		verifyZeroInteractions(recorder);
	}


	@Test
	public void testBeforeUpdateCartWrongEntry()
	{
		parameter.setEntryNumber(2);
		given(configurationAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry(ENTRY_KEY)).willReturn(CONFIG_ID);
		classUnderTest.beforeUpdateCartEntry(parameter);
		assertNull(parameter.getConfigToBeDeleted());
		verifyZeroInteractions(configurationAbstractOrderEntryLinkStrategy);
		verifyZeroInteractions(recorder);
	}


	@Test
	public void testBeforeUpdateCartEntryUpdateConfigurable()
	{
		parameter.setQuantity(5);
		given(configurationAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry(ENTRY_KEY)).willReturn(CONFIG_ID);
		classUnderTest.beforeUpdateCartEntry(parameter);
		assertNull(parameter.getConfigToBeDeleted());
		verifyZeroInteractions(configurationAbstractOrderEntryLinkStrategy);
		verifyZeroInteractions(recorder);
	}

	@Test
	public void testBeforeUpdateCartEntryUpdateNull()
	{
		cart.setEntries(Collections.singletonList(null));
		classUnderTest.beforeUpdateCartEntry(parameter);
		assertNull(parameter.getConfigToBeDeleted());
		verifyZeroInteractions(configurationAbstractOrderEntryLinkStrategy);
		verifyZeroInteractions(recorder);
	}

	@Test
	public void testHandleCartEntryWithDraft()
	{
		given(configurationAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry(ENTRY_KEY)).willReturn(CONFIG_ID);
		given(configurationAbstractOrderEntryLinkStrategy.getDraftConfigIdForCartEntry(ENTRY_KEY)).willReturn(CONFIG_ID_DRAFT);
		classUnderTest.handleCartEntry(parameter, entry);
		assertEquals("Draft configId to be deleted: ", CONFIG_ID_DRAFT, parameter.getDraftConfigToBeDeleted());
	}
}

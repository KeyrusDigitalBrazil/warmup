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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class DefaultConfigurationAbstractOrderEntryLinkStrategyImplTest
{
	private static final String ENTRY_KEY = "entry key";
	private static final String CONFIG_ID = "123";


	@Mock
	private SessionAccessService sessionAccessService;

	@InjectMocks
	DefaultConfigurationAbstractOrderEntryLinkStrategyImpl classUnderTest = new DefaultConfigurationAbstractOrderEntryLinkStrategyImpl();


	@Before
	public void initialize()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetCartEntryForConfigId()
	{
		classUnderTest.getCartEntryForConfigId(CONFIG_ID);
		verify(sessionAccessService).getCartEntryForConfigId(CONFIG_ID);
	}

	@Test
	public void testGetCartEntryForDraftConfigId()
	{
		classUnderTest.getCartEntryForDraftConfigId(CONFIG_ID);
		verify(sessionAccessService).getCartEntryForDraftConfigId(CONFIG_ID);
	}

	@Test
	public void testIsDocumentRelatedRetursTrue()
	{
		given(sessionAccessService.getCartEntryForConfigId(CONFIG_ID)).willReturn(ENTRY_KEY);
		assertTrue(classUnderTest.isDocumentRelated(CONFIG_ID));
	}

	@Test
	public void testIsDocumentRelatedRetursTrueForDrafts()
	{
		given(sessionAccessService.getCartEntryForDraftConfigId(CONFIG_ID)).willReturn(ENTRY_KEY);
		assertTrue(classUnderTest.isDocumentRelated(CONFIG_ID));
	}

	@Test
	public void testIsDocumentRelatedRetursFalse()
	{
		assertFalse(classUnderTest.isDocumentRelated(CONFIG_ID));
	}
}

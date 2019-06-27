/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.commerceservices.attributeHandlers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.model.ConsentActiveAttribute;
import de.hybris.platform.commerceservices.model.consent.ConsentModel;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * ActiveAttribute dynamic attribute handler unit test. This dynamic attribute is defined in Consent type.
 */
@UnitTest
public class ConsentActiveAttributeTest
{
	private ConsentModel consentModel;

	private ConsentActiveAttribute consentActiveAttribute;

	@Before
	public void setUp()
	{
		consentModel = Mockito.mock(ConsentModel.class);
		consentActiveAttribute = new ConsentActiveAttribute();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAttributeHandlerForNull()
	{
		consentActiveAttribute.get(null);
	}

	@Test
	public void testAttributeHandlerWhenConsentWithdrawnDateExists()
	{
		Mockito.when(consentModel.getConsentWithdrawnDate()).thenReturn(new Date());
		assertFalse(consentActiveAttribute.get(consentModel).booleanValue());
	}

	@Test
	public void testAttributeHandlerWhenNoConsentWithdrawnDate()
	{
		Mockito.when(consentModel.getConsentWithdrawnDate()).thenReturn(null);
		assertTrue(consentActiveAttribute.get(consentModel).booleanValue());
	}
}
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
package de.hybris.platform.sap.productconfig.facades.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.facades.ProductConfigMessageData;
import de.hybris.platform.sap.productconfig.facades.ProductConfigMessageUISeverity;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessagePromoType;

import org.junit.Test;


@UnitTest
public class ConfigurationMessageComparatorTest
{

	private final ConfigurationMessageComparator classUnderTest = new ConfigurationMessageComparator();

	@Test
	public void testCompareDiscountMessagesConsideringPromoTypeWrongOrder()
	{
		final ProductConfigMessageData m1 = createMessage("Message 2", ProductConfigMessagePromoType.PROMO_OPPORTUNITY, null);
		final ProductConfigMessageData m2 = createMessage("Message 1", ProductConfigMessagePromoType.PROMO_APPLIED, null);
		assertEquals(1, classUnderTest.compareDiscountMessagesConsideringPromoType(m1, m2));
	}

	@Test
	public void testCompareDiscountMessagesConsideringPromoTypeEqual()
	{
		final ProductConfigMessageData m1 = createMessage("Message 2", ProductConfigMessagePromoType.PROMO_APPLIED, null);
		final ProductConfigMessageData m2 = createMessage("Message 1", ProductConfigMessagePromoType.PROMO_APPLIED, null);
		assertEquals(0, classUnderTest.compareDiscountMessagesConsideringPromoType(m1, m2));
	}


	@Test
	public void testCompareDiscountMessagesConsideringPromoTypeCorrectOrder()
	{
		final ProductConfigMessageData m2 = createMessage("Message 1", ProductConfigMessagePromoType.PROMO_APPLIED, null);
		final ProductConfigMessageData m1 = createMessage("Message 2", ProductConfigMessagePromoType.PROMO_OPPORTUNITY, null);
		assertEquals(-1, classUnderTest.compareDiscountMessagesConsideringPromoType(m2, m1));
	}

	@Test
	public void testCompareDiscountMessagesConsideringPromoTypeNoDiscountMessage()
	{
		final ProductConfigMessageData m2 = createMessage("Message 1", null, ProductConfigMessageUISeverity.INFO);
		final ProductConfigMessageData m1 = createMessage("Message 2", ProductConfigMessagePromoType.PROMO_OPPORTUNITY, null);
		assertEquals(-2, classUnderTest.compareDiscountMessagesConsideringPromoType(m2, m1));
	}

	@Test
	public void testCompareStandardMessagesConsideringSeverityWrongOrder()
	{
		final ProductConfigMessageData m1 = createMessage("Message 2", null, ProductConfigMessageUISeverity.CONFIG);
		final ProductConfigMessageData m2 = createMessage("Message 1", null, ProductConfigMessageUISeverity.INFO);
		assertEquals(1, classUnderTest.compareStandardMessagesConsideringSeverity(m1, m2));
	}

	@Test
	public void testCompareStandardMessagesConsideringSeverityCorrectOrder()
	{
		final ProductConfigMessageData m2 = createMessage("Message 1", null, ProductConfigMessageUISeverity.INFO);
		final ProductConfigMessageData m1 = createMessage("Message 2", null, ProductConfigMessageUISeverity.CONFIG);
		assertEquals(-1, classUnderTest.compareStandardMessagesConsideringSeverity(m2, m1));
	}

	@Test
	public void testCompareStandardMessagesConsideringSeverityEqual()
	{
		final ProductConfigMessageData m2 = createMessage("Message 1", null, ProductConfigMessageUISeverity.INFO);
		final ProductConfigMessageData m1 = createMessage("Message 2", null, ProductConfigMessageUISeverity.INFO);
		assertEquals(0, classUnderTest.compareStandardMessagesConsideringSeverity(m2, m1));
	}

	@Test
	public void testCompareStandardMessagesConsideringSeverityNoStandardMessage()
	{
		final ProductConfigMessageData m2 = createMessage("Message 1", ProductConfigMessagePromoType.PROMO_OPPORTUNITY, null);
		final ProductConfigMessageData m1 = createMessage("Message 2", null, ProductConfigMessageUISeverity.INFO);
		assertEquals(-2, classUnderTest.compareStandardMessagesConsideringSeverity(m2, m1));
	}

	@Test
	public void testCompareStandardVsDiscountCorrectOrder()
	{
		final ProductConfigMessageData m1 = createMessage("Message 2", null, ProductConfigMessageUISeverity.INFO);
		final ProductConfigMessageData m2 = createMessage("Message 1", ProductConfigMessagePromoType.PROMO_OPPORTUNITY, null);
		assertEquals(-1, classUnderTest.compare(m1, m2));
	}

	@Test
	public void testCompareStandardVsDiscountWrongOrder()
	{
		final ProductConfigMessageData m2 = createMessage("Message 1", ProductConfigMessagePromoType.PROMO_OPPORTUNITY, null);
		final ProductConfigMessageData m1 = createMessage("Message 2", null, ProductConfigMessageUISeverity.INFO);
		assertEquals(1, classUnderTest.compare(m2, m1));
	}


	@Test
	public void testCompareWithStandardMessagesWrongOrder()
	{
		final ProductConfigMessageData m1 = createMessage("Message 2", null, ProductConfigMessageUISeverity.CONFIG);
		final ProductConfigMessageData m2 = createMessage("Message 2", null, ProductConfigMessageUISeverity.INFO);
		assertEquals(1, classUnderTest.compare(m1, m2));
	}

	@Test
	public void testCompareWithDiscountMessagesWrongOrder()
	{
		final ProductConfigMessageData m1 = createMessage("Message 2", ProductConfigMessagePromoType.PROMO_OPPORTUNITY, null);
		final ProductConfigMessageData m2 = createMessage("Message 1", ProductConfigMessagePromoType.PROMO_APPLIED, null);
		assertEquals(1, classUnderTest.compare(m1, m2));
	}


	public static ProductConfigMessageData createMessage(final String message, final ProductConfigMessagePromoType type,
			final ProductConfigMessageUISeverity severity)
	{
		final ProductConfigMessageData uiMessage = new ProductConfigMessageData();
		uiMessage.setMessage(message);
		uiMessage.setPromoType(type);
		uiMessage.setSeverity(severity);
		return uiMessage;
	}


}

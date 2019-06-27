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
package de.hybris.platform.sap.productconfig.runtime.interf.model.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessagePromoType;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSeverity;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSource;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSourceSubType;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;


@UnitTest
public class ProductConfigMessageImplTest
{

	private ProductConfigMessage classUnderTest;
	private ProductConfigMessageBuilder builder;

	@Before
	public void setUp()
	{
		builder = new ProductConfigMessageBuilder();
		classUnderTest = appendDefaults(builder).build();
		appendDefaults(builder.reset());
	}


	protected ProductConfigMessageBuilder appendDefaults(final ProductConfigMessageBuilder builder)
	{
		builder.appendBasicFields("test message", "123", ProductConfigMessageSeverity.INFO);
		builder.appendSourceAndType(ProductConfigMessageSource.ENGINE, ProductConfigMessageSourceSubType.DEFAULT);
		return builder;
	}


	@Test
	public void testDataNotModified()
	{
		assertNotNull(classUnderTest);
		assertEquals("test message", classUnderTest.getMessage());
		assertEquals("123", classUnderTest.getKey());
		assertSame(ProductConfigMessageSeverity.INFO, classUnderTest.getSeverity());
		assertSame(ProductConfigMessageSource.ENGINE, classUnderTest.getSource());
	}

	@Test
	public void testDataNotModifiedExtended()
	{
		Date endDate = new Date();
		classUnderTest = builder.appendPromotionFields(null, "test extended message", endDate).build();

		assertNotNull(classUnderTest);
		assertEquals("test message", classUnderTest.getMessage());
		assertEquals("123", classUnderTest.getKey());
		assertSame(ProductConfigMessageSeverity.INFO, classUnderTest.getSeverity());
		assertSame(ProductConfigMessageSource.ENGINE, classUnderTest.getSource());
		assertEquals("test extended message", classUnderTest.getExtendedMessage());
		assertEquals(endDate, classUnderTest.getEndDate());
	}

	@Test
	public void testDataNotModifiedExtendedPromo()
	{
		Date endDate = new Date();
		classUnderTest = builder.appendPromotionFields(ProductConfigMessagePromoType.PROMO_OPPORTUNITY, "test extended message", endDate).build();

		assertNotNull(classUnderTest);
		assertEquals("test message", classUnderTest.getMessage());
		assertEquals("123", classUnderTest.getKey());
		assertSame(ProductConfigMessageSeverity.INFO, classUnderTest.getSeverity());
		assertSame(ProductConfigMessageSource.ENGINE, classUnderTest.getSource());
		assertSame(ProductConfigMessagePromoType.PROMO_OPPORTUNITY, classUnderTest.getPromoType());
		assertEquals("test extended message", classUnderTest.getExtendedMessage());
		assertEquals(endDate, classUnderTest.getEndDate());
	}

	@Test
	public void testEqualsSameData()
	{
		final ProductConfigMessage messageWithSameData = builder.build();

		assertTrue(classUnderTest.equals(classUnderTest));
		assertTrue(classUnderTest.equals(messageWithSameData));
		assertEquals(classUnderTest.hashCode(), messageWithSameData.hashCode());
	}

	@Test
	public void testEqualsSameKey()
	{
		final ProductConfigMessage messageWithSameKey = builder.appendSeverity(ProductConfigMessageSeverity.WARNING).build();

		assertTrue(classUnderTest.equals(messageWithSameKey));
		assertEquals(classUnderTest.hashCode(), messageWithSameKey.hashCode());
	}

	@Test
	public void testEqualsOtherKey()
	{
		final ProductConfigMessage messageWithDifferentKey = builder.appendKey("456").build();
		assertFalse(classUnderTest.equals(messageWithDifferentKey));

	}

	@Test
	public void testEqualsOtherSource()
	{
		if (ProductConfigMessageSource.values().length > 1)
		{
			builder.appendSource(ProductConfigMessageSource.valueOf("RULE"));
			final ProductConfigMessage messageWithDifferentSource = builder.build();
			assertFalse(classUnderTest.equals(messageWithDifferentSource));
		}
	}

	@Test
	public void testEqualsOtherObjects()
	{
		assertFalse(classUnderTest.equals(null));
		assertFalse(classUnderTest.equals(new String("Hello World")));
	}

	@Test
	public void testEqualsNullKey()
	{
		final ProductConfigMessage messageWithNullKey = builder.appendKey(null).build();
		assertFalse(classUnderTest.equals(messageWithNullKey));


		final ProductConfigMessage anotherMessageWithNullKey = appendDefaults(builder.reset()).appendKey(null).build();
		assertTrue(messageWithNullKey.equals(anotherMessageWithNullKey));
	}


}

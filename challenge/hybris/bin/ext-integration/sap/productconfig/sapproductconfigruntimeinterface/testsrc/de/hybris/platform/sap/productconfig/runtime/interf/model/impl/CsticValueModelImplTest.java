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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSeverity;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSource;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSourceSubType;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("javadoc")
@UnitTest
public class CsticValueModelImplTest
{
	private static final String csticName = "Name";
	private static final String csticNameAnother = "AnotherName";
	private static final String csticNameNumeric = "2.0";
	private static final String csticNameNumericDifferentFormat = "2.00";
	private final CsticValueModelImpl classUnderTest = new CsticValueModelImpl();
	private final CsticValueModelImpl other = new CsticValueModelImpl();

	protected CsticValueModelImpl createNumericValue()
	{
		final CsticValueModelImpl csticNumericValueModelImpl = new CsticValueModelImpl();
		csticNumericValueModelImpl.setNumeric(true);
		return csticNumericValueModelImpl;
	}

	@Before
	public void initialze()
	{
		other.setNumeric(false);
		classUnderTest.setName(csticName);
	}

	@Test
	public void testEquals_smallWith0Fraction()
	{
		final CsticValueModel value1 = createNumericValue();
		value1.setName("1");
		final CsticValueModel value2 = createNumericValue();
		value2.setName("1.0");
		assertTrue(value1.equals(value2));
	}

	@Test
	public void testEquals_bigWith0Fraction()
	{
		final CsticValueModel value1 = createNumericValue();
		value1.setName("12345678.0");
		final CsticValueModel value2 = createNumericValue();
		value2.setName("12345678");
		assertTrue(value1.equals(value2));
	}

	@Test
	public void testEquals_bigWith00Fraction()
	{
		final CsticValueModel value1 = createNumericValue();
		value1.setName("12345678.00");
		final CsticValueModel value2 = createNumericValue();
		value2.setName("12345678");
		assertTrue(value1.equals(value2));
	}

	@Test
	public void testEquals_bigWithFractionAndExponential()
	{
		final CsticValueModel value1 = createNumericValue();
		value1.setName("9999999999.99999");
		final CsticValueModel value2 = createNumericValue();
		value2.setName("9.99999999999999E9");
		assertTrue(value1.equals(value2));
	}

	@Test
	public void testEquals_bigNegativeWithFractionAndExponential()
	{
		final CsticValueModel value1 = createNumericValue();
		value1.setName("-9999999999.99999");
		final CsticValueModel value2 = createNumericValue();
		value2.setName("-9.99999999999999E9");
		assertTrue(value1.equals(value2));
	}


	@Test
	public void testEquals_bigWithExponential()
	{
		final CsticValueModel value1 = createNumericValue();
		value1.setName("12345678");
		final CsticValueModel value2 = createNumericValue();
		value2.setName("1.2345678E7");
		assertTrue(value1.equals(value2));
	}

	@Test
	public void testEquals_bigNegativeWithExponential()
	{
		final CsticValueModel value1 = createNumericValue();
		value1.setName("-12343678");
		final CsticValueModel value2 = createNumericValue();
		value2.setName("-1.2343678E7");
		assertTrue(value1.equals(value2));
	}

	@Test
	public void testNotEquals()
	{
		final CsticValueModel value1 = createNumericValue();
		value1.setName("123a43678");
		final CsticValueModel value2 = createNumericValue();
		value2.setName("12343678");
		assertFalse(value1.equals(value2));
	}

	@Test
	public void testNotEqualsNoNumericCstic()
	{
		final CsticValueModel value1 = new CsticValueModelImpl();
		value1.setName("01");
		final CsticValueModel value2 = new CsticValueModelImpl();
		value2.setName("1");
		assertFalse(value1.equals(value2));
	}

	@Test
	public void testIsNumeric()
	{

		assertFalse(classUnderTest.isNumeric());
		classUnderTest.setNumeric(true);
		assertTrue(classUnderTest.isNumeric());
	}

	@Test
	public void testCompareName()
	{
		other.setName(csticName);
		assertTrue(classUnderTest.compareName(other));
	}

	@Test
	public void testCompareNameDifferentName()
	{
		other.setName(csticNameAnother);
		assertFalse(classUnderTest.compareName(other));
	}

	@Test
	public void testCompareNameNumericWithNonNumeric()
	{
		classUnderTest.setNumeric(true);
		classUnderTest.setName(csticNameNumeric);
		other.setName(csticNameAnother);
		assertFalse(classUnderTest.compareName(other));
	}

	@Test
	public void testCompareNameNumericSameValue()
	{
		classUnderTest.setNumeric(true);
		classUnderTest.setName(csticNameNumeric);
		other.setName(csticNameNumeric);
		assertTrue(classUnderTest.compareName(other));
	}

	@Test
	public void testCompareNameNumericSameValueDifferentFormats()
	{
		classUnderTest.setNumeric(true);
		classUnderTest.setName(csticNameNumeric);
		other.setName(csticNameNumericDifferentFormat);
		assertTrue(classUnderTest.compareName(other));
	}

	@Test
	public void testAddMessagetoList()
	{
		final ProductConfigMessageBuilder builder = new ProductConfigMessageBuilder();
		builder.appendBasicFields("message", "key", ProductConfigMessageSeverity.INFO);
		builder.appendSourceAndType(ProductConfigMessageSource.ENGINE, ProductConfigMessageSourceSubType.DEFAULT);

		classUnderTest.getMessages().add(builder.build());
	}

	@Test
	public void testGetMessageListNotNull()
	{
		assertNotNull(classUnderTest.getMessages());
	}

	@Test
	public void testSetGetMessageList()
	{
		final Set<ProductConfigMessage> messages = new HashSet();
		classUnderTest.setMessages(messages);
		assertEquals(messages, classUnderTest.getMessages());
	}

	@Test
	public void testGetSetLongText()
	{
		classUnderTest.setLongText("test123");
		assertEquals("test123", classUnderTest.getLongText());
	}
}

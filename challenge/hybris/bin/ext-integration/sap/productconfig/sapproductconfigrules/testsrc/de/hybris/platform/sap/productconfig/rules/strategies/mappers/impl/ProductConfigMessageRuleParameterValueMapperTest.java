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
package de.hybris.platform.sap.productconfig.rules.strategies.mappers.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class ProductConfigMessageRuleParameterValueMapperTest
{
	private static final String ENGLISH_TEXT = "english \" text";
	private static final String ENGLISH_TEXT_SER = new StringBuilder().append("english ")
			.append(ProductConfigMessageRuleParameterValueMapper.TEXT_QUOTATION).append(" text").toString();
	private static final String GERMAN_TEXT = "german text";

	private ProductConfigMessageRuleParameterValueMapper classUnderTest;

	@Before
	public void setUp()
	{
		classUnderTest = new ProductConfigMessageRuleParameterValueMapper();
	}

	private String getSerializedMessage(final String germanTextSerialized, final String englishTextSerialized)
	{
		final String germanMessageSerialized = new StringBuilder()
				.append(ProductConfigMessageRuleParameterValueMapper.SERIALIZATION_QUOTATION).append("de")
				.append(ProductConfigMessageRuleParameterValueMapper.SERIALIZATION_QUOTATION).append(":")
				.append(ProductConfigMessageRuleParameterValueMapper.SERIALIZATION_QUOTATION).append(germanTextSerialized)
				.append(ProductConfigMessageRuleParameterValueMapper.SERIALIZATION_QUOTATION).toString();
		final String englishMessageSerialized = new StringBuilder()
				.append(ProductConfigMessageRuleParameterValueMapper.SERIALIZATION_QUOTATION).append("en")
				.append(ProductConfigMessageRuleParameterValueMapper.SERIALIZATION_QUOTATION).append(":")
				.append(ProductConfigMessageRuleParameterValueMapper.SERIALIZATION_QUOTATION).append(englishTextSerialized)
				.append(ProductConfigMessageRuleParameterValueMapper.SERIALIZATION_QUOTATION).toString();
		return new StringBuilder().append("{").append(germanMessageSerialized).append(",").append(englishMessageSerialized)
				.append("}").toString();
	}

	@Test
	public void testToString()
	{
		//remove this workaround when issue in the rule engine is solved
		//expected serialized string "{\"de\":\"german text\",\"en\":\"english text\"}";

		final Map<Locale, String> messageMap = new HashMap<>();
		messageMap.put(Locale.ENGLISH, ENGLISH_TEXT);
		messageMap.put(Locale.GERMAN, GERMAN_TEXT);

		final String message = classUnderTest.toString(messageMap);
		assertEquals(getSerializedMessage(GERMAN_TEXT, ENGLISH_TEXT_SER), message);
	}

	@Test
	public void testToAndFromStringNullMessages()
	{
		//remove this workaround when issue in the rule engine is solved
		//expected serialized string "{\"de\":\"german text\",\"en\":\"english text\"}";

		final Map<Locale, String> messageMap = new HashMap<>();
		messageMap.put(Locale.ENGLISH, null);
		messageMap.put(Locale.GERMAN, null);

		final String message = classUnderTest.toString(messageMap);
		assertEquals("{}", message);

		final Map<Locale, String> resultMap = classUnderTest.fromString(message);
		assertNotNull(resultMap);
		assertTrue(resultMap.isEmpty());
	}

	@Test
	public void testToAndFromStringBackslash()
	{
		final Map<Locale, String> messageMap = new HashMap<>();
		final String backslashText = "\\";
		messageMap.put(Locale.GERMAN, backslashText);

		final String message = classUnderTest.toString(messageMap);
		assertEquals(new StringBuilder().append("{").append(ProductConfigMessageRuleParameterValueMapper.SERIALIZATION_QUOTATION)
				.append("de").append(ProductConfigMessageRuleParameterValueMapper.SERIALIZATION_QUOTATION).append(":")
				.append(ProductConfigMessageRuleParameterValueMapper.SERIALIZATION_QUOTATION).append("\\\\\\\\")
				.append(ProductConfigMessageRuleParameterValueMapper.SERIALIZATION_QUOTATION).append("}").toString(), message);

		final Map<Locale, String> resultMap = classUnderTest.fromString(message);
		assertNotNull(resultMap);
		assertEquals(1, resultMap.size());
		final String doubleBackslashText = "\\\\";
		assertEquals(doubleBackslashText, resultMap.get(Locale.GERMAN));

	}

	@Test(expected = IllegalArgumentException.class)
	public void testToStringWrongMessageMap()
	{
		final Map<Locale, String> messageMap = new HashMap<>();
		messageMap.put(null, ENGLISH_TEXT);
		final String message = classUnderTest.toString(messageMap);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testToStringNullMessageMap()
	{
		final Map<Locale, String> messageMap = null;
		final String message = classUnderTest.toString(messageMap);
	}

	@Test
	public void testFromString()
	{
		final Map<Locale, String> messageMap = classUnderTest.fromString(getSerializedMessage(GERMAN_TEXT, ENGLISH_TEXT_SER));
		assertNotNull(messageMap);
		assertEquals(2, messageMap.size());
		assertEquals(GERMAN_TEXT, messageMap.get(Locale.GERMAN));
		assertEquals(ENGLISH_TEXT, messageMap.get(Locale.ENGLISH));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFromStringWrongMessageText()
	{
		final String message = "xyz";
		final Map<Locale, String> messageMap = classUnderTest.fromString(message);
	}

	@Test
	public void testFromStringNull()
	{
		final Map<Locale, String> result = classUnderTest.fromString(null);
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}
}

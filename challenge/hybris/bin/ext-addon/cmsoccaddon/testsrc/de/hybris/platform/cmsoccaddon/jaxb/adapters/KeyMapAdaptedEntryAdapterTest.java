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
package de.hybris.platform.cmsoccaddon.jaxb.adapters;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsoccaddon.jaxb.adapters.KeyMapAdaptedEntryAdapter.KeyMapAdaptedEntry;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;


/**
 * JUnit Tests for the KeyMapAdaptedEntryAdapter
 */
@UnitTest
public class KeyMapAdaptedEntryAdapterTest
{
	private static final String TEST_KEY = "TestKey";
	private static final String TEST_STRING = "TestString";
	private static final String TEST_KEY_MAP = "TestKeyMapAdaptedEntry";


	private final KeyMapAdaptedEntryAdapter keyMapAdaptedEntryAdapter = new KeyMapAdaptedEntryAdapter();
	private KeyMapAdaptedEntry keyMapAdaptedEntry;

	@Before
	public void setup()
	{
		keyMapAdaptedEntry = new KeyMapAdaptedEntry();
	}

	@Test
	public void shouldMarshalStringValue()
	{
		keyMapAdaptedEntry.key = TEST_KEY;
		keyMapAdaptedEntry.strValue = TEST_STRING;

		final Element result = keyMapAdaptedEntryAdapter.marshal(keyMapAdaptedEntry);

		assertThat(result.getTextContent(), equalTo(TEST_STRING));
	}

	@Test
	public void shouldMarshalMapValue()
	{
		final List<KeyMapAdaptedEntry> testMap = new ArrayList<KeyMapAdaptedEntry>();
		final KeyMapAdaptedEntry testkeyMapAdaptedEntry = new KeyMapAdaptedEntry();
		testkeyMapAdaptedEntry.key = TEST_KEY_MAP;
		testkeyMapAdaptedEntry.strValue = TEST_STRING;

		testMap.add(testkeyMapAdaptedEntry);

		keyMapAdaptedEntry.key = TEST_KEY;
		keyMapAdaptedEntry.mapValue = testMap;

		final Element result = keyMapAdaptedEntryAdapter.marshal(keyMapAdaptedEntry);
		assertThat(result.getTextContent(), equalTo(TEST_STRING));
	}

	@Test
	public void shouldMarshalMapValue_MapIsEmpty()
	{
		final List<KeyMapAdaptedEntry> testMap = new ArrayList<KeyMapAdaptedEntry>();

		keyMapAdaptedEntry.key = TEST_KEY;
		keyMapAdaptedEntry.mapValue = testMap;

		final Element result = keyMapAdaptedEntryAdapter.marshal(keyMapAdaptedEntry);
		assertThat(result.getTextContent(), equalTo(""));
	}

	@Test
	public void shouldMarshalCollectionValue()
	{
		final List<String> testCollection = new ArrayList<String>();
		final String testString = TEST_STRING;

		testCollection.add(testString);

		keyMapAdaptedEntry.key = TEST_KEY;
		keyMapAdaptedEntry.arrayValue = testCollection;

		final Element result = keyMapAdaptedEntryAdapter.marshal(keyMapAdaptedEntry);

		assertThat(result.getTextContent(), equalTo(TEST_STRING));
	}

	@Test
	public void shouldMarshalCollectionValue_CollectionIsEmpty()
	{
		final List<String> testCollection = new ArrayList<String>();

		keyMapAdaptedEntry.key = TEST_KEY;
		keyMapAdaptedEntry.arrayValue = testCollection;

		final Element result = keyMapAdaptedEntryAdapter.marshal(keyMapAdaptedEntry);

		assertThat(result, equalTo(null));
	}

	@Test
	public void shouldNotMarshalNullValue()
	{
		keyMapAdaptedEntry.key = TEST_KEY;

		final Object result = keyMapAdaptedEntryAdapter.marshal(keyMapAdaptedEntry);

		assertThat(result, equalTo(null));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldThrowUnsupportedOperationException() throws Exception
	{
		keyMapAdaptedEntryAdapter.unmarshal(null);
	}
}
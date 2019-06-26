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
package de.hybris.platform.cmsfacades.uniqueidentifier;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.junit.Test;

@UnitTest
public class EncodedItemComposedKeyTest
{

	private static final String CATALOG_VERSION = "CATALOG_VERSION";
	private static final String CATALOG_ID = "CATALOG_ID";
	private static final String ITEM_ID = "ITEM_ID";
	private static final String JSON_STRING = 
			"{\"itemId\":\"" + ITEM_ID 
					+ "\",\"catalogId\":\"" + CATALOG_ID 
					+ "\",\"catalogVersion\":\"" + CATALOG_VERSION + "\"}";
	private static final String ENCODED_STRING = "eyJpdGVtSWQiOiJJVEVNX0lEIiwiY2F0YWxvZ0lkIjoiQ0FUQUxPR19JRCIsImNhdGFsb2dWZXJzaW9uIjoiQ0FUQUxPR19WRVJTSU9OIn0=";
	private static final String INVALID_ENCODED_STRING = "ew0KCSJpbnZhbGlkS2V5IjogInNvbWUgaW52YWxpZCBrZXkiLCANCgkiY2F0YWxvZ0lkIjogInNvbWUgY2F0YWxvZyBpZCIsIA0KCSJjYXRhbG9nVmVyc2lvbiI6ICJzb21lIGNhdGFsb2cgdmVyc2lvbiINCn0=";
	private static final String INVALID_UTF_ENCODED_STRING = "My4xLjkgIFNlcXVlbmNlIG9mIGFsbCA2NCBwb3NzaWJsZSBjb250aW51YXRpb24gYnl0ZXMgKDB4ODAtMHhiZik6ICAgICAgICAgICAgfA0KICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgfA0KICAgIu";

	@Test
	public void testConversionFromObjectToJsonString()
	{
		EncodedItemComposedKey itemComposedKey =  new EncodedItemComposedKey();
		itemComposedKey.setCatalogVersion(CATALOG_VERSION);
		itemComposedKey.setCatalogId(CATALOG_ID);
		itemComposedKey.setItemId(ITEM_ID);
		
		final String jsonString = itemComposedKey.toJsonString();
		assertThat(jsonString, is(JSON_STRING));
	}

	@Test
	public void testConversionFromJsonStringToObject()
	{
		final ItemComposedKey itemComposedKey = 
				new EncodedItemComposedKey.Builder(JSON_STRING).build();
		assertThat(itemComposedKey, notNullValue());
		assertThat(itemComposedKey.getItemId(), is(ITEM_ID));
		assertThat(itemComposedKey.getCatalogId(), is(CATALOG_ID));
		assertThat(itemComposedKey.getCatalogVersion(), is(CATALOG_VERSION));
	}
	
	@Test
	public void testConversionFromAndToObjectShouldBeSuccessful()
	{
		EncodedItemComposedKey itemComposedKey =  new EncodedItemComposedKey();
		itemComposedKey.setCatalogVersion(CATALOG_VERSION);
		itemComposedKey.setCatalogId(CATALOG_ID);
		itemComposedKey.setItemId(ITEM_ID);
		
		final String hexString = itemComposedKey.toEncoded();
		
		final EncodedItemComposedKey itemComposedKey2 = 
				new EncodedItemComposedKey.Builder(hexString).encoded().build();
		assertThat(itemComposedKey2, notNullValue());
		assertThat(itemComposedKey2.getItemId(), is(ITEM_ID));
		assertThat(itemComposedKey2.getCatalogId(), is(CATALOG_ID));
		assertThat(itemComposedKey2.getCatalogVersion(), is(CATALOG_VERSION));
		
	}


	@Test
	public void testConversionFromObjectToEncodedString()
	{
		EncodedItemComposedKey itemComposedKey =  new EncodedItemComposedKey();
		itemComposedKey.setCatalogVersion(CATALOG_VERSION);
		itemComposedKey.setCatalogId(CATALOG_ID);
		itemComposedKey.setItemId(ITEM_ID);
		
		final String hexString = itemComposedKey.toEncoded();
		System.out.println(hexString);
		assertThat(hexString, is(ENCODED_STRING));
	}

	@Test
	public void testConversionFromEncodedToObject()
	{
		final EncodedItemComposedKey itemComposedKey = new EncodedItemComposedKey
				.Builder(ENCODED_STRING).encoded().build();
		assertThat(itemComposedKey, notNullValue());
		assertThat(itemComposedKey.getItemId(), is(ITEM_ID));
		assertThat(itemComposedKey.getCatalogId(), is(CATALOG_ID));
		assertThat(itemComposedKey.getCatalogVersion(), is(CATALOG_VERSION));
	}

	@Test(expected = ConversionException.class)
	public void givenIllegalUTFString_TestConversionFromEncodedToObject_ShouldReturnConversionException()
	{
		// Act
		new EncodedItemComposedKey.Builder(INVALID_UTF_ENCODED_STRING).encoded().build();
	}

	@Test(expected = ConversionException.class)
	public void givenInvalidJSON_TestConversionFromEncodedToObject_ShouldReturnConversionException()
	{
		// Act
		new EncodedItemComposedKey.Builder(INVALID_ENCODED_STRING).encoded().build();
	}
}

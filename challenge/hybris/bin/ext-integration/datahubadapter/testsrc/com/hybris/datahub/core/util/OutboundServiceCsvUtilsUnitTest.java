/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package com.hybris.datahub.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;

import com.hybris.datahub.core.data.TestProductData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.apache.commons.lang.time.DateFormatUtils;
import org.junit.Before;
import org.junit.Test;

@UnitTest
public class OutboundServiceCsvUtilsUnitTest
{
	private final OutboundServiceCsvUtils csvUtils = new OutboundServiceCsvUtils();

	private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.S";

	private TestProductData testProduct;
	private Map<String, Object> testProductMap;

	@Before
	public void setup()
	{
		testProduct = OutboundServiceDataGenerationTestUtils.createTestProductData();
		testProductMap = OutboundServiceDataGenerationTestUtils.createUniqueTestProductMap();

		csvUtils.setDatePattern(DEFAULT_DATE_FORMAT);
	}

	@Test
	public void testGetStringValueOfObjectShouldFormatDates()
	{
		final Date date = new Date();
		final String formattedDate = csvUtils.toCsvValue(date);

		assertThat(formattedDate).isEqualTo(DateFormatUtils.formatUTC(date, DEFAULT_DATE_FORMAT));
	}

	@Test
	public void testGetStringValueOfObjectStringValueOfUtcDateCanBeConvertedBackToOriginalDate() throws ParseException
	{
		final Date originalDate = new Date();
		final String formattedDate = csvUtils.toCsvValue(originalDate);

		final SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		final Date convertedDate = formatter.parse(formattedDate);

		assertThat(originalDate.getTime()).isEqualTo(convertedDate.getTime());
		assertThat(originalDate.compareTo(convertedDate)).isEqualTo(0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetStringValueOfObjectWithInvalidDateFormat()
	{
		csvUtils.setDatePattern("INVALID_DATE_FORMAT");
		final Date date = new Date();
		csvUtils.toCsvValue(date);
	}

	@Test
	public void testConvertMapToCsvShouldIgnoreCollectionEntry()
	{
		final Map<String, Object> objMap = new HashMap<>();
		objMap.put("testProperty", "testValue");
		objMap.put("collectionProperty", Arrays.asList("value 1", "value 2"));

		final String[] csvArray = csvUtils.convertMapToCsv(objMap);

		assertThat(csvArray).hasSize(2)
							.containsExactly("testProperty", "testValue");
	}

	@Test
	public void testConvertMapToCsv()
	{
		final String[] objArray = csvUtils.convertMapToCsv(testProductMap);

		assertThat(objArray).hasSize(2);
		assertThat(objArray[0]).isNotEmpty();

		final Set<String> actualHeaders = new HashSet<>(Arrays.asList(objArray[0].split(",")));
		final Set<String> expectedHeaders = new HashSet<>(Arrays.asList("unit", "style", "SKU", "integrationKey", "baseName", "isoCode", "size"));
		assertThat(actualHeaders).isEqualTo(expectedHeaders);

		assertThat(objArray[1]).isNotEmpty();
	}

	@Test
	public void testConvertMapToCsvShouldHandleNullValues()
	{
		testProductMap.put("propertyWithNullValue", null);
		final String[] objArray = csvUtils.convertMapToCsv(testProductMap);

		assertThat(objArray).hasSize(2);
		assertThat(objArray[0]).contains("propertyWithNullValue");
		assertThat(objArray[1]).containsPattern(Pattern.compile(",,|^,.*"));
	}

	@Test
	public void testConvertListToCsvWithMultipleMapsShouldProperlyHandleNullValues()
	{
		final Map<String, Object> mapWithoutNullValue = new HashMap<>();
		mapWithoutNullValue.put("propertyWhichSometimesContainsNull", "non-null-value");
		mapWithoutNullValue.put("anotherProperty", "anotherValue1");

		final Map<String, Object> mapWithNullValue = new HashMap<>();
		mapWithNullValue.put("propertyWhichSometimesContainsNull", null);
		mapWithNullValue.put("anotherProperty", "anotherValue2");

		final List<Map<String, Object>> objList = Arrays.asList(mapWithNullValue, mapWithoutNullValue);
		final String[] csvArray = csvUtils.convertListToCsv(objList);

		assertThat(csvArray).hasSize(3)
							.contains("propertyWhichSometimesContainsNull,anotherProperty", ",anotherValue2", "non-null-value,anotherValue1");
	}

	@Test
	public void testConvertObjectToMap()
	{
		final Map<String, Object> objMap = csvUtils.convertObjectToMap(testProduct);

		assertThat(objMap).hasSize(7);
		assertThat(objMap.get("baseName")).isEqualTo(testProduct.getBaseName());
	}

	@Test
	public void testConvertObjectToMapShouldIgnoreCollectionProperty()
	{
		final ObjectWithCollection obj = new ObjectWithCollection();
		obj.setTestList(Arrays.asList("value 1", "value 2"));

		final Map<String, Object> objMap = csvUtils.convertObjectToMap(obj);

		assertThat(objMap).isEmpty();
	}

	@Test
	public void testConvertListToCsv()
	{
		final List<Map<String, Object>> objList = Collections.singletonList(testProductMap);
		final String[] csvArray = csvUtils.convertListToCsv(objList);

		assertThat(csvArray).hasSize(2);
	}

	@Test
	public void testConvertObjectToEscapedCsv()
	{
		testProduct.setBaseName("test\"test,test\"\nLine2");
		final String[] csvArray = csvUtils.convertObjectToCsv(testProduct);

		assertThat(csvArray).hasSize(2);
		assertThat(csvArray[1]).contains("\"test\"\"test,test\"\"\nLine2\"");
	}

	@Test
	public void testConvertMapToEscapedCsv()
	{
		testProductMap.put("baseName", "test\"test,test\"\nLine2");
		final String[] csvArray = csvUtils.convertMapToCsv(testProductMap);

		assertThat(csvArray).hasSize(2);
		assertThat(csvArray[1]).contains("\"test\"\"test,test\"\"\nLine2\"");
	}

	@Test
	public void testConvertListToCsvWithMultipleMaps()
	{
		final List<Map<String, Object>> objList = Arrays.asList(testProductMap,
				OutboundServiceDataGenerationTestUtils.createUniqueTestProductMap());
		final String[] csvArray = csvUtils.convertListToCsv(objList);

		assertThat(csvArray).hasSize(3);
	}

	@Test
	public void testTransmissionSafeMapWithObjects()
	{
		final Map<String, Object> original = new HashMap<>(1);
		original.put("attr1", "a string");
		original.put("attr2", 1);

		final Map<String, Object> safe = csvUtils.transmissionSafe(original);
		assertThat(safe).isNotSameAs(original);
		assertThat(safe.get("attr1")).isEqualTo("a string");
		assertThat(safe.get("attr2")).isEqualTo("1");
	}

	@Test
	public void testTransmissionSafeMapWithDate()
	{
		final Date date = new Date();
		final Map<String, Object> original = new HashMap<>(1);
		original.put("date", date);

		final Map<String, Object> safe = csvUtils.transmissionSafe(original);
		assertThat(safe.get("date")).isEqualTo(DateFormatUtils.formatUTC(date, DEFAULT_DATE_FORMAT));
	}

	@Test
	public void testTransmissionSafeMapWithNull()
	{
		final Map<String, Object> original = new HashMap<>(1);
		original.put("attr", null);

		final Map<String, Object> safe = csvUtils.transmissionSafe(original);
		assertThat(safe).isEmpty();
	}

	private static class ObjectWithCollection
	{
		private Collection<String> testList;

		public void setTestList(final Collection<String> testList)
		{
			this.testList = testList;
		}
	}
}

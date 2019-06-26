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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.data.AbstractCMSComponentData;
import de.hybris.platform.cmsoccaddon.data.ComponentWsDTO;
import de.hybris.platform.cmsoccaddon.data.NavigationNodeWsDTO;
import de.hybris.platform.cmsoccaddon.jaxb.adapters.ComponentAdapterUtil.ComponentAdaptedData;
import de.hybris.platform.cmsoccaddon.jaxb.adapters.KeyMapAdaptedEntryAdapter.KeyMapAdaptedEntry;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.Test;



/**
 * JUnit Tests for the ComponentAdapterUtil
 */
@UnitTest
public class ComponentAdapterUtilTest
{
	private static final String TEST_UID = "testUid";
	private static final String TEST_NAME = "testName";
	private static final String TEST_TYPECODE = "testTypeCode";
	private static final String TEST_STRING = "testString";
	private static final String TEST_KEY = "testKey";
	private static final String TEST_OBJECT_KEY_1 = "keyObject1";
	private static final String TEST_OBJECT = "Object";
	private static final Date TEST_DATE = mock(Date.class);
	private static final Map<String, Object> TEST_MAP = new HashMap<String, Object>();
	private static final Map<String, Object> TEST_OBJECT_MAP_1 = new HashMap<String, Object>();

	@Test
	public void shouldConvertComponentWsDTOToComponentAdaptedData()
	{
		final ComponentWsDTO testComponentDTO = new ComponentWsDTO();

		final Map<String, Object> testOtherProperties = new HashMap<String, Object>();
		final String testOtherPropertiesString1 = "testOtherPropertiesString1";
		final NavigationNodeWsDTO testOtherPropertiesObject1 = new NavigationNodeWsDTO();
		testOtherPropertiesObject1.setUid("test navigation node uid");

		final String testOtherPropertiesString2 = "testOtherPropertiesString2";
		final String testOtherPropertiesObject2 = "testOtherPropertiesObject2";

		testOtherProperties.put(testOtherPropertiesString1, testOtherPropertiesObject1);
		testOtherProperties.put(testOtherPropertiesString2, testOtherPropertiesObject2);

		testComponentDTO.setUid(TEST_UID);
		testComponentDTO.setName(TEST_NAME);
		testComponentDTO.setTypeCode(TEST_TYPECODE);
		testComponentDTO.setModifiedtime(TEST_DATE);
		testComponentDTO.setOtherProperties(testOtherProperties);

		final ComponentAdaptedData componentAdaptedDataResult = ComponentAdapterUtil.convert(testComponentDTO);

		assertThat(componentAdaptedDataResult, is(notNullValue()));
		assertThat(componentAdaptedDataResult.uid, equalTo(TEST_UID));
		assertThat(componentAdaptedDataResult.name, equalTo(TEST_NAME));
		assertThat(componentAdaptedDataResult.typeCode, equalTo(TEST_TYPECODE));
		assertThat(componentAdaptedDataResult.modifiedTime, equalTo(TEST_DATE));
		assertThat(componentAdaptedDataResult.navigationNode.getUid(), equalTo(testOtherPropertiesObject1.getUid()));
		assertThat(componentAdaptedDataResult.entries.get(0).strValue, equalTo(testOtherPropertiesObject2));
	}

	@Test
	public void shouldConvertComponentWsDTOWithoutOtherPropertiesToComponentAdaptedData()
	{
		final ComponentWsDTO testComponentDTO = new ComponentWsDTO();
		testComponentDTO.setUid(TEST_UID);
		testComponentDTO.setName(TEST_NAME);
		testComponentDTO.setTypeCode(TEST_TYPECODE);
		testComponentDTO.setModifiedtime(TEST_DATE);

		final ComponentAdaptedData componentAdaptedDataResult = ComponentAdapterUtil.convert(testComponentDTO);

		assertThat(componentAdaptedDataResult, is(notNullValue()));
		assertThat(componentAdaptedDataResult.uid, equalTo(TEST_UID));
		assertThat(componentAdaptedDataResult.name, equalTo(TEST_NAME));
		assertThat(componentAdaptedDataResult.typeCode, equalTo(TEST_TYPECODE));
		assertThat(componentAdaptedDataResult.modifiedTime, equalTo(TEST_DATE));
		assertThat(componentAdaptedDataResult.navigationNode, equalTo(null));
		assertThat(componentAdaptedDataResult.entries, Matchers.<KeyMapAdaptedEntry> empty());
	}


	@Test
	public void shouldNotMarshalNavigationNodeMap()
	{
		final Map<String, Object> testMap = new HashMap<String, Object>();
		final NavigationNodeWsDTO navigationNodeTest = new NavigationNodeWsDTO();
		testMap.put(TEST_STRING, navigationNodeTest);

		final List<KeyMapAdaptedEntry> resultList = ComponentAdapterUtil.marshalMap(testMap);

		assertThat(Boolean.valueOf(resultList.isEmpty()), equalTo(Boolean.TRUE));
	}

	@Test
	public void shouldMarshalMapToListOfKeyMapAdaptedEntry()
	{
		TEST_OBJECT_MAP_1.put(TEST_KEY, TEST_STRING);

		TEST_MAP.put(TEST_OBJECT_KEY_1, TEST_OBJECT_MAP_1);
		final List<KeyMapAdaptedEntry> resultList = ComponentAdapterUtil.marshalMap(TEST_MAP);

		assertThat(Boolean.valueOf(resultList.isEmpty()), equalTo(Boolean.FALSE));
		assertThat(resultList.get(0).mapValue.get(0).strValue, equalTo(TEST_STRING));
	}

	@Test
	public void shouldMarshalPrimitiveToListOfKeyMapAdaptedEntry()
	{
		TEST_MAP.put(TEST_OBJECT_KEY_1, TEST_STRING);

		final List<KeyMapAdaptedEntry> resultList = ComponentAdapterUtil.marshalMap(TEST_MAP);

		assertThat(resultList.get(0).strValue, equalTo(TEST_STRING));
	}

	@Test
	public void shouldMarshalCollectionOfStringToListOfKeyMapAdaptedEntry()
	{
		final List<Object> testList = new ArrayList<>();
		testList.add(TEST_STRING);

		TEST_MAP.put(TEST_OBJECT_KEY_1, testList);

		final List<KeyMapAdaptedEntry> resultList = ComponentAdapterUtil.marshalMap(TEST_MAP);
		assertThat(resultList.get(0).arrayValue.get(0), equalTo(TEST_STRING));
	}

	@Test
	public void shouldMarshalDefaultCaseToListOfKeyMapAdaptedEntry()
	{
		final Map<String, Object> testMap = new HashMap<String, Object>();
		final AbstractCMSComponentData component1 = new AbstractCMSComponentData();
		component1.setUid(TEST_OBJECT);

		testMap.put(TEST_OBJECT_KEY_1, component1);

		final List<KeyMapAdaptedEntry> resultList = ComponentAdapterUtil.marshalMap(testMap);

		assertThat(resultList.get(0).mapValue.get(0).strValue, equalTo(TEST_OBJECT));
	}
}

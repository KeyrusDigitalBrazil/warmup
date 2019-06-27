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
package com.hybris.backoffice.cockpitng.json.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.hybris.backoffice.cockpitng.json.ConverterRegistry;
import com.hybris.cockpitng.core.util.impl.TypedSettingsMap;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.testing.util.BeanLookupFactory;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;


@UnitTest
public class ConverterModelDataMapperTest
{

	private static final int INT_BEFORE = 5;
	private static final int INT_AFTER = 8;

	private static final double DOUBLE_BEFORE = 3.0;
	private static final double DOUBLE_AFTER = 6.0;

	private static final String STRING_BEFORE = "before";
	private static final String STRING_AFTER = "after";

	private static final Serializable OBJECT_BEFORE = new TestDTOField();
	private static final Serializable OBJECT_AFTER = new TestDTOField();


	@Mock
	private WidgetInstanceManager widgetInstanceManager;

	@Mock
	private ConverterRegistry converterRegistry;

	private ConverterModelDataMapper mapper;

	private TypedSettingsMap settingsMap;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);

		mapper = new ConverterModelDataMapper();
		mapper.setConverterRegistry(converterRegistry);

		settingsMap = Mockito.spy(new TypedSettingsMap());
		Mockito.when(widgetInstanceManager.getWidgetSettings()).thenReturn(settingsMap);
	}

	@Test
	public void testMapValues() throws Exception
	{
		final TestDTO dto = new TestDTO();

		final Map<String, Object> values = new HashMap<>();
		values.put("intField", Integer.valueOf(INT_AFTER));
		values.put("doubleField", Double.valueOf(DOUBLE_AFTER));
		values.put("stringField", STRING_AFTER);
		values.put("writeonlyField", OBJECT_AFTER);
		values.put("readonlyField", OBJECT_AFTER);

		mapper.map(widgetInstanceManager, dto, values);

		Assert.assertEquals(INT_AFTER, dto.getIntField());
		Assert.assertEquals(null, DOUBLE_AFTER, dto.getDoubleField(), 0.0);
		Assert.assertEquals(STRING_AFTER, dto.getStringField());
		Assert.assertSame(OBJECT_BEFORE, dto.getReadonlyField());
		Assert.assertSame(OBJECT_AFTER, dto.writeonlyField);
	}

	@Test
	public void testConverterSetting() throws Exception
	{
		final TestDTO dto = new TestDTO();

		final Converter<Object, Object> converter = Mockito.mock(Converter.class);
		final String beanName = "someTestBean";
		settingsMap.put("converter." + TestDTO.class.getName(), beanName);
		CockpitTestUtil.mockBeanLookup(BeanLookupFactory.createBeanLookup().registerBean(beanName, converter));

		mapper.map(widgetInstanceManager, dto);

		Mockito.verify(converter, Mockito.atLeastOnce()).convert(Mockito.any());
	}

	@Test
	public void testClassSetting() throws Exception
	{
		final String className = ConverterModelDataMapperTest.class.getName();
		settingsMap.put("dto." + TestDTO.class.getName(), className);

		final Class<Object> sourceType = mapper.getSourceType(widgetInstanceManager, TestDTO.class);

		Assert.assertEquals(className, sourceType.getName());
	}

	public static class TestDTO
	{

		private final Object readonlyField = OBJECT_BEFORE;

		private int intField = INT_BEFORE;

		private double doubleField = DOUBLE_BEFORE;

		private String stringField = STRING_BEFORE;

		private Serializable writeonlyField = OBJECT_BEFORE;

		public int getIntField()
		{
			return intField;
		}

		public void setIntField(final int intField)
		{
			this.intField = intField;
		}

		public double getDoubleField()
		{
			return doubleField;
		}

		public void setDoubleField(final double doubleField)
		{
			this.doubleField = doubleField;
		}

		public String getStringField()
		{
			return stringField;
		}

		public void setStringField(final String stringField)
		{
			this.stringField = stringField;
		}

		public Object getReadonlyField()
		{
			return readonlyField;
		}

		public void setWriteonlyField(final Serializable writeonlyField)
		{
			this.writeonlyField = writeonlyField;
		}
	}

	protected static class TestDTOField implements Serializable
	{
		// class represents read-only and write-only fields
	}
}
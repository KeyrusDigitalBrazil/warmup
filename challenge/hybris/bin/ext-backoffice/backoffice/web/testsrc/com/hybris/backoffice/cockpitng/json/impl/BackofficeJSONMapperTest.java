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

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.servicelayer.model.AbstractItemModel;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.hybris.backoffice.cockpitng.json.ModelDataMapper;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.json.impl.DefaultObjectMapperConfiguration;


@UnitTest
public class BackofficeJSONMapperTest
{

	private static final String FIELD_VALUE_STRING = "stringValue";

	private static final int FIELD_VALUE_INT = 3;

	private static final double FIELD_VALUE_DOUBLE = 3.0d;

	private static final String JSON_STRING_FIELD_VALUE = "otherStringValue";

	private static final int JSON_INT_FIELD_VALUE = 7;

	private static final String JSON = String.format("{\"string\":\"%s\",\"integer\":%d,\"pk\":%d}", JSON_STRING_FIELD_VALUE,
			Integer.valueOf(JSON_INT_FIELD_VALUE), Long.valueOf(10l));

	private BackofficeJSONMapper backofficeJSONMapper;

	@Mock
	private WidgetInstanceManager widgetInstanceManager;

	@Mock
	private ModelDataMapper mapper;

	private PK pk;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		backofficeJSONMapper = new BackofficeJSONMapper();

		Mockito.when(mapper.map(Mockito.any(), Mockito.any(TestItem.class))).thenReturn(new TestDTO());
		Mockito.when(mapper.getSourceType(Mockito.any(), Mockito.any())).then(invoke -> {

			if (TestDTO.class.equals(invoke.getArguments()[1]))
			{
				return TestItem.class;
			}
			else if (TestItem.class.equals(invoke.getArguments()[1]))
			{
				return TestDTO.class;
			}
			else
			{
				return invoke.getArguments()[1];
			}

		});
		backofficeJSONMapper.setModelDataMapper(mapper);
		backofficeJSONMapper.setConfigurations(Arrays.asList(new DefaultObjectMapperConfiguration()));

		final ObjectFacade objectFacade = Mockito.mock(ObjectFacade.class);
		Mockito.when(objectFacade.load(Mockito.anyString())).thenReturn(new TestItem());
		backofficeJSONMapper.setObjectFacade(objectFacade);

		pk = PK.createFixedCounterPK(10, 12);
	}

	protected void checkJSON(final String json, final Object... reqs) throws IOException
	{
		final ObjectMapper mapper = new ObjectMapper();
		final Map map = mapper.readValue(json, Map.class);
		for (int i = 0; i < reqs.length;)
		{
			Assert.assertEquals(map.get(reqs[i++]), reqs[i++]);
		}
	}

	@Test
	public void testToJSONMapping() throws Exception
	{
		backofficeJSONMapper.toJSONString(widgetInstanceManager, new TestItem());
		Mockito.verify(mapper, Mockito.atLeastOnce()).map(Mockito.any(), Mockito.any(TestItem.class));
	}

	@Test
	public void testToJSONWithConverterAndNoPk() throws Exception
	{
		Mockito.when(mapper.map(Mockito.any(), Mockito.any(TestItem.class))).thenReturn(new TestDTO());


		final String json = backofficeJSONMapper.toJSONString(widgetInstanceManager, new TestItem());


		checkJSON(json, "string", FIELD_VALUE_STRING, "integer", Integer.valueOf(FIELD_VALUE_INT));
	}

	@Test
	public void testToJSONWithConverterAndPk() throws Exception
	{
		Mockito.when(mapper.map(Mockito.any(), Mockito.any(TestItem.class))).thenReturn(new TestDTO());


		final String json = backofficeJSONMapper.toJSONString(widgetInstanceManager, new TestItem(pk));


		checkJSON(json, "string", FIELD_VALUE_STRING, "integer", Integer.valueOf(FIELD_VALUE_INT));
	}

	@Test
	public void testToJSONWithoutConverterAndNoPk() throws Exception
	{
		Mockito.when(mapper.map(Mockito.any(), Mockito.any(TestItem.class))).thenReturn(new TestItem());


		final String json = backofficeJSONMapper.toJSONString(widgetInstanceManager, new TestItem());


		checkJSON(json, "stringField", FIELD_VALUE_STRING, "intField", Integer.valueOf(FIELD_VALUE_INT), "doubleField",
				Double.valueOf(FIELD_VALUE_DOUBLE));
	}

	@Test
	public void testToJSONWithoutConverterAndPk() throws Exception
	{
		Mockito.when(mapper.map(Mockito.any(), Mockito.any(TestItem.class))).thenReturn(new TestItem(pk));


		final String json = backofficeJSONMapper.toJSONString(widgetInstanceManager, new TestItem());


		checkJSON(json, "stringField", FIELD_VALUE_STRING, "intField", Integer.valueOf(FIELD_VALUE_INT), "doubleField",
				Double.valueOf(FIELD_VALUE_DOUBLE));
	}

	@Test
	public void testFromJSONWithConverter() throws Exception
	{
		Mockito.when(mapper.getSourceType(Mockito.any(), Mockito.any())).then(invoke -> TestDTO.class);
		Mockito.when(mapper.map(Mockito.any(), Mockito.any(TestDTO.class))).thenReturn(new TestItem());

		backofficeJSONMapper.fromJSONString(widgetInstanceManager, JSON, TestItem.class);
	}

	@Test
	public void testFromJSONWithoutConverter() throws Exception
	{
		Mockito.when(mapper.getSourceType(Mockito.any(), Mockito.any())).then(invoke -> null);

		backofficeJSONMapper.fromJSONString(widgetInstanceManager, JSON, TestItem.class);

		Mockito.verify(backofficeJSONMapper.getObjectFacade(), Mockito.atLeastOnce()).load(Mockito.anyString());
	}

	private static class TestItem extends AbstractItemModel
	{

		private final PK pk;

		private String stringField = FIELD_VALUE_STRING;

		private int intField = FIELD_VALUE_INT;

		private double doubleField = FIELD_VALUE_DOUBLE;

		public TestItem()
		{
			this.pk = null;
		}

		protected TestItem(final PK pk)
		{
			this.pk = pk;
		}

		@Override
		public PK getPk()
		{
			return pk != null ? pk : super.getPk();
		}

		public String getStringField()
		{
			return stringField;
		}

		public void setStringField(final String stringField)
		{
			this.stringField = stringField;
		}

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
	}

	private static class TestDTO implements Serializable
	{

		private String string = FIELD_VALUE_STRING;

		private int integer = FIELD_VALUE_INT;

		public String getString()
		{
			return string;
		}

		public void setString(final String string)
		{
			this.string = string;
		}

		public int getInteger()
		{
			return integer;
		}

		public void setInteger(final int integer)
		{
			this.integer = integer;
		}
	}
}

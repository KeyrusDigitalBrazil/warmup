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
package de.hybris.platform.addonsupport.config.javascript;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.storefront.data.JavaScriptVariableData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ModelMap;


@UnitTest
public class JavaScriptVariableDataFactoryTest
{
	private static final String VALUE3 = "value3";
	private static final String VALUE2 = "value2";
	private static final String TEST3 = "test3";
	private static final String TEST2 = "test2";
	private static final String VALUE1 = "value1";
	private static final String TEST1 = "test1";
	private static final String JSON_VARIABLE = "{\"test2\":\"value2\",\"test3\":\"value3\",\"test1\":\"value1\"}";
	private Map<String, String> variables;
	private ModelMap model;

	@Before
	public void setUp() throws Exception
	{
		variables = new HashMap<>();
		variables.put(TEST1, VALUE1);
		variables.put(TEST2, VALUE2);
		variables.put(TEST3, VALUE3);
		model = new ModelMap();
		model.put(TEST1, VALUE1);
		model.put(TEST2, VALUE2);
		model.put(TEST3, VALUE3);
	}

	@Test
	public void testCreate()
	{
		final JavaScriptVariableData result = JavaScriptVariableDataFactory.create(TEST1, VALUE1);
		Assertions.assertThat(result.getQualifier().equals(TEST1)).isTrue();
		Assertions.assertThat(result.getValue().equals(VALUE1)).isTrue();
	}

	@Test
	public void testCreateFromMap()
	{
		final List<JavaScriptVariableData> result = JavaScriptVariableDataFactory.createFromMap(variables);
		for (final JavaScriptVariableData entry : result)
		{
			switch (entry.getQualifier())
			{
				case TEST1:
					Assertions.assertThat(entry.getValue().equals(VALUE1)).isTrue();
					break;
				case TEST2:
					Assertions.assertThat(entry.getValue().equals(VALUE2)).isTrue();
					break;
				case TEST3:
					Assertions.assertThat(entry.getValue().equals(VALUE3)).isTrue();
					break;

				default:
					break;
			}
		}
	}

	@Test
	public void testGetVariables()
	{
		final List<JavaScriptVariableData> result = JavaScriptVariableDataFactory.getVariables(model);
		for (final JavaScriptVariableData entry : result)
		{
			switch (entry.getQualifier())
			{
				case TEST1:
					Assertions.assertThat(entry.getValue().equals(VALUE1)).isTrue();
					break;
				case TEST2:
					Assertions.assertThat(entry.getValue().equals(VALUE2)).isTrue();
					break;
				case TEST3:
					Assertions.assertThat(entry.getValue().equals(VALUE3)).isTrue();
					break;

				default:
					break;
			}
		}
	}

	@Test
	public void testCreateJSONFromObject()
	{
		final JavaScriptVariableData result = JavaScriptVariableDataFactory.createJSONFromObject(TEST1, variables);
		Assertions.assertThat(result.getQualifier().equals(TEST1)).isTrue();
		Assertions.assertThat(result.getValue().equals(JSON_VARIABLE)).isTrue();
	}

}

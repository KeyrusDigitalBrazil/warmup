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
package de.hybris.platform.ruleengineservices.rule.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CatalogVersionStripperRuleParameterValueNormalizerTest
{
	@InjectMocks
	private CatalogVersionStripperRuleParameterValueNormalizer normalizer;

	private final static String DELIMITER = "::";

	@Before
	public void setUp()
	{
		normalizer.setDelimiter(DELIMITER);
	}

	@Test
	public void shouldNormalizeValue()
	{
		assertEquals("some_value", normalizer.normalize("some_value::afterDelimiter::Value::"));
	}

	@Test
	public void shouldHandleNullValue()
	{
		assertEquals(null, normalizer.normalize(null));
	}

	@Test
	public void shouldReturnProvidedValueAsIsInCaseOfDelimiterIsNotDetected()
	{
		assertEquals("Value_As:Is", normalizer.normalize("Value_As:Is"));
	}

	@Test
	public void shouldNormalizeValuesWithinCollection()
	{
		final List<String> values = asList("Value_As:Is", null, "some_value::afterDelimiter::Value::");

		assertThat((List<String>) normalizer.normalize(values), containsInAnyOrder("Value_As:Is", null, "some_value"));
	}
}

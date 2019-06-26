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
import de.hybris.platform.ruleengineservices.rule.strategies.RuleParameterValueMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;


@UnitTest
public class DefaultRuleParameterValueMapperStrategyTest
{
	@Mock
	private ApplicationContext applicationContext;

	@Mock
	private RuleParameterValueTypeDefinition mapperDefinition;

	@Mock
	private RuleParameterValueMapper mapper;

	private DefaultRuleParameterValueMapperStrategy mapperStrategy;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		when(mapperDefinition.getType()).thenReturn("ItemType(Product)");
		when(mapperDefinition.getMapper()).thenReturn(mapper);

		when(applicationContext.getBeansOfType(RuleParameterValueTypeDefinition.class))
				.thenReturn(Collections.singletonMap("mapperDefinition", mapperDefinition));

		mapperStrategy = new DefaultRuleParameterValueMapperStrategy();
		mapperStrategy.setSupportedTypes(new HashSet<>());
		mapperStrategy.setApplicationContext(applicationContext);

		mapperStrategy.afterPropertiesSet();
	}

	@Test
	public void fromRuleParameterNullValue()
	{
		// when
		final Object value = mapperStrategy.fromRuleParameter(null, "ItemType(Product)");

		// then
		assertNull(value);
	}

	@Test
	public void toRuleParameterNullValue()
	{
		// when
		final Object value = mapperStrategy.toRuleParameter(null, "ItemType(Product)");

		// then
		assertNull(value);
	}


	@Test
	public void fromRuleParameterSimpleValue()
	{
		// given
		final BigDecimal expectedValue = BigDecimal.valueOf(123);
		mapperStrategy.getSupportedTypes().add(BigDecimal.class.getName());

		// when
		final Object value = mapperStrategy.fromRuleParameter(expectedValue, BigDecimal.class.getName());

		// then
		assertEquals(expectedValue, value);
	}

	@Test
	public void toRuleParameterSimpleValue()
	{
		// given
		final BigDecimal expectedValue = BigDecimal.valueOf(123);
		mapperStrategy.getSupportedTypes().add(BigDecimal.class.getName());

		// when
		final Object value = mapperStrategy.toRuleParameter(expectedValue, BigDecimal.class.getName());

		// then
		assertEquals(expectedValue, value);
	}

	@Test
	public void fromRuleParameter()
	{
		// given
		final String code = "producta";
		final Product expectedValue = new Product(code);

		when(mapper.fromString(code)).thenReturn(new Product(code));

		// when
		final Object value = mapperStrategy.fromRuleParameter(code, "ItemType(Product)");

		// then
		assertEquals(expectedValue, value);
	}

	@Test
	public void toRuleParameter()
	{
		// given
		final String expectedValue = "producta";

		when(mapper.toString(new Product(expectedValue))).thenReturn(expectedValue);

		// when
		final Object value = mapperStrategy.toRuleParameter(new Product(expectedValue), "ItemType(Product)");

		// then
		assertEquals(expectedValue, value);
	}

	@Test
	public void fromRuleParameterListValue()
	{
		// given
		final String code = "producta";
		final List<Product> expectedValue = Collections.singletonList(new Product(code));

		when(mapper.fromString(code)).thenReturn(new Product(code));

		// when
		final Object value = mapperStrategy.fromRuleParameter(Collections.singletonList(code), "List(ItemType(Product))");

		// then
		assertEquals(expectedValue, value);
	}

	@Test
	public void toRuleParameterListValue()
	{
		// given
		final String code = "producta";
		final List<String> expectedValue = Collections.singletonList(code);

		when(mapper.toString(new Product(code))).thenReturn(code);

		// when
		final Object value = mapperStrategy.toRuleParameter(Collections.singletonList(new Product(code)),
				"List(ItemType(Product))");

		// then
		assertEquals(expectedValue, value);
	}

	@Test
	public void fromRuleParameterMapValue()
	{
		// given
		final String code = "producta";
		final BigDecimal amount = BigDecimal.valueOf(123);
		final Map<Product, BigDecimal> expectedValue = Collections.singletonMap(new Product(code), amount);
		mapperStrategy.getSupportedTypes().add(BigDecimal.class.getName());

		when(mapper.fromString(code)).thenReturn(new Product(code));

		// when
		final Object value = mapperStrategy.fromRuleParameter(Collections.singletonMap(code, amount),
				"Map(ItemType(Product),java.math.BigDecimal)");

		// then
		assertEquals(expectedValue, value);
	}

	@Test
	public void toRuleParameterMapValue()
	{
		final String code = "producta";
		final BigDecimal amount = BigDecimal.valueOf(123);
		final Map<String, BigDecimal> expectedValue = Collections.singletonMap(code, amount);
		mapperStrategy.getSupportedTypes().add(BigDecimal.class.getName());

		when(mapper.toString(new Product(code))).thenReturn(code);

		// when
		final Object value = mapperStrategy.toRuleParameter(Collections.singletonMap(new Product(code), amount),
				"Map(ItemType(Product),java.math.BigDecimal)");

		// then
		assertEquals(expectedValue, value);
	}

	protected static class Product
	{
		private String code;

		public Product(final String code)
		{
			this.code = code;
		}

		public String getCode()
		{
			return code;
		}

		public void setCode(final String code)
		{
			this.code = code;
		}

		@Override
		public boolean equals(final Object obj)
		{
			if (this == obj)
			{
				return true;
			}

			if (obj == null)
			{
				return false;
			}

			if (!(obj instanceof Product))
			{
				return false;
			}

			return Objects.equals(code, ((Product) obj).code);
		}

		@Override
		public int hashCode()
		{
			return Objects.hash(code);
		}

		@Override
		public String toString()
		{
			return "Product(" + code + ")";
		}
	}
}

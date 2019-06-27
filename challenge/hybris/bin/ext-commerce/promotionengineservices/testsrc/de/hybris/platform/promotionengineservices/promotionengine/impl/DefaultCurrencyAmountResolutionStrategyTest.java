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
package de.hybris.platform.promotionengineservices.promotionengine.impl;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.promotionengineservices.util.PromotionResultUtils;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCurrencyAmountResolutionStrategyTest
{

	@InjectMocks
	private final DefaultCurrencyAmountResolutionStrategy strategy = new DefaultCurrencyAmountResolutionStrategy();

	@Mock
	private PromotionResultModel promotionResult;

	@Mock
	private AbstractOrderModel order;

	@Mock
	private CurrencyModel currency;

	@Mock
	private RuleParameterData data;

	@Mock
	private RuleParameterData mockedParamToReplace;

	@Rule
	public final ExpectedException expectedException = ExpectedException.none(); //NOPMD
	
	@Mock
	private PromotionResultUtils promotionResultUtils;


	@Before
	public void setUp()
	{
		final Map<String, BigDecimal> entries = new LinkedHashMap<>();
		entries.put("USD", new BigDecimal("20.00"));
		entries.put("JPY", new BigDecimal("200"));

		MockitoAnnotations.initMocks(this);
		when(promotionResult.getOrder()).thenReturn(order);
		when(order.getCurrency()).thenReturn(currency);
		when(currency.getDigits()).thenReturn(Integer.valueOf(2));
		when(currency.getIsocode()).thenReturn("USD");
		when(data.getValue()).thenReturn(entries);
		when(data.getType()).thenReturn("type");
		when(data.getUuid()).thenReturn("uuid");
		when(promotionResultUtils.getOrder(promotionResult)).thenReturn(order);
	}

	@Test
	public void testValueNullPromotionResult()
	{
		//expect
		expectedException.expect(IllegalArgumentException.class);

		//when
		strategy.getValue(data, null, Locale.US);
	}

	@Test
	public void testValueNullData()
	{
		//expect
		expectedException.expect(IllegalArgumentException.class);

		//when
		strategy.getValue(null, promotionResult, Locale.US);
	}

	@Test
	public void testValueNullLocale()
	{
		//expect
		expectedException.expect(IllegalArgumentException.class);

		//when
		strategy.getValue(data, promotionResult, null);
	}

	@Test
	public void testValueWrongInputType()
	{
		when(data.getValue()).thenReturn("WRONG_VALUE_TYPE");

		// expect
		expectedException.expect(ClassCastException.class);

		//when
		strategy.getValue(data, promotionResult, Locale.US);
	}

	@Test
	public void testValueSimpleResolutionUSD()
	{
		// setting up currency to be USD
		when(currency.getIsocode()).thenReturn("USD");
		when(currency.getSymbol()).thenReturn("$");

		final String value = strategy.getValue(data, promotionResult, Locale.US);
		Assert.assertEquals("$20.00", value);
	}

	@Test
	public void testValueSimpleResolutionJPY()
	{
		// setting up currency to be JPY
		when(currency.getIsocode()).thenReturn("JPY");
		when(currency.getDigits()).thenReturn(Integer.valueOf(0));

		final String value = strategy.getValue(data, promotionResult, Locale.JAPAN);
		Assert.assertEquals("￥200", value);
	}

	@Test
	public void testReplacedParameterNullPromotionResult()
	{
		//expect
		expectedException.expect(IllegalArgumentException.class);

		//when
		strategy.getReplacedParameter(data, null, new BigDecimal("10"));
	}

	@Test
	public void testReplacedParameterNullData()
	{
		//expect
		expectedException.expect(IllegalArgumentException.class);

		//when
		strategy.getReplacedParameter(null, promotionResult, new BigDecimal("10"));
	}

	@Test
	public void testReplacedParameterWrongValue()
	{
		//expect
		expectedException.expect(IllegalArgumentException.class);

		//when
		strategy.getReplacedParameter(data, promotionResult, new Object());
	}

	@Test
	public void testParameterToReplaceWrongValue()
	{
		//expect
		expectedException.expect(IllegalArgumentException.class);

		//when
		when(mockedParamToReplace.getValue()).thenReturn(new Object());
		strategy.getReplacedParameter(mockedParamToReplace, promotionResult, new BigDecimal("10"));
	}

	@Test
	public void testReplacedParameterNullValue()
	{
		//expect
		expectedException.expect(IllegalArgumentException.class);

		//when
		strategy.getReplacedParameter(data, promotionResult, null);
	}

	@Test
	public void testReplacedParameter()
	{
		final BigDecimal actualValueAsObject = new BigDecimal("100");
		final RuleParameterData replacedParam = strategy.getReplacedParameter(data, promotionResult, actualValueAsObject);

		Assert.assertNotEquals(data.getValue(), replacedParam.getValue());
		Assert.assertEquals(actualValueAsObject, ((Map<String, BigDecimal>) replacedParam.getValue()).get("USD"));
		Assert.assertEquals(data.getType(), replacedParam.getType());
		Assert.assertEquals(data.getUuid(), replacedParam.getUuid());
	}
}

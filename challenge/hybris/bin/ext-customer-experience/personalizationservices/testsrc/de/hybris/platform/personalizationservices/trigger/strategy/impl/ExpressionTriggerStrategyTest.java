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
/**
 *
 */
package de.hybris.platform.personalizationservices.trigger.strategy.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.personalizationservices.trigger.expression.CxExpressionContext;
import de.hybris.platform.personalizationservices.trigger.expression.CxExpressionEvaluator;
import de.hybris.platform.personalizationservices.trigger.expression.impl.CxGroupExpression;
import de.hybris.platform.personalizationservices.trigger.expression.impl.CxGroupExpression.CxGroupOperator;
import de.hybris.platform.personalizationservices.trigger.expression.impl.CxSegmentExpression;
import de.hybris.platform.personalizationservices.trigger.expression.impl.DefaultExpressionEvaluator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;


@UnitTest
public class ExpressionTriggerStrategyTest
{
	static CxExpressionEvaluator evaluator = new DefaultExpressionEvaluator();
	CxExpressionContext context;

	@Before
	public void before()
	{
		context = new CxExpressionContext();
		context.setSegments(Lists.newArrayList("a", "b", "c"));
	}

	@Test
	public void test1()
	{
		final CxGroupExpression gr = new CxGroupExpression(CxGroupOperator.AND).add(new CxSegmentExpression("a"))
				.add(new CxSegmentExpression("b"));

		Assert.assertTrue(evaluator.evaluate(gr, context));
	}

	@Test
	public void test2()
	{
		final CxGroupExpression gr = new CxGroupExpression(CxGroupOperator.OR).add(new CxSegmentExpression("a"))
				.add(new CxSegmentExpression("b"));

		Assert.assertTrue(evaluator.evaluate(gr, context));
	}

	@Test
	public void test3()
	{
		final CxGroupExpression gr = new CxGroupExpression(CxGroupOperator.AND).add(new CxSegmentExpression("a"))
				.add(new CxSegmentExpression("d"));

		Assert.assertFalse(evaluator.evaluate(gr, context));
	}

	@Test
	public void test4()
	{
		final CxGroupExpression gr = new CxGroupExpression(CxGroupOperator.OR).add(new CxSegmentExpression("a"))
				.add(new CxSegmentExpression("d"));

		Assert.assertTrue(evaluator.evaluate(gr, context));
	}

	@Test
	public void test5()
	{
		final CxGroupExpression gr = new CxGroupExpression(CxGroupOperator.OR)//
				.add(new CxGroupExpression(CxGroupOperator.AND).add(new CxSegmentExpression("a")).add(new CxSegmentExpression("b")))
				.add(new CxGroupExpression(CxGroupOperator.AND).add(new CxSegmentExpression("c")).add(new CxSegmentExpression("d")));

		Assert.assertTrue(evaluator.evaluate(gr, context));
	}
}

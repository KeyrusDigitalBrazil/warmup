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
package de.hybris.platform.personalizationfacades.trigger;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.personalizationfacades.data.ExpressionData;
import de.hybris.platform.personalizationfacades.data.ExpressionTriggerData;
import de.hybris.platform.personalizationfacades.data.GroupExpressionData;
import de.hybris.platform.personalizationfacades.data.NegationExpressionData;
import de.hybris.platform.personalizationfacades.data.SegmentExpressionData;
import de.hybris.platform.personalizationfacades.trigger.converters.populator.ExpressionTriggerPopulator;
import de.hybris.platform.personalizationfacades.trigger.converters.populator.ExpressionTriggerReversePopulator;
import de.hybris.platform.personalizationservices.model.CxExpressionTriggerModel;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@UnitTest
public class ExpressionTriggerTest
{

	private ExpressionTriggerPopulator populator;
	private ExpressionTriggerReversePopulator reversePopulator;

	private final String expr = "{\"type\":\"groupExpression\"," + "\"elements\":["
			+ "{\"type\":\"segmentExpression\",\"code\":\"segment1\"},"
			+ "{\"type\":\"negationExpression\",\"element\":{\"type\":\"segmentExpression\",\"code\":\"segment2\"}}],"
			+ "\"operator\":\"AND\"}";

	@Before
	public void setup()
	{
		populator = new ExpressionTriggerPopulator();
		reversePopulator = new ExpressionTriggerReversePopulator();
	}

	@Test
	public void populatorTest()
	{
		//given
		final CxExpressionTriggerModel source = new CxExpressionTriggerModel();
		source.setCode("exTrigger");
		source.setExpression(expr);

		final ExpressionTriggerData target = new ExpressionTriggerData();

		//when
		populator.populate(source, target);

		//then
		final ExpressionData group = target.getExpression();
		Assert.assertNotNull(group);
		Assert.assertEquals(GroupExpressionData.class, group.getClass());
		Assert.assertEquals("AND", ((GroupExpressionData) group).getOperator());
		Assert.assertNotNull(((GroupExpressionData) group).getElements());
		Assert.assertEquals(2, ((GroupExpressionData) group).getElements().size());
		final ExpressionData element1 = ((GroupExpressionData) group).getElements().get(0);
		final ExpressionData element2 = ((GroupExpressionData) group).getElements().get(1);

		Assert.assertNotNull(element1);
		Assert.assertEquals(SegmentExpressionData.class, element1.getClass());
		Assert.assertEquals("segment1", ((SegmentExpressionData) element1).getCode());

		Assert.assertNotNull(element1);
		Assert.assertEquals(NegationExpressionData.class, element2.getClass());
		final ExpressionData element3 = ((NegationExpressionData) element2).getElement();

		Assert.assertNotNull(element3);
		Assert.assertEquals(SegmentExpressionData.class, element3.getClass());
		Assert.assertEquals("segment2", ((SegmentExpressionData) element3).getCode());
	}

	@Test
	public void reversePopulatorTest()
	{
		//given
		final ExpressionTriggerData source = new ExpressionTriggerData();
		final CxExpressionTriggerModel target = new CxExpressionTriggerModel();

		final SegmentExpressionData segment1 = new SegmentExpressionData();
		segment1.setCode("segment1");

		final SegmentExpressionData segment2 = new SegmentExpressionData();
		segment2.setCode("segment2");

		final NegationExpressionData negation = new NegationExpressionData();
		negation.setElement(segment2);

		final GroupExpressionData group = new GroupExpressionData();
		group.setOperator("AND");
		group.setElements(new ArrayList());
		group.getElements().add(segment1);
		group.getElements().add(negation);

		source.setExpression(group);

		//when
		reversePopulator.populate(source, target);

		//then
		Assert.assertEquals(target.getExpression(), expr);
	}



}

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
package de.hybris.platform.personalizationfacades.trigger;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.personalizationfacades.AbstractFacadeIntegrationTest;
import de.hybris.platform.personalizationfacades.converters.ConfigurableConverter;
import de.hybris.platform.personalizationfacades.data.DefaultTriggerData;
import de.hybris.platform.personalizationfacades.data.ExpressionTriggerData;
import de.hybris.platform.personalizationfacades.data.GroupExpressionData;
import de.hybris.platform.personalizationfacades.data.NegationExpressionData;
import de.hybris.platform.personalizationfacades.data.SegmentData;
import de.hybris.platform.personalizationfacades.data.SegmentExpressionData;
import de.hybris.platform.personalizationfacades.data.SegmentTriggerData;
import de.hybris.platform.personalizationfacades.data.TriggerData;
import de.hybris.platform.personalizationfacades.data.VariationData;
import de.hybris.platform.personalizationfacades.enums.TriggerConversionOptions;
import de.hybris.platform.personalizationservices.enums.CxGroupingOperator;
import de.hybris.platform.personalizationservices.model.CxAbstractTriggerModel;
import de.hybris.platform.personalizationservices.model.CxCustomizationModel;
import de.hybris.platform.personalizationservices.model.CxDefaultTriggerModel;
import de.hybris.platform.personalizationservices.model.CxExpressionTriggerModel;
import de.hybris.platform.personalizationservices.model.CxSegmentModel;
import de.hybris.platform.personalizationservices.model.CxSegmentTriggerModel;
import de.hybris.platform.personalizationservices.model.CxVariationModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;


@IntegrationTest
public class TriggerConversionTest extends AbstractFacadeIntegrationTest
{
	@Resource
	private ConfigurableConverter<CxAbstractTriggerModel, TriggerData, TriggerConversionOptions> cxTriggerConfigurableConverter;

	@Resource
	private Converter<TriggerData, CxAbstractTriggerModel> cxTriggerReverseConverter;

	private CxSegmentTriggerModel trigger;
	private CxExpressionTriggerModel exTrigger;
	private CxDefaultTriggerModel defaultTrigger;
	private CxVariationModel variation;

	@Before
	public void setup()
	{
		final CxSegmentModel segment1 = new CxSegmentModel();
		segment1.setCode("segment1");

		final CxSegmentModel segment2 = new CxSegmentModel();
		segment2.setCode("segment2");

		variation = new CxVariationModel();
		variation.setCode("variation");

		final CxCustomizationModel customization = new CxCustomizationModel();
		customization.setCode("customization");
		variation.setCustomization(customization);


		trigger = new CxSegmentTriggerModel();
		trigger.setCode("trigger");
		trigger.setGroupBy(CxGroupingOperator.OR);
		trigger.setVariation(variation);
		trigger.setSegments(Lists.newArrayList(segment1, segment2));


		exTrigger = new CxExpressionTriggerModel();
		exTrigger.setVariation(variation);
		exTrigger.setCode("exTrigger");
		exTrigger.setExpression("{\"type\":\"groupExpression\", \"operator\":\"AND\", \"elements\":["
				+ "{\"type\":\"segmentExpression\", \"code\":\"segment1\"},"
				+ "{\"type\":\"negationExpression\", \"element\": {\"type\":\"segmentExpression\", \"code\":\"segment2\"} }]}");

		defaultTrigger = new CxDefaultTriggerModel();
		defaultTrigger.setCode("defaultTrigger");
		defaultTrigger.setVariation(variation);

	}

	@Test
	public void defaultTest() throws Exception
	{
		//when
		final SegmentTriggerData data = (SegmentTriggerData) cxTriggerConfigurableConverter.convert(trigger);

		//then
		Assert.assertNotNull(data);
		Assert.assertEquals(trigger.getCode(), data.getCode());
		Assert.assertNull(data.getVariation());
		Assert.assertNull(data.getSegments());
	}

	@Test
	public void baseTest() throws Exception
	{
		//when
		final SegmentTriggerData data = (SegmentTriggerData) cxTriggerConfigurableConverter.convert(trigger,
				Lists.newArrayList(TriggerConversionOptions.BASE));

		//then
		Assert.assertNotNull(data);
		Assert.assertEquals(trigger.getCode(), data.getCode());
		Assert.assertNull(data.getVariation());
		Assert.assertNull(data.getSegments());

	}

	@Test
	public void variationTest() throws Exception
	{
		//when
		final SegmentTriggerData data = (SegmentTriggerData) cxTriggerConfigurableConverter.convert(trigger,
				Lists.newArrayList(TriggerConversionOptions.FOR_SEGMENT));

		//then
		Assert.assertNotNull(data);
		Assert.assertEquals(trigger.getCode(), data.getCode());
		Assert.assertNotNull(data.getVariation());
		Assert.assertEquals(variation.getCode(), data.getVariation().getCode());
		Assert.assertNull(data.getSegments());

	}

	@Test
	public void segmentTest() throws Exception
	{
		//when
		final SegmentTriggerData data = (SegmentTriggerData) cxTriggerConfigurableConverter.convert(trigger,
				Lists.newArrayList(TriggerConversionOptions.FOR_VARIATION));

		//then
		Assert.assertNotNull(data);
		Assert.assertEquals(trigger.getCode(), data.getCode());
		Assert.assertEquals("OR", data.getGroupBy());
		Assert.assertNull(data.getVariation());
		Assert.assertNotNull(data.getSegments());
		Assert.assertEquals(2, data.getSegments().size());

	}

	@Test
	public void allTest() throws Exception
	{
		//when
		final SegmentTriggerData data = (SegmentTriggerData) cxTriggerConfigurableConverter.convert(trigger,
				Lists.newArrayList(TriggerConversionOptions.FULL));

		//then
		Assert.assertNotNull(data);
		Assert.assertEquals(trigger.getCode(), data.getCode());
		Assert.assertEquals("OR", data.getGroupBy());
		Assert.assertNotNull(data.getVariation());
		Assert.assertEquals(variation.getCode(), data.getVariation().getCode());
		Assert.assertNotNull(data.getSegments());
		Assert.assertEquals(2, data.getSegments().size());

	}

	@Test
	public void reverseTest() throws Exception
	{
		//given
		final SegmentTriggerData source = new SegmentTriggerData();
		source.setCode("trigger");
		source.setGroupBy("AND");
		source.setVariation(new VariationData());
		source.getVariation().setCode("variation");
		source.setSegments(new ArrayList<>());
		final SegmentData segment = new SegmentData();
		segment.setCode(SEGMENT_ID);
		source.getSegments().add(segment);

		//when
		final CxAbstractTriggerModel aResult = cxTriggerReverseConverter.convert(source);

		//then
		Assert.assertNotNull(aResult);
		Assert.assertEquals(aResult.getClass(), CxSegmentTriggerModel.class);
		final CxSegmentTriggerModel result = (CxSegmentTriggerModel) aResult;
		Assert.assertEquals("trigger", result.getCode());
		Assert.assertEquals(CxGroupingOperator.AND, result.getGroupBy());
		Assert.assertNull(result.getVariation());
		Assert.assertNotNull(result.getSegments());
		Assert.assertEquals(1, result.getSegments().size());
		Assert.assertEquals(SEGMENT_ID, result.getSegments().iterator().next().getCode());
	}

	@Test
	public void defaultExTest() throws Exception
	{
		//when
		final ExpressionTriggerData data = (ExpressionTriggerData) cxTriggerConfigurableConverter.convert(exTrigger);

		//then
		Assert.assertNotNull(data);
		Assert.assertEquals(exTrigger.getCode(), data.getCode());
		Assert.assertNull(data.getVariation());
		Assert.assertNull(data.getExpression());
	}

	@Test
	public void baseExTest() throws Exception
	{
		//when
		final ExpressionTriggerData data = (ExpressionTriggerData) cxTriggerConfigurableConverter.convert(exTrigger,
				Lists.newArrayList(TriggerConversionOptions.BASE));

		//then
		Assert.assertNotNull(data);
		Assert.assertEquals(exTrigger.getCode(), data.getCode());
		Assert.assertNull(data.getVariation());
		Assert.assertNull(data.getExpression());

	}

	@Test
	public void forVariationExTest() throws Exception
	{
		//when
		final ExpressionTriggerData data = (ExpressionTriggerData) cxTriggerConfigurableConverter.convert(exTrigger,
				Lists.newArrayList(TriggerConversionOptions.FOR_VARIATION));

		//then
		Assert.assertNotNull(data);
		Assert.assertEquals(exTrigger.getCode(), data.getCode());
		Assert.assertNull(data.getVariation());
		Assert.assertNotNull(data.getExpression());
	}

	@Test
	public void allExTest() throws Exception
	{
		//when
		final ExpressionTriggerData data = (ExpressionTriggerData) cxTriggerConfigurableConverter.convert(exTrigger,
				Lists.newArrayList(TriggerConversionOptions.FULL));

		//then
		Assert.assertNotNull(data);
		Assert.assertEquals(exTrigger.getCode(), data.getCode());
		Assert.assertNotNull(data.getVariation());
		Assert.assertEquals(variation.getCode(), data.getVariation().getCode());
		Assert.assertNotNull(data.getExpression());
	}

	@Test
	public void reverseExTest() throws Exception
	{
		//given
		final ExpressionTriggerData source = new ExpressionTriggerData();
		source.setCode("exTrigger");

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
		final CxAbstractTriggerModel aResult = cxTriggerReverseConverter.convert(source);

		//then
		Assert.assertNotNull(aResult);
		Assert.assertEquals(aResult.getClass(), CxExpressionTriggerModel.class);
		final CxExpressionTriggerModel result = (CxExpressionTriggerModel) aResult;
		Assert.assertEquals("exTrigger", result.getCode());
		Assert.assertNull(result.getVariation());
		Assert.assertEquals(
				"{\"type\":\"groupExpression\",\"elements\":[{\"type\":\"segmentExpression\",\"code\":\"segment1\"},{\"type\":\"negationExpression\",\"element\":{\"type\":\"segmentExpression\",\"code\":\"segment2\"}}],\"operator\":\"AND\"}",
				result.getExpression());
	}

	@Test
	public void defaultTriggerTest() throws Exception
	{
		//when
		final DefaultTriggerData data = (DefaultTriggerData) cxTriggerConfigurableConverter.convert(defaultTrigger);

		//then
		Assert.assertNotNull(data);
		Assert.assertEquals(defaultTrigger.getCode(), data.getCode());
		Assert.assertNull(data.getVariation());
	}

	@Test
	public void baseDefaultTriggerTest() throws Exception
	{
		//when
		final DefaultTriggerData data = (DefaultTriggerData) cxTriggerConfigurableConverter.convert(defaultTrigger,
				Lists.newArrayList(TriggerConversionOptions.BASE));

		//then
		Assert.assertNotNull(data);
		Assert.assertEquals(defaultTrigger.getCode(), data.getCode());
		Assert.assertNull(data.getVariation());
	}

	@Test
	public void forVariationDefaultTriggerTest() throws Exception
	{
		//when
		final DefaultTriggerData data = (DefaultTriggerData) cxTriggerConfigurableConverter.convert(defaultTrigger,
				Lists.newArrayList(TriggerConversionOptions.FOR_VARIATION));

		//then
		Assert.assertNotNull(data);
		Assert.assertEquals(defaultTrigger.getCode(), data.getCode());
		Assert.assertNull(data.getVariation());
	}

	@Test
	public void allDefaultTriggerTest() throws Exception
	{
		//when
		final DefaultTriggerData data = (DefaultTriggerData) cxTriggerConfigurableConverter.convert(defaultTrigger,
				Lists.newArrayList(TriggerConversionOptions.FULL));

		//then
		Assert.assertNotNull(data);
		Assert.assertEquals(defaultTrigger.getCode(), data.getCode());
		Assert.assertNotNull(data.getVariation());
		Assert.assertEquals(variation.getCode(), data.getVariation().getCode());
	}

	@Test
	public void reverseDefaultTriggerTest() throws Exception
	{
		//given
		final DefaultTriggerData source = new DefaultTriggerData();
		source.setCode("defaultTrigger");

		//when
		final CxAbstractTriggerModel aResult = cxTriggerReverseConverter.convert(source);

		//then
		Assert.assertNotNull(aResult);
		Assert.assertEquals(aResult.getClass(), CxDefaultTriggerModel.class);
		final CxDefaultTriggerModel result = (CxDefaultTriggerModel) aResult;
		Assert.assertEquals("defaultTrigger", result.getCode());
		Assert.assertNull(result.getVariation());
	}
}

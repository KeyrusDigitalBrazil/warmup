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
package de.hybris.platform.personalizationservices.trigger;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.personalizationservices.AbstractCxServiceTest;
import de.hybris.platform.personalizationservices.enums.CxGroupingOperator;
import de.hybris.platform.personalizationservices.model.CxAbstractTriggerModel;
import de.hybris.platform.personalizationservices.model.CxDefaultTriggerModel;
import de.hybris.platform.personalizationservices.model.CxExpressionTriggerModel;
import de.hybris.platform.personalizationservices.model.CxSegmentModel;
import de.hybris.platform.personalizationservices.model.CxSegmentTriggerModel;
import de.hybris.platform.personalizationservices.model.CxVariationModel;
import de.hybris.platform.personalizationservices.trigger.expression.CxExpression;
import de.hybris.platform.personalizationservices.trigger.expression.CxExpressionTriggerService;
import de.hybris.platform.personalizationservices.trigger.expression.impl.CxGroupExpression;
import de.hybris.platform.personalizationservices.trigger.expression.impl.CxSegmentExpression;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.ArrayList;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;


@IntegrationTest
public class CxTriggerServiceIntegrationTest extends AbstractCxServiceTest
{
	private static final String SEGMENT_1 = "segment1";
	private static final String SEGMENT_2 = "segment2";
	private static final String SEGMENT_3 = "segment3";

	private static final String VARIATION_10 = "variation10";

	private static final String VARIATION_1 = "exvariation1";
	private static final String VARIATION_8 = "exvariation8";


	@Resource
	private CxTriggerService cxTriggerService;

	@Resource
	private CxExpressionTriggerService cxExpressionTriggerService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	private CatalogVersionModel catalogVersion;

	@Before
	public void setup() throws Exception
	{
		catalogVersion = catalogVersionService.getCatalogVersion("testCatalog", "Online");
	}

	@Test
	public void testSegmentTriggerAdd()
	{
		//given
		final CxVariationModel variation = getVariation(VARIATION_8);
		final CxSegmentTriggerModel trigger = buildSegmentTrigger(SEGMENT_1);

		//when
		final CxAbstractTriggerModel createdTrigger = cxTriggerService.createTrigger(trigger, variation);

		//then
		assertSegmentTrigger(trigger, createdTrigger);
	}

	@Test
	public void testExpressionTriggerAdd()
	{
		//given
		final CxVariationModel variation = getVariation(VARIATION_8);
		final CxExpressionTriggerModel trigger = buildExpressionTrigger(
				new CxGroupExpression().add(new CxSegmentExpression(SEGMENT_1)).add(new CxSegmentExpression(SEGMENT_2))
				.add(new CxSegmentExpression(SEGMENT_3)));
		trigger.getSegments().add(getSegment(SEGMENT_2));

		//when
		final CxAbstractTriggerModel createdTrigger = cxTriggerService.createTrigger(trigger, variation);

		//then
		assertExpressionTrigger(trigger, createdTrigger, 3);
	}

	@Test
	public void testDefaultTriggerAdd()
	{
		//given
		final CxDefaultTriggerModel trigger = buildDefaultTrigger();
		final CxVariationModel variation = getVariation(VARIATION_8);

		//when
		final CxAbstractTriggerModel createdTrigger = cxTriggerService.createTrigger(trigger, variation);

		//then
		assertDefaultTrigger(trigger, createdTrigger);
	}

	@Test(expected = ModelSavingException.class)
	public void testSegmentTriggerAddToExisting()
	{
		//given
		final CxVariationModel variation = getVariation(VARIATION_1);
		final CxSegmentTriggerModel trigger = buildSegmentTrigger(SEGMENT_1);

		//when
		cxTriggerService.createTrigger(trigger, variation);
	}

	@Test(expected = ModelSavingException.class)
	public void testExpressionTriggerAddToExisting()
	{
		//given
		final CxVariationModel variation = getVariation(VARIATION_1);
		final CxExpressionTriggerModel trigger = buildExpressionTrigger(
				new CxGroupExpression().add(new CxSegmentExpression(SEGMENT_1)).add(new CxSegmentExpression(SEGMENT_2))
				.add(new CxSegmentExpression(SEGMENT_3)));
		trigger.getSegments().add(getSegment(SEGMENT_2));

		//when
		cxTriggerService.createTrigger(trigger, variation);
	}

	@Test(expected = ModelSavingException.class)
	public void testDefaultTriggerAddToExisting()
	{
		final CxDefaultTriggerModel trigger = buildDefaultTrigger();
		final CxVariationModel variation = getVariation(VARIATION_1);

		//when
		cxTriggerService.createTrigger(trigger, variation);
	}

	@Test
	public void testSegmentTriggerAddWithoutSegments()
	{
		//given
		final CxVariationModel variation = getVariation(VARIATION_10);
		final CxSegmentTriggerModel trigger = buildSegmentTrigger();

		//when
		final CxAbstractTriggerModel createdTrigger = cxTriggerService.createTrigger(trigger, variation);


		//then
		assertSegmentTrigger(trigger, createdTrigger);
	}

	@Test
	public void testCreateSegmentTriggerForVariationThatIsAlreadyRelatedToExpressionTrigger()
	{
		//given
		final CxVariationModel variation = getVariation(VARIATION_1);
		final CxSegmentTriggerModel trigger = buildSegmentTrigger();

		try
		{
			// when
			cxTriggerService.createTrigger(trigger, variation);
			fail("should throw InterceptorException");
		}
		catch (final ModelSavingException e)
		{
			// then
			assertThat(e.getMessage()).contains("CxSegmentTrigger or CxExpressionTrigger already exists for this variation.");
			assertThat(e.getCause()).isInstanceOf(InterceptorException.class);
		}
	}

	@Test
	public void testExpressionTriggerAddWithoutSegments()
	{
		//given
		final CxVariationModel variation = getVariation(VARIATION_8);
		final CxExpressionTriggerModel trigger = buildExpressionTrigger(
				new CxGroupExpression().add(new CxSegmentExpression(SEGMENT_1)).add(new CxSegmentExpression(SEGMENT_2))
				.add(new CxSegmentExpression(SEGMENT_3)));

		//when
		final CxAbstractTriggerModel createdTrigger = cxTriggerService.createTrigger(trigger, variation);

		//then
		assertExpressionTrigger(trigger, createdTrigger, 3);

	}

	@Test(expected = ModelSavingException.class)
	public void testExpressionTriggerAddWithoutExpression()
	{
		//given
		final CxVariationModel variation = getVariation(VARIATION_8);
		final CxExpressionTriggerModel trigger = buildExpressionTrigger(null);
		trigger.getSegments().add(getSegment(SEGMENT_2));

		//when
		cxTriggerService.createTrigger(trigger, variation);
	}

	@Test(expected = ModelSavingException.class)
	public void testExpressionTriggerAddWithInvalidExpression()
	{
		//given
		final CxVariationModel variation = getVariation(VARIATION_8);
		final CxExpressionTriggerModel trigger = buildExpressionTrigger(new CxGroupExpression());
		trigger.getSegments().add(getSegment(SEGMENT_2));

		//when
		cxTriggerService.createTrigger(trigger, variation);
	}

	private CxVariationModel getVariation(final String code)
	{
		final CxVariationModel example = new CxVariationModel();
		example.setCode(code);
		example.setCatalogVersion(catalogVersion);

		return flexibleSearchService.getModelByExample(example);
	}

	private CxSegmentModel getSegment(final String code)
	{
		final CxSegmentModel example = new CxSegmentModel();
		example.setCode(code);

		return flexibleSearchService.getModelByExample(example);
	}

	private CxSegmentTriggerModel buildSegmentTrigger(final String... segments)
	{
		final CxSegmentTriggerModel trigger = new CxSegmentTriggerModel();
		trigger.setCatalogVersion(catalogVersion);
		trigger.setCode("testTrigger");
		trigger.setGroupBy(CxGroupingOperator.AND);

		trigger.setSegments(new ArrayList<>());
		if (segments != null)
		{
			for (final String segment : segments)
			{
				trigger.getSegments().add(getSegment(segment));
			}
		}

		return trigger;
	}

	private CxExpressionTriggerModel buildExpressionTrigger(final CxExpression expression)
	{
		final CxExpressionTriggerModel trigger = new CxExpressionTriggerModel();
		trigger.setCatalogVersion(catalogVersion);
		trigger.setCode("testExTrigger");
		trigger.setSegments(new ArrayList<>());

		cxExpressionTriggerService.saveExpression(trigger, expression);

		return trigger;
	}

	private CxDefaultTriggerModel buildDefaultTrigger()
	{
		final CxDefaultTriggerModel trigger = new CxDefaultTriggerModel();
		trigger.setCatalogVersion(catalogVersion);
		trigger.setCode("testDefaultTrigger");

		return trigger;
	}

	private void assertSegmentTrigger(final CxSegmentTriggerModel expected, final CxAbstractTriggerModel created)
	{
		Assert.assertNotNull(created);
		Assert.assertTrue(created instanceof CxSegmentTriggerModel);
		final CxSegmentTriggerModel actual = (CxSegmentTriggerModel) created;

		Assert.assertEquals(expected.getCode(), actual.getCode());
		Assert.assertEquals(expected.getGroupBy(), actual.getGroupBy());
		Assert.assertEquals(expected.getSegments(), actual.getSegments());
	}

	private void assertExpressionTrigger(final CxExpressionTriggerModel expected, final CxAbstractTriggerModel created,
			final int segmentsNumber)
	{
		Assert.assertNotNull(created);
		Assert.assertTrue(created instanceof CxExpressionTriggerModel);
		final CxExpressionTriggerModel actual = (CxExpressionTriggerModel) created;

		Assert.assertEquals(expected.getCode(), actual.getCode());
		Assert.assertEquals(expected.getExpression(), actual.getExpression());
		Assert.assertNotNull(actual.getSegments());
		Assert.assertEquals(segmentsNumber, actual.getSegments().size());

	}

	private void assertDefaultTrigger(final CxDefaultTriggerModel expected, final CxAbstractTriggerModel created)
	{
		Assert.assertNotNull(created);
		Assert.assertTrue(created instanceof CxDefaultTriggerModel);

		Assert.assertEquals(expected.getCode(), created.getCode());
	}
}

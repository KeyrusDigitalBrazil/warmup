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
package de.hybris.platform.personalizationfacades.trigger.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.personalizationfacades.AbstractFacadeIntegrationTest;
import de.hybris.platform.personalizationfacades.data.DefaultTriggerData;
import de.hybris.platform.personalizationfacades.data.SegmentData;
import de.hybris.platform.personalizationfacades.data.SegmentTriggerData;
import de.hybris.platform.personalizationfacades.data.TriggerData;
import de.hybris.platform.personalizationfacades.trigger.TriggerFacade;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;


@IntegrationTest
public class DefaultTriggerFacadeIntegrationTest extends AbstractFacadeIntegrationTest
{
	protected static final String VARIATION_OR = "variation2";
	protected static final String VARIATION_AND = "variation3";

	protected static final String TRIGGER_ID = "trigger0";
	protected static final String NEW_TRIGGER = "newTrigger";

	protected static final String OR = "OR";
	protected static final String AND = "AND";

	@Resource
	private TriggerFacade cxTriggerFacade;

	@Test
	public void getTriggersTest()
	{
		//when
		final List<TriggerData> triggers = cxTriggerFacade.getTriggers(CUSTOMIZATION_ID, VARIATION_ID, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);

		//then
		Assert.assertNotNull(triggers);
		Assert.assertEquals(1, triggers.size());
	}

	@Test(expected = UnknownIdentifierException.class)
	public void getTriggersFromInvalidVariationTest()
	{
		//when
		cxTriggerFacade.getTriggers(CUSTOMIZATION_ID, NEW_VARIATION_ID, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void getTriggersFromInvalidCustomizationTest()
	{
		//when
		cxTriggerFacade.getTriggers(NOTEXISTING_CUSTOMIZATION_ID, VARIATION_ID, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void getTriggersFromInvalidCatalogTest()
	{
		//when
		cxTriggerFacade.getTriggers(CUSTOMIZATION_ID, VARIATION_ID, NOTEXISTING_CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test
	public void getTriggerTest()
	{
		//when
		final TriggerData trigger = cxTriggerFacade.getTrigger(CUSTOMIZATION_ID, VARIATION_ID, TRIGGER_ID, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);

		//then
		Assert.assertNotNull(trigger);
		Assert.assertTrue(trigger instanceof SegmentTriggerData);
		final SegmentTriggerData sTrigger = (SegmentTriggerData) trigger;
		Assert.assertEquals(TRIGGER_ID, sTrigger.getCode());
		Assert.assertEquals(OR, sTrigger.getGroupBy());
		Assert.assertNotNull(sTrigger.getSegments());
		Assert.assertEquals(1, sTrigger.getSegments().size());
	}

	@Test
	public void getTriggerWithORTest()
	{
		//when
		final TriggerData trigger = cxTriggerFacade.getTrigger(CUSTOMIZATION_ID, VARIATION_OR, TRIGGER_ID, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);

		//then
		Assert.assertNotNull(trigger);
		Assert.assertTrue(trigger instanceof SegmentTriggerData);
		final SegmentTriggerData sTrigger = (SegmentTriggerData) trigger;
		Assert.assertEquals(TRIGGER_ID, sTrigger.getCode());
		Assert.assertEquals(OR, sTrigger.getGroupBy());
		Assert.assertNotNull(sTrigger.getSegments());
		Assert.assertEquals(2, sTrigger.getSegments().size());
	}

	@Test
	public void getTriggerWithANDTest()
	{
		//when
		final TriggerData trigger = cxTriggerFacade.getTrigger(CUSTOMIZATION_ID, VARIATION_AND, TRIGGER_ID, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);

		//then
		Assert.assertNotNull(trigger);
		Assert.assertTrue(trigger instanceof SegmentTriggerData);
		final SegmentTriggerData sTrigger = (SegmentTriggerData) trigger;
		Assert.assertEquals(TRIGGER_ID, sTrigger.getCode());
		Assert.assertEquals(AND, sTrigger.getGroupBy());
		Assert.assertNotNull(sTrigger.getSegments());
		Assert.assertEquals(2, sTrigger.getSegments().size());
	}


	@Test(expected = UnknownIdentifierException.class)
	public void getTriggerWithInvalidIdTest()
	{
		//when
		cxTriggerFacade.getTrigger(CUSTOMIZATION_ID, NEW_VARIATION_ID, NEW_TRIGGER, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}


	@Test(expected = UnknownIdentifierException.class)
	public void getTriggerFromInvalidVariationTest()
	{
		//when
		cxTriggerFacade.getTrigger(CUSTOMIZATION_ID, NEW_VARIATION_ID, TRIGGER_ID, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void getTriggerFromInvalidCustomizationTest()
	{
		//when
		cxTriggerFacade.getTrigger(NOTEXISTING_CUSTOMIZATION_ID, VARIATION_ID, TRIGGER_ID, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void getTriggerFromInvalidCatalogTest()
	{
		//when
		cxTriggerFacade.getTrigger(CUSTOMIZATION_ID, VARIATION_ID, TRIGGER_ID, NOTEXISTING_CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test
	public void createTriggerTest()
	{
		//given
		final SegmentTriggerData triggerData = getTriggerData(NEW_TRIGGER, OR, SEGMENT_ID, SEGMENT_ID_1);

		//when
		final TriggerData trigger = cxTriggerFacade.createTrigger(CUSTOMIZATION_ID, VARIATION_ID_1, triggerData, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);

		//then
		Assert.assertNotNull(trigger);
		Assert.assertTrue(trigger instanceof SegmentTriggerData);
		final SegmentTriggerData sTrigger = (SegmentTriggerData) trigger;
		Assert.assertEquals(triggerData.getCode(), sTrigger.getCode());
		Assert.assertEquals(triggerData.getGroupBy(), sTrigger.getGroupBy());
		Assert.assertNull(sTrigger.getVariation());
		assertSegmentCodes(sTrigger, SEGMENT_ID, SEGMENT_ID_1);
	}


	@Test
	public void createAndUpdateTriggerSameSegmentsTest()
	{
		//given
		final SegmentTriggerData triggerData = getTriggerData(NEW_TRIGGER, OR, SEGMENT_ID, SEGMENT_ID);

		//when
		final TriggerData trigger = cxTriggerFacade.createTrigger(CUSTOMIZATION_ID, VARIATION_ID_1, triggerData, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);

		//then
		Assert.assertNotNull(trigger);
		Assert.assertTrue(trigger instanceof SegmentTriggerData);
		final SegmentTriggerData sTrigger = (SegmentTriggerData) trigger;
		Assert.assertEquals(triggerData.getCode(), sTrigger.getCode());
		Assert.assertEquals(triggerData.getGroupBy(), sTrigger.getGroupBy());
		Assert.assertNull(sTrigger.getVariation());
		assertSegmentCodes(sTrigger, SEGMENT_ID);



		//when
		final TriggerData triggerUpdated = cxTriggerFacade.updateTrigger(CUSTOMIZATION_ID, VARIATION_ID_1, trigger.getCode(),
				triggerData, CATALOG_ID, CATALOG_VERSION_STAGE_ID);


		//then
		Assert.assertNotNull(trigger);
		Assert.assertTrue(trigger instanceof SegmentTriggerData);
		final SegmentTriggerData sTrigger2 = (SegmentTriggerData) triggerUpdated;
		Assert.assertEquals(triggerData.getCode(), sTrigger2.getCode());
		Assert.assertEquals(triggerData.getGroupBy(), sTrigger2.getGroupBy());
		Assert.assertNull(sTrigger2.getVariation());
		assertSegmentCodes(sTrigger2, SEGMENT_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createNextSegmentTriggerTest()
	{
		//given
		final SegmentTriggerData triggerData = getTriggerData(NEW_TRIGGER, OR, SEGMENT_ID, SEGMENT_ID_1);

		//when
		cxTriggerFacade.createTrigger(CUSTOMIZATION_ID, VARIATION_ID, triggerData, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void createTriggerInInvalidVariationTest()
	{
		//given
		final SegmentTriggerData triggerData = getTriggerData(NEW_TRIGGER, OR, SEGMENT_ID, SEGMENT_ID_1);

		//when
		cxTriggerFacade.createTrigger(CUSTOMIZATION_ID, NEW_VARIATION_ID, triggerData, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void createTriggerInInvalidCustomizationTest()
	{
		//given
		final SegmentTriggerData triggerData = getTriggerData(NEW_TRIGGER, OR, SEGMENT_ID, SEGMENT_ID_1);

		//when
		cxTriggerFacade.createTrigger(NOTEXISTING_CUSTOMIZATION_ID, VARIATION_ID_1, triggerData, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void createTriggerInInvalidcatalogTest()
	{
		//given
		final SegmentTriggerData triggerData = getTriggerData(NEW_TRIGGER, OR, SEGMENT_ID, SEGMENT_ID_1);

		//when
		cxTriggerFacade.createTrigger(CUSTOMIZATION_ID, VARIATION_ID_1, triggerData, NOTEXISTING_CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void createTriggerWithInvalidSegmentTest()
	{
		//given
		final SegmentTriggerData triggerData = getTriggerData(NEW_TRIGGER, OR, SEGMENT_ID, NOTEXISTING_SEGMENT_ID);

		//when
		cxTriggerFacade.createTrigger(CUSTOMIZATION_ID, VARIATION_ID_1, triggerData, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test
	public void createDefaultTriggerTest()
	{
		//given
		final DefaultTriggerData triggerData = new DefaultTriggerData();
		triggerData.setCode(NEW_TRIGGER);

		//when
		final TriggerData trigger = cxTriggerFacade.createTrigger(CUSTOMIZATION_ID, VARIATION_ID_1, triggerData, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);

		//then
		Assert.assertNotNull(trigger);
		Assert.assertTrue(trigger instanceof DefaultTriggerData);
		Assert.assertEquals(triggerData.getCode(), trigger.getCode());
	}

	@Test
	public void createTriggerWithoutSegmentsTest()
	{
		//given
		final SegmentTriggerData triggerData = getTriggerData(NEW_TRIGGER, OR);

		//when
		final TriggerData trigger = cxTriggerFacade.createTrigger(CUSTOMIZATION_ID, VARIATION_ID_1, triggerData, CATALOG_ID, CATALOG_VERSION_STAGE_ID);

		//then
		Assert.assertNotNull(trigger);
		Assert.assertTrue(trigger instanceof SegmentTriggerData);
		final SegmentTriggerData sTrigger = (SegmentTriggerData) trigger;
		Assert.assertEquals(triggerData.getCode(), sTrigger.getCode());
		Assert.assertEquals(triggerData.getGroupBy(), sTrigger.getGroupBy());
		Assert.assertNull(sTrigger.getVariation());
		Assert.assertThat(sTrigger.getSegments(), is(empty()));
	}

	@Test
	public void createTriggerWithoutNullSegmentsTest()
	{
		//given
		final SegmentTriggerData triggerData = getTriggerData(NEW_TRIGGER, OR);
		triggerData.setSegments(null);

		//when
		final TriggerData trigger = cxTriggerFacade.createTrigger(CUSTOMIZATION_ID, VARIATION_ID_1, triggerData, CATALOG_ID, CATALOG_VERSION_STAGE_ID);

		//then
		Assert.assertNotNull(trigger);
		Assert.assertTrue(trigger instanceof SegmentTriggerData);
		final SegmentTriggerData sTrigger = (SegmentTriggerData) trigger;
		Assert.assertEquals(triggerData.getCode(), sTrigger.getCode());
		Assert.assertEquals(triggerData.getGroupBy(), sTrigger.getGroupBy());
		Assert.assertNull(sTrigger.getVariation());
		Assert.assertThat(sTrigger.getSegments(), is(empty()));
	}

	@Test
	public void updateTriggerTest()
	{
		//given
		final SegmentTriggerData triggerData = getTriggerData(TRIGGER_ID, AND, SEGMENT_ID, SEGMENT_ID_1);

		//when
		final TriggerData trigger = cxTriggerFacade.updateTrigger(CUSTOMIZATION_ID, VARIATION_ID, TRIGGER_ID, triggerData,
				CATALOG_ID, CATALOG_VERSION_STAGE_ID);

		//then
		Assert.assertNotNull(trigger);
		Assert.assertTrue(trigger instanceof SegmentTriggerData);
		final SegmentTriggerData sTrigger = (SegmentTriggerData) trigger;
		Assert.assertEquals(triggerData.getCode(), sTrigger.getCode());
		Assert.assertEquals(triggerData.getGroupBy(), sTrigger.getGroupBy());
		Assert.assertNull(sTrigger.getVariation());
		assertSegmentCodes(sTrigger, SEGMENT_ID, SEGMENT_ID_1);
	}

	@Test
	public void updateTriggerWithInconsistentIdTest()
	{
		//given
		final SegmentTriggerData triggerData = getTriggerData(NEW_TRIGGER, AND, SEGMENT_ID, SEGMENT_ID_1);

		//when
		final TriggerData trigger = cxTriggerFacade.updateTrigger(CUSTOMIZATION_ID, VARIATION_ID, TRIGGER_ID, triggerData,
				CATALOG_ID, CATALOG_VERSION_STAGE_ID);

		//then
		Assert.assertNotNull(trigger);
		Assert.assertTrue(trigger instanceof SegmentTriggerData);
		final SegmentTriggerData sTrigger = (SegmentTriggerData) trigger;
		Assert.assertEquals(triggerData.getCode(), sTrigger.getCode());
		Assert.assertEquals(triggerData.getGroupBy(), sTrigger.getGroupBy());
		Assert.assertNull(sTrigger.getVariation());
		assertSegmentCodes(sTrigger, SEGMENT_ID, SEGMENT_ID_1);
	}

	@Test
	public void updateTriggerWithoutSegmentsTest()
	{
		//given
		final SegmentTriggerData triggerData = getTriggerData(TRIGGER_ID, AND);

		//when
		final TriggerData trigger = cxTriggerFacade.updateTrigger(CUSTOMIZATION_ID, VARIATION_ID, TRIGGER_ID, triggerData, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);

		//then
		Assert.assertNotNull(trigger);
		Assert.assertTrue(trigger instanceof SegmentTriggerData);
		final SegmentTriggerData sTrigger = (SegmentTriggerData) trigger;
		Assert.assertEquals(triggerData.getCode(), sTrigger.getCode());
		Assert.assertEquals(triggerData.getGroupBy(), sTrigger.getGroupBy());
		Assert.assertNull(sTrigger.getVariation());
		Assert.assertThat(sTrigger.getSegments(), is(empty()));
	}

	@Test(expected = UnknownIdentifierException.class)
	public void updateNonexistingTriggerTest()
	{
		//given
		final SegmentTriggerData triggerData = getTriggerData(TRIGGER_ID, AND, SEGMENT_ID);

		//when
		cxTriggerFacade.updateTrigger(CUSTOMIZATION_ID, VARIATION_ID, NEW_TRIGGER, triggerData, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void updateTriggerInInvalidCustomizationTest()
	{
		//given
		final SegmentTriggerData triggerData = getTriggerData(TRIGGER_ID, AND, SEGMENT_ID);

		//when
		cxTriggerFacade.updateTrigger(NOTEXISTING_CUSTOMIZATION_ID, VARIATION_ID, TRIGGER_ID, triggerData, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void updateTriggerInInvalidVariationTest()
	{
		//given
		final SegmentTriggerData triggerData = getTriggerData(TRIGGER_ID, AND, SEGMENT_ID);

		//when
		cxTriggerFacade.updateTrigger(CUSTOMIZATION_ID, NEW_VARIATION_ID, TRIGGER_ID, triggerData, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void updateTriggerInInvalidCatalogTest()
	{
		//given
		final SegmentTriggerData triggerData = getTriggerData(TRIGGER_ID, AND, SEGMENT_ID);

		//when
		cxTriggerFacade.updateTrigger(CUSTOMIZATION_ID, VARIATION_ID, TRIGGER_ID, triggerData, NOTEXISTING_CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);
	}

	@Test
	public void deleteTriggerTest()
	{
		//when
		cxTriggerFacade.deleteTrigger(CUSTOMIZATION_ID, VARIATION_ID, TRIGGER_ID, CATALOG_ID, CATALOG_VERSION_STAGE_ID);

		//then
		boolean deleted = false;
		try
		{
			cxTriggerFacade.getTrigger(CUSTOMIZATION_ID, VARIATION_ID, TRIGGER_ID, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
		}
		catch (final UnknownIdentifierException e)
		{
			deleted = true;
		}
		Assert.assertTrue(deleted);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void deleteNonexistingTriggerTest()
	{
		//when
		cxTriggerFacade.deleteTrigger(CUSTOMIZATION_ID, VARIATION_ID, NEW_TRIGGER, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void deleteTriggerFromInvalidCustomizationTest()
	{
		//when
		cxTriggerFacade.deleteTrigger(NOTEXISTING_CUSTOMIZATION_ID, VARIATION_ID, TRIGGER_ID, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void deleteTriggerFromInvalidVariationTest()
	{
		//when
		cxTriggerFacade.deleteTrigger(CUSTOMIZATION_ID, NEW_VARIATION_ID, TRIGGER_ID, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void deleteTriggerFromInvalidCatalogTest()
	{
		//when
		cxTriggerFacade.deleteTrigger(CUSTOMIZATION_ID, VARIATION_ID, TRIGGER_ID, NOTEXISTING_CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	private void assertSegmentCodes(final SegmentTriggerData data, final String... segmentCodes)
	{
		if (segmentCodes != null)
		{
			Assert.assertNotNull(data.getSegments());
			Assert.assertEquals(segmentCodes.length, data.getSegments().size());
			final List<String> existingSegmentCodes = data.getSegments().stream().map(SegmentData::getCode)
					.collect(Collectors.toList());
			for (final String segment : segmentCodes)
			{
				Assert.assertTrue("Missing segment " + segment, existingSegmentCodes.contains(segment));
			}
		}
		else
		{
			Assert.assertNull(data.getSegments());
		}

	}

	private SegmentTriggerData getTriggerData(final String code, final String operator, final String... segmentCodes)
	{
		final SegmentTriggerData result = new SegmentTriggerData();

		result.setCode(code);
		result.setGroupBy(operator);

		if (segmentCodes != null)
		{
			result.setSegments(new ArrayList<>());
			for (final String segmentCode : segmentCodes)
			{
				final SegmentData segment = new SegmentData();
				segment.setCode(segmentCode);
				result.getSegments().add(segment);
			}
		}
		return result;
	}
}

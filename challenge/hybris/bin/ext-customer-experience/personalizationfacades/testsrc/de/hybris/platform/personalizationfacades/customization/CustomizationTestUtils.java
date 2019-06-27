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
package de.hybris.platform.personalizationfacades.customization;

import de.hybris.platform.personalizationfacades.data.CustomizationData;
import de.hybris.platform.personalizationfacades.data.SegmentData;
import de.hybris.platform.personalizationfacades.data.SegmentTriggerData;
import de.hybris.platform.personalizationfacades.data.TriggerData;
import de.hybris.platform.personalizationfacades.data.VariationData;
import de.hybris.platform.personalizationfacades.enums.ItemStatus;
import de.hybris.platform.personalizationservices.enums.CxGroupingOperator;
import de.hybris.platform.personalizationservices.model.CxCustomizationModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Collections;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import org.junit.Assert;


public class CustomizationTestUtils
{

	public static CustomizationData creteCustomizationData(final String customizationCode, final String customizationName,
			final String variationCode, final String variationName, final String triggerCode, final String segmentCode)
	{
		return creteCustomizationData(customizationCode, customizationName, variationCode, variationName,
				() -> createSegmentTriggerData(triggerCode, segmentCode));
	}

	public static CustomizationData creteCustomizationData(final String customizationCode, final String customizationName,
			final String variationCode, final String variationName, final Supplier<TriggerData> triggerSupplier)
	{
		if (customizationCode == null)
		{
			return null;
		}

		final CustomizationData customizationData = new CustomizationData();
		customizationData.setCode(customizationCode);
		customizationData.setName(customizationName);
		customizationData.setStatus(ItemStatus.ENABLED);
		if (variationCode != null)
		{
			customizationData
					.setVariations(Collections.singletonList(createVariationData(variationCode, variationName, triggerSupplier)));
		}

		return customizationData;
	}

	public static VariationData createVariationData(final String variationCode, final String variationName,
			final Supplier<TriggerData> triggerSupplier)
	{
		if (variationCode == null)
		{
			return null;
		}

		final VariationData variation = new VariationData();
		variation.setCode(variationCode);
		variation.setName(variationName);
		variation.setStatus(ItemStatus.ENABLED);
		if (triggerSupplier != null)
		{
			final TriggerData trigger = triggerSupplier.get();
			if (trigger != null)
			{
				variation.setTriggers(Collections.singletonList(trigger));
			}
		}
		return variation;
	}

	public static SegmentTriggerData createSegmentTriggerData(final String triggerCode, final String segmentCode)
	{
		if (triggerCode == null)
		{
			return null;
		}

		final SegmentTriggerData trigger = new SegmentTriggerData();
		trigger.setCode(triggerCode);
		trigger.setGroupBy(CxGroupingOperator.AND.getCode());
		if (segmentCode != null)
		{
			final SegmentData segment = new SegmentData();
			segment.setCode(segmentCode);
			trigger.setSegments(Collections.singletonList(segment));
		}
		return trigger;
	}

	public static void assertVariationsEquals(final VariationData expected, final VariationData actual)
	{
		assertVariationsEquals(expected, actual, (t1, t2) -> assertSegmentTriggerEquals(t1, t2));
	}

	public static void assertVariationsEquals(final VariationData expected, final VariationData actual,
			final BiConsumer<TriggerData, TriggerData> triggerEqualsValidator)
	{
		Assert.assertEquals(expected.getCode(), actual.getCode());
		if (expected.getTriggers() != null)
		{
			Assert.assertNotNull("Triggers are missing for variation : " + expected.getCode(), actual.getTriggers());
			Assert.assertEquals("Trigger list is not equal for variation : " + expected.getCode(), expected.getTriggers().size(),
					actual.getTriggers().size());
			for (final TriggerData expectedTrigger : expected.getTriggers())
			{
				boolean triggerFound = false;
				for (final TriggerData actualTrigger : actual.getTriggers())
				{
					if (expectedTrigger.getCode().equals(actualTrigger.getCode()))
					{
						if (triggerEqualsValidator != null)
						{
							triggerEqualsValidator.accept(expectedTrigger, actualTrigger);
						}
						triggerFound = true;
						break;
					}
				}
				Assert.assertTrue("Missing trigger " + expectedTrigger.getCode(), triggerFound);
			}
		}
	}

	public static void assertSegmentTriggerEquals(final TriggerData expectedTrigger, final TriggerData actualTrigger)
	{
		Assert.assertEquals(expectedTrigger.getClass(), actualTrigger.getClass());
		Assert.assertTrue(expectedTrigger instanceof SegmentTriggerData);

		final SegmentTriggerData expectedSegmentTrigger = (SegmentTriggerData) expectedTrigger;
		final SegmentTriggerData actualSegmentTrigger = (SegmentTriggerData) actualTrigger;
		expectedSegmentTrigger.getGroupBy().equals(actualSegmentTrigger.getGroupBy());
		if (expectedSegmentTrigger.getSegments() != null)
		{
			Assert.assertNotNull("Segments are missing for trigger : " + expectedTrigger.getCode(),
					actualSegmentTrigger.getSegments());
			Assert.assertEquals("Segment list is not equal for trigger : " + expectedTrigger.getCode(),
					expectedSegmentTrigger.getSegments().size(), actualSegmentTrigger.getSegments().size());
			for (final SegmentData expectedSegment : expectedSegmentTrigger.getSegments())
			{
				boolean segmentFound = false;
				for (final SegmentData actualSegment : actualSegmentTrigger.getSegments())
				{
					if (expectedSegment.getCode().equals(actualSegment.getCode()))
					{
						segmentFound = true;
						break;
					}
				}
				Assert.assertTrue("Missing segment " + expectedSegment.getCode(), segmentFound);
			}
		}
	}

	public static void removeAllCustomizations(final FlexibleSearchService flexibleSearchService, final ModelService modelService)
	{
		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(
				"SELECT {" + CxCustomizationModel.PK + "} FROM {" + CxCustomizationModel._TYPECODE + "} ");

		fQuery.setResultClassList(Collections.singletonList(CxCustomizationModel.class));
		final SearchResult<CxCustomizationModel> searchResult = flexibleSearchService.search(fQuery);
		for (final CxCustomizationModel model : searchResult.getResult())
		{
			modelService.remove(model);
		}
	}

}

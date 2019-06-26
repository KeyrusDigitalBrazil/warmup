/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.facades.populator.analytics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.facades.analytics.AnalyticCsticData;
import de.hybris.platform.sap.productconfig.facades.analytics.AnalyticCsticValueData;
import de.hybris.platform.sap.productconfig.facades.analytics.AnalyticsPopulatorInput;
import de.hybris.platform.sap.productconfig.facades.impl.UniqueUIKeyGeneratorImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsDocument;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsItem;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsPopularityIndicator;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsPossibleValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("javadoc")
@UnitTest
public class AnalyticsPopulatorTest
{
	private static final String UI_KEY = "instanceId-instanceName.groupName.csticName";
	private AnalyticsPopulator classUnderTest;
	private List<AnalyticCsticData> target;
	private AnalyticsPopulatorInput source;
	private List<String> csticUiKeys;
	private AnalyticsDocument document;
	private AnalyticsCharacteristic analyticCstic;
	private AnalyticsItem analyticRootInstance;
	private List<AnalyticsPossibleValue> analyticValues;

	@Before
	public void setUp()
	{
		classUnderTest = new AnalyticsPopulator();
		classUnderTest.setUiKeyGenerator(new UniqueUIKeyGeneratorImpl());
		target = new ArrayList<>();
		csticUiKeys = new ArrayList<>();
		document = new AnalyticsDocument();
		source = new AnalyticsPopulatorInput();
		source.setCsticUiKeys(csticUiKeys);
		source.setDocument(document);

		analyticRootInstance = new AnalyticsItem();
		analyticRootInstance.setProductId("instanceName");
		document.setRootItem(analyticRootInstance);

		analyticCstic = new AnalyticsCharacteristic();
		analyticRootInstance.setCharacteristics(Collections.singletonList(analyticCstic));
		analyticCstic.setId("csticName");

		analyticValues = new ArrayList<>();
		analyticCstic.setPossibleValues(analyticValues);
		createAnalyticValue(analyticValues, "val1", 100.0);


	}

	protected AnalyticsPossibleValue createEmptyAnalyticValue(final List<AnalyticsPossibleValue> analyticValueList,
			final String valueName)
	{
		final AnalyticsPossibleValue analyticPossibleValue = new AnalyticsPossibleValue();
		if (null != analyticValueList)
		{
			analyticValueList.add(analyticPossibleValue);
		}
		analyticPossibleValue.setValue(valueName);
		analyticPossibleValue.setPopularityIndicators(Collections.emptyList());
		return analyticPossibleValue;
	}

	protected AnalyticsPossibleValue createAnalyticValue(final List<AnalyticsPossibleValue> analyticValueList,
			final String valueName, final double popularityInPercent)
	{
		final AnalyticsPossibleValue analyticPossibleValue = createEmptyAnalyticValue(analyticValueList, valueName);
		final List<AnalyticsPopularityIndicator> indicatorList = new ArrayList<>();
		final AnalyticsPopularityIndicator popularotyIndicator = new AnalyticsPopularityIndicator();
		indicatorList.add(popularotyIndicator);
		popularotyIndicator.setValue(Double.valueOf(popularityInPercent));
		popularotyIndicator.setType(AnalyticsPopulator.POPULARITY_INDICATOR_TYPE_PERCENTAGE);
		analyticPossibleValue.setPopularityIndicators(indicatorList);
		return analyticPossibleValue;
	}

	@Test
	public void testPopulate()
	{
		csticUiKeys.add(UI_KEY);
		classUnderTest.populate(source, target);
		assertEquals(1, target.size());
		assertEquals(UI_KEY, target.get(0).getCsticUiKey());
		assertNotNull(target.get(0).getAnalyticValues());
	}

	@Test
	public void testPopulateNoValues()
	{
		createEmptyAnalyticValue(analyticValues, "val1");
		csticUiKeys.add(UI_KEY);
		classUnderTest.populate(source, target);
		assertTrue(target.isEmpty());

	}

	@Test
	public void testPopulate_instanceNoExisting()
	{
		csticUiKeys.add("instanceIdX-instanceNameX.groupName.csticName");
		classUnderTest.populate(source, target);
		assertTrue(target.isEmpty());
	}

	@Test
	public void testPopulate_csticNoExisting()
	{
		csticUiKeys.add("instanceId-instanceName.groupName.csticNameX");
		classUnderTest.populate(source, target);
		assertTrue(target.isEmpty());
	}

	@Test
	public void testFindAnalyticInstance()
	{
		final AnalyticsItem analyticInstance = classUnderTest.findAnalyticInstance(null, "instanceName", document);
		assertSame(this.analyticRootInstance, analyticInstance);
	}

	@Test
	public void testFindAnalyticInstance_notMatching()
	{
		final AnalyticsItem analyticInstance = classUnderTest.findAnalyticInstance(null, "bla", document);
		assertNull(analyticInstance);
	}

	@Test
	public void testFindAnalyticInstance_matchLast()
	{
		document.setRootItem(null);
		final AnalyticsItem analyticInstance = classUnderTest.findAnalyticInstance(analyticRootInstance, "instanceName", document);
		assertSame(this.analyticRootInstance, analyticInstance);
	}

	@Test
	public void testFindAnalyticCharacteristic()
	{
		final AnalyticsCharacteristic analyticCstic = classUnderTest.findAnalyticCstic("csticName", analyticRootInstance);
		assertSame(this.analyticCstic, analyticCstic);
	}

	@Test
	public void testFindAnalyticCharacteristic_notMatching()
	{
		final AnalyticsCharacteristic analyticCstic = classUnderTest.findAnalyticCstic("bla", analyticRootInstance);
		assertNull(analyticCstic);
	}

	@Test
	public void testPopulateAanalyticCstic()
	{
		createEmptyAnalyticValue(analyticValues, "val2");
		final AnalyticCsticData analyticCsticData = new AnalyticCsticData();
		classUnderTest.populate(analyticCstic, analyticCsticData);
		assertNotNull(analyticCsticData.getAnalyticValues());
		assertEquals(2, analyticCsticData.getAnalyticValues().size());
		assertTrue(analyticCsticData.getAnalyticValues().keySet().contains("val1"));
		assertTrue(analyticCsticData.getAnalyticValues().keySet().contains("val2"));
	}

	@Test
	public void testPopulatePossibleValue()
	{
		final AnalyticCsticValueData analyticValueData = new AnalyticCsticValueData();
		final AnalyticsPossibleValue analyticValue = createAnalyticValue(null, "val1", 40.0);

		classUnderTest.populate(analyticValue, analyticValueData);
		assertEquals(40.0, analyticValueData.getPopularityPercentage(), 0);
	}

	@Test
	public void testPopulatePossibleValue_Null()
	{
		final AnalyticCsticValueData analyticValueData = new AnalyticCsticValueData();
		final AnalyticsPossibleValue analyticValue = createEmptyAnalyticValue(null, "val1");
		analyticValue.setPopularityIndicators(null);

		classUnderTest.populate(analyticValue, analyticValueData);
		assertEquals(0, analyticValueData.getPopularityPercentage(), 0);
	}


}

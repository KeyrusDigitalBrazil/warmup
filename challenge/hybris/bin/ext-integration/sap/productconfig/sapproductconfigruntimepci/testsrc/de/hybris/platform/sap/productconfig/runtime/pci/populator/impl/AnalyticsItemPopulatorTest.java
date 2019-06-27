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
package de.hybris.platform.sap.productconfig.runtime.pci.populator.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsItem;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("javadoc")
@UnitTest
public class AnalyticsItemPopulatorTest
{
	private static final String VALUE_ID = "value id";
	private static final String CSTIC_ID = "cstic id";
	private static final String PRODUCT_ID = "product id ";
	private AnalyticsItemPopulator classUnderTest;
	private InstanceModel source;
	private AnalyticsItem target;
	private CsticModel cstic;
	private CsticValueModel value;

	@Before
	public void setup()
	{
		classUnderTest = new AnalyticsItemPopulator();
		source = new InstanceModelImpl();
		source.setName(PRODUCT_ID);
		source.setCstics(new ArrayList<>());
		cstic = new CsticModelImpl();
		cstic.setName(CSTIC_ID);
		source.addCstic(cstic);
		value = new CsticValueModelImpl();
		value.setName(VALUE_ID);
		final List<CsticValueModel> possibleValues = new ArrayList<>();
		possibleValues.add(value);
		cstic.setAssignableValues(possibleValues);
		cstic.setConstrained(true);
		final List<CsticValueModel> values = new ArrayList<>();
		values.add(value);
		cstic.setAssignedValues(values);
		target = new AnalyticsItem();
	}

	@Test
	public void testPopulate()
	{
		classUnderTest.populate(source, target);
		assertEquals(PRODUCT_ID, target.getProductId());
	}

	@Test
	public void testPopulateCharacteristics()
	{
		classUnderTest.populateCharacteristics(source, target);
		assertNotNull(target.getCharacteristics());
		assertEquals(1, target.getCharacteristics().size());
		assertEquals(CSTIC_ID, target.getCharacteristics().get(0).getId());
	}


	@Test
	public void testPopulatePossibleValues()
	{
		final AnalyticsCharacteristic result = new AnalyticsCharacteristic();
		classUnderTest.populatePossibleValues(cstic, result);
		assertNotNull(result.getPossibleValues());
		assertEquals(1, result.getPossibleValues().size());
		assertEquals(VALUE_ID, result.getPossibleValues().get(0).getValue());
	}

	@Test
	public void testPopulatePossibleValuesNotSelectable()
	{
		value.setSelectable(false);
		final AnalyticsCharacteristic result = new AnalyticsCharacteristic();
		classUnderTest.populatePossibleValues(cstic, result);
		assertNotNull(result.getPossibleValues());
		assertEquals("Value not selectable, don't send to PCI!", 0, result.getPossibleValues().size());
	}

	@Test
	public void testPopulateValues()
	{
		final AnalyticsCharacteristic result = new AnalyticsCharacteristic();
		classUnderTest.populateValues(cstic, result);
		assertNotNull(result.getValues());
		assertEquals(1, result.getValues().size());
		assertEquals(VALUE_ID, result.getValues().get(0).getValue());
	}

	@Test
	public void testConsiderCsticForAnalytics()
	{
		assertTrue(classUnderTest.considerCsticForAnalytics(cstic));
	}

	@Test
	public void testConsiderCsticForAnalyticsNoPossibleValues()
	{
		cstic.setAssignableValues(Collections.emptyList());
		assertFalse(classUnderTest.considerCsticForAnalytics(cstic));
	}

	@Test
	public void testConsiderCsticForAnalyticsMultiValued()
	{
		cstic.setMultivalued(true);
		assertFalse(classUnderTest.considerCsticForAnalytics(cstic));
	}

	@Test
	public void testConsiderCsticForAnalyticsUnconstrained()
	{
		cstic.setConstrained(false);
		assertFalse(classUnderTest.considerCsticForAnalytics(cstic));
	}

	@Test
	public void testConsiderCsticForAnalyticsAdditionalValues()
	{
		cstic.setAllowsAdditionalValues(true);
		assertFalse(classUnderTest.considerCsticForAnalytics(cstic));
	}

	@Test
	public void testConsiderCsticForAnalyticsIntervalsInDomain()
	{
		cstic.setIntervalInDomain(true);
		assertFalse(classUnderTest.considerCsticForAnalytics(cstic));
	}

	@Test
	public void testConsiderCsticForAnalyticsValidCstic()
	{
		ensureCsticHasCorrectMetadata(cstic);
		assertTrue(classUnderTest.considerCsticForAnalytics(cstic));
	}

	protected void ensureCsticHasCorrectMetadata(final CsticModel cstic2)
	{
		cstic.setAllowsAdditionalValues(false);
		cstic.setMultivalued(false);
		cstic.setIntervalInDomain(false);
		cstic.setConstrained(true);
	}


}

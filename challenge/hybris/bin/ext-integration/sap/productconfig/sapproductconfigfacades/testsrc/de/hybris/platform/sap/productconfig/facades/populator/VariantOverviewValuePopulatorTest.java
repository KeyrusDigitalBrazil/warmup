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
package de.hybris.platform.sap.productconfig.facades.populator;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.FeatureData;
import de.hybris.platform.sap.productconfig.facades.overview.CharacteristicValue;
import de.hybris.platform.sap.productconfig.facades.overview.ValuePositionTypeEnum;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


/**
 * Unit tests
 */
@UnitTest
public class VariantOverviewValuePopulatorTest
{

	public VariantOverviewValuePopulator classUnderTest;

	@Before
	public void setUp()
	{
		classUnderTest = new VariantOverviewValuePopulator();
	}

	@Test
	public void testPopulatorSingleValue()
	{
		final List<CharacteristicValue> target = new ArrayList<>();
		final FeatureData source = VariantOverviewPopulatorTest.mockFeatureColorRed();
		classUnderTest.populate(source, target);

		assertEquals("We expect 1 value: ", 1, target.size());

		final CharacteristicValue value1 = target.get(0);
		assertEquals("We expect cstic description: ", VariantOverviewPopulatorTest.CSTIC_COLOR, value1.getCharacteristic());
		assertEquals("We expect value: ", VariantOverviewPopulatorTest.VALUE_RED, value1.getValue());
		assertEquals("We expect value position type: ", ValuePositionTypeEnum.ONLY_VALUE, value1.getValuePositionType());
	}

	@Test
	public void testPopulatorMultiValue()
	{
		final List<CharacteristicValue> target = new ArrayList<>();
		final FeatureData source = VariantOverviewPopulatorTest.mockFeatureAccessories();
		classUnderTest.populate(source, target);

		assertEquals("We expect 3 values: ", 3, target.size());

		final CharacteristicValue value1 = target.get(0);
		assertEquals("We expect cstic description: ", VariantOverviewPopulatorTest.CSTIC_ACC, value1.getCharacteristic());
		assertEquals("We expect value: ", VariantOverviewPopulatorTest.VALUE_RADIO, value1.getValue());
		assertEquals("We expect value position type: ", ValuePositionTypeEnum.FIRST, value1.getValuePositionType());

		final CharacteristicValue value2 = target.get(1);
		assertEquals("We expect cstic description: ", VariantOverviewPopulatorTest.CSTIC_ACC, value2.getCharacteristic());
		assertEquals("We expect value: ", VariantOverviewPopulatorTest.VALUE_CUP, value2.getValue());
		assertEquals("We expect value position type: ", ValuePositionTypeEnum.INTERJACENT, value2.getValuePositionType());

		final CharacteristicValue value3 = target.get(2);
		assertEquals("We expect cstic description: ", VariantOverviewPopulatorTest.CSTIC_ACC, value2.getCharacteristic());
		assertEquals("We expect value: ", VariantOverviewPopulatorTest.VALUE_NAVI, value3.getValue());
		assertEquals("We expect value position type: ", ValuePositionTypeEnum.LAST, value3.getValuePositionType());
	}

}

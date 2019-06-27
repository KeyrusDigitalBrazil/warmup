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
package de.hybris.platform.sap.productconfig.rules.backoffice.editors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.rules.backoffice.constants.SapproductconfigrulesbackofficeConstants;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DefaultProductConfigRuleCsticValueEditorTest extends BaseProductConfigRuleParameterEditorTest
{

	private DefaultProductConfigRuleCsticValueEditor classUnderTest;

	@Override
	@Before
	public void setUp()
	{
		super.setUp();
		classUnderTest = new DefaultProductConfigRuleCsticValueEditor();
		classUnderTest.setParameterProviderService(parameterProviderService);
		classUnderTest.setFlexibleSearchService(flexibleSearchService);
	}

	@Test
	public void testGetPossibleValuesForConditionParameterNoProduct()
	{
		final List<Object> possibleValues = classUnderTest.getPossibleValuesForConditionParameter(null, CSTIC_2);
		assertEquals(0, possibleValues.size());
	}


	@Test
	public void testGetPossibleValuesForConditionParameter()
	{
		final List<Object> possibleValues = classUnderTest.getPossibleValuesForConditionParameter(productModel.getCode(), CSTIC_2);
		assertEquals(4, possibleValues.size());
		assertTrue(possibleValues.contains("C21"));
		assertTrue(possibleValues.contains("C22"));
		assertTrue(possibleValues.contains("C23"));
		assertTrue(possibleValues.contains("C24"));
	}

	@Test
	public void testGetPossibleValuesForActionParameterNoProduct()
	{
		final List<String> productCodeList = new ArrayList<String>();
		productCodeList.add(null);

		final List<Object> possibleValues = classUnderTest.getPossibleValuesForActionParameter(productCodeList, CSTIC_2);
		assertEquals(0, possibleValues.size());

	}

	@Test
	public void testGetPossibleValuesForActionParameter()
	{
		final List<String> productCodeList = new ArrayList<String>();
		productCodeList.add(PRODUCT_CODE);
		productCodeList.add(PRODUCT_CODE2);
		final List<Object> possibleValues = classUnderTest.getPossibleValuesForActionParameter(productCodeList, CSTIC_2);
		assertEquals(5, possibleValues.size());
		assertTrue(possibleValues.contains("C21"));
		assertTrue(possibleValues.contains("C22"));
		assertTrue(possibleValues.contains("C23"));
		assertTrue(possibleValues.contains("C24"));
		assertTrue(possibleValues.contains("C25"));
	}

	@Test
	public void testGetPossibleValues()
	{
		final List<Object> possibleValues = classUnderTest.getPossibleValues(context);

		assertEquals(4, possibleValues.size());
		for (int i = 1; i <= possibleValues.size(); i++)
		{
			final String valueName = CSTIC_2 + i;
			assertTrue(possibleValues.contains(valueName));
		}
	}

	@Test
	public void testGetPossibleValues_ProductModel()
	{
		final List<Object> possibleValues = classUnderTest.getPossibleValues(context);
		assertEquals(4, possibleValues.size());
	}

	@Test
	public void testGetPossibleValues_ProductCodeList()
	{
		final List<String> productCodeList = new ArrayList<String>();
		productCodeList.add(PRODUCT_CODE);
		productCodeList.add(PRODUCT_CODE2);
		parameters.put(SapproductconfigrulesbackofficeConstants.REFERENCE_SEARCH_CONDITION_PRODUCT_CODE_LIST, productCodeList);

		final List<Object> possibleValues = classUnderTest.getPossibleValues(context);
		assertEquals(5, possibleValues.size());
	}

	@Test
	public void testAddValuesForProductCode()
	{
		final List<Object> values = new ArrayList<Object>();
		classUnderTest.addValuesForProductCode(values, PRODUCT_CODE, CSTIC_2);
		assertEquals(4, values.size());
		assertTrue(values.contains("C21"));
		assertTrue(values.contains("C22"));
		assertTrue(values.contains("C23"));
		assertTrue(values.contains("C24"));

		classUnderTest.addValuesForProductCode(values, PRODUCT_CODE2, CSTIC_2);
		assertEquals(5, values.size());
		assertTrue(values.contains("C21"));
		assertTrue(values.contains("C22"));
		assertTrue(values.contains("C23"));
		assertTrue(values.contains("C24"));
		assertTrue(values.contains("C25"));
	}
}

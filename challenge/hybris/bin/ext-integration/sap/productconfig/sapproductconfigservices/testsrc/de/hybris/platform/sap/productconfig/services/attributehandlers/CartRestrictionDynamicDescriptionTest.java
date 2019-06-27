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
package de.hybris.platform.sap.productconfig.services.attributehandlers;

import static org.junit.Assert.assertFalse;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.spy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.services.model.CMSCartConfigurationRestrictionModel;

import org.junit.Test;


/**
 * Tests: CartRestrictionDynamicDescription
 */
@UnitTest
public class CartRestrictionDynamicDescriptionTest //extends ServicelayerTest
{
	private CartRestrictionDynamicDescription classUnderTest = new CartRestrictionDynamicDescription();

	/**
	 * Testing description
	 */
	@Test
	public void testDescription()
	{
		final CMSCartConfigurationRestrictionModel restrictionModel = new CMSCartConfigurationRestrictionModel();
		classUnderTest = spy(classUnderTest);
		willReturn("a nice text").given(classUnderTest).getLocalizedText(CartRestrictionDynamicDescription.DESCR_KEY);
		assertFalse("We expect a non empty description", classUnderTest.get(restrictionModel).isEmpty());
	}

	/**
	 * Test set (which will raise an exception)
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testSet()
	{
		final CMSCartConfigurationRestrictionModel restrictionModel = new CMSCartConfigurationRestrictionModel();
		classUnderTest.set(restrictionModel, "");
	}

}

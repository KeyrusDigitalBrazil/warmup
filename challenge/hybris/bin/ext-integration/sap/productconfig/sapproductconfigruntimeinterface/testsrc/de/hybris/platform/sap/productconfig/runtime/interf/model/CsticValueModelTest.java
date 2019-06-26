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
package de.hybris.platform.sap.productconfig.runtime.interf.model;

import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;

import org.apache.commons.lang.NotImplementedException;
import org.junit.Test;


/**
 *
 */
@UnitTest
public class CsticValueModelTest
{

	private final CsticValueModelStable classUnderTest = new CsticValueModelStable();

	@Test(expected = NotImplementedException.class)
	public void testSetLongText()
	{
		classUnderTest.setLongText("");
	}

	@Test
	public void testGetLongText()
	{
		assertTrue(classUnderTest.getLongText().isEmpty());
	}

	private static final class CsticValueModelStable implements CsticValueModel
	{

		@Override
		public String getName()
		{
			return null;
		}

		@Override
		public void setName(final String name)
		{

		}

		@Override
		public String getLanguageDependentName()
		{
			return null;
		}

		@Override
		public void setLanguageDependentName(final String languageDependentName)
		{
		}

		@Override
		public boolean isDomainValue()
		{
			return false;
		}

		@Override
		public void setDomainValue(final boolean domainValue)
		{

		}

		@Override
		public void setAuthor(final String author)
		{

		}

		@Override
		public String getAuthor()
		{
			return null;
		}

		@Override
		public boolean isSelectable()
		{
			return false;
		}

		@Override
		public void setSelectable(final boolean selectable)
		{

		}

		@Override
		public void setAuthorExternal(final String authorExternal)
		{

		}

		@Override
		public String getAuthorExternal()
		{
			return null;
		}

		@Override
		public PriceModel getDeltaPrice()
		{
			return null;
		}

		@Override
		public void setDeltaPrice(final PriceModel deltaPrice)
		{

		}

		@Override
		public PriceModel getValuePrice()
		{
			return null;
		}

		@Override
		public void setValuePrice(final PriceModel valuePrice)
		{
		}

		@Override
		public void setNumeric(final boolean b)
		{

		}

		@Override
		public boolean isNumeric()
		{
			return false;
		}

	}
}

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
package de.hybris.platform.sap.productconfig.facades.filters;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.facades.ConfigurationTestData;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;

import java.util.List;

import org.junit.Before;
import org.junit.Test;


/**
 *
 */
@UnitTest
public class VisibleValueFilterTest
{
	private CsticModel csticModel;

	private VisibleValueFilter visibleFilter;

	@Before
	public void setup()
	{
		visibleFilter = new VisibleValueFilter();
		csticModel = new CsticModelImpl();

	}

	@Test
	public void testVisibleFilterVisible()
	{
		final String name = "abc";
		csticModel = ConfigurationTestData.createSTRCstic();


		ConfigurationTestData.setAssignedValue(name, csticModel, CsticValueModel.AUTHOR_USER);

		final List<CsticValueModel> filterResult = visibleFilter.filter(csticModel, FilterTestData.setVisibleFilter());

		assertEquals(csticModel.getAssignedValues().size(), filterResult.size());

	}


	@Test
	public void testVisibleFilterNotVisible()
	{
		final String name = "abc";
		csticModel = ConfigurationTestData.createSTRCstic();
		csticModel.setVisible(false);
		ConfigurationTestData.setAssignedValue(name, csticModel, CsticValueModel.AUTHOR_USER);

		final List<CsticValueModel> filterResult = visibleFilter.filter(csticModel, FilterTestData.setVisibleFilter());

		assertEquals(0, filterResult.size());

	}

	@Test
	public void testVisibleFilterVisibleFilterNotactive()
	{
		final String name = "abc";
		csticModel = ConfigurationTestData.createSTRCstic();


		ConfigurationTestData.setAssignedValue(name, csticModel, CsticValueModel.AUTHOR_USER);

		final List<CsticValueModel> filterResult = visibleFilter.filter(csticModel, FilterTestData.setNoFilters());

		assertEquals(csticModel.getAssignedValues().size(), filterResult.size());

	}


	@Test
	public void testVisibleFilterNotVisibleFilterNotActive()
	{
		final String name = "abc";
		csticModel = ConfigurationTestData.createSTRCstic();
		csticModel.setVisible(false);
		ConfigurationTestData.setAssignedValue(name, csticModel, CsticValueModel.AUTHOR_USER);

		final List<CsticValueModel> filterResult = visibleFilter.filter(csticModel, FilterTestData.setNoFilters());

		assertEquals(csticModel.getAssignedValues().size(), filterResult.size());

	}

	@Test
	public void testVisibleFilterVisibleNoMatch()
	{
		final String name = "abc";
		csticModel = ConfigurationTestData.createSTRCstic();
		csticModel.setVisible(false);
		ConfigurationTestData.setAssignedValue(name, csticModel, CsticValueModel.AUTHOR_USER);

		final List<CsticValueModel> filterResult = visibleFilter.noMatch(csticModel.getAssignedValues(), csticModel);

		assertEquals(csticModel.getAssignedValues().size(), filterResult.size());
	}

	@Test
	public void testVisibleFilterNotVisibleNoMatch()
	{
		final String name = "abc";
		csticModel = ConfigurationTestData.createSTRCstic();
		csticModel.setVisible(true);
		ConfigurationTestData.setAssignedValue(name, csticModel, CsticValueModel.AUTHOR_USER);

		final List<CsticValueModel> filterResult = visibleFilter.noMatch(csticModel.getAssignedValues(), csticModel);

		assertEquals(0, filterResult.size());
	}
}

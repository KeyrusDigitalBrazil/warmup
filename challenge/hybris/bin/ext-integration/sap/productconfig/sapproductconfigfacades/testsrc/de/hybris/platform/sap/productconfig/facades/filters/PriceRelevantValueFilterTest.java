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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;


/**
 *
 */
@UnitTest
public class PriceRelevantValueFilterTest
{
	private CsticModel csticModel;

	private PriceRelevantValueFilter priceFilter;

	@Before
	public void setup()
	{
		priceFilter = new PriceRelevantValueFilter();
		csticModel = new CsticModelImpl();

	}

	@Test
	public void testPriceRelevanFilter_noPriced()
	{
		final String name = "abc";
		csticModel = ConfigurationTestData.createSTRCstic();

		ConfigurationTestData.setAssignedValue(name, csticModel, CsticValueModel.AUTHOR_USER);

		final List<CsticValueModel> filterResult = priceFilter.filter(csticModel, FilterTestData.setPiceRelevantFilter());

		assertEquals(0, filterResult.size());


	}

	@Test
	public void testPriceRelevanFilter_noPrice_FilterNotActive()
	{
		final String name = "abc";
		csticModel = ConfigurationTestData.createSTRCstic();

		ConfigurationTestData.setAssignedValue(name, csticModel, CsticValueModel.AUTHOR_SYSTEM);
		final List<CsticValueModel> filterResult = priceFilter.filter(csticModel, FilterTestData.setNoFilters());

		assertEquals(csticModel.getAssignedValues().size(), filterResult.size());


	}

	@Test
	public void testPriceRelevantFilter()
	{
		final String name = "abc";
		csticModel = ConfigurationTestData.createSTRCstic();

		ConfigurationTestData.setAssignedValue(name, csticModel, CsticValueModel.AUTHOR_SYSTEM, "200");
		final List<CsticValueModel> filterResult = priceFilter.filter(csticModel, FilterTestData.setAllFilters());

		assertEquals(csticModel.getAssignedValues().size(), filterResult.size());

	}

	@Test
	public void testMVPriceFilter_2CsticsWithPrice()
	{
		csticModel = ConfigurationTestData.createCheckBoxListCsticWithValue2Assigned();
		final Map<String, String> valueAndAuthor = new HashMap<String, String>();
		valueAndAuthor.put("VAL2", CsticValueModel.AUTHOR_USER);
		valueAndAuthor.put("VAL3", CsticValueModel.AUTHOR_SYSTEM);
		valueAndAuthor.put("VAL4", CsticValueModel.AUTHOR_USER);

		final Map<String, String> valueAndPrice = new HashMap<String, String>();
		valueAndPrice.put("VAL2", "200");
		valueAndPrice.put("VAL3", "50");
		valueAndPrice.put("VAL4", null);
		ConfigurationTestData.setAssignedValues(csticModel, valueAndAuthor, valueAndPrice);

		final List<CsticValueModel> filterResult = priceFilter.filter(csticModel, FilterTestData.setAllFilters());

		assertEquals(2, filterResult.size());
		assertEquals("VAL2", filterResult.get(0).getName());
		assertEquals("VAL3", filterResult.get(1).getName());
	}

	@Test
	public void testNonMatchMVPriceFilter_2CsticsWithPrice()
	{
		csticModel = ConfigurationTestData.createCheckBoxListCsticWithValue2Assigned();
		final Map<String, String> valueAndAuthor = new HashMap<String, String>();
		valueAndAuthor.put("VAL2", CsticValueModel.AUTHOR_USER);
		valueAndAuthor.put("VAL3", CsticValueModel.AUTHOR_SYSTEM);
		valueAndAuthor.put("VAL4", CsticValueModel.AUTHOR_USER);

		final Map<String, String> valueAndPrice = new HashMap<String, String>();
		valueAndPrice.put("VAL2", "200");
		valueAndPrice.put("VAL3", "50");
		valueAndPrice.put("VAL4", null);
		ConfigurationTestData.setAssignedValues(csticModel, valueAndAuthor, valueAndPrice);

		final List<CsticValueModel> filterResult = priceFilter.noMatch(csticModel.getAssignedValues(), csticModel);

		assertEquals(1, filterResult.size());
		assertEquals("VAL4", filterResult.get(0).getName());
	}

	@Test
	public void testMVPriceFilter_2CsticsWithPrice_FilterNotActive()
	{
		csticModel = ConfigurationTestData.createCheckBoxListCsticWithValue2Assigned();
		final Map<String, String> valueAndAuthor = new HashMap<String, String>();
		valueAndAuthor.put("VAL2", CsticValueModel.AUTHOR_USER);
		valueAndAuthor.put("VAL3", CsticValueModel.AUTHOR_SYSTEM);
		valueAndAuthor.put("VAL4", CsticValueModel.AUTHOR_USER);

		final Map<String, String> valueAndPrice = new HashMap<String, String>();
		valueAndPrice.put("VAL2", "200");
		valueAndPrice.put("VAL3", "50");
		valueAndPrice.put("VAL4", null);
		ConfigurationTestData.setAssignedValues(csticModel, valueAndAuthor, valueAndPrice);


		final List<CsticValueModel> filterResult = priceFilter.filter(csticModel, FilterTestData.setNoFilters());

		assertEquals(3, filterResult.size());
		assertEquals("VAL2", filterResult.get(0).getName());
		assertEquals("VAL3", filterResult.get(1).getName());
		assertEquals("VAL4", filterResult.get(2).getName());

	}

	@Test
	public void testMVPriceFilter_noPrice()
	{
		csticModel = ConfigurationTestData.createCheckBoxListCsticWithValue2Assigned();
		final Map<String, String> valueAndAuthor = new HashMap<String, String>();
		valueAndAuthor.put("VAL2", CsticValueModel.AUTHOR_SYSTEM);
		valueAndAuthor.put("VAL3", CsticValueModel.AUTHOR_SYSTEM);
		valueAndAuthor.put("VAL4", CsticValueModel.AUTHOR_SYSTEM);
		ConfigurationTestData.setAssignedValues(csticModel, valueAndAuthor);

		final List<CsticValueModel> filterResult = priceFilter.filter(csticModel, FilterTestData.setAllFilters());

		assertEquals(0, filterResult.size());

	}


}

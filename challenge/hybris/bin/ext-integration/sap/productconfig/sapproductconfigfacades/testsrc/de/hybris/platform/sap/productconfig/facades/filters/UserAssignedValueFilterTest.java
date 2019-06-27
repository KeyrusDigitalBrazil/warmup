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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;


/**
 *
 */
@UnitTest
public class UserAssignedValueFilterTest
{
	private CsticModel csticModel;

	private UserAssignedValueFilter userAssignedFilter;

	@Before
	public void setup()
	{
		userAssignedFilter = new UserAssignedValueFilter();
		csticModel = new CsticModelImpl();

	}

	@Test
	public void testSVUserAssignedFilter_userAssigned()
	{
		final String name = "abc";
		csticModel = ConfigurationTestData.createSTRCstic();

		ConfigurationTestData.setAssignedValue(name, csticModel, CsticValueModel.AUTHOR_EXTERNAL_USER);
		final List<CsticValueModel> filterResult = userAssignedFilter.filter(csticModel, FilterTestData.setUserAssignedFilter());

		assertEquals(csticModel.getAssignedValues().size(), filterResult.size());


	}

	@Test
	public void testSVUserAssignedFilter_userAssigned_FilterNotActive()
	{
		final String name = "abc";
		csticModel = ConfigurationTestData.createSTRCstic();

		ConfigurationTestData.setAssignedValue(name, csticModel, "4");
		final List<CsticValueModel> filterResult = userAssignedFilter.filter(csticModel, FilterTestData.setNoFilters());

		assertEquals(csticModel.getAssignedValues().size(), filterResult.size());


	}

	@Test
	public void testSVUserAssignedFilter_systemAssigned()
	{
		final String name = "abc";
		csticModel = ConfigurationTestData.createSTRCstic();

		ConfigurationTestData.setAssignedValue(name, csticModel, "4");
		final List<CsticValueModel> filterResult = userAssignedFilter.filter(csticModel, FilterTestData.setAllFilters());

		assertEquals(0, filterResult.size());

	}

	@Test
	public void testSVUserAssignedFilter_noValue()
	{
		csticModel = ConfigurationTestData.createSTRCstic();
		final List<CsticValueModel> filterResult = userAssignedFilter.filter(csticModel, FilterTestData.setAllFilters());

		assertEquals(csticModel.getAssignedValues().size(), filterResult.size());

	}


	@Test
	public void testMVUserAssignedFilter_userAndSystemAssigned()
	{
		csticModel = ConfigurationTestData.createCheckBoxListCsticWithValue2Assigned();
		final Map<String, String> valueAndAuthor = new HashMap<String, String>();
		valueAndAuthor.put("VAL2", CsticValueModel.AUTHOR_EXTERNAL_USER);
		valueAndAuthor.put("VAL3", "4");
		valueAndAuthor.put("VAL4", CsticValueModel.AUTHOR_EXTERNAL_USER);
		ConfigurationTestData.setAssignedValues(csticModel, valueAndAuthor);

		final List<CsticValueModel> filterResult = userAssignedFilter.filter(csticModel, FilterTestData.setAllFilters());

		assertEquals(2, filterResult.size());
		assertEquals("VAL2", filterResult.get(0).getName());
		assertEquals("VAL4", filterResult.get(1).getName());

	}

	@Test
	public void testNoneMatchMVUserAssignedFilter_userAndSystemAssigned()
	{
		csticModel = ConfigurationTestData.createCheckBoxListCsticWithValue2Assigned();
		final Map<String, String> valueAndAuthor = new HashMap<String, String>();
		valueAndAuthor.put("VAL2", CsticValueModel.AUTHOR_EXTERNAL_USER);
		valueAndAuthor.put("VAL3", "4");
		valueAndAuthor.put("VAL4", CsticValueModel.AUTHOR_EXTERNAL_USER);
		ConfigurationTestData.setAssignedValues(csticModel, valueAndAuthor);

		final List<CsticValueModel> filterResult = userAssignedFilter.noMatch(csticModel.getAssignedValues(), csticModel);

		assertEquals(1, filterResult.size());
		assertEquals("VAL3", filterResult.get(0).getName());
	}


	@Test
	public void testMVUserAssignedFilter_userAndSystemAssigned_FilterNotActive()
	{
		csticModel = ConfigurationTestData.createCheckBoxListCsticWithValue2Assigned();
		final Map<String, String> valueAndAuthor = new HashMap<String, String>();
		valueAndAuthor.put("VAL2", CsticValueModel.AUTHOR_EXTERNAL_USER);
		valueAndAuthor.put("VAL3", "4");
		valueAndAuthor.put("VAL4", CsticValueModel.AUTHOR_EXTERNAL_USER);
		ConfigurationTestData.setAssignedValues(csticModel, valueAndAuthor);

		final List<CsticValueModel> filterResult = userAssignedFilter.filter(csticModel, FilterTestData.setNoFilters());

		assertEquals(3, filterResult.size());
		assertEquals("VAL2", filterResult.get(0).getName());
		assertEquals("VAL3", filterResult.get(1).getName());
		assertEquals("VAL4", filterResult.get(2).getName());

	}

	@Test
	public void testMVUserAssignedFilter_systemAssigned()
	{
		csticModel = ConfigurationTestData.createCheckBoxListCsticWithValue2Assigned();
		final Map<String, String> valueAndAuthor = new HashMap<String, String>();
		valueAndAuthor.put("VAL2", "4");
		valueAndAuthor.put("VAL3", "4");
		valueAndAuthor.put("VAL4", "4");
		ConfigurationTestData.setAssignedValues(csticModel, valueAndAuthor);

		final List<CsticValueModel> filterResult = userAssignedFilter.filter(csticModel, FilterTestData.setAllFilters());

		assertEquals(0, filterResult.size());

	}

	@Test
	public void testMVUserAssignedFilter_userAssigned()
	{
		csticModel = ConfigurationTestData.createCheckBoxListCsticWithValue2Assigned();
		final Map<String, String> valueAndAuthor = new HashMap<String, String>();
		valueAndAuthor.put("VAL2", CsticValueModel.AUTHOR_EXTERNAL_USER);
		valueAndAuthor.put("VAL3", CsticValueModel.AUTHOR_EXTERNAL_USER);
		valueAndAuthor.put("VAL4", CsticValueModel.AUTHOR_EXTERNAL_USER);
		ConfigurationTestData.setAssignedValues(csticModel, valueAndAuthor);

		final List<CsticValueModel> filterResult = userAssignedFilter.filter(csticModel, FilterTestData.setAllFilters());

		assertEquals(3, filterResult.size());

	}

	@Test
	public void testMVUserAssignedFilter_noAssigendValues()
	{
		csticModel = ConfigurationTestData.createCheckBoxListCsticWithValue2Assigned();
		csticModel.setAssignedValues(new ArrayList<CsticValueModel>());
		final List<CsticValueModel> filterResult = userAssignedFilter.filter(csticModel, FilterTestData.setAllFilters());

		assertEquals(0, filterResult.size());

	}


}

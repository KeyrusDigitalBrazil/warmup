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
package de.hybris.platform.sap.productconfig.rules.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.rules.ConfigurationRulesTestData;
import de.hybris.platform.sap.productconfig.rules.service.ProductConfigRuleUtil;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Before;
import org.junit.Test;


@UnitTest
public class ProductConfigRuleUtilImplTest
{
	private ProductConfigRuleUtil classUnderTest;
	private List<CsticModel> cstics;

	@Before
	public void setUp()
	{
		classUnderTest = new ProductConfigRuleUtilImpl();
		cstics = Collections.emptyList();
	}

	@Test
	public void testGetPlainCsticsRootInstanceNull()
	{
		cstics = classUnderTest.getCstics(null);
		assertTrue("cstics list should be empty", CollectionUtils.isEmpty(cstics));
		final Map<String, CsticModel> csticMap = classUnderTest.getCsticMap(null);
		assertTrue("cstic map should be empty", csticMap.isEmpty());
	}

	@Test
	public void testGetPlainCsticsWithNoneSubInstance()
	{
		final ConfigModel configModel = ConfigurationRulesTestData.createConfigModelWith2GroupAndAssignedValues();
		cstics = classUnderTest.getCstics(configModel);
		assertTrue("cstics list should not be empty", CollectionUtils.isNotEmpty(cstics));
		assertEquals("length of cstics should be equal 9", 9, cstics.size());
		final Map<String, CsticModel> csticMap = classUnderTest.getCsticMap(configModel);
		assertEquals("size of cstic map should be equal 9", 9, csticMap.size());
	}

	@Test
	public void testGetPlainCsticsWithSeveralSubInstances()
	{
		final ConfigModel configModel = ConfigurationRulesTestData.createConfigModelWithSubInstances();
		cstics = classUnderTest.getCstics(configModel);
		assertTrue("cstics list should not be empty", CollectionUtils.isNotEmpty(cstics));
		assertEquals("length of cstics should be equal 2", 2, cstics.size());
		final Map<String, CsticModel> csticMap = classUnderTest.getCsticMap(configModel);
		assertEquals("size of cstic map should be equal 2", 2, csticMap.size());
	}

	@Test
	public void testGetCsticsForCsticNameSingle()
	{
		final ConfigModel configModel = ConfigurationRulesTestData.createConfigModelWithCstic();
		cstics = classUnderTest.getCsticsForCsticName(configModel, ConfigurationRulesTestData.STRING_CSTIC);
		assertEquals("length of cstics should be equal 1", 1, cstics.size());
	}

	@Test
	public void testGetCsticsForCsticNameMulti()
	{
		final ConfigModel configModel = ConfigurationRulesTestData.createConfigModelWithSubInstances();
		cstics = classUnderTest.getCsticsForCsticName(configModel, ConfigurationRulesTestData.STRING_CSTIC);
		assertEquals("length of cstics should be equal 3", 3, cstics.size());
	}

	@Test
	public void testGetCsticsForCsticNameNotExist()
	{
		final ConfigModel configModel = ConfigurationRulesTestData.createConfigModelWithSubInstances();
		cstics = classUnderTest.getCsticsForCsticName(configModel, "WRONG_CSTIC_NAME");
		assertTrue("cstiics should be empty", cstics.isEmpty());
	}


}

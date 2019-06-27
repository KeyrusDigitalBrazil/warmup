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
package de.hybris.platform.sap.productconfig.rules.interceptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengine.enums.RuleType;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengineservices.rule.dao.RuleDao;
import de.hybris.platform.sap.productconfig.rules.enums.ProductConfigRuleMessageSeverity;
import de.hybris.platform.sap.productconfig.rules.model.ProductConfigSourceRuleModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductConfigRulesPrepareInterceptorTest
{

	protected static final String CHARACTERISTIC = "CSTIC";

	private ProductConfigRulesPrepareInterceptor classUnderTest;
	private AbstractRuleEngineRuleModel runtimeRule;
	private ProductConfigSourceRuleModel sourceRule;

	@Mock
	private RuleDao mockedRuleDao;


	@Before
	public void setUp()
	{
		classUnderTest = new ProductConfigRulesPrepareInterceptor();
		runtimeRule = new AbstractRuleEngineRuleModel();
		sourceRule = new ProductConfigSourceRuleModel();

		MockitoAnnotations.initMocks(this);
		classUnderTest.setRuleDao(mockedRuleDao);
	}



	@Test
	public void testMapSeverity()
	{
		sourceRule.setMessageSeverity(ProductConfigRuleMessageSeverity.WARNING);
		classUnderTest.mapSeverity(runtimeRule, sourceRule);
		assertEquals(ProductConfigRuleMessageSeverity.WARNING, runtimeRule.getMessageSeverity());
	}

	@Test
	public void testMapMessageForCstic()
	{
		sourceRule.setMessageForCstic(CHARACTERISTIC);
		classUnderTest.mapMessageForCstic(runtimeRule, sourceRule);
		assertEquals(CHARACTERISTIC, runtimeRule.getMessageForCstic());
	}

	@Test
	public void testOnPrepareWrongType() throws InterceptorException
	{
		sourceRule.setMessageSeverity(ProductConfigRuleMessageSeverity.INFO);
		sourceRule.setMessageForCstic(CHARACTERISTIC);
		runtimeRule.setRuleType(RuleType.DEFAULT);
		classUnderTest.onPrepare(runtimeRule, null);
		assertNull("no mapping should happen", runtimeRule.getMessageSeverity());
		assertNull("no mapping should happen", runtimeRule.getMessageForCstic());
	}

	@Test
	public void testOnPrepare() throws InterceptorException
	{
		runtimeRule.setCode("123");
		given(mockedRuleDao.findRuleByCode("123")).willReturn(sourceRule);
		sourceRule.setMessageSeverity(ProductConfigRuleMessageSeverity.INFO);
		sourceRule.setMessageForCstic(CHARACTERISTIC);
		runtimeRule.setRuleType(RuleType.PRODUCTCONFIG);
		classUnderTest.onPrepare(runtimeRule, null);
		assertEquals(ProductConfigRuleMessageSeverity.INFO, runtimeRule.getMessageSeverity());
		assertEquals(CHARACTERISTIC, runtimeRule.getMessageForCstic());
	}


	/**
	 * rule will be null if you archive a rule, as the compiler is not involved in this
	 */
	@Test
	public void testOnPrepare_nullRule() throws InterceptorException
	{
		sourceRule.setMessageSeverity(ProductConfigRuleMessageSeverity.INFO);
		sourceRule.setMessageForCstic(CHARACTERISTIC);
		runtimeRule.setRuleType(RuleType.PRODUCTCONFIG);
		classUnderTest.onPrepare(runtimeRule, null);
		assertNull("no mapping should happen", runtimeRule.getMessageSeverity());
		assertNull("no mapping should happen", runtimeRule.getMessageForCstic());
	}
}

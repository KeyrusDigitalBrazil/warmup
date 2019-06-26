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
package de.hybris.platform.sap.productconfig.rules.compiler.processors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleIr;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrTypeCondition;
import de.hybris.platform.ruleengineservices.rao.RuleEngineResultRAO;
import de.hybris.platform.sap.productconfig.rules.model.ProductConfigSourceRuleModel;
import de.hybris.platform.sap.productconfig.rules.rao.BaseStoreRAO;
import de.hybris.platform.sap.productconfig.rules.rao.ProductConfigProcessStepRAO;
import de.hybris.platform.store.BaseStoreModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductConfigRuleIrProcessorTest
{
	public static final String RESULT_RAO_VARIABLE_ = "resultRaoVariable";
	public static final String PROCESS_STEP_RAO_VARIABLE_ = "processStepRaoVariable";
	public static final String BASE_STORE_RAO_VARIABLE_ = "baseStoreRaoVariable";

	public static final String BASE_STORE_1 = "powertools";
	public static final String BASE_STORE_2 = "electronics";

	private ProductConfigRuleIrProcessor classUnderTest;
	private RuleIr ruleIr;
	private ProductConfigSourceRuleModel sourceRule;

	@Mock
	private RuleCompilerContext context;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		ruleIr = new RuleIr();
		ruleIr.setConditions(new ArrayList<RuleIrCondition>());
		classUnderTest = new ProductConfigRuleIrProcessor();

		sourceRule = new ProductConfigSourceRuleModel();

		final Set<BaseStoreModel> baseStores = new HashSet<BaseStoreModel>();
		final BaseStoreModel baseStore1 = new BaseStoreModel();
		baseStore1.setUid(BASE_STORE_1);
		baseStores.add(baseStore1);
		final BaseStoreModel baseStore2 = new BaseStoreModel();
		baseStore2.setUid(BASE_STORE_2);
		baseStores.add(baseStore2);

		sourceRule.setBaseStores(baseStores);

		given(context.getRule()).willReturn(sourceRule);
		given(context.generateVariable(RuleEngineResultRAO.class)).willReturn(RESULT_RAO_VARIABLE_);
		given(context.generateVariable(ProductConfigProcessStepRAO.class)).willReturn(PROCESS_STEP_RAO_VARIABLE_);
		given(context.generateVariable(BaseStoreRAO.class)).willReturn(BASE_STORE_RAO_VARIABLE_);
	}

	@Test
	public void testProcess() throws Exception
	{
		classUnderTest.process(context, ruleIr);

		final RuleIrTypeCondition resultCondition = (RuleIrTypeCondition) ruleIr.getConditions().get(0);
		final RuleIrTypeCondition processStepCondition = (RuleIrTypeCondition) ruleIr.getConditions().get(1);
		final RuleIrAttributeCondition baseStoreCondition = (RuleIrAttributeCondition) ruleIr.getConditions().get(2);

		assertEquals(RESULT_RAO_VARIABLE_, resultCondition.getVariable());
		assertEquals(PROCESS_STEP_RAO_VARIABLE_, processStepCondition.getVariable());

		assertEquals(BASE_STORE_RAO_VARIABLE_, baseStoreCondition.getVariable());
		final List<String> baseStoreUids = (List<String>) baseStoreCondition.getValue();
		assertEquals(2, baseStoreUids.size());
		assertTrue(baseStoreUids.contains(BASE_STORE_1));
		assertTrue(baseStoreUids.contains(BASE_STORE_2));
	}
}

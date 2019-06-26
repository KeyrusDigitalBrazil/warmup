/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.ruleengineservices.rule.interceptors;

import static org.hamcrest.Matchers.instanceOf;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.ruleengineservices.model.SourceRuleModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@IntegrationTest
public class SourceRuleValidatorTest extends ServicelayerTransactionalTest
{
	public static final String RULE_CODE = "rule_code";
	public static final String RULE_NAME = "rule_name";

	public static final String VALID_CONDITIONS_JSON = "[{\"definitionId\":\"rcd\",\"parameters\":{\"cond_param\":{\"value\":null}}}]";
	public static final String VALID_ACTIONS_JSON = "[{\"definitionId\":\"rad\",\"parameters\":{\"action_param\":{\"value\":null}}}]";

	public static final String INVALID_CONDITIONS_JSON = "[{\"definitionId\":\"rcd\",\"parameters\":{\"cond_parassms\":\"null\"}}]";
	public static final String INVALID_ACTIONS_JSON = "[{\"definitionId\":\"radss\"}]";

	@Rule
	public ExpectedException expectedException = ExpectedException.none(); //NOPMD

	@Resource
	private ModelService modelService;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/ruleengineservices/test/rule/sourceRuleValidatorTest.impex", "utf-8");
	}

	@Test
	public void ruleIsValid()
	{
		// given
		final SourceRuleModel sourceRule = modelService.create(SourceRuleModel.class);
		sourceRule.setCode(RULE_CODE);
		sourceRule.setName(RULE_NAME);
		sourceRule.setConditions(VALID_CONDITIONS_JSON);
		sourceRule.setActions(VALID_ACTIONS_JSON);

		// when
		modelService.save(sourceRule);
	}

	@Test
	public void ruleHasInvalidConditions()
	{
		// given
		final SourceRuleModel sourceRule = modelService.create(SourceRuleModel.class);
		sourceRule.setCode(RULE_CODE);
		sourceRule.setName(RULE_NAME);
		sourceRule.setConditions(INVALID_CONDITIONS_JSON);
		sourceRule.setActions(VALID_ACTIONS_JSON);

		// expect
		expectedException.expect(ModelSavingException.class);
		expectedException.expectCause(instanceOf(InterceptorException.class));

		// when
		modelService.save(sourceRule);
	}

	@Test
	public void ruleHasInvalidActions()
	{
		// given
		final SourceRuleModel sourceRule = modelService.create(SourceRuleModel.class);
		sourceRule.setCode(RULE_CODE);
		sourceRule.setName(RULE_NAME);
		sourceRule.setConditions(VALID_CONDITIONS_JSON);
		sourceRule.setActions(INVALID_ACTIONS_JSON);

		// expect
		expectedException.expect(ModelSavingException.class);
		expectedException.expectCause(instanceOf(InterceptorException.class));

		// when
		modelService.save(sourceRule);
	}
}
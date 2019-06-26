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
package de.hybris.platform.ruleengineservices.setup.tasks.impl;

import static com.google.common.collect.Lists.newArrayList;
import static de.hybris.platform.ruleengineservices.constants.RuleEngineServicesConstants.DEFAULT_RULE_VERSION;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.ruleengineservices.model.AbstractRuleModel;
import de.hybris.platform.ruleengineservices.rule.dao.RuleDao;
import de.hybris.platform.servicelayer.model.ModelService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class UpdateSourceRuleVersionTaskTest
{
	@InjectMocks
	private UpdateSourceRuleVersionTask task;
	@Mock
	private ModelService modelService;
	@Mock
	private RuleDao ruleDao;
	@Mock
	private AbstractRuleModel rule1;
	@Mock
	private AbstractRuleModel rule2;
	@Mock
	private AbstractRuleModel rule3;
	@Mock
	private SystemSetupContext context;

	@Test
	public void shouldUpdateAllRulesVersionsToDefaultValue() throws Exception
	{
		//given
		given(rule1.getVersion()).willReturn(null);
		given(rule2.getVersion()).willReturn(null);
		given(rule3.getVersion()).willReturn(Long.valueOf(1L));
		given(ruleDao.findAllRules()).willReturn(newArrayList(rule1, rule2, rule3));
		//when
		task.execute(context);
		//then
		verify(rule1).setVersion(DEFAULT_RULE_VERSION);
		verify(rule2).setVersion(DEFAULT_RULE_VERSION);
		verify(rule3, never()).setVersion(DEFAULT_RULE_VERSION);

	}
}

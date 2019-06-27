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
package de.hybris.platform.ruleengineservices.compiler.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.campaigns.model.CampaignModel;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleIr;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeOperator;
import de.hybris.platform.ruleengineservices.model.AbstractRuleModel;
import de.hybris.platform.ruleengineservices.model.SourceRuleModel;
import de.hybris.platform.ruleengineservices.rao.CampaignRAO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.fest.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;



@UnitTest
public class DefaultSourceRuleIrProcessorTest
{
	private static final String CAMPAIGN_1_CODE = "campaign1";
	private static final String CAMPAIGN_2_CODE = "campaign2";
	private static final String VARIABLE_NAME = "v1";

	@InjectMocks
	private DefaultSourceRuleIrProcessor processor;

	@Mock
	private RuleCompilerContext context;

	@Mock
	private RuleIr ruleIr;

	@Mock
	private AbstractRuleModel abstractRule;

	@Mock
	private SourceRuleModel sourceRule;

	@Mock
	private CampaignModel campaign1;

	@Mock
	private CampaignModel campaign2;

	private Set<CampaignModel> campaigns;
	private Collection<String> campaignCodes;

	@Before
	public void setUp()
	{
		initMocks(this);
		campaigns = Collections.set(campaign1, campaign2);
		campaignCodes = Arrays.asList(new String[]
		{ CAMPAIGN_1_CODE, CAMPAIGN_2_CODE });
		when(context.getRule()).thenReturn(sourceRule);
		when(context.generateVariable(CampaignRAO.class)).thenReturn(VARIABLE_NAME);
		when(sourceRule.getCampaigns()).thenReturn(campaigns);
		when(campaign1.getCode()).thenReturn(CAMPAIGN_1_CODE);
		when(campaign2.getCode()).thenReturn(CAMPAIGN_2_CODE);
		when(ruleIr.getConditions()).thenReturn(new ArrayList<>());
	}

	@Test
	public void testIgnoreNonSourceRules()
	{
		when(context.getRule()).thenReturn(abstractRule);

		processor.process(context, ruleIr);

		verify(context).getRule();
		verifyNoMoreInteractions(ruleIr);
		verifyNoMoreInteractions(context);
	}

	@Test
	public void testProcessSourceRules()
	{
		processor.process(context, ruleIr);

		assertTrue(ruleIr.getConditions().size() == 1);
		assertTrue(ruleIr.getConditions().get(0) instanceof RuleIrAttributeCondition);
		final RuleIrAttributeCondition condition = (RuleIrAttributeCondition) ruleIr.getConditions().get(0);
		assertEquals("code", condition.getAttribute());
		assertEquals(RuleIrAttributeOperator.IN, condition.getOperator());
		assertEquals(campaignCodes, condition.getValue());
		assertEquals(VARIABLE_NAME, condition.getVariable());
	}

}

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
package de.hybris.platform.sap.productconfig.rules.rule.evaluation.impl;

import static de.hybris.platform.ruleengine.constants.RuleEngineConstants.RULEMETADATA_RULECODE;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.when;

import de.hybris.platform.ruleengineservices.calculation.AbstractRuleEngineTest;
import de.hybris.platform.ruleengineservices.rao.RuleEngineResultRAO;
import de.hybris.platform.ruleengineservices.rule.evaluation.RuleActionContext;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;

import org.drools.core.WorkingMemory;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.spi.KnowledgeHelper;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.kie.api.runtime.ObjectFilter;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("squid:S2187")
public class AbstractProductConfigRAOActionTest extends AbstractRuleEngineTest
{
	private RuleEngineResultRAO result;

	@Mock
	private ConfigurationService configurationService;
	@Mock
	private RuleActionContext context;
	@Mock
	private WorkingMemory workingMemory;
	@Mock
	private RuleImpl rule;
	@Mock
	private Map<String, Object> metaData;
	@Mock
	private KnowledgeHelper delegateContext;

	@Before
	public void abstractSetUp()
	{
		result = new RuleEngineResultRAO();
		result.setActions(new LinkedHashSet<>());

		when(context.getRuleMetadata()).thenReturn(metaData);
		when(context.getRuleEngineResultRao()).thenReturn(result);
		when(context.getDelegate()).thenReturn(delegateContext);
		when(delegateContext.getWorkingMemory()).thenReturn(workingMemory);
		when(workingMemory.getFactHandles((ObjectFilter) notNull())).thenReturn(Collections.emptyList());
		when(metaData.get(RULEMETADATA_RULECODE)).thenReturn("notNullValue");
	}

	protected RuleEngineResultRAO getResult()
	{
		return result;
	}

	protected RuleActionContext getContext()
	{
		return context;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}
}


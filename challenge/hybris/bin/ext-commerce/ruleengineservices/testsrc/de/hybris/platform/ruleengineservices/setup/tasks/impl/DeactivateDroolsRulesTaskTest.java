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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DeactivateDroolsRulesTaskTest
{
	@InjectMocks
	private DeactivateDroolsRulesTask deactivateDroolsRulesTask;
	@Mock
	private FlexibleSearchService flexibleSearchService;
	@Mock
	private ModelService modelService;
	@Mock
	private SearchResult<AbstractRuleEngineRuleModel> searchResults;
	@Mock
	private AbstractRuleEngineRuleModel activeEngineRule;
	@Mock
	private AbstractRuleEngineRuleModel inactiveEngineRule;
	@Mock
	private SystemSetupContext context;

	@Before
	public void setUp() throws Exception
	{
		given(flexibleSearchService.<AbstractRuleEngineRuleModel>search(DeactivateDroolsRulesTask.FIND_QUALIFYING_RULES)).willReturn(searchResults);
		given(activeEngineRule.getActive()).willReturn(Boolean.TRUE);
		given(activeEngineRule.getCurrentVersion()).willReturn(Boolean.TRUE);
		given(inactiveEngineRule.getActive()).willReturn(Boolean.FALSE);
		given(inactiveEngineRule.getCurrentVersion()).willReturn(Boolean.TRUE);
	}

	@Test
	public void shouldDeactiveAllActiveEngineRulesWhichHasCorrespondingSourceRule() throws Exception
	{
		//given
		given(searchResults.getResult()).willReturn(newArrayList(activeEngineRule));
		//when
		deactivateDroolsRulesTask.execute(context);
		//then
		verify(activeEngineRule).setActive(Boolean.FALSE);
	}

	@Test
	public void shouldLeaveNonActiveEngineRulesUntouched() throws Exception
	{
		//given
		given(searchResults.getResult()).willReturn(newArrayList(inactiveEngineRule));
		//when
		deactivateDroolsRulesTask.execute(context);
		//then
		verify(activeEngineRule,never()).setActive(Boolean.FALSE);
	}
}

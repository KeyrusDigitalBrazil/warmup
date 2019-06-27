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
package de.hybris.platform.ruleengineservices.dynamic;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengine.model.AbstractRulesModuleModel;
import de.hybris.platform.ruleengine.strategies.RulesModuleResolver;
import de.hybris.platform.ruleengineservices.enums.RuleStatus;
import de.hybris.platform.ruleengineservices.model.AbstractRuleModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * @Deprecated since 18.08
 */
@Deprecated
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RuleDeploymentsAttributeHandlerTest
{
	@InjectMocks
	private RuleDeploymentsAttributeHandler handler = new RuleDeploymentsAttributeHandler();

	@Mock
	private RulesModuleResolver rulesModuleResolver;

	@Mock
	private AbstractRuleModel ruleModel;

	@Test(expected = UnsupportedOperationException.class)
	public void shouldThrowUnsupportedOperationException()
	{
		handler.set(ruleModel, emptyList());
	}

	@Test
	public void shouldPropagateCallToRulesModuleResolver()
	{
		final List<AbstractRulesModuleModel> rulesModules = asList(mock(AbstractRulesModuleModel.class),
				mock(AbstractRulesModuleModel.class));
		given(rulesModuleResolver.lookupForRulesModules(ruleModel)).willReturn(rulesModules);
		given(ruleModel.getStatus()).willReturn(RuleStatus.PUBLISHED);
		//when
		final List<AbstractRulesModuleModel> result = handler.get(ruleModel);
		//then
		assertThat(result).isSameAs(rulesModules);
		verify(rulesModuleResolver).lookupForRulesModules(ruleModel);
	}


	@Test
	public void cannotPropagateCallToRulesModuleResolverIfRuleIsNotPublished()
	{
		final List<AbstractRulesModuleModel> rulesModules = asList(mock(AbstractRulesModuleModel.class),
				mock(AbstractRulesModuleModel.class));
		given(rulesModuleResolver.lookupForRulesModules(ruleModel)).willReturn(rulesModules);
		given(ruleModel.getStatus()).willReturn(RuleStatus.ARCHIVED);
		//when
		final List<AbstractRulesModuleModel> result = handler.get(ruleModel);
		//then
		assertThat(result).isEmpty();
		verify(rulesModuleResolver,never()).lookupForRulesModules(ruleModel);
	}

}

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
package de.hybris.platform.ruleengineservices.versioning.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.campaigns.model.CampaignModel;
import de.hybris.platform.ruleengineservices.enums.RuleStatus;
import de.hybris.platform.ruleengineservices.model.SourceRuleModel;
import de.hybris.platform.servicelayer.internal.model.impl.DefaultModelServiceInterceptorContext;
import de.hybris.platform.servicelayer.internal.model.impl.RegisteredElements;

import java.util.Collections;
import java.util.Set;

import org.assertj.core.util.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CampaignHistoricalRuleContentProviderUnitTest extends HistoricalRuleVersionTestHelper
{

	@InjectMocks
	private CampaignHistoricalRuleContentProvider contentProvider;
	@Mock
	private DefaultModelServiceInterceptorContext interceptorContext;

	private Set<Object> initialElements = Sets.newHashSet();

	@Before
	public void setUp()
	{
		contentProvider = spy(contentProvider);
		initialElements.add(createSourceRuleModel(RULE_CODE, RuleStatus.PUBLISHED));
		final RegisteredElements registeredElements = new RegisteredElements(initialElements);
		when(interceptorContext.getInitialElements()).thenReturn(registeredElements);
	}

	/**
	 * If the campaign is not involved into modification, just don't do any campaign-related properties alignment
	 */
	@Test
	public void copyOriginalValuesIntoHistoricalVersionCampaignNotInvolved()
	{
		final SourceRuleModel sourceRule = (SourceRuleModel) createSourceRuleModel(RULE_CODE, RuleStatus.PUBLISHED);
		final SourceRuleModel historicalSourceRule = (SourceRuleModel) createSourceRuleModel(RULE_CODE, RuleStatus.UNPUBLISHED);

		contentProvider.copyOriginalValuesIntoHistoricalVersion(sourceRule, historicalSourceRule, interceptorContext);
		verify(contentProvider, times(0)).substituteAssociatedSourceRule(any(CampaignModel.class), any(SourceRuleModel.class), any(SourceRuleModel.class));
	}

	/**
	 * If the involved source rule was never published just skip any property copy
	 */
	@Test
	public void copyOriginalValuesIntoHistoricalVersionSourceRuleUnpublished()
	{
		final SourceRuleModel sourceRule = (SourceRuleModel) createSourceRuleModel(RULE_CODE, RuleStatus.UNPUBLISHED);
		final SourceRuleModel historicalSourceRule = (SourceRuleModel) createSourceRuleModel(RULE_CODE, RuleStatus.UNPUBLISHED);

		contentProvider.copyOriginalValuesIntoHistoricalVersion(sourceRule, historicalSourceRule, interceptorContext);
		verify(interceptorContext, times(0)).getInitialElements();
	}

	@Test
	public void copyOriginalValuesIntoHistoricalVersionSourceRuleChangeVersion()
	{
		final CampaignModel campaign = new CampaignModel();
		initialElements.add(campaign);
		final RegisteredElements registeredElements = new RegisteredElements(initialElements);
		when(interceptorContext.getInitialElements()).thenReturn(registeredElements);

		final SourceRuleModel sourceRule = (SourceRuleModel) createSourceRuleModelWithCampaign(RULE_CODE, RuleStatus.PUBLISHED, campaign);
		campaign.setSourceRules(Sets.newHashSet(Collections.singleton(sourceRule)));
		final SourceRuleModel historicalSourceRule = (SourceRuleModel) createSourceRuleModel(RULE_CODE, RuleStatus.UNPUBLISHED);

		contentProvider.copyOriginalValuesIntoHistoricalVersion(sourceRule, historicalSourceRule, interceptorContext);
		verify(contentProvider, times(1)).substituteAssociatedSourceRule(campaign, sourceRule, historicalSourceRule);
		assertThat(campaign.getSourceRules()).containsExactly(historicalSourceRule);
	}

}

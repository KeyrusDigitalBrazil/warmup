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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.campaigns.model.CampaignModel;
import de.hybris.platform.ruleengineservices.enums.RuleStatus;
import de.hybris.platform.ruleengineservices.model.AbstractRuleModel;
import de.hybris.platform.ruleengineservices.model.SourceRuleModel;
import de.hybris.platform.servicelayer.model.ItemModelContext;

import java.util.Set;

import org.assertj.core.util.Sets;

import com.google.common.collect.ImmutableSet;


class HistoricalRuleVersionTestHelper  // NOSONAR
{
	static final String RULE_CODE = "TEST_RULE_CODE";
	static final String ASSOCIATED_RULE_CODE = "ASSOCIATED_TEST_RULE_CODE";

	AbstractRuleModel createSourceRuleModel(final String ruleCode, final RuleStatus status)
	{
		final AbstractRuleModel rule = mock(SourceRuleModel.class);
		when(rule.getStatus()).thenReturn(status);
		when(rule.getCode()).thenReturn(ruleCode);
		final ItemModelContext ruleModelContext = mock(ItemModelContext.class);
		when(rule.getItemModelContext()).thenReturn(ruleModelContext);

		final Set<CampaignModel> origCampaigns = Sets.newHashSet();
		when(ruleModelContext.getOriginalValue(SourceRuleModel.CAMPAIGNS)).thenReturn(origCampaigns);
		return rule;
	}

	AbstractRuleModel createSourceRuleModelWithCampaign(final String ruleCode, final RuleStatus status,
			final CampaignModel campaign)
	{
		final AbstractRuleModel rule = mock(SourceRuleModel.class);
		when(rule.getStatus()).thenReturn(status);
		when(rule.getCode()).thenReturn(ruleCode);
		final ItemModelContext ruleModelContext = mock(ItemModelContext.class);
		when(rule.getItemModelContext()).thenReturn(ruleModelContext);

		final Set<CampaignModel> origCampaigns = ImmutableSet.of(campaign);
		when(ruleModelContext.getOriginalValue(SourceRuleModel.CAMPAIGNS)).thenReturn(origCampaigns);
		return rule;
	}
}

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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.campaigns.model.CampaignModel;
import de.hybris.platform.ruleengineservices.enums.RuleStatus;
import de.hybris.platform.ruleengineservices.model.AbstractRuleModel;
import de.hybris.platform.ruleengineservices.model.SourceRuleModel;
import de.hybris.platform.servicelayer.internal.model.impl.DefaultModelServiceInterceptorContext;
import de.hybris.platform.servicelayer.internal.model.impl.RegisteredElements;

import java.util.Set;

import org.assertj.core.util.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableSet;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CampaignSourceRuleModelHistoricalContentCreatorUnitTest extends HistoricalRuleVersionTestHelper
{

	@InjectMocks
	private CampaignSourceRuleModelHistoricalContentCreator contentCreator;
	@Mock
	private DefaultModelServiceInterceptorContext ctx;
	private Set<Object> initialElements = Sets.newHashSet();

	@Before
	public void setUp()
	{
		initialElements.add(createSourceRuleModel(RULE_CODE, RuleStatus.PUBLISHED));
		final RegisteredElements registeredElements = new RegisteredElements(initialElements);
		when(ctx.getInitialElements()).thenReturn(registeredElements);
	}

	/**
	 * No campaign model was registered as part of initialElements
	 */
	@Test
	public void associatedTypesChangedNoCampaignModelRegistered()
	{
		final AbstractRuleModel ruleModule = createSourceRuleModel(RULE_CODE, RuleStatus.PUBLISHED);

		final boolean associatedTypesChanged = contentCreator.associatedTypesChanged(ruleModule, ctx);
		verify(ctx, times(1)).getInitialElements();
		assertThat(associatedTypesChanged).isFalse();
	}

	/**
	 * Campaign model was registered as part of initialElements, but had an empty source rules set
	 */
	@Test
	public void associatedTypesChangedCampaignModelWasAdded()
	{
		final AbstractRuleModel ruleModule = createSourceRuleModel(RULE_CODE, RuleStatus.PUBLISHED);

		final CampaignModel campaign = new CampaignModel();
		campaign.setSourceRules(ImmutableSet.of((SourceRuleModel) ruleModule));
		initialElements.add(campaign);
		final RegisteredElements registeredElements = new RegisteredElements(initialElements);
		when(ctx.getInitialElements()).thenReturn(registeredElements);

		final boolean associatedTypesChanged = contentCreator.associatedTypesChanged(ruleModule, ctx);
		verify(ctx, times(1)).getInitialElements();
		assertThat(associatedTypesChanged).isTrue();
	}

	/**
	 * Campaign model was registered as part of initialElements, had existing source rules set
	 */
	@Test
	public void associatedTypesChangedCampaignModelHadAssociatedRules()
	{
		final AbstractRuleModel ruleModule = createSourceRuleModel(RULE_CODE, RuleStatus.PUBLISHED);

		final CampaignModel campaign = new CampaignModel();
		campaign.setSourceRules(
				ImmutableSet.of((SourceRuleModel) ruleModule,
						(SourceRuleModel) createSourceRuleModel(ASSOCIATED_RULE_CODE, RuleStatus.UNPUBLISHED)));
		initialElements.add(campaign);
		final RegisteredElements registeredElements = new RegisteredElements(initialElements);
		when(ctx.getInitialElements()).thenReturn(registeredElements);

		final boolean associatedTypesChanged = contentCreator.associatedTypesChanged(ruleModule, ctx);
		verify(ctx, times(1)).getInitialElements();
		assertThat(associatedTypesChanged).isTrue();
	}

	/**
	 * Rules associated campaign was removed, but rule was published
	 */
	@Test
	public void associatedTypesChangedRuleAssociatedCampaignWasRemovedRuleWasPublished()
	{
		final CampaignModel campaign = new CampaignModel();
		final AbstractRuleModel ruleModule = createSourceRuleModelWithCampaign(RULE_CODE, RuleStatus.PUBLISHED, campaign);

		campaign.setSourceRules(
				ImmutableSet.of((SourceRuleModel) createSourceRuleModel(ASSOCIATED_RULE_CODE, RuleStatus.UNPUBLISHED)));
		initialElements.add(campaign);
		final RegisteredElements registeredElements = new RegisteredElements(initialElements);
		when(ctx.getInitialElements()).thenReturn(registeredElements);

		final boolean associatedTypesChanged = contentCreator.associatedTypesChanged(ruleModule, ctx);
		verify(ctx, times(1)).getInitialElements();
		verify(ruleModule, times(1)).getItemModelContext();

		assertThat(associatedTypesChanged).isTrue();
	}

	/**
	 * Rule was never published
	 */
	@Test
	public void associatedTypesChangedRuleWasNeverPublished()
	{
		final AbstractRuleModel ruleModule = createSourceRuleModel(RULE_CODE, RuleStatus.UNPUBLISHED);

		final RegisteredElements registeredElements = new RegisteredElements(initialElements);
		when(ctx.getInitialElements()).thenReturn(registeredElements);

		final boolean associatedTypesChanged = contentCreator.associatedTypesChanged(ruleModule, ctx);
		verify(ctx, times(0)).getInitialElements();

		assertThat(associatedTypesChanged).isFalse();
	}

}

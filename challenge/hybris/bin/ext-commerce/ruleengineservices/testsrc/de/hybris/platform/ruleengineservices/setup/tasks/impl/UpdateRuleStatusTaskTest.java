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
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.verify;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.ruleengineservices.enums.RuleStatus;
import de.hybris.platform.ruleengineservices.model.SourceRuleModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class UpdateRuleStatusTaskTest
{
	@InjectMocks
	private UpdateRuleStatusTask task;
	@Mock
	private FlexibleSearchService flexibleSearchService;
	@Mock
	private ModelService modelService;
	@Mock
	private SourceRuleModel publishedSourceRule;
	@Mock
	private SourceRuleModel modifiedSourceRule;
	@Mock
	private SearchResult<SourceRuleModel> publishedSearchResults;
	@Mock
	private SearchResult<SourceRuleModel> modifiedSearchResults;
	@Mock
	private SystemSetupContext context;
	@Before
	public void setUp() throws Exception
	{
		given(flexibleSearchService.<SourceRuleModel> search(Mockito.eq(UpdateRuleStatusTask.SELECT_RULES_BY_STATUS),
				anyMap())).willReturn(publishedSearchResults, modifiedSearchResults);

		given(publishedSourceRule.getStatus()).willReturn(RuleStatus.PUBLISHED);
		given(modifiedSourceRule.getStatus()).willReturn(RuleStatus.MODIFIED);
	}

	@Test
	public void shouldChangeSourceRuleStatusFromPublishedToInactive() throws Exception
	{
		//given
		given(publishedSearchResults.getResult()).willReturn(newArrayList(publishedSourceRule));
		//when
		task.execute(context);
		//then
		verify(publishedSourceRule).setStatus(RuleStatus.INACTIVE);
	}

	@Test
	public void shouldChangeSourceRuleStatusFromModifiedToUnpublished() throws Exception
	{
		//given
		given(modifiedSearchResults.getResult()).willReturn(newArrayList(modifiedSourceRule));
		//when
		task.execute(context);
		//then
		verify(modifiedSourceRule).setStatus(RuleStatus.UNPUBLISHED);
	}

}

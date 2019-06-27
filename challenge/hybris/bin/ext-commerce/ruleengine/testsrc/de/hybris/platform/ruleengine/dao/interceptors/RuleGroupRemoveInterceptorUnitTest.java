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
package de.hybris.platform.ruleengine.dao.interceptors;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.model.RuleGroupModel;
import de.hybris.platform.ruleengineservices.model.SourceRuleModel;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RuleGroupRemoveInterceptorUnitTest
{

	@InjectMocks
	private RuleGroupRemoveInterceptor ruleGroupRemoveInterceptor;
	@Mock
	private L10NService l10NService;
	@Mock
	private RuleGroupModel removableRuleGroup;
	@Mock
	private RuleGroupModel nonRemovableRuleGroup;
	@Mock
	private InterceptorContext context;

	@Before
	public void setUp()
	{
		when(l10NService.getLocalizedString(eq("error.rulegroup.cantremovehasrules"), any(Object[].class))).thenReturn(
				"err message");
		when(removableRuleGroup.getRules()).thenReturn(Collections.emptySet());
		when(nonRemovableRuleGroup.getRules()).thenReturn(Collections.singleton(new SourceRuleModel()));
	}

	@Test
	public void testValidRemove() throws InterceptorException
	{
		ruleGroupRemoveInterceptor.onRemove(removableRuleGroup, context);
	}

	@Test(expected = InterceptorException.class)
	public void testInvalidRemove() throws InterceptorException
	{
		ruleGroupRemoveInterceptor.onRemove(nonRemovableRuleGroup, context);
	}

}

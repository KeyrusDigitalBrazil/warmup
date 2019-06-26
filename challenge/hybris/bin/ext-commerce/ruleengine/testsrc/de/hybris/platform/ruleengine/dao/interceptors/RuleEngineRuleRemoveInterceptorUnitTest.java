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

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengine.dao.EngineRuleDao;
import de.hybris.platform.ruleengine.model.DroolsKIEBaseModel;
import de.hybris.platform.ruleengine.model.DroolsKIEModuleModel;
import de.hybris.platform.ruleengine.model.DroolsRuleModel;
import de.hybris.platform.ruleengine.versioning.impl.RuleEngineRuleModelRemoveHandler;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RuleEngineRuleRemoveInterceptorUnitTest
{

	private final static String RULE_CODE = "test_code";
	private final static String MODULE_NAME = "MODULE_NAME";

	private final static String UUID_STRING = UUID.randomUUID().toString();

	@InjectMocks
	private RuleEngineRuleModelRemoveHandler ruleEngineRemoveHandler;
	@InjectMocks
	private RuleEngineRuleRemoveInterceptor removeInterceptor;
	@Mock
	private EngineRuleDao engineRuleDao;

	private DroolsRuleModel model;
	private DroolsRuleModel previousVersionModel;
	@Mock
	private InterceptorContext context;
	@Mock
	private DroolsKIEModuleModel rulesModule;
	@Mock
	private DroolsKIEBaseModel rulesBase;

	@Before
	public void setUp()
	{
		ruleEngineRemoveHandler = spy(ruleEngineRemoveHandler);
		removeInterceptor.setRuleModelRemoveHandler(ruleEngineRemoveHandler);

		model = new DroolsRuleModel();
		model.setCode(RULE_CODE);
		model.setUuid(UUID_STRING);
		model.setActive(Boolean.TRUE);
		model.setVersion(1L);
		model.setCurrentVersion(Boolean.TRUE);
		model.setKieBase(rulesBase);

		previousVersionModel = new DroolsRuleModel();
		previousVersionModel.setCode(RULE_CODE);
		previousVersionModel.setUuid(UUID_STRING);
		previousVersionModel.setVersion(0l);
		previousVersionModel.setCurrentVersion(Boolean.FALSE);

		when(rulesBase.getKieModule()).thenReturn(rulesModule);
		when(rulesModule.getName()).thenReturn(MODULE_NAME);

		when(engineRuleDao.getRuleByCodeAndMaxVersion(RULE_CODE, MODULE_NAME, 0l)).thenReturn(previousVersionModel);
	}

	@Test
	public void testOnRemove() throws Exception
	{
		removeInterceptor.onRemove(model, context);
		verify(ruleEngineRemoveHandler, times(1)).handleOnRemove(model, context);
		assertThat(previousVersionModel.getCurrentVersion()).isTrue();
	}

	@Test(expected = InterceptorException.class)
	public void testOnRemoveCurrentVersionFalse() throws Exception
	{
		model.setCurrentVersion(Boolean.FALSE);
		removeInterceptor.onRemove(model, context);
	}

	@Test
	public void testOnRemoveNoPreviousVersionModel() throws Exception
	{
		when(engineRuleDao.getRuleByCodeAndMaxVersion(RULE_CODE, MODULE_NAME, 0L)).thenReturn(null);
		removeInterceptor.onRemove(model, context);
		verify(ruleEngineRemoveHandler, times(1)).handleOnRemove(model, context);
		assertThat(previousVersionModel.getCurrentVersion()).isFalse();
	}

}

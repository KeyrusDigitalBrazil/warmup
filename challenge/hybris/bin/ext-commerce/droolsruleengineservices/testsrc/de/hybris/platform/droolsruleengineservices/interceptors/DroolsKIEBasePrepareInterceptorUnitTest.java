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
package de.hybris.platform.droolsruleengineservices.interceptors;

import static java.lang.Long.valueOf;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.droolsruleengineservices.versioning.impl.DroolsModuleVersioningService;
import de.hybris.platform.ruleengine.dao.EngineRuleDao;
import de.hybris.platform.ruleengine.model.DroolsKIEBaseModel;
import de.hybris.platform.ruleengine.model.DroolsKIEModuleModel;
import de.hybris.platform.ruleengine.model.DroolsRuleModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableSet;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DroolsKIEBasePrepareInterceptorUnitTest
{

	private static final Long DEFAULT_MODULE_VERSION = -1L;

	@InjectMocks
	private DroolsKIEBasePrepareInterceptor prepareInterceptor;
	@InjectMocks
	private DroolsModuleVersioningService moduleVersioningService;
	@Mock
	private EngineRuleDao engineRuleDao;
	@Mock
	private InterceptorContext context;
	private DroolsKIEBaseModel baseModel;
	private DroolsKIEModuleModel moduleModel;

	@Before
	public void setUp()
	{
		prepareInterceptor.setModuleVersioningService(moduleVersioningService);
		moduleModel = new DroolsKIEModuleModel();
		moduleModel.setVersion(DEFAULT_MODULE_VERSION);
		baseModel = new DroolsKIEBaseModel();
		baseModel.setKieModule(moduleModel);
	}

	@Test
	public void testOnPrepareKieModuleIsNull() throws InterceptorException
	{
		baseModel.setKieModule(null);
		prepareInterceptor = spy(prepareInterceptor);
		prepareInterceptor.onPrepare(baseModel, context);
		assertThat(moduleModel.getVersion()).isEqualTo(DEFAULT_MODULE_VERSION);
	}

	@Test
	public void testOnPrepareEmptyRulesSet() throws InterceptorException
	{
		baseModel.setRules(Collections.EMPTY_SET);
		prepareInterceptor = spy(prepareInterceptor);
		prepareInterceptor.onPrepare(baseModel, context);
		assertThat(moduleModel.getVersion()).isEqualTo(DEFAULT_MODULE_VERSION);
	}

	@Test
	public void testOnPrepareCurrentVersionIsBigger() throws InterceptorException
	{
		baseModel.setRules(ImmutableSet.of(getRule(0), getRule(1)));
		when(engineRuleDao.getCurrentRulesSnapshotVersion(moduleModel)).thenReturn(valueOf(2));
		prepareInterceptor = spy(prepareInterceptor);
		prepareInterceptor.onPrepare(baseModel, context);
		assertThat(moduleModel.getVersion()).isEqualTo(2);
	}

	@Test
	public void testOnPrepareCurrentVersionIsLess() throws InterceptorException
	{
		baseModel.setRules(ImmutableSet.of(getRule(0), getRule(3)));
		when(engineRuleDao.getCurrentRulesSnapshotVersion(moduleModel)).thenReturn(valueOf(2));
		prepareInterceptor = spy(prepareInterceptor);
		prepareInterceptor.onPrepare(baseModel, context);
		assertThat(moduleModel.getVersion()).isEqualTo(3);
	}

	@Test
	public void testOnPrepareModuleVersionIsLessThenCurrentRulesVersion() throws InterceptorException
	{
		baseModel.setRules(ImmutableSet.of(getRule(2)));
		moduleModel.setVersion(valueOf(1));
		when(engineRuleDao.getCurrentRulesSnapshotVersion(moduleModel)).thenReturn(valueOf(2));
		prepareInterceptor = spy(prepareInterceptor);
		prepareInterceptor.onPrepare(baseModel, context);
		assertThat(moduleModel.getVersion()).isEqualTo(2);
	}

	private DroolsRuleModel getRule(final long version)
	{
		final DroolsRuleModel ruleModel = new DroolsRuleModel();
		ruleModel.setVersion(version);
		return ruleModel;
	}

}

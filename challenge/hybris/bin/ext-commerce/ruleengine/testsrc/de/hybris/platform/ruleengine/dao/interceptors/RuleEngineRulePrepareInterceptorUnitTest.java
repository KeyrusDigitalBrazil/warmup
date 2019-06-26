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

import static java.lang.Boolean.valueOf;
import static java.lang.Long.valueOf;
import static java.util.Collections.singletonList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengine.RuleEngineService;
import de.hybris.platform.ruleengine.dao.EngineRuleDao;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengine.model.DroolsKIEBaseModel;
import de.hybris.platform.ruleengine.model.DroolsKIEModuleModel;
import de.hybris.platform.ruleengine.model.DroolsRuleModel;
import de.hybris.platform.ruleengine.versioning.ModuleVersioningService;
import de.hybris.platform.ruleengine.versioning.impl.RuleEngineHistoricalRuleContentProvider;
import de.hybris.platform.ruleengine.versioning.impl.RuleEngineRuleModelChecksumCalculator;
import de.hybris.platform.ruleengine.versioning.impl.RuleEngineRuleModelHistoricalContentCreator;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PersistenceOperation;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.UUID;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RuleEngineRulePrepareInterceptorUnitTest
{
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private final static String RULE_MODULE_NAME = "test_rule_module";

	private final static String RULE_CONTENT = "rule content";
	private final static String NEW_RULE_CONTENT = "new rule content";
	private final static String RULE_CHECKSUM = "RULE_CHECKSUM";
	private final static String NEW_RULE_CHECKSUM = "NEW_RULE_CHECKSUM";
	private final static Long RULE_VERSION = 1l;
	private final static Long NEW_RULE_VERSION = 10l;

	private final static String RULE_CODE = "test_code";
	private final static String UUID_STRING = UUID.randomUUID().toString();

	@InjectMocks
	private RuleEngineRuleModelHistoricalContentCreator historicalContentCreator;
	@InjectMocks
	private RuleEngineRulePrepareInterceptor prepareInterceptor;
	private MockRuleModelChecksumCalculator ruleModelChecksumCalculator;
	@Mock
	private EngineRuleDao engineRuleDao;

	private DroolsRuleModel model;
	private DroolsRuleModel historicalModel;
	@Mock
	private InterceptorContext context;
	@Mock
	private ModelService modelService;
	@Mock
	private DroolsKIEBaseModel kieBaseModel;
	@Mock
	private DroolsKIEModuleModel kieModuleModel;
	@Mock
	private RuleEngineService ruleEngineService;
	@Mock
	private ModuleVersioningService moduleVersioningService;
	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;

	@Before
	public void setUp()
	{

		final RuleEngineHistoricalRuleContentProvider ruleContentProvider = new RuleEngineHistoricalRuleContentProvider();

		historicalContentCreator.setHistoricalRuleContentProviders(singletonList(ruleContentProvider));

		ruleModelChecksumCalculator = spy(new MockRuleModelChecksumCalculator());
		prepareInterceptor.setRuleModelChecksumCalculator(ruleModelChecksumCalculator);
		prepareInterceptor.setHistoricalContentCreator(historicalContentCreator);

		model = new DroolsRuleModel();
		model.setCode(RULE_CODE);
		model.setUuid(UUID_STRING);
		model.setRuleContent(NEW_RULE_CONTENT);
		model.setActive(Boolean.TRUE);
		model.setKieBase(kieBaseModel);

		historicalModel = new DroolsRuleModel();
		historicalModel.setCode(RULE_CODE);
		historicalModel.setUuid(UUID_STRING);
		historicalModel.setKieBase(kieBaseModel);

		historicalModel = spy(historicalModel);
		doNothing().when(historicalModel).setMessageFired(Mockito.anyString());

		when(context.getModelService()).thenReturn(modelService);
		when(modelService.clone(model)).thenReturn(historicalModel);
		when(kieBaseModel.getKieModule()).thenReturn(kieModuleModel);
		when(kieModuleModel.getName()).thenReturn(RULE_MODULE_NAME);
		when(kieModuleModel.getVersion()).thenReturn(valueOf(-1));

		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getBoolean("ruleengine.rule.content.check.enabled", false)).thenReturn(false);
	}

	@Test
	public void testOnPrepareNewModelNullVersion() throws InterceptorException
	{
		when(valueOf(context.isNew(model))).thenReturn(Boolean.TRUE);

		prepareInterceptor.onPrepare(model, context);
		assertThat(model.getUuid()).isNotEmpty();
		assertThat(model.getVersion()).isNotNull().isEqualTo(0l);

		verify(modelService, times(0)).clone(model);
		verify(context, times(0)).registerElement(historicalModel);
	}

	@Test
	public void testOnPrepareNewModelNotNullVersion() throws InterceptorException
	{
		final Long version = 1l;
		model.setVersion(version);
		when(valueOf(context.isNew(model))).thenReturn(Boolean.TRUE);

		prepareInterceptor.onPrepare(model, context);
		assertThat(model.getUuid()).isNotEmpty();
		assertThat(model.getVersion()).isNotNull().isEqualTo(version);
	}

	@Test
	public void testOnPrepareUpdateModel() throws InterceptorException
	{
		defineRegularUpdateBehaviour();
		defineContextBehaviour();
		defineModelServiceBehaviour(true);

		prepareInterceptor.onPrepare(model, context);

		assertThat(model.getVersion()).isNotNull().isEqualTo(NEW_RULE_VERSION + 1);
		assertThat(model.getChecksum()).isEqualTo(NEW_RULE_CHECKSUM);
		assertThat(model.getRuleContent()).isNotEmpty().isEqualTo(NEW_RULE_CONTENT);
		assertThat(model.getKieBase()).isNotNull();
		assertThat(model.getActive()).isTrue();

		assertThat(historicalModel.getVersion()).isNotNull().isEqualTo(RULE_VERSION);
		assertThat(historicalModel.getChecksum()).isNotEmpty().isEqualTo(RULE_CHECKSUM);
		assertThat(historicalModel.getRuleContent()).isNotEmpty().isEqualTo(RULE_CONTENT);
		assertThat(historicalModel.getKieBase()).isNotNull();
		assertThat(historicalModel.getCurrentVersion()).isFalse();
	}

	@Test
	public void testOnPrepareUpdateModelEqualContent() throws InterceptorException
	{
		when(configuration.getBoolean("ruleengine.rule.content.check.enabled", false)).thenReturn(true);

		model.setRuleContent(RULE_CONTENT);
		historicalModel.setKieBase(null);

		defineRegularUpdateBehaviour();
		defineContextBehaviour();
		defineModelServiceBehaviour(true);

		prepareInterceptor.onPrepare(model, context);

		assertThat(model.getVersion()).isNotNull().isEqualTo(RULE_VERSION);
		assertThat(model.getChecksum()).isEqualTo(RULE_CHECKSUM);
		assertThat(model.getRuleContent()).isNotEmpty().isEqualTo(RULE_CONTENT);
		assertThat(model.getKieBase()).isNotNull();
		assertThat(model.getActive()).isTrue();

		verify(modelService, times(0)).clone(model);
		verify(context, times(0)).registerElement(historicalModel);

		assertThat(historicalModel.getVersion()).isNull();
		assertThat(historicalModel.getChecksum()).isNull();
		assertThat(historicalModel.getRuleContent()).isNull();
		assertThat(historicalModel.getKieBase()).isNull();
		assertThat(historicalModel.getActive()).isNull();
	}

	@Test
	public void testOnPrepareUpdateModelUpdateNotActive() throws InterceptorException
	{
		model.setActive(Boolean.FALSE);
		historicalModel.setKieBase(null);

		defineRegularUpdateBehaviour();
		defineContextBehaviour();
		defineModelServiceBehaviour(false);

		prepareInterceptor.onPrepare(model, context);

		assertThat(model.getVersion()).isNotNull().isEqualTo(RULE_VERSION);
		assertThat(model.getChecksum()).isEqualTo(NEW_RULE_CHECKSUM);
		assertThat(model.getRuleContent()).isNotEmpty().isEqualTo(NEW_RULE_CONTENT);
		assertThat(model.getKieBase()).isNotNull();
		assertThat(model.getActive()).isFalse();

		verify(modelService, times(0)).clone(model);
		verify(context, times(0)).registerElement(historicalModel);

		assertThat(historicalModel.getVersion()).isNull();
		assertThat(historicalModel.getChecksum()).isNull();
		assertThat(historicalModel.getRuleContent()).isNull();
		assertThat(historicalModel.getKieBase()).isNull();
		assertThat(historicalModel.getActive()).isNull();
	}

	@Test
	public void testOnPrepareUpdateModelUpdateActiveFlagChanged() throws InterceptorException
	{
		when(engineRuleDao.getRuleVersion(RULE_CODE, RULE_MODULE_NAME)).thenReturn(RULE_VERSION);

		model.setRuleContent(RULE_CONTENT);
		model.setChecksum(RULE_CHECKSUM);
		model.setActive(Boolean.FALSE);

		defineRegularUpdateBehaviour();
		defineContextBehaviour();
		defineModelServiceBehaviour(true);

		prepareInterceptor.onPrepare(model, context);

		assertThat(model.getVersion()).isNotNull().isEqualTo(NEW_RULE_VERSION + 1);
		assertThat(model.getChecksum()).isEqualTo(RULE_CHECKSUM);
		assertThat(model.getRuleContent()).isNotEmpty().isEqualTo(RULE_CONTENT);
		assertThat(model.getKieBase()).isNotNull();
		assertThat(model.getActive()).isFalse();

		verify(modelService).clone(model);
		verify(context).registerElementFor(historicalModel, PersistenceOperation.SAVE);

		assertThat(historicalModel.getVersion()).isNotNull().isEqualTo(RULE_VERSION);
		assertThat(historicalModel.getChecksum()).isNotEmpty().isEqualTo(RULE_CHECKSUM);
		assertThat(historicalModel.getRuleContent()).isNotEmpty().isEqualTo(RULE_CONTENT);
		assertThat(historicalModel.getKieBase()).isNotNull();
		assertThat(historicalModel.getCurrentVersion()).isFalse();
	}

	@Test
	public void testOnPrepareUpdateModelUpdateActiveFlagChangedWrongVersion() throws InterceptorException
	{
		when(engineRuleDao.getRuleVersion(RULE_CODE, RULE_MODULE_NAME)).thenReturn(RULE_VERSION + 1);
		historicalModel.setKieBase(null);

		model.setRuleContent(RULE_CONTENT);
		model.setChecksum(RULE_CHECKSUM);
		model.setActive(Boolean.FALSE);

		defineRegularUpdateBehaviour();
		defineContextBehaviour();
		defineModelServiceBehaviour(true);

		prepareInterceptor.onPrepare(model, context);

		assertThat(model.getVersion()).isNotNull().isEqualTo(RULE_VERSION);
		assertThat(model.getChecksum()).isEqualTo(RULE_CHECKSUM);
		assertThat(model.getRuleContent()).isNotEmpty().isEqualTo(RULE_CONTENT);
		assertThat(model.getKieBase()).isNotNull();
		assertThat(model.getActive()).isFalse();

		verify(modelService, times(0)).clone(model);
		verify(context, times(0)).registerElement(historicalModel);

		assertThat(historicalModel.getVersion()).isNull();
		assertThat(historicalModel.getChecksum()).isNull();
		assertThat(historicalModel.getRuleContent()).isNull();
		assertThat(historicalModel.getKieBase()).isNull();
		assertThat(historicalModel.getActive()).isNull();
	}


	@Test
	public void testOnPrepareModelRemoved() throws InterceptorException
	{
		historicalModel.setKieBase(null);

		defineRegularUpdateBehaviour();
		defineContextBehaviour();
		defineModelServiceBehaviour(true);

		when(valueOf(context.isRemoved(model))).thenReturn(Boolean.TRUE);

		prepareInterceptor.onPrepare(model, context);

		assertThat(model.getVersion()).isNotNull().isEqualTo(RULE_VERSION);
		assertThat(model.getChecksum()).isEqualTo(NEW_RULE_CHECKSUM);
		assertThat(model.getRuleContent()).isNotEmpty().isEqualTo(NEW_RULE_CONTENT);
		assertThat(model.getKieBase()).isNotNull();
		assertThat(model.getActive()).isTrue();

		verify(modelService, times(0)).clone(model);
		verify(context, times(0)).registerElement(historicalModel);

		assertThat(historicalModel.getVersion()).isNull();
		assertThat(historicalModel.getChecksum()).isNull();
		assertThat(historicalModel.getRuleContent()).isNull();
		assertThat(historicalModel.getKieBase()).isNull();
		assertThat(historicalModel.getActive()).isNull();
	}

	@Test
	public void testOnPrepareNewModuleVersionIsNull() throws InterceptorException
	{
		when(kieModuleModel.getVersion()).thenReturn(null);
		when(context.isNew(model)).thenReturn(true);

		prepareInterceptor.onPrepare(model, context);
		assertThat(model.getVersion()).isEqualTo(0);
	}

	@Test
	public void testOnPrepareNewModuleIsEmpty() throws InterceptorException
	{
		when(kieModuleModel.getVersion()).thenReturn(valueOf(-1));
		when(context.isNew(model)).thenReturn(true);
		model.setKieBase(null);
		model.setVersion(null);

		expectedException.expect(InterceptorException.class);
		expectedException.expectMessage("The Kie base should be assigned to the drools rule instance");

		prepareInterceptor.onPrepare(model, context);
	}

	private void defineRegularUpdateBehaviour()
	{
		model.setVersion(RULE_VERSION);
		when(engineRuleDao.getCurrentRulesSnapshotVersion(kieModuleModel)).thenReturn(NEW_RULE_VERSION);
		when(ruleModelChecksumCalculator.calculateContentChecksum(RULE_CONTENT)).thenReturn(RULE_CHECKSUM);
		when(ruleModelChecksumCalculator.calculateContentChecksum(NEW_RULE_CONTENT)).thenReturn(NEW_RULE_CHECKSUM);
	}

	private void defineContextBehaviour()
	{
		when(context.isModified(eq(model), anyString())).thenReturn(false);
		when(valueOf(context.isNew(model))).thenReturn(Boolean.FALSE);
		when(valueOf(context.isRemoved(model))).thenReturn(Boolean.FALSE);
		when(valueOf(context.isModified(model))).thenReturn(Boolean.TRUE);
	}

	private void defineModelServiceBehaviour(final boolean active)
	{
		when(modelService.getAttributeValue(model, AbstractRuleEngineRuleModel.ACTIVE)).thenReturn(active);
		when(modelService.getAttributeValue(model, AbstractRuleEngineRuleModel.VERSION)).thenReturn(RULE_VERSION);
		when(modelService.getAttributeValue(model, AbstractRuleEngineRuleModel.CHECKSUM)).thenReturn(RULE_CHECKSUM);
		when(modelService.getAttributeValue(model, AbstractRuleEngineRuleModel.RULECONTENT)).thenReturn(RULE_CONTENT);
		when(modelService.getAttributeValue(model, AbstractRuleEngineRuleModel.UUID)).thenReturn(UUID_STRING);
	}

	public static class MockRuleModelChecksumCalculator extends RuleEngineRuleModelChecksumCalculator
	{
		@Override
		protected String calculateContentChecksum(final String ruleContent) //NOPMD
		{
			return super.calculateContentChecksum(ruleContent);
		}
	}

}

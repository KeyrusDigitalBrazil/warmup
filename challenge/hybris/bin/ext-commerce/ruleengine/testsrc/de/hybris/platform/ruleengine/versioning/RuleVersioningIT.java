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
package de.hybris.platform.ruleengine.versioning;

import static java.lang.Long.valueOf;
import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.ruleengine.dao.EngineRuleDao;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengine.model.AbstractRulesModuleModel;
import de.hybris.platform.ruleengine.test.RuleEngineTestSupportService;
import de.hybris.platform.ruleengineservices.enums.RuleStatus;
import de.hybris.platform.ruleengineservices.model.SourceRuleModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Resource;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;


@IntegrationTest
public class RuleVersioningIT extends ServicelayerTest
{
	private static final String MODULE_NAME = "versioningTestModule";

	@Resource
	private ModelService modelService;
	@Resource
	private EngineRuleDao engineRuleDao;
	@Resource
	private RuleEngineTestSupportService ruleEngineTestSupportService;

	private AbstractRuleEngineRuleModel ruleModel;
	private AbstractRuleEngineRuleModel ruleModel2;
	private AbstractRulesModuleModel module;
	private SourceRuleModel sourceRuleModel;
	@Before
	public void setUp() throws Exception
	{

		ruleModel = getRuleFromResource("ruleengine/test/versioning/rule1.drl", "ruleengine/test/versioning/rule.drl",
				"1d1c86c4-05c0-4fa1-a3b0-35dfaee8129a");
		module = ruleEngineTestSupportService.associateRulesToNewModule(MODULE_NAME, ImmutableSet.of(ruleModel));
		modelService.save(ruleModel);

		ruleModel2 = createEmptyRule("ruleengine/test/versioning/rule4.drl", "1d1c86c4-05c0-4fa1-a3b0-35dfaee8129b");
		ruleEngineTestSupportService.associateRulesToNewModule(MODULE_NAME, ImmutableSet.of(ruleModel2));
		ruleModel2.setActive(Boolean.TRUE);
		modelService.save(ruleModel2);

		sourceRuleModel = createSourceRule();
		modelService.save(sourceRuleModel);
	}

	protected SourceRuleModel createSourceRule()
	{
		SourceRuleModel sourceRuleModel = new SourceRuleModel();
		sourceRuleModel.setVersion(1l);
		sourceRuleModel.setCode(UUID.randomUUID().toString());
		sourceRuleModel.setPriority(100);
		sourceRuleModel.setStatus(RuleStatus.PUBLISHED);
		return sourceRuleModel;
	}


	@Test
	public void testRuleWithoutContent() throws Exception
	{
		assertThat(ruleModel2.getRuleContent()).isEqualTo(null);
		assertThat(ruleModel2.getChecksum()).isEqualTo(null);

		ruleModel2.setRuleContent(readFromResource("ruleengine/test/versioning/rule4.drl"));
		modelService.save(ruleModel2);
		ruleModel2 = engineRuleDao.getRuleByCode("ruleengine/test/versioning/rule4.drl", MODULE_NAME);

		assertThat(ruleModel2.getRuleContent()).isNotEqualTo(null);
		assertThat(ruleModel2.getChecksum()).isNotEqualTo(null);
	}

	@Test
	public void testRuleAndModuleInitialVersion() throws Exception
	{
		assertThat(ruleModel.getVersion()).isEqualTo(1);
		assertThat(ruleModel.getCurrentVersion()).isEqualTo(true);
		assertThat(ruleModel.getChecksum()).isNotEqualTo(null);
		assertThat(module.getVersion()).isEqualTo(2);
	}

	@Test
	public void testRuleAndModuleChangeVersionSync() throws Exception
	{
		ruleModel.setRuleContent(readFromResource("ruleengine/test/versioning/rule1_modified.drl"));
		modelService.save(ruleModel);

		ruleModel = engineRuleDao.getRuleByCode("ruleengine/test/versioning/rule.drl", MODULE_NAME);

		assertThat(ruleModel.getVersion()).isEqualTo(3);
		assertThat(ruleModel.getCurrentVersion()).isEqualTo(true);
		final Optional<AbstractRulesModuleModel> associatedModuleOptional = ruleEngineTestSupportService
				.resolveAssociatedRuleModule(ruleModel);
		assertThat(associatedModuleOptional.isPresent()).isTrue();
		assertThat(associatedModuleOptional.get().getVersion()).isEqualTo(3);
	}

	@Test
	public void testRuleAndModuleVersionNotChangedIfContentIsSame() throws Exception
	{
		ruleModel.setRuleContent(readFromResource("ruleengine/test/versioning/rule1.drl"));
		ruleEngineTestSupportService.associateRulesToNewModule(MODULE_NAME, ImmutableSet.of(ruleModel));
		modelService.save(ruleModel);

		ruleModel = engineRuleDao.getRuleByCode("ruleengine/test/versioning/rule.drl", MODULE_NAME);

		assertThat(ruleModel.getVersion()).isEqualTo(1);
		assertThat(ruleModel.getCurrentVersion()).isEqualTo(true);
		final Optional<AbstractRulesModuleModel> associatedModuleOptional = ruleEngineTestSupportService
				.resolveAssociatedRuleModule(ruleModel);
		assertThat(associatedModuleOptional.isPresent()).isTrue();
		assertThat(associatedModuleOptional.get().getVersion()).isEqualTo(2);
	}

	@Test
	public void testRuleVersionIfNewRuleIsAdded() throws Exception
	{
		ruleModel.setRuleContent(readFromResource("ruleengine/test/versioning/rule1_modified.drl"));
		modelService.save(ruleModel);

		AbstractRuleEngineRuleModel newRuleModel = getRuleFromResource("ruleengine/test/versioning/rule3.drl",
				"ruleengine/test/versioning/rule3.drl", "ruleengine/test/versioning/rule3");
		ruleEngineTestSupportService.associateRulesModule(module, ImmutableSet.of(newRuleModel));
		modelService.save(newRuleModel);

		newRuleModel = engineRuleDao.getRuleByCode("ruleengine/test/versioning/rule3.drl", MODULE_NAME);


		assertThat(newRuleModel.getVersion()).isEqualTo(4);
		assertThat(newRuleModel.getCurrentVersion()).isEqualTo(true);
		final Optional<AbstractRulesModuleModel> associatedModuleOptional = ruleEngineTestSupportService
				.resolveAssociatedRuleModule(newRuleModel);
		assertThat(associatedModuleOptional.isPresent()).isTrue();
		assertThat(associatedModuleOptional.get().getVersion()).isEqualTo(4);
	}

	@Test
	public void testRuleVersionEqualsToModuleVersion() throws Exception
	{
		ruleModel.setRuleContent(readFromResource("ruleengine/test/versioning/rule1_modified.drl"));
		ruleEngineTestSupportService.associateRulesModule(module, ImmutableSet.of(ruleModel));
		modelService.save(ruleModel);

		final AbstractRuleEngineRuleModel newRuleModel = createEmptyRule("ruleengine/test/versioning/rule3.drl",
				"ruleengine/test/versioning/rule3");
		newRuleModel.setActive(Boolean.TRUE);
		newRuleModel.setVersion(valueOf(1));
		ruleEngineTestSupportService.associateRulesModule(module, ImmutableSet.of(newRuleModel));
		modelService.save(newRuleModel);
	}

	@Test
	public void shouldRaiseExceptionWhenTryingToSaveSourceRuleBasedRuleVersionLessThenModuleVersion() throws Exception
	{
		//given
		ruleModel.setRuleContent(readFromResource("ruleengine/test/versioning/rule1_modified.drl"));
		modelService.save(ruleModel);

		final AbstractRuleEngineRuleModel newRuleModel = createEmptyRule("ruleengine/test/versioning/rule3.drl",
				"ruleengine/test/versioning/rule3");
		newRuleModel.setVersion(valueOf(0));
		newRuleModel.setSourceRule(sourceRuleModel);
		ruleEngineTestSupportService.associateRulesModule(module, ImmutableSet.of(newRuleModel));
		//when
		final Throwable throwable = ThrowableAssert.catchThrowable( () -> modelService.save(newRuleModel) );
		//then
		Assertions.assertThat(throwable).isInstanceOf(ModelSavingException.class)
			.hasMessageContaining("Non active rule version cannot increase overall knowledgebase snapshot version");
	}

	@Test
	public void shouldSaveManuallyCreatedRuleVersionWithoutContentValidation() throws Exception
	{
		//given
		ruleModel.setRuleContent(readFromResource("ruleengine/test/versioning/rule1_modified.drl"));
		modelService.save(ruleModel);

		final AbstractRuleEngineRuleModel newRuleModel = createEmptyRule("ruleengine/test/versioning/rule3.drl",
					 "ruleengine/test/versioning/rule3");
		newRuleModel.setVersion(valueOf(0));
		newRuleModel.setSourceRule(null);
		ruleEngineTestSupportService.associateRulesModule(module, ImmutableSet.of(newRuleModel));
		//when
		final Throwable throwable = ThrowableAssert.catchThrowable( () -> modelService.save(newRuleModel) );
		//then
		Assertions.assertThat(throwable).isNull();
	}

	protected String readFromResource(final String resourceName) throws IOException
	{
		final URL url = Resources.getResource(resourceName);
		return Resources.toString(url, Charsets.UTF_8);
	}

	protected AbstractRuleEngineRuleModel getRuleFromResource(final String resourceName, final String ruleCode,
			final String ruleUUID) throws IOException
	{
		final AbstractRuleEngineRuleModel rule = createEmptyRule(ruleCode, ruleUUID);
		rule.setActive(Boolean.TRUE);
		rule.setRuleContent(readFromResource(resourceName));
		return rule;
	}

	protected AbstractRuleEngineRuleModel createEmptyRule(final String ruleCode, final String ruleUUID) throws IOException
	{
		final AbstractRuleEngineRuleModel rule = ruleEngineTestSupportService.createRuleModel();
		rule.setCode(ruleCode);
		rule.setUuid(ruleUUID);
		rule.setActive(Boolean.FALSE);
		return rule;
	}

}

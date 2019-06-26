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
package de.hybris.platform.ruleengine.drools.impl;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.ruleengine.cronjob.DefaultKieModuleCleanupStrategy;
import de.hybris.platform.ruleengine.model.DroolsKIEModuleMediaModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.internal.model.MaintenanceCleanupJobModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.drools.compiler.kie.builder.impl.MemoryKieModule;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;


@IntegrationTest
public class DefaultKieModuleServiceIT extends ServicelayerTest
{
	private static final String KIE_MODULE_NAME = "kieModuleName";
	private static final String KIE_MODULE_NAME2 = "kieModuleName2";
	private static final String KIE_MODULE_NAME3 = "kieModuleName3";
	private static final String RELEASE_ID1 = "groupId:artifactId:version1";
	private static final String RELEASE_ID2 = "groupId:artifactId:version2";
	private static final String RELEASE_ID3 = "groupId:artifactId:version3";
	private static final String RELEASE_ID4 = "groupId:artifactId:version4";
	private static final String RELEASE_ID5 = "groupId:artifactId:version5";
	private static final String RELEASE_ID16 = "groupId:artifactId:version16";

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultKieModuleServiceIT.class);

	@Resource(name = "defaultKieModuleService")
	private DefaultKieModuleService defaultKieModuleService;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "kieModuleCleanupStrategy")
	private DefaultKieModuleCleanupStrategy kieModuleCleanupStrategy;

	@Resource(name = "flexibleSearchService")
	private FlexibleSearchService flexibleSearchService;

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/ruleengine/test/ruleenginesetup.impex", "utf-8");
	}

	protected String getRuleContent(final String ruleUuid, final String ruleCode, final String moduleName) throws IOException
	{
		final String ruleTemplateContent = new String(Files.readAllBytes(Paths.get(new ClassPathResource(
				"/ruleengine/test/impl/drools_rule_template.drl").getURI())));
		final String ruleContent = ruleTemplateContent.replaceAll("\\$\\{rule_uuid\\}", ruleUuid)
				.replaceAll("\\$\\{rule_code\\}", ruleCode).replaceAll("\\$\\{module_name\\}", moduleName);
		return ruleContent;
	}

	@Test
	public void testWriteReadKieModule() throws IOException
	{
		if (checkCMCDisabled())
		{
			return;
		}
		final KieModule kieModule = getKieModule();
		defaultKieModuleService.saveKieModule(KIE_MODULE_NAME, RELEASE_ID1, kieModule);

		final Optional<KieModule> kieModuleOptional = defaultKieModuleService.loadKieModule(KIE_MODULE_NAME, RELEASE_ID1);
		assertThat(kieModuleOptional.isPresent()).isTrue();
		assertThat(kieModuleOptional.get() instanceof MemoryKieModule).isTrue();
		final KieBase kieBase = getKieBase(kieModuleOptional.get());
		assertThat(kieBase.getRule("de.hybris.platform.ruleengine.drools", "ruleUuid1")).isNotNull();
		assertThat(kieBase.getRule("de.hybris.platform.ruleengine.drools", "ruleUuid2")).isNotNull();
		assertThat(kieBase.getRule("de.hybris.platform.ruleengine.drools", "ruleUuid3")).isNull();
	}

	@Test
	public void testWriteRemoveKieModules() throws IOException
	{
		if (checkCMCDisabled())
		{
			return;
		}
		final KieModule kieModule = getKieModule();
		defaultKieModuleService.saveKieModule(KIE_MODULE_NAME, RELEASE_ID1, kieModule);
		defaultKieModuleService.saveKieModule(KIE_MODULE_NAME, RELEASE_ID2, kieModule);
		defaultKieModuleService.saveKieModule(KIE_MODULE_NAME, RELEASE_ID16, kieModule);
		defaultKieModuleService.saveKieModule(KIE_MODULE_NAME2, RELEASE_ID3, kieModule);
		defaultKieModuleService.saveKieModule(KIE_MODULE_NAME2, RELEASE_ID4, kieModule);
		defaultKieModuleService.saveKieModule(KIE_MODULE_NAME3, RELEASE_ID5, kieModule);

		removeOldKieModules(1); // the only RELEASE_ID4, RELEASE_ID5, RELEASE_ID6 (last 1 for kiemodule, kiemodule2, kiemodule3) should left

		final Optional<KieModule> kieModuleOptional1 = defaultKieModuleService.loadKieModule(KIE_MODULE_NAME, RELEASE_ID1);
		assertThat(kieModuleOptional1.isPresent()).isFalse();
		final Optional<KieModule> kieModuleOptional2 = defaultKieModuleService.loadKieModule(KIE_MODULE_NAME, RELEASE_ID2);
		assertThat(kieModuleOptional2.isPresent()).isFalse();
		final Optional<KieModule> kieModuleOptional3 = defaultKieModuleService.loadKieModule(KIE_MODULE_NAME2, RELEASE_ID3);
		assertThat(kieModuleOptional3.isPresent()).isFalse();
		final Optional<KieModule> kieModuleOptional6 = defaultKieModuleService.loadKieModule(KIE_MODULE_NAME, RELEASE_ID16);
		assertThat(kieModuleOptional6.isPresent()).isTrue();
		final KieBase kieBase = getKieBase(kieModuleOptional6.get());
		assertThat(kieBase.getRule("de.hybris.platform.ruleengine.drools", "ruleUuid1")).isNotNull();
		assertThat(kieBase.getRule("de.hybris.platform.ruleengine.drools", "ruleUuid2")).isNotNull();
		assertThat(kieBase.getRule("de.hybris.platform.ruleengine.drools", "ruleUuid3")).isNull();
		final Optional<KieModule> kieModuleOptional4 = defaultKieModuleService.loadKieModule(KIE_MODULE_NAME2, RELEASE_ID4);
		assertThat(kieModuleOptional4.isPresent()).isTrue();
		final Optional<KieModule> kieModuleOptional5 = defaultKieModuleService.loadKieModule(KIE_MODULE_NAME3, RELEASE_ID5);
		assertThat(kieModuleOptional5.isPresent()).isTrue();
	}

	protected void removeOldKieModules(final int threshold)
	{
		final CronJobModel cjm = new CronJobModel();
		final MaintenanceCleanupJobModel cleanupJob = new MaintenanceCleanupJobModel();
		cleanupJob.setThreshold(threshold);
		cjm.setJob(cleanupJob);
		final SearchResult<DroolsKIEModuleMediaModel> search = flexibleSearchService.search(kieModuleCleanupStrategy
				.createFetchQuery(cjm));
		final List<DroolsKIEModuleMediaModel> oldKieModulesToRemove = search.getResult();
		kieModuleCleanupStrategy.process(oldKieModulesToRemove);
	}

	protected KieBase getKieBase(final KieModule kieModule)
	{
		final KieServices kieServices = KieServices.Factory.get();
		final KieRepository kieRepository = kieServices.getRepository();
		kieRepository.addKieModule(kieModule);
		final KieContainer kieContainer = kieServices.newKieContainer(kieModule.getReleaseId());
		return kieContainer.getKieBase();
	}

	protected KieModule getKieModule() throws IOException
	{
		final KieServices kieServices = KieServices.Factory.get();
		final KieFileSystem kfs = kieServices.newKieFileSystem();

		kfs.write("src/main/resources/rule1.drl",
				kieServices.getResources().newByteArrayResource(getRuleContent("ruleUuid1", "ruleCode1", "moduleName1").getBytes()));
		kfs.write("src/main/resources/rule2.drl",
				kieServices.getResources().newByteArrayResource(getRuleContent("ruleUuid2", "ruleCode2", "moduleName2").getBytes()));

		final KieBuilder kieBuilder = kieServices.newKieBuilder(kfs).buildAll();
		final Results results = kieBuilder.getResults();
		if (results.hasMessages(Message.Level.ERROR))
		{
			throw new IllegalStateException(results.getMessages().stream().map(m -> m.getText()).collect(Collectors.joining(", ")));
		}
		final KieModule kieModule = kieBuilder.getKieModule();
		return kieModule;
	}

	protected boolean checkCMCDisabled()
	{
		if (!configurationService.getConfiguration().getBoolean("ruleengine.centralized.module.compilation.enabled"))
		{
			LOGGER.info("skipping tests as centralized module compilation is disabled.");
			return true;
		}
		return false;
	}
}

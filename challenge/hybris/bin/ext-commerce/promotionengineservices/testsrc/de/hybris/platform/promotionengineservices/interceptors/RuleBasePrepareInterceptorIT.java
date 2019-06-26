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
package de.hybris.platform.promotionengineservices.interceptors;

import static java.lang.Long.valueOf;
import static java.nio.charset.Charset.forName;
import static java.nio.file.Files.newInputStream;
import static java.nio.file.Paths.get;
import static java.util.Arrays.stream;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toSet;
import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.promotionengineservices.dao.PromotionDao;
import de.hybris.platform.promotionengineservices.promotionengine.impl.PromotionEngineServiceBaseTest;
import de.hybris.platform.promotions.model.PromotionGroupModel;
import de.hybris.platform.ruleengine.MessageLevel;
import de.hybris.platform.ruleengine.RuleEngineActionResult;
import de.hybris.platform.ruleengine.RuleEngineService;
import de.hybris.platform.ruleengine.dao.RuleEngineContextDao;
import de.hybris.platform.ruleengine.enums.RuleType;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineContextModel;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengine.model.AbstractRulesModuleModel;
import de.hybris.platform.ruleengine.model.DroolsKIEBaseModel;
import de.hybris.platform.ruleengine.model.DroolsRuleModel;
import de.hybris.platform.ruleengine.test.RuleEngineTestSupportService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class RuleBasePrepareInterceptorIT extends PromotionEngineServiceBaseTest
{
	@Resource
	private ModelService modelService;
	@Resource
	private PromotionDao promotionDao;
	@Resource
	private RuleEngineService commerceRuleEngineService;
	@Resource
	private RuleEngineContextDao ruleEngineContextDao;
	@Resource
	private RuleEngineTestSupportService ruleEngineTestSupportService;

	private DroolsKIEBaseModel kieBaseModel;

	@Before
	public void setUp() throws ImpExException, IOException
	{
		// setup with promotionsenginesetup impex
		super.importCsv("/promotionengineservices/test/promotionenginesetup.impex", "UTF-8");
		kieBaseModel = getKieBaseModel("promotions-base-junit");
	}

	@Test
	public void testTransferCartToOrderUpdateRule() throws IOException
	{
		final DroolsRuleModel ruleModel = (DroolsRuleModel) getRuleForFile("orderPercentageDiscount.drl",
				"/promotionengineservices/test/rules/");
		ruleModel.setCode("orderPercentageDiscount");
		ruleModel.setVersion(valueOf(0L));
		ruleEngineTestSupportService.decorateRuleForTest(new HashMap<String, String>()
		{
			{
				put("ruleOrderPercentageDiscountAction", "ruleOrderPercentageDiscountAction");
			}
		}).accept(ruleModel);
		ruleModel.setKieBase(kieBaseModel);
		modelService.save(ruleModel);
		final PromotionGroupModel group = promotionDao.findPromotionGroupByCode("promoGroup1");
		final Collection<PromotionGroupModel> groupList = new ArrayList<PromotionGroupModel>();
		groupList.add(group);
		initializeRuleEngine(ruleModel);

		assertThat(ruleModel.getVersion()).isEqualTo(0);
		Optional<AbstractRulesModuleModel> associatedRuleModule = ruleEngineTestSupportService
				.resolveAssociatedRuleModule(ruleModel);
		assertThat(associatedRuleModule.isPresent()).isTrue();
		assertThat(associatedRuleModule.get().getVersion()).isEqualTo(0);

		// change the rule...
		ruleModel.setRuleContent(readRuleFile("orderPercentageDiscount1.drl", "/promotionengineservices/test/rules/"));
		modelService.save(ruleModel);
		initializeRuleEngine(ruleModel);

		assertThat(ruleModel.getVersion()).isEqualTo(1);
		associatedRuleModule = ruleEngineTestSupportService.resolveAssociatedRuleModule(ruleModel);
		assertThat(associatedRuleModule.isPresent()).isTrue();
		assertThat(associatedRuleModule.get().getVersion()).isEqualTo(1);
	}

	protected void initializeRuleEngine(final AbstractRuleEngineRuleModel... rules)
	{
		final AbstractRuleEngineContextModel abstractContext = ruleEngineContextDao
				.findRuleEngineContextByName("promotions-junit-context");
		final List<RuleEngineActionResult> results = commerceRuleEngineService
				.initialize(
						singletonList(ruleEngineTestSupportService.getTestRulesModule(abstractContext, stream(rules).collect(toSet()))),
						true, false).waitForInitializationToFinish().getResults();
		if (CollectionUtils.isEmpty(results))
		{
			Assert.fail("rule engine initialization failed: no results found");
		}
		final RuleEngineActionResult result = results.get(0);
		if (result.isActionFailed())
		{
			Assert.fail("rule engine initialization failed with errors: " + result.getMessagesAsString(MessageLevel.ERROR));
		}
	}

	/**
	 * Creates a (non-persisted) AbstractRuleEngineRuleModel based on the given file and path. Note that this
	 * implementation assumes that the fileName matches the rule's name (excluding the .drl extension).
	 *
	 * @param fileName
	 * @param path
	 * @return new AbstractRuleEngineRuleModel
	 * @throws IOException
	 */
	protected AbstractRuleEngineRuleModel getRuleForFile(final String fileName, final String path) throws IOException
	{
		final DroolsRuleModel rule = (DroolsRuleModel) ruleEngineTestSupportService.createRuleModel();
		rule.setCode(fileName);
		rule.setUuid(fileName.substring(0, fileName.length() - 4));
		rule.setActive(Boolean.TRUE);
		rule.setRuleContent(readRuleFile(fileName, path));
		rule.setRuleType(RuleType.PROMOTION);
		rule.setKieBase(kieBaseModel);
		return rule;
	}

	@Override
	protected String readRuleFile(final String fileName, final String path) throws IOException
	{
		final Path rulePath = get(getApplicationContext().getResource("classpath:" + path + fileName).getURI());
		final InputStream is = newInputStream(rulePath);
		final StringWriter writer = new StringWriter();
		IOUtils.copy(is, writer, forName("UTF-8"));
		return writer.toString();
	}
}

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
package de.hybris.platform.droolsruleengineservices.impl;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.singletonList;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.ruleengine.MessageLevel;
import de.hybris.platform.ruleengine.RuleEngineActionResult;
import de.hybris.platform.ruleengine.RuleEngineService;
import de.hybris.platform.ruleengine.RuleEvaluationContext;
import de.hybris.platform.ruleengine.RuleEvaluationResult;
import de.hybris.platform.ruleengine.dao.DroolsKIEBaseDao;
import de.hybris.platform.ruleengine.dao.EngineRuleDao;
import de.hybris.platform.ruleengine.dao.RuleEngineContextDao;
import de.hybris.platform.ruleengine.dao.RulesModuleDao;
import de.hybris.platform.ruleengine.enums.RuleType;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineContextModel;
import de.hybris.platform.ruleengine.model.DroolsKIEBaseModel;
import de.hybris.platform.ruleengine.model.DroolsKIEModuleModel;
import de.hybris.platform.ruleengine.model.DroolsRuleEngineContextModel;
import de.hybris.platform.ruleengine.model.DroolsRuleModel;
import de.hybris.platform.ruleengineservices.calculation.RuleEngineCalculationService;
import de.hybris.platform.ruleengineservices.converters.populator.CartModelBuilder;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rao.DeliveryModeRAO;
import de.hybris.platform.ruleengineservices.rao.OrderEntryRAO;
import de.hybris.platform.ruleengineservices.rao.ProductRAO;
import de.hybris.platform.ruleengineservices.rao.providers.RAOProvider;
import de.hybris.platform.ruleengineservices.rao.providers.impl.FactContext;
import de.hybris.platform.ruleengineservices.rao.util.DefaultRaoService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;

import reactor.util.CollectionUtils;


public abstract class AbstractRuleEngineServicesTest extends ServicelayerTest
{

	/**
	 * the default rule engine context used for tests (see corresponding impex file)
	 */
	public final static String RULE_ENGINGE_CONTEXT_NAME = "rules-junit-context";
	public final static String RULE_ENGINGE_KBASE_JUNIT = "rules-base-junit";

	public final static String RULE_ENGINGE_KMODULE_JUNIT = "rules-module-junit";

	@Resource
	private ModelService modelService;

	@Resource
	private RuleEngineService commerceRuleEngineService;

	@Resource
	private RuleEngineService platformRuleEngineService;

	@Resource
	private RuleEngineContextDao ruleEngineContextDao;

	@Resource
	private EngineRuleDao engineRuleDao;

	@Resource
	private RulesModuleDao rulesModuleDao;

	@Resource
	private RAOProvider cartRAOProvider;

	@Resource
	private DroolsKIEBaseDao droolsKIEBaseDao;

	@Resource
	private RuleEngineCalculationService ruleEngineCalculationService;

	@Resource
	private MediaService mediaService;

	private final KieServices kieServices = KieServices.Factory.get();

	private final DefaultRaoService raoService = new DefaultRaoService();

	protected Set<Object> provideRAOs(final FactContext factContext)
	{
		final Set<Object> result = newHashSet();
		for (final Object fact : factContext.getFacts())
		{
			for (final RAOProvider raoProvider : factContext.getProviders(fact))
			{
				result.addAll(raoProvider.expandFactModel(fact));
			}
		}
		return result;
	}

	protected Set buildRAOsForNotEmptyCartWithCode(final String code)
	{
		return cartRAOProvider.expandFactModel(buildNotEmptyCartWithCodeAndCurrency(code));
	}

	protected Set buildRAOsForCartWithCode(final String code)
	{
		return cartRAOProvider.expandFactModel(buildCartWithCodeAndCurrency(code));
	}

	protected RuleEvaluationContext prepareContext(final Set<Object> facts)
	{
		final RuleEvaluationContext context = new RuleEvaluationContext();
		context.setFacts(facts);
		context.setRuleEngineContext(getRuleEngineContextDao().findRuleEngineContextByName(RULE_ENGINGE_CONTEXT_NAME));
		return context;
	}

	protected RuleEvaluationResult evaluate(final Set<Object> facts)
	{
		final RuleEvaluationContext context = prepareContext(facts);
		return getCommerceRuleEngineService().evaluate(context);
	}

	protected RuleEvaluationResult evaluateAndFailOnError(final Set<Object> facts)
	{
		final RuleEvaluationResult result = evaluate(facts);
		if (result.isEvaluationFailed())
		{
			Assert.fail("rule evaluation failed with error message:" + result.getErrorMessage());
		}
		return result;
	}

	/**
	 * creates a non-persistent cart with no entries and default currency USD.
	 *
	 * @param code
	 *           the code to use
	 * @return the CartModel
	 */
	protected CartModel buildCartWithCodeAndCurrency(final String code)
	{
		return CartModelBuilder.newCart(code).setCurrency("USD").getModel();
	}

	/**
	 * creates a non-persistent not-empty cart with default currency USD.
	 *
	 * @param code
	 *           the code to use
	 * @return the CartModel
	 */
	protected CartModel buildNotEmptyCartWithCodeAndCurrency(final String code)
	{
		return CartModelBuilder.newCart(code).addProduct("productCode", 2, 50, 1).setCurrency("USD").getModel();
	}

	protected DroolsRuleModel getRuleForFile(final String fileName, final String path, final String packageName,
			final Map<String, String> globals) throws IOException
	{
		return getRuleForFile(fileName, path, packageName, globals, RuleType.DEFAULT);
	}

	protected DroolsRuleModel getRuleForFile(final String fileName, final String path, final String packageName,
			final Map<String, String> globals, final RuleType ruleType) throws IOException
	{
		final DroolsRuleModel rule = modelService.create(DroolsRuleModel.class);
		rule.setCode(fileName);
		rule.setActive(Boolean.TRUE);
		rule.setVersion(0L);
		rule.setCurrentVersion(true);
		rule.setUuid(fileName.substring(0, fileName.length() - 4));
		rule.setRuleContent(readRuleFile(fileName, path));
		rule.setRulePackage(packageName);
		rule.setGlobals(globals);
		rule.setRuleType(ruleType);

		return rule;
	}

	protected String readRuleFile(final String fileName, final String path) throws IOException
	{
		final Path rulePath = Paths.get(Registry.getApplicationContext().getResource("classpath:" + path + fileName).getURI());
		final InputStream inputStream = Files.newInputStream(rulePath);
		final StringWriter writer = new StringWriter();
		IOUtils.copy(inputStream, writer, Charset.forName("UTF-8"));
		return writer.toString();
	}

	protected DroolsKIEModuleModel getTestRulesModule(final Set<DroolsRuleModel> rules)
	{
		final AbstractRuleEngineContextModel abstractContext = getRuleEngineContextDao()
				.findRuleEngineContextByName(RULE_ENGINGE_CONTEXT_NAME);
		checkState(abstractContext instanceof DroolsRuleEngineContextModel,
				"ruleengine context must be of type DroolsRuleEngineContextModel");

		final DroolsRuleEngineContextModel context = (DroolsRuleEngineContextModel) abstractContext;

		final DroolsKIEBaseModel kieBase = context.getKieSession().getKieBase();
		for (DroolsRuleModel droolsRule : rules) {
			droolsRule.setKieBase(kieBase);
		}
		kieBase.setRules(rules);
		rules.stream().forEach(rule -> rule.setKieBase(kieBase));
		getModelService().saveAll();
		return context.getKieSession().getKieBase().getKieModule();

	}

	protected void initializeRuleEngine(final DroolsRuleModel... rules)
	{
		final List<RuleEngineActionResult> results = getPlatformRuleEngineService()
				.initialize(singletonList(getTestRulesModule(Arrays.stream(rules).collect(Collectors.toSet()))), false, false)
				.waitForInitializationToFinish().getResults();
		if(CollectionUtils.isEmpty(results))
		{
			Assert.fail("rule engine initialization failed: no results found");
		}
		final RuleEngineActionResult result = results.get(0);
		if (result.isActionFailed())
		{
			Assert.fail("rule engine initialization failed with errors: " + result.getMessagesAsString(MessageLevel.ERROR));
		}
	}

	protected void initializeRuleEngine(final DroolsRuleEngineContextModel context)
	{
		final List<RuleEngineActionResult> results = getPlatformRuleEngineService()
				.initialize(singletonList(context.getKieSession().getKieBase().getKieModule()), false, false)
				.waitForInitializationToFinish().getResults();
		if(CollectionUtils.isEmpty(results))
		{
			Assert.fail("rule engine initialization failed: no results found");
		}
		final RuleEngineActionResult result = results.get(0);
		if (result.isActionFailed())
		{
			Assert.fail("rule engine initialization failed with errors: " + result.getMessagesAsString(MessageLevel.ERROR));
		}
	}

	protected KieBase getKieBase(final DroolsRuleEngineContextModel context)
	{
		final KieContainer kContainer = kieServices.newKieContainer(getReleaseId(context));

		return kContainer.getKieBase(RULE_ENGINGE_KBASE_JUNIT);
	}

	protected ReleaseId getReleaseId(final DroolsRuleEngineContextModel context)
	{
		final DroolsKIEModuleModel module = context.getKieSession().getKieBase().getKieModule();
		return kieServices.newReleaseId(module.getMvnGroupId(), module.getMvnArtifactId(), module.getMvnVersion());
	}

	protected CartRAO createCartRAO(final String code, final String currencyIso)
	{
		final CartRAO cart = raoService.createCart();
		cart.setCode(code);
		cart.setCurrencyIsoCode(currencyIso);
		return cart;
	}

	protected OrderEntryRAO createOrderEntryRAO(final String basePrice, final String currencyIso, final int quantity,
			final int entryNumber)
	{
		return createOrderEntryRAO(null, basePrice, currencyIso, quantity, entryNumber);
	}

	protected DeliveryModeRAO createDeliveryModeRAO(final String code, final String cost, final String currencyIsoCode)
	{
		final DeliveryModeRAO result = new DeliveryModeRAO();
		result.setCode(code);
		result.setCost(new BigDecimal(cost));
		result.setCurrencyIsoCode(currencyIsoCode);
		return result;
	}

	protected OrderEntryRAO createOrderEntryRAO(final CartRAO cartRao, final String basePrice, final String currencyIso,
			final int quantity, final int entryNumber)
	{
		final OrderEntryRAO entry = raoService.createOrderEntry();
		entry.setOrder(cartRao);
		entry.setCurrencyIsoCode(currencyIso);
		entry.setEntryNumber(Integer.valueOf(entryNumber));
		final ProductRAO product = new ProductRAO();
		product.setCode("product01");
		entry.setBasePrice(new BigDecimal(basePrice));
		entry.setPrice(new BigDecimal(basePrice));
		entry.setQuantity(quantity);
		entry.setProduct(product);
		return entry;
	}

	protected DroolsKIEBaseModel getKieBaseOrCreateNew()
	{
		DroolsKIEModuleModel moduleModel;
		try
		{
			moduleModel = (DroolsKIEModuleModel)getRulesModuleDao().findByName(RULE_ENGINGE_KMODULE_JUNIT);
		}
		catch (final ModelNotFoundException e)
		{
			moduleModel = getModelService().create(DroolsKIEModuleModel.class);
			moduleModel.setName(RULE_ENGINGE_KMODULE_JUNIT);
			moduleModel.setVersion(0L);
			getModelService().save(moduleModel);
		}

		final DroolsKIEBaseModel baseModel = getModelService().create(DroolsKIEBaseModel.class);
		baseModel.setName(RULE_ENGINGE_KBASE_JUNIT);
		baseModel.setKieModule(moduleModel);
		getModelService().save(baseModel);

		return baseModel;
	}

	protected Set<OrderEntryRAO> set(final OrderEntryRAO... entries)
	{
		return new LinkedHashSet<>(Arrays.asList(entries));
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	protected RuleEngineCalculationService getRuleEngineCalculationService()
	{
		return ruleEngineCalculationService;
	}

	protected RuleEngineService getCommerceRuleEngineService()
	{
		return commerceRuleEngineService;
	}

	protected RuleEngineService getPlatformRuleEngineService()
	{
		return platformRuleEngineService;
	}

	protected RuleEngineContextDao getRuleEngineContextDao()
	{
		return ruleEngineContextDao;
	}

	protected EngineRuleDao getEngineRuleDao()
	{
		return engineRuleDao;
	}

	protected MediaService getMediaService()
	{
		return mediaService;
	}

	protected RulesModuleDao getRulesModuleDao()
	{
		return rulesModuleDao;
	}

	protected DroolsKIEBaseDao getDroolsKIEBaseDao()
	{
		return droolsKIEBaseDao;
	}
}

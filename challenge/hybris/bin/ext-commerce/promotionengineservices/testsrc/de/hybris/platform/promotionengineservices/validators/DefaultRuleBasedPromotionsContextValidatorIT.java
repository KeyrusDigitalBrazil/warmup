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
package de.hybris.platform.promotionengineservices.validators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.promotionengineservices.model.RuleBasedPromotionModel;
import de.hybris.platform.promotionengineservices.validators.impl.DefaultRuleBasedPromotionsContextValidator;
import de.hybris.platform.ruleengine.dao.RuleEngineContextDao;
import de.hybris.platform.ruleengine.enums.RuleType;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import com.google.common.collect.ImmutableMap;


@IntegrationTest
public class DefaultRuleBasedPromotionsContextValidatorIT extends ServicelayerTest
{
	private static final String FIND_RULE_BASED_PROMOTION_BY_CODE = "SELECT {Pk} FROM {" + RuleBasedPromotionModel._TYPECODE
			+ "} WHERE {code} =?code";
	public static final String TEST_RULE_MAPPING_CATALOG = "testRuleMappingCatalog";
	@Resource(name = "droolsRuleBasedPromotionsContextValidator")
	private DefaultRuleBasedPromotionsContextValidator validator;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private RuleEngineContextDao ruleEngineContextDao;


	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Resource
	private ModelService modelService;

	@Before
	public void setUp() throws Exception
	{
		importCsv("/promotionengineservices/test/droolsRulesMapping.impex", "UTF-8");
		assertDataPopulatedSuccessfully();
	}

	private RuleBasedPromotionModel findRuleBasedPromotion(String code)
	{
		final Map<String, Object> params = ImmutableMap.of("code", code);
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_RULE_BASED_PROMOTION_BY_CODE, params);
		return flexibleSearchService.searchUnique(query);
	}

	private void assertDataPopulatedSuccessfully()
	{
		try
		{
			ruleEngineContextDao.findRuleEngineContextByName("promotions-junit-context1");
			ruleEngineContextDao.findRuleEngineContextByName("promotions-junit-context2");
			ruleEngineContextDao.findRuleEngineContextByName("promotions-junit-context3");
			catalogVersionService.getCatalogVersion(TEST_RULE_MAPPING_CATALOG, "NoMappings");
			catalogVersionService.getCatalogVersion(TEST_RULE_MAPPING_CATALOG, "SingleMappings");
			catalogVersionService.getCatalogVersion(TEST_RULE_MAPPING_CATALOG, "DoubledMappings");
			// service layer seems to require a refresh for relations to be populated on both ends
			modelService.detachAll();
		}
		catch (Exception ex)
		{
			fail("Test data is missing");
		}
	}

	@Test
	public void shouldRejectPromoResultIfInvalidForGivenContext()
	{
		//given
		final RuleBasedPromotionModel promotion = findRuleBasedPromotion("drools-rule1-junit");
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(TEST_RULE_MAPPING_CATALOG, "NoMappings");
		//when
		final boolean applicable = validator.isApplicable(promotion, catalogVersion, RuleType.PROMOTION);
		//then
		assertThat(applicable).isFalse();
	}

	@Test
	public void shouldAcceptPromoResultIfValidForGivenContext()
	{
		//given
		final RuleBasedPromotionModel promotion = findRuleBasedPromotion("drools-rule3-junit");
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(TEST_RULE_MAPPING_CATALOG,
				"SingleMappings");
		//when
		final boolean applicable = validator.isApplicable(promotion, catalogVersion, RuleType.PROMOTION);
		//then
		assertThat(applicable).isTrue();
	}

	@Test
	public void shouldRejectPromoResultIfInvalidForGivenContexts()
	{
		//given
		final RuleBasedPromotionModel promotion = findRuleBasedPromotion("drools-rule3-junit");
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(TEST_RULE_MAPPING_CATALOG,
				"DoubledMappings");
		//when
		final boolean applicable = validator.isApplicable(promotion, catalogVersion, RuleType.PROMOTION);
		//then
		assertThat(applicable).isFalse();
	}

	@Test
	public void shouldAcceptPromoResultIfValidForGivenContexts()
	{
		//given
		final RuleBasedPromotionModel promotionResult1 = findRuleBasedPromotion("drools-rule1-junit");
		final RuleBasedPromotionModel promotionResult2 = findRuleBasedPromotion("drools-rule2-junit");
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(TEST_RULE_MAPPING_CATALOG,
				"DoubledMappings");
		//when
		final boolean applicable = validator.isApplicable(promotionResult1, catalogVersion, RuleType.PROMOTION)
				&& validator.isApplicable(promotionResult2, catalogVersion, RuleType.PROMOTION);
		//then
		assertThat(applicable).isTrue();
	}


}

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
package de.hybris.platform.personalizationservices.setup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.personalizationservices.RecalculateAction;
import de.hybris.platform.personalizationservices.configuration.CxConfigurationService;
import de.hybris.platform.personalizationservices.constants.PersonalizationservicesConstants;
import de.hybris.platform.personalizationservices.enums.CxUserType;
import de.hybris.platform.personalizationservices.jalo.config.CxPeriodicVoterConfig;
import de.hybris.platform.personalizationservices.jalo.config.CxUrlVoterConfig;
import de.hybris.platform.personalizationservices.model.config.CxConfigModel;
import de.hybris.platform.personalizationservices.model.config.CxPeriodicVoterConfigModel;
import de.hybris.platform.personalizationservices.model.config.CxUrlVoterConfigModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.impex.impl.ClasspathImpExResource;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.site.BaseSiteService;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;


@IntegrationTest
public class PersonalizationServiceSystemSetupIntegrationTest extends ServicelayerTransactionalTest
{
	private static final String CONFIG_WITH_URL_VOTERS = "testConfig";
	private static final String CONFIG_WITH_URL_VOTER_AND_IGNORE_ANONYMOUS_RECALC = "testConfig1";
	private static final String CONFIG_WITH_URL_VOTER_AND_IGNORE_OTHER_ACTION_FOR_ANONYMOUS = "testConfig2";
	private static final String CONFIG_WITH_ANONYMOUS_RECALCULATION = "testConfig3";
	private static final String CONFIG_WITH_PERIODIC_VOTER = "testConfig4";
	private static final String CONFIG_WITH_NEGATIVE_MIN_REQUEST_NUMBER = "testConfig5";
	private static final String CONFIG_WITH_NEGATIVE_MIN_TIME = "testConfig6";
	private static final String CONFIG_WITHOUT_ANONYMOUS_ACTIONS = "testConfig7";

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Resource
	private PersonalizationServicesSystemSetup personalizationServicesSystemSetup;

	@Test
	public void updateCalculationConfigTest() throws Exception {

		//given
		importData(new ClasspathImpExResource("/personalizationservices/test/testdata_oldcxconfig.impex", "UTF-8"));

		//when
		personalizationServicesSystemSetup.updateCalculationConfig();

		//then
		CxConfigModel config = getConfiguration(CONFIG_WITH_URL_VOTERS);
		verifyConfigWithUrlVoter(config);

		config = getConfiguration(CONFIG_WITH_URL_VOTER_AND_IGNORE_ANONYMOUS_RECALC);
		verifyConfigWithUrlVoterAndIngnoreAnonymous(config);

		config = getConfiguration(CONFIG_WITH_URL_VOTER_AND_IGNORE_OTHER_ACTION_FOR_ANONYMOUS);
		verifyConfigWithUrlVoterAndIngnoreAnonymous(config);

		config = getConfiguration(CONFIG_WITH_ANONYMOUS_RECALCULATION);
		verifyConfigWithAnonymousRecalculation(config);

		config = getConfiguration(CONFIG_WITH_PERIODIC_VOTER);
		verifyConfigWithPeriodicVoter(config);

		config = getConfiguration(CONFIG_WITH_NEGATIVE_MIN_REQUEST_NUMBER);
		verifyConfigWithNegativeMinRequestNumber(config);

		config = getConfiguration(CONFIG_WITH_NEGATIVE_MIN_TIME);
		verifyConfigWithNegativeMinTime(config);

		config = getConfiguration(CONFIG_WITHOUT_ANONYMOUS_ACTIONS);
		verifyConfigWithoutAnonymousActions(config);
	}

	protected CxConfigModel getConfiguration(String code) {
		CxConfigModel configModel = new CxConfigModel();
		configModel.setCode(code);
		return flexibleSearchService.getModelByExample(configModel);
	}


	private void verifyConfigWithUrlVoter(CxConfigModel config)
	{
		assertNotNull(config);
		List<CxUrlVoterConfigModel> urlConfigs = config.getUrlVoterConfigs();
		assertNotNull(urlConfigs);
		assertTrue(urlConfigs.size()==2);
		assertEquals("default",urlConfigs.get(0).getCode());
		assertEquals(CxUserType.ANONYMOUS,urlConfigs.get(0).getUserType());
		assertEquals("checkout",urlConfigs.get(1).getCode());
		assertEquals(CxUserType.ALL,urlConfigs.get(1).getUserType());
	}

	private void verifyConfigWithUrlVoterAndIngnoreAnonymous(CxConfigModel config)
	{
		assertNotNull(config);
		List<CxUrlVoterConfigModel> urlConfigs = config.getUrlVoterConfigs();
		assertNotNull(urlConfigs);
		assertTrue(urlConfigs.size()==1);
		assertEquals("default",urlConfigs.get(0).getCode());
		assertEquals(CxUserType.REGISTERED,urlConfigs.get(0).getUserType());
	}

	private void verifyConfigWithAnonymousRecalculation(CxConfigModel config)
	{
		assertNotNull(config);
		assertNotNull(config.getPeriodicVoterConfigs());
		assertEquals(1,config.getPeriodicVoterConfigs().size());
		CxPeriodicVoterConfigModel periodicConfig = config.getPeriodicVoterConfigs().iterator().next();
		assertEquals("anonymousPeriodicVoter",periodicConfig.getCode());
		assertEquals(Integer.valueOf(0),periodicConfig.getUserMinRequestNumber());
		assertEquals(Long.valueOf(6000),periodicConfig.getUserMinTime());
		assertNotNull(periodicConfig.getActions());
		assertEquals(1,periodicConfig.getActions().size());
		assertEquals(RecalculateAction.LOAD.toString(), periodicConfig.getActions().iterator().next());

		assertTrue(CollectionUtils.isEmpty(config.getAnonymousUserActions()));
		assertNull(config.getAnonymousUserMinRequestNumber());
		assertNull(config.getAnonymousUserMinTime());
	}
	private void verifyConfigWithPeriodicVoter(CxConfigModel config)
	{
		assertNotNull(config);
		assertTrue(CollectionUtils.isNotEmpty(config.getPeriodicVoterConfigs()));

		assertEquals(Integer.valueOf(0),config.getAnonymousUserMinRequestNumber());
		assertEquals(Long.valueOf(6000),config.getAnonymousUserMinTime());
		assertNotNull(config.getAnonymousUserActions());
		assertEquals(1,config.getAnonymousUserActions().size());
		assertEquals(RecalculateAction.LOAD.toString(), config.getAnonymousUserActions().iterator().next());
	}

	private void verifyConfigWithNegativeMinRequestNumber(CxConfigModel config)
	{
		assertNotNull(config);
		assertTrue(CollectionUtils.isEmpty(config.getPeriodicVoterConfigs()));

		assertEquals(Integer.valueOf(-1),config.getAnonymousUserMinRequestNumber());
		assertEquals(Long.valueOf(0),config.getAnonymousUserMinTime());
		assertNotNull(config.getAnonymousUserActions());
		assertEquals(1,config.getAnonymousUserActions().size());
		assertEquals(RecalculateAction.LOAD.toString(), config.getAnonymousUserActions().iterator().next());
	}

	private void verifyConfigWithNegativeMinTime(CxConfigModel config)
	{
		assertNotNull(config);
		assertTrue(CollectionUtils.isEmpty(config.getPeriodicVoterConfigs()));

		assertEquals(Integer.valueOf(0),config.getAnonymousUserMinRequestNumber());
		assertEquals(Long.valueOf(-1),config.getAnonymousUserMinTime());
		assertNotNull(config.getAnonymousUserActions());
		assertEquals(1,config.getAnonymousUserActions().size());
		assertEquals(RecalculateAction.LOAD.toString(), config.getAnonymousUserActions().iterator().next());
	}

	private void verifyConfigWithoutAnonymousActions(CxConfigModel config)
	{
		assertNotNull(config);
		assertTrue(CollectionUtils.isEmpty(config.getPeriodicVoterConfigs()));

		assertEquals(Integer.valueOf(0),config.getAnonymousUserMinRequestNumber());
		assertEquals(Long.valueOf(6000),config.getAnonymousUserMinTime());
		assertTrue(CollectionUtils.isEmpty(config.getAnonymousUserActions()));
	}
}

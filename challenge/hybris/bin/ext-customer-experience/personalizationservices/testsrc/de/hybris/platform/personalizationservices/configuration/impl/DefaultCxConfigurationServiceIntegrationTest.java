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
package de.hybris.platform.personalizationservices.configuration.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.personalizationservices.configuration.CxConfigurationService;
import de.hybris.platform.personalizationservices.constants.PersonalizationservicesConstants;
import de.hybris.platform.personalizationservices.model.config.CxConfigModel;
import de.hybris.platform.personalizationservices.model.config.CxPeriodicVoterConfigModel;
import de.hybris.platform.personalizationservices.model.config.CxUrlVoterConfigModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.impex.impl.ClasspathImpExResource;
import de.hybris.platform.site.BaseSiteService;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;


@IntegrationTest
public class DefaultCxConfigurationServiceIntegrationTest extends ServicelayerTransactionalTest
{
	private static final String CURRENT_BASE_SITE_UID = "testSite";
	private static final BigDecimal CURRENT_BASE_SITE_MIN_AFFINITY = BigDecimal.valueOf(0.5);
	private static final Integer CURRENT_BASE_SITE_ACTION_RESULT_MAX_REPEAT = Integer.valueOf(1);
	private static final Set<String> CURRENT_BASE_SITE_LOGIN_ACTIONS = Sets.newHashSet("RECALCULATE", "ASYNC_PROCESS");
	private static final String CURRENT_BASE_SITE_URL_DEFAULT_CODE = "default";
	private static final String CURRENT_BASE_SITE_URL_DEFAULT_PATTERN = ".*";
	private static final Set<String> CURRENT_BASE_SITE_URL_DEFAULT_ACTIONS = Sets.newHashSet("LOAD");
	private static final String CURRENT_BASE_SITE_URL_CHECKOUT_CODE = "checkout";
	private static final String CURRENT_BASE_SITE_URL_CHECKOUT_PATTERN = ".*/checkout";
	private static final Set<String> CURRENT_BASE_SITE_URL_CHECKOUT_ACTIONS = Sets.newHashSet("ASYNC_PROCESS");
	private static final String CURRENT_BASE_SITE_URL_CART_CODE = "cart";
	private static final String CURRENT_BASE_SITE_URL_CART_PATTERN = ".*/checkout";
	private static final Set<String> CURRENT_BASE_SITE_URL_CART_ACTIONS = Sets.newHashSet("ASYNC_PROCESS", "UPDATE");

	private static final String BASE_SITE_1_UID = "testSite1";
	private static final BigDecimal BASE_SITE_1_MIN_AFFINITY = BigDecimal.valueOf(0.75);
	private static final Integer BASE_SITE_1_ACTION_RESULT_MAX_REPEAT = Integer.valueOf(2);
	private static final Set<String> BASE_SITE_1_LOGIN_ACTIONS = Sets.newHashSet("LOAD");
	private static final String BASE_SITE_1_URL_DEFAULT_CODE = "default";
	private static final String BASE_SITE_1_URL_DEFAULT_PATTERN = ".*";
	private static final Set<String> BASE_SITE_1_URL_DEFAULT_ACTIONS = Sets.newHashSet("LOAD");

	private static final String CURRENT_BASE_SITE_PERIODIC_DEFAULT_CODE = "default";
	private static final String CURRENT_BASE_SITE_PERIODIC_CHECKOUT_CODE = "checkout";
	private static final String CURRENT_BASE_SITE_PERIODIC_CART_CODE = "cart";
	private static final String BASE_SITE_1_PERIODIC_DEFAULT_CODE = "default";
	private static final Set<String> BASE_SITE_1_PERIODIC_DEFAULT_ACTIONS = Sets.newHashSet("LOAD");
	private static final Set<String> CURRENT_BASE_SITE_PERIODIC_DEFAULT_ACTIONS = Sets.newHashSet("LOAD");
	private static final Set<String> CURRENT_BASE_SITE_PERIODIC_CHECKOUT_ACTIONS = Sets.newHashSet("ASYNC_PROCESS");
	private static final Set<String> CURRENT_BASE_SITE_PERIODIC_CART_ACTIONS = Sets.newHashSet("ASYNC_PROCESS", "UPDATE");

	private static final String BASE_SITE_2_UID = "testSite2";

	private static final String BASE_SITE_3_UID = "testSite3";


	private static final String LOAD_ACTION_RESULT_MAX_REPEAT_PARAMETER = "4";
	private static final String MIN_AFFINITY_PARAMETER = "0.90";
	private static final String LOGIN_ACTIONS_PARAMETER = "RECALCULATE\\,ASYNC_PROCESS";

	private static final String CATALOG_WITH_MULTIPLE_BASE_SITE = "testCatalog";
	private static final String CATALOG_WITH_SINGLE_BASE_SITE = "testCatalog1";
	private static final String CATALOG_VERSION = "Online";
	private static final Integer ANONYMOUS_USER_MIN_REQUEST_VALUE = Integer.valueOf(2);
	private static final BigDecimal MIN_AFFINITY_VALUE = BigDecimal.valueOf(0.9);

	@Resource
	private ConfigurationService configurationService;

	@Resource
	private CxConfigurationService cxConfigurationService;

	@Resource
	private BaseSiteService baseSiteService;

	@Resource
	private CatalogVersionService catalogVersionService;


	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		importData(new ClasspathImpExResource("/personalizationservices/test/testdata_cxconfig.impex", "UTF-8"));
	}

	@Test
	public void findConfigurationForCurrentBaseSiteTest()
	{
		//having
		baseSiteService.setCurrentBaseSite(CURRENT_BASE_SITE_UID, false);

		//when
		final Optional<CxConfigModel> configuration = cxConfigurationService.getConfiguration();

		//then
		assertNotNull(configuration);
		assertTrue(configuration.isPresent());
		final CxConfigModel cxConfigModel = configuration.get();
		assertTrue(cxConfigModel.getBaseSites().stream().filter(bs -> StringUtils.equals(bs.getUid(), CURRENT_BASE_SITE_UID))
				.findAny().isPresent());
		assertEquals(CURRENT_BASE_SITE_MIN_AFFINITY, cxConfigModel.getMinAffinity());
		assertEquals(CURRENT_BASE_SITE_ACTION_RESULT_MAX_REPEAT, cxConfigModel.getActionResultMaxRepeat());
		assertEquals(CURRENT_BASE_SITE_LOGIN_ACTIONS, cxConfigModel.getUserChangedActions());
	}

	@Test
	public void findConfigurationForBaseSiteTest()
	{
		//having
		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(BASE_SITE_1_UID);

		//when
		final Optional<CxConfigModel> configuration = cxConfigurationService.getConfiguration(baseSite);

		//then
		assertNotNull(configuration);
		assertTrue(configuration.isPresent());
		final CxConfigModel cxConfigModel = configuration.get();
		assertTrue(cxConfigModel.getBaseSites().stream().filter(bs -> StringUtils.equals(bs.getUid(), BASE_SITE_1_UID)).findAny()
				.isPresent());
		assertEquals(BASE_SITE_1_MIN_AFFINITY, cxConfigModel.getMinAffinity());
		assertEquals(BASE_SITE_1_ACTION_RESULT_MAX_REPEAT, cxConfigModel.getActionResultMaxRepeat());
		assertEquals(BASE_SITE_1_LOGIN_ACTIONS, cxConfigModel.getUserChangedActions());
	}

	@Test
	public void findNoConfigurationForBaseSiteTest()
	{
		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(BASE_SITE_3_UID);

		final Optional<CxConfigModel> configuration = cxConfigurationService.getConfiguration(baseSite);

		assertNotNull(configuration);
		assertFalse(configuration.isPresent());
	}

	@Test(expected = IllegalArgumentException.class)
	public void findConfigurationForNullBaseSiteTest()
	{
		cxConfigurationService.getConfiguration(null);
	}

	@Test
	public void testGetValue()
	{
		//given
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(CATALOG_WITH_SINGLE_BASE_SITE,
				CATALOG_VERSION);

		//when
		final BigDecimal value = cxConfigurationService.getValue(catalogVersion, config -> config.getMinAffinity(), BigDecimal.ONE);

		//then
		assertTrue(MIN_AFFINITY_VALUE.equals(value));
	}

	@Test
	public void testGetValueFallback()
	{
		//given
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(CATALOG_WITH_SINGLE_BASE_SITE,
				CATALOG_VERSION);
		final Integer defaultValue = Integer.valueOf(1);

		//when
		final Integer value = cxConfigurationService.getValue(catalogVersion, config -> config.getAnonymousUserMinRequestNumber(),
				defaultValue);

		//then
		assertTrue(defaultValue.equals(value));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetValueWhenCatalogVersionIsNull()
	{
		//when
		cxConfigurationService.getValue(null, config -> config.getMinAffinity(), BigDecimal.ONE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetValueWhenAccessorIsNull()
	{
		//given
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(CATALOG_WITH_SINGLE_BASE_SITE,
				CATALOG_VERSION);

		//when
		cxConfigurationService.getValue(catalogVersion, null, BigDecimal.ONE);
	}

	@Test
	public void testGetValueWhenFallbackIsNull()
	{
		//given
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(CATALOG_WITH_SINGLE_BASE_SITE,
				CATALOG_VERSION);

		//when
		final Integer value = cxConfigurationService.getValue(catalogVersion, config -> config.getAnonymousUserMinRequestNumber(),
				null);

		//then
		assertNull(value);
	}

	@Test
	public void testGetValueWhenMultipleBasesite()
	{
		//given
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(CATALOG_WITH_MULTIPLE_BASE_SITE,
				CATALOG_VERSION);

		//when
		final Integer value = cxConfigurationService.getValue(catalogVersion, config -> config.getAnonymousUserMinRequestNumber(),
				Integer.valueOf(1));

		//then
		assertTrue(ANONYMOUS_USER_MIN_REQUEST_VALUE.equals(value));
	}

	@Test
	public void testGetValueFallbackWhenMultipleBasesite()
	{
		//given
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(CATALOG_WITH_MULTIPLE_BASE_SITE,
				CATALOG_VERSION);
		final BigDecimal defaultValue = BigDecimal.ONE;

		//when
		final BigDecimal value = cxConfigurationService.getValue(catalogVersion, config -> config.getMinAffinity(), defaultValue);

		//then
		assertTrue(defaultValue.equals(value));
	}

	@Test
	public void testGetActionResultMaxRepeatForCurrentBaseSite()
	{
		//having
		baseSiteService.setCurrentBaseSite(CURRENT_BASE_SITE_UID, false);

		//when
		final Integer actionResultMaxRepeat = cxConfigurationService.getActionResultMaxRepeat();

		//then
		assertNotNull(actionResultMaxRepeat);
		assertEquals(CURRENT_BASE_SITE_ACTION_RESULT_MAX_REPEAT, actionResultMaxRepeat);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetActionResultMaxRepeatForNullBaseSite()
	{
		cxConfigurationService.getActionResultMaxRepeat(null);
	}

	@Test
	public void testGetActionResultMaxRepeatForBaseSite()
	{
		//having
		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(BASE_SITE_1_UID);

		//when
		final Integer actionResultMaxRepeat = cxConfigurationService.getActionResultMaxRepeat(baseSite);

		//then
		assertNotNull(actionResultMaxRepeat);
		assertEquals(BASE_SITE_1_ACTION_RESULT_MAX_REPEAT, actionResultMaxRepeat);
	}


	@Test
	public void testNoActionResultMaxRepeatForBaseSiteShouldFallBack()
	{
		//having
		configurationService.getConfiguration().addProperty(PersonalizationservicesConstants.LOAD_ACTION_RESULT_MAX_REPEAT,
				LOAD_ACTION_RESULT_MAX_REPEAT_PARAMETER);

		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(BASE_SITE_3_UID);

		//when
		final Integer actionResultMaxRepeat = cxConfigurationService.getActionResultMaxRepeat(baseSite);

		//then
		assertEquals(Integer.valueOf(LOAD_ACTION_RESULT_MAX_REPEAT_PARAMETER), actionResultMaxRepeat);
	}

	@Test
	public void testGetMinAffinityForCurrentBaseSite()
	{
		//having
		baseSiteService.setCurrentBaseSite(CURRENT_BASE_SITE_UID, false);

		//when
		final BigDecimal minAffinity = cxConfigurationService.getMinAffinity();

		//then
		assertNotNull(minAffinity);
		assertTrue(CURRENT_BASE_SITE_MIN_AFFINITY.equals(minAffinity));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetMinAffinityForNullBaseSite()
	{
		cxConfigurationService.getMinAffinity(null);
	}

	@Test
	public void testGetMinAffinityForBaseSite()
	{
		//having
		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(BASE_SITE_1_UID);

		//when
		final BigDecimal minAffinity = cxConfigurationService.getMinAffinity(baseSite);

		//then
		assertNotNull(minAffinity);
		assertTrue(BASE_SITE_1_MIN_AFFINITY.equals(minAffinity));
	}


	@Test
	public void testNoMinAffinityForBaseSiteShouldFallBack()
	{
		//having
		configurationService.getConfiguration().addProperty(PersonalizationservicesConstants.MIN_AFFINITY_PROPERTY,
				MIN_AFFINITY_PARAMETER);

		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(BASE_SITE_3_UID);

		//when
		final BigDecimal minAffinity = cxConfigurationService.getMinAffinity(baseSite);

		//then
		assertNotNull(minAffinity);
		assertTrue(new BigDecimal(MIN_AFFINITY_PARAMETER).equals(minAffinity));
	}

	@Test
	public void testGetUserChangedActionsForCurrentBaseSite()
	{
		//having
		baseSiteService.setCurrentBaseSite(CURRENT_BASE_SITE_UID, false);

		//when
		final Set<String> userChangedActions = cxConfigurationService.getUserChangedActions();

		//then
		assertNotNull(userChangedActions);
		assertTrue(CURRENT_BASE_SITE_LOGIN_ACTIONS.equals(userChangedActions));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetUserChangedActionsForNullBaseSite()
	{
		cxConfigurationService.getUserChangedActions(null);
	}

	@Test
	public void testGetUserChangedActionsForBaseSite()
	{
		//having
		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(BASE_SITE_1_UID);

		//when
		final Set<String> userChangedActions = cxConfigurationService.getUserChangedActions(baseSite);

		//then
		assertNotNull(userChangedActions);
		assertTrue(BASE_SITE_1_LOGIN_ACTIONS.equals(userChangedActions));
	}


	@Test
	public void testNoUserChangedActionsForBaseSiteShouldFallBack()
	{
		//having
		configurationService.getConfiguration().addProperty(PersonalizationservicesConstants.USER_CHANGED_ACTIONS_PROPERTY,
				LOGIN_ACTIONS_PARAMETER);

		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(BASE_SITE_3_UID);

		//when
		final Set<String> userChangedActions = cxConfigurationService.getUserChangedActions(baseSite);

		//then
		assertNotNull(userChangedActions);
		final String actions = configurationService.getConfiguration()
				.getString(PersonalizationservicesConstants.USER_CHANGED_ACTIONS_PROPERTY);
		assertTrue(Sets.newHashSet(actions.split(",")).equals(userChangedActions));
	}

	@Test
	public void findUrlVoterConfigurationsForCurrentBaseSiteTest()
	{
		//having
		baseSiteService.setCurrentBaseSite(CURRENT_BASE_SITE_UID, false);

		//when
		final List<CxUrlVoterConfigModel> urlVoterConfigurations = cxConfigurationService.getUrlVoterConfigurations();

		//then
		assertNotNull(urlVoterConfigurations);
		assertEquals(3, urlVoterConfigurations.size());

		final Optional<CxUrlVoterConfigModel> defaultUrlVoterConfigModel = urlVoterConfigurations.stream()
				.filter(c -> StringUtils.equals(CURRENT_BASE_SITE_URL_DEFAULT_CODE, c.getCode())).findAny();
		assertTrue(defaultUrlVoterConfigModel.isPresent());
		assertEquals(CURRENT_BASE_SITE_URL_DEFAULT_PATTERN, defaultUrlVoterConfigModel.get().getUrlRegexp());
		assertEquals(CURRENT_BASE_SITE_URL_DEFAULT_ACTIONS, defaultUrlVoterConfigModel.get().getActions());

		final Optional<CxUrlVoterConfigModel> checkoutUrlVoterConfigModel = urlVoterConfigurations.stream()
				.filter(c -> StringUtils.equals(CURRENT_BASE_SITE_URL_CHECKOUT_CODE, c.getCode())).findAny();
		assertTrue(checkoutUrlVoterConfigModel.isPresent());
		assertEquals(CURRENT_BASE_SITE_URL_CHECKOUT_PATTERN, checkoutUrlVoterConfigModel.get().getUrlRegexp());
		assertEquals(CURRENT_BASE_SITE_URL_CHECKOUT_ACTIONS, checkoutUrlVoterConfigModel.get().getActions());

		final Optional<CxUrlVoterConfigModel> cartUrlVoterConfigModel = urlVoterConfigurations.stream()
				.filter(c -> StringUtils.equals(CURRENT_BASE_SITE_URL_CART_CODE, c.getCode())).findAny();
		assertTrue(cartUrlVoterConfigModel.isPresent());
		assertEquals(CURRENT_BASE_SITE_URL_CART_PATTERN, cartUrlVoterConfigModel.get().getUrlRegexp());
		assertEquals(CURRENT_BASE_SITE_URL_CART_ACTIONS, cartUrlVoterConfigModel.get().getActions());
	}

	@Test
	public void findUrlVoterConfigurationsForBaseSiteTest()
	{
		//having
		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(BASE_SITE_1_UID);

		//when
		final List<CxUrlVoterConfigModel> urlVoterConfigurations = cxConfigurationService.getUrlVoterConfigurations(baseSite);

		//then
		assertNotNull(urlVoterConfigurations);
		assertEquals(1, urlVoterConfigurations.size());

		final Optional<CxUrlVoterConfigModel> defaultUrlVoterConfigModel = urlVoterConfigurations.stream()
				.filter(c -> StringUtils.equals(BASE_SITE_1_URL_DEFAULT_CODE, c.getCode())).findAny();
		assertTrue(defaultUrlVoterConfigModel.isPresent());
		assertEquals(BASE_SITE_1_URL_DEFAULT_PATTERN, defaultUrlVoterConfigModel.get().getUrlRegexp());
		assertEquals(BASE_SITE_1_URL_DEFAULT_ACTIONS, defaultUrlVoterConfigModel.get().getActions());
	}

	@Test
	public void findNoUrlVoterConfigurationsForBaseSiteTest()
	{
		//having
		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(BASE_SITE_2_UID);

		//when
		final List<CxUrlVoterConfigModel> urlVoterConfigurations = cxConfigurationService.getUrlVoterConfigurations(baseSite);

		//then
		assertNotNull(urlVoterConfigurations);
		assertTrue(urlVoterConfigurations.isEmpty());
	}


	@Test(expected = IllegalArgumentException.class)
	public void findUrlVoterConfigurationsForNullBaseSiteTest()
	{
		cxConfigurationService.getUrlVoterConfigurations(null);
	}

	@Test
	public void findPeriodicVoterConfigurationsForCurrentBaseSiteTest()
	{
		//having
		baseSiteService.setCurrentBaseSite(CURRENT_BASE_SITE_UID, false);

		//when
		final Collection<CxPeriodicVoterConfigModel> periodicVoterConfigurations = cxConfigurationService
				.getPeriodicVoterConfigurations();

		//then
		assertNotNull(periodicVoterConfigurations);
		assertEquals(3, periodicVoterConfigurations.size());

		final Optional<CxPeriodicVoterConfigModel> defaultPeriodicVoterConfigModel = periodicVoterConfigurations.stream()
				.filter(c -> StringUtils.equals(CURRENT_BASE_SITE_PERIODIC_DEFAULT_CODE, c.getCode())).findAny();
		assertTrue(defaultPeriodicVoterConfigModel.isPresent());
		assertEquals(CURRENT_BASE_SITE_PERIODIC_DEFAULT_ACTIONS, defaultPeriodicVoterConfigModel.get().getActions());

		final Optional<CxPeriodicVoterConfigModel> checkoutPeriodicVoterConfigModel = periodicVoterConfigurations.stream()
				.filter(c -> StringUtils.equals(CURRENT_BASE_SITE_PERIODIC_CHECKOUT_CODE, c.getCode())).findAny();
		assertTrue(checkoutPeriodicVoterConfigModel.isPresent());
		assertEquals(CURRENT_BASE_SITE_PERIODIC_CHECKOUT_ACTIONS, checkoutPeriodicVoterConfigModel.get().getActions());

		final Optional<CxPeriodicVoterConfigModel> cartPeriodicVoterConfigModel = periodicVoterConfigurations.stream()
				.filter(c -> StringUtils.equals(CURRENT_BASE_SITE_PERIODIC_CART_CODE, c.getCode())).findAny();
		assertTrue(cartPeriodicVoterConfigModel.isPresent());
		assertEquals(CURRENT_BASE_SITE_PERIODIC_CART_ACTIONS, cartPeriodicVoterConfigModel.get().getActions());
	}

	@Test
	public void findPeriodicVoterConfigurationsForBaseSiteTest()
	{
		//having
		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(BASE_SITE_1_UID);

		//when
		final Collection<CxPeriodicVoterConfigModel> periodicConfigurations = cxConfigurationService
				.getPeriodicVoterConfigurations(baseSite);

		//then
		assertNotNull(periodicConfigurations);
		assertEquals(1, periodicConfigurations.size());

		final Optional<CxPeriodicVoterConfigModel> defaultPeriodicVoterConfigModel = periodicConfigurations.stream()
				.filter(c -> StringUtils.equals(BASE_SITE_1_PERIODIC_DEFAULT_CODE, c.getCode())).findAny();
		assertTrue(defaultPeriodicVoterConfigModel.isPresent());
		assertEquals(BASE_SITE_1_PERIODIC_DEFAULT_ACTIONS, defaultPeriodicVoterConfigModel.get().getActions());
	}

	@Test
	public void findNoPeriodicVoterConfigurationsForBaseSiteTest()
	{
		//having
		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(BASE_SITE_2_UID);

		//when
		final Collection<CxPeriodicVoterConfigModel> periodicVoterConfigurations = cxConfigurationService
				.getPeriodicVoterConfigurations(baseSite);

		//then
		assertNotNull(periodicVoterConfigurations);
		assertTrue(periodicVoterConfigurations.isEmpty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void findPeriodicVoterConfigurationsForNullBaseSiteTest()
	{
		cxConfigurationService.getPeriodicVoterConfigurations(null);
	}

	@Test
	public void testIsUserSegmentsStoreInSession()
	{
		//given
		baseSiteService.setCurrentBaseSite(BASE_SITE_1_UID, false);

		//when
		final Boolean result = cxConfigurationService.isUserSegmentsStoreInSession();

		//then
		assertTrue(result);
	}


	@Test
	public void testIsUserSegmentsStoreInSessionForBaseSite()
	{
		//given
		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(BASE_SITE_1_UID);

		//when
		final Boolean result = cxConfigurationService.isUserSegmentsStoreInSession(baseSite);

		//then
		assertTrue(result);
	}

	@Test
	public void testIsUserSegmentsNotStoreInSession()
	{
		//given
		baseSiteService.setCurrentBaseSite(CURRENT_BASE_SITE_UID, false);

		//when
		final Boolean result = cxConfigurationService.isUserSegmentsStoreInSession();

		//then
		assertFalse(result);
	}

	@Test
	public void testIsUserSegmentsNotStoreInSessionForBaseSite()
	{
		//given
		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(CURRENT_BASE_SITE_UID);

		//when
		final Boolean result = cxConfigurationService.isUserSegmentsStoreInSession(baseSite);

		//then
		assertFalse(result);
	}


}

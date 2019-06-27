/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.facades.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assume.assumeTrue;

import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.security.JaloSecurityException;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.LifecycleStrategiesTestChecker;
import de.hybris.platform.servicelayer.security.auth.InvalidCredentialsException;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


// test base class, do not execute as test
@ManualTest
public class LifecycleStrategiesIntegrationTestBase extends CPQFacadeLayerTest
{
	private static Logger LOG = Logger.getLogger(LifecycleStrategiesIntegrationTestBase.class);

	protected LifecycleStrategiesTestChecker lifecycleChecker;

	protected static final String ADMINISTRATOR = "Administrator";
	protected static final String ANONYMOUS = "Anonymous";
	protected static final String CPQ_DISPLAY = "CPQ_DISPLAY";
	protected static final String CPQ_DISPLAY_13 = "13";
	protected static final String CPQ_DISPLAY_17 = "17";
	protected static final String CPQ_DISPLAY_15 = "15";
	protected static final String CPQ_SECURITY = "CPQ_SECURITY";
	protected static final String NORTON = "NORTON";
	protected static final String MCAFEE = "MCAFEE";

	@Override
	public void initProviders()
	{
		ensureMockProvider();
	}


	@Before
	public void setUp() throws Exception
	{
		lifecycleChecker = selectStrategyTestChecker();
		prepareCPQData();
	}


	@Override
	public void importCPQTestData() throws ImpExException, Exception
	{
		super.importCPQTestData();
		importCPQUserData();
		importCsv("/sapproductconfigfacades/test/sapProductConfig_quote_testData.impex", "utf-8");
	}

	@Test
	public void testLinkToProduct() throws CommerceCartModificationException
	{
		// create default configuration ==>  config id is persisted
		final ConfigurationData configuration = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		lifecycleChecker.checkProductConfiguration(configuration.getConfigId(), ADMINISTRATOR, PRODUCT_CODE_YSAP_SIMPLE_POC);

		// add to cart ==> configId is linked to cart entry
		final String cartItemHandle = cpqCartFacade.addConfigurationToCart(configuration);
		lifecycleChecker.checkProductConfiguration(configuration.getConfigId(), ADMINISTRATOR, null, cartItemHandle, false);
	}

	@Before
	public void initializeKbKey()
	{
		KB_KEY_CPQ_LAPTOP_MUSIC.setProductCode(PRODUCT_CODE_CPQ_LAPTOP_MUSIC);
		KB_KEY_CPQ_LAPTOP_MUZAC.setProductCode(PRODUCT_CODE_CPQ_LAPTOP_MUZAC);
	}

	@Test
	public void testLinkToCartEntryForVariantThenRestore() throws CommerceCartModificationException
	{
		// create default configuration ==>  config id is persisted. Variant product code has been replaced with base product code
		final ConfigurationData configuration = cpqFacade.getConfiguration(KB_KEY_CPQ_LAPTOP_MUZAC);
		lifecycleChecker.checkProductConfiguration(configuration.getConfigId(), ADMINISTRATOR, PRODUCT_CODE_CPQ_LAPTOP);

		// add to cart ==> configId is linked to cart entry
		final String cartItemHandle = cpqCartFacade.addConfigurationToCart(configuration);
		lifecycleChecker.checkProductConfiguration(configuration.getConfigId(), ADMINISTRATOR, null, cartItemHandle, false);

		//restore config ==> 1 cfg attached to cart entry
		final ConfigurationData restordConfiguration = cpqCartFacade.restoreConfiguration(KB_KEY_CPQ_LAPTOP, cartItemHandle);
		assertEquals("Config IDs must match", restordConfiguration.getConfigId(), configuration.getConfigId());
		lifecycleChecker.checkProductConfiguration(restordConfiguration.getConfigId(), ADMINISTRATOR, null, cartItemHandle, false);
	}

	@Test
	public void testAddToCartThenConfigureTwice() throws CommerceCartModificationException
	{
		// create default configuration ==> config id is persisted
		final ConfigurationData configData = cpqFacade.getConfiguration(KB_KEY_CPQ_LAPTOP_MUZAC);
		lifecycleChecker.checkProductConfiguration(configData.getConfigId(), ADMINISTRATOR, PRODUCT_CODE_CPQ_LAPTOP);
		lifecycleChecker.checkNumberOfConfigsPersisted(1);

		// add to cart ==> configId is linked to cart entry
		final String cartItemHandle = cpqCartFacade.addConfigurationToCart(configData);
		lifecycleChecker.checkProductConfiguration(configData.getConfigId(), ADMINISTRATOR, null, cartItemHandle, false);
		lifecycleChecker.checkNumberOfConfigsPersisted(1);

		// configure ==> a draft exists, old config is still linked
		final ConfigurationData configData2 = cpqCartFacade.configureCartItem(cartItemHandle);
		lifecycleChecker.checkProductConfiguration(configData2.getConfigId(), ADMINISTRATOR, null, cartItemHandle, true);
		lifecycleChecker.checkProductConfiguration(configData.getConfigId(), ADMINISTRATOR, null, cartItemHandle, false);
		lifecycleChecker.checkNumberOfConfigsPersisted(2);

		// configure => new draft exists, old draft released, old config still there
		final ConfigurationData configData3 = cpqCartFacade.configureCartItem(cartItemHandle);
		lifecycleChecker.checkProductConfiguration(configData3.getConfigId(), ADMINISTRATOR, null, cartItemHandle, true);
		lifecycleChecker.checkProductConfiguration(configData.getConfigId(), ADMINISTRATOR, null, cartItemHandle, false);
		lifecycleChecker.checkConfigDeleted(configData2.getConfigId(), null);
		lifecycleChecker.checkNumberOfConfigsPersisted(2);

		// add to cart ==> old config deleted
		cpqCartFacade.addConfigurationToCart(configData3);
		lifecycleChecker.checkProductConfiguration(configData3.getConfigId(), ADMINISTRATOR, null, cartItemHandle, false);
		lifecycleChecker.checkConfigDeleted(configData.getConfigId(), null);
		lifecycleChecker.checkNumberOfConfigsPersisted(1);
	}

	@Test
	public void testAddToCartThenConfigureOnSameDraft() throws CommerceCartModificationException
	{
		// create default configuration ==> config id is persisted
		final ConfigurationData configData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		lifecycleChecker.checkNumberOfConfigsPersisted(1);

		// add to cart ==> configId is linked to cart entry
		final String cartItemHandle = cpqCartFacade.addConfigurationToCart(configData);
		lifecycleChecker.checkProductConfiguration(configData.getConfigId(), ADMINISTRATOR, null, cartItemHandle, false);
		lifecycleChecker.checkNumberOfConfigsPersisted(1);

		// configure ==> a draft exists, old config is still linked
		final ConfigurationData configData2 = cpqCartFacade.configureCartItem(cartItemHandle);
		lifecycleChecker.checkProductConfiguration(configData2.getConfigId(), ADMINISTRATOR, null, cartItemHandle, true);
		lifecycleChecker.checkProductConfiguration(configData.getConfigId(), ADMINISTRATOR, null, cartItemHandle, false);
		lifecycleChecker.checkNumberOfConfigsPersisted(2);

		// configure on existing draft
		final ConfigurationData configData3 = cpqCartFacade.configureCartItemOnExistingDraft(cartItemHandle);
		lifecycleChecker.checkProductConfiguration(configData3.getConfigId(), ADMINISTRATOR, null, cartItemHandle, true);
		assertEquals(configData2.getConfigId(), configData3.getConfigId());
		lifecycleChecker.checkProductConfiguration(configData.getConfigId(), ADMINISTRATOR, null, cartItemHandle, false);
		lifecycleChecker.checkNumberOfConfigsPersisted(2);

		// add to cart ==> old config deleted
		cpqCartFacade.addConfigurationToCart(configData3);
		lifecycleChecker.checkProductConfiguration(configData3.getConfigId(), ADMINISTRATOR, null, cartItemHandle, false);
		lifecycleChecker.checkConfigDeleted(configData.getConfigId(), null);
		lifecycleChecker.checkNumberOfConfigsPersisted(1);
	}

	@Test
	public void testAddToCartDirectlyThenConfigure() throws CommerceCartModificationException, InvalidCredentialsException
	{
		// add to cart ==> no config exists yet (config will be created 'dark' by some populator)
		final CartModificationData addToCart = cartFacade.addToCart(PRODUCT_CODE_YSAP_SIMPLE_POC, 1);
		login(USER_NAME, PASSWORD);
		final String cartItemHandle = cartFacade.getSessionCart().getEntries().get(0).getItemPK();

		final String configId = cpqAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry(cartItemHandle);
		lifecycleChecker.checkNumberOfConfigsPersisted(1);

		// configure ==> a draft exists
		final ConfigurationData configData = cpqCartFacade.configureCartItem(cartItemHandle);
		lifecycleChecker.checkProductConfiguration(configData.getConfigId(), USER_NAME, null, cartItemHandle, true);
		lifecycleChecker.checkNumberOfConfigsPersisted(2);

		// addToCart => not drafted anymore
		cpqCartFacade.addConfigurationToCart(configData);
		lifecycleChecker.checkProductConfiguration(configData.getConfigId(), USER_NAME, null, cartItemHandle, false);
		lifecycleChecker.checkNumberOfConfigsPersisted(1);
	}

	@Test
	public void testLinkToCartEntryThenRemove() throws CommerceCartModificationException, InvalidCredentialsException
	{
		// create default configuration ==>  config id is persisted
		final ConfigurationData configuration = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		lifecycleChecker.checkProductConfiguration(configuration.getConfigId(), ADMINISTRATOR,
				KB_KEY_Y_SAP_SIMPLE_POC.getProductCode());

		// add to cart ==> configId is linked to cart entry
		final String cartItemHandle = cpqCartFacade.addConfigurationToCart(configuration);
		lifecycleChecker.checkProductConfiguration(configuration.getConfigId(), ADMINISTRATOR, null, cartItemHandle, false);

		final ConfigurationData darftConfiguration = cpqCartFacade.configureCartItem(cartItemHandle);
		lifecycleChecker.checkProductConfiguration(darftConfiguration.getConfigId(), ADMINISTRATOR, null, cartItemHandle, true);

		login(USER_NAME, PASSWORD);

		//remove product configuration from ProductConfiguration table
		final Integer entryNum = cartFacade.getSessionCart().getEntries().get(0).getEntryNumber();
		cartFacade.updateCartEntry(entryNum, 0);
		lifecycleChecker.checkConfigDeleted(configuration.getConfigId(), cartItemHandle);
		lifecycleChecker.checkConfigDeleted(darftConfiguration.getConfigId(), cartItemHandle);
	}

	@Test
	public void testCreateConfigWithLogInUser() throws CommerceCartModificationException, InvalidCredentialsException
	{
		login(USER_NAME, PASSWORD);

		// create default configuration ==>  config id is persisted
		final ConfigurationData configuration = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		lifecycleChecker.checkProductConfiguration(configuration.getConfigId(), USER_NAME,
				KB_KEY_Y_SAP_SIMPLE_POC.getProductCode());
	}

	@Test
	public void testUpdateUserAfterLogIn() throws CommerceCartModificationException, InvalidCredentialsException
	{
		// create three default configurations ==>  config id is persisted
		final ConfigurationData config1 = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		final ConfigurationData config2 = cpqFacade.getConfiguration(KB_KEY_CPQ_LAPTOP);
		final ConfigurationData config3 = cpqFacade.getConfiguration(KB_KEY_CPQ_HOME_THEATER);

		lifecycleChecker.checkProductConfiguration(config1.getConfigId(), ADMINISTRATOR, KB_KEY_Y_SAP_SIMPLE_POC.getProductCode());
		lifecycleChecker.checkProductConfiguration(config2.getConfigId(), ADMINISTRATOR, KB_KEY_CPQ_LAPTOP.getProductCode());
		lifecycleChecker.checkProductConfiguration(config3.getConfigId(), ADMINISTRATOR, KB_KEY_CPQ_HOME_THEATER.getProductCode());

		login(USER_NAME, PASSWORD);

		lifecycleChecker.checkProductConfiguration(config1.getConfigId(), USER_NAME, KB_KEY_Y_SAP_SIMPLE_POC.getProductCode());
		lifecycleChecker.checkProductConfiguration(config2.getConfigId(), USER_NAME, KB_KEY_CPQ_LAPTOP.getProductCode());
		lifecycleChecker.checkProductConfiguration(config3.getConfigId(), USER_NAME, KB_KEY_CPQ_HOME_THEATER.getProductCode());
	}

	/**
	 * Further logic needs to be pushed to facades, therefore ignore for the time being
	 *
	 * @throws InvalidCredentialsException
	 */
	@Test
	public void testSanityCheckToProductLinkPersistent() throws InvalidCredentialsException
	{
		assumeTrue(isPersistentLifecycle());
		//Log in and create default configuration with product code 'CPQ_LAPTOP'
		login(USER_NAME, PASSWORD);
		final ConfigurationData config = cpqFacade.getConfiguration(KB_KEY_CPQ_LAPTOP);

		lifecycleChecker.checkProductConfiguration(config.getConfigId(), USER_NAME, PRODUCT_CODE_CPQ_LAPTOP);
		lifecycleChecker.checkNumberOfConfigsPersisted(1);

		logout();

		// reconfigure the same product
		final ConfigurationData configAnonymus = cpqFacade.getConfiguration(KB_KEY_CPQ_LAPTOP);

		lifecycleChecker.checkProductConfiguration(configAnonymus.getConfigId(), ANONYMOUS, PRODUCT_CODE_CPQ_LAPTOP);
		lifecycleChecker.checkNumberOfConfigsPersisted(2);

		//Log in as same user as before
		login(USER_NAME, PASSWORD);

		// old for CPQ_LAPTOP should be deleted, the one created with the anoymous user should remain
		lifecycleChecker.checkNumberOfConfigsPersisted(1);
		lifecycleChecker.checkConfigDeleted(config.getConfigId(), null);
		lifecycleChecker.checkProductConfiguration(configAnonymus.getConfigId(), USER_NAME, PRODUCT_CODE_CPQ_LAPTOP);

		// try to configure without exception
		cpqFacade.getConfiguration(KB_KEY_CPQ_LAPTOP);
		lifecycleChecker.checkProductConfiguration(configAnonymus.getConfigId(), USER_NAME, PRODUCT_CODE_CPQ_LAPTOP);
	}

	@Test
	public void testSanityCheckToProductLinkDefault() throws InvalidCredentialsException
	{
		assumeTrue(isDefaultLifecycle());
		//Log in and create default configuration with product code 'CPQ_LAPTOP'
		login(USER_NAME, PASSWORD);
		final ConfigurationData config = cpqFacade.getConfiguration(KB_KEY_CPQ_LAPTOP);

		lifecycleChecker.checkProductConfiguration(config.getConfigId(), USER_NAME, PRODUCT_CODE_CPQ_LAPTOP);
		lifecycleChecker.checkNumberOfConfigsPersisted(1);

		logout();

		// reconfigure the same product
		final ConfigurationData configAnonymus = cpqFacade.getConfiguration(KB_KEY_CPQ_LAPTOP);

		lifecycleChecker.checkProductConfiguration(configAnonymus.getConfigId(), ANONYMOUS, PRODUCT_CODE_CPQ_LAPTOP);
		//after logout, all sessions are gone -> we expect one CFG in the session
		lifecycleChecker.checkNumberOfConfigsPersisted(1);


		//Log in as same user as before
		login(USER_NAME, PASSWORD);

		// old for CPQ_LAPTOP should be deleted, the one created with the anonymous user should remain
		lifecycleChecker.checkNumberOfConfigsPersisted(1);
		lifecycleChecker.checkConfigDeleted(config.getConfigId(), null);
		lifecycleChecker.checkProductConfiguration(configAnonymus.getConfigId(), USER_NAME, PRODUCT_CODE_CPQ_LAPTOP);

		// try to configure without exception
		cpqFacade.getConfiguration(KB_KEY_CPQ_LAPTOP);
		lifecycleChecker.checkProductConfiguration(configAnonymus.getConfigId(), USER_NAME, PRODUCT_CODE_CPQ_LAPTOP);
	}

	@Test
	public void testReleaseConfigurationsOnSessionExpire() throws JaloSecurityException
	{
		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getAnonymousCustomer());
		// create three default configurations ==>  config id is persisted
		final ConfigurationData config1 = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		final ConfigurationData config2 = cpqFacade.getConfiguration(KB_KEY_CPQ_LAPTOP);
		final ConfigurationData config3 = cpqFacade.getConfiguration(KB_KEY_CPQ_HOME_THEATER);

		lifecycleChecker.checkProductConfiguration(config1.getConfigId(), ANONYMOUS, KB_KEY_Y_SAP_SIMPLE_POC.getProductCode());
		lifecycleChecker.checkProductConfiguration(config2.getConfigId(), ANONYMOUS, KB_KEY_CPQ_LAPTOP.getProductCode());
		lifecycleChecker.checkProductConfiguration(config3.getConfigId(), ANONYMOUS, KB_KEY_CPQ_HOME_THEATER.getProductCode());

		//close session
		JaloSession.getCurrentSession().close();
		// creates a fresh session
		establishJaloSession(JaloSession.getCurrentSession());

		lifecycleChecker.checkConfigDeleted(config1.getConfigId(), null);
		lifecycleChecker.checkConfigDeleted(config2.getConfigId(), null);
		lifecycleChecker.checkConfigDeleted(config3.getConfigId(), null);
	}

	@Test
	public void testOrderProcessPersistent() throws Exception
	{
		assumeTrue(isPersistentLifecycle());
		final PrepareProcessTestReturnContainer ret = prepareProcessTest();

		// 3) checkout ==> config is now linked with order entry instead of cart entry
		final OrderData order = validateCartAndPlaceOrder(true);
		assertNotNull("We expect an order as result of placeOrder", order);
		LOG.info("testOrderProcess, order code: " + order.getCode());
		final int size = order.getEntries().size();
		LOG.info("testOrderProcess, number of order entries: " + size);
		assertEquals("We expect one order entry", 1, size);
		final OrderEntryData orderEntry = order.getEntries().get(0);
		final String orderItemHandle = orderEntry.getItemPK();
		assertNotEquals(ret.cartItemHandle, orderItemHandle);

		lifecycleChecker.checkNumberOfConfigsPersisted(1);
		lifecycleChecker.checkProductConfiguration(ret.configIdBeforeCheckout, USER_NAME, null, orderItemHandle, false);
		lifecycleChecker.checkConfigDeleted(null, ret.cartItemHandle);

		// 4) logout/login for new session ==> when we read the config we get the very same id as before checkout
		makeNewSessionByLoggingOutAndIn(USER_NAME);

		final ConfigurationOverviewData configOverview = configOrderIntegrationFacade.getConfiguration(order.getCode(),
				orderEntry.getEntryNumber());
		final String configIdAfterCheckout = configOverview.getId();
		assertEquals(ret.configIdBeforeCheckout, configIdAfterCheckout);
		lifecycleChecker.checkNumberOfConfigsPersisted(1);
		lifecycleChecker.checkProductConfiguration(ret.configIdBeforeCheckout, USER_NAME, null, orderItemHandle, false);
	}

	@Test
	public void testOrderProcessDefault()
			throws CommerceCartModificationException, InvalidCartException, InvalidCredentialsException
	{
		assumeTrue(isDefaultLifecycle());
		final PrepareProcessTestReturnContainer ret = prepareProcessTest();

		// 3) checkout ==> config is released
		final OrderData order = validateCartAndPlaceOrder(true);
		final OrderEntryData orderEntry = order.getEntries().get(0);
		final String orderItemHandle = orderEntry.getItemPK();
		assertNotEquals(ret.cartItemHandle, orderItemHandle);

		lifecycleChecker.checkNumberOfConfigsPersisted(0);
		lifecycleChecker.checkConfigDeleted(ret.configIdBeforeCheckout, ret.cartItemHandle);

		// 4) logout/login for new session ==> when we read the config we get a new id compared to before checkout
		makeNewSessionByLoggingOutAndIn(USER_NAME);

		final ConfigurationOverviewData configOverview = configOrderIntegrationFacade.getConfiguration(order.getCode(),
				orderEntry.getEntryNumber());
		final String configIdAfterCheckout = configOverview.getId();
		assertNotEquals(ret.configIdBeforeCheckout, configIdAfterCheckout);
		lifecycleChecker.checkNumberOfConfigsPersisted(0);
		lifecycleChecker.checkConfigDeleted(configIdAfterCheckout, ret.cartItemHandle);
	}

	@Test
	public void testAddToCartDirectlyForVariant() throws CommerceCartModificationException, InvalidCredentialsException
	{
		authenticationService.login(USER_NAME, PASSWORD);
		customerFacade.loginSuccess();
		cartFacade.removeSessionCart();

		// add to cart ==> no config exists yet
		final CartModificationData addToCart = cartFacade.addToCart(PRODUCT_CODE_CPQ_LAPTOP_MUZAC, 1);
		final List<OrderEntryData> cartEntries = cartFacade.getSessionCart().getEntries();
		assertEquals("One cart entry expected", 1, cartEntries.size());
		final String cartItemHandle = cartEntries.get(0).getItemPK();
		LOG.info("New entry key: " + cartItemHandle);
		final String configId = cpqAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry(cartItemHandle);
		assertNull("No configuration must exist for variant at this point", configId);
		lifecycleChecker.checkNumberOfConfigsPersisted(0);


		//launch configuration. We expect a configuration
		final ConfigurationData configurationFromCart = cpqCartFacade.configureCartItem(cartItemHandle);
		lifecycleChecker.checkNumberOfConfigsPersisted(1);
		lifecycleChecker.checkProductConfiguration(configurationFromCart.getConfigId(), USER_NAME, null, cartItemHandle, true);

		//Session cart hasn't been changed at this point
		final List<OrderEntryData> entriesAfterConfigureFromCart = cartFacade.getSessionCart().getEntries();
		assertEquals("One cart entry expected", 1, entriesAfterConfigureFromCart.size());
		assertEquals("Cart must contain the initial product at this point", PRODUCT_CODE_CPQ_LAPTOP_MUZAC,
				entriesAfterConfigureFromCart.get(0).getProduct().getCode());

		//Re-enter configuration from product. We get a new configuration
		final ConfigurationData config = cpqFacade.getConfiguration(KB_KEY_CPQ_LAPTOP);
		assertNotEquals("Both configurations must differ", config.getConfigId(), configurationFromCart.getConfigId());
		lifecycleChecker.checkProductConfiguration(configurationFromCart.getConfigId(), USER_NAME, null, cartItemHandle, true);

		//Add new configuration to cart
		final String cartItemHandleSecondItem = cpqCartFacade.addConfigurationToCart(config);
		final List<OrderEntryData> entriesAfterCartUpdate = cartFacade.getSessionCart().getEntries();
		assertEquals("2 cart entries expected", 2, entriesAfterCartUpdate.size());


		//2 configurations attached to 2 entries. The configuration attached to the first entry is still in draft status
		lifecycleChecker.checkNumberOfConfigsPersisted(2);
		lifecycleChecker.checkProductConfiguration(configurationFromCart.getConfigId(), USER_NAME, null, cartItemHandle, true);
		lifecycleChecker.checkProductConfiguration(config.getConfigId(), USER_NAME, null, cartItemHandleSecondItem, false);
	}

	@Test
	public void testAddVariantDirectlyToCarAndProveDraftHandlingPersistent()
			throws CommerceCartModificationException, InvalidCredentialsException
	{
		assumeTrue(isPersistentLifecycle());
		authenticationService.login(USER_NAME, PASSWORD);
		customerFacade.loginSuccess();
		cartFacade.removeSessionCart();

		//Add to cart ==> no config exists yet
		LOG.info("Add 'CPQ_LAPTOP_MUSIC' variant directly to cart");
		final CartModificationData addToCart = cartFacade.addToCart(PRODUCT_CODE_CPQ_LAPTOP_MUSIC, 1);
		//Get cart entries
		final List<OrderEntryData> cartEntries = cartFacade.getSessionCart().getEntries();
		assertEquals("Only one cart entry is expected: ", 1, cartEntries.size());
		final String cartItemHandle = cartEntries.get(0).getItemPK();
		LOG.info("New entry key: " + cartItemHandle);
		//Get 'configId' for cart entry
		final String configId = cpqAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry(cartItemHandle);
		assertNull("No configuration should be existing for variant at this point: ", configId);
		lifecycleChecker.checkNumberOfConfigsPersisted(0);

		//Get order entry. We expect there is not any configuration or draft are attached to the order entry
		AbstractOrderEntryModel orderEntry = productConfigurationPersistenceService.getOrderEntryByPK(cartItemHandle);
		assertNotNull("Order entry should not be null: ", orderEntry);
		assertNull("There should not be any product configuration attached to the order: ", orderEntry.getProductConfiguration());
		assertNull("There should not be any draft attached to the order: ", orderEntry.getProductConfigurationDraft());

		//Launch configuration process from cart
		LOG.info("By launching the configuration process from the cart a draft should be created and attachted to the order");
		ConfigurationData configurationFromCart = cpqCartFacade.configureCartItem(cartItemHandle);
		//Get order entry. We expect a draft is created and attached to the order entry
		orderEntry = productConfigurationPersistenceService.getOrderEntryByPK(cartItemHandle);
		assertNull("There should not be any product configuration attached to the order: ", orderEntry.getProductConfiguration());
		assertNotNull("There should be a draft attached to the order: ", orderEntry.getProductConfigurationDraft());
		final String oldDraftConfigId = orderEntry.getProductConfigurationDraft().getConfigurationId();
		lifecycleChecker.checkNumberOfConfigsPersisted(1);

		//Launch configuration from cart again
		LOG.info("Launch the configuration process from the cart again");
		configurationFromCart = cpqCartFacade.configureCartItem(cartItemHandle);
		orderEntry = productConfigurationPersistenceService.getOrderEntryByPK(cartItemHandle);
		assertNull("There should not be any product configuration attached to the order: ", orderEntry.getProductConfiguration());
		assertNotNull("There should be a draft attached to the order: ", orderEntry.getProductConfigurationDraft());
		final String newDraftConfigId = orderEntry.getProductConfigurationDraft().getConfigurationId();
		lifecycleChecker.checkNumberOfConfigsPersisted(1);
		assertNotEquals("We acpect that draft configIds are not equal: ", oldDraftConfigId, newDraftConfigId);

		// add to cart ==> draft should be deleted
		LOG.info("Add the configuration to the cart the draft should be deleted");
		cpqCartFacade.addConfigurationToCart(configurationFromCart);
		orderEntry = productConfigurationPersistenceService.getOrderEntryByPK(cartItemHandle);
		assertNotNull("There should be a product configuration attached to the order: ", orderEntry.getProductConfiguration());
		assertNull("There should not be any draft attached to the order: ", orderEntry.getProductConfigurationDraft());
		assertEquals("The draft should become a product configuration: ", newDraftConfigId,
				orderEntry.getProductConfiguration().getConfigurationId());
		lifecycleChecker.checkNumberOfConfigsPersisted(1);

		//Launch configuration from cart again
		LOG.info("Launch the configuration process from the cart again");
		configurationFromCart = cpqCartFacade.configureCartItem(cartItemHandle);
		orderEntry = productConfigurationPersistenceService.getOrderEntryByPK(cartItemHandle);
		assertNotNull("There should be a product configuration attached to the order: ", orderEntry.getProductConfiguration());
		assertNotNull("There should be a draft attached to the order: ", orderEntry.getProductConfigurationDraft());
		lifecycleChecker.checkNumberOfConfigsPersisted(2);
	}

	@Test
	public void testAddVariantDirectlyToCarAndProveDraftHandlingDefault()
			throws CommerceCartModificationException, InvalidCredentialsException
	{
		assumeTrue(isDefaultLifecycle());
		authenticationService.login(USER_NAME, PASSWORD);
		customerFacade.loginSuccess();
		cartFacade.removeSessionCart();

		//Add to cart ==> no config exists yet
		LOG.info("Add 'CPQ_LAPTOP_MUSIC' variant directly to cart");
		final CartModificationData addToCart = cartFacade.addToCart(PRODUCT_CODE_CPQ_LAPTOP_MUZAC, 1);
		//Get cart entries
		final List<OrderEntryData> cartEntries = cartFacade.getSessionCart().getEntries();
		assertEquals("Only one cart entry is expected: ", 1, cartEntries.size());
		final String cartItemHandle = cartEntries.get(0).getItemPK();
		String configId = cpqAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry(cartItemHandle);
		String oldDraftConfigId = cpqAbstractOrderEntryLinkStrategy.getDraftConfigIdForCartEntry(cartItemHandle);
		assertNull("No configId should be existing: ", configId);
		assertNull("No draft configId should be existing: ", oldDraftConfigId);
		lifecycleChecker.checkNumberOfConfigsPersisted(0);

		//Launch configuration process from cart
		LOG.info("By launching the configuration process from the cart a draft configId should be stored in the session");
		ConfigurationData configurationFromCart = cpqCartFacade.configureCartItem(cartItemHandle);
		oldDraftConfigId = cpqAbstractOrderEntryLinkStrategy.getDraftConfigIdForCartEntry(cartItemHandle);
		assertNotNull("ConfigId should be existing: ", configurationFromCart.getConfigId());
		assertNotNull("Draft configId should be existing: ", oldDraftConfigId);
		lifecycleChecker.checkNumberOfConfigsPersisted(1);

		//Launch configuration from cart again. Old draft to be discarded
		LOG.info("Launch the configuration process from the cart again");
		configurationFromCart = cpqCartFacade.configureCartItem(cartItemHandle);
		String newDraftConfigId = cpqAbstractOrderEntryLinkStrategy.getDraftConfigIdForCartEntry(cartItemHandle);
		assertNotNull("New draft configId should be existing: ", newDraftConfigId);
		assertNotEquals("We expect that old and new draft configIds are not equal: ", oldDraftConfigId, newDraftConfigId);
		lifecycleChecker.checkNumberOfConfigsPersisted(1);

		// add to cart ==> draft configId should be deleted from the session
		LOG.info("Add the configuration to the cart the draft configId should be deleted from the session");
		cpqCartFacade.addConfigurationToCart(configurationFromCart);
		configId = cpqAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry(cartItemHandle);
		newDraftConfigId = cpqAbstractOrderEntryLinkStrategy.getDraftConfigIdForCartEntry(cartItemHandle);
		assertNotNull("ConfigId should be existing: ", configId);
		assertNull("No draft configId should be existing: ", newDraftConfigId);
		lifecycleChecker.checkNumberOfConfigsPersisted(1);

		//Launch configuration from cart again
		LOG.info("Launch the configuration process from the cart again");
		configurationFromCart = cpqCartFacade.configureCartItem(cartItemHandle);
		configId = cpqAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry(cartItemHandle);
		newDraftConfigId = cpqAbstractOrderEntryLinkStrategy.getDraftConfigIdForCartEntry(cartItemHandle);
		assertNotNull("ConfigId should be existing: ", configId);
		assertNotNull("New draft configId should be existing: ", newDraftConfigId);
		lifecycleChecker.checkNumberOfConfigsPersisted(2);
	}

	@Test
	public void testQuoteProcessPersistent() throws Exception
	{
		assumeTrue(isPersistentLifecycle());
		final PrepareProcessTestReturnContainer ret = prepareProcessTest();

		// 3) request quote ==> config is now linked with order entry instead of cart entry
		QuoteData quote = defaultQuoteFacade.initiateQuote();
		assertNotNull("We expect an order as result of placeOrder", quote);
		LOG.info("testQuoteProcess, quote code: " + quote.getCode());
		final int size = quote.getEntries().size();
		LOG.info("testQuoteProcess, number of quote entries: " + size);
		assertEquals("We expect one order entry", 1, size);
		OrderEntryData quoteEntry = quote.getEntries().get(0);
		String quoteItemHandle = quoteEntry.getItemPK();
		assertNotEquals(ret.cartItemHandle, quoteItemHandle);

		lifecycleChecker.checkNumberOfConfigsPersisted(1);
		lifecycleChecker.checkProductConfiguration(ret.configIdBeforeCheckout, USER_NAME, null, quoteItemHandle, false);
		lifecycleChecker.checkConfigDeleted(null, ret.cartItemHandle);

		// 4) logout/login for new session ==> and start quote edit process
		makeNewSessionByLoggingOutAndIn(USER_NAME);
		defaultQuoteFacade.enableQuoteEdit(quote.getCode());
		quoteEntry = cartFacade.getSessionCart().getEntries().get(0);
		final String quoteCartItemHandle = quoteEntry.getItemPK();
		final String quoteCartConfigId = cpqAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry(quoteCartItemHandle);
		lifecycleChecker.checkNumberOfConfigsPersisted(2);
		lifecycleChecker.checkProductConfiguration(ret.configIdBeforeCheckout, USER_NAME, null, quoteItemHandle, false);
		lifecycleChecker.checkProductConfiguration(quoteCartConfigId, USER_NAME, null, quoteCartItemHandle, false);

		// 5) submit quote ==> the old quote data is discarded and the quote including configuration is updated from the quote cart
		cartService.getSessionCart().setUser(realUserService.getUserForUID(USER_NAME2));
		defaultQuoteFacade.submitQuote(quote.getCode());
		makeNewSessionByLoggingOutAndIn(USER_NAME);

		final ConfigurationOverviewData configOverview = configQuoteIntegrationFacade.getConfiguration(quote.getCode(),
				quoteEntry.getEntryNumber());
		final String configIdAfterCheckout = configOverview.getId();
		assertEquals(quoteCartConfigId, configIdAfterCheckout);
		// update quote item handle (as the quote is cloned during submit, the item handle is outdated)
		realUserService.setCurrentUser(realUserService.getUserForUID(USER_NAME2));
		quote = defaultQuoteFacade.getQuoteForCode(quote.getCode());
		quoteEntry = quote.getEntries().get(0);
		quoteItemHandle = quoteEntry.getItemPK();
		lifecycleChecker.checkNumberOfConfigsPersisted(1);
		lifecycleChecker.checkProductConfiguration(quoteCartConfigId, USER_NAME, null, quoteItemHandle, false);
		lifecycleChecker.checkConfigDeleted(ret.configIdBeforeCheckout, quoteCartItemHandle);
	}

	@Test
	public void testQuoteProcessDefault()
			throws CommerceCartModificationException, InvalidCartException, InvalidCredentialsException
	{
		assumeTrue(isDefaultLifecycle());
		final PrepareProcessTestReturnContainer ret = prepareProcessTest();

		// 3) request quote  ==> config is released
		final QuoteData quote = defaultQuoteFacade.initiateQuote();
		final OrderEntryData orderEntry = quote.getEntries().get(0);
		final String quoteItemHandle = orderEntry.getItemPK();
		assertNotEquals(ret.cartItemHandle, quoteItemHandle);


		lifecycleChecker.checkNumberOfConfigsPersisted(0);
		lifecycleChecker.checkConfigDeleted(ret.configIdBeforeCheckout, ret.cartItemHandle);

		// 4) logout/login for new session ==> wehen we read the config we get a new id compared to before checkout
		makeNewSessionByLoggingOutAndIn(USER_NAME);

		final ConfigurationOverviewData configOverview = configQuoteIntegrationFacade.getConfiguration(quote.getCode(),
				orderEntry.getEntryNumber());
		final String configIdAfterCheckout = configOverview.getId();
		assertNotEquals(ret.configIdBeforeCheckout, configIdAfterCheckout);
		lifecycleChecker.checkNumberOfConfigsPersisted(0);
		lifecycleChecker.checkConfigDeleted(configIdAfterCheckout, ret.cartItemHandle);
	}

	protected PrepareProcessTestReturnContainer prepareProcessTest()
			throws CommerceCartModificationException, InvalidCredentialsException
	{
		// 1) anonymous user configures and adds config to cart
		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getAnonymousCustomer());

		final ConfigurationData configData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		final String configIdBeforeCheckout = configData.getConfigId();
		lifecycleChecker.checkNumberOfConfigsPersisted(1);
		lifecycleChecker.checkProductConfiguration(configIdBeforeCheckout, ANONYMOUS, PRODUCT_CODE_YSAP_SIMPLE_POC);

		final String cartItemHandle = cpqCartFacade.addConfigurationToCart(configData);
		lifecycleChecker.checkNumberOfConfigsPersisted(1);
		lifecycleChecker.checkProductConfiguration(configIdBeforeCheckout, ANONYMOUS, null, cartItemHandle, false);

		// 2) user logs in ==> user updated
		login(USER_NAME, PASSWORD);
		lifecycleChecker.checkNumberOfConfigsPersisted(1);
		lifecycleChecker.checkProductConfiguration(configIdBeforeCheckout, USER_NAME, null, cartItemHandle, false);
		final PrepareProcessTestReturnContainer ret = new PrepareProcessTestReturnContainer();
		ret.configIdBeforeCheckout = configIdBeforeCheckout;
		ret.cartItemHandle = cartItemHandle;
		return ret;
	}

	protected void makeNewSessionByLoggingOutAndIn(final String userName) throws InvalidCredentialsException
	{
		logout();
		login(userName, PASSWORD);
	}

	public static class PrepareProcessTestReturnContainer
	{
		public String configIdBeforeCheckout;
		public String cartItemHandle;
	}
}

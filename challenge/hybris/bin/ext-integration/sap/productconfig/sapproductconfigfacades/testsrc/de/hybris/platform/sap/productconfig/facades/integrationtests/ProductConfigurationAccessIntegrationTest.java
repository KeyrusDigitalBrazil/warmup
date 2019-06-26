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

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assume.assumeTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.impl.DefaultQuoteFacade;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.exceptions.ProductConfigurationAccessException;
import de.hybris.platform.sap.productconfig.services.impl.ProductConfigurationServiceImpl;
import de.hybris.platform.servicelayer.security.auth.InvalidCredentialsException;

import javax.annotation.Resource;

import org.hamcrest.core.AnyOf;
import org.hamcrest.core.IsEqual;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * This test checks the negative cases, when accessing a product configuration shall fail. The positive cases are
 * already tests by all other integration tests.
 */
@IntegrationTest
public class ProductConfigurationAccessIntegrationTest extends CPQFacadeLayerTest
{

	@Resource(name = "sapProductConfigSessionAccessService")
	protected SessionAccessService sessionAccess;
	@Resource(name = "orderFacade")
	protected OrderFacade orderfacde;
	@Resource(name = "defaultQuoteFacade")
	protected DefaultQuoteFacade defaultQuoteFacade;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	protected boolean isPersistentLifecycle;

	@Before
	public void setUp() throws Exception
	{
		prepareCPQData();
		isPersistentLifecycle = isPersistentLifecycle();
	}

	@Override
	public void importCPQTestData() throws ImpExException, Exception
	{
		super.importCPQTestData();
		importCPQUserData();
		importCsv("/sapproductconfigfacades/test/sapProductConfig_quote_testData.impex", "utf-8");

	}

	/**
	 * Configurations with product relation<br>
	 * read: allow for every user<br>
	 * update: allow only for same user<br>
	 */

	@Test
	public void testReadingNotOwnProductRelatedFailsNot() throws InvalidCredentialsException
	{
		login(USER_NAME, PASSWORD);
		final ConfigurationData configOwnedByCPQ01 = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		logout();

		// this should NOT fail!
		// thrown.expect(ProductConfigurationAccessException.class);
		cpqFacade.getConfiguration(configOwnedByCPQ01);
	}

	@Test
	public void testUpdatingNotOwnProductRelatedFails() throws InvalidCredentialsException
	{
		assumeTrue("Test makes only sense in context of persistent lifecycle", isPersistentLifecycle);
		login(USER_NAME, PASSWORD);
		final ConfigurationData configOwnedByCPQ01 = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		logout();

		// this should fail!
		thrown.expect(ProductConfigurationAccessException.class);
		thrown.expectMessage(ProductConfigurationServiceImpl.NOT_ALLOWED_TO_UPDATE_CONFIGURATION);
		cpqFacade.updateConfiguration(configOwnedByCPQ01);
	}

	@Test
	public void testReleasingNotOwnProductRelatedFails() throws InvalidCredentialsException
	{
		assumeTrue("Test makes only sense in context of persistent lifecycle", isPersistentLifecycle);
		login(USER_NAME, PASSWORD);
		final ConfigurationData configOwnedByCPQ01 = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		logout();

		// this should fail!
		thrown.expect(ProductConfigurationAccessException.class);
		thrown.expectMessage(ProductConfigurationServiceImpl.NOT_ALLOWED_TO_RELEASE_CONFIGURATION);
		cpqService.releaseSession(configOwnedByCPQ01.getConfigId());
	}



	/**
	 * Configurations with cart relation<br>
	 * read: allow only for same user<br>
	 * update: allow only for drafts and for same user<br>
	 */

	@Test
	public void testReadingNotOwnCartRelatedFails() throws InvalidCredentialsException, CommerceCartModificationException
	{
		assumeTrue("Test makes only sense in context of persistent lifecycle", isPersistentLifecycle);
		login(USER_NAME, PASSWORD);
		final ConfigurationData configOwnedByCPQ01 = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		cpqCartFacade.addConfigurationToCart(configOwnedByCPQ01);
		logout();

		// this should fail!
		thrown.expect(ProductConfigurationAccessException.class);
		cpqFacade.getConfiguration(configOwnedByCPQ01);
	}


	@Test
	public void testUpdatingNotOwnCartRelatedFails() throws InvalidCredentialsException, CommerceCartModificationException
	{
		assumeTrue("Test makes only sense in context of persistent lifecycle", isPersistentLifecycle);
		login(USER_NAME, PASSWORD);
		final ConfigurationData configOwnedByCPQ01 = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		cpqCartFacade.addConfigurationToCart(configOwnedByCPQ01);
		logout();

		// this should fail!
		thrown.expect(ProductConfigurationAccessException.class);
		thrown.expectMessage(AnyOf.anyOf(IsEqual.equalTo(ProductConfigurationServiceImpl.NOT_ALLOWED_TO_UPDATE_CONFIGURATION),
				IsEqual.equalTo(ProductConfigurationServiceImpl.NOT_ALLOWED_TO_READ_CONFIGURATION)));
		cpqFacade.updateConfiguration(configOwnedByCPQ01);
	}

	@Test
	public void testReleasingNotOwnCartRelatedFails() throws InvalidCredentialsException, CommerceCartModificationException
	{
		assumeTrue("Test makes only sense in context of persistent lifecycle", isPersistentLifecycle);
		login(USER_NAME, PASSWORD);
		final ConfigurationData configOwnedByCPQ01 = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		cpqCartFacade.addConfigurationToCart(configOwnedByCPQ01);
		logout();

		// this should fail!
		thrown.expect(ProductConfigurationAccessException.class);
		thrown.expectMessage(ProductConfigurationServiceImpl.NOT_ALLOWED_TO_RELEASE_CONFIGURATION);
		cpqService.releaseSession(configOwnedByCPQ01.getConfigId());
	}

	@Test
	public void testUpdatingOwnCartRelatedNoDraftFails() throws InvalidCredentialsException, CommerceCartModificationException
	{
		login(USER_NAME, PASSWORD);
		final ConfigurationData configOwnedByCPQ01 = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		cpqCartFacade.addConfigurationToCart(configOwnedByCPQ01);

		// this should fail!
		thrown.expect(ProductConfigurationAccessException.class);
		thrown.expectMessage(AnyOf.anyOf(IsEqual.equalTo(ProductConfigurationServiceImpl.NOT_ALLOWED_TO_UPDATE_CONFIGURATION),
				IsEqual.equalTo(ProductConfigurationServiceImpl.NOT_ALLOWED_TO_READ_CONFIGURATION)));
		cpqFacade.updateConfiguration(configOwnedByCPQ01);
	}

	@Test
	public void testReadingNotOwnDraftCartRelatedFails() throws InvalidCredentialsException, CommerceCartModificationException
	{
		assumeTrue("Test makes only sense in context of persistent lifecycle", isPersistentLifecycle);
		login(USER_NAME, PASSWORD);
		final ConfigurationData darftOwnedByCPQ01 = prepareCartDraft();
		logout();

		// this should fail!
		thrown.expect(ProductConfigurationAccessException.class);
		cpqFacade.getConfiguration(darftOwnedByCPQ01);
	}

	@Test
	public void testUpdatingNotOwnDraftCartRelatedFails() throws InvalidCredentialsException, CommerceCartModificationException
	{
		assumeTrue("Test makes only sense in context of persistent lifecycle", isPersistentLifecycle);
		login(USER_NAME, PASSWORD);
		final ConfigurationData darftOwnedByCPQ01 = prepareCartDraft();
		logout();

		// this should fail!
		thrown.expect(ProductConfigurationAccessException.class);
		thrown.expectMessage(AnyOf.anyOf(IsEqual.equalTo(ProductConfigurationServiceImpl.NOT_ALLOWED_TO_UPDATE_CONFIGURATION),
				IsEqual.equalTo(ProductConfigurationServiceImpl.NOT_ALLOWED_TO_READ_CONFIGURATION)));
		cpqFacade.updateConfiguration(darftOwnedByCPQ01);
	}

	protected ConfigurationData prepareCartDraft() throws CommerceCartModificationException
	{
		final ConfigurationData configOwnedByCPQ01 = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		final String cartItemKey = cpqCartFacade.addConfigurationToCart(configOwnedByCPQ01);
		final ConfigurationData darftOwnedByCPQ01 = cpqCartFacade.configureCartItem(cartItemKey);
		return darftOwnedByCPQ01;
	}

	@Test
	public void testReleasingNotOwnDraftCartRelatedFails() throws InvalidCredentialsException, CommerceCartModificationException
	{
		assumeTrue("Test makes only sense in context of persistent lifecycle", isPersistentLifecycle);
		login(USER_NAME, PASSWORD);
		final ConfigurationData configOwnedByCPQ01 = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		final String cartItemKey = cpqCartFacade.addConfigurationToCart(configOwnedByCPQ01);
		final ConfigurationData darftOwnedByCPQ01 = cpqCartFacade.configureCartItem(cartItemKey);
		logout();

		// this should fail!
		thrown.expect(ProductConfigurationAccessException.class);
		thrown.expectMessage(ProductConfigurationServiceImpl.NOT_ALLOWED_TO_RELEASE_CONFIGURATION);
		cpqService.releaseSession(configOwnedByCPQ01.getConfigId());
	}


	/**
	 * Configurations with order relation<br>
	 * read: allow only for same user<br>
	 * update: never allowed<br>
	 */

	@Test
	public void testReadingNotOwnOrderRelatedFails()
			throws InvalidCredentialsException, CommerceCartModificationException, InvalidCartException
	{
		assumeTrue("Test makes only sense in context of persistent lifecycle", isPersistentLifecycle);
		login(USER_NAME, PASSWORD);
		final ConfigurationData configOwnedByCPQ01 = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		cpqCartFacade.addConfigurationToCart(configOwnedByCPQ01);
		final String orderCode = validateCartAndPlaceOrder(true).getCode();
		final Integer orderEntryNum = orderfacde.getOrderDetailsForCode(orderCode).getEntries().get(0).getEntryNumber();
		final ConfigurationOverviewData configOverview = configOrderIntegrationFacade.getConfiguration(orderCode, orderEntryNum);

		final ConfigurationData orderConfigOwnedByCPQ01 = new ConfigurationData();
		orderConfigOwnedByCPQ01.setKbKey(KB_KEY_Y_SAP_SIMPLE_POC);
		orderConfigOwnedByCPQ01.setConfigId(configOverview.getId());
		logout();

		// this should fail!
		thrown.expect(ProductConfigurationAccessException.class);
		cpqFacade.getConfiguration(orderConfigOwnedByCPQ01);
	}

	@Test
	public void testUpdatingOwnOrderRelatedFails()
			throws InvalidCredentialsException, CommerceCartModificationException, InvalidCartException
	{
		assumeTrue("Test makes only sense in context of persistent lifecycle", isPersistentLifecycle);
		login(USER_NAME, PASSWORD);
		final ConfigurationData configOwnedByCPQ01 = prepareOrder();
		// this should fail!
		thrown.expect(ProductConfigurationAccessException.class);
		thrown.expectMessage(AnyOf.anyOf(IsEqual.equalTo(ProductConfigurationServiceImpl.NOT_ALLOWED_TO_UPDATE_CONFIGURATION),
				IsEqual.equalTo(ProductConfigurationServiceImpl.NOT_ALLOWED_TO_READ_CONFIGURATION)));
		cpqFacade.updateConfiguration(configOwnedByCPQ01);
	}

	@Test
	public void testReleasingNotOwnOrderRelatedFails()
			throws InvalidCredentialsException, CommerceCartModificationException, InvalidCartException
	{
		assumeTrue("Test makes only sense in context of persistent lifecycle", isPersistentLifecycle);
		login(USER_NAME, PASSWORD);
		final ConfigurationData configOwnedByCPQ01 = prepareOrder();
		logout();

		// this should fail!
		thrown.expect(ProductConfigurationAccessException.class);
		thrown.expectMessage(ProductConfigurationServiceImpl.NOT_ALLOWED_TO_RELEASE_CONFIGURATION);
		cpqService.releaseSession(configOwnedByCPQ01.getConfigId());
	}

	protected ConfigurationData prepareOrder() throws CommerceCartModificationException, InvalidCartException
	{
		final ConfigurationData configOwnedByCPQ01 = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		cpqCartFacade.addConfigurationToCart(configOwnedByCPQ01);
		final String orderCode = validateCartAndPlaceOrder(true).getCode();
		final Integer orderEntryNum = orderfacde.getOrderDetailsForCode(orderCode).getEntries().get(0).getEntryNumber();
		final ConfigurationOverviewData configOverview = configOrderIntegrationFacade.getConfiguration(orderCode, orderEntryNum);

		final ConfigurationData orderConfigOwnedByCPQ01 = new ConfigurationData();
		configOwnedByCPQ01.setConfigId(configOverview.getId());
		return configOwnedByCPQ01;
	}


	/**
	 * Configurations with quote relation<br>
	 * read: allow only for same user<br>
	 * update: never allowed<br>
	 */

	@Test
	public void testReadingNotOwnQuoteRelatedFails()
			throws InvalidCredentialsException, CommerceCartModificationException, InvalidCartException
	{
		assumeTrue("Test makes only sense in context of persistent lifecycle", isPersistentLifecycle);
		login(USER_NAME, PASSWORD);
		final ConfigurationData configOwnedByCPQ01 = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		cpqCartFacade.addConfigurationToCart(configOwnedByCPQ01);
		final String quoteCode = defaultQuoteFacade.initiateQuote().getCode();
		final Integer orderEntryNum = defaultQuoteFacade.getQuoteForCode(quoteCode).getEntries().get(0).getEntryNumber();
		final ConfigurationOverviewData configOverview = configQuoteIntegrationFacade.getConfiguration(quoteCode, orderEntryNum);

		final ConfigurationData orderConfigOwnedByCPQ01 = new ConfigurationData();
		orderConfigOwnedByCPQ01.setKbKey(KB_KEY_Y_SAP_SIMPLE_POC);
		orderConfigOwnedByCPQ01.setConfigId(configOverview.getId());
		logout();

		// this should fail!
		thrown.expect(ProductConfigurationAccessException.class);
		cpqFacade.getConfiguration(orderConfigOwnedByCPQ01);
	}

	@Test
	public void testUpdatingOwnQuoteRelatedFails()
			throws InvalidCredentialsException, CommerceCartModificationException, InvalidCartException
	{
		assumeTrue("Test makes only sense in context of persistent lifecycle", isPersistentLifecycle);
		login(USER_NAME, PASSWORD);
		final ConfigurationData configOwnedByCPQ01 = prepareQuote();

		// this should fail!
		thrown.expect(ProductConfigurationAccessException.class);
		thrown.expectMessage(AnyOf.anyOf(IsEqual.equalTo(ProductConfigurationServiceImpl.NOT_ALLOWED_TO_UPDATE_CONFIGURATION),
				IsEqual.equalTo(ProductConfigurationServiceImpl.NOT_ALLOWED_TO_READ_CONFIGURATION)));
		cpqFacade.updateConfiguration(configOwnedByCPQ01);
	}

	@Test
	public void testReleasingNotOwnQuoteRelatedFails()
			throws InvalidCredentialsException, CommerceCartModificationException, InvalidCartException
	{
		assumeTrue("Test makes only sense in context of persistent lifecycle", isPersistentLifecycle);
		login(USER_NAME, PASSWORD);
		final ConfigurationData configOwnedByCPQ01 = prepareQuote();
		logout();

		// this should fail!
		thrown.expect(ProductConfigurationAccessException.class);
		thrown.expectMessage(ProductConfigurationServiceImpl.NOT_ALLOWED_TO_RELEASE_CONFIGURATION);
		cpqService.releaseSession(configOwnedByCPQ01.getConfigId());
	}

	protected ConfigurationData prepareQuote() throws CommerceCartModificationException
	{
		final ConfigurationData configOwnedByCPQ01 = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		cpqCartFacade.addConfigurationToCart(configOwnedByCPQ01);
		final String quoteCode = defaultQuoteFacade.initiateQuote().getCode();
		final Integer orderEntryNum = defaultQuoteFacade.getQuoteForCode(quoteCode).getEntries().get(0).getEntryNumber();
		final ConfigurationOverviewData configOverview = configQuoteIntegrationFacade.getConfiguration(quoteCode, orderEntryNum);

		configOwnedByCPQ01.setConfigId(configOverview.getId());
		return configOwnedByCPQ01;
	}

	@Test
	public void testInconsistentStateIsResolved() throws CommerceCartModificationException
	{
		final ConfigurationData config = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		cpqCartFacade.addConfigurationToCart(config);
		// config now linked to product and cart item, getting config again by KBKey should now result in a new config,
		// as updating the old config, which is already linked to a cart item, would result in an access exception, as it is not a draft.
		final ConfigurationData config2 = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		assertNotEquals(config.getConfigId(), config2.getConfigId());
	}


	@Override
	public void login(final String userName, final String password) throws InvalidCredentialsException
	{
		// "rescue" configuration provider into new session, so we can test with persistent strategies
		final ConfigurationProvider configProvider = providerFactory.getConfigurationProvider();
		super.login(userName, password);
		sessionAccess.setConfigurationProvider(configProvider);
	}

	@Override
	public void logout()
	{
		// "rescue" configuration provider into new session, so we can test with persistent strategies
		final ConfigurationProvider configProvider = providerFactory.getConfigurationProvider();
		super.logout();
		sessionAccess.setConfigurationProvider(configProvider);
	}
}

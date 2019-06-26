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

import static org.junit.Assert.assertTrue;

import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.impl.DefaultCheckoutFacade;
import de.hybris.platform.commercefacades.order.impl.DefaultQuoteFacade;
import de.hybris.platform.commercefacades.order.impl.DefaultSaveCartFacade;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.impl.CommerceCartFactory;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.sap.productconfig.facades.ConfigurationCartIntegrationFacade;
import de.hybris.platform.sap.productconfig.facades.ConfigurationFacade;
import de.hybris.platform.sap.productconfig.facades.KBKeyData;
import de.hybris.platform.sap.productconfig.facades.SessionAccessFacade;
import de.hybris.platform.sap.productconfig.facades.impl.ConfigurationOrderIntegrationFacadeImpl;
import de.hybris.platform.sap.productconfig.facades.impl.ConfigurationQuoteIntegrationFacadeImpl;
import de.hybris.platform.sap.productconfig.facades.impl.ConfigurationSavedCartIntegrationFacadeImpl;
import de.hybris.platform.sap.productconfig.services.integrationtests.CPQServiceLayerTest;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPersistenceService;
import de.hybris.platform.sap.productconfig.services.strategies.impl.ProductConfigurationCartEntryValidationStrategyImpl;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.testdata.impl.ConfigurationValueHelperImpl;
import de.hybris.platform.servicelayer.security.auth.InvalidCredentialsException;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Assert;


@SuppressWarnings("javadoc")
public abstract class CPQFacadeLayerTest extends CPQServiceLayerTest
{

	protected static final String KB_VERSION = "3800";
	protected static final String LOGICAL_SYSTEM = "RR4CLNT910";
	private static Logger LOG = Logger.getLogger(CPQFacadeLayerTest.class);

	@Resource(name = "cartFacade")
	protected CartFacade cartFacade;
	@Resource(name = "saveCartFacade")
	protected DefaultSaveCartFacade saveCartFacade;
	@Resource(name = "commerceCartFactory")
	protected CommerceCartFactory commerceCartFactory;
	@Resource(name = "defaultQuoteFacade")
	protected DefaultQuoteFacade defaultQuoteFacade;
	@Resource(name = "sapProductConfigFacade")
	protected ConfigurationFacade cpqFacade;
	@Resource(name = "sapProductConfigCartIntegrationFacade")
	protected ConfigurationCartIntegrationFacade cpqCartFacade;
	@Resource(name = "sapProductConfigSavedCartIntegrationFacade")
	protected ConfigurationSavedCartIntegrationFacadeImpl cpqSavedCartFacade;
	@Resource(name = "sapProductConfigDefaultOrderIntegrationFacade")
	protected ConfigurationOrderIntegrationFacadeImpl configOrderIntegrationFacade;
	@Resource(name = "sapProductConfigQuoteIntegrationFacade")
	protected ConfigurationQuoteIntegrationFacadeImpl configQuoteIntegrationFacade;
	@Resource(name = "sapProductConfigSessionAccessFacade")
	protected SessionAccessFacade cpqSessionAccessFacade;
	@Resource(name = "sapProductConfigAbstractOrderEntryLinkStrategy")
	protected ConfigurationAbstractOrderEntryLinkStrategy cpqAbstractOrderEntryLinkStrategy;
	@Resource(name = "sapProductConfigProductConfigurationPersistenceService")
	protected ProductConfigurationPersistenceService productConfigurationPersistenceService;
	@Resource(name = "customerFacade")
	protected CustomerFacade customerFacade;
	@Resource(name = "defaultCheckoutFacade")
	protected DefaultCheckoutFacade checkoutFacade;

	protected ConfigurationValueHelperImpl facadeConfigValueHelper = new ConfigurationValueHelperImpl();

	protected static final KBKeyData KB_KEY_Y_SAP_SIMPLE_POC;
	protected static final KBKeyData KB_KEY_WEC_DRAGON_BUS;
	protected static final KBKeyData KB_KEY_CPQ_HOME_THEATER;
	protected static final KBKeyData KB_KEY_CPQ_LAPTOP;
	protected static final KBKeyData KB_KEY_CPQ_LAPTOP_MUSIC;
	protected static final KBKeyData KB_KEY_CPQ_LAPTOP_MUZAC;
	protected static final KBKeyData KB_KEY_KD990SOL;
	protected static final KBKeyData KB_KEY_NUMERIC_PRODUCT;
	protected static final KBKeyData KB_KEY_CONF_PIPE;

	public static final String USER_NAME = "cpq01";
	public static final String USER_NAME2 = "cpq02";
	public static final String SOLD_TO_NAME = "axel.krause@rustic-hw.com";
	public static final String PASSWORD = "welcome";

	static
	{
		KB_KEY_Y_SAP_SIMPLE_POC = new KBKeyData();
		KB_KEY_Y_SAP_SIMPLE_POC.setProductCode("YSAP_SIMPLE_POC");
		KB_KEY_Y_SAP_SIMPLE_POC.setKbName("YSAP_SIMPLE_POC_KB");
		KB_KEY_Y_SAP_SIMPLE_POC.setKbLogsys(LOGICAL_SYSTEM);
		KB_KEY_Y_SAP_SIMPLE_POC.setKbVersion(KB_VERSION);

		KB_KEY_WEC_DRAGON_BUS = new KBKeyData();
		KB_KEY_WEC_DRAGON_BUS.setProductCode("WEC_DRAGON_BUS");
		KB_KEY_WEC_DRAGON_BUS.setKbName("YSAP_SIMPLE_POC_KB");
		KB_KEY_WEC_DRAGON_BUS.setKbLogsys(LOGICAL_SYSTEM);
		KB_KEY_WEC_DRAGON_BUS.setKbVersion(KB_VERSION);

		KB_KEY_CPQ_HOME_THEATER = new KBKeyData();
		KB_KEY_CPQ_HOME_THEATER.setProductCode(PRODUCT_CODE_CPQ_HOME_THEATER);

		KB_KEY_CPQ_LAPTOP = new KBKeyData();
		KB_KEY_CPQ_LAPTOP.setProductCode("CPQ_LAPTOP");

		KB_KEY_CPQ_LAPTOP_MUSIC = new KBKeyData();
		KB_KEY_CPQ_LAPTOP_MUSIC.setProductCode("CPQ_LAPTOP_MUSIC");

		KB_KEY_CPQ_LAPTOP_MUZAC = new KBKeyData();
		KB_KEY_CPQ_LAPTOP_MUZAC.setProductCode("CPQ_LAPTOP_MUZAC");

		KB_KEY_NUMERIC_PRODUCT = new KBKeyData();
		KB_KEY_NUMERIC_PRODUCT.setProductCode("000000000000056227");

		KB_KEY_KD990SOL = new KBKeyData();
		KB_KEY_KD990SOL.setProductCode("KD990SOL");
		KB_KEY_KD990SOL.setKbName("KD990SOL");
		KB_KEY_KD990SOL.setKbLogsys(LOGICAL_SYSTEM);
		KB_KEY_KD990SOL.setKbVersion("2");

		KB_KEY_CONF_PIPE = new KBKeyData();
		KB_KEY_CONF_PIPE.setProductCode(PRODUCT_CODE_CONF_PIPE);
	}


	@Override
	protected void prepareCPQData() throws Exception
	{
		Assert.assertNotNull("Test setup failed, cpqFacade is null", cpqFacade);
		Assert.assertNotNull("Test setup failed, cpqCartFacade is null", cpqCartFacade);
		super.prepareCPQData();
	}

	public void login(final String userName, final String password) throws InvalidCredentialsException
	{
		authenticationService.login(userName, password);
		makeProductCatalogVersionAvailableInSession();
		customerFacade.loginSuccess();
	}

	public void logout()
	{
		//Log out and create a fresh session
		authenticationService.logout();
		establishJaloSession(JaloSession.getCurrentSession());
		baseSiteService.setCurrentBaseSite(TEST_CONFIGURE_SITE, false);
		makeProductCatalogVersionAvailableInSession();
	}

	protected OrderData validateCartAndPlaceOrder() throws CommerceCartModificationException, InvalidCartException
	{
		return validateCartAndPlaceOrder(false);
	}

	protected OrderData validateCartAndPlaceOrder(final boolean ignoreInconsistencyAndIncompletness)
			throws CommerceCartModificationException, InvalidCartException
	{
		final List<CartModificationData> validationResult = cartFacade.validateCartData();
		boolean validationPassed = true;
		for (final CartModificationData entryValidationResult : validationResult)
		{
			final String message = String.format("Validating entry %s - %s failed, because %s (%s).",
					entryValidationResult.getEntry().getEntryNumber(), entryValidationResult.getEntry().getProduct().getCode(),
					entryValidationResult.getStatusMessage(), entryValidationResult.getStatusCode());
			if (ignoreInconsistencyAndIncompletness && ProductConfigurationCartEntryValidationStrategyImpl.REVIEW_CONFIGURATION
					.equals(entryValidationResult.getStatusCode()))
			{
				LOG.info(message + " -- but test requests to ignore this");
			}
			else
			{
				validationPassed = false;
				LOG.error(message);
			}

		}
		assertTrue("cart validation failed, see log output", validationPassed);
		final OrderData order = checkoutFacade.placeOrder();
		return order;
	}
}

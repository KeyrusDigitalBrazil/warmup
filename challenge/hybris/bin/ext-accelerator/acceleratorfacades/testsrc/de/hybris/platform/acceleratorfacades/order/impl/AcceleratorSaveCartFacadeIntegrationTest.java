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
package de.hybris.platform.acceleratorfacades.order.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.acceleratorservices.constants.AcceleratorServicesConstants;
import de.hybris.platform.commercefacades.order.SaveCartFacade;
import de.hybris.platform.commercefacades.order.data.CommerceSaveCartParameterData;
import de.hybris.platform.commerceservices.constants.CommerceServicesConstants;
import de.hybris.platform.commerceservices.order.CommerceSaveCartException;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import javax.annotation.Resource;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class AcceleratorSaveCartFacadeIntegrationTest extends ServicelayerTransactionalTest
{

	private static final Logger LOG = Logger.getLogger(AcceleratorSaveCartFacadeIntegrationTest.class);
	private static final String TEST_BASESITE_UID = "testSite";
	private static final String USER = "abrode";

	private Configuration configuration;

	@Resource
	private SaveCartFacade saveCartFacade;

	@Resource
	private BaseSiteService baseSiteService;

	@Resource
	private CartService cartService;

	@Resource
	private UserService userService;

	@Resource
	private ConfigurationService configurationService;


	@Before
	public void setUp() throws Exception
	{
		LOG.info("Save Cart Facade Integration Test ..");
		final long startTime = System.currentTimeMillis();

		// importing test csv
		importCsv("/acceleratorfacades/test/testCommerceCart.csv", "utf-8");

		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID), false);
		final UserModel user = userService.getUserForUID(USER);
		userService.setCurrentUser(user);

		configuration = configurationService.getConfiguration();

		LOG.info("Finished data for commerce cart " + (System.currentTimeMillis() - startTime) + "ms");
	}

	@Test
	public void shouldReplaceSessionCartAfterSaving() throws CommerceSaveCartException
	{
		final String csPropBackup = configuration.getString(CommerceServicesConstants.SAVECARTHOOK_ENABLED);
		final String accPropBackup = configuration.getString(AcceleratorServicesConstants.SAVECART_SESSIONCARTHOOK_ENABLED);

		// enable hooks
		configuration.setProperty(CommerceServicesConstants.SAVECARTHOOK_ENABLED, "true");
		configuration.setProperty(AcceleratorServicesConstants.SAVECART_SESSIONCARTHOOK_ENABLED, "true");

		String originalSessionCartId = cartService.getSessionCart().getCode();
		saveSessionCart();
		assertFalse("Saved cart is still the session cart.", originalSessionCartId.equals(cartService.getSessionCart().getCode()));

		// disable hook
		configuration.setProperty(AcceleratorServicesConstants.SAVECART_SESSIONCARTHOOK_ENABLED, "false");
		originalSessionCartId = cartService.getSessionCart().getCode();
		saveSessionCart();
		assertTrue("Saved cart is not the session cart.", originalSessionCartId.equals(cartService.getSessionCart().getCode()));


		configuration.setProperty(CommerceServicesConstants.SAVECARTHOOK_ENABLED, csPropBackup);
		configuration.setProperty(AcceleratorServicesConstants.SAVECART_SESSIONCARTHOOK_ENABLED, accPropBackup);
	}

	@Test
	public void shouldRemoveSaveTimeAfterRestoration() throws CommerceSaveCartException
	{
		final String csPropBackup = configuration.getString(CommerceServicesConstants.SAVECARTRESTORATIONHOOK_ENABLED);
		final String accPropBackup = configuration.getString(AcceleratorServicesConstants.SAVECART_RESTORATION_SAVETIMEHOOK_ENABLED);

		// enable hooks
		configuration.setProperty(CommerceServicesConstants.SAVECARTRESTORATIONHOOK_ENABLED, "true");
		configuration.setProperty(AcceleratorServicesConstants.SAVECART_RESTORATION_SAVETIMEHOOK_ENABLED, "true");

		CommerceSaveCartParameterData parameter = saveSessionCart();
		saveCartFacade.restoreSavedCart(parameter);
		assertNull("saveTime attribute was not cleared.", cartService.getSessionCart().getSaveTime());

		// disable hook
		configuration.setProperty(AcceleratorServicesConstants.SAVECART_RESTORATION_SAVETIMEHOOK_ENABLED, "false");
		parameter = saveSessionCart();
		saveCartFacade.restoreSavedCart(parameter);
		assertNotNull("saveTime attribute is null.", cartService.getSessionCart().getSaveTime());

		configuration.setProperty(CommerceServicesConstants.SAVECARTRESTORATIONHOOK_ENABLED, csPropBackup);
		configuration.setProperty(AcceleratorServicesConstants.SAVECART_RESTORATION_SAVETIMEHOOK_ENABLED, accPropBackup);
	}

	private CommerceSaveCartParameterData saveSessionCart() throws CommerceSaveCartException
	{
		final CommerceSaveCartParameterData parameter = new CommerceSaveCartParameterData();
		parameter.setEnableHooks(true);

		final String sessionCartId = cartService.getSessionCart().getCode();
		parameter.setCartId(sessionCartId);
		saveCartFacade.saveCart(parameter);

		return parameter;
	}
}

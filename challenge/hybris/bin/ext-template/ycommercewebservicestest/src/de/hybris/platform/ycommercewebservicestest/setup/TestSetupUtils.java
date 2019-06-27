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
package de.hybris.platform.ycommercewebservicestest.setup;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.core.Initialization;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.initialization.SystemSetup.Process;
import de.hybris.platform.core.initialization.SystemSetup.Type;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.payment.commands.factory.CommandFactoryRegistry;
import de.hybris.platform.payment.commands.factory.impl.DefaultCommandFactoryImpl;
import de.hybris.platform.payment.commands.factory.impl.DefaultCommandFactoryRegistryImpl;
import de.hybris.platform.payment.methods.impl.DefaultCardPaymentServiceImpl;
import de.hybris.platform.servicelayer.datasetup.ServiceLayerDataSetup;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.Utilities;
import de.hybris.platform.webservicescommons.testsupport.server.EmbeddedServerController;
import de.hybris.platform.ycommercewebservices.constants.YcommercewebservicesConstants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SuppressWarnings("deprecation")
public class TestSetupUtils
{
	private static final String[] EXTENSIONS_TO_START = new String[]
	{ YcommercewebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME };

	private static final Logger LOG = LoggerFactory.getLogger(TestSetupUtils.class);

	private static CommandFactoryRegistry oryginalCommandFactoryRegistry;

	public static void loadDataInJunit() throws Exception
	{
		Registry.setCurrentTenantByID("junit");
		loadData();
	}

	public static void loadData() throws Exception
	{
		if (shouldLoadData())
		{
			loginAdmin();
			setupCore();
			localizeTypes();
			initPaymentCommandFactory();
			setupCommerce();
		}
		else
		{
			LOG.info("Data are already loaded");
		}
	}

	private static boolean shouldLoadData()
	{
		final BaseSiteService baseSiteService = Registry.getApplicationContext().getBean("baseSiteService", BaseSiteService.class);
		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID("wsTest");
		return baseSite == null;
	}

	private static void setupCommerce()
	{
		setupCommerce((systemsetup, context) -> systemsetup.createProjectData(context));
	}

	private static void setupCommerce(final BiConsumer<YCommerceWebServicesTestSetup, SystemSetupContext> triggerEqualsValidator)
	{
		final YCommerceWebServicesTestSetup setup = getCommerceWebServicesTestSetup();
		final SystemSetupContext context = createSystemSetupContext();
		triggerEqualsValidator.accept(setup, context);
	}

	private static void loginAdmin()
	{
		final UserService userService = Registry.getApplicationContext().getBean("userService", UserService.class);
		userService.setCurrentUser(userService.getAdminUser());
	}

	private static void setupCore() throws Exception
	{
		new CoreBasicDataCreator().createEssentialData(Collections.EMPTY_MAP, null);
		Registry.getApplicationContext().getBean("serviceLayerDataSetup", ServiceLayerDataSetup.class).setup();
		new CatalogManager().createEssentialData(Collections.singletonMap("initmethod", "init"), null);
	}

	private static void localizeTypes()
	{
		de.hybris.platform.util.localization.TypeLocalization.getInstance().localizeTypes();
	}

	private static void initPaymentCommandFactory()
	{
		final DefaultCommandFactoryRegistryImpl commandFactoryReg = Registry.getApplicationContext()
				.getBean("commandFactoryRegistry", DefaultCommandFactoryRegistryImpl.class);
		// set command factory because tests from yacceleratorfulfilmentprocess remove it
		commandFactoryReg.setCommandFactoryList(Collections.singletonList(createPaymentCommandFactory()));

		//set command factory registry because some tests change it and not restore (QuotesProcessIntegrationTest,AnonymousCheckoutIntegrationTest,DefaultExpressCheckoutIntegrationCheckoutTest)
		final DefaultCardPaymentServiceImpl cartPaymentService = Registry.getApplicationContext().getBean("cardPaymentService",
				DefaultCardPaymentServiceImpl.class);
		oryginalCommandFactoryRegistry = cartPaymentService.getCommandFactoryRegistry();
		cartPaymentService.setCommandFactoryRegistry(commandFactoryReg);
	}

	private static DefaultCommandFactoryImpl createPaymentCommandFactory()
	{
		final Map<Object, Object> commands = new HashMap<>();
		commands.put(de.hybris.platform.payment.commands.IsApplicableCommand.class,
				new de.hybris.platform.payment.commands.impl.IsApplicableMockCommand());
		commands.put(de.hybris.platform.payment.commands.AuthorizationCommand.class,
				new de.hybris.platform.payment.commands.impl.AuthorizationMockCommand());
		commands.put(de.hybris.platform.payment.commands.SubscriptionAuthorizationCommand.class,
				new de.hybris.platform.payment.commands.impl.SubscriptionAuthorizationMockCommand());
		commands.put(de.hybris.platform.payment.commands.CaptureCommand.class,
				new de.hybris.platform.payment.commands.impl.CaptureMockCommand());
		commands.put(de.hybris.platform.payment.commands.PartialCaptureCommand.class,
				new de.hybris.platform.payment.commands.impl.PartialCaptureMockCommand());
		commands.put(de.hybris.platform.payment.commands.EnrollmentCheckCommand.class,
				new de.hybris.platform.payment.commands.impl.EnrollmentCheckMockCommand());
		commands.put(de.hybris.platform.payment.commands.VoidCommand.class,
				new de.hybris.platform.payment.commands.impl.VoidMockCommand());
		commands.put(de.hybris.platform.payment.commands.FollowOnRefundCommand.class,
				new de.hybris.platform.payment.commands.impl.FollowOnRefundMockCommand());
		commands.put(de.hybris.platform.payment.commands.StandaloneRefundCommand.class,
				new de.hybris.platform.payment.commands.impl.StandaloneRefundMockCommand());
		commands.put(de.hybris.platform.payment.commands.CreateSubscriptionCommand.class,
				new de.hybris.platform.payment.commands.impl.CreateSubscriptionMockCommand());
		commands.put(de.hybris.platform.payment.commands.DeleteSubscriptionCommand.class,
				new de.hybris.platform.payment.commands.impl.DeleteSubscriptionMockCommand());
		commands.put(de.hybris.platform.payment.commands.GetSubscriptionDataCommand.class,
				new de.hybris.platform.payment.commands.impl.GetSubscriptionDataMockCommand());
		commands.put(de.hybris.platform.payment.commands.UpdateSubscriptionCommand.class,
				new de.hybris.platform.payment.commands.impl.UpdateSubscriptionMockCommand());

		final DefaultCommandFactoryImpl commandFactory = new DefaultCommandFactoryImpl();
		commandFactory.setCommands((Map) commands);
		commandFactory.setPaymentProvider("Mockup");

		return commandFactory;
	}



	private static YCommerceWebServicesTestSetup getCommerceWebServicesTestSetup()
	{
		return Registry.getApplicationContext().getBean("yCommerceWebServicesTestSetup", YCommerceWebServicesTestSetup.class);
	}

	private static SystemSetupContext createSystemSetupContext()
	{
		final Map<String, String[]> params = new HashMap<>();
		params.put("init", new String[]
		{ "Go" });
		params.put("ycommercewebservicestest_sample", new String[]
		{ "true" });
		params.put("lucenesearch_rebuild", new String[]
		{ "true" });
		params.put("lucenesearch_update.index.configuration", new String[]
		{ "true" });

		return new SystemSetupContext(params, Type.ALL, Process.ALL, "ycommercewebservicestest");
	}

	public static void cleanData() throws Exception
	{
		LOG.info("Clean data created for test");
		//restore oryginal values
		final DefaultCommandFactoryRegistryImpl commandFactoryReg = Registry.getApplicationContext()
				.getBean("commandFactoryRegistry", DefaultCommandFactoryRegistryImpl.class);
		commandFactoryReg.afterPropertiesSet();
		if (oryginalCommandFactoryRegistry != null)
		{
			final DefaultCardPaymentServiceImpl cartPaymentService = Registry.getApplicationContext().getBean("cardPaymentService",
					DefaultCardPaymentServiceImpl.class);
			cartPaymentService.setCommandFactoryRegistry(oryginalCommandFactoryRegistry);
		}

		Initialization.initializeTestSystem();
	}

	public static boolean isServerStarted()
	{
		final EmbeddedServerController controller = Registry.getApplicationContext().getBean("embeddedServerController",
				EmbeddedServerController.class);
		return controller.ensureWebAppsAreStarted(EXTENSIONS_TO_START);
	}

	public static void startServer()
	{
		startServer(EXTENSIONS_TO_START);
		if (isNewPromotionEngineTurnedOn())
		{
			final YCommerceWebServicesTestSetup setup = getCommerceWebServicesTestSetup();
			setup.initializePromotionEngineModule();
		}
	}

	public static void startServer(final String[] ext)
	{
		if (!Config.getBoolean("ycommercewebservicestest.embedded.server.enabled", true))
		{
			LOG.info("Ignoring embedded server");
			return;
		}

		LOG.info("Starting embedded server");
		final EmbeddedServerController controller = Registry.getApplicationContext().getBean("embeddedServerController",
				EmbeddedServerController.class);
		controller.start(ext);
		LOG.info("embedded server is running");

	}

	public static void stopServer()
	{
		if (!Config.getBoolean("ycommercewebservicestest.embedded.server.enabled", true))
		{
			LOG.info("Ignoring embedded server");
			return;
		}

		LOG.info("Stopping embedded server");
		final EmbeddedServerController controller = Registry.getApplicationContext().getBean("embeddedServerController",
				EmbeddedServerController.class);
		controller.stop();
		LOG.info("Stopped embedded server");
	}

	public static boolean isNewPromotionEngineTurnedOn()
	{
		return Objects.nonNull(Utilities.getExtensionInfo("promotionengineservices"));
	}
}

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
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.sap.productconfig.services.exceptions.ProductConfigurationAccessException;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.LifecycleStrategiesTestChecker;
import de.hybris.platform.servicelayer.security.auth.InvalidCredentialsException;
import de.hybris.platform.store.BaseStoreModel;

import java.util.List;
import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@SuppressWarnings("javadoc")
@IntegrationTest
public class CPQOrderFacadeIntegrationTest extends CPQFacadeLayerTest
{
	private static final String MESSAGE_ORDER_ENTRY = "After order submit, we only expect one configuration that is attached to the resulting order entry";
	private static final String MESSAGE_CART_ENTRY = "We expect exactly one configuration that is linked to the cart entry";

	private static final Logger LOG = Logger.getLogger(CPQOrderFacadeIntegrationTest.class);

	@Resource(name = "customerAccountService")
	private CustomerAccountService customerAccountService;
	protected LifecycleStrategiesTestChecker lifecycleChecker;

	@Rule
	public ExpectedException expected = ExpectedException.none();

	@Before
	public void setUp() throws Exception
	{
		prepareCPQData();
		configOrderIntegrationFacade.setBaseStoreService(baseStoreService);
		lifecycleChecker = selectStrategyTestChecker();
	}

	@Override
	public void importCPQTestData() throws ImpExException, Exception
	{
		super.importCPQTestData();
		importCPQUserData();
	}

	@Test
	public void testConfigOrderIntegrationFacade()
			throws CommerceCartModificationException, InvalidCartException, InvalidCredentialsException
	{
		/*
		 * Step 1: Create a cart containing one entry.
		 */
		final ConfigurationData configData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		cpqCartFacade.addConfigurationToCart(configData);
		lifecycleChecker.checkNumberOfConfigsPersisted(MESSAGE_CART_ENTRY, 1);

		/*
		 * Step 2: Create an order from cart
		 */
		login(USER_NAME, PASSWORD);
		final OrderData order = validateCartAndPlaceOrder(true);

		/*
		 * Step 2a: Basic checks on new order
		 */
		final BaseStoreModel store = baseStoreService.getCurrentBaseStore();
		final OrderModel orderModel = customerAccountService.getOrderForCode(order.getCode(), store);
		assertNotNull(orderModel);
		final List<AbstractOrderEntryModel> entries = orderModel.getEntries();
		assertNotNull(entries);
		assertEquals(1, entries.size());
		final AbstractOrderEntryModel abstractOrderEntryModel = entries.get(0);
		LOG.info("Order entry PK: " + abstractOrderEntryModel.getPk());
		LOG.info("Configuration attached: " + abstractOrderEntryModel.getProductConfiguration());

		/*
		 * Step 3: Get the ConfigurationOverviewData from ConfigurationOrderIntegrationFacade.
		 */
		final String orderCode = order.getCode();
		final int entryNumber = order.getEntries().get(0).getEntryNumber().intValue();
		final ConfigurationOverviewData configOverview = configOrderIntegrationFacade.getConfiguration(orderCode, entryNumber);
		assertNotNull(configOverview);
		assertEquals(KB_KEY_Y_SAP_SIMPLE_POC.getProductCode(), configOverview.getProductCode());
	}

	@Test
	public void testPlaceOrderAccessCheck()
			throws CommerceCartModificationException, InvalidCartException, InvalidCredentialsException
	{
		/*
		 * Step 1: Create a cart containing one entry.
		 */
		final ConfigurationData configData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		cpqCartFacade.addConfigurationToCart(configData);

		/*
		 * Step 2: Create an order from cart
		 */
		login(USER_NAME, PASSWORD);
		final OrderData order = validateCartAndPlaceOrder(true);

		if (isPersistentLifecycle())
		{
			expected.expect(ProductConfigurationAccessException.class);
		}
		else
		{
			expected.expect(IllegalArgumentException.class);
		}

		/*
		 * Step 3: Try to update configuration attached to order. We expect an exception
		 */
		cpqService.updateConfiguration(cpqService.retrieveConfigurationModel(configData.getConfigId()));
	}

	@Test
	public void testConfigOrderIntegrationProductBoundHandlingFacade()
			throws CommerceCartModificationException, InvalidCartException, InvalidCredentialsException
	{
		/*
		 * Step 1: Create a cart containing one entry but keep link to product
		 */
		final ConfigurationData configData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		cpqCartFacade.addConfigurationToCart(configData);
		lifecycleChecker.checkNumberOfConfigsPersisted(MESSAGE_CART_ENTRY, 1);

		/*
		 * Step 2: Create an order from cart
		 */
		login(USER_NAME, PASSWORD);
		final OrderData order = validateCartAndPlaceOrder(true);

		/*
		 * Step 3: After order submit, draft config has been deleted
		 */
		final int numExpected = isPersistentLifecycle() ? 1 : 0;
		lifecycleChecker.checkNumberOfConfigsPersisted(MESSAGE_ORDER_ENTRY, numExpected);
	}

	@Test
	public void testConfigOrderIntegrationDraftHandlingFacade()
			throws CommerceCartModificationException, InvalidCartException, InvalidCredentialsException
	{
		login(USER_NAME, PASSWORD);

		/*
		 * Step 1: Create a cart containing one entry
		 */
		final ConfigurationData configData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		cpqCartFacade.addConfigurationToCart(configData);
		lifecycleChecker.checkNumberOfConfigsPersisted(MESSAGE_CART_ENTRY, 1);

		/*
		 * Step 2: Configure from cart, create draft
		 */
		final String cartItemKey = cartFacade.getSessionCart().getEntries().get(0).getItemPK().toString();
		cpqCartFacade.configureCartItem(cartItemKey);
		lifecycleChecker.checkNumberOfConfigsPersisted("We expect 2 configurations at this point as we created a draft", 2);


		/*
		 * Step 3: Create an order from cart
		 */
		login(USER_NAME, PASSWORD);
		final OrderData order = validateCartAndPlaceOrder(true);

		/*
		 * Step 4: After order submit, draft config has been deleted
		 */
		final int numExpected = isPersistentLifecycle() ? 1 : 0;
		lifecycleChecker.checkNumberOfConfigsPersisted(MESSAGE_ORDER_ENTRY, numExpected);
	}


}

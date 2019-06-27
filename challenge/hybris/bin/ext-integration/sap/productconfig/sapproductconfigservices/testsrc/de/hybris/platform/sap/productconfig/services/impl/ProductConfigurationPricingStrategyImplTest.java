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
package de.hybris.platform.sap.productconfig.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.PriceModelImpl;
import de.hybris.platform.sap.productconfig.services.intf.PricingService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.servicelayer.model.ModelService;

import java.math.BigDecimal;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class ProductConfigurationPricingStrategyImplTest
{
	private ProductConfigurationPricingStrategyImpl cut;

	@Mock
	private PricingService pricingService;

	private static final String CONFIG_ID_2 = "asdasdwer4543556zgfhvchtr";
	private static final String CONFIG_ID_1 = "asdsafsdgftert6er6erzz";

	@Mock
	private ProductConfigurationService configurationService;
	@Mock
	private ModelService modelService;

	@Mock
	private ConfigModel modelMock;

	private static final String CONFIG_ID = "abc123";

	@Mock
	private CartEntryModel cartEntry;

	@Mock
	private ProductModel productModel;

	@Mock
	private ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy;

	@Mock
	private CommerceCartService commerceCartService;

	private static final long keyAsLong = 12;

	private final PK primaryKey = PK.fromLong(keyAsLong);

	private CommerceCartParameter parameters;

	private static final String configId = "1";

	private final ConfigModel configModel = new ConfigModelImpl();

	private final InstanceModel instanceModel = new InstanceModelImpl();

	@Mock
	private CartModel cart;

	@Before
	public void setup()
	{
		cut = new ProductConfigurationPricingStrategyImpl();
		MockitoAnnotations.initMocks(this);
		cut.setPricingService(pricingService);
		cut.setConfigurationService(configurationService);
		Mockito.when(configurationService.retrieveConfigurationModel(configId)).thenReturn(configModel);
		Mockito.when(Boolean.valueOf(pricingService.isActive())).thenReturn(Boolean.FALSE);
		cut.setAbstractOrderEntryLinkStrategy(configurationAbstractOrderEntryLinkStrategy);
		cut.setCommerceCartService(commerceCartService);
		cut.setModelService(modelService);

		Mockito.when(modelMock.getId()).thenReturn(CONFIG_ID);
		Mockito.when(cartEntry.getPk()).thenReturn(primaryKey);
		Mockito.when(cartEntry.getProduct()).thenReturn(productModel);
		Mockito.when(cartEntry.getOrder()).thenReturn(cart);
		Mockito.when(configurationAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry(primaryKey.toString())).thenReturn(configId);

		configModel.setRootInstance(instanceModel);
		configModel.setId(configId);
		instanceModel.setSubInstances(Collections.EMPTY_LIST);

		parameters = new CommerceCartParameter();
		parameters.setConfigId(CONFIG_ID);
	}

	@Test
	public void testUpdateCartEntryBasePrice_NoPrice() throws Exception
	{
		Mockito.when(configurationService.retrieveConfigurationModel(CONFIG_ID)).thenReturn(modelMock);
		Mockito.when(modelMock.getCurrentTotalPrice()).thenReturn(PriceModel.NO_PRICE);

		final boolean entryUpdated = cut.updateCartEntryBasePrice(cartEntry);

		assertFalse("Entry should not be updated", entryUpdated);

	}

	@Test
	public void testUpdateCartEntryBasePrice() throws Exception
	{
		final ConfigModel cfgModel = createConfigModel();
		Mockito.when(configurationService.retrieveConfigurationModel(CONFIG_ID)).thenReturn(cfgModel);
		Mockito.when(configurationAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry(Mockito.any())).thenReturn(CONFIG_ID);

		final boolean entryUpdated = cut.updateCartEntryBasePrice(cartEntry);

		Mockito.verify(cartEntry, Mockito.times(1))
				.setBasePrice(Mockito.eq(Double.valueOf(cfgModel.getCurrentTotalPrice().getPriceValue().doubleValue())));

		assertTrue("Entry should be updated", entryUpdated);

	}

	private ConfigModel createConfigModel()
	{
		final PriceModel currentTotalPrice = new PriceModelImpl();
		final ConfigModel configModel = new ConfigModelImpl();
		configModel.setId(CONFIG_ID);
		currentTotalPrice.setCurrency("EUR");
		currentTotalPrice.setPriceValue(BigDecimal.valueOf(132.85));
		configModel.setCurrentTotalPrice(currentTotalPrice);
		return configModel;
	}


	@Test
	public void testGetLockDifferrentForDifferntConfigIds()
	{
		final Object lock1 = ProductConfigurationServiceImpl.getLock(CONFIG_ID_1);
		final Object lock2 = ProductConfigurationServiceImpl.getLock(CONFIG_ID_2);
		Assert.assertNotSame("Lock objects should not be same!", lock1, lock2);
	}

	@Test
	public void testGetLockSameforSameConfigIds()
	{
		final Object lock1 = ProductConfigurationServiceImpl.getLock(CONFIG_ID_1);
		final Object lock2 = ProductConfigurationServiceImpl.getLock(CONFIG_ID_1);
		Assert.assertSame("Lock objects should be same!", lock1, lock2);
	}

	@Test
	public void testGetLockMapShouldNotGrowEndless()
	{

		final Object lock1 = ProductConfigurationServiceImpl.getLock(CONFIG_ID_1);
		final int maxLocks = ProductConfigurationServiceImpl.getMaxLocksPerMap() * 2;
		for (int ii = 0; ii <= maxLocks; ii++)
		{
			ProductConfigurationServiceImpl.getLock(String.valueOf(ii));
		}
		final Object lock2 = ProductConfigurationServiceImpl.getLock(CONFIG_ID_1);
		Assert.assertNotSame("Lock objects should not be same!", lock1, lock2);
	}

	@Test
	public void testRetrieveCurrentTotalPriceSSC() throws ConfigurationEngineException
	{
		final ConfigModel cfgModel = createConfigModel();
		Mockito.when(configurationService.retrieveConfigurationModel(CONFIG_ID)).thenReturn(cfgModel);
		Mockito.when(Boolean.valueOf(pricingService.isActive())).thenReturn(Boolean.FALSE);
		final PriceModel result = cut.retrieveCurrentTotalPrice(CONFIG_ID);
		assertNotNull(result);
		Mockito.verify(pricingService, Mockito.times(0)).getPriceSummary(CONFIG_ID);
		Mockito.verify(configurationService).retrieveConfigurationModel(CONFIG_ID);
	}

	@Test
	public void testRetrieveCurrentTotalPriceCPS()
	{
		final PriceSummaryModel priceSummary = new PriceSummaryModel();
		priceSummary.setCurrentTotalPrice(new PriceModelImpl());
		Mockito.when(pricingService.getPriceSummary(CONFIG_ID)).thenReturn(priceSummary);
		Mockito.when(Boolean.valueOf(pricingService.isActive())).thenReturn(Boolean.TRUE);
		final PriceModel result = cut.retrieveCurrentTotalPrice(CONFIG_ID);
		assertNotNull(result);
		Mockito.verify(pricingService).getPriceSummary(CONFIG_ID);
		Mockito.verify(configurationService, Mockito.times(0)).retrieveConfigurationModel(CONFIG_ID);
	}

	@Test
	public void testRetrieveCurrentTotalPriceCPSNull()
	{
		Mockito.when(pricingService.getPriceSummary(CONFIG_ID)).thenReturn(null);
		Mockito.when(Boolean.valueOf(pricingService.isActive())).thenReturn(Boolean.TRUE);
		final PriceModel result = cut.retrieveCurrentTotalPrice(CONFIG_ID);
		assertNull(result);
		Mockito.verify(pricingService).getPriceSummary(CONFIG_ID);
	}

	@Test
	public void testGetParametersForCartUpdate()
	{
		final CommerceCartParameter result = cut.getParametersForCartUpdate(cartEntry);
		assertEquals(cartEntry.getOrder(), result.getCart());
		assertEquals(configId, result.getConfigId());
		assertTrue(result.isEnableHooks());
	}

	@Test
	public void testhasBasePriceChanged()
	{
		assertTrue(cut.hasBasePriceChanged(cartEntry, Double.valueOf(4)));
	}

	@Test
	public void testhasBasePriceChanged_Not()
	{
		assertFalse(cut.hasBasePriceChanged(cartEntry, cartEntry.getBasePrice()));
	}

	@Test
	public void testUpdateCartEntryPricesEntrySaved()
	{
		prepareModelsForUpdateCartEntryPrices();
		assertTrue(cut.updateCartEntryPrices(cartEntry, true, null));
		Mockito.verify(commerceCartService).calculateCart(Mockito.any(CommerceCartParameter.class));
		Mockito.verify(modelService).save(cartEntry);
	}

	@Test
	public void testUpdateCartEntryPricesNoCalculate()
	{
		prepareModelsForUpdateCartEntryPrices();
		assertTrue(cut.updateCartEntryPrices(cartEntry, false, null));
		Mockito.verify(commerceCartService, Mockito.times(0)).calculateCart(Mockito.any(CommerceCartParameter.class));
		Mockito.verify(modelService).save(cartEntry);
		Mockito.verify(modelService).save(cartEntry.getOrder());
	}

	@Test
	public void testUpdateCartEntryPricesCartSaved()
	{
		prepareModelsForUpdateCartEntryPrices();
		assertTrue(cut.updateCartEntryPrices(cartEntry, false, null));
		Mockito.verify(modelService).save(cartEntry.getOrder());
	}


	@Test
	public void testUpdateCartEntryPrices_passedParameter()
	{
		prepareModelsForUpdateCartEntryPrices();
		final CommerceCartParameter passedParameter = new CommerceCartParameter();
		assertTrue(cut.updateCartEntryPrices(cartEntry, true, passedParameter));
		Mockito.verify(modelService).save(cartEntry);
		Mockito.verify(commerceCartService).calculateCart(passedParameter);
	}

	protected void prepareModelsForUpdateCartEntryPrices()
	{
		final PriceModel price = new PriceModelImpl();
		price.setPriceValue(new BigDecimal(2));
		price.setCurrency("EUR");
		configModel.setCurrentTotalPrice(price);
		configModel.setId("123");
	}

	@Test
	public void testUpdateCartEntryPricesNoUpdate()
	{
		configModel.setCurrentTotalPrice(null);
		assertFalse(cut.updateCartEntryPrices(cartEntry, true, null));
		Mockito.verify(modelService, Mockito.times(0)).save(cartEntry);
		Mockito.verify(commerceCartService, Mockito.times(0)).calculateCart(Mockito.any(CommerceCartParameter.class));
	}

	@Test
	public void testIsPricingErrorPresent()
	{
		configModel.setPricingError(true);
		assertTrue(cut.isCartPricingErrorPresent(configModel));

		configModel.setPricingError(false);
		assertFalse(cut.isCartPricingErrorPresent(configModel));
	}

}

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
package de.hybris.platform.sap.productconfig.facades.populator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.sap.productconfig.facades.ConfigPricing;
import de.hybris.platform.sap.productconfig.facades.PricingData;
import de.hybris.platform.sap.productconfig.facades.impl.ConfigPricingImpl;
import de.hybris.platform.sap.productconfig.facades.impl.ConfigPricingImplTest.DummyPriceDataFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.PriceModelImpl;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class PriceSummaryPopulatorTest
{
	PriceSummaryPopulator summaryPopulator;
	PriceSummaryModel source;
	PriceDataFactory priceDataFactory = new DummyPriceDataFactory();

	private final ConfigPricing configPricing = new ConfigPricingImpl();

	@Before
	public void setup()
	{
		configPricing.setPriceDataFactory(priceDataFactory);
		summaryPopulator = new PriceSummaryPopulator();
		summaryPopulator.setConfigPricing(configPricing);
	}


	@Test
	public void testPopulatePriceSummary_null()
	{
		source = new PriceSummaryModel();
		source.setBasePrice(null);
		source.setCurrentTotalPrice(null);
		source.setCurrentTotalSavings(null);
		source.setSelectedOptionsPrice(null);

		final PricingData target = new PricingData();

		summaryPopulator.populate(source, target);
		assertNotNull("Target should not be null", target);
		assertSame(ConfigPricing.NO_PRICE, target.getBasePrice());
		assertSame(ConfigPricing.NO_PRICE, target.getCurrentTotal());
		assertSame(ConfigPricing.NO_PRICE, target.getCurrentTotalSavings());
		assertSame(ConfigPricing.NO_PRICE, target.getSelectedOptions());

	}

	@Test
	public void testPopulatePriceSummary_empty()
	{
		source = new PriceSummaryModel();
		source.setBasePrice(new PriceModelImpl());
		source.setCurrentTotalPrice(new PriceModelImpl());
		source.setCurrentTotalSavings(new PriceModelImpl());
		source.setSelectedOptionsPrice(new PriceModelImpl());

		final PricingData target = new PricingData();

		summaryPopulator.populate(source, target);
		assertNotNull("Target should not be null", target);
		assertSame(ConfigPricing.NO_PRICE, target.getBasePrice());
		assertSame(ConfigPricing.NO_PRICE, target.getCurrentTotal());
		assertSame(ConfigPricing.NO_PRICE, target.getCurrentTotalSavings());
		assertSame(ConfigPricing.NO_PRICE, target.getSelectedOptions());

	}

	@Test
	public void testPopulatePriceSummary_priceDefined()
	{
		source = new PriceSummaryModel();
		final PricingData target = new PricingData();

		final PriceModel basePrice = new PriceModelImpl();
		basePrice.setPriceValue(new BigDecimal(1000));
		basePrice.setCurrency("EUR");
		source.setBasePrice(basePrice);

		final PriceModel selectedOptionsPrice = new PriceModelImpl();
		selectedOptionsPrice.setPriceValue(new BigDecimal(100));
		selectedOptionsPrice.setCurrency("EUR");
		source.setSelectedOptionsPrice(selectedOptionsPrice);

		final PriceModel currentTotalPrice = new PriceModelImpl();
		currentTotalPrice.setPriceValue(new BigDecimal(1100));
		currentTotalPrice.setCurrency("EUR");
		source.setCurrentTotalPrice(currentTotalPrice);

		final PriceModel currentTotalSavings = new PriceModelImpl();
		currentTotalSavings.setPriceValue(new BigDecimal(200));
		currentTotalSavings.setCurrency("EUR");
		source.setCurrentTotalSavings(currentTotalSavings);

		summaryPopulator.populate(source, target);

		assertNotNull("Base price should not be null", target.getBasePrice());
		assertNotNull("CurrentTotal price should not be null", target.getCurrentTotal());
		assertNotNull("CurrentTotal savings should not be null", target.getCurrentTotalSavings());
		assertNotNull("Selected Options price should not be null", target.getSelectedOptions());

		assertEquals(source.getBasePrice().getPriceValue(), target.getBasePrice().getValue());
		assertEquals(source.getSelectedOptionsPrice().getPriceValue(), target.getSelectedOptions().getValue());
		assertEquals(source.getCurrentTotalPrice().getPriceValue(), target.getCurrentTotal().getValue());
		assertEquals(source.getCurrentTotalSavings().getPriceValue(), target.getCurrentTotalSavings().getValue());


	}

	@Test
	public void testPopulatePriceSummarPriceDefinedWithoutSavings()
	{
		source = new PriceSummaryModel();
		final PricingData target = new PricingData();

		final PriceModel basePrice = new PriceModelImpl();
		basePrice.setPriceValue(new BigDecimal(1000));
		basePrice.setCurrency("EUR");
		source.setBasePrice(basePrice);

		final PriceModel selectedOptionsPrice = new PriceModelImpl();
		selectedOptionsPrice.setPriceValue(new BigDecimal(100));
		selectedOptionsPrice.setCurrency("EUR");
		source.setSelectedOptionsPrice(selectedOptionsPrice);

		final PriceModel currentTotalPrice = new PriceModelImpl();
		currentTotalPrice.setPriceValue(new BigDecimal(1100));
		currentTotalPrice.setCurrency("EUR");
		source.setCurrentTotalPrice(currentTotalPrice);

		source.setCurrentTotalSavings(null);

		summaryPopulator.populate(source, target);

		assertNotNull("Base price should not be null", target.getBasePrice());
		assertNotNull("CurrentTotal price should not be null", target.getCurrentTotal());
		assertNotNull("CurrentTotal savings should not be null", target.getCurrentTotalSavings());
		assertNotNull("Selected Options price should not be null", target.getSelectedOptions());

		assertEquals(source.getBasePrice().getPriceValue(), target.getBasePrice().getValue());
		assertEquals(source.getSelectedOptionsPrice().getPriceValue(), target.getSelectedOptions().getValue());
		assertEquals(ConfigPricing.NO_PRICE, target.getCurrentTotalSavings());
		assertEquals(source.getCurrentTotalPrice().getPriceValue(), target.getCurrentTotal().getValue());
	}

	@Test
	public void testPopulateSourceNull()
	{
		final PricingData target = new PricingData();
		summaryPopulator.populate(null, target);
		assertNotNull("Target should not be null", target);
		assertEquals(null, target.getBasePrice());
		assertEquals(null, target.getCurrentTotal());
		assertEquals(null, target.getCurrentTotalSavings());
		assertEquals(null, target.getSelectedOptions());
	}


}

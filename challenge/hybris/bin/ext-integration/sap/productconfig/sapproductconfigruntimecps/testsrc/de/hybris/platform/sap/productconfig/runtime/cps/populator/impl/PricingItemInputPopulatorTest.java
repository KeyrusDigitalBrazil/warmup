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
package de.hybris.platform.sap.productconfig.runtime.cps.populator.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.impl.ConfigurationModificationHandlerImpl;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingItemInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSPricingQuantity;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSQuantity;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSVariantCondition;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigurationRetrievalOptions;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ProductConfigurationDiscount;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class PricingItemInputPopulatorTest
{
	private static final String ISO_UNIT = "ISO unit";
	private static final String INSTANCE_PRODUCT = "INSTANCE_PRODUCT";
	private static final String CONTEXT_PRICING_PRODUCT = "CONTEXT_PRICING_PRODUCT";

	private PricingItemInputPopulator classUnderTest;
	private PricingItemInput target;
	private CPSItem item;
	private CPSVariantCondition vc1;

	private ConfigurationModificationHandlerImpl configurationModificationHandler;

	@Before
	public void setup()
	{
		classUnderTest = new PricingItemInputPopulator();
		configurationModificationHandler = Mockito.spy(new ConfigurationModificationHandlerImpl());

		MockitoAnnotations.initMocks(this);

		classUnderTest.setConfigurationModificationHandler(configurationModificationHandler);
		target = new PricingItemInput();
		item = new CPSItem();
		item.setVariantConditions(new ArrayList<CPSVariantCondition>());
		vc1 = new CPSVariantCondition();
		vc1.setKey("vc1");
		vc1.setFactor("1.0");
		item.getVariantConditions().add(vc1);
		item.setQuantity(new CPSQuantity());
		item.getQuantity().setUnit(ISO_UNIT);
	}


	@Test
	public void testFillVariantConditions()
	{
		final CPSVariantCondition vc2 = new CPSVariantCondition();
		item.getVariantConditions().add(vc2);
		vc2.setKey("vc2");
		vc2.setFactor("2.0");
		classUnderTest.fillVariantConditions(item, target, null);
		assertEquals(2, target.getVariantConditions().size());
		assertTrue(isVariantConditionPresent(vc1, target.getVariantConditions()));
		assertTrue(isVariantConditionPresent(vc2, target.getVariantConditions()));

		item.getVariantConditions().get(1).setFactor("3.0");
		assertEquals("2.0", target.getVariantConditions().get(1).getFactor());
	}

	@Test
	public void testFillVariantConditionsZeroFactorGetFiltered()
	{
		final CPSVariantCondition vc2 = new CPSVariantCondition();
		item.getVariantConditions().add(vc2);
		vc2.setKey("vc2");
		vc2.setFactor("0.0");
		classUnderTest.fillVariantConditions(item, target, null);
		assertEquals(1, target.getVariantConditions().size());
		assertTrue(isVariantConditionPresent(vc1, target.getVariantConditions()));
	}

	@Test(expected = IllegalStateException.class)
	public void testFillVariantConditionsEmptyKey()
	{
		vc1.setKey(" ");
		classUnderTest.fillVariantConditions(item, target, null);
	}

	@Test
	public void testFillVariantConditionsWithDiscounts()
	{
		final Map<String, BigDecimal> variantConditionDiscounts = new HashMap<>();
		variantConditionDiscounts.put("vc2", BigDecimal.TEN);

		Mockito.doReturn(variantConditionDiscounts).when(configurationModificationHandler).retrieveVarCondDiscounts(Mockito.any(),
				Mockito.any(), Mockito.any());

		item.setParentConfiguration(new CPSConfiguration());

		final ConfigurationRetrievalOptions context = new ConfigurationRetrievalOptions();
		final List<ProductConfigurationDiscount> discountList = new ArrayList<>();
		context.setDiscountList(discountList);

		final CPSVariantCondition vc2 = new CPSVariantCondition();
		item.getVariantConditions().add(vc2);
		vc2.setKey("vc2");
		vc2.setFactor("2.0");
		classUnderTest.fillVariantConditions(item, target, context);
		assertEquals(2, target.getVariantConditions().size());
		assertTrue(isVariantConditionPresent(vc1, target.getVariantConditions()));

		assertEquals("vc2", target.getVariantConditions().get(1).getKey());

		final BigDecimal resultFactor = new BigDecimal(target.getVariantConditions().get(1).getFactor());
		final BigDecimal expectedFactor = new BigDecimal("1.8");
		assertTrue(expectedFactor.compareTo(resultFactor) == 0);
	}

	protected boolean isVariantConditionPresent(final CPSVariantCondition present, final List<CPSVariantCondition> conditions)
	{
		for (final CPSVariantCondition condition : conditions)
		{
			if (condition.getKey().equals(present.getKey()) && condition.getFactor().equals(present.getFactor()))
			{
				return true;
			}
		}
		return false;
	}

	@Test
	public void testCalculateQuantityNoFixedQuantity()
	{
		final CPSItem cpsItem1 = new CPSItem();
		final CPSItem cpsItem2 = new CPSItem();
		final CPSItem cpsItem3 = new CPSItem();
		prepareItems(cpsItem1, cpsItem2, cpsItem3);

		final CPSPricingQuantity calculatedQuantity = classUnderTest.calculateQuantity(cpsItem3);
		final BigDecimal expected = BigDecimal.valueOf(1.331).setScale(3, RoundingMode.HALF_UP);
		assertEquals(0, calculatedQuantity.getValue().compareTo(expected));
		assertEquals("ST", calculatedQuantity.getUnit());
	}

	@Test
	public void testCalculateQuantityFixedQuantity()
	{
		final CPSItem cpsItem1 = new CPSItem();
		final CPSItem cpsItem2 = new CPSItem();
		final CPSItem cpsItem3 = new CPSItem();
		prepareItems(cpsItem1, cpsItem2, cpsItem3);

		cpsItem2.setFixedQuantity(true);

		final CPSPricingQuantity calculatedQuantity = classUnderTest.calculateQuantity(cpsItem3);
		final BigDecimal expected = BigDecimal.valueOf(1.21).setScale(3, RoundingMode.HALF_UP);
		assertEquals(0, calculatedQuantity.getValue().compareTo(expected));
		assertEquals("ST", calculatedQuantity.getUnit());
	}

	@Test
	public void testIsNotZero()
	{
		final List<Object[]> parameters = Arrays.asList(new Object[][]
		{ //
				{ "Expected TRUE for value 0.1", "0.1", Boolean.TRUE }, //
				{ "Expected TRUE for value -0.1", "-0.1", Boolean.TRUE }, //
				{ "Expected FALSE for value 0.0", "0.0", Boolean.FALSE }, //
				{ "Expected FALSE for value null", null, Boolean.FALSE }, //
				{ "Expected FALSE for value 0E-9", "0E-9", Boolean.FALSE }, //
				{ "Expected FALSE for value 'value'", "value", Boolean.FALSE } //
		});

		for (final Object[] values : parameters)
		{
			assertEquals((String) values[0], (Boolean) values[2], classUnderTest.isNotZero((String) values[1]));
		}
	}

	@Test
	public void testRetrievePricingProductRootInstanceNoContextProduct()
	{
		final CPSItem source = new CPSItem();
		source.setKey(INSTANCE_PRODUCT);
		source.setParentItem(null);
		final ConfigurationRetrievalOptions context = new ConfigurationRetrievalOptions();
		assertEquals(INSTANCE_PRODUCT, classUnderTest.retrievePricingProduct(source, context));
	}

	@Test
	public void testRetrievePricingProductRootInstanceContextPricinqProduct()
	{
		final CPSItem source = new CPSItem();
		source.setKey(INSTANCE_PRODUCT);
		source.setParentItem(null);
		final ConfigurationRetrievalOptions context = new ConfigurationRetrievalOptions();
		context.setPricingProduct(CONTEXT_PRICING_PRODUCT);
		assertEquals(CONTEXT_PRICING_PRODUCT, classUnderTest.retrievePricingProduct(source, context));
	}

	@Test
	public void testRetrievePricingProductSubInstanceContextPricinqProduct()
	{
		final CPSItem source = new CPSItem();
		source.setKey(INSTANCE_PRODUCT);
		source.setParentItem(new CPSItem());
		final ConfigurationRetrievalOptions context = new ConfigurationRetrievalOptions();
		context.setPricingProduct(CONTEXT_PRICING_PRODUCT);
		assertEquals(INSTANCE_PRODUCT, classUnderTest.retrievePricingProduct(source, context));
	}

	private void prepareItems(final CPSItem cpsItem1, final CPSItem cpsItem2, final CPSItem cpsItem3)
	{
		final CPSQuantity quantity = new CPSQuantity();
		quantity.setUnit("ST");
		quantity.setValue(1.1);

		cpsItem1.setQuantity(quantity);
		cpsItem2.setQuantity(quantity);
		cpsItem3.setQuantity(quantity);

		cpsItem3.setParentItem(cpsItem2);
		cpsItem2.setParentItem(cpsItem1);
	}
}

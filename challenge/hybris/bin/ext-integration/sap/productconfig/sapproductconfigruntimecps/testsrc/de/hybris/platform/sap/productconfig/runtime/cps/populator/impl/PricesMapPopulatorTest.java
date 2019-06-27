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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.pricing.CPSMasterDataVariantPriceKey;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.ConditionResult;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentResult;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingItemResult;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.pricing.CPSValuePrice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("javadoc")
@UnitTest
public class PricesMapPopulatorTest
{

	/**
	 *
	 */
	private static final String CURRENCY = "currency";
	private static final String ITEM1 = "item1";
	private static final String ITEM2 = "item2";
	private PricesMapPopulator classUnderTest;
	private PricingDocumentResult pricingResult;
	private Map<CPSMasterDataVariantPriceKey, CPSValuePrice> target;


	@Before
	public void setup()
	{
		classUnderTest = new PricesMapPopulator();
		pricingResult = new PricingDocumentResult();
		pricingResult.setDocumentCurrencyUnit(CURRENCY);
		final List<PricingItemResult> items = new ArrayList<>();
		pricingResult.setItems(items);
		target = new HashMap<>();
	}

	@Test
	public void testPopulate_1Item()
	{
		final PricingItemResult item1 = new PricingItemResult();
		pricingResult.getItems().add(item1);
		item1.setItemId(ITEM1);
		item1.setConditions(createConditionsFull());
		classUnderTest.populate(pricingResult, target);
		assertEquals(3, target.size());
		final CPSMasterDataVariantPriceKey key = new CPSMasterDataVariantPriceKey();
		key.setProductId(ITEM1);
		key.setVariantConditionKey("CAM700");
		final double priceValue = 350;
		assertEquals(BigDecimal.valueOf(priceValue), target.get(key).getValuePrice());
		assertEquals(CURRENCY, target.get(key).getCurrency());
	}


	@Test
	public void testPopulate_2Items()
	{
		final PricingItemResult item1 = new PricingItemResult();
		pricingResult.getItems().add(item1);
		item1.setItemId(ITEM1);
		item1.setConditions(createConditionsFull());
		final PricingItemResult item2 = new PricingItemResult();
		pricingResult.getItems().add(item2);
		item2.setItemId(ITEM2);
		item2.setConditions(new ArrayList<ConditionResult>());

		classUnderTest.populate(pricingResult, target);
		assertEquals(3, target.size());
		final CPSMasterDataVariantPriceKey key = new CPSMasterDataVariantPriceKey();
		key.setProductId(ITEM1);
		key.setVariantConditionKey("CAM700");
		final double expectedValue = 350;
		assertEquals(BigDecimal.valueOf(expectedValue), target.get(key).getValuePrice());
		assertEquals(CURRENCY, target.get(key).getCurrency());
	}

	@Test
	public void testPopulate_NoItems()
	{
		classUnderTest.populate(pricingResult, target);
		assertEquals(0, target.size());
	}

	@Test
	public void testPopulate_ItemsNull()
	{
		pricingResult.setItems(null);
		classUnderTest.populate(pricingResult, target);
		assertEquals(0, target.size());
	}


	protected List<ConditionResult> createConditionsFull()
	{
		final List<ConditionResult> conditions = new ArrayList<>();
		conditions.add(createConditionResult("PR00", null, Double.valueOf(500)));
		conditions.add(createConditionResult("K005", null, Double.valueOf(0)));
		conditions.add(createConditionResult("K007", null, Double.valueOf(-50)));
		conditions.add(createConditionResult(null, null, Double.valueOf(450)));
		conditions.add(createConditionResult("VA00", "CAM600", Double.valueOf(200)));
		conditions.add(createConditionResult("VA00", "CAM700", Double.valueOf(350)));
		conditions.add(createConditionResult("VA00", "CAM300", Double.valueOf(200)));
		conditions.add(createConditionResult(null, null, Double.valueOf(889.72)));
		return conditions;
	}

	protected ConditionResult createConditionResult(final String conditionType, final String varcondKey,
			final Double conditionValue)
	{
		final ConditionResult condition = new ConditionResult();
		condition.setConditionType(conditionType);
		condition.setVarcondKey(varcondKey);
		condition.setConditionValue(conditionValue);
		return condition;
	}
}

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
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataPossibleValueSpecific;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataCharacteristicSpecificContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataProductContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.Attribute;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingItemInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSVariantCondition;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingConfigurationParameter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


@SuppressWarnings("javadoc")
@UnitTest
public class PricingItemInputKBProductPopulatorTest
{
	private static final String PRICING_PRODUCT = "PRICING_PRODUCT_ID";
	private static final String PRODUCT_ID = "THE_PRODUCT_ID";
	private static final String UOM_ST = "PCE";
	private PricingItemInputKBProductPopulator classUnderTest;
	private CPSMasterDataProductContainer productContainer;
	private PricingItemInput target;
	private MasterDataContext context;

	@Mock
	private ProductService productService;
	@Mock
	private PricingConfigurationParameter pricingConfigurationParameter;
	@Mock
	private CommonI18NService i18NService;

	Answer<String> isoCodeAnswer = new Answer<String>()
	{
		public String answer(final InvocationOnMock invocation) throws Throwable
		{
			final UnitModel unitModel = (UnitModel) invocation.getArguments()[0];
			return unitModel.getCode();
		}
	};


	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new PricingItemInputKBProductPopulator();
		target = new PricingItemInput();
		classUnderTest.setProductService(productService);
		classUnderTest.setI18NService(i18NService);
		final UnitModel unitModel = new UnitModel();
		unitModel.setCode(UOM_ST);
		final ProductModel product = new ProductModel();
		product.setUnit(unitModel);
		Mockito.when(productService.getProductForCode(Mockito.anyString())).thenReturn(product);
		productContainer = new CPSMasterDataProductContainer();
		productContainer.setId(PRODUCT_ID);
		productContainer.setCstics(Collections.emptyMap());
		Mockito.when(pricingConfigurationParameter.retrieveUnitIsoCode(Mockito.any())).thenAnswer(isoCodeAnswer);
		classUnderTest.setPricingConfigurationParameter(pricingConfigurationParameter);

		context = new MasterDataContext();
		context.setPricingProduct(PRICING_PRODUCT);

	}

	@Test
	public void testFillVariantConditions()
	{
		final Map<String, CPSMasterDataCharacteristicSpecificContainer> cstics = new HashMap<>();

		final String id11 = "val1.1";
		final CPSMasterDataPossibleValueSpecific val11 = createPossibleValueSpecific(id11, "vc1.1");
		final String id12 = "val1.2";
		final CPSMasterDataPossibleValueSpecific val12 = createPossibleValueSpecific(id12, "vc1.2");
		final String id13 = "val1.3";
		final CPSMasterDataPossibleValueSpecific val13 = createPossibleValueSpecific(id13, "vc1.3");
		final Map<String, CPSMasterDataPossibleValueSpecific> possibleValueSpecifics = new HashMap<>();
		possibleValueSpecifics.put(id11, val11);
		possibleValueSpecifics.put(id12, val12);
		possibleValueSpecifics.put(id13, val13);

		final CPSMasterDataCharacteristicSpecificContainer cstic1 = new CPSMasterDataCharacteristicSpecificContainer();
		cstic1.setId("cstic1");
		cstic1.setPossibleValueSpecifics(possibleValueSpecifics);
		cstics.put("cstic1", cstic1);

		final CPSMasterDataCharacteristicSpecificContainer cstic2 = new CPSMasterDataCharacteristicSpecificContainer();
		cstic2.setId("cstic2");
		cstic2.setPossibleValueSpecifics(Collections.emptyMap());
		cstics.put("cstic2", cstic2);

		final String id31 = "val3.1";
		final CPSMasterDataPossibleValueSpecific val31 = createPossibleValueSpecific(id31, "vc3.1");
		final Map<String, CPSMasterDataPossibleValueSpecific> possibleValueSpecifics2 = new HashMap<>();
		possibleValueSpecifics2.put(id31, val31);

		final CPSMasterDataCharacteristicSpecificContainer cstic3 = new CPSMasterDataCharacteristicSpecificContainer();
		cstic3.setId("cstic3");
		cstic3.setPossibleValueSpecifics(possibleValueSpecifics2);
		cstics.put("cstic3", cstic3);

		productContainer.setCstics(cstics);

		classUnderTest.fillVariantConditions(productContainer, target);
		assertEquals(4, target.getVariantConditions().size());
		assertTrue(isVariantConditionPresent("vc1.1", target.getVariantConditions()));
		assertTrue(isVariantConditionPresent("vc1.2", target.getVariantConditions()));
		assertTrue(isVariantConditionPresent("vc1.3", target.getVariantConditions()));
		assertTrue(isVariantConditionPresent("vc3.1", target.getVariantConditions()));
	}

	private CPSMasterDataPossibleValueSpecific createPossibleValueSpecific(final String id, final String pricingKey)
	{
		final CPSMasterDataPossibleValueSpecific possibleValueSpecific = new CPSMasterDataPossibleValueSpecific();
		possibleValueSpecific.setId(id);
		possibleValueSpecific.setVariantConditionKey(pricingKey);
		return possibleValueSpecific;
	}

	protected boolean isVariantConditionPresent(final String pricingKey, final List<CPSVariantCondition> conditions)
	{
		for (final CPSVariantCondition condition : conditions)
		{
			if (condition.getKey().equals(pricingKey))
			{
				return true;
			}
		}
		return false;
	}

	@Test
	public void testGetIsoUOM()
	{
		final String isoCodeUnit = classUnderTest.getIsoUOM(productContainer);
		assertEquals(UOM_ST, isoCodeUnit);
	}

	@Test
	public void testGetIsoUOMProductNotInHybris()
	{
		Mockito.when(productService.getProductForCode(Mockito.anyString())).thenThrow(new UnknownIdentifierException("NotFound"));
		productContainer.setUnitOfMeasure(UOM_ST);
		final String isoCodeUnit = classUnderTest.getIsoUOM(productContainer);
		assertEquals(UOM_ST, isoCodeUnit);
	}

	@Test
	public void testRetrievePricingProductNullContext()
	{
		final String productCode = classUnderTest.retrievePricingProduct(productContainer, null);
		assertEquals(PRODUCT_ID, productCode);
	}

	@Test
	public void testRetrievePricingProductEmptyContext()
	{
		final String productCode = classUnderTest.retrievePricingProduct(productContainer, new MasterDataContext());
		assertEquals(PRODUCT_ID, productCode);
	}

	@Test
	public void testRetrievePricingProductFromContext()
	{
		final String productCode = classUnderTest.retrievePricingProduct(productContainer, context);
		assertEquals(PRICING_PRODUCT, productCode);
	}

	@Test
	public void testPopulateFillsPricingProductAttribute()
	{
		classUnderTest.populate(productContainer, target, context);
		boolean found = false;
		for (final Attribute attr : target.getAttributes())
		{
			if (SapproductconfigruntimecpsConstants.PRICING_ATTRIBUTE_MATERIAL_NUMBER.equals(attr.getName()))
			{
				assertEquals(PRICING_PRODUCT, attr.getValues().get(0));
				found = true;
			}
		}
		assertTrue("Pricing Attribute with material number i mandatory", found);
	}



}

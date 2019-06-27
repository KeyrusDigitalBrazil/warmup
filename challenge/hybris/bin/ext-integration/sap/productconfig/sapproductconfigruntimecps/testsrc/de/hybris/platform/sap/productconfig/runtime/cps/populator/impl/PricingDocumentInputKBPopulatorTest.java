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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataProductContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingItemInput;
import de.hybris.platform.sap.productconfig.runtime.cps.pricing.PricingConfigurationParameterCPS;
import de.hybris.platform.sap.productconfig.runtime.interf.ContextualConverter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class PricingDocumentInputKBPopulatorTest
{
	private static final String PRODUCT_ID = "product1";
	private static final String PRODUCT_NAME = "The Product 1";
	private static final String PRODUCT_ID2 = "product2";
	private static final String PRODUCT_NAME2 = "The Product 2";
	private static final String UOM = "PCE";
	private PricingDocumentInputKBPopulator classUnderTest;
	private PricingDocumentInput pricingDocumentInput;
	@Mock
	private ContextualConverter<CPSMasterDataProductContainer, PricingItemInput, MasterDataContext> pricingItemInputKBProductConverter;
	@Mock
	private CommonI18NService i18NService;
	@Mock
	private PricingConfigurationParameterCPS pricingConfigurationParameter;
	private final CPSMasterDataProductContainer productContainer = new CPSMasterDataProductContainer();
	private final CPSMasterDataKnowledgeBaseContainer source = new CPSMasterDataKnowledgeBaseContainer();



	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new PricingDocumentInputKBPopulator();
		classUnderTest.setI18NService(i18NService);
		classUnderTest.setPricingConfigurationParameter(pricingConfigurationParameter);
		pricingDocumentInput = new PricingDocumentInput();
		pricingDocumentInput.setItems(new ArrayList<PricingItemInput>());
		classUnderTest.setPricingItemInputKBProductConverter(pricingItemInputKBProductConverter);
		source.setRootUnitOfMeasure(UOM);
		productContainer.setId(PRODUCT_ID);
		productContainer.setName(PRODUCT_NAME);
		final Map<String, CPSMasterDataProductContainer> products = createProductContainer();
		source.setProducts(products);

	}

	@Test
	public void testPopulate()
	{
		final PricingDocumentInput target = new PricingDocumentInput();
		classUnderTest.populate(source, target, null);
		assertNotNull(target.getItems());
		assertFalse(target.isGroupCondition());
		assertTrue(target.isItemConditionsRequired());
	}

	@Test
	public void testFillPricingItemsInputFromProducts()
	{
		classUnderTest.fillPricingItemsInputFromProducts(source, pricingDocumentInput, null);
		Mockito.verify(pricingItemInputKBProductConverter, Mockito.times(2)).convertWithContext(Mockito.any(), Mockito.any());
	}


	protected Map<String, CPSMasterDataProductContainer> createProductContainer()
	{

		final Map<String, CPSMasterDataProductContainer> products = new HashMap<>();
		products.put(PRODUCT_ID, productContainer);

		final CPSMasterDataProductContainer productContainer2 = new CPSMasterDataProductContainer();
		productContainer2.setId(PRODUCT_ID2);
		productContainer2.setName(PRODUCT_NAME2);
		products.put(PRODUCT_ID2, productContainer2);
		return products;
	}

	@Test
	public void testEnrichProductWithUnit()
	{
		classUnderTest.enrichProductWithUnit(productContainer, source);
		assertEquals(UOM, productContainer.getUnitOfMeasure());
	}

	@Test(expected = NullPointerException.class)
	public void testEnrichProductWithUnitMapIsNull()
	{
		source.setRootUnitOfMeasure(null);
		classUnderTest.enrichProductWithUnit(productContainer, source);
	}


}

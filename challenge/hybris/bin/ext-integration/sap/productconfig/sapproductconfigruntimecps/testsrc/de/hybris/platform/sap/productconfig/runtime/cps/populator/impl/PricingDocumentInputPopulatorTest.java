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
import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingItemInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.sap.productconfig.runtime.interf.ContextualConverter;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigurationRetrievalOptions;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class PricingDocumentInputPopulatorTest
{
	private static final String ITEM_ID = "item1";
	private PricingDocumentInputPopulator classUnderTest;
	private PricingDocumentInput pricingDocumentInput;
	private ConfigurationRetrievalOptions context;
	@Mock
	private ContextualConverter<CPSItem, PricingItemInput, ConfigurationRetrievalOptions> pricingItemInputConverter;



	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new PricingDocumentInputPopulator();
		context = new ConfigurationRetrievalOptions();
		classUnderTest.setPricingItemInputConverter(pricingItemInputConverter);
		pricingDocumentInput = new PricingDocumentInput();
		Mockito.when(pricingItemInputConverter.convertWithContext(Mockito.any(), Mockito.any()))
				.thenAnswer(invocation -> new PricingItemInput());

	}

	@Test
	public void testfillPricingItemsInput_singleLevel()
	{
		pricingDocumentInput.setItems(new ArrayList<PricingItemInput>());
		final CPSConfiguration source = new CPSConfiguration();
		final CPSItem rootItems = createItem();
		source.setRootItem(rootItems);
		classUnderTest.fillPricingItemsInput(source, pricingDocumentInput, context);
		Mockito.verify(pricingItemInputConverter, Mockito.times(1)).convertWithContext(Mockito.any(), Mockito.eq(context));
	}

	@Test
	public void testfillPricingItemInput_NotMara()
	{
		pricingDocumentInput.setItems(new ArrayList<PricingItemInput>());
		final CPSConfiguration source = new CPSConfiguration();
		final CPSItem rootItems = createItem();
		rootItems.setType("not mara");
		source.setRootItem(rootItems);
		classUnderTest.fillPricingItemsInput(source, pricingDocumentInput, context);
		Mockito.verify(pricingItemInputConverter, Mockito.times(0)).convertWithContext(Mockito.any(), Mockito.eq(context));
	}

	@Test
	public void testfillPricingItemsInput_multiLevel()
	{
		pricingDocumentInput.setItems(new ArrayList<PricingItemInput>());
		final CPSConfiguration source = new CPSConfiguration();
		final CPSItem rootItem = createItem();
		final CPSItem subItem = createItem();
		rootItem.getSubItems().add(subItem);
		source.setRootItem(rootItem);
		classUnderTest.fillPricingItemsInput(source, pricingDocumentInput, context);
		Mockito.verify(pricingItemInputConverter, Mockito.times(2)).convertWithContext(Mockito.any(), Mockito.eq(context));
		assertEquals(1, pricingDocumentInput.getItems().size());
		assertEquals(1, pricingDocumentInput.getItems().get(0).getSubItems().size());
	}

	@Test
	public void testFillPricingItemInput_dealWithNull()
	{
		pricingDocumentInput.setItems(new ArrayList<PricingItemInput>());
		final CPSConfiguration source = new CPSConfiguration();
		final CPSItem rootItems = createItem();
		source.setRootItem(rootItems);
		rootItems.setSubItems(null);
		classUnderTest.fillPricingItemsInput(source, pricingDocumentInput, context);
		Mockito.verify(pricingItemInputConverter, Mockito.times(1)).convertWithContext(Mockito.any(), Mockito.eq(context));
	}

	protected CPSItem createItem()
	{
		final CPSItem item = new CPSItem();
		item.setId(ITEM_ID);
		item.setType(SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA);
		item.setSubItems(new ArrayList<>());
		return item;
	}

}

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
package de.hybris.platform.sap.productconfig.cpiquoteexchange.service.outbound;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.sap.hybris.sapquoteintegration.model.SAPCpiOutboundQuoteModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.store.BaseStoreModel;

@UnitTest
public class ProductConfigSCPIQuoteMapperImplTest
{

	private static final String PRICING_PROC = "ZTEST";
	private static final String DEFAULT_PRICING_PROC = "ZDEFAULT";
	@InjectMocks
	private ProductConfigSCPIQuoteMapperImpl classUnderTest = new ProductConfigSCPIQuoteMapperImpl();
	private QuoteModel quoteModel = new QuoteModel();
	private AbstractOrderEntryModel entryConfigurable = new AbstractOrderEntryModel();
	private AbstractOrderEntryModel entryNonConfigurable = new AbstractOrderEntryModel();
	private ProductConfigurationModel productConfigModel = new ProductConfigurationModel();
	private SAPCpiOutboundQuoteModel outboundModel = new SAPCpiOutboundQuoteModel();
	private BaseStoreModel baseStoreModel = new BaseStoreModel();
	private SAPConfigurationModel sapConfigurationModel = new SAPConfigurationModel();
	

	
	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		quoteModel.setEntries(new ArrayList<AbstractOrderEntryModel>());
		quoteModel.getEntries().add(entryConfigurable);
		quoteModel.getEntries().add(entryNonConfigurable);
		entryConfigurable.setProductConfiguration(productConfigModel);
		
		outboundModel.setPricingProcedure(DEFAULT_PRICING_PROC);
		quoteModel.setStore(baseStoreModel);
		baseStoreModel.setSAPConfiguration(sapConfigurationModel );
		sapConfigurationModel.setProperty(ProductConfigSCPIQuoteMapperImpl.SAPPRODUCTCONFIG_PRICINGPROCEDURE_CPS, PRICING_PROC);
	}
	
	
	@Test
	public void testContainsAtLestOneConfigurationFalse()
	{
		quoteModel.getEntries().remove(entryConfigurable);
		assertFalse(classUnderTest.containsAtLeastOneConfiguration(quoteModel));
	}

	@Test
	public void testContainsAtLestOneConfigurationTrue()
	{
		assertTrue(classUnderTest.containsAtLeastOneConfiguration(quoteModel));
	}
	
	@Test
	public void testSetPricingProcedure() {
		classUnderTest.map(quoteModel, outboundModel );
		assertEquals(PRICING_PROC, outboundModel.getPricingProcedure());
	}
	
	@Test
	public void testSetPricingProcedureNull() {
		sapConfigurationModel.setProperty(ProductConfigSCPIQuoteMapperImpl.SAPPRODUCTCONFIG_PRICINGPROCEDURE_CPS, null);
		classUnderTest.map(quoteModel, outboundModel );
		assertEquals(DEFAULT_PRICING_PROC, outboundModel.getPricingProcedure());
	}
}

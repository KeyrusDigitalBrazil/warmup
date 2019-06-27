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
package com.sap.hybris.saprevenuecloudproduct.inbound;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.subscription.BillingTimeService;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.sap.hybris.saprevenuecloudproduct.model.SAPMarketToCatalogMappingModel;
import com.sap.hybris.saprevenuecloudproduct.service.SapRevenueCloudProductService;



/**
 * JUnit test suite for {@link SapRevenueCloudProductInboudHelperTest}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SapRevenueCloudProductInboudHelperTest
{
	private String defaultCatalogId;
	private String defaultCatalogVersion;

	@Mock
	private CatalogService catalogService;
	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private ProductService productService;
	@Mock
	private SapRevenueCloudProductService sapRevenueCloudProductService;
	@Mock
	private ModelService modelService;
	@Mock
	private BillingTimeService billingTimeService;
	@Mock
	private GenericDao<SAPMarketToCatalogMappingModel> sapMarketToCatalogMappingModelGenericDao;

	@InjectMocks
	private SapRevenueCloudProductInboudHelper sapRevenueCloudProductInboudHelper;

	@Before
	public void setUp()
	{
		defaultCatalogVersion = "Staged";
		defaultCatalogId = "reveuneCloudProductCatalog";
		sapRevenueCloudProductInboudHelper.setDefaultCatalogId(defaultCatalogId);
		sapRevenueCloudProductInboudHelper.setDefaultCatalogVersion(defaultCatalogVersion);
		when(sapMarketToCatalogMappingModelGenericDao.find()).thenReturn(getDummyMarketToCatalogList());
	}

	@Test
	public void checkCatalogReturnedForMarket()
	{

		final CatalogModel catalog = sapRevenueCloudProductInboudHelper.mapMarketToCatalog("m1");
		assertEquals(catalog.getId(), "catalog1");
	}

	@Test
	public void checkForDefaultCatalogForNonExistingMarketMapping()
	{
		final CatalogModel defCatalog = new CatalogModel();
		defCatalog.setId(defaultCatalogId);
		when(catalogService.getCatalogForId(defaultCatalogId)).thenReturn(defCatalog);
		final CatalogModel catalog = sapRevenueCloudProductInboudHelper.mapMarketToCatalog("nonexistmarket");
		assertEquals(catalog.getId(), defaultCatalogId);
	}

	@Test
	public void checkIFVaidCatalogIsReturned()
	{
		final CatalogModel catalog = sapRevenueCloudProductInboudHelper.mapMarketToCatalog("m1");
		assertEquals(catalog.getId(), "catalog1");
	}

	@Test(expected = IllegalArgumentException.class)
	public void throwErrorIfProductAndMarketCodeStringIsInvalid()
	{
		sapRevenueCloudProductInboudHelper.processProductForCodeAndMarketId("");
	}

	@Test
	public void checkForSubscriptionPricePlanEndDate()
	{
		final CatalogVersionModel cv = getDummyCatalogVersion();
		final ProductModel p = new ProductModel();
		final Calendar c = Calendar.getInstance();

		final SubscriptionPricePlanModel sp1 = new SubscriptionPricePlanModel();
		sp1.setPricePlanId("sp1Id");
		sp1.setProduct(p);

		final SubscriptionPricePlanModel sp2 = new SubscriptionPricePlanModel();
		c.setTime(new Date());
		c.add(Calendar.YEAR, -1);
		sp2.setPricePlanId("sp2Id");
		final Date sp2EndDate = c.getTime();
		sp2.setEndTime(sp2EndDate);

		final SubscriptionPricePlanModel sp3 = new SubscriptionPricePlanModel();
		sp3.setPricePlanId("sp3Id");
		c.setTime(new Date());
		c.add(Calendar.YEAR, 1);
		final Date sp3EndDate = c.getTime();
		sp3.setEndTime(sp3EndDate);

		p.setEurope1Prices(Arrays.asList(sp1, sp2, sp3));
		when(catalogVersionService.getCatalogVersion(any(String.class), any(String.class))).thenReturn(cv);
		when(sapRevenueCloudProductService.getSubscriptionPricePlanForId(any(String.class), eq(cv))).thenReturn(sp1);
		sapRevenueCloudProductInboudHelper.processSubscriptionPricePlanEndDate("sp1Id", new CatalogVersion());
		doNothing().when(modelService).save(any(SubscriptionPricePlanModel.class));


		//Check for the subscriptionPrice Plan, the end date with future date has changed or not.
		assertNotEquals(sp3.getEndTime(), sp2EndDate);

		//Check the end Date previous to current date remain unchanged
		assertEquals(sp2.getEndTime(), sp2EndDate);

	}


	private List<SAPMarketToCatalogMappingModel> getDummyMarketToCatalogList()
	{
		final SAPMarketToCatalogMappingModel mc1 = createMarketToCatalogMapping("m1", "catalog1");
		final SAPMarketToCatalogMappingModel mc2 = createMarketToCatalogMapping("m2", "catalog2");
		final SAPMarketToCatalogMappingModel mc3 = createMarketToCatalogMapping("m3", "catalog3");
		return Arrays.asList(mc1, mc2, mc3);

	}

	private SAPMarketToCatalogMappingModel createMarketToCatalogMapping(final String market, final String catalog)
	{
		final SAPMarketToCatalogMappingModel mc = new SAPMarketToCatalogMappingModel();
		mc.setMarketId(market);
		final CatalogModel c = new CatalogModel();
		c.setId(catalog);
		mc.setCatalog(c);
		return mc;

	}

	private CatalogVersionModel getDummyCatalogVersion()
	{
		final CatalogVersionModel cv = new CatalogVersionModel();
		final CatalogModel c = new CatalogModel();
		c.setId("dummyCatalogId");
		cv.setCatalog(c);
		cv.setVersion("dummyCatalogVersion");
		return cv;
	}


}

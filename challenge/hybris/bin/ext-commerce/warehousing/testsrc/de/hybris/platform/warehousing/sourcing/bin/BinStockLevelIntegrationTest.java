/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.warehousing.sourcing.bin;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.stock.StockService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.storelocator.pos.PointOfServiceService;
import de.hybris.platform.warehousing.atp.services.impl.WarehousingCommerceStockService;
import de.hybris.platform.warehousing.constants.WarehousingTestConstants;
import de.hybris.platform.warehousing.util.BaseWarehousingIntegrationTest;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


@IntegrationTest
public class BinStockLevelIntegrationTest extends BaseWarehousingIntegrationTest
{
	private final static long FIVE = 5;
	private final static long TEN = 10;

	private final static String POS_NAME = "Nakano";
	private final static String PRODUCT_CODE_1 = "product1";
	private final static String PRODUCT_CODE_3 = "product3";
	private final static String STOCKLEVEL_BINS = "bin1,bin2";

	@Resource
	private WarehousingCommerceStockService commerceStockService;

	@Resource
	private PointOfServiceService pointOfServiceService;

	@Resource
	private ProductService productService;

	@Resource
	private StockService defaultStockService;

	@Before
	public void setup() throws IOException, ImpExException
	{
		importCsv("/warehousing/test/impex/binstocklevel-test-data.impex", WarehousingTestConstants.ENCODING);
	}

		@Test
		public void shouldFindStockLevelHavingBins()
		{
			final PointOfServiceModel pos = pointOfServiceService.getPointOfServiceForName(POS_NAME);
			final ProductModel product = productService.getProductForCode(PRODUCT_CODE_1);
			final StockLevelModel stockLevel = defaultStockService.getStockLevel(product, pos.getWarehouses().get(0));
			final Long availableStock = commerceStockService.getStockLevelForProductAndPointOfService(product, pos);

			assertEquals(TEN, availableStock.longValue());
			assertEquals(STOCKLEVEL_BINS, stockLevel.getBin());
		}

		@Test
		public void shouldFindStockLevelNotHavingBins()
		{
			final PointOfServiceModel pos = pointOfServiceService.getPointOfServiceForName(POS_NAME);
			final ProductModel product = productService.getProductForCode(PRODUCT_CODE_3);
			final StockLevelModel stockLevel = defaultStockService.getStockLevel(product, pos.getWarehouses().get(0));
			final Long availableStock = commerceStockService.getStockLevelForProductAndPointOfService(product, pos);

			assertEquals(FIVE, availableStock.longValue());
			assertNull(stockLevel.getBin());
		}
}

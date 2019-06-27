/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.warehousing.asn.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.warehousing.asn.service.AsnService;
import de.hybris.platform.warehousing.asn.strategy.AsnReleaseDateStrategy;
import de.hybris.platform.warehousing.asn.strategy.BinSelectionStrategy;
import de.hybris.platform.warehousing.asn.strategy.impl.DelayedReleaseDateStrategy;
import de.hybris.platform.warehousing.model.AdvancedShippingNoticeEntryModel;
import de.hybris.platform.warehousing.model.AdvancedShippingNoticeModel;
import de.hybris.platform.warehousing.util.BaseWarehousingIntegrationTest;
import de.hybris.platform.warehousing.util.models.Asns;
import de.hybris.platform.warehousing.util.models.Warehouses;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration test for stock level creation based on ASN.
 */
@IntegrationTest
public class DefaultAsnServiceIntegrationTest extends BaseWarehousingIntegrationTest
{
	private static final String PRODUCT_CODE_1 = "123";
	private static final String PRODUCT_CODE_2 = "456";
	private static final String PRODUCT_CODE_3 = "789";
	private static final int QUANTITY_1 = 34;
	private static final int QUANTITY_2 = 9;
	private static final int QUANTITY_3 = 176;
	private static final AdvancedShippingNoticeEntryModel asnEntry1 = new AdvancedShippingNoticeEntryModel();
	private static final AdvancedShippingNoticeEntryModel asnEntry2 = new AdvancedShippingNoticeEntryModel();
	private static final AdvancedShippingNoticeEntryModel asnEntry3 = new AdvancedShippingNoticeEntryModel();
	private static Date delayedReleaseDate;
	private static AdvancedShippingNoticeModel asn = new AdvancedShippingNoticeModel();
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private Configuration configuration;

	@Resource
	private AsnService defaultAsnService;
	@Resource
	private BinSelectionStrategy noBinSelectionStrategy;
	@Resource
	private AsnReleaseDateStrategy delayedReleaseDateStrategy;
	@Resource
	private ConfigurationService configurationService;
	@Resource
	private Asns asns;
	@Resource
	protected Warehouses warehouses;

	@Before
	public void setUp() throws Exception
	{
		final Date releaseDate = sdf.parse("2016-11-04");
		delayedReleaseDate = sdf.parse("2016-11-06");
		asn = asns.EXT123(releaseDate);
		asnEntry1.setProductCode(PRODUCT_CODE_1);
		asnEntry1.setQuantity(QUANTITY_1);
		asnEntry1.setAsn(asn);
		asnEntry2.setProductCode(PRODUCT_CODE_2);
		asnEntry2.setQuantity(QUANTITY_2);
		asnEntry2.setAsn(asn);
		asnEntry3.setProductCode(PRODUCT_CODE_3);
		asnEntry3.setQuantity(QUANTITY_3);
		asnEntry3.setAsn(asn);
		final List<AdvancedShippingNoticeEntryModel> asnEntries = Arrays.asList(asnEntry1, asnEntry2, asnEntry3);
		asn.setAsnEntries(asnEntries);
	}

	/**
	 * Should create stock levels based on ASN data.
	 *
	 * @throws ParseException
	 */
	@Test
	public void shouldCreateStockLevelBasedOnAsn() throws ParseException
	{
		//Given

		configuration = configurationService.getConfiguration();
		configuration.setProperty(DelayedReleaseDateStrategy.DELAY_DAYS, "2");
		final WarehouseModel warehouse = warehouses.Boston();


		//When
		defaultAsnService.processAsn(asn);
		//Then
		final Set<StockLevelModel> stockLevelsForEntry1 = asnEntry1.getStockLevels();
		assertNotNull(stockLevelsForEntry1);
		final StockLevelModel stockLevel = stockLevelsForEntry1.iterator().next();
		assertEquals(stockLevel.getProductCode(), PRODUCT_CODE_1);
		assertEquals(stockLevel.getAvailable(), QUANTITY_1);
		assertNull(stockLevel.getBin());
		assertEquals(stockLevel.getReleaseDate(), delayedReleaseDate);
		assertEquals(stockLevel.getAsnEntry(), asnEntry1);
		assertEquals(stockLevel.getWarehouse(), warehouse);

		final Set<StockLevelModel> stockLevelsForEntry2 = asnEntry2.getStockLevels();
		assertNotNull(stockLevelsForEntry2);
		final StockLevelModel stockLevel2 = stockLevelsForEntry2.iterator().next();
		assertEquals(stockLevel2.getProductCode(), PRODUCT_CODE_2);
		assertEquals(stockLevel2.getAvailable(), QUANTITY_2);
		assertNull(stockLevel2.getBin());
		assertEquals(stockLevel2.getReleaseDate(), delayedReleaseDate);
		assertEquals(stockLevel2.getAsnEntry(), asnEntry2);
		assertEquals(stockLevel2.getWarehouse(), warehouse);

		final Set<StockLevelModel> stockLevelsForEntry3 = asnEntry3.getStockLevels();
		assertNotNull(stockLevelsForEntry3);
		final StockLevelModel stockLevel3 = stockLevelsForEntry3.iterator().next();
		assertEquals(stockLevel3.getProductCode(), PRODUCT_CODE_3);
		assertEquals(stockLevel3.getAvailable(), QUANTITY_3);
		assertNull(stockLevel3.getBin());
		assertEquals(stockLevel3.getReleaseDate(), delayedReleaseDate);
		assertEquals(stockLevel3.getAsnEntry(), asnEntry3);
		assertEquals(stockLevel3.getWarehouse(), warehouse);
	}

}

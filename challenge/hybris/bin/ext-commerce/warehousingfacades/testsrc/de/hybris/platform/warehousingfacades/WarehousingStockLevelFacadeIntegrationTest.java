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
 */
package de.hybris.platform.warehousingfacades;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.enums.InStockStatus;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.warehousing.enums.StockLevelAdjustmentReason;
import de.hybris.platform.warehousing.stock.services.impl.DefaultWarehouseStockService;
import de.hybris.platform.warehousing.util.builder.StockLevelModelBuilder;
import de.hybris.platform.warehousingfacades.product.data.StockLevelData;
import de.hybris.platform.warehousingfacades.stocklevel.WarehousingStockLevelFacade;
import de.hybris.platform.warehousingfacades.stocklevel.data.StockLevelAdjustmentData;
import de.hybris.platform.warehousingfacades.storelocator.data.WarehouseData;
import de.hybris.platform.warehousingfacades.util.BaseWarehousingFacadeIntegrationTest;

import javax.annotation.Resource;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


@IntegrationTest
public class WarehousingStockLevelFacadeIntegrationTest extends BaseWarehousingFacadeIntegrationTest
{
	private static final String COMMENT_TEXT = "A test comment";
	private static final int STOCKLEVEL_QTY = 20;
	private static final long INCREASE_QTY = 8L;
	private static final long WASTAGE_QTY = 3L;
	private static final long SHRINKAGE_QTY = 2L;
	private static final String BIN_CODE_1 = "bin1";
	private static final String BIN_CODE_2 = "bin2";

	private StockLevelModel stockLevels_Montreal_Camera;
	private Date date = new Date();
	private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

	@Resource
	private WarehousingStockLevelFacade warehousingStockLevelFacade;

	@Resource
	private DefaultWarehouseStockService warehouseStockService;

	String cameraProductCode;
	String lensProductCode;

	@Before
	public void setup() {
		cameraProductCode = products.Camera().getCode();
		lensProductCode = products.Lens().getCode();
		stockLevels.Camera(warehouses.Montreal(), STOCKLEVEL_QTY);
		stockLevels_Montreal_Camera = stockLevels.Camera(warehouses.Montreal(), STOCKLEVEL_QTY);
	}

	@Test
	public void isGetStockLevelAdjustmentReasons_Successfully()
	{
		//when
		List<StockLevelAdjustmentReason> reasons = warehousingStockLevelFacade.getStockLevelAdjustmentReasons();
		//then
		assertTrue(reasons.contains(StockLevelAdjustmentReason.INCREASE));
		assertTrue(reasons.contains(StockLevelAdjustmentReason.SHRINKAGE));
		assertTrue(reasons.contains(StockLevelAdjustmentReason.WASTAGE));
	}

	@Test
	public void createStockLevel_Success()
	{
		//when
		warehousingStockLevelFacade.createStockLevel(createStockLevelData(warehouses.Montreal().getCode(), Long.valueOf(STOCKLEVEL_QTY), products.MemoryCard().getCode()));

		//then
		assertEquals(STOCKLEVEL_QTY, warehouseStockService.getStockLevelForProductCodeAndWarehouse(products.MemoryCard().getCode(), warehouses.Montreal()).longValue());
	}

	@Test
	public void getStockLevelsForWarehouseCode_Success()
	{
		//when
		stockLevels.Lens(warehouses.Montreal(), STOCKLEVEL_QTY);
		stockLevels.MemoryCard(warehouses.Montreal(), STOCKLEVEL_QTY);
		SearchPageData<StockLevelData> result = warehousingStockLevelFacade.getStockLevelsForWarehouseCode(warehouses.Montreal().getCode(), createPageable());

		//then
		assertEquals(3, result.getResults().size());
	}

	@Test
	public void getStockLevelsForWarehouseCode_NoStock()
	{
		//when
		SearchPageData<StockLevelData> result = warehousingStockLevelFacade.getStockLevelsForWarehouseCode(warehouses.Boston().getCode(), createPageable());

		//then
		assertEquals(0, result.getResults().size());
	}

	@Test
	public void createStockLevelAdjustment_NoComment()
	{
		//When
		Long initialStock = warehouseStockService.getStockLevelForProductCodeAndWarehouse(cameraProductCode, warehouses.Montreal());
		StockLevelAdjustmentData stockLevelAdjustmentData = createStockLevelAdjustmentData(StockLevelAdjustmentReason.INCREASE, INCREASE_QTY, null);
		List<StockLevelAdjustmentData> stockLevelAdjustmentDatas = Arrays.asList(stockLevelAdjustmentData);
		List<StockLevelAdjustmentData> response = warehousingStockLevelFacade.createStockLevelAdjustements(cameraProductCode, warehouses.Montreal().getCode(), null, null, stockLevelAdjustmentDatas);

		//then
		assertEquals(initialStock+INCREASE_QTY, warehouseStockService.getStockLevelForProductCodeAndWarehouse(cameraProductCode, warehouses.Montreal()).longValue());
		assertEquals(INCREASE_QTY, response.iterator().next().getQuantity().longValue());
		assertEquals(StockLevelAdjustmentReason.INCREASE, response.iterator().next().getReason());
	}

	@Test
	public void createStockLevelAdjustment_ExternalWarehouse()
	{
		//When
		StockLevelAdjustmentData stockLevelAdjustmentData = createStockLevelAdjustmentData(StockLevelAdjustmentReason.INCREASE, INCREASE_QTY, null);
		List<StockLevelAdjustmentData> stockLevelAdjustmentDatas = Arrays.asList(stockLevelAdjustmentData);
		List<StockLevelAdjustmentData> response = warehousingStockLevelFacade.createStockLevelAdjustements(cameraProductCode, warehouses.Montreal_External().getCode(), null, null, stockLevelAdjustmentDatas);

		//then
		assertNull(response);
	}

	@Test
	public void createStockLevelAdjustment_StockLevelNull()
	{
		//When
		StockLevelAdjustmentData stockLevelAdjustmentData = createStockLevelAdjustmentData(StockLevelAdjustmentReason.INCREASE, INCREASE_QTY, null);
		List<StockLevelAdjustmentData> stockLevelAdjustmentDatas = Arrays.asList(stockLevelAdjustmentData);

		try
		{
			warehousingStockLevelFacade.createStockLevelAdjustements(cameraProductCode, warehouses.Random().getCode(), null, null,
					stockLevelAdjustmentDatas);
		}catch(IllegalArgumentException e)
		{
			assertTrue(e.getMessage().contains("No StockLevel can be found for product code"));
		}
	}

	@Test
	public void createStockLevelAdjustment_MultiStockLevels()
	{
		//When
		stockLevels.NewStockLevel(products.Camera(), warehouses.Toronto(), STOCKLEVEL_QTY, null);
		stockLevels.NewStockLevel(products.Camera(), warehouses.Toronto(), STOCKLEVEL_QTY, null);
		StockLevelAdjustmentData stockLevelAdjustmentData = createStockLevelAdjustmentData(StockLevelAdjustmentReason.INCREASE, INCREASE_QTY, null);
		List<StockLevelAdjustmentData> stockLevelAdjustmentDatas = Arrays.asList(stockLevelAdjustmentData);

		try
		{
			warehousingStockLevelFacade.createStockLevelAdjustements(cameraProductCode, warehouses.Toronto().getCode(), null, null,
					stockLevelAdjustmentDatas);
		}catch(IllegalArgumentException e)
		{
			assertTrue(e.getMessage().contains("More than one StockLevels have been found for product code [camera] and warehouse [toronto]. You might want to be more specific and provide bin code and/or release date"));
		}
	}

	@Test
	public void createStockLevelAdjustment_WithComment()
	{
		//When
		Long initialStock = warehouseStockService.getStockLevelForProductCodeAndWarehouse(cameraProductCode, warehouses.Montreal());
		StockLevelAdjustmentData stockLevelAdjustmentData = createStockLevelAdjustmentData(StockLevelAdjustmentReason.INCREASE, INCREASE_QTY, COMMENT_TEXT);
		List<StockLevelAdjustmentData> stockLevelAdjustmentDatas = Arrays.asList(stockLevelAdjustmentData);
		List<StockLevelAdjustmentData> response = warehousingStockLevelFacade.createStockLevelAdjustements(cameraProductCode, warehouses.Montreal().getCode(), null, null, stockLevelAdjustmentDatas);

		//then
		assertEquals(initialStock+INCREASE_QTY, warehouseStockService.getStockLevelForProductCodeAndWarehouse(cameraProductCode, warehouses.Montreal()).longValue());
		assertEquals(INCREASE_QTY, response.iterator().next().getQuantity().longValue());
		assertEquals(StockLevelAdjustmentReason.INCREASE, response.iterator().next().getReason());
		assertEquals(COMMENT_TEXT, response.iterator().next().getComment());
	}

	@Test
	public void createMultipleStockLevelAdjustments_NoDuplicate()
	{
		//When
		Long initialStock = warehouseStockService.getStockLevelForProductCodeAndWarehouse(cameraProductCode, warehouses.Montreal());
		StockLevelAdjustmentData stockLevelAdjustmentDataIncrease = createStockLevelAdjustmentData(StockLevelAdjustmentReason.INCREASE, INCREASE_QTY, COMMENT_TEXT);
		StockLevelAdjustmentData stockLevelAdjustmentDataWastage = createStockLevelAdjustmentData(StockLevelAdjustmentReason.WASTAGE, WASTAGE_QTY, COMMENT_TEXT);
		StockLevelAdjustmentData stockLevelAdjustmentDataSkrinkage = createStockLevelAdjustmentData(StockLevelAdjustmentReason.SHRINKAGE, SHRINKAGE_QTY, COMMENT_TEXT);
		List<StockLevelAdjustmentData> stockLevelAdjustmentDatas = Arrays.asList(stockLevelAdjustmentDataIncrease, stockLevelAdjustmentDataWastage, stockLevelAdjustmentDataSkrinkage);
		List<StockLevelAdjustmentData> response = warehousingStockLevelFacade.createStockLevelAdjustements(cameraProductCode, warehouses.Montreal().getCode(), null, null, stockLevelAdjustmentDatas);

		//then
		assertEquals(initialStock+INCREASE_QTY-WASTAGE_QTY-SHRINKAGE_QTY, warehouseStockService.getStockLevelForProductCodeAndWarehouse(cameraProductCode, warehouses.Montreal()).longValue());

		assertTrue(response.stream().filter(stockLevelAdjustment->StockLevelAdjustmentReason.INCREASE.equals(stockLevelAdjustment.getReason())).anyMatch(stockLevelAdjustment->stockLevelAdjustment.getQuantity().equals(INCREASE_QTY)));
		assertTrue(response.stream().filter(stockLevelAdjustment->StockLevelAdjustmentReason.WASTAGE.equals(stockLevelAdjustment.getReason())).anyMatch(stockLevelAdjustment->stockLevelAdjustment.getQuantity().equals(WASTAGE_QTY)));
		assertTrue(response.stream().filter(stockLevelAdjustment->StockLevelAdjustmentReason.SHRINKAGE.equals(stockLevelAdjustment.getReason())).anyMatch(stockLevelAdjustment->stockLevelAdjustment.getQuantity().equals(SHRINKAGE_QTY)));

		assertTrue(response.stream().allMatch(stockLevelAdjustment->COMMENT_TEXT.equals(stockLevelAdjustment.getComment())));
	}

	@Test
	public void createMultipleStockLevelAdjustments_WithDuplicate()
	{
		Long initialStock = warehouseStockService.getStockLevelForProductCodeAndWarehouse(cameraProductCode, warehouses.Montreal());
		StockLevelAdjustmentData stockLevelAdjustmentDataIncrease = createStockLevelAdjustmentData(StockLevelAdjustmentReason.INCREASE, INCREASE_QTY, COMMENT_TEXT);
		StockLevelAdjustmentData stockLevelAdjustmentDataWastage = createStockLevelAdjustmentData(StockLevelAdjustmentReason.WASTAGE, WASTAGE_QTY, COMMENT_TEXT);
		StockLevelAdjustmentData stockLevelAdjustmentDataIncrease2 = createStockLevelAdjustmentData(StockLevelAdjustmentReason.INCREASE, INCREASE_QTY, COMMENT_TEXT);
		List<StockLevelAdjustmentData> stockLevelAdjustmentDatas = Arrays.asList(stockLevelAdjustmentDataIncrease, stockLevelAdjustmentDataWastage, stockLevelAdjustmentDataIncrease2);
		try{
			warehousingStockLevelFacade.createStockLevelAdjustements(cameraProductCode, warehouses.Montreal().getCode(), null, null, stockLevelAdjustmentDatas);
		}
		catch(IllegalArgumentException e){
			assertEquals(initialStock, warehouseStockService.getStockLevelForProductCodeAndWarehouse(cameraProductCode, warehouses.Montreal()));
		}
	}

	@Test
	public void createStockLevelAdjustments_With2bins_Success()
	{
		//when
		createLensStockLevel(STOCKLEVEL_QTY, BIN_CODE_1, warehouses.Montreal(), null);
		createLensStockLevel(STOCKLEVEL_QTY, BIN_CODE_2, warehouses.Montreal(), null);

		Long initialStock = warehouseStockService.getStockLevelForProductCodeAndWarehouse(lensProductCode, warehouses.Montreal());
		StockLevelAdjustmentData stockLevelAdjustmentData = createStockLevelAdjustmentData(StockLevelAdjustmentReason.INCREASE, INCREASE_QTY, null);
		List<StockLevelAdjustmentData> stockLevelAdjustmentDatas = Arrays.asList(stockLevelAdjustmentData);
		List<StockLevelAdjustmentData> response = warehousingStockLevelFacade.createStockLevelAdjustements(lensProductCode, warehouses.Montreal().getCode(), BIN_CODE_1, null, stockLevelAdjustmentDatas);

		//then
		assertEquals(initialStock+INCREASE_QTY, warehouseStockService.getStockLevelForProductCodeAndWarehouse(lensProductCode, warehouses.Montreal()).longValue());
		assertEquals(INCREASE_QTY, response.iterator().next().getQuantity().longValue());
		assertEquals(StockLevelAdjustmentReason.INCREASE, response.iterator().next().getReason());
	}

	@Test(expected = IllegalArgumentException.class)
	public void createStockLevelAdjustments_With2bins_Failure()
	{
		//when
		createLensStockLevel(STOCKLEVEL_QTY, BIN_CODE_1, warehouses.Montreal(), null);
		createLensStockLevel(STOCKLEVEL_QTY, BIN_CODE_2, warehouses.Montreal(), null);

		StockLevelAdjustmentData stockLevelAdjustmentData = createStockLevelAdjustmentData(StockLevelAdjustmentReason.INCREASE,
				INCREASE_QTY, null);
		List<StockLevelAdjustmentData> stockLevelAdjustmentDatas = Arrays.asList(stockLevelAdjustmentData);
		warehousingStockLevelFacade
				.createStockLevelAdjustements(products.Lens().getCode(), warehouses.Montreal().getCode(), null, null,
						stockLevelAdjustmentDatas);
	}

	@Test
	public void createStockLevelAdjustments_With1bins_NoReleaseDate()
	{
		//when
		createLensStockLevel(STOCKLEVEL_QTY, BIN_CODE_1, warehouses.Montreal(), null);
		createLensStockLevel(STOCKLEVEL_QTY, BIN_CODE_1, warehouses.Montreal(), Date.from(LocalDate.now().plusDays(1).atStartOfDay(
				ZoneId.systemDefault()).toInstant()));

		Long initialStock = warehouseStockService.getStockLevelForProductCodeAndWarehouse(lensProductCode, warehouses.Montreal());
		StockLevelAdjustmentData stockLevelAdjustmentData = createStockLevelAdjustmentData(StockLevelAdjustmentReason.INCREASE, INCREASE_QTY, null);
		List<StockLevelAdjustmentData> stockLevelAdjustmentDatas = Arrays.asList(stockLevelAdjustmentData);
		List<StockLevelAdjustmentData> response = warehousingStockLevelFacade.createStockLevelAdjustements(lensProductCode, warehouses.Montreal().getCode(), BIN_CODE_1, null, stockLevelAdjustmentDatas);

		//then
		assertEquals(initialStock+INCREASE_QTY, warehouseStockService.getStockLevelForProductCodeAndWarehouse(lensProductCode, warehouses.Montreal()).longValue());
		assertEquals(INCREASE_QTY, response.iterator().next().getQuantity().longValue());
		assertEquals(StockLevelAdjustmentReason.INCREASE, response.iterator().next().getReason());
	}

	public void createStockLevelAdjustment_WithReleaseDate_SingleResult()
	{
		//given
		stockLevels_Montreal_Camera.setReleaseDate(date);
		modelService.save(stockLevels_Montreal_Camera);
		//stock level 2
		modelService.save(stockLevels.NewStockLevel(products.Camera() ,warehouses.Montreal(), STOCKLEVEL_QTY, null));
		//When
		Long initialStock = warehouseStockService.getStockLevelForProductCodeAndWarehouse(cameraProductCode, warehouses.Montreal());
		StockLevelAdjustmentData stockLevelAdjustmentData = createStockLevelAdjustmentData(StockLevelAdjustmentReason.INCREASE, INCREASE_QTY, COMMENT_TEXT);
		List<StockLevelAdjustmentData> stockLevelAdjustmentDatas = Arrays.asList(stockLevelAdjustmentData);
		List<StockLevelAdjustmentData> response = warehousingStockLevelFacade.createStockLevelAdjustements(cameraProductCode, warehouses.Montreal().getCode(), null, dateFormat.format(date), stockLevelAdjustmentDatas);

		//then
		assertEquals(initialStock+INCREASE_QTY, warehouseStockService.getStockLevelForProductCodeAndWarehouse(cameraProductCode, warehouses.Montreal()).longValue());
		assertEquals(INCREASE_QTY, response.iterator().next().getQuantity().longValue());
		assertEquals(StockLevelAdjustmentReason.INCREASE, response.iterator().next().getReason());
		assertEquals(COMMENT_TEXT, response.iterator().next().getComment());
	}

	@Test
	public void createStockLevelAdjustment_WithDuplicateReleaseDateAndUniqueBin_SingleResult()
	{
		//given
		//stock level 1
		stockLevels_Montreal_Camera.setReleaseDate(date);
		stockLevels_Montreal_Camera.setBin("4");
		modelService.save(stockLevels_Montreal_Camera);
		//stock level 2
		StockLevelModel stockLevels_Montreal_Camera_2 = stockLevels.NewStockLevel(products.Camera() ,warehouses.Montreal(), STOCKLEVEL_QTY,
				null);
		stockLevels_Montreal_Camera_2.setReleaseDate(date);
		stockLevels_Montreal_Camera_2.setBin("3");
		modelService.save(stockLevels_Montreal_Camera_2);
		//When
		Long initialStock = warehouseStockService.getStockLevelForProductCodeAndWarehouse(cameraProductCode, warehouses.Montreal());
		StockLevelAdjustmentData stockLevelAdjustmentData = createStockLevelAdjustmentData(StockLevelAdjustmentReason.SHRINKAGE, SHRINKAGE_QTY, COMMENT_TEXT);
		List<StockLevelAdjustmentData> stockLevelAdjustmentDatas = Arrays.asList(stockLevelAdjustmentData);
		List<StockLevelAdjustmentData> response = warehousingStockLevelFacade.createStockLevelAdjustements(cameraProductCode, warehouses.Montreal().getCode(), "4", dateFormat.format(date), stockLevelAdjustmentDatas);

		//then
		assertEquals(initialStock-SHRINKAGE_QTY, warehouseStockService.getStockLevelForProductCodeAndWarehouse(cameraProductCode, warehouses.Montreal()).longValue());
		assertEquals(SHRINKAGE_QTY, response.iterator().next().getQuantity().longValue());
		assertEquals(StockLevelAdjustmentReason.SHRINKAGE, response.iterator().next().getReason());
		assertEquals(COMMENT_TEXT, response.iterator().next().getComment());
		assertEquals(SHRINKAGE_QTY, stockLevels_Montreal_Camera.getInventoryEvents().iterator().next().getQuantity());
	}

	@Test
	public void createStockLevelAdjustment_WithReleaseDate_DuplicatedResult()
	{
		//given
		//stock level 1
		stockLevels_Montreal_Camera.setReleaseDate(date);
		modelService.save(stockLevels_Montreal_Camera);
		//stock level 2
		StockLevelModel stockLevels_Montreal_Camera_2 = stockLevels.NewStockLevel(products.Camera() ,warehouses.Montreal(), STOCKLEVEL_QTY,
				null);
		stockLevels_Montreal_Camera_2.setReleaseDate(date);
		modelService.save(stockLevels_Montreal_Camera_2);
		//When
		Long initialStock = warehouseStockService.getStockLevelForProductCodeAndWarehouse(cameraProductCode, warehouses.Montreal());
		StockLevelAdjustmentData stockLevelAdjustmentData = createStockLevelAdjustmentData(StockLevelAdjustmentReason.INCREASE, INCREASE_QTY, COMMENT_TEXT);
		List<StockLevelAdjustmentData> stockLevelAdjustmentDatas = Arrays.asList(stockLevelAdjustmentData);
		try
		{
			warehousingStockLevelFacade.createStockLevelAdjustements(cameraProductCode, warehouses.Montreal().getCode(), null, dateFormat.format(date), stockLevelAdjustmentDatas);

		}
		//then
		catch(IllegalArgumentException e){
			assertEquals(initialStock.longValue(), warehouseStockService.getStockLevelForProductCodeAndWarehouse(cameraProductCode, warehouses.Montreal()).longValue());
		}
	}

	@Test (expected = IllegalArgumentException.class)
	public void createStockLevelAdjustment_WithInvalidReleaseDate()
	{
		//given
		stockLevels_Montreal_Camera.setReleaseDate(date);
		modelService.save(stockLevels_Montreal_Camera);

		//When
		StockLevelAdjustmentData stockLevelAdjustmentData = createStockLevelAdjustmentData(StockLevelAdjustmentReason.INCREASE, INCREASE_QTY, COMMENT_TEXT);
		List<StockLevelAdjustmentData> stockLevelAdjustmentDatas = Arrays.asList(stockLevelAdjustmentData);
		List<StockLevelAdjustmentData> response = warehousingStockLevelFacade.createStockLevelAdjustements(cameraProductCode, warehouses.Montreal().getCode(), null, "11/11/1111", stockLevelAdjustmentDatas);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createStockLevelAdjustment_NoQuantity()
	{
		StockLevelAdjustmentData stockLevelAdjustmentData = createStockLevelAdjustmentData(StockLevelAdjustmentReason.INCREASE, null, null);
		List<StockLevelAdjustmentData> stockLevelAdjustmentDatas = Arrays.asList(stockLevelAdjustmentData);
		warehousingStockLevelFacade.createStockLevelAdjustements(cameraProductCode, warehouses.Montreal().getCode(), null, null, stockLevelAdjustmentDatas);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createStockLevelAdjustment_NoReason()
	{
		StockLevelAdjustmentData stockLevelAdjustmentData = createStockLevelAdjustmentData(null, INCREASE_QTY, null);
		List<StockLevelAdjustmentData> stockLevelAdjustmentDatas = Arrays.asList(stockLevelAdjustmentData);
		warehousingStockLevelFacade.createStockLevelAdjustements(cameraProductCode, warehouses.Montreal().getCode(), null, null, stockLevelAdjustmentDatas);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createStockLevel_NoQuantity()
	{
		StockLevelData stockLevelData = createStockLevelData(warehouses.Montreal().getCode(), null, products.MemoryCard().getCode());
		warehousingStockLevelFacade.createStockLevel(stockLevelData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createStockLevel_NoWarehouse()
	{
		StockLevelData stockLevelData = createStockLevelData(null, Long.valueOf(STOCKLEVEL_QTY), products.MemoryCard().getCode());
		warehousingStockLevelFacade.createStockLevel(stockLevelData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createStockLevel_NoProduct()
	{
		StockLevelData stockLevelData = createStockLevelData(warehouses.Montreal().getCode(), Long.valueOf(STOCKLEVEL_QTY), null);
		warehousingStockLevelFacade.createStockLevel(stockLevelData);
	}

	private StockLevelAdjustmentData createStockLevelAdjustmentData(final StockLevelAdjustmentReason reason, final Long quantity, final String comment)
	{
		StockLevelAdjustmentData stockLevelAdjustmentData = new StockLevelAdjustmentData();
		stockLevelAdjustmentData.setReason(reason);
		stockLevelAdjustmentData.setQuantity(quantity);
		stockLevelAdjustmentData.setComment(comment);
		return stockLevelAdjustmentData;
	}

	private StockLevelData createStockLevelData(final String warehouseCode, final Long quantity, final String productCode)
	{
		StockLevelData stockLevelData = new StockLevelData();
		stockLevelData.setWarehouse(createWarehouseData(warehouseCode));
		stockLevelData.setInitialQuantityOnHand(quantity==null?null:quantity.intValue());
		stockLevelData.setProductCode(productCode);
		return stockLevelData;
	}

	private WarehouseData createWarehouseData(final String warehouseCode){
		WarehouseData warehouseData = new WarehouseData();
		warehouseData.setCode(warehouseCode);
		return warehouseData;
	}

	private void createLensStockLevel(final int quantity, final String bin, final WarehouseModel warehouse, final Date releaseDate){
		stockLevels.getModelService().save(StockLevelModelBuilder.aModel()
				.withAvailable(quantity)
				.withBin(bin)
				.withMaxPreOrder(0)
				.withPreOrder(0)
				.withMaxStockLevelHistoryCount(-1)
				.withReserved(0)
				.withWarehouse(warehouse)
				.withProduct(products.Lens())
				.withReleaseDate(releaseDate)
				.withInStockStatus(InStockStatus.NOTSPECIFIED)
				.build());
	}

}

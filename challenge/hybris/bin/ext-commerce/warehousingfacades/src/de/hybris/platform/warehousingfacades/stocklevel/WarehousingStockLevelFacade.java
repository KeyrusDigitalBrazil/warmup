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
package de.hybris.platform.warehousingfacades.stocklevel;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.warehousing.enums.StockLevelAdjustmentReason;
import de.hybris.platform.warehousingfacades.product.data.StockLevelData;
import de.hybris.platform.warehousingfacades.stocklevel.data.StockLevelAdjustmentData;

import java.util.List;


/**
 * Warehousing facade exposing CRUD operations on {@link de.hybris.platform.ordersplitting.model.WarehouseModel}
 */
public interface WarehousingStockLevelFacade
{
	/**
	 * API to get the stocklevels for the {@link WarehouseModel#CODE}
	 *
 	 * @param code
	 *           the code of warehouse to search
	 * @param pageableData
	 *           pageable object that contains info on the number or pages and how many items in each page in addition
	 *           the sorting info
	 * @return list of stocklevels that complies with above conditions
	 */
	SearchPageData<StockLevelData> getStockLevelsForWarehouseCode(String code, PageableData pageableData);

	/**
	 * API to create a {@link de.hybris.platform.ordersplitting.model.StockLevelModel}
	 *
	 * @param stockLevelData
	 * 		the {@link StockLevelData} to create {@link de.hybris.platform.ordersplitting.model.StockLevelModel} in the system
	 * @return the {@link StockLevelData} converted from the newly created {@link de.hybris.platform.ordersplitting.model.StockLevelModel}
	 */
	StockLevelData createStockLevel(StockLevelData stockLevelData);

	/**
	 * API to get all stock level adjustment reasons
	 *
	 * @return a list of {@link StockLevelAdjustmentReason}
	 */
	List<StockLevelAdjustmentReason> getStockLevelAdjustmentReasons();

	/**
	 * API to create one or several {@link StockLevelAdjustmentData} for a specific stock level
	 *
	 * @param productCode
	 * 		the product code of the product for which adjustments are required
	 * @param warehouseCode
	 * 		the warehouse code for which adjustments are required
	 * @param binCode
	 * 		the bin code of the stock level for which adjustments are required
	 * @param releaseDate
	 * 		the release date for which adjustments are required
	 * @param stockLevelAdjustmentDatas
	 * 		the list of stock level adjustements to be created
	 * @return the list of stock level adjustments created
	 */
	List<StockLevelAdjustmentData> createStockLevelAdjustements(String productCode, String warehouseCode, String binCode,
			String releaseDate, List<StockLevelAdjustmentData> stockLevelAdjustmentDatas);
}

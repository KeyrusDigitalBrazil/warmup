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
package de.hybris.platform.warehousing.util.models;

import de.hybris.platform.basecommerce.enums.InStockStatus;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.stock.impl.StockLevelDao;
import de.hybris.platform.warehousing.util.builder.StockLevelModelBuilder;

import java.util.Collections;
import java.util.Date;


public class StockLevels extends AbstractItems<StockLevelModel>
{
	private StockLevelDao stockLevelDao;
	private Warehouses warehouses;
	private Products products;

	public StockLevelModel Camera(final WarehouseModel warehouse, final int quantity)
	{
		return getFromCollectionOrSaveAndReturn(() -> getStockLevelDao().findStockLevels(Products.CODE_CAMERA,
				Collections.singletonList(warehouse)),
				() -> StockLevelModelBuilder.aModel()
						.withAvailable(quantity)
						.withMaxPreOrder(0)
						.withPreOrder(0)
						.withMaxStockLevelHistoryCount(-1)
						.withReserved(0)
						.withWarehouse(warehouse)
						.withProduct(getProducts().Camera())
						.withInStockStatus(InStockStatus.NOTSPECIFIED)
						.build());
	}

	public StockLevelModel MemoryCard(final WarehouseModel warehouse, final int quantity)
	{
		return getFromCollectionOrSaveAndReturn(() -> getStockLevelDao().findStockLevels(Products.CODE_MEMORY_CARD,
				Collections.singletonList(warehouse)),
				() -> StockLevelModelBuilder.aModel()
						.withAvailable(quantity)
						.withMaxPreOrder(0)
						.withPreOrder(0)
						.withMaxStockLevelHistoryCount(-1)
						.withReserved(0)
						.withWarehouse(warehouse)
						.withProduct(getProducts().MemoryCard())
						.withInStockStatus(InStockStatus.NOTSPECIFIED)
						.build());
	}

	public StockLevelModel NewStockLevel(final ProductModel product, final WarehouseModel warehouse, final int quantity,
			final Date releaseDate)
	{
		final StockLevelModel stockLevelModel = StockLevelModelBuilder.aModel()
						.withAvailable(quantity)
						.withMaxPreOrder(0)
						.withPreOrder(0)
						.withMaxStockLevelHistoryCount(-1)
						.withReserved(0)
						.withWarehouse(warehouse)
						.withProduct(product)
						.withInStockStatus(InStockStatus.NOTSPECIFIED)
						.withReleaseDate(releaseDate)
						.build();
		getModelService().save(stockLevelModel);
		return stockLevelModel;
	}

	public StockLevelModel Lens(final WarehouseModel warehouse, final int quantity)
	{
		return getFromCollectionOrSaveAndReturn(() -> getStockLevelDao().findStockLevels(Products.CODE_LENS,
				Collections.singletonList(warehouse)),
				() -> StockLevelModelBuilder.aModel()
						.withAvailable(quantity)
						.withMaxPreOrder(0)
						.withPreOrder(0)
						.withMaxStockLevelHistoryCount(-1)
						.withReserved(0)
						.withWarehouse(warehouse)
						.withProduct(getProducts().Lens())
						.withInStockStatus(InStockStatus.NOTSPECIFIED)
						.build());
	}

	public StockLevelDao getStockLevelDao()
	{
		return stockLevelDao;
	}

	public void setStockLevelDao(final StockLevelDao stockLevelDao)
	{
		this.stockLevelDao = stockLevelDao;
	}

	public Warehouses getWarehouses()
	{
		return warehouses;
	}

	public void setWarehouses(final Warehouses warehouses)
	{
		this.warehouses = warehouses;
	}

	public Products getProducts()
	{
		return products;
	}

	public void setProducts(final Products products)
	{
		this.products = products;
	}

}

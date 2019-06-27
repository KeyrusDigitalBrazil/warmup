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
package de.hybris.platform.warehousing.util.builder;

import de.hybris.platform.basecommerce.enums.InStockStatus;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;

import java.util.Date;


public class StockLevelModelBuilder
{
	private final StockLevelModel model;

	private StockLevelModelBuilder()
	{
		model = new StockLevelModel();
	}

	private StockLevelModel getModel()
	{
		return this.model;
	}

	public static StockLevelModelBuilder aModel()
	{
		return new StockLevelModelBuilder();
	}

	public StockLevelModel build()
	{
		return getModel();
	}

	public StockLevelModelBuilder withAvailable(final int available)
	{
		getModel().setAvailable(available);
		return this;
	}

	public StockLevelModelBuilder withBin(final String bin)
	{
		getModel().setBin(bin);
		return this;
	}

	public StockLevelModelBuilder withInStockStatus(final InStockStatus inStockStatus)
	{
		getModel().setInStockStatus(inStockStatus);
		return this;
	}

	public StockLevelModelBuilder withMaxPreOrder(final int maxPreOrder)
	{
		getModel().setMaxPreOrder(maxPreOrder);
		return this;
	}

	public StockLevelModelBuilder withWarehouse(final WarehouseModel warehouse)
	{
		getModel().setWarehouse(warehouse);
		return this;
	}

	public StockLevelModelBuilder withMaxStockLevelHistoryCount(final int maxStockLevelHistoryCount)
	{
		getModel().setMaxStockLevelHistoryCount(maxStockLevelHistoryCount);
		return this;
	}

	public StockLevelModelBuilder withOverselling(final int overselling)
	{
		getModel().setOverSelling(overselling);
		return this;
	}

	public StockLevelModelBuilder withPreOrder(final int preOrder)
	{
		getModel().setPreOrder(preOrder);
		return this;
	}

	public StockLevelModelBuilder withReserved(final int reserved)
	{
		getModel().setReserved(reserved);
		return this;
	}

	public StockLevelModelBuilder withProduct(final ProductModel product)
	{
		getModel().setProductCode(product.getCode());
		getModel().setProduct(product);
		return this;
	}

	public StockLevelModelBuilder withReleaseDate(final Date releaseDate)
	{
		getModel().setReleaseDate(releaseDate);
		return this;
	}

}

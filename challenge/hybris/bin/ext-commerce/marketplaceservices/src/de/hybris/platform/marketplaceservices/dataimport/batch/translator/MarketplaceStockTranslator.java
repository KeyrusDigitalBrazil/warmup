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
package de.hybris.platform.marketplaceservices.dataimport.batch.translator;

import java.util.Optional;

import de.hybris.platform.acceleratorservices.dataimport.batch.stock.StockTranslator;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.impex.jalo.header.SpecialColumnDescriptor;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.marketplaceservices.vendor.VendorService;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.model.ModelService;


/**
 * Marketplace translator for updating the stock. Validation of warehouse is performed before importing.
 */
public class MarketplaceStockTranslator extends StockTranslator
{
	private static final String VENDOR_SERVICE = "vendorService";
	private static final String MODEL_SERVICE = "modelService";

	private ModelService modelService;
	private VendorService vendorService;

	@Override
	public void performImport(final String cellValue, final Item processedItem)
	{
		final ProductModel product = getModelService().get(processedItem);
		final Optional<VendorModel> vendorOptional = getVendorService().getVendorByProduct(product);
		if (!vendorOptional.isPresent())
		{
			throw new IllegalArgumentException("No vendor is assigned to product " + product.getCode());
		}

		final VendorModel vendor = vendorOptional.get();
		final WarehouseModel warehouse = vendor.getWarehouses().stream().findAny()
				.orElseThrow(() -> new IllegalArgumentException("No Warehouse found for vendor: " + vendor.getCode()));

		super.performImport(cellValue + ":" + warehouse.getCode(), processedItem);
	}

	@Override
	public void init(final SpecialColumnDescriptor columnDescriptor)
	{
		super.init(columnDescriptor);
		setModelService((ModelService) Registry.getApplicationContext().getBean(MODEL_SERVICE));
		setVendorService((VendorService) Registry.getApplicationContext().getBean(VENDOR_SERVICE));
	}

	public ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	public VendorService getVendorService()
	{
		return vendorService;
	}

	public void setVendorService(final VendorService vendorService)
	{
		this.vendorService = vendorService;
	}
}

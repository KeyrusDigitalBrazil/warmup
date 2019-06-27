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

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.impex.jalo.header.StandardColumnDescriptor;
import de.hybris.platform.impex.jalo.translators.AbstractValueTranslator;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.product.ProductManager;
import de.hybris.platform.marketplaceservices.vendor.VendorService;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.product.ProductService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;


/**
 * Products translator for marketplace, to find products under specific CatalogVersion
 */
public class MarketplaceProductsTranslator extends AbstractValueTranslator
{
	private static final String VENDOR_SERVICE = "vendorService";
	private static final String PRODUCT_SERVICE = "productService";
	private static final String CATEGORYVERSION_SERVICE = "catalogVersionService";

	private VendorService vendorService;
	private CatalogVersionService catalogVersionService;
	private ProductService productService;


	@Override
	public Object importValue(final String paramString, final Item paramItem)
	{
		if (StringUtils.isBlank(paramString))
		{
			throw new IllegalArgumentException("product code is missing");
		}

		final String vendorCode = this.getColumnDescriptor().getDescriptorData().getModifier("vendor").trim();
		final String vendorCatalog = this.getColumnDescriptor().getDescriptorData().getModifier("vendorCatalog").trim();
		final String version = this.getColumnDescriptor().getDescriptorData().getModifier("version").trim();
		final Optional<VendorModel> vendorOptional = getVendorService().getVendorByCode(vendorCode);
		if (!vendorOptional.isPresent())
		{
			throw new IllegalArgumentException("Invalid vendor code: " + vendorCode);
		}


		final CatalogVersionModel catalogVersion = this.getCatalogVersionService().getCatalogVersion(vendorCatalog, version);

		final List<Product> products = Arrays.stream(StringUtils.split(paramString, ","))
				.map(productCode -> getProductManager()
						.getProductByPK(productService.getProductForCode(catalogVersion, vendorCode + "_" + productCode).getPk()))
				.collect(Collectors.toList());
		return products;
	}


	@Override
	public String exportValue(final Object paramObject)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void init(final StandardColumnDescriptor descriptor)
	{
		super.init(descriptor);
		setVendorService((VendorService) getApplicationContext().getBean(VENDOR_SERVICE));
		setProductService((ProductService) getApplicationContext().getBean(PRODUCT_SERVICE));
		setCatalogVersionService((CatalogVersionService) getApplicationContext().getBean(CATEGORYVERSION_SERVICE));
	}

	public ProductService getProductService()
	{
		return productService;
	}

	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	public VendorService getVendorService()
	{
		return vendorService;
	}

	public void setVendorService(final VendorService vendorService)
	{
		this.vendorService = vendorService;
	}

	public CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}

	protected ApplicationContext getApplicationContext()
	{
		return Registry.getApplicationContext();
	}

	protected ProductManager getProductManager()
	{
		return ProductManager.getInstance();
	}
}

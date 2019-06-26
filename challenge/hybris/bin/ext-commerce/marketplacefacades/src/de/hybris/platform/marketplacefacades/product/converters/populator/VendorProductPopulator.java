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
package de.hybris.platform.marketplacefacades.product.converters.populator;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.marketplacefacades.vendor.data.VendorData;
import de.hybris.platform.marketplaceservices.vendor.VendorService;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import org.springframework.beans.factory.annotation.Required;


public class VendorProductPopulator implements Populator<ProductModel, ProductData>
{

	private VendorService vendorService;
	private Converter<VendorModel, VendorData> vendorConverter;

	@Override
	public void populate(final ProductModel source, final ProductData target)
	{
		ServicesUtil.validateParameterNotNullStandardMessage("source", source);
		ServicesUtil.validateParameterNotNullStandardMessage("target", target);

		final VendorData vendor = new VendorData();
		getVendorService().getVendorByProduct(source).ifPresent(x -> getVendorConverter().convert(x, vendor));

		target.setVendor(vendor);
		target.setSaleable(source.getSaleable());
	}

	public VendorService getVendorService()
	{
		return vendorService;
	}

	@Required
	public void setVendorService(final VendorService vendorService)
	{
		this.vendorService = vendorService;
	}

	public Converter<VendorModel, VendorData> getVendorConverter()
	{
		return vendorConverter;
	}

	@Required
	public void setVendorConverter(final Converter<VendorModel, VendorData> vendorConverter)
	{
		this.vendorConverter = vendorConverter;
	}



}

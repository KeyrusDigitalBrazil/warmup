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

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.marketplacefacades.vendor.data.VendorData;
import de.hybris.platform.marketplaceservices.vendor.VendorService;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;


public class SearchResultVendorProductPopulator implements Populator<SearchResultValueData, ProductData>
{
	private VendorService vendorService;
	private Converter<VendorModel, VendorData> vendorConverter;

	@Override
	public void populate(final SearchResultValueData source, final ProductData target)
	{
		final VendorData vendor = new VendorData();
		final List<String> vendors = this.<List<String>> getValue(source, "vendor");

		if (vendors != null)
		{
			getVendorService().getVendorByCode(vendors.get(0)).ifPresent(x -> getVendorConverter().convert(x, vendor));
			target.setVendor(vendor);
		}
	}

	protected <T> T getValue(final SearchResultValueData source, final String propertyName)
	{
		if (source.getValues() == null)
		{
			return null;
		}

		// DO NOT REMOVE the cast (T) below, while it should be unnecessary it is required by the javac compiler
		return (T) source.getValues().get(propertyName);
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

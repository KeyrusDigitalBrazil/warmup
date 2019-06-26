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
package de.hybris.platform.marketplacefacades.vendor.impl;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.marketplacefacades.vendor.VendorFacade;
import de.hybris.platform.marketplacefacades.vendor.data.VendorData;
import de.hybris.platform.marketplaceservices.vendor.VendorService;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link VendorFacade}.
 */
public class DefaultVendorFacade implements VendorFacade
{
	private VendorService vendorService;
	private Converter<VendorModel, VendorData> vendorConverter;

	@Override
	public Optional<VendorData> getVendorByCode(final String vendorCode)
	{
		if (vendorService.getVendorByCode(vendorCode).isPresent())
		{

			return Optional.ofNullable(vendorConverter.convert(vendorService.getVendorByCode(vendorCode).get()));

		}

		return Optional.empty();
	}

	@Override
	public SearchPageData<VendorData> getPagedIndexVendors(final PageableData pageableData)
	{
		final SearchPageData<VendorModel> vendors = getVendorService().getIndexVendors(pageableData);
		return convertPageData(vendors, getVendorConverter());
	}

	protected <S, T> SearchPageData<T> convertPageData(final SearchPageData<S> source, final Converter<S, T> converter)
	{
		final SearchPageData<T> result = new SearchPageData<>();
		result.setPagination(source.getPagination());
		result.setSorts(source.getSorts());
		result.setResults(Converters.convertAll(source.getResults(), converter));
		return result;
	}

	protected VendorService getVendorService()
	{
		return vendorService;
	}

	@Required
	public void setVendorService(final VendorService vendorService)
	{
		this.vendorService = vendorService;
	}

	protected Converter<VendorModel, VendorData> getVendorConverter()
	{
		return vendorConverter;
	}

	@Required
	public void setVendorConverter(final Converter<VendorModel, VendorData> vendorConverter)
	{
		this.vendorConverter = vendorConverter;
	}

}
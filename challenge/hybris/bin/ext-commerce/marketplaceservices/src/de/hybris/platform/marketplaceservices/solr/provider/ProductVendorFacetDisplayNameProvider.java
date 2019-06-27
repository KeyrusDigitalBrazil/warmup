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
package de.hybris.platform.marketplaceservices.solr.provider;

import de.hybris.platform.marketplaceservices.vendor.VendorService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractFacetValueDisplayNameProvider;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Required;


/**
 * Display Name provider for product vendor
 */
public class ProductVendorFacetDisplayNameProvider extends AbstractFacetValueDisplayNameProvider
{

	private I18NService i18nService;
	private CommonI18NService commonI18NService;
	private VendorService vendorService;

	@Override
	public String getDisplayName(final SearchQuery query, final IndexedProperty property, final String facetValue)
	{
		if (facetValue == null)
		{
			return "";
		}

		return getVendorService().getVendorByCode(facetValue).map(vendor -> {
			Locale queryLocale = null;
			if (query == null || query.getLanguage() == null || query.getLanguage().isEmpty())
			{
				queryLocale = getI18nService().getCurrentLocale();
			}

			if (queryLocale == null && query != null)
			{
				queryLocale = getCommonI18NService().getLocaleForLanguage(getCommonI18NService().getLanguage(query.getLanguage()));
			}
			return vendor.getName(queryLocale);
		}).orElse(null);

	}

	protected I18NService getI18nService()
	{
		return i18nService;
	}

	@Required
	public void setI18nService(I18NService i18nService)
	{
		this.i18nService = i18nService;
	}

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	protected VendorService getVendorService()
	{
		return vendorService;
	}

	@Required
	public void setVendorService(VendorService vendorService)
	{
		this.vendorService = vendorService;
	}

}

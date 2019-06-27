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
package de.hybris.platform.marketplaceservices.vendor.impl;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.marketplaceservices.model.CustomerVendorReviewModel;
import de.hybris.platform.marketplaceservices.vendor.CustomerVendorReviewService;
import de.hybris.platform.marketplaceservices.vendor.daos.CustomerVendorReviewDao;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation for {@link CustomerVendorReviewService}.
 */
public class DefaultCustomerVendorReviewService implements CustomerVendorReviewService
{
	private CustomerVendorReviewDao customerVendorReviewDao;
	private ModelService modelService;

	@Override
	public Collection<CustomerVendorReviewModel> getReviewsForVendor(final VendorModel vendor)
	{
		return getCustomerVendorReviewDao().findReviewsForVendor(vendor);
	}

	@Override
	public CustomerVendorReviewModel createReview(final CustomerVendorReviewModel vendorReview)
	{
		getModelService().save(vendorReview);
		return vendorReview;
	}

	@Override
	public boolean postedReview(final String consignmentCode, final UserModel user)
	{
		return getCustomerVendorReviewDao().postedReview(consignmentCode, user);
	}

	@Override
	public SearchPageData<CustomerVendorReviewModel> getPagedReviewsForVendor(final String vendorCode,
			final LanguageModel language, final PageableData pageableData)
	{
		return getCustomerVendorReviewDao().findPagedReviewsForVendor(vendorCode, language, pageableData);
	}

	protected CustomerVendorReviewDao getCustomerVendorReviewDao()
	{
		return customerVendorReviewDao;
	}

	@Required
	public void setCustomerVendorReviewDao(final CustomerVendorReviewDao customerVendorReviewDao)
	{
		this.customerVendorReviewDao = customerVendorReviewDao;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

}

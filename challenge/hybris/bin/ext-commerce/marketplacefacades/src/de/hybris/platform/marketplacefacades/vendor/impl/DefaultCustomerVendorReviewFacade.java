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

import de.hybris.platform.commercefacades.product.data.VendorReviewData;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.consignmenttrackingservices.service.ConsignmentTrackingService;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.marketplacefacades.vendor.CustomerVendorReviewFacade;
import de.hybris.platform.marketplaceservices.model.CustomerVendorReviewModel;
import de.hybris.platform.marketplaceservices.vendor.CustomerVendorReviewService;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link CustomerVendorReviewFacade}.
 */
public class DefaultCustomerVendorReviewFacade implements CustomerVendorReviewFacade
{
	private CustomerVendorReviewService customerVendorReviewService;
	private UserService userService;
	private ConsignmentTrackingService consignmentTrackingService;
	private Converter<CustomerVendorReviewModel, VendorReviewData> customerVendorReviewConverter;
	private Converter<VendorReviewData, CustomerVendorReviewModel> customerVendorReviewReverseConverter;
	private CommerceCommonI18NService commerceCommonI18NService;

	@Override
	public void postReview(final String orderCode, final String consignmentCode, final VendorReviewData reviewData)
	{
		final UserModel user = getUserService().getCurrentUser();
		final Optional<ConsignmentModel> consignmentOptional = getConsignmentTrackingService().getConsignmentForCode(orderCode,
				consignmentCode);

		if (consignmentOptional.isPresent())
		{
			final CustomerVendorReviewModel vendorReview = new CustomerVendorReviewModel();
			getCustomerVendorReviewReverseConverter().convert(reviewData, vendorReview);
			vendorReview.setUser(user);
			vendorReview.setConsignment(consignmentOptional.get());
			vendorReview.setCreateDate(new Date());
			vendorReview.setVendor(consignmentOptional.get().getWarehouse().getVendor());
			vendorReview.setLanguage(getCommerceCommonI18NService().getCurrentLanguage());
			customerVendorReviewService.createReview(vendorReview);
		}
	}

	@Override
	public boolean postedReview(final String consignmentCode)
	{
		return getCustomerVendorReviewService().postedReview(consignmentCode, getUserService().getCurrentUser());
	}

	@Override
	public SearchPageData<VendorReviewData> getPagedReviewsForVendor(final String vendorCode, final PageableData pageableData)
	{
		return convertPageData(getCustomerVendorReviewService().getPagedReviewsForVendor(vendorCode,
				getCommerceCommonI18NService().getCurrentLanguage(), pageableData));
	}

	protected SearchPageData<VendorReviewData> convertPageData(final SearchPageData<CustomerVendorReviewModel> source)
	{
		final SearchPageData<VendorReviewData> result = new SearchPageData<>();
		result.setPagination(source.getPagination());
		result.setSorts(source.getSorts());
		result.setResults(Converters.convertAll(source.getResults(), getCustomerVendorReviewConverter()));

		return result;
	}

	protected CustomerVendorReviewService getCustomerVendorReviewService()
	{
		return customerVendorReviewService;
	}

	@Required
	public void setCustomerVendorReviewService(final CustomerVendorReviewService customerVendorReviewService)
	{
		this.customerVendorReviewService = customerVendorReviewService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected ConsignmentTrackingService getConsignmentTrackingService()
	{
		return consignmentTrackingService;
	}

	@Required
	public void setConsignmentTrackingService(final ConsignmentTrackingService consignmentTrackingService)
	{
		this.consignmentTrackingService = consignmentTrackingService;
	}

	protected Converter<CustomerVendorReviewModel, VendorReviewData> getCustomerVendorReviewConverter()
	{
		return customerVendorReviewConverter;
	}

	@Required
	public void setCustomerVendorReviewConverter(
			final Converter<CustomerVendorReviewModel, VendorReviewData> customerVendorReviewConverter)
	{
		this.customerVendorReviewConverter = customerVendorReviewConverter;
	}

	protected CommerceCommonI18NService getCommerceCommonI18NService()
	{
		return commerceCommonI18NService;
	}

	@Required
	public void setCommerceCommonI18NService(final CommerceCommonI18NService commerceCommonI18NService)
	{
		this.commerceCommonI18NService = commerceCommonI18NService;
	}

	protected Converter<VendorReviewData, CustomerVendorReviewModel> getCustomerVendorReviewReverseConverter()
	{
		return customerVendorReviewReverseConverter;
	}

	@Required
	public void setCustomerVendorReviewReverseConverter(
			final Converter<VendorReviewData, CustomerVendorReviewModel> customerVendorReviewReverseConverter)
	{
		this.customerVendorReviewReverseConverter = customerVendorReviewReverseConverter;
	}

}

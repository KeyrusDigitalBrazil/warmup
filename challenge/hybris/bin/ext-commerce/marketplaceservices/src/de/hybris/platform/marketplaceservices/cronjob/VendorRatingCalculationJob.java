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
package de.hybris.platform.marketplaceservices.cronjob;

import java.util.Collection;
import java.util.function.ToDoubleFunction;

import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.marketplaceservices.model.CustomerVendorReviewModel;
import de.hybris.platform.marketplaceservices.vendor.CustomerVendorReviewService;
import de.hybris.platform.marketplaceservices.vendor.VendorService;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;


/**
 * Calculate the vendor ratings
 */
public class VendorRatingCalculationJob extends AbstractJobPerformable<CronJobModel>
{

	private VendorService vendorService;
	private CustomerVendorReviewService customerVendorReviewService;

	@Override
	public PerformResult perform(final CronJobModel job)
	{
		getVendorService().getActiveVendors().forEach(vendor -> {
			final Collection<CustomerVendorReviewModel> reviews = getCustomerVendorReviewService().getReviewsForVendor(vendor);
			vendor.setSatisfactionRating(calculateRating(reviews, CustomerVendorReviewModel::getSatisfaction));
			vendor.setDeliveryRating(calculateRating(reviews, CustomerVendorReviewModel::getDelivery));
			vendor.setCommunicationRating(calculateRating(reviews, CustomerVendorReviewModel::getCommunication));
			vendor.setAverageRating(calculateAverage(vendor));
			vendor.setReviewCount(Long.valueOf(reviews.size()));
		});
		modelService.saveAll();
		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
	}

	protected double calculateRating(final Collection<CustomerVendorReviewModel> reviews,
			final ToDoubleFunction<CustomerVendorReviewModel> mapper)
	{
		return reviews.stream().mapToDouble(mapper).average().orElse(0);
	}

	protected double calculateAverage(final VendorModel vendor)
	{
		return (vendor.getSatisfactionRating() + vendor.getDeliveryRating() + vendor.getCommunicationRating()) / 3;
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

	protected CustomerVendorReviewService getCustomerVendorReviewService()
	{
		return customerVendorReviewService;
	}

	@Required
	public void setCustomerVendorReviewService(final CustomerVendorReviewService customerVendorReviewService)
	{
		this.customerVendorReviewService = customerVendorReviewService;
	}

}

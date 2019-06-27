/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.sap.hybris.saprevenuecloudcustomer.jobs;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.io.IOException;
import java.util.List;

import com.sap.hybris.saprevenuecloudcustomer.dto.Customer;
import com.sap.hybris.saprevenuecloudcustomer.service.SapRevenueCloudCustomerOutboundService;


/**
 * Cronjob to trigger the customer update iflow which will fetch the customer data from Revenue Cloud update in Hybris
 * Commerce
 */
public class SAPRevenueCloudCustomerImportJob extends AbstractJobPerformable<CronJobModel>
{
	private FlexibleSearchService flexibleSearchService;
	private SapRevenueCloudCustomerOutboundService sapRevenueCloudCustomerOutboundService;
	private static final String CUSTOMER_QUERY = "select {c.pk} from {Customer as c} where {c.revenueCloudCustomerId} is null";


	@Override
	public PerformResult perform(final CronJobModel job)
	{
		final Customer customer = new Customer();
		final SearchResult<CustomerModel> result = getFlexibleSearchService().search(new FlexibleSearchQuery(CUSTOMER_QUERY));
		if (result != null)
		{
			final List<CustomerModel> customers = result.getResult();
			if (customers != null && !customers.isEmpty())
			{
				for (final CustomerModel customerModel : customers)
				{
					customer.setCustomerId(customerModel.getCustomerID());
					try
					{
						getSapRevenueCloudCustomerOutboundService().publishCustomerUpdate(customer);
					}
					catch (final IOException e)
					{
						return new PerformResult(CronJobResult.ERROR, CronJobStatus.FINISHED);

					}
				}
			}
		}
		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
	}

	/**
	 * @return the flexibleSearchService
	 */
	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	/**
	 * @param flexibleSearchService
	 *           the flexibleSearchService to set
	 */
	@Override
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	/**
	 * @return the sapRevenueCloudCustomerOutboundService
	 */
	public SapRevenueCloudCustomerOutboundService getSapRevenueCloudCustomerOutboundService()
	{
		return sapRevenueCloudCustomerOutboundService;
	}

	/**
	 * @param sapRevenueCloudCustomerOutboundService
	 *           the sapRevenueCloudCustomerOutboundService to set
	 */
	public void setSapRevenueCloudCustomerOutboundService(
			final SapRevenueCloudCustomerOutboundService sapRevenueCloudCustomerOutboundService)
	{
		this.sapRevenueCloudCustomerOutboundService = sapRevenueCloudCustomerOutboundService;
	}



}

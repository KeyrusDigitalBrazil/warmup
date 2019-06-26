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
package de.hybris.platform.sap.saprevenuecloudorder.service.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.Bills;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.CancelSubscription;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.ExtendSubscription;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.Subscription;
import de.hybris.platform.sap.saprevenuecloudorder.service.SapRevenueCloudSubscriptionConfigurationService;
import de.hybris.platform.sap.saprevenuecloudorder.service.SapRevenueCloudSubscriptionService;
import de.hybris.platform.subscriptionservices.model.BillingFrequencyModel;
import de.hybris.platform.subscriptionservices.model.BillingPlanModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionTermModel;

/**
 * Service to fetch SUbscription Data from Revenue Cloud.
 *
 */
public class DefaultSapRevenueCloudSubscriptionService implements SapRevenueCloudSubscriptionService
{

	private static final Logger LOG = Logger.getLogger(DefaultSapRevenueCloudSubscriptionService.class);

    private SapRevenueCloudSubscriptionConfigurationService sapRevenueCloudSubscriptionConfigurationService;
    
    private static final String ERROR = "Error while calling the Revenue Cloud subscription service!";

    @Override
    public List<Subscription> getSubscriptionsByClientId(final String clientId) {

        List<Subscription> subscriptionsByClientId = Arrays.asList();

        try {
            subscriptionsByClientId = getSapSubscriptionConfigurationService().getSapSubscriptionClient().getSubscriptionsByClientId(clientId);
        } catch (final Exception ex) {
            LOG.error(ERROR + ex);
        }

        return subscriptionsByClientId;
    }

    @Override
    public Subscription getSubscriptionById(final String subscriptionId) {

        Subscription subscriptionById = null;

        try {
            subscriptionById = getSapSubscriptionConfigurationService().getSapSubscriptionClient().getSubscriptionById(subscriptionId);
        } catch (final Exception ex) {
            LOG.error(ERROR + ex);
        }

        return subscriptionById;
    }

    @Override
	public String cancelSubscription(final String code, final CancelSubscription subscription)
	{
   	 final String status = "Failure";
		
        try {
      	   getSapSubscriptionConfigurationService().getSapSubscriptionClient().cancelSubscription(code,subscription);
        } catch (final Exception ex) {
            LOG.error(ERROR + ex);
        }
        return status;
    }


	@Override
	public String extendSubscription(final String code, final ExtendSubscription subscription)
	{
		final String status = "Failure";
		try
		{
			getSapSubscriptionConfigurationService().getSapSubscriptionClient().extendSubscription(code, subscription);
		}
		catch (final Exception ex)
		{
			LOG.error(ERROR + ex);
		}
		return status;
 	}
	
	@Override
	public BillingFrequencyModel getBillingFrequency(final ProductModel productModel)
	{
		BillingFrequencyModel billingFrequency = null;
		final SubscriptionTermModel subscriptionTerm = productModel.getSubscriptionTerm();
		if (subscriptionTerm != null)
		{
			final BillingPlanModel billingPlan = subscriptionTerm.getBillingPlan();
			if (billingPlan != null && billingPlan.getBillingFrequency() != null)
			{
				billingFrequency = billingPlan.getBillingFrequency();
			}
		}
		return billingFrequency;

	}
	
	@Override
	public String computeCancelaltionDate(final String subscriptionsId, String reqCancellationDate) 
	{
		String subscriptionById = "";
		try 
		{
			Subscription subscription= getSapSubscriptionConfigurationService().getSapSubscriptionClient()
					.getCancellationDate(subscriptionsId, reqCancellationDate);
			subscriptionById = subscription.getEffectiveExpirationDate();
		} 
		catch (final Exception ex) 
		{
			LOG.error(ERROR + ex);
		}
		return subscriptionById;
	}
	
	
	@Override
	public List<Bills> getBillsBySubscriptionsId(final String customerId, final String fromDate, final String todate) 
	{
		try
		{
			return getSapSubscriptionConfigurationService().getSapSubscriptionClient().getSubscriptionBills(customerId,
					fromDate != null ? fromDate : "", todate != null ? todate : "");
		} catch (final Exception ex) {
			LOG.error(ERROR + ex);
		}
		return Collections.emptyList();
	}
	
	@Override
	public List<Bills> getSubscriptionCurrentUsage(final String subscriptionId, final String currentDate) 
	{
		try
		{
			return getSapSubscriptionConfigurationService().getSapSubscriptionClient()
					.getSubscriptionCurrentUsage(subscriptionId, currentDate);
		} 
		catch (final Exception ex)
		{
			LOG.error(ERROR + ex);
		}
		return Collections.emptyList();
	}
	
	@Override
	public Bills getSubscriptionBillsById(final String billId)
	{
		Bills bills = null ;
		try 
		{
			bills = getSapSubscriptionConfigurationService().getSapSubscriptionClient().getSubscriptionBillById(billId);
		}
		catch (final Exception ex) 
		{
			LOG.error(ERROR + ex);
		}
		return bills;
	}


    protected SapRevenueCloudSubscriptionConfigurationService getSapSubscriptionConfigurationService() {
        return sapRevenueCloudSubscriptionConfigurationService;
    }

    @Required
    public void setSapSubscriptionConfigurationService(final SapRevenueCloudSubscriptionConfigurationService sapRevenueCloudSubscriptionConfigurationService) {
        this.sapRevenueCloudSubscriptionConfigurationService = sapRevenueCloudSubscriptionConfigurationService;
    }

}

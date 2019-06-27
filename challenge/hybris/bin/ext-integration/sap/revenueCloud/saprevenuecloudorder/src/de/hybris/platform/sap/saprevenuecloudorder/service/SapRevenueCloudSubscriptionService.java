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
package de.hybris.platform.sap.saprevenuecloudorder.service;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.Bills;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.CancelSubscription;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.ExtendSubscription;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.Subscription;
import de.hybris.platform.subscriptionservices.model.BillingFrequencyModel;
import java.util.List;


/**
 * Service API that provides methods for SAP RevenueCloud Subscription Orders
 */
public interface SapRevenueCloudSubscriptionService {

	/**
	 * fetch subscription details using customerId
	 *
	 * @param clientId
	 *           customerId
	 *
	 * @return {@link List} list of subscriptions
	 *
	 */
	List<Subscription> getSubscriptionsByClientId(String clientId);

	/**
	 * fetch subscription details using subscriptionId
	 *
	 * @param subscriptionsId
	 *           subscription code
	 *
	 * @return {@link Subscription } Subscription details
	 */
	Subscription getSubscriptionById(String subscriptionsId);

	/**
	 * cancel a subscription based on subscription code
	 * 
	 * @param code
	 * 			subscription code
	 * @param subscription
	 * 			subscription data required for cancellation
	 *
	 * @return {@link String} cancellation status
	 */
	String cancelSubscription(String code, CancelSubscription subscription);

	/**
	 * extend a subscription based on subscription code
	 *
	 *@param subscriptionCode
	 *				subscriptionId
	 *@param subscription
	 *			subscription data required for extending subscription
	 * @return extension status
	 */
	String extendSubscription(String subscriptionCode, ExtendSubscription subscription);

	/**
	 * get billingFrequency for specific product
	 *
	 * @param productModel
	 *           - product model 
	 *
	 * @return {@link BillingFrequencyModel}
	 */
	BillingFrequencyModel getBillingFrequency(final ProductModel productModel);
	
	/**
	 * get effective end date for subscription
	 *
	 * @param subscriptionsId
	 *           - subscription code
	 * @param reqCancellationDate
	 * 			 - requested cancellation date
	 *
	 * @return effective end date for subscription
	 */
	String computeCancelaltionDate(String subscriptionsId, String reqCancellationDate);
	
	/**
	 * fetch bills using subscriptionId and dates
	 * 
	 * @param subscriptionsId 
	 * 				subscriptionId
	 * @param fromDate 
	 * 				the date from which bills should be displayed
	 * @param todate
	 * 				the date till which bills should be displayed
	 * @return {@link List<Bills>}
	 * 			list of bills based on the filter applied
	 */
	List<Bills> getBillsBySubscriptionsId(final String subscriptionsId,final String fromDate,final String todate);
	/**
	 * fetch bill using billId
	 * 
	 * @param billId 
	 * 			subscription billId
	 * @return
	 * 		{@link Bills} bill for the given id
	 */
	Bills getSubscriptionBillsById(String billId);

	/**
	 * fetch usage of subscription in current billing cycle
	 * 
	 * @param subscriptionId
	 * 				- id of subscription for which current usage is needed
	 * @param currentDate
	 * 				- current date to fetch usage till current date in present billing cycle
	 * @return {@link List<Bills>}
	 * 				returns bills which contains current usage for given subscription
	 */
	List<Bills> getSubscriptionCurrentUsage(String subscriptionId, String currentDate); 
}

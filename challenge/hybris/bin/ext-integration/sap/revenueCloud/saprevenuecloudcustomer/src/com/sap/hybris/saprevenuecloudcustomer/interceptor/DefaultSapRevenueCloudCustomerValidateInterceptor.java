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
package com.sap.hybris.saprevenuecloudcustomer.interceptor;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sap.hybris.saprevenuecloudcustomer.service.SapRevenueCloudCustomerOutboundService;
import com.sap.hybris.saprevenuecloudproduct.model.SAPRevenueCloudConfigurationModel;


/**
 * Updates already existing customer in Revenue Cloud.
 */
public class DefaultSapRevenueCloudCustomerValidateInterceptor implements ValidateInterceptor<CustomerModel>
{
	private GenericDao sapRevenueCloudConfigurationModelGenericDao;
	private SapRevenueCloudCustomerOutboundService sapRevenueCloudCustomerOutboundService;
	private static final Logger LOGGER = LogManager.getLogger(DefaultSapRevenueCloudCustomerValidateInterceptor.class);


	@Override
	public void onValidate(final CustomerModel customerModel, final InterceptorContext ctx) throws InterceptorException
	{
		final SAPRevenueCloudConfigurationModel revenueCloudConfig = getRevenueCloudConfiguration();
		if (revenueCloudConfig == null || !revenueCloudConfig.isReplicateCustomer())
		{
			return;
		}
		if (shouldReplicate(customerModel, ctx))
		{
			getSapRevenueCloudCustomerOutboundService()
					.sendCustomerData(customerModel, "", "", customerModel.getDefaultShipmentAddress()).subscribe();
		}
	}

	protected boolean shouldReplicate(final CustomerModel customerModel, final InterceptorContext ctx)
	{
		if ((customerModel.getRevenueCloudCustomerId() != null) && (customerModel.getClass() == CustomerModel.class))
		{
			return getChangeAttributesList().stream().anyMatch(attribute -> ctx.isModified(customerModel, attribute));
		}
		return false;
	}

	protected List<String> getChangeAttributesList()
	{
		final List<String> attributeList = new ArrayList<String>();
		attributeList.add(CustomerModel.NAME);
		attributeList.add(CustomerModel.UID);
		attributeList.add(CustomerModel.DEFAULTSHIPMENTADDRESS);
		return attributeList;
	}

	protected SAPRevenueCloudConfigurationModel getRevenueCloudConfiguration()
	{
		final Optional<SAPRevenueCloudConfigurationModel> revenueCloudConfigOpt = getSapRevenueCloudConfigurationModelGenericDao()
				.find().stream().findFirst();
		if (revenueCloudConfigOpt.isPresent())
		{
			return revenueCloudConfigOpt.get();
		}
		else
		{
			LOGGER.error("No Revenue Cloud Configuration found.");
			return null;
		}
	}

	/**
	 * @return the sapRevenueCloudConfigurationModelGenericDao
	 */
	public GenericDao getSapRevenueCloudConfigurationModelGenericDao()
	{
		return sapRevenueCloudConfigurationModelGenericDao;
	}

	/**
	 * @param sapRevenueCloudConfigurationModelGenericDao
	 *           the sapRevenueCloudConfigurationModelGenericDao to set
	 */
	public void setSapRevenueCloudConfigurationModelGenericDao(final GenericDao sapRevenueCloudConfigurationModelGenericDao)
	{
		this.sapRevenueCloudConfigurationModelGenericDao = sapRevenueCloudConfigurationModelGenericDao;
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

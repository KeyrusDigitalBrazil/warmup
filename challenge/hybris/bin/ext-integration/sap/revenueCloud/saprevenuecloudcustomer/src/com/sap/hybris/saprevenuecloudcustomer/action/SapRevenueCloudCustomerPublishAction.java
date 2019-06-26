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
package com.sap.hybris.saprevenuecloudcustomer.action;

import static com.google.common.base.Preconditions.checkArgument;

import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.task.RetryLaterException;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;

import com.sap.hybris.saprevenuecloudcustomer.service.SapRevenueCloudCustomerOutboundService;
import com.sap.hybris.saprevenuecloudproduct.model.SAPMarketToCatalogMappingModel;


/**
 * Publish Customer to Revenue Cloud via CPI.
 */
public class SapRevenueCloudCustomerPublishAction extends AbstractSimpleDecisionAction<BusinessProcessModel>
{
	private SapRevenueCloudCustomerOutboundService sapRevenueCloudCustomerOutboundService;
	private Populator customerPopulator;
	private ConfigurationService configurationService;
	private GenericDao<SAPMarketToCatalogMappingModel> sapMarketToCatalogMappingModelGenericDao;
	private static final Logger LOGGER = LogManager.getLogger(SapRevenueCloudCustomerPublishAction.class);
	private static final String SUCCESS = "success";
	private static final String RESPONSE_STATUS = "responseStatus";

	@Override
	public Transition executeAction(final BusinessProcessModel process) throws RetryLaterException
	{
		final StoreFrontCustomerProcessModel customerProcess = (StoreFrontCustomerProcessModel) process;
		final CustomerModel customerModel = customerProcess.getCustomer();
		getSapRevenueCloudCustomerOutboundService().sendCustomerData(customerModel, customerProcess.getStore().getUid(), "", null)
				.subscribe(

						responseEntityMap -> {

							if (isSentSuccessfully(responseEntityMap))
							{
								LOGGER.info("Customer with id " + customerModel.getUid() + " published successfully to CPI.");
								final Map<String, Object> response = responseEntityMap.getBody();
								customerModel.setRevenueCloudCustomerId(getPropertyValue(responseEntityMap, "revenueCloudCustomerId"));
								customerModel.setSapIsReplicated(Boolean.TRUE);
								getModelService().save(customerModel);
							}
							else
							{
								LOGGER.info("Failed to publish customer with id " + customerModel.getUid() + " to CPI.");
							}

						}

						, error -> {
							LOGGER.error(error);
						});
		;
		return Transition.OK;

	}

	protected String getPropertyValue(final ResponseEntity<Map> responseEntityMap, final String property)
	{
		final Object next = responseEntityMap.getBody().keySet().iterator().next();
		checkArgument(next != null, String.format("SCPI response entity key set cannot be null."));

		final String responseKey = next.toString();
		checkArgument(responseKey != null && !responseKey.isEmpty(),
				String.format("SCPI response entity can neither be null nor empty."));

		final Object propertyValue = ((HashMap) responseEntityMap.getBody().get(responseKey)).get(property);

		return propertyValue.toString();

	}

	protected boolean isSentSuccessfully(final ResponseEntity<Map> responseEntityMap)
	{
		if (SUCCESS.equalsIgnoreCase(getPropertyValue(responseEntityMap, RESPONSE_STATUS))
				&& responseEntityMap.getStatusCode().is2xxSuccessful())
		{
			return true;
		}
		else
		{
			return false;
		}

	}

	/**
	 * @return the customerPopulator
	 */
	public Populator getCustomerPopulator()
	{
		return customerPopulator;
	}

	/**
	 * @param customerPopulator
	 *           the customerPopulator to set
	 */
	public void setCustomerPopulator(final Populator customerPopulator)
	{
		this.customerPopulator = customerPopulator;
	}

	/**
	 * @return the configurationService
	 */
	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @param configurationService
	 *           the configurationService to set
	 */
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	/**
	 * @return the sapMarketToCatalogMappingModelGenericDao
	 */
	public GenericDao<SAPMarketToCatalogMappingModel> getSapMarketToCatalogMappingModelGenericDao()
	{
		return sapMarketToCatalogMappingModelGenericDao;
	}

	/**
	 * @param sapMarketToCatalogMappingModelGenericDao
	 *           the sapMarketToCatalogMappingModelGenericDao to set
	 */
	public void setSapMarketToCatalogMappingModelGenericDao(
			final GenericDao<SAPMarketToCatalogMappingModel> sapMarketToCatalogMappingModelGenericDao)
	{
		this.sapMarketToCatalogMappingModelGenericDao = sapMarketToCatalogMappingModelGenericDao;
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

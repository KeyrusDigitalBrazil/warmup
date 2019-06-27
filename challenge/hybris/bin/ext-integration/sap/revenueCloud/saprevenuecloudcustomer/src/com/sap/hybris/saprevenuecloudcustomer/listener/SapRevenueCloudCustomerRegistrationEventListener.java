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
package com.sap.hybris.saprevenuecloudcustomer.listener;

import de.hybris.platform.commerceservices.event.RegisterEvent;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sap.hybris.saprevenuecloudproduct.model.SAPRevenueCloudConfigurationModel;


/**
 *
 */
public class SapRevenueCloudCustomerRegistrationEventListener extends AbstractEventListener<RegisterEvent>
{
	private ModelService modelService;

	private BaseStoreService baseStoreService;

	private BusinessProcessService businessProcessService;

	private GenericDao sapRevenueCloudConfigurationModelGenericDao;

	private static final String REVENUE_CLOUD_CUSTOMER_PROCESS = "revenuecloud-customer-process";

	private static final Logger LOGGER = LogManager.getLogger(SapRevenueCloudCustomerRegistrationEventListener.class);


	@Override
	protected void onEvent(final RegisterEvent registerEvent)
	{
		final SAPRevenueCloudConfigurationModel revenueCloudConfig = getRevenueCloudConfiguration();
		if (revenueCloudConfig == null || !revenueCloudConfig.isReplicateCustomer())
		{
			return;
		}
		final StoreFrontCustomerProcessModel storeFrontCustomerProcessModel = createProcess();
		storeFrontCustomerProcessModel.setSite(registerEvent.getSite());
		storeFrontCustomerProcessModel.setCustomer(registerEvent.getCustomer());

		final BaseStoreModel currentBaseStore = getBaseStoreService().getCurrentBaseStore();
		if (currentBaseStore != null)
		{
			storeFrontCustomerProcessModel.setStore(currentBaseStore);
		}

		getModelService().save(storeFrontCustomerProcessModel);
		getBusinessProcessService().startProcess(storeFrontCustomerProcessModel);

	}

	protected StoreFrontCustomerProcessModel createProcess()
	{
		return (StoreFrontCustomerProcessModel) getBusinessProcessService()
				.createProcess(REVENUE_CLOUD_CUSTOMER_PROCESS + System.currentTimeMillis(), REVENUE_CLOUD_CUSTOMER_PROCESS);
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
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return the baseStoreService
	 */
	public BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * @param baseStoreService
	 *           the baseStoreService to set
	 */
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	/**
	 * @return the businessProcessService
	 */
	public BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	/**
	 * @param businessProcessService
	 *           the businessProcessService to set
	 */
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
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

}

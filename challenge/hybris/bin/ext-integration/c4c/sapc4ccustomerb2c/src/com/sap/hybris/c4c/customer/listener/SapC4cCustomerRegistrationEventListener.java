/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.sap.hybris.c4c.customer.listener;

import de.hybris.platform.commerceservices.event.RegisterEvent;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;


/**
 * Catch the register event and start the <code>sapC4cCustomerRegisterProcess</code> business process
 */
public class SapC4cCustomerRegistrationEventListener extends AbstractEventListener<RegisterEvent>
{

	private ModelService modelService;

	private BaseStoreService baseStoreService;

	private BusinessProcessService businessProcessService;

	/**
	 * start the <code>sapC4cCustomerRegisterProcess</code> business process
	 *
	 */
	@Override
	protected void onEvent(final RegisterEvent registerEvent)
	{
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

	/**
	 * Creates business process
	 */
	protected StoreFrontCustomerProcessModel createProcess()
	{
		return (StoreFrontCustomerProcessModel) getBusinessProcessService()
				.createProcess("c4c-customer-process" + System.currentTimeMillis(), "c4c-customer-process");
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

}

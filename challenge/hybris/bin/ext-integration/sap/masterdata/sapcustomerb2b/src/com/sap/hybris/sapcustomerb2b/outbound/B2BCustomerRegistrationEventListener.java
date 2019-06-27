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
package com.sap.hybris.sapcustomerb2b.outbound;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commercefacades.storesession.impl.DefaultStoreSessionFacade;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.keygenerator.impl.PersistentKeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.log4j.Logger;


/**
 * Catch the register event and start the <code>sapCustomerPublishProcess</code> business process
 */
public class B2BCustomerRegistrationEventListener extends AbstractEventListener<B2BRegistrationEvent>
{
	private static final Logger LOGGER = Logger
			.getLogger(com.sap.hybris.sapcustomerb2b.outbound.B2BCustomerRegistrationEventListener.class.getName());

	private ModelService modelService;
	private B2BCustomerExportService b2bCustomerExportService;
	private DefaultStoreSessionFacade storeSessionFacade;
	private PersistentKeyGenerator sapContactIdGenerator;

	/**
	 * @return businessProcessService
	 */
	public BusinessProcessService getBusinessProcessService()
	{
		return (BusinessProcessService) Registry.getApplicationContext().getBean("businessProcessService");
	}

	/**
	 * @return modelService
	 */
	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * start the <code>sapCustomerPublishProcess</code> business process
	 *
	 */
	@Override
	protected void onEvent(final B2BRegistrationEvent registerEvent)
	{
		if (!getB2bCustomerExportService().isB2BCustomerReplicationEnabled())
		{
			if (LOGGER.isDebugEnabled())
			{
				LOGGER.debug(
						"B2B customer was registered but not sent to Data Hub. 'Replicate B2B Customers' flag in 'SAP Base Store Configuration' is set to 'false'.");
			}
			return;
		}

		/*
		 * SAPB2BModelService.save() publishes B2BRegistrationEvent on every save from the user management page, i.e. even
		 * when saving changes to an existing B2B customer. To distinguish a new B2B customer from an existing one, we
		 * examine the customerID. If it is empty, this is a new B2B customer, and we generate a customerID from
		 * sapContactIdGenerator. Finally, we replicate the B2B customer to Data Hub.
		 */
		final B2BCustomerModel b2bCustomer = (B2BCustomerModel) registerEvent.getCustomer();
		if (b2bCustomer.getCustomerID() == null || b2bCustomer.getCustomerID().isEmpty())
		{
			final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			b2bCustomer.setSapReplicationInfo("Sent to Data Hub at " + dateFormat.format(Calendar.getInstance().getTime()));
			b2bCustomer.setCustomerID((String) getSapContactIdGenerator().generate());
			b2bCustomer.setLoginDisabled(true);
			modelService.save(b2bCustomer);
			final String sessionLanguage = getStoreSessionFacade().getCurrentLanguage() != null
					? getStoreSessionFacade().getCurrentLanguage().getIsocode() : "en";
			getB2bCustomerExportService().prepareAndSend(b2bCustomer, sessionLanguage);
		}
	}

	/**
	 * Create BusinessProcessService
	 *
	 * @return StoreFrontCustomerProcessModel
	 */
	protected StoreFrontCustomerProcessModel createProcess()
	{
		return (StoreFrontCustomerProcessModel) getBusinessProcessService()
				.createProcess("customerPublishProcess" + System.currentTimeMillis(), "customerPublishProcess");
	}

	public DefaultStoreSessionFacade getStoreSessionFacade()
	{
		return storeSessionFacade;
	}

	public void setStoreSessionFacade(final DefaultStoreSessionFacade storeSessionFacade)
	{
		this.storeSessionFacade = storeSessionFacade;
	}

	public PersistentKeyGenerator getSapContactIdGenerator()
	{
		return sapContactIdGenerator;
	}

	public void setSapContactIdGenerator(final PersistentKeyGenerator sapContactIdGenerator)
	{
		this.sapContactIdGenerator = sapContactIdGenerator;
	}

	public B2BCustomerExportService getB2bCustomerExportService()
	{
		return b2bCustomerExportService;
	}

	public void setB2bCustomerExportService(final B2BCustomerExportService b2bCustomerExportService)
	{
		this.b2bCustomerExportService = b2bCustomerExportService;
	}
}
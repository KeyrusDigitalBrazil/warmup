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
package com.sap.hybris.sapcustomerb2c.outbound;

import de.hybris.platform.commercefacades.storesession.impl.DefaultStoreSessionFacade;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.store.services.BaseStoreService;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


/**
 * If default shipment address was updated send default shipment address to Data Hub in case of user replication is
 * active and the address is related to a sap consumer. This is indicated by the filled sap contact id.
 *
 */
public class DefaultAddressInterceptor implements ValidateInterceptor<AddressModel>
{

	private static final Logger LOGGER = Logger
			.getLogger(com.sap.hybris.sapcustomerb2c.outbound.DefaultAddressInterceptor.class.getName());
	private DefaultStoreSessionFacade storeSessionFacade;
	private CustomerExportService customerExportService;
	private BaseStoreService baseStoreService;

	@Override
	public void onValidate(final AddressModel addressModel, final InterceptorContext ctx) throws InterceptorException
	{
		if (!getCustomerExportService().isCustomerReplicationEnabled())
		{
			if (LOGGER.isDebugEnabled())
			{
				LOGGER.debug("'Replicate Registered Users' flag in 'SAP Global Configuration' is set to 'false'.");
				LOGGER.debug("Address " + addressModel.getPk() + " was not sent to Data Hub.");
			}
			return;
		}

		// we only replicate the address of a CustomerModel
		if (!getCustomerExportService().isClassCustomerModel(addressModel.getOwner()))
		{
			return;
		}

		final CustomerModel customerModel = ((CustomerModel) addressModel.getOwner());

		// interceptor only used to replicate changes to existing customers
		if (customerModel.getSapContactID() == null)
		{
			return;
		}

		// we only replicate changes to the default shipment address
		if (!addressModel.equals(customerModel.getDefaultShipmentAddress()))
		{
			if (LOGGER.isDebugEnabled())
			{
				LOGGER.debug("Address " + addressModel.getPk() + " is not the default shipment address.");
			}
			return;
		}

		// check if one of the supported fields was modified
		if (isModelModified(addressModel, ctx))
		{
			updateAndSendCustomerData(addressModel, customerModel);
		}
		else if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("Address " + addressModel.getPk() + " was not sent to Data Hub.");
			LOGGER.debug("Address country modified =  " + ctx.isModified(addressModel, AddressModel.COUNTRY));
			LOGGER.debug("Address streetname modified = " + ctx.isModified(addressModel, AddressModel.STREETNAME));
			LOGGER.debug("Address phone1 modified = " + ctx.isModified(addressModel, AddressModel.PHONE1));
			LOGGER.debug("Address fax modified = " + ctx.isModified(addressModel, AddressModel.FAX));
			LOGGER.debug("Address town modified = " + ctx.isModified(addressModel, AddressModel.TOWN));
			LOGGER.debug("Address postalcode modified = " + ctx.isModified(addressModel, AddressModel.POSTALCODE));
			LOGGER.debug("Address streetnumber modified = " + ctx.isModified(addressModel, AddressModel.STREETNUMBER));
			LOGGER.debug("Address region modified = " + ctx.isModified(addressModel, AddressModel.REGION));
			LOGGER.debug("Customer sapContactId = " + customerModel.getSapContactID());
		}
	}

	private void updateAndSendCustomerData(AddressModel addressModel, CustomerModel customerModel) {
		final String baseStoreUid = baseStoreService.getCurrentBaseStore() != null
				? baseStoreService.getCurrentBaseStore().getUid()
				: null;
		final String sessionLanguage = getStoreSessionFacade().getCurrentLanguage() != null
				? getStoreSessionFacade().getCurrentLanguage().getIsocode()
				: null;
		getCustomerExportService().sendCustomerData(customerModel, baseStoreUid, sessionLanguage, addressModel);
	}

	private boolean isModelModified(AddressModel addressModel, InterceptorContext ctx) {

		List<String> modelAttributes = new ArrayList<>();

		modelAttributes.add(AddressModel.COUNTRY);
		modelAttributes.add(AddressModel.STREETNAME);
		modelAttributes.add(AddressModel.PHONE1);
		modelAttributes.add(AddressModel.FAX);
		modelAttributes.add(AddressModel.TOWN);
		modelAttributes.add(AddressModel.POSTALCODE);
		modelAttributes.add(AddressModel.STREETNUMBER);
		modelAttributes.add(AddressModel.REGION);

		return modelAttributes.stream().filter(modelAttribute -> ctx.isModified(addressModel,modelAttribute)).findAny().isPresent()? true : false;

	}

	/**
	 * @return storeSessionFacade
	 */
	public DefaultStoreSessionFacade getStoreSessionFacade()
	{
		return storeSessionFacade;
	}

	/**
	 * set storeSessionFacade
	 *
	 * @param storeSessionFacade
	 */
	public void setStoreSessionFacade(final DefaultStoreSessionFacade storeSessionFacade)
	{
		this.storeSessionFacade = storeSessionFacade;
	}

	/**
	 * @return customerExportService
	 */
	public CustomerExportService getCustomerExportService()
	{
		return customerExportService;
	}

	/**
	 * set customerExportService
	 *
	 * @param customerExportService
	 */
	public void setCustomerExportService(final CustomerExportService customerExportService)
	{
		this.customerExportService = customerExportService;
	}

	/**
	 * @return baseStoreService
	 */
	public BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * set baseStoreService
	 *
	 * @param baseStoreService
	 */
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}
}

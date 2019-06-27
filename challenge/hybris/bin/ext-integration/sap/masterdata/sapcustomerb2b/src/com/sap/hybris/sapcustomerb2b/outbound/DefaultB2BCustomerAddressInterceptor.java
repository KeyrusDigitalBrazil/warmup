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
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

import org.apache.log4j.Logger;


public class DefaultB2BCustomerAddressInterceptor implements ValidateInterceptor<AddressModel>
{
	private static final Logger LOGGER = Logger
			.getLogger(com.sap.hybris.sapcustomerb2b.outbound.DefaultB2BCustomerAddressInterceptor.class.getName());

	private B2BCustomerExportService b2bCustomerExportService;
	private DefaultStoreSessionFacade storeSessionFacade;

	@Override
	public void onValidate(final AddressModel addressModel, final InterceptorContext ctx) throws InterceptorException
	{
		if (!getB2bCustomerExportService().isB2BCustomerReplicationEnabled())
		{
			if (LOGGER.isDebugEnabled())
			{
				LOGGER.debug("'Replicate B2B Customers' flag in 'SAP Base Store Configuration' is set to 'false'.");
				LOGGER.debug("Address " + addressModel.getPk() + " was not sent to Data Hub.");
			}
			return;
		}

		final ItemModel owner = addressModel.getOwner();

		// we only replicate the address of a B2BCustomerModel
		if (!(owner instanceof B2BCustomerModel))
		{
			return;
		}

		final B2BCustomerModel b2bCustomerModel = (B2BCustomerModel) owner;
		if (b2bCustomerModel.getCustomerID() == null || b2bCustomerModel.getCustomerID().isEmpty())
		{
			return;
		}

		// we only replicate changes to the default shipment address
		if (!addressModel.equals(b2bCustomerModel.getDefaultShipmentAddress()))
		{
			if (LOGGER.isDebugEnabled())
			{
				LOGGER.debug("Address " + addressModel.getPk() + " is not the default shipment address.");
			}
			return;
		}

		// check if one of the supported fields was modified
		if (ctx.isModified(b2bCustomerModel.getDefaultShipmentAddress(), AddressModel.PHONE1))
		{
			final String sessionLanguage = getStoreSessionFacade().getCurrentLanguage() != null
					? getStoreSessionFacade().getCurrentLanguage().getIsocode()
					: "en";
			getB2bCustomerExportService().prepareAndSend(b2bCustomerModel, sessionLanguage);
		}
	}

	public DefaultStoreSessionFacade getStoreSessionFacade()
	{
		return storeSessionFacade;
	}

	public void setStoreSessionFacade(final DefaultStoreSessionFacade storeSessionFacade)
	{
		this.storeSessionFacade = storeSessionFacade;
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

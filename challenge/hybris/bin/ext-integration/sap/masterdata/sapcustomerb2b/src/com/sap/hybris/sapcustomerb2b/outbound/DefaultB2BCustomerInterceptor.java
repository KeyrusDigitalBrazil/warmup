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
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

import org.apache.log4j.Logger;


public class DefaultB2BCustomerInterceptor implements ValidateInterceptor<B2BCustomerModel>
{
	private static final Logger LOGGER = Logger
			.getLogger(com.sap.hybris.sapcustomerb2b.outbound.DefaultB2BCustomerInterceptor.class.getName());
	private B2BCustomerExportService b2bCustomerExportService;
	private DefaultStoreSessionFacade storeSessionFacade;

	@Override
	public void onValidate(final B2BCustomerModel customerModel, final InterceptorContext ctx) throws InterceptorException
	{
		if (!getB2bCustomerExportService().isB2BCustomerReplicationEnabled())
		{
			if (LOGGER.isDebugEnabled())
			{
				LOGGER.debug("'Replicate B2B Customers' flag in 'SAP Base Store Configuration' is set to 'false'.");
				LOGGER.debug("B2B Customer with customer ID " + customerModel.getCustomerID() + " was not sent to Data Hub.");
			}
			return;
		}

		/*
		 * This interceptor is used to replicate changes to existing B2B customers. New B2B customers are handled and
		 * replicated to Data Hub by B2BCustomerRegistrationEventListener. Therefore check ctx.isNew(customerModel).
		 *
		 * This interceptor is also called when B2B customers are received from Data Hub and Data Hub always sets the
		 * sapIsReplicated flag when sending B2B customers. But sapIsReplicated can't be set from the storefront.
		 * Therefore, to avoid replicating B2B customers that have just been received from Data Hub back to Data Hub,
		 * check ctx.isModified(customerModel, "sapIsReplicated").
		 */
		if (ctx.isNew(customerModel) || ctx.isModified(customerModel, "sapIsReplicated"))
		{
			return;
		}

		// check if one of the supported fields was modified
		if (ctx.isModified(customerModel, CustomerModel.NAME) || //
				ctx.isModified(customerModel, CustomerModel.TITLE) || //
				ctx.isModified(customerModel, CustomerModel.UID) || //
				ctx.isModified(customerModel, CustomerModel.DEFAULTSHIPMENTADDRESS))
		{
			LOGGER.debug("Sending modified B2B customer details to Data Hub.");
			final String sessionLanguage = getStoreSessionFacade().getCurrentLanguage() != null
					? getStoreSessionFacade().getCurrentLanguage().getIsocode() : "en";
			getB2bCustomerExportService().prepareAndSend(customerModel, sessionLanguage);
		}
		else
		{
			if (LOGGER.isDebugEnabled())
			{
				LOGGER.debug("B2B Customer with customer ID " + customerModel.getCustomerID() + " was not sent to Data Hub.");
				LOGGER.debug("B2B Customer name modified = " + ctx.isModified(customerModel, CustomerModel.NAME));
				LOGGER.debug("B2B Customer title modified = " + ctx.isModified(customerModel, CustomerModel.TITLE));
				LOGGER.debug("B2B Customer uid modified = " + ctx.isModified(customerModel, CustomerModel.UID));
				LOGGER.debug("B2B Customer default shipment address modified =  "
						+ ctx.isModified(customerModel, CustomerModel.DEFAULTSHIPMENTADDRESS));
			}
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
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
package com.sap.hybris.sec.eventpublisher.handler.impl;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.sec.eventpublisher.data.ResponseData;
import com.sap.hybris.sec.eventpublisher.dto.customer.Customer;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.tx.AfterSaveEvent;


/**
 * Replicate the updated/created customer to target
 */
public class AfterCustomerSaveEventHandler extends DefaultSaveEventHandler
{
	private Populator customerPopulator;
	
	@Override
	public boolean shouldHandle(AfterSaveEvent event, ItemModel model) throws Exception {
		 if (model instanceof CustomerModel) {
			CustomerModel customerModel = (CustomerModel) model;
			if (!StringUtils.isEmpty((customerModel.getCustomerID()))
					&& !CustomerType.GUEST.equals(customerModel.getType())) {
				return true;
			}
		}
		return false;
	} 
	
	@Override
	public ResponseData handle(AfterSaveEvent event, ItemModel model) throws Exception{
		ResponseData resData = null;
		if ((event.getType() == AfterSaveEvent.CREATE || event.getType() == AfterSaveEvent.UPDATE)
				&& (model instanceof CustomerModel)) {
			
			CustomerModel customerModel = (CustomerModel) model;
			resData = createOrUpdateCustomer(customerModel);

		}
		return resData;
	}

	/**
	 * @param endPoint
	 * @throws URISyntaxException
	 * @throws IOException
	 *
	 */
	protected ResponseData createOrUpdateCustomer(final CustomerModel customerModel)
			throws Exception {
		final Customer customerJson = new Customer();
		getCustomerPopulator().populate(customerModel, customerJson);
		final ResponseData resData = publish(getFinalJson(customerModel, customerJson.toString()),
				customerModel.getItemtype());
		return resData;
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
	@Required
	public void setCustomerPopulator(final Populator customerPopulator)
	{
		this.customerPopulator = customerPopulator;
	}

}

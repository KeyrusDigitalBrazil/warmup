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
package com.sap.hybris.sec.eventpublisher.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.Assert;

import com.sap.hybris.sec.eventpublisher.constants.EventpublisherConstants;
import com.sap.hybris.sec.eventpublisher.dto.order.OrderWS;


/**
 *
 */
public class SECOrderWSPopulator implements Populator<OrderModel, OrderWS>
{
	private static final Logger LOGGER = LogManager.getLogger(SECOrderWSPopulator.class);
	private ConfigurationService configurationService;

	@Override
	public void populate(final OrderModel source, final OrderWS target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		populateOrderFields(source, target);

	}


	protected void populateOrderFields(final OrderModel orderModel, final OrderWS orderWSJson)
	{
		final String bdType = getConfigurationService().getConfiguration().getString(EventpublisherConstants.BD_TYPE);
		final String formatterType = getConfigurationService().getConfiguration()
				.getString(EventpublisherConstants.DATE_FORMATTER_TYPE);

		final CustomerModel customerModel = getCustomerModel(orderModel.getUser());
		final UserModel employee = orderModel.getPlacedBy();

		if (customerModel != null && employee != null)
		{
			orderWSJson.setCustomerId(customerModel.getCustomerID());
			orderWSJson.setBdtType(bdType);
			orderWSJson.setOrderId(orderModel.getCode());
			orderWSJson.setCreatedTime(dateFormatter(orderModel.getCreationtime(), formatterType));
			orderWSJson.setAgentId(employee.getUid());
			orderWSJson.setStatus(orderModel.getStatus()!=null ? orderModel.getStatus().getCode() : "");
			orderWSJson.setModificationType(EventpublisherConstants.ORDER_MODIFICATION_TYPE);

		}
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("WS Order JSON:" + orderWSJson.toString());
		}


	}


	protected static String dateFormatter(final Date date, final String formatterType)
	{

		final SimpleDateFormat formatter = new SimpleDateFormat(formatterType);
		final String strDate = formatter.format(date);
		return strDate;
	}


	/**
	 *
	 */
	private CustomerModel getCustomerModel(final UserModel user)
	{
		CustomerModel customer = null;
		if (user instanceof CustomerModel)
		{
			customer = (CustomerModel) user;
		}
		return customer;
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


}

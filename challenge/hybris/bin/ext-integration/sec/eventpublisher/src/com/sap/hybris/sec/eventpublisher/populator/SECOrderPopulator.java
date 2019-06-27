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

import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.sap.hybris.sec.eventpublisher.constants.EventpublisherConstants;
import com.sap.hybris.sec.eventpublisher.dto.order.OrderCustomer;
import com.sap.hybris.sec.eventpublisher.dto.order.OrderIndex;


/**
 *
 */
public class SECOrderPopulator implements Populator<OrderModel, OrderIndex>
{
	private static final Logger LOGGER = LogManager.getLogger(SECOrderPopulator.class);


	private ConfigurationService configurationService;
	private CustomerNameStrategy customerNameStrategy;





	@Override
	public void populate(final OrderModel source, final OrderIndex target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		populateOrderFields(source, target);

	}


	/**
	 *
	 */
	protected void populateOrderFields(final OrderModel orderModel, final OrderIndex orderJson)
	{
		final String bdType = getConfigurationService().getConfiguration().getString(EventpublisherConstants.BD_TYPE);
		final String formatterType = getConfigurationService().getConfiguration()
				.getString(EventpublisherConstants.DATE_FORMATTER_TYPE);

		final OrderCustomer customerJson = new OrderCustomer();
		final Map<String, String> indexOrderData = new HashMap<String, String>();


		final CustomerModel customerModel = getCustomerModel(orderModel.getUser());
		populateIndexOrder(orderModel, indexOrderData, customerModel);
		if (customerModel != null)
		{
			final String[] names = getCustomerNameStrategy().splitName(customerModel.getName());
			if (names.length > 0 && !StringUtils.isEmpty(names[0]))
			{
				customerJson.setFirstName(names[0]);
			}
			if (names.length > 1 && !StringUtils.isEmpty(names[1]))
			{
				customerJson.setLastName(names[1]);
			}
			customerJson.setEmail(customerModel.getContactEmail());
			if( CustomerType.GUEST.equals(customerModel.getType())){
				customerJson.setGuest(true);
			}
			orderJson.setCustomer(customerJson);
			orderJson.setBdtType(bdType);
			orderJson.setOrderId(orderModel.getCode());
			if (orderModel.getCurrency() != null)
			{
				orderJson.setCurrency(orderModel.getCurrency().getIsocode());
			}
			orderJson.setTotalPrice(orderModel.getTotalPrice().toString());
			orderJson.setSubTotalPrice(orderModel.getSubtotal().toString());
			if (orderModel.getStatus() != null)
			{
				orderJson.setOrderStatus(orderModel.getStatus().getCode());
			}
			orderJson.setCreatedTime(dateFormatter(orderModel.getCreationtime(), formatterType));
			orderJson.setIndexOrderData(indexOrderData);
			if (orderModel.getModifiedtime() != null)
			{
				orderJson.setModifiedTime(dateFormatter(orderModel.getModifiedtime(), formatterType));
			}

			if (LOGGER.isDebugEnabled())
			{
				LOGGER.debug("Order Index JSON:" + orderJson.toString());
			}
		}


	}


	/**
	 *
	 */
	protected void populateIndexOrder(final OrderModel orderModel, final Map<String, String> indexOrderData,
			final CustomerModel customerModel)
	{
		final String orderId = EventpublisherConstants.ORDER_ID;
		final String customerAttributeId = getConfigurationService().getConfiguration()
				.getString(EventpublisherConstants.CUSTOMER_ID_ATTRIBUTE);
		indexOrderData.put(orderId, orderModel.getCode());
		indexOrderData.put(customerAttributeId, customerModel.getCustomerID());

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

	/**
	 * @return the customerNameStrategy
	 */
	public CustomerNameStrategy getCustomerNameStrategy()
	{
		return customerNameStrategy;
	}


	/**
	 * @param customerNameStrategy
	 *           the customerNameStrategy to set
	 */
	public void setCustomerNameStrategy(final CustomerNameStrategy customerNameStrategy)
	{
		this.customerNameStrategy = customerNameStrategy;
	}

}

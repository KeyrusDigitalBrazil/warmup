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

import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.sap.hybris.sec.eventpublisher.constants.EventpublisherConstants;
import com.sap.hybris.sec.eventpublisher.dto.order.OrderCustomer;
import com.sap.hybris.sec.eventpublisher.dto.order.OrderIndex;
import com.sap.hybris.sec.eventpublisher.dto.order.ReturnRequest;


/**
 *
 */
public class SECReturnRequestPopulator implements Populator<ReturnRequestModel, ReturnRequest>
{
	private static final Logger LOGGER = LogManager.getLogger(SECReturnRequestPopulator.class);


	private ConfigurationService configurationService;
	private CustomerNameStrategy customerNameStrategy;
	private List<ReturnStatus> cancellableReturnStatusList;





	@Override
	public void populate(final ReturnRequestModel source, final ReturnRequest target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		populateReturnRequestFields(source, target);

	}


	/**
	 *
	 */
	protected void populateReturnRequestFields(final ReturnRequestModel returnRequestModel, final ReturnRequest returnRequest)
	{
		final String formatterType = getConfigurationService().getConfiguration()
				.getString(EventpublisherConstants.DATE_FORMATTER_TYPE);
		
		returnRequest.setCode(returnRequestModel.getCode());
		returnRequest.setRma(returnRequestModel.getRMA());
		
		if (returnRequestModel.getStatus() != null)
		{
			returnRequest.setStatus(returnRequestModel.getStatus().getCode());
		}
		
		returnRequest.setRefundDeliveryCost(returnRequestModel.getRefundDeliveryCost());
		returnRequest.setCancellable(getCancellableReturnStatusList().contains(returnRequestModel.getStatus()));
		
		if (returnRequestModel.getCreationtime() != null){
			returnRequest.setCreatedTime(dateFormatter(returnRequestModel.getCreationtime(), formatterType));
		}
		
		if (returnRequestModel.getModifiedtime() != null)
		{
			returnRequest.setModifiedTime(dateFormatter(returnRequestModel.getModifiedtime(), formatterType));
		}

		
		final OrderModel order = returnRequestModel.getOrder();
		if(order!=null)
		{
			final CustomerModel customerModel = getCustomerModel(order.getUser());
			if (customerModel != null)
			{
				final OrderCustomer customerJson = new OrderCustomer();
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
				returnRequest.setCustomer(customerJson);

				if (LOGGER.isDebugEnabled())
				{
					LOGGER.debug("Order Index JSON:" + returnRequest.toString());
				}
			}
			
			final OrderIndex orderindex = new OrderIndex();
			final Map<String, String> indexOrderData = new HashMap<String, String>();
			populateIndexOrder(order, indexOrderData, customerModel);
			orderindex.setOrderId(order.getCode());
			orderindex.setCreatedTime(dateFormatter(order.getCreationtime(), formatterType));
			if (order.getCurrency() != null)
			{
				orderindex.setCurrency(order.getCurrency().getIsocode());
			}
			orderindex.setTotalPrice(order.getTotalPrice().toString());
			orderindex.setSubTotalPrice(order.getSubtotal().toString());
			orderindex.getOrderStatus();
			orderindex.setIndexOrderData(indexOrderData);
			if (order.getModifiedtime() != null)
			{
				orderindex.setModifiedTime(dateFormatter(order.getModifiedtime(), formatterType));
			}
			returnRequest.setOrder(orderindex);
		}
		

	}

	
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
	
	protected List<ReturnStatus> getCancellableReturnStatusList()
	{
		return cancellableReturnStatusList;
	}

	@Required
	public void setCancellableReturnStatusList(final List<ReturnStatus> cancellableReturnStatusList)
	{
		this.cancellableReturnStatusList = cancellableReturnStatusList;
	}

}

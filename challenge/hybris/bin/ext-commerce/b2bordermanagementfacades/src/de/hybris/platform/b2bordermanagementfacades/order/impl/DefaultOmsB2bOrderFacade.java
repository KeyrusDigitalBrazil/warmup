/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.b2bordermanagementfacades.order.impl;

import de.hybris.platform.b2b.company.B2BCommerceCostCenterService;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordermanagementfacades.order.data.OrderRequestData;
import de.hybris.platform.ordermanagementfacades.order.impl.DefaultOmsOrderFacade;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.util.localization.Localization;

import org.springframework.beans.factory.annotation.Required;


/**
 * Extend implementation of {@link DefaultOmsB2bOrderFacade}
 */
public class DefaultOmsB2bOrderFacade extends DefaultOmsOrderFacade
{
	private B2BCommerceCostCenterService b2bCommerceCostCenterService;
	private Converter<OrderRequestData, OrderModel> b2bOrderRequestReverseConverter;

	@Override
	public OrderData submitOrder(final OrderRequestData orderRequestData)
	{
		validateOrderData(orderRequestData);
		if (orderRequestData.getCostCenterCode() != null)
		{
			if (orderRequestData.getPaymentTransactions() != null)
			{
				validatePaymentTransactions(orderRequestData.getPaymentTransactions());
			}
			orderRequestData.getUser().setUid(getB2bCustomerById(orderRequestData.getUser()));
			final OrderModel orderModel = getB2bOrderRequestReverseConverter().convert(orderRequestData);
				orderModel.getEntries().stream().forEach(entry -> {
					entry.setCostCenter(getCostCenterById(orderRequestData.getCostCenterCode()));
					getModelService().save(entry);
				});
			getModelService().save(orderModel);
			orderModel.setCalculated(orderRequestData.isCalculated());
			getModelService().save(orderModel);
			return submitOrderInContext(orderModel);
		}
		else
		{
			validatePaymentTransactions(orderRequestData.getPaymentTransactions());
			return super.submitValidatedOrder(orderRequestData);
		}
	}

	/**
	 * Find {@link B2BCustomerModel} based on given {@link CustomerData#getUid()}
	 *
	 * @param customerData
	 * 		the {@link CustomerData}
	 * @return the customer uid
	 * @throws UnknownIdentifierException
	 */
	protected String getB2bCustomerById(final CustomerData customerData)
	{
		customerData.setUid(customerData.getUid().toLowerCase());

		if (getUserService().isUserExisting(customerData.getUid()) && (getUserService()
				.getUserForUID(customerData.getUid()) instanceof B2BCustomerModel))
		{
			return customerData.getUid();
		}
		else
		{
			throw new UnknownIdentifierException(
					String.format(Localization.getLocalizedString("b2bordermanagementfacade.orders.validation.false.isUserExisting"),
							customerData.getUid()));
		}
	}

	/**
	 * Find {@link B2BCostCenterModel} based on given code
	 *
	 * @param costCenterCode
	 * @return the B2BCostCenterModel
	 */
	protected B2BCostCenterModel getCostCenterById(final String costCenterCode)
	{
		final B2BCostCenterModel costCenter = getB2bCommerceCostCenterService().getCostCenterForCode(costCenterCode);
		if (costCenter != null)
		{
			return costCenter;
		}
		else
		{
			throw new UnknownIdentifierException(String.format(
					Localization.getLocalizedString("b2bordermanagementfacade.orders.validation.false.isCostCenterExisting"),
					costCenterCode));
		}
	}

	protected B2BCommerceCostCenterService getB2bCommerceCostCenterService()
	{
		return b2bCommerceCostCenterService;
	}

	@Required
	public void setB2bCommerceCostCenterService(final B2BCommerceCostCenterService b2bCommerceCostCenterService)
	{
		this.b2bCommerceCostCenterService = b2bCommerceCostCenterService;
	}

	protected Converter<OrderRequestData, OrderModel> getB2bOrderRequestReverseConverter()
	{
		return b2bOrderRequestReverseConverter;
	}

	@Required
	public void setB2bOrderRequestReverseConverter(final Converter<OrderRequestData, OrderModel> b2bOrderRequestReverseConverter)
	{
		this.b2bOrderRequestReverseConverter = b2bOrderRequestReverseConverter;
	}
}

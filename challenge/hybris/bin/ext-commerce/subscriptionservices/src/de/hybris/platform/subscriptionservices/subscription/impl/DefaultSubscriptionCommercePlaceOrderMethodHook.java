/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.subscriptionservices.subscription.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commerceservices.enums.SalesApplication;
import de.hybris.platform.commerceservices.order.hook.CommercePlaceOrderMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import javax.annotation.Nonnull;

import org.apache.commons.lang.BooleanUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation for the place order method hooks.
 */
public class DefaultSubscriptionCommercePlaceOrderMethodHook implements CommercePlaceOrderMethodHook
{
	private static final Logger LOG = Logger.getLogger(DefaultSubscriptionCommercePlaceOrderMethodHook.class);

	private OrderService orderService;
	private ModelService modelService;
	private BaseStoreService baseStoreService;
	private BaseSiteService baseSiteService;
	private PromotionsService promotionsService;
	private CommonI18NService commonI18NService;
	private CalculationService calculationService;


	@Override
	public void afterPlaceOrder(final CommerceCheckoutParameter parameter, final CommerceOrderResult orderModel)
			throws InvalidCartException
	{
		// default implementation doesn't do any after place order logic
	}

	@Override
	public void beforePlaceOrder(@Nonnull final CommerceCheckoutParameter parameter) throws InvalidCartException
	{
		validateParameterNotNull(parameter, "parameters cannot be null");
		final CartModel masterCart = parameter.getCart();

		if (masterCart.getParent() != null)
		{
			throw new InvalidCartException("The provided cart [" + masterCart.getCode()
					+ "] is a child cart. Only master carts can be processed.");
		}
	}

	@Override
	public void beforeSubmitOrder(final CommerceCheckoutParameter parameter, final CommerceOrderResult result)
			throws InvalidCartException
	{

		validateParameterNotNull(parameter, "parameters cannot be null");
		validateParameterNotNull(result, "result cannot be null");

		final OrderModel masterOrder = result.getOrder();
		final Collection<OrderModel> allOrders = new ArrayList<>();
		final CartModel masterCartModel = parameter.getCart();

		if (masterOrder != null)
		{
			allOrders.add(masterOrder);

			for (final AbstractOrderModel childCart : masterCartModel.getChildren())
			{
				OrderModel childOrder;

				childOrder = createEnrichedOrderFromCart(masterCartModel, (CartModel) childCart, parameter.getSalesApplication(),
						masterOrder);

				if (childOrder != null)
				{
					allOrders.add(childOrder);
					setMasterEntryForOrderEntries(masterOrder, childOrder, masterCartModel, (CartModel) childCart);
					childOrder.setParent(masterOrder);
					childOrder.setDate(masterOrder.getDate());
					getModelService().save(childOrder);
					getModelService().refresh(masterOrder);
				}
			}

			// Calculate the multi-order now that it has been copied
			for (final OrderModel orderModel : allOrders)
			{
				try
				{
					getCalculationService().calculateTotals(orderModel, false);
				}
				catch (final CalculationException ex)
				{
					LOG.error("Failed to calculate order [" + orderModel + "]", ex);
				}
				getModelService().refresh(orderModel);
			}

			getModelService().refresh(masterOrder.getUser());
		}

	}

	/**
	 * Creates an {@link OrderModel} based on the data of the given <code>cartModel</code> and returns it. In case a
	 * child order is created from a child cart, the child order is enriched with data of the given
	 * <code>masterCart</code> where necessary.
	 *
	 */
	protected OrderModel createEnrichedOrderFromCart(final CartModel masterCart, final CartModel cartModel,
			final SalesApplication salesApplication, final OrderModel masterOrder) throws InvalidCartException
	{
		validateParameterNotNullStandardMessage("masterCart", masterCart);
		validateParameterNotNullStandardMessage("cartModel", cartModel);

		if (BooleanUtils.isFalse(cartModel.getCalculated()))
		{
			throw new IllegalArgumentException("Cart model '" + cartModel.getCode() + "' must be calculated");
		}
		final CustomerModel customer = (CustomerModel) masterCart.getUser();
		validateParameterNotNull(customer, "Customer model cannot be null");

		final OrderModel orderModel = getOrderService().createOrderFromCart(cartModel);
		if (orderModel == null)
		{
			return orderModel;
		}

		// the user of child carts/orders may be 'anonymous'
		if (!customer.equals(orderModel.getUser()))
		{
			orderModel.setUser(customer);
		}

		// Store the current site and store on the order
		orderModel.setSite(getBaseSiteService().getCurrentBaseSite());
		orderModel.setStore(getBaseStoreService().getCurrentBaseStore());
		orderModel.setLanguage(getCommonI18NService().getCurrentLanguage());

		// set the date for the master order only; the date for child orders
		// will be set to the master order's date in the calling method
		if (masterCart.equals(cartModel))
		{
			orderModel.setDate(new Date());
		}

		if (salesApplication != null)
		{
			orderModel.setSalesApplication(salesApplication);
		}
		getModelService().saveAll(customer, orderModel);

		// clear the promotionResults that where cloned
		// from cart PromotionService.transferPromotionsToOrder will copy them over below.
		orderModel.setAllPromotionResults(Collections.<PromotionResultModel> emptySet());

		setPaymentInfo(cartModel, masterOrder, orderModel);

		setDeliveryInfo(masterCart, cartModel, orderModel);

		getModelService().save(orderModel);

		// Transfer promotions to the order
		getPromotionsService().transferPromotionsToOrder(cartModel, orderModel, false);
		
		return orderModel;
	}

	protected void setDeliveryInfo(final CartModel masterCart, final CartModel cartModel, final OrderModel orderModel)
	{
		if (cartModel.getDeliveryAddress() == null && masterCart.getDeliveryAddress() != null)
		{
			orderModel.setDeliveryAddress(masterCart.getDeliveryAddress());
		}

		if (cartModel.getDeliveryMode() == null && masterCart.getDeliveryMode() != null)
		{
			orderModel.setDeliveryMode(masterCart.getDeliveryMode());
		}
	}

	protected void setPaymentInfo(final CartModel cartModel, final OrderModel masterOrder, final OrderModel orderModel)
	{
		if (cartModel.getPaymentInfo() == null || cartModel.getPaymentInfo().getBillingAddress() == null)
		{
			// child carts usually do not have payment data, take it from master order
			setPaymentFromMasterOrder(masterOrder, orderModel);
		}
		else
		{
			setPaymentFromCart(cartModel, orderModel);
		}
	}

	protected void setPaymentFromCart(final CartModel cartModel, final OrderModel orderModel)
	{
		final AddressModel billingAddress = cartModel.getPaymentInfo().getBillingAddress();
		orderModel.setPaymentAddress(billingAddress);
		orderModel.getPaymentInfo().setBillingAddress(getModelService().clone(billingAddress));
		getModelService().save(orderModel.getPaymentInfo());
	}

	protected void setPaymentFromMasterOrder(final OrderModel masterOrder, final OrderModel orderModel)
	{
		if (masterOrder != null && masterOrder.getPaymentInfo() != null
				&& masterOrder.getPaymentInfo().getBillingAddress() != null)
		{
			orderModel.setPaymentInfo(masterOrder.getPaymentInfo());
			orderModel.setPaymentAddress(masterOrder.getPaymentInfo().getBillingAddress());
			getModelService().save(orderModel.getPaymentInfo());
		}
	}

	/**
	 * Sets the references for the masterEntry - childEntries relation for the created orders.
	 */
	protected void setMasterEntryForOrderEntries(final OrderModel masterOrder, final OrderModel childOrder,
			final CartModel masterCart, final CartModel childCart)
	{
		for (final AbstractOrderEntryModel masterCartEntry : masterCart.getEntries())
		{
			for (final AbstractOrderEntryModel childCartEntry : masterCartEntry.getChildEntries())
			{
				if (childCartEntry.getOrder().equals(childCart))
				{
					final AbstractOrderEntryModel childOrderEntry = getOrderService().getEntryForNumber(childOrder,
							childCartEntry.getEntryNumber());
					final AbstractOrderEntryModel masterOrderEntry = getOrderService().getEntryForNumber(masterOrder,
							masterCartEntry.getEntryNumber());
					childOrderEntry.setMasterEntry(masterOrderEntry);
					getModelService().save(childOrderEntry);
					getModelService().refresh(masterOrderEntry);
				}
			}
		}
		getModelService().refresh(masterOrder);
	}

	protected OrderService getOrderService()
	{
		return orderService;
	}

	@Required
	public void setOrderService(final OrderService orderService)
	{
		this.orderService = orderService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	protected PromotionsService getPromotionsService()
	{
		return promotionsService;
	}

	@Required
	public void setPromotionsService(final PromotionsService promotionsService)
	{
		this.promotionsService = promotionsService;
	}

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	protected CalculationService getCalculationService()
	{
		return calculationService;
	}

	@Required
	public void setCalculationService(final CalculationService calculationService)
	{
		this.calculationService = calculationService;
	}
}

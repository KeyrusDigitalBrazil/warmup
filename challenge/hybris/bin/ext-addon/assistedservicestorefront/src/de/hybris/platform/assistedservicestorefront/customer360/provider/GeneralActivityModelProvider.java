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
package de.hybris.platform.assistedservicestorefront.customer360.provider;

import static de.hybris.platform.assistedservicefacades.constants.AssistedservicefacadesConstants.CART_DESCRIPTION;
import static de.hybris.platform.assistedservicefacades.constants.AssistedservicefacadesConstants.CART_TEXT;
import static de.hybris.platform.assistedservicefacades.constants.AssistedservicefacadesConstants.ORDER_DESCRIPTION;
import static de.hybris.platform.assistedservicefacades.constants.AssistedservicefacadesConstants.ORDER_TEXT;
import static de.hybris.platform.assistedservicefacades.constants.AssistedservicefacadesConstants.SAVED_CART_TEXT;

import de.hybris.platform.assistedservicefacades.util.AssistedServiceUtils;
import de.hybris.platform.assistedservicestorefront.customer360.GeneralActivityData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.order.CommerceSaveCartService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Model provider implementation for General Activity fragment.
 */
public class GeneralActivityModelProvider extends TicketsModelProvider
{
	private CustomerAccountService customerAccountService;
	private BaseStoreService baseStoreService;
	private BaseSiteService baseSiteService;
	private CommerceCartService commerceCartService;
	private CommerceSaveCartService commerceSaveCartService;

	@Override
	public List<GeneralActivityData> getModel(final Map<String, String> parameters)
	{
		final List<GeneralActivityData> generalActivityList = super.getModel(parameters);
		final CustomerModel user = (CustomerModel) getUserService().getCurrentUser();

		populateCartData(generalActivityList, user);
		populateOrderData(generalActivityList, user);

		// Sort by Update Date desc as default sorting
		Collections.sort(generalActivityList, Comparator.comparing(GeneralActivityData::getUpdated).reversed());

		// return regarding to limit
		return generalActivityList.size() > getEventNumberLimit() ? generalActivityList.subList(0, getEventNumberLimit()) : generalActivityList;
	}



	/**
	 * OrderModel -> GeneralActivityData converter
	 *
	 * @param generalActivityList
	 * @param user
	 */
	protected void populateOrderData(final List<GeneralActivityData> generalActivityList, final CustomerModel user)
	{
		final PageableData latestOrderPageableData = new PageableData();
		latestOrderPageableData.setCurrentPage(0);
		latestOrderPageableData.setPageSize(getEventNumberLimit());
		latestOrderPageableData.setSort("byDate");

		final List<OrderModel> latestOrders = getCustomerAccountService()
				.getOrderList(user, getBaseStoreService().getCurrentBaseStore(), null, latestOrderPageableData).getResults();

		for (final OrderModel order : latestOrders)
		{
			final GeneralActivityData activityData = new GeneralActivityData();
			activityData.setType(ORDER_TEXT);
			activityData.setId(order.getCode());
			activityData.setStatus(order.getStatus() == null ? null : order.getStatus().getCode());
			activityData.setCreated(order.getCreationtime());
			activityData.setUpdated(order.getModifiedtime());
			activityData.setUrl(AssistedServiceUtils.populateCartorOrderUrl(order, getBaseSiteService().getCurrentBaseSite()));
			activityData.setDescription(ORDER_DESCRIPTION);
			activityData.setDescriptionArgs(new Object[]
			{ Integer.valueOf(order.getEntries().size()), order.getCurrency().getSymbol(), order.getTotalPrice() });
			generalActivityList.add(activityData);
		}
	}

	/**
	 * CartModel -> GeneralActivityData converter
	 *
	 * @param generalActivityList
	 * @param user
	 */
	protected void populateCartData(final List<GeneralActivityData> generalActivityList, final CustomerModel user)
	{
		final List<CartModel> carts = new ArrayList<>();

		// populate Session Carts
		final List<CartModel> sessionCarts =
				getCommerceCartService().getCartsForSiteAndUser(getBaseSiteService().getCurrentBaseSite(), user);
		if (CollectionUtils.isNotEmpty(sessionCarts))
		{
			carts.addAll(sessionCarts);
		}

		//populate Saved Carts
		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(0);
		pageableData.setPageSize(getEventNumberLimit());
		final List<CartModel> savedCarts = getCommerceSaveCartService().getSavedCartsForSiteAndUser(pageableData,
				getBaseSiteService().getCurrentBaseSite(), user, null).getResults();
		if (CollectionUtils.isNotEmpty(savedCarts))
		{
			carts.addAll(savedCarts);
		}

		for (final CartModel cart : carts)
		{
			final GeneralActivityData activityData = new GeneralActivityData();
			activityData.setId(cart.getCode());
			activityData.setStatus(null);
			activityData.setCreated(cart.getCreationtime());
			activityData.setUpdated(cart.getModifiedtime());
			activityData.setUrl(AssistedServiceUtils.populateCartorOrderUrl(cart, getBaseSiteService().getCurrentBaseSite()));

			if (cart.getSaveTime() != null)
			{
				activityData.setType(SAVED_CART_TEXT);
				activityData.setDescription(cart.getName());
			}
			else
			{
				activityData.setType(CART_TEXT);
				activityData.setDescription(CART_DESCRIPTION);
				activityData.setDescriptionArgs(new Object[]
				{ Integer.valueOf(cart.getEntries().size()), cart.getCurrency().getSymbol(), cart.getTotalPrice() });
			}
			generalActivityList.add(activityData);
		}
	}

	@Override
	protected int getEventNumberLimit()
	{
		return 20; // 20 for General Activity
	}

	protected CustomerAccountService getCustomerAccountService()
	{
		return customerAccountService;
	}

	@Required
	public void setCustomerAccountService(final CustomerAccountService customerAccountService)
	{
		this.customerAccountService = customerAccountService;
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

	protected CommerceCartService getCommerceCartService()
	{
		return commerceCartService;
	}

	@Required
	public void setCommerceCartService(final CommerceCartService commerceCartService)
	{
		this.commerceCartService = commerceCartService;
	}

	protected CommerceSaveCartService getCommerceSaveCartService()
	{
		return commerceSaveCartService;
	}

	@Required
	public void setCommerceSaveCartService(final CommerceSaveCartService commerceSaveCartService)
	{
		this.commerceSaveCartService = commerceSaveCartService;
	}
}

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

import de.hybris.platform.commerceservices.order.CommerceSaveCartException;
import de.hybris.platform.commerceservices.order.hook.CommerceCloneSavedCartMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceSaveCartParameter;
import de.hybris.platform.commerceservices.service.data.CommerceSaveCartResult;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * DefaultSubscriptionCommerceCloneCartHook.
 */
public class DefaultSubscriptionCommerceCloneCartHook implements CommerceCloneSavedCartMethodHook
{
	private static final Logger LOG = Logger.getLogger(DefaultSubscriptionCommerceCloneCartHook.class);

	private CartService cartService;
	private ModelService modelService;

	@Override
	public void beforeCloneSavedCart(@Nonnull final CommerceSaveCartParameter parameter) throws CommerceSaveCartException
	{
		validateParameterNotNull(parameter, "parameter cannot be null");

		final CartModel cartToBeCloned = parameter.getCart();

		if (null != cartToBeCloned.getParent())
		{
			throw new CommerceSaveCartException("The provided cart [" + cartToBeCloned.getCode()
					+ "] is a child cart. Only master carts can be cloned.");
		}
	}

	@Override
	public void afterCloneSavedCart(@Nonnull final CommerceSaveCartParameter parameter,@Nonnull final CommerceSaveCartResult saveCartResult)
			throws CommerceSaveCartException
	{
		validateParameterNotNull(parameter, "parameter cannot be null");
		validateParameterNotNull(saveCartResult, "saveCartResult cannot be null");
		validateParameterNotNull(saveCartResult.getSavedCart(), "savedCart cannot be null");

		final CartModel clonedMasterCart = saveCartResult.getSavedCart();
		final CartModel originalMasterCart = parameter.getCart();

		if (CollectionUtils.isEmpty(originalMasterCart.getChildren()))
		{
			return;
		}

		final Map<String, AbstractOrderEntryModel> clonedEntriesCodeMap = new HashMap<>();
		for (int i = 0; i < originalMasterCart.getEntries().size(); ++i)
		{
			clonedEntriesCodeMap
					.put(originalMasterCart.getEntries().get(i).getPk().toString(), clonedMasterCart.getEntries().get(i));
		}

		final List<AbstractOrderModel> clonedChildren = new ArrayList<>();
		for (final AbstractOrderModel abstractOrder : originalMasterCart.getChildren())
		{
			final CartModel originalChildCart = (CartModel) abstractOrder;
			final CartModel clonedChildCart = getCartService().clone(null, null, originalChildCart, null);
			clonedChildCart.setPaymentTransactions(null);
			clonedChildCart.setCode(null); // save new cart, do not update existing one
			clonedChildCart.setParent(clonedMasterCart);

			for (final AbstractOrderEntryModel clonedChildCartEntry : clonedChildCart.getEntries())
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug(String.format("Master entry with code [%s] changed to [%s]", clonedChildCartEntry.getMasterEntry()
							.getPk().toString(), clonedEntriesCodeMap.get(clonedChildCartEntry.getMasterEntry().getPk().toString())));
				}
				clonedChildCartEntry.setMasterEntry(clonedEntriesCodeMap
						.get(clonedChildCartEntry.getMasterEntry().getPk().toString()));
			}
			clonedChildren.add(clonedChildCart);
			getModelService().save(clonedChildCart);
			getModelService().refresh(clonedMasterCart);
		}
		clonedMasterCart.setChildren(clonedChildren);
		getModelService().save(clonedMasterCart);
		getModelService().refresh(clonedMasterCart);
	}

	protected CartService getCartService()
	{
		return cartService;
	}

	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
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
}

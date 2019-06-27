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
package de.hybris.platform.commerceservices.order.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commerceservices.constants.CommerceServicesConstants;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.order.CommerceRemoveEntryGroupStrategy;
import de.hybris.platform.commerceservices.order.CommerceUpdateCartEntryStrategy;
import de.hybris.platform.commerceservices.order.hook.CommerceRemoveEntryGroupMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.service.data.RemoveEntryGroupParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.order.EntryGroupService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class DefaultCommerceRemoveEntryGroupStrategy implements CommerceRemoveEntryGroupStrategy
{
	private static final Logger LOG = Logger.getLogger(DefaultCommerceRemoveEntryGroupStrategy.class);

	private List<CommerceRemoveEntryGroupMethodHook> commerceRemoveEntryGroupHooks;
	private ConfigurationService configurationService;
	private CommerceUpdateCartEntryStrategy updateCartEntryStrategy;
	private EntryGroupService entryGroupService;
	private ModelService modelService;

	/**
	 * Removes from the cart an entry group with all nested groups and their cart entries
	 *
	 * @param parameter
	 *           remove entry group parameters
	 * @return Cart modification information
	 * @throws CommerceCartModificationException
	 *            if related cart entry wasn't removed
	 *
	 */
	@Override
	@Nonnull
	public CommerceCartModification removeEntryGroup(@Nonnull final RemoveEntryGroupParameter parameter)
			throws CommerceCartModificationException
	{
		validateParameterNotNullStandardMessage("parameter", parameter);

		beforeRemoveEntryGroup(parameter);
		final CommerceCartModification modification = doRemoveEntryGroup(parameter);
		afterRemoveEntryGroup(parameter, modification);
		return modification;
	}

	/**
	 * Do remove from the cart.
	 *
	 * @param parameter
	 *           remove entry group parameter
	 * @return the commerce cart modification
	 * @throws CommerceCartModificationException
	 *            the commerce cart modification exception
	 */
	protected CommerceCartModification doRemoveEntryGroup(final RemoveEntryGroupParameter parameter)
			throws CommerceCartModificationException
	{
		validateRemoveEntryGroupParameter(parameter);

		final CartModel cartModel = parameter.getCart();
		final Integer groupNumber = parameter.getEntryGroupNumber();

		try
		{
			final EntryGroup parentGroup = entryGroupService.getParent(cartModel, groupNumber);
			final EntryGroup group = getEntryGroupService().getGroup(cartModel, groupNumber);
			final List<Integer> groupNumbers = getAllSubsequentGroupNumbers(group);
			removeEntriesByGroupNumber(parameter, groupNumbers);
			if (parentGroup == null)
			{
				final List<EntryGroup> rootGroups = cartModel.getEntryGroups();
				cartModel.setEntryGroups(excludeEntryGroup(rootGroups, groupNumber));
			}
			else
			{
				parentGroup.setChildren(excludeEntryGroup(parentGroup.getChildren(), groupNumber));
			}

			getEntryGroupService().forceOrderSaving(cartModel);
			return createRemoveEntryGroupResp(parameter, CommerceCartModificationStatus.SUCCESSFULLY_REMOVED);


		}
		catch (final IllegalArgumentException e)
		{
			LOG.error("Failed to remove entry group.", e);
			return createRemoveEntryGroupResp(parameter, CommerceCartModificationStatus.INVALID_ENTRY_GROUP_NUMBER);
		}
	}

	protected List<EntryGroup> excludeEntryGroup(final List<EntryGroup> source, final Integer groupNumber)
	{
		return source.stream().filter(g -> !groupNumber.equals(g.getGroupNumber())).collect(Collectors.toList());
	}

	protected CommerceCartModification createRemoveEntryGroupResp(final RemoveEntryGroupParameter parameter, final String status)
	{
		final Integer entryGroupNumber = parameter.getEntryGroupNumber();

		final CommerceCartModification modification = new CommerceCartModification();
		modification.setStatusCode(status);
		modification.setEntryGroupNumbers(new HashSet(Collections.singletonList(entryGroupNumber)));

		return modification;
	}

	protected void removeEntriesByGroupNumber(final RemoveEntryGroupParameter parameter, final List<Integer> groupNumbers)
			throws CommerceCartModificationException
	{
		final CartModel cartModel = parameter.getCart();

		if (cartModel.getEntries() == null)
		{
			return;
		}

		for (final AbstractOrderEntryModel entry : cartModel.getEntries())
		{
			if (CollectionUtils.containsAny(groupNumbers, entry.getEntryGroupNumbers()))
			{
				final CommerceCartParameter updateParameter = new CommerceCartParameter();
				updateParameter.setCart(parameter.getCart());
				updateParameter.setEnableHooks(parameter.isEnableHooks());
				updateParameter.setQuantity(0L);
				updateParameter.setEntryNumber(entry.getEntryNumber().intValue());
				getUpdateCartEntryStrategy().updateQuantityForCartEntry(updateParameter);
			}
		}
	}

	protected List<Integer> getAllSubsequentGroupNumbers(final EntryGroup group)
	{
		return getEntryGroupService().getNestedGroups(group).stream()
				.map(EntryGroup::getGroupNumber)
				.collect(Collectors.toList());
	}

	protected void validateRemoveEntryGroupParameter(final RemoveEntryGroupParameter parameters)
	{
		final CartModel cartModel = parameters.getCart();
		final Integer entryGroupNumber = parameters.getEntryGroupNumber();

		validateParameterNotNull(cartModel, "Cart model cannot be null");
		validateParameterNotNullStandardMessage("entryGroupNumber", entryGroupNumber);
	}

	protected void beforeRemoveEntryGroup(final RemoveEntryGroupParameter parameters) throws CommerceCartModificationException
	{
		if (getCommerceRemoveEntryGroupHooks() != null
				&& (parameters.isEnableHooks()
				&& getConfigurationService().getConfiguration().getBoolean(CommerceServicesConstants.REMOVEENTRYGROUPHOOK_ENABLED, true)))
		{
			for (final CommerceRemoveEntryGroupMethodHook commerceAddToCartMethodHook : getCommerceRemoveEntryGroupHooks())
			{
				commerceAddToCartMethodHook.beforeRemoveEntryGroup(parameters);
			}
		}
	}

	protected void afterRemoveEntryGroup(final RemoveEntryGroupParameter parameters, final CommerceCartModification result)
			throws CommerceCartModificationException
	{
		if (getCommerceRemoveEntryGroupHooks() != null
				&& (parameters.isEnableHooks()
				&& getConfigurationService().getConfiguration().getBoolean(CommerceServicesConstants.REMOVEENTRYGROUPHOOK_ENABLED, true)))
		{
			for (final CommerceRemoveEntryGroupMethodHook commerceAddToCartMethodHook : getCommerceRemoveEntryGroupHooks())
			{
				commerceAddToCartMethodHook.afterRemoveEntryGroup(parameters, result);
			}
		}
	}

	protected List<CommerceRemoveEntryGroupMethodHook> getCommerceRemoveEntryGroupHooks()
	{
		return commerceRemoveEntryGroupHooks;
	}

	@Required
	public void setCommerceRemoveEntryGroupHooks(final List<CommerceRemoveEntryGroupMethodHook> commerceRemoveEntryGroupHooks)
	{
		this.commerceRemoveEntryGroupHooks = commerceRemoveEntryGroupHooks;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	protected CommerceUpdateCartEntryStrategy getUpdateCartEntryStrategy()
	{
		return updateCartEntryStrategy;
	}

	@Required
	public void setUpdateCartEntryStrategy(final CommerceUpdateCartEntryStrategy updateCartEntryStrategy)
	{
		this.updateCartEntryStrategy = updateCartEntryStrategy;
	}

	protected EntryGroupService getEntryGroupService()
	{
		return entryGroupService;
	}

	@Required
	public void setEntryGroupService(final EntryGroupService entryGroupService)
	{
		this.entryGroupService = entryGroupService;
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

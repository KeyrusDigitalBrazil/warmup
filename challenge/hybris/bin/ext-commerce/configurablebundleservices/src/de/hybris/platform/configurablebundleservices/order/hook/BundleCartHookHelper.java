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
package de.hybris.platform.configurablebundleservices.order.hook;

import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.enums.GroupType;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.order.EntryGroupService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Helper methods for operating with bundles in cart.
 *
 * @see BundleAddToCartMethodHook
 * @see BundleUpdateCartEntryHook
 */
public class BundleCartHookHelper
{
	private EntryGroupService entryGroupService;
	private ModelService modelService;

	/**
	 * Sets all cart entries that belong to the same bundle to "not calculated". As the prices within a bundle may vary
	 * dependent on the bundle entries a re-calculation of the whole bundle (and all the carts that contain entries of
	 * the affected bundle) is necessary.
	 *
	 * @param cart
	 * 		the cart to recalculate entries in.
	 * @param entryGroupNumber
	 * 		the entry group number to recalculate entries for.
	 */
	public void invalidateBundleEntries(@Nonnull final CartModel cart, @Nonnull final Integer entryGroupNumber)
	{
		final EntryGroup root = getEntryGroupService().getRoot(cart, entryGroupNumber);
		final List<Integer> treeGroupIds = getEntryGroupService().getNestedGroups(root).stream()
				.filter(group -> GroupType.CONFIGURABLEBUNDLE.equals(group.getGroupType()))
				.map(EntryGroup::getGroupNumber)
				.collect(Collectors.toList());
		final Set<ItemModel> models = new HashSet<>();
		cart.getEntries().stream()
				.filter(e -> e.getEntryGroupNumbers() != null)
				.filter(e -> CollectionUtils.containsAny(treeGroupIds, e.getEntryGroupNumbers()))
				.forEach(e -> {
					e.setCalculated(Boolean.FALSE);
					e.getOrder().setCalculated(Boolean.FALSE);
					models.add(e);
					models.add(e.getOrder());
				});
		getModelService().saveAll(models);
	}

	/**
	 * Adds given entry group numbers to the parameter.
	 *
	 * @param parameter parameter to add the number to
	 * @param newGroupNumbers the numbers to add
	 * @deprecated Since 6.5 use {@link this#union(Set, Collection)}
	 */
	@Deprecated
	public void updateParameterEntryGroupNumbers(@Nonnull final CommerceCartParameter parameter,
			final Collection<Integer> newGroupNumbers)
	{
		parameter.setEntryGroupNumbers(union(parameter.getEntryGroupNumbers(), newGroupNumbers));
	}

	/**
	 * Joins two sets of group numbers.
	 *
	 * @param first first set
	 * @param second second set
	 * @return the union of the sets
	 */
	@Nonnull
	public Set<Integer> union(final Set<Integer> first, final Collection<Integer> second)
	{
		final Set<Integer> result = new HashSet<>();
		if (first != null)
		{
			result.addAll(first);
		}
		if (second != null)
		{
			result.addAll(second);
		}
		return result;
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

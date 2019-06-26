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
package de.hybris.platform.subscriptionbundleservices.order.hook.impl;

import de.hybris.platform.configurablebundleservices.order.hook.BundleCartHookHelper;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.order.EntryGroup;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Subscription-aware implementation of {@link de.hybris.platform.configurablebundleservices.order.hook.BundleCartHookHelper},
 * invalidates entries for child carts as well as the master cart entries.
 */
public class DefaultSubscriptionBundleCartHookHelper extends BundleCartHookHelper
{

	@Override
	public void invalidateBundleEntries(@Nonnull final CartModel cart, @Nonnull final Integer entryGroupNumber)
	{
		final EntryGroup root = getEntryGroupService().getRoot(cart, entryGroupNumber);
		final List<Integer> treeGroupIds = getEntryGroupService().getNestedGroups(root).stream()
				.map(EntryGroup::getGroupNumber)
				.collect(Collectors.toList());
		cart.getEntries().stream()
				.filter(e -> e.getEntryGroupNumbers() != null)
				.filter(e -> CollectionUtils.containsAny(treeGroupIds, e.getEntryGroupNumbers()))
				.flatMap(e -> Stream.concat(Stream.of(e), (e.getChildEntries() == null) ? Stream.empty() : e.getChildEntries().stream()))
				.forEach(e -> {
					e.setCalculated(Boolean.FALSE);
					e.getOrder().setCalculated(Boolean.FALSE);
					getModelService().save(e);
					getModelService().save(e.getOrder());
				});
	}
}

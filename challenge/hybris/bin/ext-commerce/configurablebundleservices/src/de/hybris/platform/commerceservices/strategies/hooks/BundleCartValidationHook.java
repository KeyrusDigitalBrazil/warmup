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

package de.hybris.platform.commerceservices.strategies.hooks;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.enums.GroupType;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.order.EntryGroupService;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Cart validation hook to disable checkout if cart has an invalid bundle.
 */
public class BundleCartValidationHook implements CartValidationHook
{
	private EntryGroupService entryGroupService;

	@Override
	public void beforeValidateCart(final CommerceCartParameter parameter, final List<CommerceCartModification> modifications)
	{
		// nop
	}

	@Override
	public void afterValidateCart(final CommerceCartParameter parameter, final List<CommerceCartModification> modifications)
	{
		if (modifications == null || parameter == null)
		{
			return;
		}
		if (parameter.getCart() == null)
		{
			return;
		}
		if (parameter.getCart().getEntryGroups() == null)
		{
			return;
		}

		final Set<Integer> invalidGroups = parameter.getCart().getEntryGroups().stream()
				.filter(root -> GroupType.CONFIGURABLEBUNDLE.equals(root.getGroupType()))
				.map(root -> getEntryGroupService().getNestedGroups(root))
				.flatMap(Collection::stream)
				.filter(group -> group.getErroneous() != null)
				.filter(group -> group.getErroneous().booleanValue())
				.map(EntryGroup::getGroupNumber)
				.collect(Collectors.toSet());
		if (!invalidGroups.isEmpty())
		{
			final CommerceCartModification modification = new CommerceCartModification();
			modification.setStatusCode(CommerceCartModificationStatus.ENTRY_GROUP_ERROR);
			modification.setEntryGroupNumbers(invalidGroups);
			modifications.add(modification);
		}
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
}

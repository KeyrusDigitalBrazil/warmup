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

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.hook.CommerceRemoveEntryGroupMethodHook;
import de.hybris.platform.commerceservices.service.data.RemoveEntryGroupParameter;
import de.hybris.platform.core.enums.GroupType;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.order.EntryGroupService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;
import org.apache.log4j.Logger;

import java.util.Collections;

import static de.hybris.platform.servicelayer.util.ServicesUtil.*;

/**
 * Remove entry group method hook which forbids removal of non-root entry groups of
 * {@link GroupType#CONFIGURABLEBUNDLE} type.
 */
public class BundleRemoveEntryGroupMethodHook implements CommerceRemoveEntryGroupMethodHook
{
	private static final Logger LOG = Logger.getLogger(BundleRemoveEntryGroupMethodHook.class.getName());

	private EntryGroupService entryGroupService;

	@Override
	public void beforeRemoveEntryGroup(final RemoveEntryGroupParameter parameter) throws CommerceCartModificationException
	{
		validateParameterNotNullStandardMessage("parameter", parameter);
		validateParameterNotNullStandardMessage("parameter.cart", parameter.getCart());
		validateParameterNotNullStandardMessage("parameter.entryGroupNumber", parameter.getEntryGroupNumber());

		final CartModel cart = parameter.getCart();
		final Integer groupNumber = parameter.getEntryGroupNumber();

		if (CollectionUtils.isEmpty(cart.getEntryGroups()))
		{
			return;
		}
		try
		{

			final EntryGroup group = getEntryGroupService().getGroupOfType(cart, Collections.singletonList(groupNumber), GroupType.CONFIGURABLEBUNDLE);
			if (group == null)
			{
				return;
			}
			if (cart.getEntryGroups().stream().map(EntryGroup::getGroupNumber).noneMatch(groupNumber::equals))
			{
				throw new  CommerceCartModificationException("Cannot remove non-root entry group with number '" + groupNumber
						+ "' from the cart with code '" + cart.getCode() + "'");
			}
		}
		catch (IllegalArgumentException | AmbiguousIdentifierException e)
		{
			LOG.debug("Got Exception", e);
			throw new  CommerceCartModificationException("Cannot remove non-existing group '" + groupNumber
					+ "' from the cart '" + cart.getCode() + "'");
		}
	}

	@Override
	public void afterRemoveEntryGroup(final RemoveEntryGroupParameter parameter, final CommerceCartModification result)
	{
		// Empty method
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

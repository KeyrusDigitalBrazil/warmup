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

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commerceservices.order.CommerceCartMergingException;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.order.strategies.EntryMergeStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.order.EntryGroupService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.access.AccessDeniedException;


public class DefaultCommerceCartMergingStrategy extends AbstractCommerceCartStrategy implements CommerceCartMergingStrategy
{
	private static final String ONLY_LOGGED_USER_CAN_MERGE_CARTS = "Only logged user can merge carts!";

	private UserService userService;
	private CommerceCartService commerceCartService;
	private BaseSiteService baseSiteService;
	private EntryMergeStrategy entryMergeStrategy;
	private EntryGroupService entryGroupService;

	@Override
	public void mergeCarts(final CartModel fromCart, final CartModel toCart, final List<CommerceCartModification> modifications)
			throws CommerceCartMergingException
	{
		// validate before merge
		validationBeforeMerge(fromCart, toCart, modifications, getUserService().getCurrentUser());

		// copy entry group
		copyEntryGroups(fromCart, toCart);

		// merge entry with cart
		for (final AbstractOrderEntryModel entry : fromCart.getEntries())
		{
			modifications.add(mergeEntryWithCart(entry, toCart));
		}

		// after merge
		// TODO payment transactions - to clear or not to clear...
		toCart.setCalculated(Boolean.FALSE);
		fromCart.setEntries(Collections.emptyList());
		getModelService().save(toCart);
		getModelService().remove(fromCart);
	}

	protected void validationBeforeMerge(final CartModel fromCart, final CartModel toCart,
			final List<CommerceCartModification> modifications, final UserModel currentUser) throws CommerceCartMergingException
	{
		if (currentUser == null || getUserService().isAnonymousUser(currentUser))
		{
			throw new AccessDeniedException(ONLY_LOGGED_USER_CAN_MERGE_CARTS);
		}

		validateParameterNotNullStandardMessage("fromCart", fromCart);
		validateParameterNotNullStandardMessage("toCart", toCart);
		validateParameterNotNullStandardMessage("modifications", modifications);

		if (!Objects.equals(getBaseSiteService().getCurrentBaseSite(), fromCart.getSite()))
		{
			throw new CommerceCartMergingException(String.format("Current site %s is not equal to cart %s site %s",
					getBaseSiteService().getCurrentBaseSite().getName(), fromCart.getCode(), fromCart.getSite().getName()));
		}

		if (!Objects.equals(getBaseSiteService().getCurrentBaseSite(), toCart.getSite()))
		{
			throw new CommerceCartMergingException(String.format("Current site %s is not equal to cart %s site %s",
					getBaseSiteService().getCurrentBaseSite().getName(), toCart.getCode(), toCart.getSite().getName()));
		}

		if (Objects.equals(fromCart.getGuid(), toCart.getGuid()))
		{
			throw new CommerceCartMergingException("Cannot merge cart to itself!");
		}
	}

	protected void copyEntryGroups(final CartModel fromCart, final CartModel toCart)
	{
		if (CollectionUtils.isEmpty(fromCart.getEntryGroups()))
		{
			return;
		}
		if (CollectionUtils.isNotEmpty(toCart.getEntryGroups()))
		{
			final Map<Integer, Integer> indexMap = reindexEntryGroups(fromCart.getEntryGroups(), toCart.getEntryGroups());
			reindexEntries(fromCart.getEntries(), indexMap);
			final List<EntryGroup> groups = new ArrayList<>(toCart.getEntryGroups());
			groups.addAll(fromCart.getEntryGroups());
			toCart.setEntryGroups(groups);
		}
		else
		{
			toCart.setEntryGroups(fromCart.getEntryGroups());
		}
	}

	protected Map<Integer, Integer> reindexEntryGroups(final List<EntryGroup> roots, final List<EntryGroup> refRoots)
	{
		final AtomicInteger groupNumber = new AtomicInteger(getEntryGroupService().findMaxGroupNumber(refRoots));
		final List<EntryGroup> groups = flatten(roots);
		return groups.stream().collect(Collectors.toMap(EntryGroup::getGroupNumber, group -> {
			group.setGroupNumber(Integer.valueOf(groupNumber.incrementAndGet()));
			return group.getGroupNumber();
		}));
	}

	protected List<EntryGroup> flatten(final List<EntryGroup> roots)
	{
		return roots.stream().map(getEntryGroupService()::getNestedGroups).flatMap(Collection::stream).collect(Collectors.toList());
	}

	protected void reindexEntries(final List<AbstractOrderEntryModel> entries, final Map<Integer, Integer> indexMap)
	{
		if (entries == null)
		{
			return;
		}
		entries.forEach(entry -> {
			if (CollectionUtils.isNotEmpty(entry.getEntryGroupNumbers()))
			{
				final Set<Integer> numbers = entry.getEntryGroupNumbers().stream().map(number -> {
					final Integer newIndex = indexMap.get(number);
					return newIndex == null ? number : newIndex;
				}).collect(Collectors.toSet());
				entry.setEntryGroupNumbers(numbers);
			}
		});
	}

	protected CommerceCartModification mergeEntryWithCart(final AbstractOrderEntryModel entry, final CartModel toCart)
			throws CommerceCartMergingException
	{
		final AbstractOrderEntryModel entryToMerge = getEntryMergeStrategy().getEntryToMerge(toCart.getEntries(), entry);

		if (entryToMerge == null)
		{
			final AbstractOrderEntryModel clonedEntry = getModelService().clone(entry, entry.getClass());
			getModelService().detach(clonedEntry);
			clonedEntry.setOrder(toCart);
			updateEntryNumber(clonedEntry, toCart);

			if (toCart.getEntries() == null)
			{
				toCart.setEntries(Collections.singletonList(clonedEntry));
			}
			else
			{
				final List<AbstractOrderEntryModel> entries = new ArrayList<>(toCart.getEntries());
				entries.add(clonedEntry);
				toCart.setEntries(entries);
			}
			getModelService().save(clonedEntry);

			final CommerceCartModification commerceCartModification = new CommerceCartModification();
			commerceCartModification.setEntry(clonedEntry);
			commerceCartModification.setQuantity(clonedEntry.getQuantity().longValue());
			commerceCartModification.setQuantityAdded(clonedEntry.getQuantity().longValue());
			commerceCartModification.setStatusCode(CommerceCartModificationStatus.SUCCESS);
			return commerceCartModification;
		}
		else
		{
			final CommerceCartParameter updateQuantityParameter = new CommerceCartParameter();
			updateQuantityParameter.setCart(toCart);
			updateQuantityParameter.setQuantity(entryToMerge.getQuantity().longValue() + entry.getQuantity().longValue());
			updateQuantityParameter.setEntryNumber(entryToMerge.getEntryNumber().longValue());
			try
			{
				return getCommerceCartService().updateQuantityForCartEntry(updateQuantityParameter);
			}
			catch (final CommerceCartModificationException e)
			{
				throw new CommerceCartMergingException("Exception during cart merge", e);
			}
		}
	}

	protected void updateEntryNumber(final AbstractOrderEntryModel entry, final CartModel toCart)
	{
		if (toCart.getEntries() == null)
		{
			return;
		}
		boolean duplicate = false;
		int maxEntryGroupNumber = 0;
		for (final AbstractOrderEntryModel e : toCart.getEntries())
		{
			if (Objects.equals(entry.getEntryNumber(), e.getEntryNumber()))
			{
				duplicate = true;
			}
			if (e.getEntryNumber() != null)
			{
				maxEntryGroupNumber = Math.max(maxEntryGroupNumber, e.getEntryNumber().intValue());
			}
		}
		if (duplicate)
		{
			entry.setEntryNumber(Integer.valueOf(maxEntryGroupNumber + 1));
		}
	}

	public UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	public CommerceCartService getCommerceCartService()
	{
		return commerceCartService;
	}

	@Required
	public void setCommerceCartService(final CommerceCartService commerceCartService)
	{
		this.commerceCartService = commerceCartService;
	}

	public BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	protected EntryMergeStrategy getEntryMergeStrategy()
	{
		return entryMergeStrategy;
	}

	@Required
	public void setEntryMergeStrategy(final EntryMergeStrategy entryMergeStrategy)
	{
		this.entryMergeStrategy = entryMergeStrategy;
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
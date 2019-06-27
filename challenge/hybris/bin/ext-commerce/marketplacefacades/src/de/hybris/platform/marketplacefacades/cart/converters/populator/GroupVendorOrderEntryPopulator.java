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
package de.hybris.platform.marketplacefacades.cart.converters.populator;

import de.hybris.platform.commercefacades.order.EntryGroupData;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.enums.GroupType;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.marketplacefacades.vendor.data.VendorData;
import de.hybris.platform.marketplaceservices.strategies.VendorOriginalEntryGroupDisplayStrategy;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.commons.collections.MapUtils;
import org.springframework.util.Assert;


/**
 * Groups multiple {@link OrderEntryData} as one entry in a {@link AbstractOrderData} based on the vendor of the
 * product. A root Vendor Group will be appended to current root groups of vendor, then all vendor groups be added to
 * that root Group
 */
public class GroupVendorOrderEntryPopulator<S extends AbstractOrderModel, T extends AbstractOrderData> implements Populator<S, T>
{
	private VendorOriginalEntryGroupDisplayStrategy vendorOriginalEntryGroupDisplayStrategy;

	@Override
	public void populate(final AbstractOrderModel source, final AbstractOrderData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		target.setEntries(groupVendorEntries(target));
		target.setDisplayOriginalEntryGroup(getVendorOriginalEntryGroupDisplayStrategy().shouldDisplayOriginalEntryGroup());
	}

	protected List<OrderEntryData> groupVendorEntries(final AbstractOrderData order)
	{
		final List<OrderEntryData> entries = order.getEntries();
		final List<OrderEntryData> allEntries = new ArrayList<>();
		final Map<String, EntryGroupData> vendorGroups = new HashMap<>();
		for (final OrderEntryData entry : entries)
		{
			addEntryToGroup(entry, vendorGroups);
			allEntries.add(entry);
		}
		if (MapUtils.isNotEmpty(vendorGroups))
		{
			final EntryGroupData vendorRootGroup = getVendorRootGroup(vendorGroups);
			addToCurrentRootGroups(order, vendorRootGroup);
		}
		return allEntries;
	}

	protected void addToCurrentRootGroups(final AbstractOrderData order, final EntryGroupData vendorRootGroup)
	{
		final List<EntryGroupData> currentRoots = order.getRootGroups();
		if (currentRoots != null)
		{
			currentRoots.add(vendorRootGroup);
		}
		else
		{
			order.setRootGroups(Collections.singletonList(vendorRootGroup));
		}

	}

	protected void addEntryToGroup(final OrderEntryData entry, final Map<String, EntryGroupData> vendorGroups)
	{
		if (entry.getProduct().getVendor() != null)
		{
			final EntryGroupData vendorGroup = getVendorGroup(entry, vendorGroups);
			final Set<Integer> entryNumbers = new HashSet<>();
			vendorGroup.getOrderEntries().forEach(e -> entryNumbers.add(e.getEntryNumber()));
			if (!entryNumbers.contains(entry.getEntryNumber()))
			{
				vendorGroup.getOrderEntries().add(entry);
				entry.setEntryGroupNumbers(Collections.singleton(vendorGroup.getGroupNumber()));
			}
		}
	}

	protected EntryGroupData getVendorGroup(final OrderEntryData entry, final Map<String, EntryGroupData> vendorGroups)
	{
		final VendorData vendor = entry.getProduct().getVendor();
		final String vendorCode = vendor.getCode();
		if (vendorGroups.containsKey(vendorCode))
		{
			return vendorGroups.get(vendorCode);
		}
		else
		{
			final EntryGroupData vendorGroup = new EntryGroupData();
			if (MapUtils.isEmpty(vendorGroups))
			{
				vendorGroup.setGroupNumber(2);
			}
			else
			{
				vendorGroup.setGroupNumber(Integer.valueOf(findCurrentMaxGroupNumber(new ArrayList(vendorGroups.values())) + 1));
			}
			vendorGroup.setGroupType(GroupType.VENDOR);
			vendorGroup.setLabel(vendor.getName());
			vendorGroup.setChildren(null);
			vendorGroup.setExternalReferenceId(vendorCode);
			vendorGroup.setOrderEntries(new ArrayList<>());
			vendorGroups.put(vendorCode, vendorGroup);
			return vendorGroup;
		}
	}

	protected EntryGroupData getVendorRootGroup(final Map<String, EntryGroupData> vendorGroups)
	{
		final EntryGroupData vendorRootGroup = new EntryGroupData();
		vendorRootGroup.setGroupType(GroupType.VENDOR);
		vendorRootGroup.setGroupNumber(1);
		vendorRootGroup.setLabel("Vendor Root Group");
		vendorRootGroup.setChildren(new ArrayList(vendorGroups.values()));
		vendorRootGroup.setExternalReferenceId("vendorroot");
		vendorRootGroup.setOrderEntries(new ArrayList<>());
		return vendorRootGroup;
	}

	@Nonnull
	protected List<EntryGroupData> getCurrentNestedGroups(@Nonnull final EntryGroupData root)
	{
		ServicesUtil.validateParameterNotNullStandardMessage("root", root);

		final List<EntryGroupData> result = new ArrayList<>();
		result.add(root);
		for (int i = 0; i < result.size(); i++)
		{
			final List<EntryGroupData> children = result.get(i).getChildren();
			if (children != null)
			{
				result.addAll(children);
			}
		}
		return result;
	}

	protected int findCurrentMaxGroupNumber(final List<EntryGroupData> roots)
	{
		if (roots == null)
		{
			return 0;
		}
		return roots.stream().map(this::getCurrentNestedGroups).flatMap(Collection::stream).map(EntryGroupData::getGroupNumber)
				.max(Integer::compareTo).orElse(0);
	}

	protected VendorOriginalEntryGroupDisplayStrategy getVendorOriginalEntryGroupDisplayStrategy()
	{
		return vendorOriginalEntryGroupDisplayStrategy;
	}

	public void setVendorOriginalEntryGroupDisplayStrategy(
			final VendorOriginalEntryGroupDisplayStrategy vendorOriginalEntryGroupDisplayStrategy)
	{
		this.vendorOriginalEntryGroupDisplayStrategy = vendorOriginalEntryGroupDisplayStrategy;
	}

}

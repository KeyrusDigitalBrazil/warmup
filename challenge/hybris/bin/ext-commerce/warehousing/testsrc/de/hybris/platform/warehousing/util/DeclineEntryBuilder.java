/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.warehousing.util;

import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.warehousing.data.allocation.DeclineEntries;
import de.hybris.platform.warehousing.data.allocation.DeclineEntry;
import de.hybris.platform.warehousing.enums.DeclineReason;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;


/**
 * This is a DeclineEntry builder implementation of the Builder interface
 */
public class DeclineEntryBuilder
{
	public static DeclineEntryBuilder aDecline()
	{
		return new DeclineEntryBuilder();
	}

	public DeclineEntries build_Manual(final Map<ConsignmentEntryModel, Long> declineEntryInfo, final WarehouseModel warehouse,
			DeclineReason reason)
	{

		final DeclineEntries declineEntries = new DeclineEntries();
		final Collection<DeclineEntry> entries = new ArrayList<>();
		declineEntryInfo.forEach((key, value) -> {
			final DeclineEntry entry = new DeclineEntry();
			entry.setConsignmentEntry(key);
			entry.setQuantity(value);
			entry.setReason(reason);
			entry.setNotes("notes");
			entry.setReallocationWarehouse(warehouse);
			entries.add(entry);
		});
		declineEntries.setEntries(entries);
		return declineEntries;
	}

	public DeclineEntries build_Manual(final Map<ConsignmentEntryModel, Long> declineEntryInfo, final WarehouseModel warehouse)
	{
		return build_Manual(declineEntryInfo, warehouse, DeclineReason.TOOBUSY);
	}

	public DeclineEntries build_Auto(final Map<ConsignmentEntryModel, Long> declineEntryInfo)
	{

		return build_Auto(declineEntryInfo, DeclineReason.TOOBUSY);
	}

	public DeclineEntries build_Auto(final Map<ConsignmentEntryModel, Long> declineEntryInfo, final DeclineReason reason)
	{
		final DeclineEntries declineEntries = new DeclineEntries();
		final Collection<DeclineEntry> entries = new ArrayList<>();
		declineEntryInfo.forEach((key, value) -> {
			final DeclineEntry entry = new DeclineEntry();
			entry.setConsignmentEntry(key);
			entry.setQuantity(value);
			entry.setReason(reason);
			entry.setNotes("notes");
			entries.add(entry);
		});
		declineEntries.setEntries(entries);
		return declineEntries;
	}
}

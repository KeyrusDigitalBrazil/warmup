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
package de.hybris.platform.warehousing.allocation.util;

import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.warehousing.data.allocation.DeclineEntry;
import de.hybris.platform.warehousing.enums.DeclineReason;


public class DeclineEntryBuilder
{
	private final DeclineEntry entry;

	private DeclineEntryBuilder()
	{
		entry = new DeclineEntry();
	}

	private DeclineEntry getEntry()
	{
		return this.entry;
	}

	public static DeclineEntryBuilder anEntry()
	{
		return new DeclineEntryBuilder();
	}

	public DeclineEntry build()
	{
		return getEntry();
	}

	public DeclineEntryBuilder withConsignmentEntry(final ConsignmentEntryModel consignmentEntry)
	{
		getEntry().setConsignmentEntry(consignmentEntry);
		return this;
	}

	public DeclineEntryBuilder withQuantity(final Long quantity)
	{
		getEntry().setQuantity(quantity);
		return this;
	}

	public DeclineEntryBuilder withNotes(final String notes)
	{
		getEntry().setNotes(notes);
		return this;
	}

	public DeclineEntryBuilder withReason(final DeclineReason reason)
	{
		getEntry().setReason(reason);
		return this;
	}

	public DeclineEntryBuilder withReallocationWarehouse(final WarehouseModel reallocationWarehouse)
	{
		getEntry().setReallocationWarehouse(reallocationWarehouse);
		return this;
	}
}

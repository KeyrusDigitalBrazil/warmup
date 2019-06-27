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
package de.hybris.platform.commercefacades.order;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;

import java.util.List;
import javax.annotation.Nonnull;


/**
 * Defines how virtual entry groups are created in AbstractOrderData for standalone products.
 */
public interface VirtualEntryGroupStrategy
{
	/**
	 * Assigns the standalone entry to an entry group. The groups can be any of {@code rootGroups}, their children or a new one.
	 * In the last case the implementation should attach the new group to the appropriate parent among {@code rootGroups}.
	 *
	 * @param rootGroups entry group structure. All grouped entries are already in.
	 * @param standaloneEntry a standalone entry
	 */
	void createGroup(@Nonnull List<EntryGroupData> rootGroups, @Nonnull OrderEntryData standaloneEntry);
}

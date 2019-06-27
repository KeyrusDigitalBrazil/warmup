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
package de.hybris.platform.marketplaceservices.strategies;

/**
 * Strategy for read Vendor EntryGroup display setting
 */
public interface VendorOriginalEntryGroupDisplayStrategy
{

	/**
	 * decide whether to display the other EntryGroups besides VendorGroups in marketplace
	 */
	boolean shouldDisplayOriginalEntryGroup();
}

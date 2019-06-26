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

package de.hybris.platform.configurablebundleservices.bundle;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;

import javax.annotation.Nonnull;


/**
 * Checks if T can be removed from the {@link AbstractOrderModel}
 */
public interface RemoveableChecker<T extends AbstractOrderEntryModel>
{
	/**
	 * Test if the {@link AbstractOrderEntryModel} can be removed from the {@link AbstractOrderModel}
	 * 
	 * @param given
	 *           {@link AbstractOrderEntryModel} that is tested
	 * @return <code>true</code> if it can be removed. Else <code>false</code>
	 */
	boolean canRemove(@Nonnull T given);
}

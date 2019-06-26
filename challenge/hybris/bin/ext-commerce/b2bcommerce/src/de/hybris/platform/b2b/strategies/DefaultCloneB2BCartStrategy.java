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
package de.hybris.platform.b2b.strategies;

import de.hybris.platform.order.AbstractOrderEntryTypeService;
import de.hybris.platform.order.strategies.ordercloning.impl.DefaultCloneAbstractOrderStrategy;
import de.hybris.platform.servicelayer.internal.model.impl.ItemModelCloneCreator;
import de.hybris.platform.servicelayer.type.TypeService;

public class DefaultCloneB2BCartStrategy extends DefaultCloneAbstractOrderStrategy {


	public DefaultCloneB2BCartStrategy(final TypeService typeService, final ItemModelCloneCreator
			itemModelCloneCreator,
			final AbstractOrderEntryTypeService abstractOrderEntryTypeService) {
		super(typeService, itemModelCloneCreator, abstractOrderEntryTypeService);
	}
}

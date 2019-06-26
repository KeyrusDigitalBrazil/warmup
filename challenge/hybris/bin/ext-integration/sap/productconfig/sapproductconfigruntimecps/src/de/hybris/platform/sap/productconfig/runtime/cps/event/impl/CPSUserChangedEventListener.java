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
package de.hybris.platform.sap.productconfig.runtime.cps.event.impl;

import de.hybris.platform.servicelayer.event.events.AfterSessionUserChangeEvent;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;


/**
 * @deprecated since 18.11 - obsolete. We do not cache pricing data in the user session cache. So we do not need to
 *             clear it.
 */
@Deprecated
public class CPSUserChangedEventListener extends AbstractEventListener<AfterSessionUserChangeEvent>
{

	/**
	 * @deprecated since 18.11 - obsolete. We do not cache pricing data in the user session cache. So we do not need to
	 *             clear it.
	 */
	@Deprecated
	@Override
	protected void onEvent(final AfterSessionUserChangeEvent evt)
	{
		return;
	}

}

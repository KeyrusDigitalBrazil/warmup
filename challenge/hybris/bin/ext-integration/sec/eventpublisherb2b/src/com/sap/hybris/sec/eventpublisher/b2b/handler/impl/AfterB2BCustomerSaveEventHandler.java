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
package com.sap.hybris.sec.eventpublisher.b2b.handler.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.tx.AfterSaveEvent;

import org.apache.commons.lang3.StringUtils;

import com.sap.hybris.sec.eventpublisher.handler.impl.AfterCustomerSaveEventHandler;


/**
 * Replicate the updated/created customer to target
 */
public class AfterB2BCustomerSaveEventHandler extends AfterCustomerSaveEventHandler
{


	@Override
	public boolean shouldHandle(final AfterSaveEvent event, final ItemModel model) throws Exception {
		if ((model instanceof B2BCustomerModel)) {
			return true;
		} else {
			return super.shouldHandle(event, model);
		}
	}

}

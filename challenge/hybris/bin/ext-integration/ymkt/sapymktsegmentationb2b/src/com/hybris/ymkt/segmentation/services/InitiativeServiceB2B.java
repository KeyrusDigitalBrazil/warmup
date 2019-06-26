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
package com.hybris.ymkt.segmentation.services;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2b.services.B2BUnitService;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


public class InitiativeServiceB2B extends InitiativeService
{
	private static final Logger LOG = LoggerFactory.getLogger(InitiativeServiceB2B.class);

	protected boolean filterOnB2BUnit;
	protected B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2bCustomerService;
	protected B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

	@Override
	public String getInteractionContactId()
	{
		final B2BCustomerModel b2bCustomer = b2bCustomerService.getCurrentB2BCustomer();
		if (filterOnB2BUnit)
		{
			return Optional.ofNullable(b2bUnitService.getParent(b2bCustomer)) //
					.map(B2BUnitModel::getUid) //
					.orElse(super.getInteractionContactId());
		}
		return Optional.ofNullable(b2bCustomer).map(B2BCustomerModel::getCustomerID).orElse(super.getInteractionContactId());
	}

	@Required
	public void setFilterOnB2BUnit(final boolean filterOnB2BUnit)
	{
		LOG.debug("filterOnB2BUnit={}", filterOnB2BUnit);
		this.filterOnB2BUnit = filterOnB2BUnit;
	}

	@Required
	public void setB2bCustomerService(final B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2bCustomerService)
	{
		this.b2bCustomerService = b2bCustomerService;
	}

	@Required
	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}
}

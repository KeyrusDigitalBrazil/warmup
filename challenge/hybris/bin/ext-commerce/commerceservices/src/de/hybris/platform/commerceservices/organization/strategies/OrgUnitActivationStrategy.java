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
package de.hybris.platform.commerceservices.organization.strategies;

import de.hybris.platform.commerceservices.model.OrgUnitModel;


/**
 * Strategy for activating and deactivating an {@link OrgUnitModel}.
 */
public interface OrgUnitActivationStrategy<T extends OrgUnitModel>
{
	/**
	 * Active the given unit.
	 *
	 * @param unit
	 *           the {@link OrgUnitModel} to activate
	 */
	void activateUnit(T unit);

	/**
	 * Deactivate the given unit and all of its child units.
	 *
	 * @param unit
	 *           the {@link OrgUnitModel} to deactivate
	 */
	void deactivateUnit(T unit);
}

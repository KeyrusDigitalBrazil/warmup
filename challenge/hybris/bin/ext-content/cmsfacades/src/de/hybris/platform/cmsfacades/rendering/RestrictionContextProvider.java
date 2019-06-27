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
package de.hybris.platform.cmsfacades.rendering;

import de.hybris.platform.cms2.servicelayer.data.RestrictionData;


/**
 * Interface to set restriction information in the context.
 */
public interface RestrictionContextProvider
{
	/**
	 * Gets the restriction, if any, stored in the context.
	 * @return The restriction stored in the context. Will return null if none is stored.
	 */
	RestrictionData getRestrictionInContext();

	/**
	 * Sets the restriction in the context. Will replace any already stored.
	 * @param restrictionData The restriction to set in the context.
	 */
	void setRestrictionInContext(RestrictionData restrictionData);

	/**
	 * Removes the current restriction in context.
	 */
	void removeRestrictionFromContext();
}

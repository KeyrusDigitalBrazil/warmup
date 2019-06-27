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
package de.hybris.platform.cmsfacades.restrictions;


import de.hybris.platform.cmsfacades.data.RestrictionTypeData;

import java.util.List;


/**
 * Restriction facade interface which deals with methods related to restriction operations.
 */
public interface RestrictionFacade
{

	/**
	 * Find all restriction types.
	 *
	 * @return list of all {@link RestrictionTypeData}; never <code>null</code>
	 */
	List<RestrictionTypeData> findAllRestrictionTypes();

}

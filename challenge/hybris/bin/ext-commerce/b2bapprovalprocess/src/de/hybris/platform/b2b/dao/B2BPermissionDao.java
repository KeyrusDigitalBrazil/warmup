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
package de.hybris.platform.b2b.dao;

import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;


/**
 * A data access object around {@link B2BPermissionModel}
 *
 */
public interface B2BPermissionDao extends GenericDao<B2BPermissionModel>
{
	/**
	 * Retrieves a {@link B2BPermissionModel} by its code.
	 *
	 * @param code
	 *           the code
	 * @return permission matching the code or null if none found
	 */
	B2BPermissionModel findPermissionByCode(final String code);
}

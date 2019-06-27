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
package de.hybris.platform.b2bacceleratorservices.company;

import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;



/**
 * A service for user group management within b2b commerce
 *
 * @deprecated Since 6.0. Use {@link de.hybris.platform.b2b.company.B2BCommerceB2BUserGroupService} instead.
 */
@Deprecated
public interface B2BCommerceB2BUserGroupService extends de.hybris.platform.b2b.company.B2BCommerceB2BUserGroupService
{
	/**
	 * Gets updated permission, after adding permission to given user group
	 *
	 * @param uid
	 *           A unique identifier for {@link B2BUserGroupModel}
	 * @param permission
	 *           Permission that has to be added to user group
	 * @return Updated {@link B2BPermissionModel} object
	 *
	 */
	B2BPermissionModel addPermissionToUserGroup(String uid, String permission);

	/**
	 * Gets updated permission, after removing permission from a given user group
	 *
	 * @param uid
	 *           A unique identifier for {@link B2BUserGroupModel}
	 * @param permission
	 *           Permission that has to be added to user group
	 * @return Updated {@link B2BPermissionModel} object
	 *
	 */
	B2BPermissionModel removePermissionFromUserGroup(String uid, String permission);
}

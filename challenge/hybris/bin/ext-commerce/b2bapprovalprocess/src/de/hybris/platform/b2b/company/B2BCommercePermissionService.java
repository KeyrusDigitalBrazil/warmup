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
package de.hybris.platform.b2b.company;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;


/**
 * A service for permission management within b2b commerce
 */
public interface B2BCommercePermissionService
{
	/**
	 * Gets list of {@link SearchPageData} B2BPermissionModel for pagination given the required pagination parameters
	 * with {@link PageableData}
	 *
	 * @param pageableData
	 *           Pagination information
	 * @return Collection of paginated {@link B2BPermissionModel} objects
	 */
	SearchPageData<B2BPermissionModel> getPagedPermissions(PageableData pageableData);

	/**
	 * Gets {@link B2BPermissionModel} for a given permission code
	 *
	 * @param permissionCode
	 *           A unique identifier for {@link B2BPermissionModel}
	 * @return {@link B2BPermissionModel} object
	 */
	B2BPermissionModel getPermissionForCode(String permissionCode);

	/**
	 * Add permission for a given user and return the updated {@link B2BCustomerModel} object
	 *
	 * @param user
	 *           A unique identifier for {@link B2BCustomerModel} representing a user
	 * @param permission
	 *           A unique identifier for {@link B2BPermissionModel} which is added to the given user
	 * @return Updated {@link B2BPermissionModel} object with permissions added
	 */
	B2BPermissionModel addPermissionToCustomer(String user, String permission);

	/**
	 * Remove permission for a given user and return the updated {@link B2BCustomerModel} object
	 *
	 * @param user
	 *           A unique identifier for {@link B2BCustomerModel} representing a user
	 * @param permission
	 *           A unique identifier for {@link B2BPermissionModel} which is removed from the given user
	 * @return Updated {@link B2BPermissionModel} object with permissions removed
	 */
	B2BPermissionModel removePermissionFromCustomer(String user, String permission);
	
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

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
package de.hybris.platform.b2bapprovalprocessfacades.company;

import de.hybris.platform.b2b.enums.B2BPermissionTypeEnum;
import de.hybris.platform.b2bapprovalprocessfacades.company.data.B2BPermissionData;
import de.hybris.platform.b2bapprovalprocessfacades.company.data.B2BPermissionTypeData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BSelectionData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;

import java.util.Collection;


/**
 * A facade for permission management within b2b commerce
 *
 * @since 6.0
 */
public interface B2BPermissionFacade
{
	/**
	 * Returns paged {@link B2BPermissionData} for the given {@link PageableData}.
	 *
	 * @param pageableData
	 *           {@link PageableData} object defining the paging properties
	 * @return {@link SearchPageData} containing the paged {@link B2BPermissionData}.
	 */
	SearchPageData<B2BPermissionData> getPagedPermissions(PageableData pageableData);

	/**
	 * Returns {@link B2BPermissionData} for a given unique id.
	 *
	 * @param permissionCode
	 *           the code of the permission code
	 * @return {@link B2BPermissionData} for the given uid.
	 */
	B2BPermissionData getPermissionDetails(String permissionCode);

	/**
	 * Returns a collection of available B2B permission types.
	 *
	 * @return {@link Collection} of {@link B2BPermissionTypeData}
	 */
	Collection<B2BPermissionTypeData> getB2BPermissionTypes();

	/**
	 * Updates an existing B2B permission.
	 *
	 * @param b2BPermissionData
	 *           {@link B2BPermissionData} holding the update information.
	 */
	void updatePermissionDetails(B2BPermissionData b2BPermissionData);

	/**
	 * Enable/disable a permission. active set to true denotes enabling permission and vice versa.
	 *
	 * @param permissionCode
	 *           the code of the permission
	 * @param enable
	 *           true to enable the permission, false to disable it
	 */
	void enableDisablePermission(String permissionCode, boolean enable);

	/**
	 * Converts the given {@link B2BPermissionTypeEnum} into a {@link B2BPermissionTypeData} object and returns it.
	 *
	 * @param permissionTypeEnum
	 *           the {@link B2BPermissionTypeEnum} to convert.
	 * @return {@link B2BPermissionTypeData} result of the conversion.
	 */
	B2BPermissionTypeData getB2BPermissionTypeDataForPermission(B2BPermissionTypeEnum permissionTypeEnum);

	/**
	 * Creates a new B2B permission based on the data passed in the {@link B2BPermissionData} parameter.
	 *
	 * @param b2BPermissionData
	 *           {@link B2BPermissionData} object holding the data for the permission to be created.
	 */
	void addPermission(B2BPermissionData b2BPermissionData);

	/**
	 * Gets the list of permissions of the customers.
	 *
	 * @param pageableData
	 *           {@link PageableData} object defining the paging properties
	 * @param customerUid
	 *           the uid of the customer
	 * @return the {@link SearchPageData}
	 */
	SearchPageData<B2BPermissionData> getPagedPermissionsForCustomer(PageableData pageableData, String customerUid);

	/**
	 * Adds a permission to a customer.
	 *
	 * @param customerUid
	 *           the uid of the customer
	 * @param permissionCode
	 *           the code of the permission
	 * @return the {@link B2BSelectionData}
	 */
	B2BSelectionData addPermissionToCustomer(String customerUid, String permissionCode);

	/**
	 * Removes a permission from a customer.
	 *
	 * @param customerUid
	 *           the uid of the customer
	 * @param permissionCode
	 *           the code of the permission
	 * @return Returns the {@link B2BSelectionData}
	 */
	B2BSelectionData removePermissionFromCustomer(String customerUid, String permissionCode);

	/**
	 * Returns a paginated list of permissions associated to a {@link de.hybris.platform.b2b.model.B2BUserGroupModel}.
	 *
	 * @param pageableData
	 *           {@link PageableData} object defining the paging properties
	 * @param userGroupUid
	 *           the uid of the user group
	 * @return a paginated list of permissions
	 */
	SearchPageData<B2BPermissionData> getPagedPermissionsForUserGroup(PageableData pageableData, String userGroupUid);

	/**
	 * Adds a permission to a {@link de.hybris.platform.b2b.model.B2BUserGroupModel}.
	 *
	 * @param userGroupUid
	 *           the uid of the user group
	 * @param permissionCode
	 *           the code of the permission
	 * @return a data object with information about the selected permission
	 */
	B2BSelectionData addPermissionToUserGroup(String userGroupUid, String permissionCode);

	/**
	 * Removes a permission from a {@link de.hybris.platform.b2b.model.B2BUserGroupModel}.
	 *
	 * @param userGroupUid
	 *           the uid of the user group
	 * @param permissionCode
	 *           the code of the permission
	 * @return a data object with information about the deselected permission
	 */
	B2BSelectionData removePermissionFromUserGroup(String userGroupUid, String permissionCode);

}

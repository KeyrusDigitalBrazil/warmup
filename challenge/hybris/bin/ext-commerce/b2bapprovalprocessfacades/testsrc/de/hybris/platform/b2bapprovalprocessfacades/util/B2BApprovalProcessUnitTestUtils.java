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
package de.hybris.platform.b2bapprovalprocessfacades.util;

import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2bapprovalprocessfacades.company.data.B2BPermissionData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.collections.CollectionUtils;


public final class B2BApprovalProcessUnitTestUtils
{

	public static List<B2BPermissionData> getSelectedPermissions(final List<B2BPermissionData> results)
	{
		final List<B2BPermissionData> permissions = new ArrayList<>();
		for (final B2BPermissionData b2bPermissionData : results)
		{
			if (b2bPermissionData.isSelected())
			{
				permissions.add(b2bPermissionData);
			}
		}
		return permissions;
	}

	public static boolean isPermissionIncluded(final Collection<B2BPermissionData> permissions, final String permissionCode)
	{
		return CollectionUtils.find(permissions, new BeanPropertyValueEqualsPredicate(B2BPermissionModel.CODE, permissionCode)) != null;
	}

}

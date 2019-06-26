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
package de.hybris.platform.cockpit.test.mock;

import de.hybris.platform.cockpit.services.impl.SystemServiceImpl;
import de.hybris.platform.core.model.user.UserModel;


public class MockSystemService extends SystemServiceImpl
{

	@Override
	public boolean checkAttributePermissionOn(final String typeCode, final String attributeQualifier, final String permissionCode)
	{
		return true;
	}

	@Override
	public boolean checkPermissionOn(final UserModel user, final String typeCode, final String permissionCode)
	{
		return true;
	}

}

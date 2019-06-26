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
package de.hybris.platform.b2b.dao.impl;

import de.hybris.platform.b2b.dao.B2BPermissionDao;
import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;

import java.util.Collections;
import java.util.List;


public class DefaultB2BPermissionDao extends DefaultGenericDao<B2BPermissionModel> implements B2BPermissionDao
{
	public DefaultB2BPermissionDao()
	{
		super(B2BPermissionModel._TYPECODE);
	}

	@Override
	public B2BPermissionModel findPermissionByCode(final String code)
	{
		final List<B2BPermissionModel> permissions = this.find(Collections.singletonMap(B2BPermissionModel.CODE, code));
		return (permissions.iterator().hasNext() ? permissions.iterator().next() : null);
	}
}

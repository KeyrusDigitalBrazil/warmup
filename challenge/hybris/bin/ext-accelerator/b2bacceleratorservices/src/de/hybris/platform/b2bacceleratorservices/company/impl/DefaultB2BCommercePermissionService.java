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
package de.hybris.platform.b2bacceleratorservices.company.impl;

import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2bacceleratorservices.company.B2BCommercePermissionService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;

import org.apache.commons.lang.NotImplementedException;



/**
 * @deprecated use Since 6.0. {@link de.hybris.platform.b2b.company.impl.DefaultB2BCommercePermissionService} instead.
 */
@Deprecated
public class DefaultB2BCommercePermissionService extends DefaultCompanyB2BCommerceService implements B2BCommercePermissionService
{
	/**
	 * @deprecated use Since 6.0.
	 */
	@Deprecated
	@Override
	public SearchPageData<B2BPermissionModel> getPagedPermissions(final PageableData pageableData)
	{
		return getPagedB2BPermissionDao().find(pageableData);
	}

	@Override

	public B2BPermissionModel addPermissionToUserGroup(final String uid, final String permission)
	{
		throw new NotImplementedException("Method added for backwards compatibility reason.");
	}

	@Override
	public B2BPermissionModel removePermissionFromUserGroup(final String uid, final String permission)
	{
		throw new NotImplementedException("Method added for backwards compatibility reason.");
	}

	/**
	 * Deprecated not-replaced method, maintained only for compatibility reasons. Just raises the
	 * {@link NotImplementedException}.
	 *
	 * @param user
	 *           parameter not used.
	 * @param permission
	 *           parameter not used.
	 * @return B2BPermissionModel
	 *           not implemented in default implementation, will throw NotImplementedException
	 *
	 */
	public B2BPermissionModel removePermissionFromCustomer(final String user, final String permission)
	{
		throw new NotImplementedException("Method added for backwards compatibility reason.");
	}

	@Override
	public B2BPermissionModel addPermissionToCustomer(final String user, final String permission)
	{
		throw new NotImplementedException("Method added for backwards compatibility reason.");
	}
}

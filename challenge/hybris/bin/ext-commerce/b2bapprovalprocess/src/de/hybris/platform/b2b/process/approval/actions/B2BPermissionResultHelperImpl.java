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
package de.hybris.platform.b2b.process.approval.actions;

import de.hybris.platform.b2b.enums.PermissionStatus;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2b.model.B2BPermissionResultModel;
import de.hybris.platform.servicelayer.type.TypeService;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * A helper bean for working with {@link B2BPermissionModel} collections
 */
public class B2BPermissionResultHelperImpl implements B2BPermissionResultHelper
{
	private TypeService typeService;

	@Override
	public Collection<B2BPermissionResultModel> filterResultByPermissionStatus(final Collection<B2BPermissionResultModel> result,
			final PermissionStatus status)
	{
		// filter the Collection
		return CollectionUtils.select(result, new BeanPropertyValueEqualsPredicate(B2BPermissionResultModel.STATUS, status, true));
	}

	@Override
	public List<B2BCustomerModel> getApproversWithPermissionStatus(final Collection<B2BPermissionResultModel> result,
			final PermissionStatus status)
	{
		final Set<B2BCustomerModel> approvers = new HashSet<B2BCustomerModel>();
		final Collection<B2BPermissionResultModel> filteredResultByPermissionStatus = filterResultByPermissionStatus(result, status);
		for (final B2BPermissionResultModel b2bPermissionResultModel : filteredResultByPermissionStatus)
		{
			approvers.add(b2bPermissionResultModel.getApprover());
		}
		return IteratorUtils.toList(approvers.iterator());
	}

	@Override
	public boolean hasOpenPermissionResult(final Collection<B2BPermissionResultModel> permissionResults)
	{
		return null != CollectionUtils.find(permissionResults, new BeanPropertyValueEqualsPredicate(
				B2BPermissionResultModel.STATUS, PermissionStatus.OPEN, true));
	}

	@Override
	public List<Class<? extends B2BPermissionModel>> extractPermissionTypes(
			final Collection<B2BPermissionResultModel> openPermissions)
	{
		final Set<Class<? extends B2BPermissionModel>> permissionsThatNeedApproval = new HashSet<Class<? extends B2BPermissionModel>>();
		for (final B2BPermissionResultModel b2bPermissionResultModel : openPermissions)
		{
			final B2BPermissionModel permission = b2bPermissionResultModel.getPermission();
			if (permission == null)
			{
				final String permissionTypeCode = b2bPermissionResultModel.getPermissionTypeCode();
				permissionsThatNeedApproval.add(getModelClass(permissionTypeCode));
			}
			else
			{
				permissionsThatNeedApproval.add(permission.getClass());
			}
		}
		return IteratorUtils.toList(permissionsThatNeedApproval.iterator());
	}

	/**
	 * Gets the model class based on type code
	 * 
	 * @param permissionTypeCode
	 *           the permission type code
	 * @return the model class
	 */
	protected Class<? extends B2BPermissionModel> getModelClass(final String permissionTypeCode)
	{
		return getTypeService().getModelClass(permissionTypeCode);
	}

	protected TypeService getTypeService()
	{
		return typeService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}
}

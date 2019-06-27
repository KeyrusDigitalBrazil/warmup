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
package de.hybris.platform.b2bacceleratorfacades.company.impl;

import de.hybris.platform.b2b.enums.B2BPermissionTypeEnum;
import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2bacceleratorfacades.company.B2BCommercePermissionFacade;
import de.hybris.platform.b2bapprovalprocessfacades.company.data.B2BPermissionData;
import de.hybris.platform.b2bapprovalprocessfacades.company.data.B2BPermissionTypeData;
import de.hybris.platform.b2bapprovalprocessfacades.company.impl.DefaultB2BPermissionFacade;
import de.hybris.platform.b2bcommercefacades.company.data.B2BSelectionData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.converters.Converters;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;


/**
 * @deprecated Since 6.0. Use {@link DefaultB2BPermissionFacade} instead.
 */
@Deprecated
public class DefaultB2BCommercePermissionFacade extends DefaultCompanyB2BCommerceFacade implements B2BCommercePermissionFacade
{
	@Override
	public SearchPageData<B2BPermissionData> getPagedPermissions(final PageableData pageableData)
	{
		final SearchPageData<B2BPermissionModel> permissions = getB2BCommercePermissionService().getPagedPermissions(pageableData);
		final SearchPageData<B2BPermissionData> searchPageData = convertPageData(permissions, getB2BPermissionConverter());
		return searchPageData;
	}

	@Override
	public List<B2BPermissionTypeData> getB2BPermissionTypes()
	{
		final List<B2BPermissionTypeEnum> permissionTypes = getEnumerationService().getEnumerationValues(
				B2BPermissionTypeEnum._TYPECODE);
		return Converters.convertAll(permissionTypes, getB2BPermissionTypeDataConverter());
	}

	@Override
	public B2BPermissionTypeData getB2BPermissionTypeDataForPermission(final B2BPermissionTypeEnum b2BPermissionTypeEnum)
	{
		return getB2BPermissionTypeDataConverter().convert(b2BPermissionTypeEnum);
	}

	@Override
	public void enableDisablePermission(final String permissionCode, final boolean active)
	{
		final B2BPermissionModel b2BPermissionModel = getCompanyB2BCommerceService().getPermissionForCode(permissionCode);
		if (b2BPermissionModel != null)
		{
			b2BPermissionModel.setActive(Boolean.valueOf(active));
		}
		getCompanyB2BCommerceService().saveModel(b2BPermissionModel);
	}

	@Override
	public void updatePermissionDetails(final B2BPermissionData b2BPermissionData)
	{
		final B2BPermissionModel b2BPermissionModel = getCompanyB2BCommerceService().getPermissionForCode(
				b2BPermissionData.getOriginalCode());
		if (b2BPermissionModel != null)
		{
			getB2BPermissionReversePopulator().populate(b2BPermissionData, b2BPermissionModel);
			getCompanyB2BCommerceService().saveModel(b2BPermissionModel);
		}
	}

	@Override
	public void addPermission(final B2BPermissionData b2BPermissionData)
	{
		final B2BPermissionTypeData b2BPermissionType = b2BPermissionData.getB2BPermissionTypeData();

		final B2BPermissionModel b2BPermissionModel = this.getModelService().create(
				B2BPermissionTypeEnum.valueOf(b2BPermissionType.getCode()).toString());

		if (b2BPermissionModel != null)
		{
			getB2BPermissionReversePopulator().populate(b2BPermissionData, b2BPermissionModel);
			getCompanyB2BCommerceService().saveModel(b2BPermissionModel);
		}
	}

	@Override
	public B2BPermissionData getPermissionDetails(final String uid)
	{
		final B2BPermissionModel b2BPermissionModel = getCompanyB2BCommerceService().getPermissionForCode(uid);
		final B2BPermissionData permissionData = getB2BPermissionConverter().convert(b2BPermissionModel);

		return permissionData;
	}


	/**
	 * Not implemented. This is for backward compatibility. Please use
	 * {@link de.hybris.platform.b2bapprovalprocessfacades.company.impl.DefaultB2BPermissionFacade#getPagedPermissionsForCustomer(PageableData, String)}
	 * .
	 */
	@Override
	public SearchPageData<B2BPermissionData> getPagedPermissionsForCustomer(final PageableData pageableData, final String uid)
	{
		throw new NotImplementedException(
				"Not implemented. Use de.hybris.platform.b2bapprovalprocessfacades.company.impl.DefaultB2BPermissionFacade.getPagedPermissionsForCustomer(PageableData, String).");
	}

	/**
	 * Not implemented. This is for backward compatibility. Please use
	 * {@link de.hybris.platform.b2bapprovalprocessfacades.company.impl.DefaultB2BPermissionFacade#addPermissionToCustomer(String, String)}
	 * .
	 */
	@Override
	public B2BSelectionData addPermissionToCustomer(final String user, final String permission)
	{
		throw new NotImplementedException(
				"Not implemented. Use de.hybris.platform.b2bapprovalprocessfacades.company.impl.DefaultB2BPermissionFacade.addPermissionToCustomer(String, String).");
	}


	/**
	 * Not implemented. This is for backward compatibility. Please use
	 * {@link de.hybris.platform.b2bapprovalprocessfacades.company.impl.DefaultB2BPermissionFacade#removePermissionFromCustomer(String, String)}
	 * .
	 */
	@Override
	public B2BSelectionData removePermissionFromCustomer(final String user, final String permission)
	{
		throw new NotImplementedException(
				"Not implemented. Use de.hybris.platform.b2bapprovalprocessfacades.company.impl.DefaultB2BPermissionFacade.removePermissionFromCustomer(String, String).");
	}

	/**
	 * Not implemented. This is for backward compatibility. Please use
	 * {@link de.hybris.platform.b2bapprovalprocessfacades.company.impl.DefaultB2BPermissionFacade#getPagedPermissionsForUserGroup(PageableData, String)}
	 * .
	 */
	@Override
	public SearchPageData<B2BPermissionData> getPagedPermissionsForUserGroup(final PageableData pageableData,
			final String usergroupUID)
	{
		throw new NotImplementedException(
				"Not implemented. Use de.hybris.platform.b2bapprovalprocessfacades.company.impl.DefaultB2BPermissionFacade.getPagedPermissionsForUserGroup(PageableData, String).");
	}

	/**
	 * Not implemented. This is for backward compatibility. Please use
	 * {@link de.hybris.platform.b2bapprovalprocessfacades.company.impl.DefaultB2BPermissionFacade#addPermissionToUserGroup(String, String)}
	 * .
	 */
	@Override
	public B2BSelectionData addPermissionToUserGroup(final String userGroupUid, final String permission)
	{
		throw new NotImplementedException(
				"Not implemented. Use de.hybris.platform.b2bapprovalprocessfacades.company.impl.DefaultB2BPermissionFacade.addPermissionToUserGroup(String, String).");
	}

	/**
	 * Not implemented. This is for backward compatibility. Please use
	 * {@link de.hybris.platform.b2bapprovalprocessfacades.company.impl.DefaultB2BPermissionFacade#removePermissionFromUserGroup(String, String)}
	 * .
	 */
	@Override
	public B2BSelectionData removePermissionFromUserGroup(final String userGroupUid, final String permission)
	{
		throw new NotImplementedException(
				"Not implemented. Use de.hybris.platform.b2bapprovalprocessfacades.company.impl.DefaultB2BPermissionFacade.removePermissionFromUserGroup(String, String).");
	}
}

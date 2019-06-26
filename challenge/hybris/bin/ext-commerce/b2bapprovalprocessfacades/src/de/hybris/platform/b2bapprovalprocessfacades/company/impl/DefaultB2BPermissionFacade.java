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
package de.hybris.platform.b2bapprovalprocessfacades.company.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.b2b.company.B2BCommerceB2BUserGroupService;
import de.hybris.platform.b2b.company.B2BCommercePermissionService;
import de.hybris.platform.b2b.enums.B2BPermissionTypeEnum;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.b2bapprovalprocessfacades.company.B2BPermissionFacade;
import de.hybris.platform.b2bapprovalprocessfacades.company.data.B2BPermissionData;
import de.hybris.platform.b2bapprovalprocessfacades.company.data.B2BPermissionTypeData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BSelectionData;
import de.hybris.platform.b2bcommercefacades.company.util.B2BCompanyUtils;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.util.CommerceUtils;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.List;

import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link B2BPermissionFacade}
 */
public class DefaultB2BPermissionFacade implements B2BPermissionFacade
{
	private static final String CUSTOMER_UID_PARAM = "customerUid";
	private static final String PAGEABLE_DATA_PARAM = "pageableData";
	private static final String PERMISSION_CODE_PARAM = "permissionCode";
	private static final String PERMISSION_DATA_PARAM = "permissionData";
	private static final String USER_GROUP_UID_PARAM = "userGroupUid";

	private B2BCommercePermissionService b2BCommercePermissionService;
	private B2BCommerceB2BUserGroupService b2BCommerceB2BUserGroupService;
	private EnumerationService enumerationService;
	private ModelService modelService;
	private UserService userService;
	private Converter<B2BPermissionModel, B2BPermissionData> b2BPermissionConverter;
	private Converter<B2BPermissionTypeEnum, B2BPermissionTypeData> b2BPermissionTypeConverter;
	private Converter<B2BPermissionData, B2BPermissionModel> b2BPermissionReverseConverter;
	private Converter<B2BCustomerModel, CustomerData> b2BCustomerConverter;

	@Override
	public SearchPageData<B2BPermissionData> getPagedPermissions(final PageableData pageableData)
	{
		validateParameterNotNullStandardMessage(PAGEABLE_DATA_PARAM, pageableData);

		final SearchPageData<B2BPermissionModel> permissions = getB2BCommercePermissionService().getPagedPermissions(pageableData);
		final SearchPageData<B2BPermissionData> searchPageData = CommerceUtils.convertPageData(permissions,
				getB2BPermissionConverter());
		return searchPageData;
	}

	@Override
	public List<B2BPermissionTypeData> getB2BPermissionTypes()
	{
		final List<B2BPermissionTypeEnum> permissionTypes = getEnumerationService()
				.getEnumerationValues(B2BPermissionTypeEnum._TYPECODE);
		return Converters.convertAll(permissionTypes, getB2BPermissionTypeConverter());
	}

	@Override
	public B2BPermissionTypeData getB2BPermissionTypeDataForPermission(final B2BPermissionTypeEnum permissionType)
	{
		return getB2BPermissionTypeConverter().convert(permissionType);
	}

	@Override
	public void enableDisablePermission(final String permissionCode, final boolean active)
	{
		validateParameterNotNullStandardMessage(PERMISSION_CODE_PARAM, permissionCode);

		final B2BPermissionModel b2BPermissionModel = getB2BCommercePermissionService().getPermissionForCode(permissionCode);
		if (b2BPermissionModel != null)
		{
			b2BPermissionModel.setActive(Boolean.valueOf(active));
		}
		getModelService().save(b2BPermissionModel);
	}

	@Override
	public void updatePermissionDetails(final B2BPermissionData permissionData)
	{
		validateParameterNotNullStandardMessage(PERMISSION_DATA_PARAM, permissionData);

		final B2BPermissionModel permissionModel = getB2BCommercePermissionService()
				.getPermissionForCode(permissionData.getOriginalCode());

		if (permissionModel != null)
		{
			getB2BPermissionReverseConverter().convert(permissionData, permissionModel);
			getModelService().save(permissionModel);
		}
	}

	@Override
	public void addPermission(final B2BPermissionData permissionData)
	{
		validateParameterNotNullStandardMessage(PERMISSION_DATA_PARAM, permissionData);

		final B2BPermissionTypeData permissionType = permissionData.getB2BPermissionTypeData();
		final B2BPermissionModel permissionModel = permissionType == null ? null
				: getModelService().create(B2BPermissionTypeEnum.valueOf(permissionType.getCode()).toString());

		if (permissionModel != null)
		{
			getB2BPermissionReverseConverter().convert(permissionData, permissionModel);
			getModelService().save(permissionModel);
		}
	}

	@Override
	public B2BPermissionData getPermissionDetails(final String permissionCode)
	{
		validateParameterNotNullStandardMessage(PERMISSION_CODE_PARAM, permissionCode);
		final B2BPermissionModel permissionModel = getB2BCommercePermissionService().getPermissionForCode(permissionCode);
		return getB2BPermissionConverter().convert(permissionModel);
	}

	@Override
	public SearchPageData<B2BPermissionData> getPagedPermissionsForCustomer(final PageableData pageableData,
			final String customerUid)
	{
		validateParameterNotNullStandardMessage(PAGEABLE_DATA_PARAM, pageableData);
		validateParameterNotNullStandardMessage(CUSTOMER_UID_PARAM, customerUid);

		final SearchPageData<B2BPermissionModel> permissions = getB2BCommercePermissionService().getPagedPermissions(pageableData);
		final SearchPageData<B2BPermissionData> searchPageData = CommerceUtils.convertPageData(permissions,
				getB2BPermissionConverter());
		final CustomerData customer = getCustomerForUid(customerUid);
		for (final B2BPermissionData permissionData : searchPageData.getResults())
		{
			permissionData.setSelected(CollectionUtils.find(customer.getPermissions(),
					new BeanPropertyValueEqualsPredicate(B2BPermissionModel.CODE, permissionData.getCode())) != null);
		}

		return searchPageData;
	}

	@Override
	public B2BSelectionData addPermissionToCustomer(final String customerUid, final String permissionCode)
	{
		validateParameterNotNullStandardMessage(PERMISSION_CODE_PARAM, permissionCode);
		validateParameterNotNullStandardMessage(CUSTOMER_UID_PARAM, customerUid);

		final B2BPermissionModel permissionModel = getB2BCommercePermissionService().addPermissionToCustomer(customerUid,
				permissionCode);
		return B2BCompanyUtils.createB2BSelectionData(permissionModel.getCode(), true, permissionModel.getActive().booleanValue());
	}

	@Override
	public B2BSelectionData removePermissionFromCustomer(final String customerUid, final String permissionCode)
	{
		validateParameterNotNullStandardMessage(PERMISSION_CODE_PARAM, permissionCode);
		validateParameterNotNullStandardMessage(CUSTOMER_UID_PARAM, customerUid);

		final B2BPermissionModel permissionModel = getB2BCommercePermissionService().removePermissionFromCustomer(customerUid,
				permissionCode);
		return B2BCompanyUtils.createB2BSelectionData(permissionModel.getCode(), false, permissionModel.getActive().booleanValue());
	}

	@Override
	public SearchPageData<B2BPermissionData> getPagedPermissionsForUserGroup(final PageableData pageableData,
			final String userGroupUid)
	{
		validateParameterNotNullStandardMessage(PAGEABLE_DATA_PARAM, pageableData);
		validateParameterNotNullStandardMessage(USER_GROUP_UID_PARAM, userGroupUid);

		final SearchPageData<B2BPermissionModel> permissions = getB2BCommercePermissionService().getPagedPermissions(pageableData);
		final SearchPageData<B2BPermissionData> searchPageData = CommerceUtils.convertPageData(permissions,
				getB2BPermissionConverter());
		final B2BUserGroupModel userGroupModel = getB2BCommerceB2BUserGroupService().getUserGroupForUID(userGroupUid,
				B2BUserGroupModel.class);
		validateParameterNotNull(userGroupModel, String.format("No user group found for uid %s", userGroupUid));
		for (final B2BPermissionData permissionData : searchPageData.getResults())
		{
			permissionData.setSelected(CollectionUtils.find(userGroupModel.getPermissions(),
					new BeanPropertyValueEqualsPredicate(B2BPermissionModel.CODE, permissionData.getCode())) != null);
		}

		return searchPageData;
	}

	@Override
	public B2BSelectionData addPermissionToUserGroup(final String userGroupUid, final String permissionCode)
	{
		validateParameterNotNullStandardMessage(PERMISSION_CODE_PARAM, permissionCode);
		validateParameterNotNullStandardMessage(USER_GROUP_UID_PARAM, userGroupUid);

		final B2BPermissionModel permissionModel = getB2BCommercePermissionService().addPermissionToUserGroup(userGroupUid,
				permissionCode);
		return B2BCompanyUtils.createB2BSelectionData(permissionModel.getCode(), true, permissionModel.getActive().booleanValue());
	}

	@Override
	public B2BSelectionData removePermissionFromUserGroup(final String userGroupUid, final String permissionCode)
	{
		validateParameterNotNullStandardMessage(PERMISSION_CODE_PARAM, permissionCode);
		validateParameterNotNullStandardMessage(USER_GROUP_UID_PARAM, userGroupUid);

		final B2BPermissionModel permissionModel = getB2BCommercePermissionService().removePermissionFromUserGroup(userGroupUid,
				permissionCode);
		return B2BCompanyUtils.createB2BSelectionData(permissionModel.getCode(), false, permissionModel.getActive().booleanValue());
	}

	protected CustomerData getCustomerForUid(final String customerUid)
	{
		return getB2BCustomerConverter().convert(getUserService().getUserForUID(customerUid, B2BCustomerModel.class));
	}

	protected B2BCommercePermissionService getB2BCommercePermissionService()
	{
		return b2BCommercePermissionService;
	}

	@Required
	public void setB2BCommercePermissionService(final B2BCommercePermissionService b2BCommercePermissionService)
	{
		this.b2BCommercePermissionService = b2BCommercePermissionService;
	}

	protected B2BCommerceB2BUserGroupService getB2BCommerceB2BUserGroupService()
	{
		return b2BCommerceB2BUserGroupService;
	}

	@Required
	public void setB2BCommerceB2BUserGroupService(final B2BCommerceB2BUserGroupService b2bCommerceB2BUserGroupService)
	{
		b2BCommerceB2BUserGroupService = b2bCommerceB2BUserGroupService;
	}

	protected Converter<B2BPermissionModel, B2BPermissionData> getB2BPermissionConverter()
	{
		return b2BPermissionConverter;
	}

	@Required
	public void setB2BPermissionConverter(final Converter<B2BPermissionModel, B2BPermissionData> b2bPermissionConverter)
	{
		this.b2BPermissionConverter = b2bPermissionConverter;
	}

	protected Converter<B2BPermissionTypeEnum, B2BPermissionTypeData> getB2BPermissionTypeConverter()
	{
		return b2BPermissionTypeConverter;
	}

	@Required
	public void setB2BPermissionTypeConverter(
			final Converter<B2BPermissionTypeEnum, B2BPermissionTypeData> b2bPermissionTypeConverter)
	{
		this.b2BPermissionTypeConverter = b2bPermissionTypeConverter;
	}

	protected Converter<B2BPermissionData, B2BPermissionModel> getB2BPermissionReverseConverter()
	{
		return b2BPermissionReverseConverter;
	}

	@Required
	public void setB2BPermissionReverseConverter(
			final Converter<B2BPermissionData, B2BPermissionModel> b2bPermissionReverseConverter)
	{
		this.b2BPermissionReverseConverter = b2bPermissionReverseConverter;
	}

	protected EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	@Required
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected Converter<B2BCustomerModel, CustomerData> getB2BCustomerConverter()
	{
		return b2BCustomerConverter;
	}

	@Required
	public void setB2BCustomerConverter(final Converter<B2BCustomerModel, CustomerData> b2bCustomerConverter)
	{
		b2BCustomerConverter = b2bCustomerConverter;
	}
}

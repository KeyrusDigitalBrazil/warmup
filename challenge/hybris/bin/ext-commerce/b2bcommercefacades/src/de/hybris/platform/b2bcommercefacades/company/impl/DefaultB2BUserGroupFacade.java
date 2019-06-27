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
package de.hybris.platform.b2bcommercefacades.company.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.b2b.company.B2BCommerceB2BUserGroupService;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.b2b.company.B2BCommerceUserService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.b2b.strategies.B2BUserGroupsLookUpStrategy;
import de.hybris.platform.b2bcommercefacades.company.B2BUserGroupFacade;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUserGroupData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.UserGroupData;
import de.hybris.platform.commercefacades.util.CommerceUtils;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.List;

import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link B2BUserGroupFacade}
 */
public class DefaultB2BUserGroupFacade implements B2BUserGroupFacade
{
	private static final String USER_GROUP_UID_PARAM = "userGroupUid";

	private B2BCommerceB2BUserGroupService b2BCommerceB2BUserGroupService;
	private B2BCommerceUnitService b2BCommerceUnitService;
	private B2BCommerceUserService b2BCommerceUserService;
	private UserService userService;
	private ModelService modelService;
	private Converter<B2BCustomerModel, CustomerData> b2BUserConverter;
	private Converter<B2BUserGroupModel, B2BUserGroupData> b2BUserGroupConverter;
	private B2BUserGroupsLookUpStrategy b2BUserGroupsLookUpStrategy;

	@Override
	public SearchPageData<CustomerData> getPagedCustomersForUserGroup(final PageableData pageableData, final String userGroupUid)
	{
		final SearchPageData<CustomerData> searchPageData = getPagedUserData(pageableData);
		// update the results with users that already have been selected.
		final UserGroupModel userGroupModel = getB2BCommerceB2BUserGroupService().getUserGroupForUID(userGroupUid,
				UserGroupModel.class);
		validateParameterNotNull(userGroupModel, String.format("No usergroup found for uid %s", userGroupUid));
		for (final CustomerData userData : searchPageData.getResults())
		{
			final UserModel user = getUserService().getUserForUID(userData.getUid());
			userData.setSelected(CollectionUtils.find(user.getGroups(),
					new BeanPropertyValueEqualsPredicate(UserModel.UID, userGroupModel.getUid())) != null);
		}
		return searchPageData;
	}

	@Override
	public void updateUserGroup(final String userGroupUid, final B2BUserGroupData userGroupData)
	{
		B2BUserGroupModel userGroupModel = getB2BCommerceB2BUserGroupService().getUserGroupForUID(userGroupUid,
				B2BUserGroupModel.class);
		if (userGroupModel == null)
		{
			userGroupModel = getModelService().create(B2BUserGroupModel.class);
		}
		userGroupModel.setName(userGroupData.getName());
		userGroupModel.setLocName(userGroupData.getName());
		userGroupModel.setUid(userGroupData.getUid());
		if (userGroupData.getUnit() != null)
		{
			final B2BUnitModel unitModel = getB2BCommerceUnitService().getUnitForUid(userGroupData.getUnit().getUid());
			userGroupModel.setUnit(unitModel);
		}

		modelService.save(userGroupModel);
	}

	@Override
	public void disableUserGroup(final String userGroupUid)
	{
		validateParameterNotNullStandardMessage(USER_GROUP_UID_PARAM, userGroupUid);
		getB2BCommerceB2BUserGroupService().disableUserGroup(userGroupUid);
	}

	@Override
	public void removeUserGroup(final String userGroupUid)
	{
		validateParameterNotNullStandardMessage(USER_GROUP_UID_PARAM, userGroupUid);
		getB2BCommerceB2BUserGroupService().removeUserGroup(userGroupUid);
	}

	@Override
	public SearchPageData<CustomerData> getPagedUserData(final PageableData pageableData)
	{
		final SearchPageData<B2BCustomerModel> customers = getB2BCommerceUserService().getPagedCustomers(pageableData);
		return CommerceUtils.convertPageData(customers, getB2BUserConverter());
	}

	@Override
	public SearchPageData<B2BUserGroupData> getPagedB2BUserGroups(final PageableData pageableData)
	{
		final SearchPageData<B2BUserGroupModel> groups = getB2BCommerceB2BUserGroupService().getPagedB2BUserGroups(pageableData);
		return CommerceUtils.convertPageData(groups, getB2BUserGroupConverter());
	}

	@Override
	public B2BUserGroupData getB2BUserGroup(final String userGroupUid)
	{
		validateParameterNotNullStandardMessage(USER_GROUP_UID_PARAM, userGroupUid);
		final B2BUserGroupModel userGroupModel = getB2BCommerceB2BUserGroupService().getUserGroupForUID(userGroupUid,
				B2BUserGroupModel.class);

		if (userGroupModel != null)
		{
			return getB2BUserGroupConverter().convert(userGroupModel);
		}
		return null;
	}

	@Override
	public CustomerData addMemberToUserGroup(final String userGroupUid, final String userUid)
	{
		validateParameterNotNullStandardMessage(USER_GROUP_UID_PARAM, userGroupUid);
		validateParameterNotNullStandardMessage("userUid", userUid);

		final B2BCustomerModel customerModel = getB2BCommerceB2BUserGroupService().addMemberToUserGroup(userGroupUid, userUid);
		final CustomerData userData = getB2BUserConverter().convert(customerModel);
		userData.setSelected(true);
		return userData;
	}

	@Override
	public CustomerData removeMemberFromUserGroup(final String userGroupUid, final String userUid)
	{
		validateParameterNotNullStandardMessage(USER_GROUP_UID_PARAM, userGroupUid);
		validateParameterNotNullStandardMessage("userUid", userUid);

		final B2BCustomerModel customerModel = getB2BCommerceB2BUserGroupService().removeMemberFromUserGroup(userGroupUid, userUid);
		final CustomerData userData = getB2BUserConverter().convert(customerModel);
		userData.setSelected(false);
		return userData;
	}

	@Override
	public UserGroupData getUserGroupDataForUid(final String userGroupUid)
	{
		final UserGroupModel userGroupModel = getB2BCommerceB2BUserGroupService().getUserGroupForUID(userGroupUid,
				UserGroupModel.class);

		if (userGroupModel == null)
		{
			return null;
		}
		else
		{
			final UserGroupData userGroupData = new UserGroupData();
			userGroupData.setUid(userGroupModel.getUid());
			userGroupData.setName(userGroupModel.getName());
			return userGroupData;
		}
	}

	@Override
	public List<String> getUserGroups()
	{
		return getB2BUserGroupsLookUpStrategy().getUserGroups();
	}

	protected B2BCommerceB2BUserGroupService getB2BCommerceB2BUserGroupService()
	{
		return b2BCommerceB2BUserGroupService;
	}

	@Required
	public void setB2BCommerceB2BUserGroupService(final B2BCommerceB2BUserGroupService b2BCommerceB2BUserGroupService)
	{
		this.b2BCommerceB2BUserGroupService = b2BCommerceB2BUserGroupService;
	}

	protected B2BCommerceUnitService getB2BCommerceUnitService()
	{
		return b2BCommerceUnitService;
	}

	@Required
	public void setB2BCommerceUnitService(final B2BCommerceUnitService b2BCommerceUnitService)
	{
		this.b2BCommerceUnitService = b2BCommerceUnitService;
	}

	protected B2BCommerceUserService getB2BCommerceUserService()
	{
		return b2BCommerceUserService;
	}

	@Required
	public void setB2BCommerceUserService(final B2BCommerceUserService b2BCommerceUserService)
	{
		this.b2BCommerceUserService = b2BCommerceUserService;
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

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected Converter<B2BCustomerModel, CustomerData> getB2BUserConverter()
	{
		return b2BUserConverter;
	}

	@Required
	public void setB2BUserConverter(final Converter<B2BCustomerModel, CustomerData> b2BUserConverter)
	{
		this.b2BUserConverter = b2BUserConverter;
	}

	protected Converter<B2BUserGroupModel, B2BUserGroupData> getB2BUserGroupConverter()
	{
		return b2BUserGroupConverter;
	}

	@Required
	public void setB2BUserGroupConverter(final Converter<B2BUserGroupModel, B2BUserGroupData> b2BUserGroupConverter)
	{
		this.b2BUserGroupConverter = b2BUserGroupConverter;
	}

	protected B2BUserGroupsLookUpStrategy getB2BUserGroupsLookUpStrategy()
	{
		return b2BUserGroupsLookUpStrategy;
	}

	@Required
	public void setB2BUserGroupsLookUpStrategy(final B2BUserGroupsLookUpStrategy b2BUserGroupsLookUpStrategy)
	{
		this.b2BUserGroupsLookUpStrategy = b2BUserGroupsLookUpStrategy;
	}
}

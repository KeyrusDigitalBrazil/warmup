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
import de.hybris.platform.b2b.company.B2BCommerceUserService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.b2bcommercefacades.company.B2BUserFacade;
import de.hybris.platform.b2bcommercefacades.company.data.B2BSelectionData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUserGroupData;
import de.hybris.platform.b2bcommercefacades.company.util.B2BCompanyUtils;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.util.CommerceUtils;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Default implementation of {@link B2BUserFacade}
 */
public class DefaultB2BUserFacade implements B2BUserFacade
{
	private static final String CUSTOMER_UID_PARAM = "customerUid";
	private static final String ROLE_UID_PARAM = "roleUid";
	private static final String USER_GROUP_UID_PARAM = "userGroupUid";
	private static final String USER_UID_PARAM = "userUid";

	private B2BCommerceUserService b2BCommerceUserService;
	private B2BCommerceB2BUserGroupService b2BCommerceB2BUserGroupService;
	private ModelService modelService;
	private UserService userService;
	private Converter<B2BCustomerModel, CustomerData> b2BCustomerConverter;
	private Converter<CustomerData, B2BCustomerModel> b2BCustomerReverseConverter;
	private Converter<B2BUnitModel, B2BUnitData> b2BUnitConverter;
	private Converter<B2BUserGroupModel, B2BUserGroupData> b2BUserGroupConverter;


	@Override
	public SearchPageData<CustomerData> getPagedCustomers(final PageableData pageableData)
	{
		final SearchPageData<B2BCustomerModel> b2bCustomer = getB2BCommerceUserService().getPagedCustomers(pageableData);
		return CommerceUtils.convertPageData(b2bCustomer, getB2BCustomerConverter());
	}

	@Override
	public B2BUnitData getParentUnitForCustomer(final String customerUid)
	{
		Assert.hasText(customerUid, "The field [customerUid] cannot be empty");
		return getB2BUnitConverter().convert(getB2BCommerceUserService().getParentUnitForCustomer(customerUid));
	}

	@Override
	public void updateCustomer(final CustomerData customerData)
	{
		validateParameterNotNullStandardMessage("customerData", customerData);
		Assert.hasText(customerData.getTitleCode(), "The field [TitleCode] cannot be empty");
		Assert.hasText(customerData.getFirstName(), "The field [FirstName] cannot be empty");
		Assert.hasText(customerData.getLastName(), "The field [LastName] cannot be empty");
		B2BCustomerModel customerModel;
		if (StringUtils.isEmpty(customerData.getUid()))
		{
			customerModel = this.getModelService().create(B2BCustomerModel.class);
		}
		else
		{
			customerModel = getUserService().getUserForUID(customerData.getUid(), B2BCustomerModel.class);
		}
		getModelService().save(getB2BCustomerReverseConverter().convert(customerData, customerModel));
	}

	@Override
	public void resetCustomerPassword(final String customerUid, final String updatedPassword)
	{
		validateParameterNotNullStandardMessage(CUSTOMER_UID_PARAM, customerUid);
		validateParameterNotNullStandardMessage("updatedPassword", updatedPassword);

		final B2BCustomerModel customerModel = getUserService().getUserForUID(customerUid, B2BCustomerModel.class);
		getUserService().setPassword(customerModel, updatedPassword, customerModel.getPasswordEncoding());
		getModelService().save(customerModel);
	}

	@Override
	public void disableCustomer(final String customerUid)
	{
		validateParameterNotNullStandardMessage(CUSTOMER_UID_PARAM, customerUid);
		getB2BCommerceUserService().disableCustomer(customerUid);
	}

	@Override
	public void enableCustomer(final String customerUid)
	{
		validateParameterNotNullStandardMessage(CUSTOMER_UID_PARAM, customerUid);
		getB2BCommerceUserService().enableCustomer(customerUid);
	}

	@Override
	public B2BSelectionData removeUserRole(final String userUid, final String roleUid)
	{
		validateParameterNotNullStandardMessage(USER_UID_PARAM, userUid);
		validateParameterNotNullStandardMessage(ROLE_UID_PARAM, roleUid);
		final B2BCustomerModel customerModel = getB2BCommerceUserService().removeUserRole(userUid, roleUid);
		final B2BSelectionData b2BSelectionData = B2BCompanyUtils.createB2BSelectionData(customerModel.getUid(), false,
				customerModel.getActive().booleanValue());
		return B2BCompanyUtils.populateRolesForCustomer(customerModel, b2BSelectionData);
	}

	@Override
	public B2BSelectionData addUserRole(final String user, final String role)
	{
		validateParameterNotNullStandardMessage(USER_UID_PARAM, user);
		validateParameterNotNullStandardMessage(ROLE_UID_PARAM, role);
		final B2BCustomerModel customerModel = getB2BCommerceUserService().addUserRole(user, role);
		final B2BSelectionData b2BSelectionData = B2BCompanyUtils.createB2BSelectionData(customerModel.getUid(), true,
				customerModel.getActive().booleanValue());
		return B2BCompanyUtils.populateRolesForCustomer(customerModel, b2BSelectionData);
	}

	@Override
	public SearchPageData<B2BUserGroupData> getPagedB2BUserGroupsForCustomer(final PageableData pageableData,
			final String customerUid)
	{
		final SearchPageData<B2BUserGroupModel> userGroups = getB2BCommerceB2BUserGroupService()
				.getPagedB2BUserGroups(pageableData);
		final SearchPageData<B2BUserGroupData> searchPageData = CommerceUtils.convertPageData(userGroups,
				getB2BUserGroupConverter());
		final CustomerData customer = this.getCustomerForUid(customerUid);
		validateParameterNotNull(customer, String.format("No customer found for uid %s", customerUid));
		for (final B2BUserGroupData userGroupData : searchPageData.getResults())
		{
			userGroupData.setSelected(CollectionUtils.find(customer.getPermissionGroups(),
					new BeanPropertyValueEqualsPredicate(B2BUserGroupModel.UID, userGroupData.getUid())) != null);
		}
		return searchPageData;
	}

	@Override
	public B2BSelectionData addB2BUserGroupToCustomer(final String customerUid, final String userGroupUid)
	{
		validateParameterNotNullStandardMessage(CUSTOMER_UID_PARAM, customerUid);
		validateParameterNotNullStandardMessage(USER_GROUP_UID_PARAM, userGroupUid);

		final B2BUserGroupModel b2BUserGroupModel = getB2BCommerceUserService().addB2BUserGroupToCustomer(customerUid,
				userGroupUid);
		final B2BSelectionData b2BSelectionData = B2BCompanyUtils.createB2BSelectionData(b2BUserGroupModel.getUid(), true,
				!b2BUserGroupModel.getMembers().isEmpty());
		return b2BSelectionData;
	}

	@Override
	public void removeB2BUserGroupFromCustomerGroups(final String customerUid, final String userGroupUid)
	{
		validateParameterNotNullStandardMessage(CUSTOMER_UID_PARAM, customerUid);
		validateParameterNotNullStandardMessage(USER_GROUP_UID_PARAM, userGroupUid);

		getB2BCommerceUserService().removeB2BUserGroupFromCustomerGroups(customerUid, userGroupUid);
	}

	@Override
	public B2BSelectionData deselectB2BUserGroupFromCustomer(final String customerUid, final String userGroupUid)
	{
		validateParameterNotNullStandardMessage(CUSTOMER_UID_PARAM, customerUid);
		validateParameterNotNullStandardMessage(USER_GROUP_UID_PARAM, userGroupUid);

		final B2BUserGroupModel b2BUserGroupModel = getB2BCommerceUserService().deselectB2BUserGroupFromCustomer(customerUid,
				userGroupUid);
		final B2BSelectionData b2BSelectionData = B2BCompanyUtils.createB2BSelectionData(b2BUserGroupModel.getUid(), false,
				!b2BUserGroupModel.getMembers().isEmpty());
		return b2BSelectionData;
	}

	@Override
	public CustomerData getCustomerForUid(final String customerUid)
	{
		validateParameterNotNullStandardMessage(CUSTOMER_UID_PARAM, customerUid);
		return getB2BCustomerConverter().convert(getUserService().getUserForUID(customerUid, B2BCustomerModel.class));
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

	protected B2BCommerceB2BUserGroupService getB2BCommerceB2BUserGroupService()
	{
		return b2BCommerceB2BUserGroupService;
	}

	@Required
	public void setB2BCommerceB2BUserGroupService(final B2BCommerceB2BUserGroupService b2BCommerceB2BUserGroupService)
	{
		this.b2BCommerceB2BUserGroupService = b2BCommerceB2BUserGroupService;
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

	protected Converter<CustomerData, B2BCustomerModel> getB2BCustomerReverseConverter()
	{
		return b2BCustomerReverseConverter;
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

	@Required
	public void setB2BCustomerReverseConverter(final Converter<CustomerData, B2BCustomerModel> b2BCustomerReverseConverter)
	{
		this.b2BCustomerReverseConverter = b2BCustomerReverseConverter;
	}

	protected Converter<B2BUnitModel, B2BUnitData> getB2BUnitConverter()
	{
		return b2BUnitConverter;
	}

	@Required
	public void setB2BUnitConverter(final Converter<B2BUnitModel, B2BUnitData> b2BUnitConverter)
	{
		this.b2BUnitConverter = b2BUnitConverter;
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
}

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

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.b2b.company.B2BCommerceUserService;
import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BApproverService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2bapprovalprocessfacades.company.B2BApproverFacade;
import de.hybris.platform.b2bcommercefacades.company.data.B2BSelectionData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.b2bcommercefacades.company.util.B2BCompanyUtils;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.util.CommerceUtils;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;

import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link B2BApproverFacade}
 */
public class DefaultB2BApproverFacade implements B2BApproverFacade
{
	private static final String APPROVER_UID_PARAM = "approverUid";
	private static final String CUSTOMER_UID_PARAM = "customerUid";
	private static final String PAGEABLE_DATA_PARAM = "pageableData";
	private static final String UNIT_UID_PARAM = "unitUid";

	private B2BApproverService<B2BCustomerModel> b2bApproverService;
	private B2BUnitService<B2BUnitModel, UserModel> b2bUnitService;
	private B2BCommerceUserService b2BCommerceUserService;
	private UserService userService;
	private Converter<B2BUnitModel, B2BUnitData> b2bUnitConverter;
	private Converter<B2BCustomerModel, CustomerData> b2bUserConverter;
	private Converter<B2BCustomerModel, CustomerData> b2BCustomerConverter;

	/**
	 * @deprecated since 1808, use
	 *             {@link de.hybris.platform.b2bapprovalprocessfacades.company.impl.DefaultB2BApproverFacade#b2bUserConverter}
	 *             instead.
	 */
	@Deprecated
	private Converter<B2BCustomerModel, CustomerData> b2BUserConverter; //NOSONAR

	@Override
	public SearchPageData<CustomerData> getPagedApproversForUnit(final PageableData pageableData, final String unitUid)
	{
		validateParameterNotNullStandardMessage(PAGEABLE_DATA_PARAM, pageableData);
		validateParameterNotNullStandardMessage(UNIT_UID_PARAM, unitUid);

		final SearchPageData<B2BCustomerModel> approvers = getB2bApproverService()
				.findPagedApproversForUnitByGroupMembership(pageableData, unitUid, B2BConstants.B2BAPPROVERGROUP);
		final SearchPageData<CustomerData> searchPageData = CommerceUtils.convertPageData(approvers, getB2bUserConverter());
		// update the results with approvers that already have been selected.
		final B2BUnitData unit = this.getUnitForUid(unitUid);

		if (unit != null)
		{
			for (final CustomerData userData : searchPageData.getResults())
			{
				userData.setSelected(CollectionUtils.find(unit.getApprovers(),
						new BeanPropertyValueEqualsPredicate(B2BCustomerModel.UID, userData.getUid())) != null);
			}
		}

		return searchPageData;
	}

	@Override
	public B2BSelectionData addApproverToUnit(final String unitUid, final String approverUid)
	{
		validateParameterNotNullStandardMessage(UNIT_UID_PARAM, unitUid);
		validateParameterNotNullStandardMessage(APPROVER_UID_PARAM, approverUid);

		final B2BCustomerModel approver = getB2bApproverService().addApproverToUnit(unitUid, approverUid);
		final B2BSelectionData b2BSelectionData = B2BCompanyUtils.createB2BSelectionData(approver.getUid(), true,
				approver.getActive().booleanValue());
		return B2BCompanyUtils.populateRolesForCustomer(approver, b2BSelectionData);
	}

	@Override
	public B2BSelectionData removeApproverFromUnit(final String unitUid, final String approverUid)
	{
		validateParameterNotNullStandardMessage(UNIT_UID_PARAM, unitUid);
		validateParameterNotNullStandardMessage(APPROVER_UID_PARAM, approverUid);

		final B2BCustomerModel approver = getB2bApproverService().removeApproverFromUnit(unitUid, approverUid);
		final B2BSelectionData b2BSelectionData = B2BCompanyUtils.createB2BSelectionData(approver.getUid(), false,
				approver.getActive().booleanValue());
		return B2BCompanyUtils.populateRolesForCustomer(approver, b2BSelectionData);
	}

	@Override
	public SearchPageData<CustomerData> getPagedApproversForCustomer(final PageableData pageableData, final String customerUid)
	{
		validateParameterNotNullStandardMessage(PAGEABLE_DATA_PARAM, pageableData);
		validateParameterNotNullStandardMessage(CUSTOMER_UID_PARAM, customerUid);

		final SearchPageData<B2BCustomerModel> approvers = getB2BCommerceUserService()
				.getPagedCustomersByGroupMembership(pageableData, B2BConstants.B2BAPPROVERGROUP);
		final SearchPageData<CustomerData> searchPageData = CommerceUtils.convertPageData(approvers, getB2bUserConverter());
		// update the results with approvers that already have been selected.
		final CustomerData customer = getCustomerForUid(customerUid);
		for (final CustomerData userData : searchPageData.getResults())
		{
			userData.setSelected(CollectionUtils.find(customer.getApprovers(),
					new BeanPropertyValueEqualsPredicate(B2BCustomerModel.UID, userData.getUid())) != null);
		}

		return searchPageData;
	}

	@Override
	public B2BSelectionData addApproverForCustomer(final String customerUid, final String approverUid)
	{
		validateParameterNotNullStandardMessage(CUSTOMER_UID_PARAM, customerUid);
		validateParameterNotNullStandardMessage(APPROVER_UID_PARAM, approverUid);

		final B2BCustomerModel approverModel = getB2bApproverService().addApproverToCustomer(customerUid, approverUid);
		final B2BSelectionData b2BSelectionData = B2BCompanyUtils.createB2BSelectionData(approverModel.getUid(), true,
				approverModel.getActive().booleanValue());
		return B2BCompanyUtils.populateRolesForCustomer(approverModel, b2BSelectionData);
	}

	@Override
	public B2BSelectionData removeApproverFromCustomer(final String customerUid, final String approverUid)
	{
		validateParameterNotNullStandardMessage(CUSTOMER_UID_PARAM, customerUid);
		validateParameterNotNullStandardMessage(APPROVER_UID_PARAM, approverUid);

		final B2BCustomerModel approverModel = getB2bApproverService().removeApproverFromCustomer(customerUid, approverUid);
		final B2BSelectionData b2BSelectionData = B2BCompanyUtils.createB2BSelectionData(approverModel.getUid(), false,
				approverModel.getActive().booleanValue());
		return B2BCompanyUtils.populateRolesForCustomer(approverModel, b2BSelectionData);
	}

	protected CustomerData getCustomerForUid(final String customerUid)
	{
		return getB2BCustomerConverter().convert(getUserService().getUserForUID(customerUid, B2BCustomerModel.class));
	}

	protected B2BUnitData getUnitForUid(final String unitUid)
	{
		final B2BUnitModel unitModel = getB2bUnitService().getUnitForUid(unitUid);
		return unitModel == null ? null : getB2bUnitConverter().convert(unitModel);
	}

	protected B2BApproverService<B2BCustomerModel> getB2bApproverService()
	{
		return b2bApproverService;
	}

	@Required
	public void setB2bApproverService(final B2BApproverService<B2BCustomerModel> b2bApproverService)
	{
		this.b2bApproverService = b2bApproverService;
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

	protected Converter<B2BCustomerModel, CustomerData> getB2BCustomerConverter()
	{
		return b2BCustomerConverter;
	}

	@Required
	public void setB2BCustomerConverter(final Converter<B2BCustomerModel, CustomerData> b2bCustomerConverter)
	{
		b2BCustomerConverter = b2bCustomerConverter;
	}

	protected Converter<B2BCustomerModel, CustomerData> getB2bUserConverter()
	{
		return b2bUserConverter;
	}

	@Required
	public void setB2bUserConverter(final Converter<B2BCustomerModel, CustomerData> b2bUserConverter)
	{
		this.b2bUserConverter = b2bUserConverter;
	}

	protected B2BUnitService<B2BUnitModel, UserModel> getB2bUnitService()
	{
		return b2bUnitService;
	}

	@Required
	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, UserModel> b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

	protected Converter<B2BUnitModel, B2BUnitData> getB2bUnitConverter()
	{
		return b2bUnitConverter;
	}

	@Required
	public void setB2bUnitConverter(final Converter<B2BUnitModel, B2BUnitData> b2bUnitConverter)
	{
		this.b2bUnitConverter = b2bUnitConverter;
	}

	/**
	 * @deprecated since 1808, use
	 *             {@link de.hybris.platform.b2bapprovalprocessfacades.company.impl.DefaultB2BApproverFacade#getB2bUserConverter()}
	 *             instead.
	 */
	@Deprecated
	protected Converter<B2BCustomerModel, CustomerData> getB2BUserConverter() //NOSONAR
	{
		return b2BUserConverter;
	}

	/**
	 * @deprecated since 1808, use
	 *             {@link de.hybris.platform.b2bapprovalprocessfacades.company.impl.DefaultB2BApproverFacade#setB2bUserConverter(Converter)}
	 *             instead.
	 */
	@Deprecated
	@Required
	public void setB2BUserConverter(final Converter<B2BCustomerModel, CustomerData> b2BUserConverter) //NOSONAR
	{
		this.b2BUserConverter = b2BUserConverter;
	}
}

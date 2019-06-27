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
package de.hybris.platform.b2b.services;

import de.hybris.platform.b2b.model.B2BBudgetModel;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;

import java.util.List;
import java.util.Set;


/**
 * A service around B2B models
 *
 */
public interface B2BItemService
{

	/**
	 * @deprecated Since 4.4. Use {@link B2BCostCenterService#getB2BBudgetForCode(String)} Find b2 b budget by code.
	 *
	 * @param code
	 *           the code
	 * @return the b2 b budget model
	 */
	@Deprecated
	abstract B2BBudgetModel findB2BBudgetByCode(final String code);

	/**
	 * @deprecated Since 4.4. Use {@link B2BCustomerService#getUserForUID(String)}
	 *
	 * @param code
	 *           the code
	 * @return the b2b customer model
	 */
	@Deprecated
	abstract B2BCustomerModel findB2BCustomerByCode(final String code);

	/**
	 * @deprecated Since 4.4. Use {@link de.hybris.platform.servicelayer.user.UserService#getUserGroupForUID(String, Class)}
	 * @param code
	 *           the group code
	 * @return the {@link B2BUserGroupModel}
	 */
	@Deprecated
	abstract B2BUserGroupModel findB2BUserGroupByCode(final String code);

	/**
	 * @deprecated Since 4.4. Use {@link de.hybris.platform.servicelayer.user.UserService#getUserGroupForUID(String, Class)}
	 * @param code
	 *           the group code
	 * @return the {@link PrincipalGroupModel}
	 */
	@Deprecated
	abstract PrincipalGroupModel findPrincipalGroupByCode(final String code);

	/**
	 * @deprecated Since 4.4. Use {@link B2BCostCenterService#getCostCenterForCode(String)}
	 *
	 * @param code
	 *           the code
	 * @return the b2b cost center model
	 */
	@Deprecated
	abstract B2BCostCenterModel findB2BCostCenterByCode(final String code);

	/**
	 * @deprecated Since 4.4. use {@link B2BCostCenterService#getB2BBudgets()}
	 * @return All {@link B2BBudgetModel} to which the user has visibility.
	 */
	@Deprecated
	abstract Set<B2BBudgetModel> findAllB2BBudgets();

	/**
	 * @deprecated Since 4.4. Use {@link de.hybris.platform.b2b.services.B2BCustomerService#getAllUsers()}
	 * @return the {@link Set} of {@link B2BCustomerModel}
	 */
	@Deprecated
	abstract Set<B2BCustomerModel> findAllApprovers();

	/**
	 * @deprecated Since 4.4. {@link de.hybris.platform.b2b.services.B2BCustomerService#getAllB2BUserGroups()}
	 * @return the {@link Set} of {@link B2BUserGroupModel}
	 */
	@Deprecated
	abstract Set<B2BUserGroupModel> findAllB2BUserGroups();

	/**
	 * @deprecated Since 4.4.
	 * @return list of Groups which grant rights to members.
	 */
	@Deprecated
	abstract List<PrincipalGroupModel> findAllRoleGroups();


	/**
	 * @deprecated Since 4.4.
	 * @param model
	 * @return list of Models
	 */
	@Deprecated
	abstract <M extends ItemModel> List<M> findAllItems(final Class<M> model);

	/**
	 *
	 * @deprecated Since 4.4. Use {@link de.hybris.platform.b2b.services.B2BBudgetService#isBudgetExisting(String)} Checks whether
	 *             the budget exists regardless of visibility constraints
	 *
	 * @param code
	 * @return true is budget with this code exists
	 */
	@Deprecated
	boolean budgetExists(final String code);

	/**
	 * Checks whether the cost center exists regardless of visibility constraints
	 *
	 * @deprecated Since 4.4. Use {@link B2BCostCenterService#isCostCenterExisting(String)}
	 * @param code
	 * @return true is budget with this code exists
	 */
	@Deprecated
	boolean costCenterExists(final String code);

}

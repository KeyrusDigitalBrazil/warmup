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
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.user.UserModel;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;


/**
 * Service around the {@link B2BCostCenterModel}.
 *
 * @param <T>
 *           the b2b cost center
 * @param <U>
 *           the customer
 * @spring.bean b2bCostCenterService
 */
public interface B2BCostCenterService<T, U extends UserModel>
{
	/**
	 * Gets all cost centers for given currency by looking at each unit of the branch and their cost centers.
	 *
	 * @param customer
	 *           The logged in b2b customer
	 * @param currency
	 *           The sessions currency
	 * @return A collection of cost centers for the users branch.
	 *
	 * @deprecated As of hybris 4.4, replaced by {@link #getCostCentersForUnitBranch(UserModel, CurrencyModel)}
	 */
	@Deprecated
	List<T> findCostCentersForUnitBranch(final U customer, final CurrencyModel currency);

	/**
	 * Gets all cost centers for given currency by looking at each unit of the branch and their cost centers.
	 *
	 * @param customer
	 *           The logged in b2b customer
	 * @param currency
	 *           The sessions currency
	 * @return A collection of cost centers for the users branch.
	 */
	List<T> getCostCentersForUnitBranch(final U customer, final CurrencyModel currency);

	/**
	 * Gets all cost centers for given currency by looking at each unit of the branch and their cost centers.
	 *
	 * @param unit
	 *           A b2b unit
	 * @param currency
	 *           The sessions currency
	 * @return A collection of cost centers for the users branch
	 *
	 * @deprecated As of hybris 4.4, replaced by {@link #getCostCentersForUnitBranch(B2BUnitModel, CurrencyModel)}
	 */
	@Deprecated
	List<T> findCostCentersForUnitBranch(final B2BUnitModel unit, final CurrencyModel currency);

	/**
	 * Gets all cost centers for given currency by looking at each unit of the branch and their cost centers.
	 *
	 * @param unit
	 *           A b2b unit
	 * @param currency
	 *           The sessions currency
	 * @return A collection of cost centers for the users branch.
	 */
	List<T> getCostCentersForUnitBranch(final B2BUnitModel unit, final CurrencyModel currency);

	/**
	 * Get cost center for a code.
	 *
	 * @param code
	 *           the code
	 * @return the b2b cost center model
	 *
	 * @deprecated As of hybris 4.4, replaced by {@link #getCostCenterForCode(String)}
	 */
	@Deprecated
	T findByCode(final String code);

	/**
	 * Get cost center for a code.
	 *
	 * @param code
	 *           the code
	 * @return the b2b cost center model
	 */
	T getCostCenterForCode(final String code);


	/**
	 * Find available currencies based on a unit and cost centers associated to the unit.
	 *
	 * @param unit
	 *           the unit
	 * @return A collection of currencies available to a unit.
	 *
	 * @deprecated As of hybris 4.4, replaced by {@link #getAvailableCurrencies(B2BUnitModel)}
	 */
	@Deprecated
	Set<CurrencyModel> findAvailableCurrencies(final B2BUnitModel unit);

	/**
	 * Get available currencies based on a unit and cost centers associated to the unit.
	 *
	 * @param unit
	 *           the unit
	 * @return A collection of currencies available to a unit.
	 */
	Set<CurrencyModel> getAvailableCurrencies(final B2BUnitModel unit);

	/**
	 * Find available currencies based on a user
	 *
	 * @param user
	 *           the session user.
	 * @return A collection of currencies available to a unit.
	 *
	 * @deprecated As of hybris 4.4, replaced by {@link #getAvailableCurrencies(UserModel)}
	 */
	@Deprecated
	Set<CurrencyModel> findAvailableCurrencies(UserModel user);

	/**
	 * Get available currencies based on a user
	 *
	 * @param user
	 *           the session user.
	 * @return A collection of currencies available to a unit.
	 */
	Set<CurrencyModel> getAvailableCurrencies(UserModel user);


	/**
	 * @deprecated Since 4.4. Use {@link B2BBudgetService#getCurrentBudgets(de.hybris.platform.b2b.model.B2BCostCenterModel)} Gets
	 *             the current budgets that are active based on a cost center for the session currency
	 *
	 * @param costCenter
	 *           the cost center
	 * @return the current budgets
	 */
	@Deprecated
	Collection<B2BBudgetModel> getCurrentBudgets(final T costCenter);

	/**
	 * Calculates the total cost for a cost center with a data range
	 *
	 * @param costCenter
	 *           the cost center
	 * @param startDate
	 *           the start date
	 * @param endDate
	 *           the end date
	 * @return the total cost
	 */
	Double getTotalCost(final T costCenter, final Date startDate, final Date endDate);

	/**
	 * Find cost centers assigned to a given unit and currency.
	 *
	 * @param unit
	 *           the unit
	 * @param currency
	 *           the currency
	 * @return the list
	 *
	 * @deprecated As of hybris 4.4
	 */
	@Deprecated
	List<T> findCostCenters(final B2BUnitModel unit, final CurrencyModel currency);


	/**
	 * @deprecated Since 4.4. Use {@link B2BCostCenterService#getCostCenterForCode(String)} Returns B2BCostCenterMode of specified
	 *             code. Returns null if none is found.
	 *
	 * @param code
	 * @return B2BCostCenterModel
	 */
	@Deprecated
	B2BCostCenterModel getB2BCostCenterForCode(final String code);

	/**
	 * Find {@link B2BBudgetModel} of specified code. Returns null if none found.
	 *
	 * @param code
	 *           the code
	 * @return the B2BBudget model
	 * @deprecated Since 6.0. Use {@link de.hybris.platform.b2b.services.B2BBudgetService#getB2BBudgetForCode(String)}
	 */
	@Deprecated
	B2BBudgetModel getB2BBudgetForCode(final String code);

	/**
	 * @return {@link B2BBudgetModel} all B2BBudgets to which the caller has visiblity
	 * @deprecated Since 6.0. Use {@link de.hybris.platform.b2b.services.B2BBudgetService#getB2BBudgets()}
	 */
	@Deprecated
	Set<B2BBudgetModel> getB2BBudgets();

	/**
	 *
	 * Checks whether the cost center exists regardless of visibility constraints
	 *
	 * @param code
	 * @return true if budget with this code exists
	 */
	boolean isCostCenterExisting(String code);

	/**
	 * Collects all the cost centers linked to order entries into a single collection.
	 *
	 * @param entries
	 *           Entries of an order
	 * @return A collections of all cost cetners associated to all order entries.
	 */
	Set<B2BCostCenterModel> getB2BCostCenters(final List<AbstractOrderEntryModel> entries);

	/**
	 * Returns all B2B Cost Centers to which the caller has visibility
	 *
	 * @return true if budget with this code exists
	 */
	List<B2BCostCenterModel> getAllCostCenters();

}

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
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.UserModel;

import java.util.Collection;
import java.util.Set;


/**
 * The Interface B2BBudgetService. Service is responsible for providing access to available B2BBudgetModels based on
 * different parameters.
 *
 * @param <B>
 *           an extension of {@link B2BBudgetModel}
 * @param <U>
 *           an extension of {@link UserModel}
 * @spring.bean b2bBudgetService
 */
public interface B2BBudgetService<B extends B2BBudgetModel, U extends UserModel> // NOSONAR
{

	/**
	 * Get {@link B2BBudgetModel} of specified code. Returns null if none found.
	 *
	 * @param code
	 *           the code
	 * @return the B2BBudget model
	 */
	B getB2BBudgetForCode(final String code);

	/**
	 * @return {@link B2BBudgetModel} - all B2BBudgets to which the caller has visibility.
	 */
	Set<B> getB2BBudgets();

	/**
	 * Gets the current budgets that are active for a cost center and have the same currency as the cost center.
	 *
	 * @param costCenter
	 *           the cost center
	 * @return the current budgets
	 */
	Collection<B> getCurrentBudgets(final B2BCostCenterModel costCenter);

	/**
	 * @deprecated Since 6.0. Use {@link #getB2BBudgetForCode(String)} directly (it will return null if budget does not exist).
	 *             Checks whether the budget exists regardless of visibility constraints
	 *
	 * @param code
	 *           the budget's code
	 * @return true is budget with this code exists
	 */
	@Deprecated
	boolean isBudgetExisting(final String code);

	/**
	 * Gets list of {@link SearchPageData} for pagination given the required pagination parameters with
	 * {@link PageableData}
	 *
	 * @param pageableData
	 *           Pagination information
	 * @return List of paginated {@link B2BBudgetModel} objects
	 */
	SearchPageData<B> findPagedBudgets(PageableData pageableData);
}

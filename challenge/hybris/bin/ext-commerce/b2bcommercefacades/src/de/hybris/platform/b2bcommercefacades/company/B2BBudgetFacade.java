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
package de.hybris.platform.b2bcommercefacades.company;

import de.hybris.platform.b2bcommercefacades.company.data.B2BBudgetData;
import de.hybris.platform.b2bcommercefacades.search.SearchFacade;
import de.hybris.platform.b2bcommercefacades.search.data.BudgetSearchStateData;


/**
 * Facade for managing budgets.
 *
 * @since 6.0
 */
public interface B2BBudgetFacade extends SearchFacade<B2BBudgetData, BudgetSearchStateData>
{
	/**
	 * Returns {@link B2BBudgetData} for the given code.
	 *
	 * @param budgetCode
	 *           the budget code
	 * @return {@link B2BBudgetData} holding the budget details
	 */
	B2BBudgetData getBudgetDataForCode(final String budgetCode);

	/**
	 * Adds a new budget based on the given {@link B2BBudgetData}.
	 *
	 * @param budgetData
	 *           {@link B2BBudgetData} holding the details of the budget to create
	 */
	void addBudget(B2BBudgetData budgetData);

	/**
	 * Enables or disables the budget with the given code.
	 *
	 * @param budgetCode
	 *           code of the budget to be enabled/disabled
	 * @param active
	 *           pass <i>true</i> to enable the budget and <i>false</i> to disable it
	 */
	void enableDisableBudget(String budgetCode, boolean active);

	/**
	 * Updates budget details based on the given {@link B2BBudgetData}.
	 *
	 * @param budgetData
	 *           {@link B2BBudgetData} holding the update information
	 */
	void updateBudget(B2BBudgetData budgetData);

}

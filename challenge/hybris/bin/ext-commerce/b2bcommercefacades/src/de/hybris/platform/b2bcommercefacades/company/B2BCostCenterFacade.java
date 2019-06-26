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

import de.hybris.platform.b2bcommercefacades.company.data.B2BCostCenterData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BSelectionData;
import de.hybris.platform.b2bcommercefacades.search.SearchFacade;
import de.hybris.platform.commercefacades.search.data.SearchStateData;

import java.util.List;


/**
 * Facade to handle cost center management.
 *
 * @since 6.0
 */
public interface B2BCostCenterFacade extends SearchFacade<B2BCostCenterData, SearchStateData>
{
	/**
	 * Gets all visible cost centers for the currently logged-in {@link de.hybris.platform.b2b.model.B2BCustomerModel}
	 * based on his parent B2Unit
	 *
	 * @return A collection of {@link de.hybris.platform.b2bcommercefacades.company.data.B2BCostCenterData}
	 */
	List<? extends B2BCostCenterData> getCostCenters(); // NOSONAR

	/**
	 * Gets all visible active cost centers for the currently logged-in
	 * {@link de.hybris.platform.b2b.model.B2BCustomerModel} based on his parent B2Unit
	 *
	 * @return A collection of {@link de.hybris.platform.b2bcommercefacades.company.data.B2BCostCenterData}
	 */
	List<? extends B2BCostCenterData> getActiveCostCenters(); // NOSONAR

	/**
	 * Get view details for a given Cost center code
	 *
	 * @param costCenterCode
	 * @return B2BCostCenterData
	 */
	B2BCostCenterData getCostCenterDataForCode(String costCenterCode);

	/**
	 * Update the cost center details for edit cost centers flow
	 *
	 * @param b2BCostCenterData
	 *           the b2b cost center
	 */
	void updateCostCenter(B2BCostCenterData b2BCostCenterData);

	/**
	 * Add cost center
	 *
	 * @param b2BCostCenterData
	 *           the b2b cost center
	 */
	void addCostCenter(B2BCostCenterData b2BCostCenterData);

	/**
	 * Enable/disable for a cost center. active set to true denotes enabling cost center and vice versa.
	 *
	 * @param costCenterCode
	 * @param active
	 */
	void enableDisableCostCenter(String costCenterCode, boolean active);

	/**
	 * Select a budget for cost center
	 *
	 * @param costCenterCode
	 * @param budgetCode
	 * @return the resulting {@link B2BSelectionData}
	 */
	B2BSelectionData selectBudgetForCostCenter(String costCenterCode, String budgetCode);

	/**
	 * Deselect a budget for a cost center
	 *
	 * @param costCenterCode
	 * @param budgetCode
	 * @return the resulting {@link B2BSelectionData}
	 */
	B2BSelectionData deSelectBudgetForCostCenter(String costCenterCode, String budgetCode);
}
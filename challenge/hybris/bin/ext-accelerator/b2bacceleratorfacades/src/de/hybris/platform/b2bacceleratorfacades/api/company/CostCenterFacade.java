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
package de.hybris.platform.b2bacceleratorfacades.api.company;

import de.hybris.platform.b2bcommercefacades.company.data.B2BCostCenterData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BSelectionData;

import java.util.List;


/**
 * Interface kept for backwards compatibility reasons.
 *
 * @deprecated Since 6.0. Use {@link de.hybris.platform.b2bcommercefacades.company.B2BCostCenterFacade} instead.
 *
 * @since 5.5
 */
@Deprecated
public interface CostCenterFacade extends de.hybris.platform.b2bcommercefacades.company.B2BCostCenterFacade
{
	/**
	 * Gets all visible cost centers for the currently logged-in {@link de.hybris.platform.b2b.model.B2BCustomerModel}
	 * based on his parent B2Unit
	 *
	 * @return A collection of {@link de.hybris.platform.b2bcommercefacades.company.data.B2BCostCenterData}
	 */
	List<? extends B2BCostCenterData> getCostCenters();

	/**
	 * Gets all visible active cost centers for the currently logged-in
	 * {@link de.hybris.platform.b2b.model.B2BCustomerModel} based on his parent B2Unit
	 *
	 * @return A collection of {@link de.hybris.platform.b2bcommercefacades.company.data.B2BCostCenterData}
	 */
	List<? extends B2BCostCenterData> getActiveCostCenters();

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
	 * @param b2BCostCenterData {@link B2BCostCenterData}
	 */
	void updateCostCenter(B2BCostCenterData b2BCostCenterData);

	/**
	 * Add cost center
	 *
	 * @param b2BCostCenterData {@link B2BCostCenterData}
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
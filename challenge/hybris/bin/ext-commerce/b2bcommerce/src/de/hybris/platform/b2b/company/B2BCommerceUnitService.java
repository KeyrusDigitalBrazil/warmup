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
package de.hybris.platform.b2b.company;

import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.AddressModel;

import java.util.Collection;


/**
 * A service for unit management within b2b commerce
 */
public interface B2BCommerceUnitService
{
	/**
	 * A collection of business units based on root unit of an organization to which the parent business unit of the
	 * currently logged in customer belongs to
	 *
	 * @return A collection of units where the root unit is the parent business unit of the currently logged in customer.
	 */
	Collection<? extends B2BUnitModel> getOrganization();

	/**
	 * A branch of business units based on the parent unit of the current session user.
	 *
	 * @return A collection of units where the root unit is the parent business unit of the currently logged in customer.
	 */
	Collection<? extends B2BUnitModel> getBranch();

	/**
	 * Gets a Root unit of the organization based on the parent business unit of the session user
	 *
	 * @see #getParentUnit()
	 * @return A root unit of an organization a session customer belongs to
	 */
	<T extends B2BUnitModel> T getRootUnit();

	/**
	 * Gets a parent unit of the current session user
	 *
	 * @return The business unit assigned to the current session user.
	 */
	<T extends B2BUnitModel> T getParentUnit();

	/**
	 * Assign a parent unit to unitModel
	 *
	 * @param unitModel
	 *           A unit to assign a parent for {@link B2BUnitModel}
	 * @param parentUnit
	 *           The parent unit {@link B2BUnitModel}
	 */
	void setParentUnit(B2BUnitModel unitModel, B2BUnitModel parentUnit);

	/**
	 * Get all the units of a organization for current logged in user
	 *
	 * @return Collection of units for the organization of the current user
	 */
	Collection<? extends B2BUnitModel> getAllUnitsOfOrganization();

	/**
	 * Gets all the allowed parent units for a given {@link B2BUnitModel}
	 *
	 * @param unit
	 *           A unique identifier for a unit
	 * @return A collection of {@link B2BUnitModel} for the given uid
	 */
	Collection<? extends B2BUnitModel> getAllowedParentUnits(B2BUnitModel unit);

	/**
	 * Updates the branch collection in the session for the current user. Should be called after a new unit creation so
	 * that its does not get filter out by the unit branch search restriction.
	 */
	void updateBranchInSession();

	/**
	 * Disable unit based on the given uid
	 *
	 * @param uid
	 *           A unique identifier of {@link B2BUnitModel}
	 */
	void disableUnit(String uid);

	/**
	 * Enable unit based on the given uid
	 *
	 * @param unit
	 *           A unique identifier of {@link B2BUnitModel}
	 */
	void enableUnit(String unit);

	/**
	 * Gets parent unit based on the given unit
	 *
	 * @param unit
	 *           A {@link B2BUnitModel} object
	 * @return A {@link B2BUnitModel} object which denotes the parent unit of the given unit
	 */
	<T extends B2BUnitModel> T getParentUnit(B2BUnitModel unit);

	/**
	 * Removes the address from a given unit
	 *
	 * @param unitUid
	 *           A unique identifier of {@link B2BUnitModel}
	 * @param addressId
	 *           A unique identifier of {@link AddressModel}
	 */
	void removeAddressEntry(String unitUid, String addressId);

	/**
	 * Sets a given address for a unit
	 *
	 * @param unitForUid
	 *           A unique identifier of {@link B2BUnitModel}
	 * @param addressModel
	 *           {@link AddressModel} object which is getting added to unit
	 */
	void saveAddressEntry(B2BUnitModel unitForUid, AddressModel addressModel);

	/**
	 * Gets a {@link AddressModel} object for a given unit
	 *
	 * @param unit
	 *           A unique identifier of {@link B2BUnitModel}
	 * @param id
	 *           A unique identifier of {@link AddressModel}
	 * @return {@link AddressModel} object
	 */
	AddressModel getAddressForCode(B2BUnitModel unit, String id);

	/**
	 * Save updated {@link AddressModel} object to a unit
	 *
	 * @param unitModel
	 *           A unique identifier of {@link B2BUnitModel}
	 * @param addressModel
	 *           {@link AddressModel} object for given unit
	 */
	void editAddressEntry(B2BUnitModel unitModel, AddressModel addressModel);

	/**
	 * Gets list of {@link SearchPageData} {@link B2BCustomerModel} for a given unit for pagination provided with
	 * required pagination parameters with {@link PageableData}
	 *
	 * @param pageableData
	 *           Pagination information
	 * @param unit
	 *           A unique identifier of {@link B2BUnitModel}
	 * @return Collection of paginated {@link B2BCostCenterModel} objects
	 */
	SearchPageData<B2BCustomerModel> getPagedUsersForUnit(PageableData pageableData, String unit);

	/**
	 * Gets the unit for uid.
	 *
	 * @param unitUid
	 *           the unit uid
	 * @return the unit for uid
	 */
	B2BUnitModel getUnitForUid(String unitUid);
}

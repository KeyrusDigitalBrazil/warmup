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

import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitNodeData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.UserGroupModel;

import java.util.List;


/**
 * Facade to handle operations over B2BUnit and related types.
 *
 * @since 6.0
 */
public interface B2BUnitFacade
{

	/**
	 * Get a list of customers associated with a give unit.
	 *
	 * @param pageableData
	 * @param unitUid
	 *           A uid of a {@link de.hybris.platform.b2b.model.B2BUnitModel}
	 * @return A paginated list of customers.
	 */
	SearchPageData<CustomerData> getPagedCustomersForUnit(PageableData pageableData, String unitUid);

	/**
	 * Get a list of administrators associated with a give unit.
	 *
	 * @param pageableData
	 * @param unitUid
	 *           A uid of a {@link de.hybris.platform.b2b.model.B2BUnitModel}
	 * @return A paginated list of customers.
	 */
	SearchPageData<CustomerData> getPagedAdministratorsForUnit(PageableData pageableData, String unitUid);

	/**
	 * Get a list of managers associated with a give unit.
	 *
	 * @param pageableData
	 * @param unitUid
	 *           A uid of a {@link de.hybris.platform.b2b.model.B2BUnitModel}
	 * @return A paginated list of customers.
	 */
	SearchPageData<CustomerData> getPagedManagersForUnit(PageableData pageableData, String unitUid);

	/**
	 * Disables a unit based on a uid of a {@link de.hybris.platform.b2b.model.B2BUnitModel}
	 *
	 * @param unitUid
	 */
	void disableUnit(String unitUid);

	/**
	 * Enable a Business Unit that is not active
	 *
	 * @param unitUid
	 *           A unitUid uid
	 */
	void enableUnit(String unitUid);

	/**
	 * Gets the business unit assigned to the current session user with all the children retrieved via
	 * {@link de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData#getChildren()}.
	 *
	 * @return the business unit assigned to the session customer
	 */
	B2BUnitData getParentUnit();

	/**
	 * Gets a business unit as a B2BUnitNodeData assigned to the current session user with all the children retrieved via
	 * {@link de.hybris.platform.b2bcommercefacades.company.data.B2BUnitNodeData#getChildren()} which only has enough
	 * data to construct a tree view.
	 *
	 * @return A business unit assigned to the session customer
	 */
	B2BUnitNodeData getParentUnitNode();

	/**
	 * A list of parent units for which the unit with <param>uid</param> can be assigned as a sibling
	 *
	 * @param unitUid
	 *           An id of a {@link de.hybris.platform.b2b.model.B2BUnitModel}
	 * @return A list of parent units that a given unit can be a child of
	 */
	List<B2BUnitNodeData> getAllowedParentUnits(String unitUid);


	/**
	 * Get All units of organization which are enabled.
	 *
	 * @return A collection of B2BUnit uids.
	 */
	List<String> getAllActiveUnitsOfOrganization();

	/**
	 * Get a list of customers directly associated to the unit plus all the customers who are members of given list of
	 * usergroups with the visible branch for the current session user. A list of {@link UserGroupModel#getUid()}
	 *
	 * @param pageableData
	 *           Pagination data
	 * @param unitUid
	 *           A unit UID
	 *
	 * @return A paginated list of {@link CustomerData}
	 */
	SearchPageData<CustomerData> getPagedUserDataForUnit(PageableData pageableData, String unitUid);

	/**
	 * Associates an address to a business unit
	 *
	 * @param newAddress
	 *           Address data object
	 * @param unitUid
	 *           A unit uid
	 */
	void addAddressToUnit(AddressData newAddress, String unitUid);

	/**
	 * Remove an address from a unit
	 *
	 * @param unitUid
	 * @param addressId
	 */
	void removeAddressFromUnit(String unitUid, String addressId);

	/**
	 * Edit address of a unit
	 *
	 * @param newAddress
	 *           Address data
	 * @param unitUid
	 *           A unit UID
	 */
	void editAddressOfUnit(AddressData newAddress, String unitUid);

	/**
	 * Updates {@link de.hybris.platform.b2b.model.B2BUnitModel} based on unit data if param originalUid is null the new
	 * unit is created
	 *
	 * @param originalUid
	 *           the uid of {@link de.hybris.platform.b2b.model.B2BUnitModel} to update.
	 * @param unit
	 *           A unit data object
	 */
	void updateOrCreateBusinessUnit(String originalUid, B2BUnitData unit);

	/**
	 * Gets a list of {@link B2BUnitNodeData} representing each unit in the branch based on the session customer
	 *
	 * @return A list of units in the branch.
	 */
	List<B2BUnitNodeData> getBranchNodes();

	/**
	 * Gets a {@link B2BUnitData} given its uid.
	 *
	 * @param unitUid
	 * @return The unit.
	 */
	B2BUnitData getUnitForUid(final String unitUid);
}

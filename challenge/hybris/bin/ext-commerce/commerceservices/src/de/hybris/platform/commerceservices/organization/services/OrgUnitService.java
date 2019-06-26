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
package de.hybris.platform.commerceservices.organization.services;

import de.hybris.platform.commerceservices.model.OrgUnitModel;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.EmployeeModel;

import java.util.Optional;
import java.util.Set;


/**
 * Service interface for organizational unit related operations.
 *
 * @since 6.1
 */
public interface OrgUnitService
{
	/**
	 * Create a new organizational unit.
	 *
	 * @param parameter
	 *           Extensible {@link OrgUnitParameter} bean containing the initial attributes of the organizational unit to
	 *           be created. Mandatory values:
	 *           <ul>
	 *           <li>{@link OrgUnitParameter#uid}</li>
	 *           <li>{@link OrgUnitParameter#name}</li>
	 *           <li>{@link OrgUnitParameter#active}</li>
	 *           </ul>
	 */
	void createUnit(OrgUnitParameter parameter);

	/**
	 * Update an existing organizational unit.
	 *
	 * @param parameter
	 *           Extensible {@link OrgUnitParameter} bean containing the initial attributes of the organizational unit to
	 *           be updated. Mandatory values:
	 *           <ul>
	 *           <li>{@link OrgUnitParameter#orgUnit}</li>
	 *           </ul>
	 */
	void updateUnit(OrgUnitParameter parameter);

	/**
	 * Get the organizational unit with the given uid.
	 *
	 * @param uid
	 *           the uid of the organizational unit
	 * @return An {@link Optional} which
	 *         <ul>
	 *         <li>contains the {@link OrgUnitModel} for the given uid if it exists and</li>
	 *         <li>is empty otherwise.</li>
	 *         </ul>
	 */
	Optional<OrgUnitModel> getUnitForUid(String uid);

	/**
	 * Activate the organizational unit with the given orgUnit.
	 *
	 * @param orgUnit
	 *           the organizational unit
	 */
	void activateUnit(OrgUnitModel orgUnit);

	/**
	 * Deactivate the organizational unit and all of its child units with the given orgUnit.
	 *
	 * @param orgUnit
	 *           the organizational unit
	 */
	void deactivateUnit(OrgUnitModel orgUnit);

	/**
	 * Add one or more members to a organizational unit.
	 *
	 * @param parameter
	 *           Extensible {@link OrgUnitMemberParameter} bean holding the method parameters. Mandatory values:
	 *           <ul>
	 *           <li>{@link OrgUnitMemberParameter#uid}</li>
	 *           <li>{@link OrgUnitMemberParameter#type}</li>
	 *           <li>{@link OrgUnitMemberParameter#members}</li>
	 *           </ul>
	 */
	void addMembers(OrgUnitMemberParameter parameter);

	/**
	 * Remove one or more members from a organizational unit.
	 *
	 * @param parameter
	 *           Extensible {@link OrgUnitMemberParameter} bean holding the method parameters. Mandatory values:
	 *           <ul>
	 *           <li>{@link OrgUnitMemberParameter#uid}</li>
	 *           <li>{@link OrgUnitMemberParameter#type}</li>
	 *           <li>{@link OrgUnitMemberParameter#members}</li>
	 *           </ul>
	 */
	void removeMembers(OrgUnitMemberParameter parameter);

	/**
	 * Get a paged search result for members of the given organizational unit.
	 *
	 * @param parameter
	 *           Extensible {@link OrgUnitMemberParameter} bean holding the method parameters. Mandatory values:
	 *           <ul>
	 *           <li>{@link OrgUnitMemberParameter#uid}</li>
	 *           <li>{@link OrgUnitMemberParameter#type}</li>
	 *           <li>{@link OrgUnitMemberParameter#members}</li>
	 *           </ul>
	 * @return {@link SearchPageData} containing the paged search result for members of the given organizational unit
	 */
	<T extends PrincipalModel> SearchPageData<T> getMembers(OrgUnitMemberParameter<T> parameter);

	/**
	 * Gets the parent unit of a unit.
	 *
	 * @param orgUnit
	 *           the organizational unit
	 * @return An {@link Optional} which
	 *         <ul>
	 *         <li>contains the parent {@link OrgUnitModel} for the given unit's uid if it exists and</li>
	 *         <li>is empty if <code>unit</code> was the root unit.</li>
	 *         </ul>
	 */
	Optional<OrgUnitModel> getParent(OrgUnitModel orgUnit);


	/**
	 * Get a list of user roles that the given employee belongs to
	 *
	 * @param employee
	 *           the employee model to search its belonging user roles
	 * @return a list of user group which represents user role
	 */
	Set<PrincipalGroupModel> getRolesForEmployee(final EmployeeModel employee);

}

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
import de.hybris.platform.commerceservices.organization.services.impl.OrgUnitHierarchyException;


/**
 * Service interface for generating and updating the value of the <b><code>path</code></b> attribute of
 * {@link OrgUnitModel} objects. The <b><code>path</code></b> attribute of an {@link OrgUnitModel} is a flat
 * {@link String} representation of the path of traversal to reach the unit from the root of its organization and
 * contains the UIDs of all units on this path separated by a delimiter.
 */
public interface OrgUnitHierarchyService
{
	/**
	 * Generates the <b><code>path</code></b> value of all {@link OrgUnitModel} objects of the given unit type. The
	 * process starts with the root units, i.e. the ones that don't have a parent of the same type, and will traverse the
	 * hierarchy tree for each of them. All changes to affected items are persisted.
	 *
	 * @param unitType
	 *           Determines the type to generate <code>path</code> values for. Sub-types will be ignored. May not be
	 *           null.
	 *
	 * @throws IllegalArgumentException
	 *            In case the passed unitType is null.
	 * @throws OrgUnitHierarchyException
	 *            In case inconsistencies are discovered during the path generation process or the process failed for
	 *            other reasons.
	 */
	<T extends OrgUnitModel> void generateUnitPaths(Class<T> unitType);

	/**
	 * Saves all changes to the given {@link OrgUnitModel} and updates its <b><code>path</code></b> value as well as the
	 * <b><code>path</code></b> values for all its descendants of the same type subsequently. All changes to affected
	 * items are persisted.<br>
	 * <br>
	 * This method should be called whenever changes to the unit include a change of its parent unit. Implementations
	 * have to make sure that all changes are rolled back in case the creation of path values fails.
	 *
	 * @param unit
	 *           The {@link OrgUnitModel} to update. May not be null.
	 *
	 * @throws IllegalArgumentException
	 *            In case the passed unit is null.
	 * @throws OrgUnitHierarchyException
	 *            In case inconsistencies are discovered during the path generation process or the process failed for
	 *            other reasons.
	 */
	void saveChangesAndUpdateUnitPath(OrgUnitModel unit);
}

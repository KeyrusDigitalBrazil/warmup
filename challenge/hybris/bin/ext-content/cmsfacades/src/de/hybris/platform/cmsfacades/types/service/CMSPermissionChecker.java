/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cmsfacades.types.service;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.servicelayer.security.permissions.PermissionsConstants;


/**
 * Helper service to check permissions
 */
public interface CMSPermissionChecker
{
	/**
	 * Type permission checking to see if the current principal has permissions to perform the specified operation (e.g.
	 * read, change, create or remove) on the type of an attribute or not.
	 * <ul>
	 * <li>When the attribute is of type ComposedTypeModel, this method verifies that the type specified in the
	 * ComposedTypeModel is manageable by the current user.
	 * <li>When the attribute is of type AtomicTypeModel, no additional type checking needs to be done. This will always
	 * return {@code TRUE}.
	 * </ul>
	 * <p>
	 * Possible scenarios are listed in the table below:
	 * <p>
	 * <table>
	 * <tr>
	 * <th>Sample Attribute</th>
	 * <th>Attribute</th>
	 * <th>Expected Result</th>
	 * </tr>
	 * <tr>
	 * <td>{@code String name}</td>
	 * <td>Atomic type</td>
	 * <td>always {@code TRUE}</td>
	 * </tr>
	 * <tr>
	 * <td>{@code Media media}</td>
	 * <td>Composed type</td>
	 * <td>{@code TRUE} when user has permission for Media type</td>
	 * </tr>
	 * <tr>
	 * <td>{@code List<MediaModel> media}</td>
	 * <td>Collection of Composed type</td>
	 * <td>{@code TRUE} when user has permission for Media type</td>
	 * </tr>
	 * <tr>
	 * <td>{@code Map<String, MediaModel> media}</td>
	 * <td>Localized Composed type</td>
	 * <td>{@code TRUE} when user has permission for Media type</td>
	 * </tr>
	 * <tr>
	 * <td>{@code Map<String, List<MediaModel>> media}</td>
	 * <td>Localized Collection of Composed type</td>
	 * <td>{@code TRUE} when user has permission for Media type</td>
	 * </tr>
	 * </table>
	 *
	 * @param attribute
	 *           - The descriptor that specifies the attribute whose read permission to check.
	 * @param permissionName
	 *           - The name of the permission to be checked; valid values defined in {@link PermissionsConstants}
	 * @return true if the current principal has been granted change permission on the type contained in the
	 *         non-primitive attribute or the attribute is of type {@code AtomicTypeModel}; false otherwise.
	 */
	public boolean hasPermissionForContainedType(final AttributeDescriptorModel attributeDescriptorModel, String permissionName);
}

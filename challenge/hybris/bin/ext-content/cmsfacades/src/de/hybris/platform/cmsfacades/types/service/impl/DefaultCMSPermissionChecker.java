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
package de.hybris.platform.cmsfacades.types.service.impl;

import de.hybris.platform.cms2.exceptions.TypePermissionException;
import de.hybris.platform.cmsfacades.types.service.CMSAttributeTypeService;
import de.hybris.platform.cmsfacades.types.service.CMSPermissionChecker;
import de.hybris.platform.core.model.enumeration.EnumerationMetaTypeModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.servicelayer.security.permissions.PermissionCRUDService;
import de.hybris.platform.servicelayer.security.permissions.PermissionsConstants;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link CMSPermissionChecker}
 */
public class DefaultCMSPermissionChecker implements CMSPermissionChecker
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCMSPermissionChecker.class);

	private PermissionCRUDService permissionCRUDService;
	private CMSAttributeTypeService cmsAttributeTypeService;

	/**
	 * Method that returns true if the attribute type is not applicable for permission checking.
	 *
	 * Currently type {@link MediaModel} is not applicable for type permission checking.
	 */
	protected boolean isAttributeTypeBlacklisted(final TypeModel attributeType)
	{
		return ((ComposedTypeModel) attributeType).getCode().equals(MediaModel._TYPECODE);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws TypePermissionException
	 *            when principal cannot {@code READ} the type contained in a required attribute.
	 */
	@Override
	public boolean hasPermissionForContainedType(final AttributeDescriptorModel attribute, final String permissionName)
	{

		final TypeModel attributeType = getCmsAttributeTypeService().getAttributeContainedType(attribute);
		final boolean isComposedType = ComposedTypeModel.class.isAssignableFrom(attributeType.getClass());
		final boolean isEnumType = EnumerationMetaTypeModel.class.isAssignableFrom(attributeType.getClass());
		boolean hasPermission = true;
		if (isComposedType && !isEnumType && Objects.nonNull(permissionName))
		{

			if (isAttributeTypeBlacklisted(attributeType))
			{
				return true;
			}

			switch (permissionName)
			{
				case PermissionsConstants.READ:
					hasPermission = getPermissionCRUDService().canReadType((ComposedTypeModel) attributeType);
					if (!attribute.getOptional() && !hasPermission)
					{
						final String errorMessage = String.format(
								"Current principal has no %s type permission for %s, which is needed by the required attribute %s of %s type.",
								permissionName, attributeType.getCode(), attribute.getQualifier(),
								attribute.getEnclosingType().getCode());

						LOGGER.info(errorMessage);
						throw new TypePermissionException(errorMessage);
					}
					break;
				case PermissionsConstants.CHANGE:
					hasPermission = getPermissionCRUDService().canChangeType((ComposedTypeModel) attributeType);
					break;
				case PermissionsConstants.CREATE:
					hasPermission = getPermissionCRUDService().canCreateTypeInstance((ComposedTypeModel) attributeType);
					break;
				case PermissionsConstants.REMOVE:
					hasPermission = getPermissionCRUDService().canRemoveTypeInstance((ComposedTypeModel) attributeType);
					break;
				default:
					hasPermission = true;
					break;
			}
		}
		return hasPermission;
	}

	protected PermissionCRUDService getPermissionCRUDService()
	{
		return permissionCRUDService;
	}

	@Required
	public void setPermissionCRUDService(final PermissionCRUDService permissionCRUDService)
	{
		this.permissionCRUDService = permissionCRUDService;
	}

	protected CMSAttributeTypeService getCmsAttributeTypeService()
	{
		return cmsAttributeTypeService;
	}

	@Required
	public void setCmsAttributeTypeService(final CMSAttributeTypeService typeService)
	{
		this.cmsAttributeTypeService = typeService;
	}
}

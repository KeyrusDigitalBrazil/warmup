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
package de.hybris.platform.acceleratorfacades.cmsitems.attributeconverters;

import static java.util.stream.Collectors.toMap;

import de.hybris.platform.cms2.common.exceptions.PermissionExceptionUtils;
import de.hybris.platform.cms2.common.functions.Converter;
import de.hybris.platform.cms2.exceptions.TypePermissionException;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.security.permissions.PermissionCRUDService;
import de.hybris.platform.servicelayer.security.permissions.PermissionsConstants;

import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Required;


/**
 * Attribute Converter for {@link MediaContainerModel}
 */
public class MediaContainerAttributeToDataContentConverter implements Converter<MediaContainerModel, Map<String, String>>
{

	private UniqueItemIdentifierService uniqueItemIdentifierService;
	private PermissionCRUDService permissionCRUDService;

	@Override
	public Map<String, String> convert(final MediaContainerModel source) throws ConversionException
	{
		if (Objects.isNull(source))
		{
			return null;
		}

		if (!getPermissionCRUDService().canReadType(MediaFormatModel._TYPECODE))
		{
			throwTypePermissionException(PermissionsConstants.READ, MediaFormatModel._TYPECODE);
		}

		return source.getMedias() //
				.stream() //
				.filter(media -> getUniqueItemIdentifierService().getItemData(media).isPresent()) //
				.filter(media -> media.getMediaFormat() != null) //
				.collect(toMap(media -> media.getMediaFormat().getQualifier(),
						media -> getUniqueItemIdentifierService().getItemData(media).get().getItemId()));
	}

	/**
	 * Throws {@link TypePermissionException}.
	 *
	 * @param permissionName
	 *           permission name
	 * @param itemType
	 *           item type code
	 */
	protected TypePermissionException throwTypePermissionException(final String permissionName, final String itemType)
	{
		throw PermissionExceptionUtils.createTypePermissionException(permissionName, itemType);
	}

	protected UniqueItemIdentifierService getUniqueItemIdentifierService()
	{
		return uniqueItemIdentifierService;
	}

	@Required
	public void setUniqueItemIdentifierService(final UniqueItemIdentifierService uniqueItemIdentifierService)
	{
		this.uniqueItemIdentifierService = uniqueItemIdentifierService;
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
}

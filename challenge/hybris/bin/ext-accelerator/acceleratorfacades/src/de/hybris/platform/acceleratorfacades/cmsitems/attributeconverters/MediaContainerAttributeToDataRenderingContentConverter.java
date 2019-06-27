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
package de.hybris.platform.acceleratorfacades.cmsitems.attributeconverters;

import static java.util.stream.Collectors.toMap;

import de.hybris.platform.cms2.common.exceptions.PermissionExceptionUtils;
import de.hybris.platform.cms2.exceptions.TypePermissionException;
import de.hybris.platform.cmsfacades.data.MediaContainerData;
import de.hybris.platform.cmsfacades.data.MediaData;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.security.permissions.PermissionCRUDService;
import de.hybris.platform.servicelayer.security.permissions.PermissionsConstants;

import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Required;


/**
 * This converter is used to convert an attribute of type {@link MediaContainerModel} to {@link MediaContainerData} for
 * rendering purposes.
 */
public class MediaContainerAttributeToDataRenderingContentConverter
		implements de.hybris.platform.cms2.common.functions.Converter<MediaContainerModel, Map<String, MediaData>>
{
	private Converter<MediaModel, MediaData> mediaModelConverter;
	private PermissionCRUDService permissionCRUDService;

	@Override
	public Map<String, MediaData> convert(final MediaContainerModel source)
	{
		if (Objects.isNull(source))
		{
			return null;
		}

		if (!getPermissionCRUDService().canReadType(MediaFormatModel._TYPECODE))
		{
			throw throwTypePermissionException(PermissionsConstants.READ, MediaFormatModel._TYPECODE);
		}

		return source.getMedias().stream() //
				.filter(media -> media.getMediaFormat() != null) //
				.collect( //
						toMap( //
								media -> media.getMediaFormat().getQualifier(), //
								media -> getMediaModelConverter().convert(media)));
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

	protected Converter<MediaModel, MediaData> getMediaModelConverter()
	{
		return mediaModelConverter;
	}

	@Required
	public void setMediaModelConverter(final Converter<MediaModel, MediaData> mediaModelConverter)
	{
		this.mediaModelConverter = mediaModelConverter;
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

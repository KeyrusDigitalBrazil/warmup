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
package de.hybris.platform.cmsfacades.media;

import de.hybris.platform.cmsfacades.data.MediaData;
import de.hybris.platform.cmsfacades.data.NamedQueryData;
import de.hybris.platform.cmsfacades.dto.MediaFileDto;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.media.exceptions.MediaNotFoundException;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.List;


/**
 * Facade for managing Media.
 */
public interface MediaFacade
{
	/**
	 * Get a media DTO by code.
	 *
	 * @param code
	 *           - the code to search for
	 * @return the media
	 * @throws MediaNotFoundException
	 *            when the media cannot be found
	 */
	MediaData getMediaByCode(String code);

	/**
	 * Get a media DTO by uuid.
	 *
	 * @param uuid
	 *           - the uuid to search for
	 * @throws MediaNotFoundException
	 *            when the media cannot be found
	 * @return the media
	 */
	MediaData getMediaByUUID(String uuid);

	/**
	 * Get a list of media DTO by their universally unique identifiers (uuids).
	 *
	 * @param uuids
	 *           - the list of uuids of the media to search for
	 * @throws MediaNotFoundException
	 *            when one of the media cannot be found
	 * @return list of media
	 */
	List<MediaData> getMediaByUUIDs(List<String> uuids);

	/**
	 * Search for a single page of media using a named query.
	 *
	 * @param namedQuery
	 *           - the named query
	 * @return the list of search results or empty collection
	 * @throws ValidationException
	 *            when the input contains validation errors
	 */
	List<MediaData> getMediaByNamedQuery(NamedQueryData namedQuery);

	/**
	 * Create a media item from an {@code InputStream}.
	 *
	 * @param media
	 *           - the attributes required to create a new media item
	 * @param mediaFile
	 *           - the actual file and an {@code InputStream} and its properties
	 * @return the newly created media
	 * @throws ValidationException
	 *            when the input contains validation errors
	 * @throws ConversionException
	 *            when unable to convert the DTOs to a hybris model
	 */
	MediaData addMedia(MediaData media, MediaFileDto mediaFile);
}

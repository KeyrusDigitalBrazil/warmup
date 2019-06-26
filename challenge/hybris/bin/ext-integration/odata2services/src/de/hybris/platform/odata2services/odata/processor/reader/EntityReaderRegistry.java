/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.odata2services.odata.processor.reader;

import de.hybris.platform.odata2services.odata.persistence.InternalProcessingException;

import java.util.Collection;

import org.apache.olingo.odata2.api.uri.UriInfo;
import org.springframework.beans.factory.annotation.Required;

/**
 * Registry that manages and provides EntityReaders
 */
public class EntityReaderRegistry
{
	private Collection<EntityReader> readers;

	/**
	 * Get the first {@link EntityReader} that can read from the commerce suite given the {@link UriInfo}
	 *
	 * @param uriInfo Use to determine which {@link EntityReader} can do the reading
	 * @return An {@link EntityReader}
	 * @throws RuntimeException or a derivative of it if no {@link EntityReader} is found
	 */
	public EntityReader getReader(final UriInfo uriInfo)
	{
		return readers.stream()
				.filter(reader -> reader.isApplicable(uriInfo))
				.findFirst()
				.orElseThrow(() -> new InternalProcessingException("Can't find an EntityReader to process the request"));
	}

	@Required
	public void setEntityReaders(final Collection<EntityReader> readers)
	{
		this.readers = readers;
	}
}

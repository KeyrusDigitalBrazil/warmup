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

import de.hybris.platform.odata2services.odata.persistence.PersistenceService;
import de.hybris.platform.odata2services.odata.processor.writer.ResponseWriter;

import org.apache.olingo.odata2.api.edm.EdmException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public abstract class AbstractEntityReader implements EntityReader
{
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractEntityReader.class);

	private PersistenceService persistenceService;
	private ResponseWriter responseWriter;

	protected boolean handleAssociationMultiplicityRetrievalError(final EdmException e)
	{
		LOGGER.warn("An exception occurred while getting the multiplicity of an association", e);
		return false;
	}

	protected PersistenceService getPersistenceService()
	{
		return persistenceService;
	}

	@Required
	public void setPersistenceService(final PersistenceService persistenceService)
	{
		this.persistenceService = persistenceService;
	}

	protected ResponseWriter getResponseWriter()
	{
		return responseWriter;
	}

	@Required
	public void setResponseWriter(final ResponseWriter responseWriter)
	{
		this.responseWriter = responseWriter;
	}
}

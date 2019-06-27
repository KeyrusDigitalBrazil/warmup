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
package com.sap.hybris.sapimpeximportadapter.controllers;

import static com.sap.hybris.sapimpeximportadapter.constants.SapimpeximportadapterConstants.IMPEX_MIMETYPE;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Preconditions;
import com.sap.hybris.sapimpeximportadapter.facades.ImpexImportFacade;


/**
 * @deprecated since 1811, please use {@link de.hybris.platform.integrationservices.model.IntegrationObjectModel}
 */

@Deprecated
@RestController
@RequestMapping("/import")
public class SapImpexImportController
{

	private static final Logger LOG = LoggerFactory.getLogger(SapImpexImportController.class);

	@Resource(name = "impexImportFacade")
	private ImpexImportFacade impexImportFacade;


	/**
	 * Import the impex payload to Hybris. On receiving the data, immediately send 200 success response to the caller
	 *
	 * @param inputStream
	 *           - input stream of type application/octet-stream
	 * @return void
	 * @throws IOException
	 */
	@PostMapping(consumes = IMPEX_MIMETYPE, produces =
	{ "application/json", "application/xml" })
	@ResponseStatus(HttpStatus.OK)
	public void importFromStream(final InputStream inputStream) throws IOException
	{
		Preconditions.checkArgument(inputStream != null, "inputStream cannot be null");
		LOG.info("Received request to import impex data");
		try
		{
			impexImportFacade.createAndImportImpexMedia(inputStream);
		}
		catch (final Exception e)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format("Error while importing the impex payload [%s]", e));
			}
			LOG.error(String.format("Error while importing the impex payload [%s]", e.getMessage()));
		}
		finally
		{
			inputStream.close();
		}

	}


}

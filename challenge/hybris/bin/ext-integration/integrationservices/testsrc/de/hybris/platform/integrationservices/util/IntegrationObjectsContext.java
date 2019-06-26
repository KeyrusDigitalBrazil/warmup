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

package de.hybris.platform.integrationservices.util;

import de.hybris.platform.integrationservices.IntegrationObjectBuilder;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class to provide context or to modify context about {@link IntegrationObjectModel}s
 * existing in the system. It's meant to be used in integration tests to set up or verify test conditions.
 */
public class IntegrationObjectsContext extends BaseContext
{
	private static final Logger LOG = LoggerFactory.getLogger(IntegrationObjectsContext.class);
	private static final IntegrationObjectModel[] ARRAY = new IntegrationObjectModel[0];
	private static IntegrationObjectsContext instance;

	private IntegrationObjectsContext()
	{
	}

	public static IntegrationObjectsContext create()
	{
		if (instance == null)
		{
			instance = new IntegrationObjectsContext();
		}
		return instance;
	}

	@Override
	public void after()
	{
		removeAll(IntegrationObjectModel.class);
	}

	/**
	 * Persists the integration object specifications in the context storage.
	 * @param specs specifications of the {@link IntegrationObjectModel} to create in the persistent storage.
	 */
	public IntegrationObjectModel[] givenExist(final IntegrationObjectBuilder... specs)
	{
		final List<IntegrationObjectModel> models = Stream.of(specs)
				.map(IntegrationObjectBuilder::build)
				.collect(Collectors.toList());
		models.forEach(this::persist);
		return models.toArray(ARRAY);
	}

	/**
	 * Persists the integration object specifications in the context storage.
	 * @param spec specifications of the {@link IntegrationObjectModel} to create in the persistent storage.
	 */
	public IntegrationObjectModel givenExists(final IntegrationObjectBuilder spec)
	{
		return givenExist(spec)[0];
	}

	/**
	 * Persists the integration object models in the context storage.
	 * @param models models of the {@link IntegrationObjectModel} to create in the persistent storage.
	 */
	public void givenExist(final IntegrationObjectModel... models)
	{
		Stream.of(models).forEach(this::persist);
	}

	private void persist(final IntegrationObjectModel model)
	{
		LOG.info("Saving IntegrationObject(code={})", model.getCode());
		modelService().save(model);
	}
}

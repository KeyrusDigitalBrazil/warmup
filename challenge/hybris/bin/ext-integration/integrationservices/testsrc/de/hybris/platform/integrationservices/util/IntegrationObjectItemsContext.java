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

import static de.hybris.platform.integrationservices.IntegrationObjectBuilder.integrationObject;

import de.hybris.platform.integrationservices.IntegrationObjectItemBuilder;
import de.hybris.platform.integrationservices.jalo.IntegrationObjectItem;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class to provide context or to modify context about {@link IntegrationObjectItem}s
 * existing in the system. It's meant to be used in integration tests to set up or verify test conditions.
 *
 * This context should be used when the test doesn't care about associating the IntegrationObjectItems to
 * a specific IntegrationObject. This class will generate a random IntegrationObject and associates the
 * IntegrationObjectItems to it.
 */
public class IntegrationObjectItemsContext extends BaseContext
{
	private static final Logger LOG = LoggerFactory.getLogger(IntegrationObjectItemsContext.class);
	private static final IntegrationObjectItemModel[] ARRAY = new IntegrationObjectItemModel[0];

	private final IntegrationObjectsContext integrationObjectsContext;
	private IntegrationObjectModel integrationObjectModel;

	private IntegrationObjectItemsContext()
	{
		integrationObjectsContext = IntegrationObjectsContext.create();
	}

	public static IntegrationObjectItemsContext create()
	{
		return new IntegrationObjectItemsContext();
	}

	@Override
	public void before()
	{
		super.before();
		integrationObjectsContext.before();
	}

	@Override
	public void after()
	{
		removeAll(IntegrationObjectItemAttributeModel.class);
		removeAll(IntegrationObjectItemModel.class);
		integrationObjectsContext.after();
	}

	/**
	 * Associates the integration object item specifications to an internally generated {@link IntegrationObjectModel},
	 * and persists them in the context storage. Use {@link #getIntegrationObject()} to get the associated IntegrationObjectModel.
	 * 
	 * @param specs specifications of the {@link IntegrationObjectItemModel} to create in the persistent storage.
	 */
	public IntegrationObjectItemModel[] givenExist(final IntegrationObjectItemBuilder... specs)
	{
		final Set<IntegrationObjectItemModel> models = Stream.of(specs)
				.map(IntegrationObjectItemBuilder::build)
				.collect(Collectors.toSet());
		persist(models);
		return models.toArray(ARRAY);
	}

	/**
	 * Associates the integration object items to an internally generated {@link IntegrationObjectModel},
	 * and persists them in the context storage. Use {@link #getIntegrationObject()} to get the associated IntegrationObjectModel.
	 *
	 * @param models models of the {@link IntegrationObjectItemModel} to create in the persistent storage.
	 */
	public void givenExist(final IntegrationObjectItemModel... models)
	{
		persist(Stream.of(models).collect(Collectors.toSet()));
	}

	/**
	 * Get the {@link IntegrationObjectModel} associated to the given {@link IntegrationObjectItemModel}s
	 *
	 * @return IntegrationObjectModel with a random code
	 */
	public IntegrationObjectModel getIntegrationObject()
	{
		if(integrationObjectModel == null)
		{
			integrationObjectModel = integrationObject().withCode(UUID.randomUUID().toString()).build();
		}
		return integrationObjectModel;
	}

	private void persist(final Set<IntegrationObjectItemModel> models)
	{
		models.forEach(model -> {
			LOG.info("Saving IntegrationObjectItem(code={})", model.getCode());
			model.setIntegrationObject(getIntegrationObject());
		});
		getIntegrationObject().setItems(models);
		integrationObjectsContext.givenExist(getIntegrationObject());
	}
}

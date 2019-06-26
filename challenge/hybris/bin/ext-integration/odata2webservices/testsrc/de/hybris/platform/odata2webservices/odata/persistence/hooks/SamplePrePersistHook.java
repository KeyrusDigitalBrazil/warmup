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
package de.hybris.platform.odata2webservices.odata.persistence.hooks;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;
import de.hybris.platform.odata2services.odata.persistence.hook.impl.DefaultPersistenceHookRegistry;

import java.util.Optional;
import java.util.function.Function;

import org.junit.rules.ExternalResource;
import org.springframework.beans.factory.annotation.Required;

public class SamplePrePersistHook extends ExternalResource implements PrePersistHook
{
	private boolean executed;
	private Function<ItemModel, Optional<ItemModel>> executeImplementation = Optional::of;

	@Override
	public Optional<ItemModel> execute(final ItemModel item)
	{
		executed = true;
		return executeImplementation.apply(item);
	}

	public boolean isExecuted()
	{
		return executed;
	}

	public void givenDoesInExecute(final Function<ItemModel, Optional<ItemModel>> function)
	{
		executeImplementation = function;
	}

	@Override
	public void after()
	{
		executed = false;
		executeImplementation = Optional::of;
	}

	@Required
	public void setHookRegistry(final DefaultPersistenceHookRegistry registry)
	{
		registry.addHook("samplePrePersistHook", this);
	}
}
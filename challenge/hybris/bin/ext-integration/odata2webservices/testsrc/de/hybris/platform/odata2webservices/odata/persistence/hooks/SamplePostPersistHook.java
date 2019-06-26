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
import de.hybris.platform.odata2services.odata.persistence.hook.PostPersistHook;
import de.hybris.platform.odata2services.odata.persistence.hook.impl.DefaultPersistenceHookRegistry;

import java.util.function.Consumer;

import org.junit.rules.ExternalResource;
import org.springframework.beans.factory.annotation.Required;

public class SamplePostPersistHook extends ExternalResource implements PostPersistHook
{
	private Consumer<ItemModel> executeProcedure = it -> {};
	private boolean executed = false;

	@Override
	public void execute(final ItemModel item)
	{
		executed = true;
		executeProcedure.accept(item);
	}

	public boolean isExecuted()
	{
		return executed;
	}

	public void givenDoesInExecute(final Consumer<ItemModel> proc)
	{
		executeProcedure = proc;
	}

	@Override
	public void after()
	{
		executed = false;
		executeProcedure = it -> {};
	}

	@Required
	public void setHookRegistry(final DefaultPersistenceHookRegistry registry)
	{
		registry.addHook("samplePostPersistHook", this);
	}
}

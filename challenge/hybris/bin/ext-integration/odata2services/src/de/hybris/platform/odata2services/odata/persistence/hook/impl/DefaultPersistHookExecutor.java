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
package de.hybris.platform.odata2services.odata.persistence.hook.impl;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.odata2services.odata.persistence.hook.PersistHookExecutor;
import de.hybris.platform.odata2services.odata.persistence.hook.PostPersistHook;
import de.hybris.platform.odata2services.odata.persistence.hook.PostPersistHookException;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHookException;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class DefaultPersistHookExecutor implements PersistHookExecutor
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultPersistHookExecutor.class);

	private PersistenceHookRegistry persistHookRegistry;

	@Override
	public Optional<ItemModel> runPrePersistHook(final String hookName, final ItemModel item, final String integrationKey)
	{
		final PrePersistHook hook = persistHookRegistry.getPrePersistHook(hookName, integrationKey);

		return hook != null
				? executePrePersistHook(hookName, hook, item, integrationKey)
				: Optional.of(item);
	}

	@Override
	public void runPostPersistHook(final String hookName, final ItemModel item, final String integrationKey)
	{
		final PostPersistHook hook = persistHookRegistry.getPostPersistHook(hookName, integrationKey);

		if (hook != null)
		{
			executePostPersistHook(hookName, hook, item, integrationKey);
		}
	}

	protected Optional<ItemModel> executePrePersistHook(final String hookName, final PrePersistHook hook, final ItemModel item, final String integrationKey)
	{
		try
		{
			LOG.debug("Executing PrePersistHook ['{}': {}] with item [{}]", hookName, hook.getClass(), item);
			return hook.execute(item);
		}
		catch (final RuntimeException e)
		{
			LOG.error("Exception occurred during the execution of Pre-Persist-Hook: [{}] with item: [{}]", hookName, item, e);
			throw new PrePersistHookException(String.format("Exception occurred during the execution of Pre-Persist-Hook: [%s]", hookName), e, integrationKey);
		}
	}

	protected void executePostPersistHook(final String hookName, final PostPersistHook hook, final ItemModel item, final String integrationKey)
	{
		try
		{
			LOG.debug("Executing PostPersistHook ['{}': {}] with item [{}]", hookName, hook.getClass(), item);
			hook.execute(item);
		}
		catch (final RuntimeException e)
		{
			LOG.error("Exception occurred during the execution of Post-Persist-Hook: [{}] with item: [{}]", hookName, item, e);
			throw new PostPersistHookException(String.format("Exception occurred during the execution of Post-Persist-Hook: [%s]", hookName), e, integrationKey);
		}
	}


	protected PersistenceHookRegistry getPersistHookRegistry()
	{
		return persistHookRegistry;
	}

	@Required
	public void setPersistHookRegistry(final PersistenceHookRegistry registry)
	{
		persistHookRegistry = registry;
	}
}

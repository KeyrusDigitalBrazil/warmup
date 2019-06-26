/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.addonsupport.setup.impl;

import de.hybris.platform.addonsupport.setup.AddOnSampleDataImportService;
import de.hybris.platform.addonsupport.setup.events.AddonSampleDataImportedEvent;
import de.hybris.platform.commerceservices.setup.data.ImportData;
import de.hybris.platform.commerceservices.setup.events.SampleDataImportedEvent;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.servicelayer.event.events.AfterInitializationEndEvent;
import de.hybris.platform.servicelayer.event.events.AfterInitializationStartEvent;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.util.Config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bouncycastle.util.Strings;
import org.springframework.beans.factory.annotation.Required;


public class GenericAddOnSampleDataEventListener extends AbstractEventListener<AbstractEvent>
{
	private static final Logger LOG = Logger.getLogger(GenericAddOnSampleDataEventListener.class);

	private String extensionName;
	private AddOnSampleDataImportService addOnSampleDataImportService;
	private boolean solrReindex;

	private final Map<String, AddonSampleDataImportedEvent> eventCache = new HashMap<>();
	private final Object eventCacheLock = new Object();

	@Override
	protected void onEvent(final AbstractEvent event)
	{
		if (event instanceof AddonSampleDataImportedEvent)
		{
			onAddonSampleDataImportedEvent((AddonSampleDataImportedEvent) event);
		}
		else if (event instanceof SampleDataImportedEvent)
		{
			final SampleDataImportedEvent importEvent = (SampleDataImportedEvent) event;
			getAddOnSampleDataImportService().importSampleData(getExtensionName(), importEvent.getContext(),
					importEvent.getImportData(), isSolrReindex());
		}
		else if (event instanceof AfterInitializationStartEvent || event instanceof AfterInitializationEndEvent)
		{
			// clear the event cache at the beginning and end of an init/update
			// the former in case a previous init/update wasn't finished properly
			clearEventCache();
		}
	}

	protected void onAddonSampleDataImportedEvent(final AddonSampleDataImportedEvent event)
	{
		validateEvent(event);

		final String eventExtName = event.getContext().getExtensionName();

		/*
		 * handle two possible cases: 1. event triggered by the addon itself, 2. event triggered by another addon that the
		 * addon is aware of
		 */
		if (getExtensionName().equals(eventExtName))
		{
			// addon's sample data has just been imported, now we can process matching events from the event cache
			eventCache.entrySet().stream().filter(e -> eventsMatch(event, e.getValue()))
					.forEach(e -> importSampleDataTriggeredByAddon(e.getValue()));

			// finally cache the event
			cacheEvent(event);
		}
		else if (!getExtensionName().equals(eventExtName) && isAwareOfAddon(eventExtName))
		{
			/*
			 * if sample data matching the event has already been imported, start the import right away, otherwise cache
			 * the event
			 */
			if (eventCache.entrySet().stream()
					.filter(e -> e.getKey().startsWith(getExtensionName()) && eventsMatch(event, e.getValue())).findFirst()
					.isPresent())
			{
				importSampleDataTriggeredByAddon(event);
			}
			else
			{
				cacheEvent(event);
			}
		}
	}

	protected void validateEvent(final AddonSampleDataImportedEvent event)
	{
		if (event.getContext() == null || StringUtils.isEmpty(event.getContext().getExtensionName()))
		{
			throw new IllegalArgumentException("event context is missing extension name");
		}
		if (CollectionUtils.isEmpty(event.getImportData()))
		{
			throw new IllegalArgumentException("event import data is empty");
		}
	}

	protected boolean eventsMatch(final AddonSampleDataImportedEvent newEvent, final AddonSampleDataImportedEvent cachedEvent)
	{
		final ImportData newImportData = newEvent.getImportData().get(0);
		final ImportData cachedImportData = cachedEvent.getImportData().get(0);

		return newImportData.getProductCatalogName().equals(cachedImportData.getProductCatalogName())
				&& newImportData.getContentCatalogNames().stream().findFirst()
						.equals(cachedImportData.getContentCatalogNames().stream().findFirst())
				&& newImportData.getStoreNames().get(0).equals(cachedImportData.getStoreNames().get(0));
	}

	protected void cacheEvent(final AddonSampleDataImportedEvent event)
	{
		final String cacheKey = createCacheKey(event);
		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("Caching AddonSampleDataImportedEvent with key [%s] for [%s]", cacheKey, getExtensionName()));
		}

		synchronized (eventCacheLock)
		{
			eventCache.put(cacheKey, event);
		}
	}

	protected String createCacheKey(final AddonSampleDataImportedEvent event)
	{
		final ImportData importData = event.getImportData().get(0);
		final String contentCatalogName = importData.getContentCatalogNames().stream().findFirst().isPresent()
				? importData.getContentCatalogNames().stream().findFirst().get()
				: StringUtils.EMPTY;
		return event.getContext().getExtensionName() + "_" + importData.getProductCatalogName() + "_" + contentCatalogName + "_"
				+ importData.getStoreNames().get(0);
	}

	protected void clearEventCache()
	{
		LOG.info(String.format("Clearing event cache of [%s] after system initialization", getExtensionName()));

		synchronized (eventCacheLock)
		{
			eventCache.clear();
		}
	}

	protected void importSampleDataTriggeredByAddon(final AddonSampleDataImportedEvent event)
	{
		final SystemSetupContext context = event.getContext();
		LOG.info(String.format("Importing sample data for [%s] triggered by [%s]", getExtensionName(), context.getExtensionName()));
		getAddOnSampleDataImportService().importSampleDataTriggeredByAddon(getExtensionName(), context, event.getImportData(),
				isSolrReindex());
	}

	protected boolean isAwareOfAddon(final String addonExtName)
	{
		final String awareOfProperty = Config.getString(getExtensionName() + ".awareof.addons", StringUtils.EMPTY);
		final List<String> awareOfList = Arrays.asList(Strings.split(awareOfProperty, ','));

		return awareOfList.contains(addonExtName);
	}

	protected AddOnSampleDataImportService getAddOnSampleDataImportService()
	{
		return addOnSampleDataImportService;
	}

	@Required
	public void setAddOnSampleDataImportService(final AddOnSampleDataImportService addOnSampleDataImportService)
	{
		this.addOnSampleDataImportService = addOnSampleDataImportService;
	}

	protected String getExtensionName()
	{
		return extensionName;
	}

	@Required
	public void setExtensionName(final String extensionName)
	{
		this.extensionName = extensionName;
	}

	protected boolean isSolrReindex()
	{
		return solrReindex;
	}

	public void setSolrReindex(final boolean solrReindex)
	{
		this.solrReindex = solrReindex;
	}

}

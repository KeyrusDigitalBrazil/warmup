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
package de.hybris.platform.multicountrysampledataaddon.setup.impl;

import de.hybris.platform.addonsupport.setup.impl.GenericAddOnSampleDataEventListener;
import de.hybris.platform.commerceservices.setup.data.ImportData;
import de.hybris.platform.commerceservices.setup.events.SampleDataImportedEvent;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.util.Config;

import java.util.*;

/**
 * This class extends {@link GenericAddOnSampleDataEventListener} and tweaks its behaviour to import sample data organized hierarchically as
 * required for multi-country set-ups.
 *
 */
public class MultiCountryAddOnSampleDataEventListener extends GenericAddOnSampleDataEventListener
{
    private static final String MULTICOUNTRY_ACTIVE_FLAG = "multicountrysampledataaddon.import.active";
    private static final String MULTICOUNTRY_CATALOGS_CONFIG_KEY_PREFIX = "multicountrysampledataaddon.setup.catalogs.";
    private static final String MULTICOUNTRY_STORES_CONFIG_KEY_PREFIX = "multicountrysampledataaddon.setup.stores.";
    private static final String ELECTRONICS_STORE_EXTENSION_NAME = "electronicsstore";

    @Override
    protected void onEvent(final AbstractEvent event)
    {
        if (Config.getBoolean(MULTICOUNTRY_ACTIVE_FLAG, true) && event instanceof SampleDataImportedEvent)
        {
            final SampleDataImportedEvent sampleDataImportedEvent = (SampleDataImportedEvent) event;
            if(sampleDataImportedEvent.getContext().getExtensionName().equals(ELECTRONICS_STORE_EXTENSION_NAME))
            {
                appendElectronicsMultiCountryImportData(sampleDataImportedEvent);
            }
        }

        super.onEvent(event);
    }

    /**
     * Adds to the import data in the event the MultiCountry catalogs and stores to load.
     * @param event the event to which to add multiCountry data info.
     */
    protected void appendElectronicsMultiCountryImportData(SampleDataImportedEvent event)
    {
        for (final ImportData importData : event.getImportData())
        {
            final Set<String> localCatalogNames = new LinkedHashSet<>(importData.getContentCatalogNames());
            for (final String catalogName : importData.getContentCatalogNames())
            {
                Arrays.asList(Config.getString(MULTICOUNTRY_CATALOGS_CONFIG_KEY_PREFIX + catalogName, "").split(","))
                        .stream()
                        .filter(contentCatalogName -> !contentCatalogName.isEmpty())
                        .forEach(localCatalogNames::add);
            }
            importData.setContentCatalogNames(new ArrayList<>(localCatalogNames));

            final Set<String> localStoreNames = new LinkedHashSet<>(importData.getContentCatalogNames());
            for (final String storeName : importData.getStoreNames())
            {
                Arrays.asList(Config.getString(MULTICOUNTRY_STORES_CONFIG_KEY_PREFIX + storeName , "").split(","))
                        .stream()
                        .filter(localStoreName -> !localStoreName.isEmpty())
                        .forEach(localStoreNames::add);
            }
            importData.setStoreNames(new ArrayList<>(localStoreNames));
        }
    }
}

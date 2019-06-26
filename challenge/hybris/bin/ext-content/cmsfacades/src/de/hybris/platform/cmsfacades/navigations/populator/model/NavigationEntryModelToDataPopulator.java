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
package de.hybris.platform.cmsfacades.navigations.populator.model;

import de.hybris.platform.cms2.model.navigation.CMSNavigationEntryModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cmsfacades.data.NavigationEntryData;
import de.hybris.platform.cmsfacades.navigations.service.NavigationEntryConverterRegistry;
import de.hybris.platform.cmsfacades.navigations.service.NavigationEntryItemModelConverter;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Required;


/**
 * This populator will populate the {@link NavigationEntryData}'s base attributes with attributes from
 * {@link CMSNavigationNodeModel}.
 */
public class NavigationEntryModelToDataPopulator implements Populator<CMSNavigationEntryModel, NavigationEntryData> {
    private NavigationEntryConverterRegistry navigationEntryConverterRegistry;

    @Override
    public void populate(final CMSNavigationEntryModel source, final NavigationEntryData target) throws ConversionException {
        if (!Objects.isNull(source.getItem())) {
            final NavigationEntryItemModelConverter navigationEntryItemModelConverter = getNavigationEntryConverterRegistry()
                    .getNavigationEntryItemModelConverter(source.getItem().getItemtype())
                    .orElseThrow(() -> new ConversionException("Converter does not exist for the item:" + source.getItem()));

            target.setName(source.getName());
            target.setItemSuperType(navigationEntryItemModelConverter.getItemType());
            target.setItemId(navigationEntryItemModelConverter.getUniqueIdentifierConverter().apply(source.getItem()));
            target.setItemType(source.getItem().getItemtype());
        }
    }

    /**
     * @deprecated since 1811 - as we continue to generify our apis, we no longer have need to crud entries
     * from the nodes themselves
     */
    @Deprecated
    protected NavigationEntryConverterRegistry getNavigationEntryConverterRegistry() {
        return navigationEntryConverterRegistry;
    }

    /**
     * @deprecated since 1811 - as we continue to generify our apis, we no longer have need to crud entries
     * from the nodes themselves
     */
    @Deprecated
    @Required
    public void setNavigationEntryConverterRegistry(final NavigationEntryConverterRegistry navigationEntryConverterRegistry) {
        this.navigationEntryConverterRegistry = navigationEntryConverterRegistry;
    }
}

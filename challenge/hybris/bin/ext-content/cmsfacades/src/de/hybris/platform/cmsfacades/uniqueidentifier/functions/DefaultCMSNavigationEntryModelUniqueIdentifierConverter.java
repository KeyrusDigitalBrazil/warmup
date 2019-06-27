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
package de.hybris.platform.cmsfacades.uniqueidentifier.functions;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationEntryModel;
import de.hybris.platform.cms2.servicelayer.services.CMSNavigationService;
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.uniqueidentifier.EncodedItemComposedKey;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueIdentifierConverter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.Optional;

/**
 * Default implementation for conversion of {@link CMSNavigationEntryModel}
 */
public class DefaultCMSNavigationEntryModelUniqueIdentifierConverter implements UniqueIdentifierConverter<CMSNavigationEntryModel>
{
    private ObjectFactory<ItemData> itemDataDataFactory;

    private CMSNavigationService cmsNavigationService;
    private CatalogVersionService catalogVersionService;

    @Override
    public String getItemType()
    {
        return CMSNavigationEntryModel._TYPECODE;
    }

    /**
     * {@inheritDoc}
     *
     * throw {@link IllegalArgumentException}.
     */
    @Override
    public ItemData convert(final CMSNavigationEntryModel navigationEntryModel) throws IllegalArgumentException
    {
        if (navigationEntryModel == null)
        {
            throw new IllegalArgumentException("The argument navigationEntryModel is null");
        }

        EncodedItemComposedKey itemComposedKey = new EncodedItemComposedKey();
        itemComposedKey.setCatalogId(navigationEntryModel.getCatalogVersion().getCatalog().getId());
        itemComposedKey.setCatalogVersion(navigationEntryModel.getCatalogVersion().getVersion());
        itemComposedKey.setItemId(navigationEntryModel.getUid());

        final ItemData itemData = getItemDataDataFactory().getObject();
        itemData.setItemId(itemComposedKey.toEncoded());
        itemData.setName(navigationEntryModel.getName());
        itemData.setItemType(navigationEntryModel.getItemtype());
        return itemData;
    }

    /**
     * {@inheritDoc}
     *
     * throw {@link IllegalArgumentException}.
     * throw {@link UnknownIdentifierException}.
     */
    @Override
    public CMSNavigationEntryModel convert(final ItemData itemData) throws IllegalArgumentException, UnknownIdentifierException
    {
        if (itemData == null)
        {
            throw new IllegalArgumentException("The argument itemData is null");
        }

        final EncodedItemComposedKey itemComposedKey = new EncodedItemComposedKey
                .Builder(itemData.getItemId()).encoded().build();
        final CatalogVersionModel catalogVersion = getCatalogVersionService().getCatalogVersion(itemComposedKey.getCatalogId(),
                itemComposedKey.getCatalogVersion());

        final Optional<CMSNavigationEntryModel> cmsNavigationEntryModel = getCmsNavigationService().getNavigationEntryForId(itemComposedKey.getItemId(), catalogVersion);
        if (cmsNavigationEntryModel.isPresent())
        {
            return cmsNavigationEntryModel.get();
        }
        throw new UnknownIdentifierException("Navigation Entry Type not found for code [" + itemData.getItemId() + "].");
    }

    protected ObjectFactory<ItemData> getItemDataDataFactory()
    {
        return itemDataDataFactory;
    }

    @Required
    public void setItemDataDataFactory(final ObjectFactory<ItemData> itemDataDataFactory)
    {
        this.itemDataDataFactory = itemDataDataFactory;
    }

    protected CatalogVersionService getCatalogVersionService()
    {
        return catalogVersionService;
    }

    @Required
    public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
    {
        this.catalogVersionService = catalogVersionService;
    }

    @Required
    public void setCmsNavigationService(CMSNavigationService cmsNavigationService)
    {
        this.cmsNavigationService = cmsNavigationService;
    }

    protected CMSNavigationService getCmsNavigationService()
    {
        return cmsNavigationService;
    }
}

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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationEntryModel;
import de.hybris.platform.cms2.servicelayer.services.CMSNavigationService;
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.uniqueidentifier.EncodedItemComposedKey;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.ObjectFactory;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCMSNavigationEntryModelUniqueIdentifierConverterTest
{
    private static final String ANY_ID = "any-id";
    private static final String CATALOG_ID = "catalog-id";
    private static final String CATALOG_VERSION = "catalog-version-id";

    @InjectMocks
    private DefaultCMSNavigationEntryModelUniqueIdentifierConverter conversionFunction;

    @Mock
    private ObjectFactory<ItemData> itemDataDataFactory;

    @Mock
    private CMSNavigationEntryModel cmsNavigationEntryModel;

    @Mock
    private CatalogVersionModel catalogVersion;

    @Mock
    private CatalogModel catalog;

    @Mock
    private CatalogVersionService catalogVersionService;

    @Mock
    private CMSNavigationService cmsNavigationService;

    private ItemData itemData = new ItemData();

    @Before
    public void setup()
    {
        EncodedItemComposedKey itemComposedKey = new EncodedItemComposedKey();
        itemComposedKey.setItemId(ANY_ID);
        itemComposedKey.setCatalogVersion(CATALOG_VERSION);
        itemComposedKey.setCatalogId(CATALOG_ID);

        String composedKey = itemComposedKey.toEncoded();
        itemData.setItemId(composedKey);
        itemData.setItemType(CMSNavigationEntryModel._TYPECODE);

        when(cmsNavigationEntryModel.getItemtype()).thenReturn(CMSNavigationEntryModel._TYPECODE);
        when(cmsNavigationEntryModel.getUid()).thenReturn(ANY_ID);

        when(cmsNavigationEntryModel.getCatalogVersion()).thenReturn(catalogVersion);
        when(catalogVersion.getCatalog()).thenReturn(catalog);
        when(catalog.getId()).thenReturn(CATALOG_ID);
        when(catalogVersion.getVersion()).thenReturn(CATALOG_VERSION);
        when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION)).thenReturn(catalogVersion);
        when(cmsNavigationService.getNavigationEntryForId(ANY_ID, catalogVersion)).thenReturn(Optional.of(cmsNavigationEntryModel));

        when(itemDataDataFactory.getObject()).thenReturn(new ItemData());
    }

    @Test
    public void testConvertValidNavigationEntryModel()
    {
        // WHEN
        final ItemData itemDataConverted = conversionFunction.convert(cmsNavigationEntryModel);

        // THEN
        assertThat(itemDataConverted.getItemId(), is(itemData.getItemId()));
        assertThat(itemDataConverted.getItemType(), is(CMSNavigationEntryModel._TYPECODE));
    }

    @Test
    public void testConvertValidNavigationEntryData()
    {
        // WHEN
        final CMSNavigationEntryModel navigationEntry = conversionFunction.convert(itemData);

        // THEN
        assertThat(navigationEntry, is(cmsNavigationEntryModel));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConvertWithItemDataNullWillThrowException()
    {
        conversionFunction.convert((ItemData)null);
    }

    @Test(expected=UnknownIdentifierException.class)
    public void testConvertValidNavigationEntryDataWithNoAssociatedNavigationEntryModelWillThrowException()
    {
        // GIVEN
        when(cmsNavigationService.getNavigationEntryForId(ANY_ID, catalogVersion)).thenReturn(Optional.empty());

        // WHEN
        conversionFunction.convert(itemData);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConvertWithNavigationEntryModeNullWillThrowException()
    {
        conversionFunction.convert((CMSNavigationEntryModel) null);
    }
}

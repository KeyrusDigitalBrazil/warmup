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
package de.hybris.platform.cmsfacades.cmsitems.populators;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_CLONE_COMPONENTS;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_PAGE_UUID;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.cloning.strategy.impl.PageCloningStrategy;
import de.hybris.platform.cms2.constants.Cms2Constants;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.assertj.core.util.Maps;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ClonePagePopulatorTest
{
    private static final String VALID_PAGE_UUID = "page-uuid";
    private static final String INVALID_PAGE_UUID = "invalid-slot-uuid";

    @InjectMocks
    private ClonePagePopulator populator;

    @Mock
    private PageCloningStrategy pageCloningStrategy;

    @Mock
    private UniqueItemIdentifierService uniqueItemIdentifierService;

    @Mock
    private AbstractPageModel pageModel;

    @Before
    public void setup()
    {
        when(uniqueItemIdentifierService.getItemModel(VALID_PAGE_UUID, AbstractPageModel.class)).thenReturn(Optional.of(pageModel));
        when(uniqueItemIdentifierService.getItemModel(INVALID_PAGE_UUID, AbstractPageModel.class)).thenReturn(Optional.empty());
    }

    @Test(expected = ConversionException.class)
    public void testWhenItemModelIsNull_should_ThrowException()
    {
        populator.populate(null, null);
    }

    @Test(expected = ConversionException.class)
    public void testWhenMapIsNull_should_ThrowException()
    {
        populator.populate(null, new ItemModel());
    }

    @Test
    public void testClonePageModelWithValidPageUUID() throws CMSItemNotFoundException
    {
        final AbstractPageModel sourcePageModel = new AbstractPageModel();
        final Map<String, Object> sourceMap = new HashMap<>();
        sourceMap.put(FIELD_PAGE_UUID, VALID_PAGE_UUID);
        sourceMap.put(FIELD_CLONE_COMPONENTS, false);

        populator.populate(sourceMap, sourcePageModel);

        Optional context = Optional.of(Maps.newHashMap(Cms2Constants.SHOULD_CLONE_COMPONENTS_CONTEXT_KEY, sourceMap.get(FIELD_CLONE_COMPONENTS)));

        verify(pageCloningStrategy).clone(pageModel, Optional.of(sourcePageModel), context);
    }

    @Test(expected = ConversionException.class)
    public void testClonePageModelWithInvalidPageUUIDShouldFail() throws CMSItemNotFoundException
    {
        final AbstractPageModel sourcePageModel = new AbstractPageModel();
        final Map<String, Object> sourceMap = new HashMap<>();
        sourceMap.put(FIELD_PAGE_UUID, INVALID_PAGE_UUID);
        sourceMap.put(FIELD_CLONE_COMPONENTS, false);

        populator.populate(sourceMap, sourcePageModel);

        Optional context = Optional.of(Maps.newHashMap(Cms2Constants.SHOULD_CLONE_COMPONENTS_CONTEXT_KEY, sourceMap.get(FIELD_CLONE_COMPONENTS)));

        verify(pageCloningStrategy).clone(pageModel, Optional.of(sourcePageModel), context);
    }

    @Test(expected = ConversionException.class)
    public void testClonePageModelWithoutSourcePageUUIDShouldFail() throws CMSItemNotFoundException
    {
        final Map<String, Object> sourceMap = new HashMap<>();
        sourceMap.put(FIELD_CLONE_COMPONENTS, false);

        populator.populate(sourceMap, new AbstractPageModel());
    }

    @Test(expected = ConversionException.class)
    public void testClonePageModelWithoutCloneComponentsShouldFail() throws CMSItemNotFoundException
    {
        final Map<String, Object> sourceMap = new HashMap<>();
        sourceMap.put(FIELD_PAGE_UUID, VALID_PAGE_UUID);

        populator.populate(sourceMap, new AbstractPageModel());
    }
}

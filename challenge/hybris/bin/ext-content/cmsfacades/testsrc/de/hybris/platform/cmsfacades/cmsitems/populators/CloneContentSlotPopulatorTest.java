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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.cloning.strategy.impl.ContentSlotCloningStrategy;
import de.hybris.platform.cms2.constants.Cms2Constants;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_CLONE_COMPONENTS;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_CONTENT_SLOT_UUID;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_PAGE_UUID;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CloneContentSlotPopulatorTest
{
    private static final String VALID_PAGE_UUID = "page-uuid";
    private static final String INVALID_PAGE_UUID = "invalid-page-uuid";

    private static final String VALID_CONTENT_SLOT_UUID = "content-slot-uuid";
    private static final String INVALID_CONTENT_SLOT_UUID = "invalid-content-slot-uuid";

    @InjectMocks
    private CloneContentSlotPopulator populator;

    @Mock
    private ContentSlotCloningStrategy contentSlotCloningStrategy;

    @Mock
    private UniqueItemIdentifierService uniqueItemIdentifierService;

    @Mock
    private AbstractPageModel pageModel;

    @Mock
    private ContentSlotModel contentSlotModel;

    @Before
    public void setup()
    {
        when(uniqueItemIdentifierService.getItemModel(VALID_PAGE_UUID, AbstractPageModel.class)).thenReturn(Optional.of(pageModel));
        when(uniqueItemIdentifierService.getItemModel(INVALID_PAGE_UUID, AbstractPageModel.class)).thenReturn(Optional.empty());
        when(uniqueItemIdentifierService.getItemModel(VALID_CONTENT_SLOT_UUID, ContentSlotModel.class)).thenReturn(Optional.of(contentSlotModel));
        when(uniqueItemIdentifierService.getItemModel(INVALID_CONTENT_SLOT_UUID, ContentSlotModel.class)).thenReturn(Optional.empty());
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
    public void testCloneContentSlotModelWithValidPageAndContentSlotUUID() throws CMSItemNotFoundException
    {
        final ContentSlotModel sourceContentSlotModel = new ContentSlotModel();
        final Map<String, Object> sourceMap = new HashMap<>();
        sourceMap.put(FIELD_PAGE_UUID, VALID_PAGE_UUID);
		sourceMap.put(FIELD_CONTENT_SLOT_UUID, VALID_CONTENT_SLOT_UUID);
		sourceMap.put(FIELD_CLONE_COMPONENTS, true);

        populator.populate(sourceMap, sourceContentSlotModel);

		final Map<String, Object> context = new HashMap<>();
		context.put(Cms2Constants.PAGE_CONTEXT_KEY, pageModel);
		context.put(Cms2Constants.SHOULD_CLONE_COMPONENTS_CONTEXT_KEY, true);

        verify(contentSlotCloningStrategy).clone(contentSlotModel, Optional.of(sourceContentSlotModel), Optional.of(context));
    }

    @Test(expected = ConversionException.class)
    public void testCloneContentSlotModelWithInvalidPageUUIDShouldFail() throws CMSItemNotFoundException
    {
		final ContentSlotModel sourceContentSlotModel = new ContentSlotModel();
		final Map<String, Object> sourceMap = new HashMap<>();
		sourceMap.put(FIELD_PAGE_UUID, INVALID_PAGE_UUID);
		sourceMap.put(FIELD_CONTENT_SLOT_UUID, VALID_CONTENT_SLOT_UUID);
		sourceMap.put(FIELD_CLONE_COMPONENTS, true);

		populator.populate(sourceMap, sourceContentSlotModel);
    }

	@Test(expected = ConversionException.class)
	public void testCloneContentSlotModelWithInvalidContentSlotUUIDShouldFail() throws CMSItemNotFoundException
	{
		final ContentSlotModel sourceContentSlotModel = new ContentSlotModel();
		final Map<String, Object> sourceMap = new HashMap<>();
		sourceMap.put(FIELD_PAGE_UUID, VALID_PAGE_UUID);
		sourceMap.put(FIELD_CONTENT_SLOT_UUID, INVALID_CONTENT_SLOT_UUID);
		sourceMap.put(FIELD_CLONE_COMPONENTS, false);

		populator.populate(sourceMap, sourceContentSlotModel);
	}

    @Test(expected = ConversionException.class)
    public void testCloneContentSlotModelWithoutPageUUIDShouldFail() throws CMSItemNotFoundException
    {
		final Map<String, Object> sourceMap = new HashMap<>();
		sourceMap.put(FIELD_CONTENT_SLOT_UUID, INVALID_CONTENT_SLOT_UUID);
		sourceMap.put(FIELD_CLONE_COMPONENTS, true);

		populator.populate(sourceMap, new ContentSlotModel());
    }

    @Test(expected = ConversionException.class)
    public void testCloneContentSlotModelWithoutContentSlotUUIDShouldFail() throws CMSItemNotFoundException
    {
		final Map<String, Object> sourceMap = new HashMap<>();
		sourceMap.put(FIELD_PAGE_UUID, VALID_PAGE_UUID);
		sourceMap.put(FIELD_CLONE_COMPONENTS, true);

		populator.populate(sourceMap, new ContentSlotModel());
    }

	@Test(expected = ConversionException.class)
	public void testCloneContentSlotModelWithoutCloneComponentsShouldFail() throws CMSItemNotFoundException
	{
		final Map<String, Object> sourceMap = new HashMap<>();
		sourceMap.put(FIELD_PAGE_UUID, VALID_PAGE_UUID);
		sourceMap.put(FIELD_CONTENT_SLOT_UUID, VALID_CONTENT_SLOT_UUID);

		populator.populate(sourceMap, new ContentSlotModel());
	}

	@Test
	public void testCloneContentSlotModelWithEmptyContext() throws CMSItemNotFoundException
	{
		populator.populate(new HashMap<>(), new ContentSlotModel());

		verify(contentSlotCloningStrategy, never()).clone(any(), any(), any());
	}
}

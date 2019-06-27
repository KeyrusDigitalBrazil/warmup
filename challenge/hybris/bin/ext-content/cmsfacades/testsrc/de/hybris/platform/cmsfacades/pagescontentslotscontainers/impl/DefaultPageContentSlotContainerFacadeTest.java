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
package de.hybris.platform.cmsfacades.pagescontentslotscontainers.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.common.service.SessionSearchRestrictionsDisabler;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.containers.AbstractCMSComponentContainerModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.data.ContentSlotData;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminComponentService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminContentSlotService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.data.PageContentSlotContainerData;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;
import java.util.function.Supplier;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPageContentSlotContainerFacadeTest
{
    private final String VALID_PAGE_ID = "someValidPage";
    private final String FIRST_SLOT_UID = "firstSlotUid";
    private final String SECOND_SLOT_UID = "secondSlotUid";
    private final String THIRD_SLOT_UID = "thirdSlotUid";
    private final String FIRST_CONTAINER_UID = "firstContainerUid";
    private final String SECOND_CONTAINER_TYPE = "someContainerType";
    private final String SECOND_CONTAINER_UID = "secondContainerUid";
    private final String FIRST_CONTAINER_TYPE = "someContainerType";
    private final String FIRST_COMPONENT_UID = "firstComponentUid";
    private final String SECOND_COMPONENT_UID = "firstComponentUid";

    @Mock
    private AbstractPageModel page;
    @Mock
    private ContentSlotData firstSlotData;
    @Mock
    private ContentSlotModel firstSlotModel;
    @Mock
    private ContentSlotData secondSlotData;
    @Mock
    private ContentSlotModel secondSlotModel;
    @Mock
    private ContentSlotData thirdSlotData;
    @Mock
    private ContentSlotModel thirdSlotModel;
    @Mock
    private AbstractCMSComponentContainerModel firstContainer;
    @Mock
    private AbstractCMSComponentContainerModel secondContainer;
    @Mock
    private AbstractCMSComponentModel firstComponent;
    @Mock
    private ItemData firstComponentData;
    @Mock
    private AbstractCMSComponentModel secondComponent;
    @Mock
    private ItemData secondComponentData;

    @Mock
    private CMSAdminPageService cmsAdminPageService;
    @Mock
    private CMSAdminComponentService cmsAdminComponentService;
    @Mock
    private CMSAdminContentSlotService cmsAdminContentSlotService;
    @Mock
    private UniqueItemIdentifierService uniqueItemIdentifierService;
    @Mock
    private SessionSearchRestrictionsDisabler sessionSearchRestrictionsDisabler;

    @InjectMocks
    private DefaultPageContentSlotContainerFacade defaultPageContentSlotContainerFacade;

    @Before
    public void setUp() throws CMSItemNotFoundException
    {
        // Page
        when(cmsAdminPageService.getPageForIdFromActiveCatalogVersion(VALID_PAGE_ID)).thenReturn(page);

        // Page slots
        when(cmsAdminComponentService.getContainersForContentSlot(firstSlotModel)).thenReturn(Collections.singletonList(firstContainer));
        when(cmsAdminComponentService.getContainersForContentSlot(secondSlotModel)).thenReturn(Collections.singletonList(secondContainer));
        when(cmsAdminComponentService.getContainersForContentSlot(thirdSlotModel)).thenReturn(Collections.emptyList());

        when(firstSlotData.getUid()).thenReturn(FIRST_SLOT_UID);
        when(firstSlotData.getContentSlot()).thenReturn(firstSlotModel);

        when(secondSlotData.getContentSlot()).thenReturn(secondSlotModel);
        when(secondSlotData.getUid()).thenReturn(SECOND_SLOT_UID);

        when(thirdSlotData.getContentSlot()).thenReturn(thirdSlotModel);
        when(thirdSlotData.getUid()).thenReturn(THIRD_SLOT_UID);

        // Containers
        when(firstContainer.getUid()).thenReturn(FIRST_CONTAINER_UID);
        when(firstContainer.getItemtype()).thenReturn(FIRST_CONTAINER_TYPE);
        when(cmsAdminComponentService.getCMSComponentsForContainer(firstContainer)).thenReturn(Collections.singletonList(firstComponent));

        when(secondContainer.getUid()).thenReturn(SECOND_CONTAINER_UID);
        when(secondContainer.getItemtype()).thenReturn(SECOND_CONTAINER_TYPE);

        // Components
        when(uniqueItemIdentifierService.getItemData(firstComponent)).thenReturn(Optional.of(firstComponentData));
        when(firstComponentData.getItemId()).thenReturn(FIRST_COMPONENT_UID);
        when(uniqueItemIdentifierService.getItemData(secondComponent)).thenReturn(Optional.of(secondComponentData));
        when(secondComponentData.getItemId()).thenReturn(SECOND_COMPONENT_UID);

        doAnswer(invocation -> {
            final Object[] args = invocation.getArguments();
            final Supplier<?> supplier = (Supplier<?>) args[0];
            return supplier.get();
        }).when(sessionSearchRestrictionsDisabler).execute(any());
    }

    @Test(expected = CMSItemNotFoundException.class)
    public void GivenAnInexistentPage_getPageContentSlotContainersByPageId_WillThrowCmsItemNotFoundException() throws CMSItemNotFoundException
    {
        // GIVEN
        final String INEXISTENT_PAGE_ID = "some inexistent pageID";
        when(cmsAdminPageService.getPageForIdFromActiveCatalogVersion(INEXISTENT_PAGE_ID)).thenThrow(new UnknownIdentifierException("some msg"));

        // WHEN / THEN
        defaultPageContentSlotContainerFacade.getPageContentSlotContainersByPageId(INEXISTENT_PAGE_ID);
    }

    @Test(expected = CMSItemNotFoundException.class)
    public void GivenAnAmbiguousPage_getPageContentSlotContainersByPageId_WillThrowCmsItemNotFoundException() throws CMSItemNotFoundException
    {
        // GIVEN
        final String AMBIGUOUS_PAGE_ID = "some ambiguous pageID";
        when(cmsAdminPageService.getPageForIdFromActiveCatalogVersion(AMBIGUOUS_PAGE_ID)).thenThrow(new AmbiguousIdentifierException("some msg"));

        // WHEN / THEN
        defaultPageContentSlotContainerFacade.getPageContentSlotContainersByPageId(AMBIGUOUS_PAGE_ID);
    }

    @Test
    public void GivenPageHasNoContainers_getPageContentSlotContainersByPageId_WillReturnAnEmptyList() throws CMSItemNotFoundException
    {
        // GIVEN
        when(cmsAdminContentSlotService.getContentSlotsForPage(page)).thenReturn(Collections.singletonList(thirdSlotData));

        // WHEN
        List<PageContentSlotContainerData> result = defaultPageContentSlotContainerFacade.getPageContentSlotContainersByPageId(VALID_PAGE_ID);

        // THEN
        assertThat(result, empty());
    }

    @Test
    public void GivenPageHasContainers_getPageContentSlotContainersByPageId_WillReturnThem() throws CMSItemNotFoundException
    {
        // GIVEN
        PageContentSlotContainerData expectedContainerData1 = new PageContentSlotContainerData();
        expectedContainerData1.setComponents(Collections.singletonList(FIRST_COMPONENT_UID));
        expectedContainerData1.setPageId(VALID_PAGE_ID);
        expectedContainerData1.setSlotId(FIRST_SLOT_UID);
        expectedContainerData1.setContainerId(FIRST_CONTAINER_UID);

        PageContentSlotContainerData expectedContainerData2 = new PageContentSlotContainerData();
        expectedContainerData2.setComponents(Collections.emptyList());
        expectedContainerData2.setPageId(VALID_PAGE_ID);
        expectedContainerData2.setSlotId(SECOND_SLOT_UID);
        expectedContainerData2.setContainerId(SECOND_CONTAINER_UID);

        when(cmsAdminContentSlotService.getContentSlotsForPage(page)).thenReturn(Arrays.asList(firstSlotData, secondSlotData));

        // WHEN
        List<PageContentSlotContainerData> result = defaultPageContentSlotContainerFacade.getPageContentSlotContainersByPageId(VALID_PAGE_ID);

        // THEN
        assertThat(result.get(0).getPageId(), is(VALID_PAGE_ID));
        assertThat(result.get(0).getSlotId(), is(FIRST_SLOT_UID));
        assertThat(result.get(0).getContainerId(), is(FIRST_CONTAINER_UID));
        assertThat(result.get(0).getComponents().size(), is(1));
        assertThat(result.get(0).getComponents().get(0), is(FIRST_COMPONENT_UID));

        assertThat(result.get(1).getPageId(), is(VALID_PAGE_ID));
        assertThat(result.get(1).getSlotId(), is(SECOND_SLOT_UID));
        assertThat(result.get(1).getContainerId(), is(SECOND_CONTAINER_UID));
        assertThat(result.get(1).getComponents(), empty());
    }
}

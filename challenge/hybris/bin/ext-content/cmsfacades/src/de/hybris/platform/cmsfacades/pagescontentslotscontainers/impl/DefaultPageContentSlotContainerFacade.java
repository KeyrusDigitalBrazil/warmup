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

import de.hybris.platform.cms2.common.service.SessionSearchRestrictionsDisabler;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.jalo.contents.components.AbstractCMSComponent;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.containers.AbstractCMSComponentContainerModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.data.ContentSlotData;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminComponentService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminContentSlotService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.pagescontentslotscontainers.PageContentSlotContainerFacade;
import de.hybris.platform.cmsfacades.data.PageContentSlotContainerData;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Default implementation of {@link PageContentSlotContainerFacade}.
 */
public class DefaultPageContentSlotContainerFacade implements PageContentSlotContainerFacade
{
    private CMSAdminPageService adminPageService;
    private CMSAdminContentSlotService adminContentSlotService;
    private CMSAdminComponentService adminComponentService;
    private SessionSearchRestrictionsDisabler sessionSearchRestrictionsDisabler;
    private UniqueItemIdentifierService uniqueItemIdentifierService;

    @Override
    public List<PageContentSlotContainerData> getPageContentSlotContainersByPageId(String pageId)
            throws CMSItemNotFoundException
    {
        AbstractPageModel page = null;
        try
        {
            page = getAdminPageService().getPageForIdFromActiveCatalogVersion(pageId);
        }
        catch (UnknownIdentifierException | AmbiguousIdentifierException e)
        {
            throw new CMSItemNotFoundException("Cannot find page with uid \"" + pageId + "\".", e);
        }

        final Collection<ContentSlotData> contentSlots = getAdminContentSlotService().getContentSlotsForPage(page);
        return contentSlots.stream().flatMap(slot -> buildPageContentSlotContainerStream(pageId, slot))
                .collect(Collectors.toList());
    }

    /**
     * Gets a list of containers data found within a given slot.
     *
     * @param pageId
     *           - the page for which to look up the containers for
     * @param contentSlot
     *           - the slot for which to look up the containers for
     * @return stream of containers data found in the content slot; never <tt>null</tt>
     */
    protected Stream<PageContentSlotContainerData> buildPageContentSlotContainerStream(final String pageId,
       final ContentSlotData contentSlot)
    {
       final List<AbstractCMSComponentContainerModel> containers = getSessionSearchRestrictionsDisabler().execute(() -> Lists
               .newArrayList(getAdminComponentService().getContainersForContentSlot(contentSlot.getContentSlot())));

       return containers.stream()
               .map(container -> buildPageContentSlotContainer(pageId, contentSlot.getUid(), container));
    }

    /**
     * Builds the data of a container in a content slot.
     *
     * @param pageId
     *           - the page where the content slot resides.
     * @param slotId
     *           - the slot where the container resides
     * @param container
     *           - the model of the container for which to retrieve its data
     * @return the newly built container data; never <tt>null</tt>
     */
    protected PageContentSlotContainerData buildPageContentSlotContainer(final String pageId, final String slotId,
       final AbstractCMSComponentContainerModel container)
    {
        final PageContentSlotContainerData pageContentSlotContainer = new PageContentSlotContainerData();
        pageContentSlotContainer.setPageId(pageId);
        pageContentSlotContainer.setSlotId(slotId);
        pageContentSlotContainer.setContainerId(container.getUid());
        pageContentSlotContainer.setContainerType(container.getItemtype());
        pageContentSlotContainer.setComponents(
                getAdminComponentService().getCMSComponentsForContainer(container).stream()
                        .flatMap(this::getComponentUuid)
                        .collect(Collectors.toList()));


        return pageContentSlotContainer;
    }

    /**
     * Retrieves the UUID of the provided component.
     *
     * @param component
     *           - the component for which to retrieve its UUID.
     * @return stream containing the component UUID; never <tt>null</tt>
     */
    protected Stream<String> getComponentUuid(AbstractCMSComponentModel component)
    {
        Optional<ItemData> itemData = getUniqueItemIdentifierService().getItemData(component);
        return itemData.isPresent() ? Stream.of(itemData.get().getItemId()) : Stream.empty();
    }


    protected CMSAdminPageService getAdminPageService()
    {
        return adminPageService;
    }

    @Required
    public void setAdminPageService(final CMSAdminPageService adminPageService)
    {
        this.adminPageService = adminPageService;
    }

    protected CMSAdminContentSlotService getAdminContentSlotService()
    {
        return adminContentSlotService;
    }

    @Required
    public void setAdminContentSlotService(final CMSAdminContentSlotService adminContentSlotService)
    {
        this.adminContentSlotService = adminContentSlotService;
    }

    protected SessionSearchRestrictionsDisabler getSessionSearchRestrictionsDisabler()
    {
        return sessionSearchRestrictionsDisabler;
    }

    @Required
    public void setSessionSearchRestrictionsDisabler(
            SessionSearchRestrictionsDisabler sessionSearchRestrictionsDisabler)
    {
        this.sessionSearchRestrictionsDisabler = sessionSearchRestrictionsDisabler;
    }

    protected CMSAdminComponentService getAdminComponentService()
    {
        return adminComponentService;
    }

    @Required
    public void setAdminComponentService(final CMSAdminComponentService adminComponentService)
    {
        this.adminComponentService = adminComponentService;
    }

    protected UniqueItemIdentifierService getUniqueItemIdentifierService()
    {
        return uniqueItemIdentifierService;
    }

    @Required
    public void setUniqueItemIdentifierService(final UniqueItemIdentifierService uniqueItemIdentifierService)
    {
        this.uniqueItemIdentifierService = uniqueItemIdentifierService;
    }
}

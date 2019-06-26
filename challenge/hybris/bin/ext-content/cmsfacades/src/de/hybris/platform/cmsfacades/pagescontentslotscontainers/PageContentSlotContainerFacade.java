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
package de.hybris.platform.cmsfacades.pagescontentslotscontainers;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.data.PageContentSlotContainerData;

import java.util.List;

/**
 * Facade for managing containers in pages.
 */
public interface PageContentSlotContainerFacade
{
    /**
     * Get a list of containers with their components for a given page.
     *
     * @param pageId
     *           - the page for which to look up the containers for
     * @return list of containers with their component UUIDs; never <tt>null</tt>
     * @throws CMSItemNotFoundException
     *            when the page cannot be found
     */
    List<PageContentSlotContainerData> getPageContentSlotContainersByPageId(final String pageId) throws CMSItemNotFoundException;
}

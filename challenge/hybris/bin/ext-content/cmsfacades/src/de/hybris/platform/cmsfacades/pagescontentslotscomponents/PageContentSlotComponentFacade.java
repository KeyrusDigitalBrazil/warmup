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
package de.hybris.platform.cmsfacades.pagescontentslotscomponents;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.data.PageContentSlotComponentData;
import de.hybris.platform.cmsfacades.exception.ComponentNotAllowedInSlotException;
import de.hybris.platform.cmsfacades.exception.ComponentNotFoundInSlotException;
import de.hybris.platform.cmsfacades.exception.ValidationException;

import java.util.List;


/**
 * Facade for managing content slots contents.
 */
public interface PageContentSlotComponentFacade
{
	/**
	 * Get a list of content slot with their components and their position in the slots for a given page.
	 *
	 * @param pageId
	 *           - the page for which to look up the content slots for
	 * @return list of content slot ids with the component ids in each slot; never <tt>null</tt>
	 * @throws CMSItemNotFoundException
	 *            when the page cannot be found
	 */
	List<PageContentSlotComponentData> getPageContentSlotComponentsByPageId(final String pageId) throws CMSItemNotFoundException;

	/**
	 * Add an existing component item into a specific index position of an existing content slot.
	 * <p>
	 * If the index provided is larger than or equal to the next available component index in the slot, then the
	 * component will be added into the next available index.
	 * </p>
	 *
	 * @param pageContentSlotComponent
	 *           - DTO holding required attributes; never <tt>null</tt>
	 * @return the pageContentSlotComponent holding the componentId added to the slot and the pageId, slotId and slot
	 *         position of the targeted content slot.
	 * @throws CMSItemNotFoundException
	 *            when the component or slot cannot be found
	 * @throws ValidationException
	 *            when some validation rules fail
	 */
	PageContentSlotComponentData addComponentToContentSlot(final PageContentSlotComponentData pageContentSlotComponent)
			throws CMSItemNotFoundException;

	/**
	 * Remove a component item from a content slot.
	 *
	 * @param slotId
	 *           - the unique identifier of the content slot; never <tt>null</tt>
	 * @param componentId
	 *           - the unique identifier of the component item; never <tt>null</tt>
	 * @throws CMSItemNotFoundException
	 *            when the component or slot cannot be found
	 * @throws ComponentNotFoundInSlotException
	 *            when the component slot does not contain the component
	 */
	void removeComponentFromContentSlot(String slotId, String componentId) throws CMSItemNotFoundException;

	/**
	 * Moves a component within a slot or between slots.
	 *
	 * @param pageUid
	 *           Page UID
	 * @param componentUid
	 *           UID of the component to move
	 * @param originSlotUid
	 *           The UID of the content slot that contains the origin content slot
	 * @param pageContentSlotComponentData
	 *           {@link PageContentSlotComponentData} The component which contains all information of it's final
	 *           destination
	 * @return the pageContentSlotComponent holding the componentId moved to the slot and the pageId, slotId and slot
	 *         position of the targeted content slot.
	 * @throws CMSItemNotFoundException
	 *            when the component or slot cannot be found
	 * @throws ComponentNotAllowedInSlotException
	 *            when the component type is not allowed in the given content slot
	 * @throws ValidationException
	 *            when there are validation errors
	 */
	PageContentSlotComponentData moveComponent(String pageUid, String componentUid, String originSlotUid,
			PageContentSlotComponentData pageContentSlotComponentData) throws CMSItemNotFoundException;
}

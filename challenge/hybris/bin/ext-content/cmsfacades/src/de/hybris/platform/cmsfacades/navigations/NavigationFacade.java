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
package de.hybris.platform.cmsfacades.navigations;

import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.data.CMSItemSearchData;
import de.hybris.platform.cmsfacades.data.NavigationNodeData;

import java.util.List;
import java.util.Map;


/**
 * Navigation facade interface which deals with methods related to navigation management.
 */
public interface NavigationFacade
{
	/**
	 * Retrieves a {@link NavigationNodeData} by its item's uid.
	 *
	 * @param uid
	 * 		The navigation node unique identifier
	 * @return an instance of {@link NavigationNodeData}
	 * @throws CMSItemNotFoundException
	 * 		when the item does not exist
	 */
	NavigationNodeData findNavigationNodeById(String uid) throws CMSItemNotFoundException;

	/**
	 * Finds all navigation nodes. Returns a list of all navigation nodes, excluding the root node.
	 *
	 * @return a list of {@link NavigationNodeData}
	 * @deprecated since 1811, please use {@link de.hybris.platform.cmsfacades.cmsitems.CMSItemFacade#findCMSItems(CMSItemSearchData, PageableData)} instead.
	 */
	@Deprecated
	List<NavigationNodeData> findAllNavigationNodes();

	/**
	 * Finds all navigation nodes by parentUid. Returns a list of navigation nodes where the parentUid equals the
	 * parentUid parameter, otherwise returns a list of all navigation nodes, excluding the root node. If the parentUid
	 * is not an existing item, then it returns an empty list.
	 *
	 * @param parentUid
	 * 		the parent unique identifier, optional.
	 * @return a list of {@link NavigationNodeData}
	 */
	List<NavigationNodeData> findAllNavigationNodes(String parentUid);

	/**
	 * Updates a {@link NavigationNodeData}. This method can also be used to move the node to a different tree structure
	 * (changing the parentUid), its position in relation to its siblings, the name and title.
	 *
	 * @param navigationNodeUid
	 * 		the navigation node unique identifier
	 * @param navigationNodeData
	 * 		the navigation node data representing the model to be updated.
	 * @return the updated navigation node data.
	 * @throws CMSItemNotFoundException
	 * 		when the item does not exist
	 * @deprecated since 1811, please use {@link de.hybris.platform.cmsfacades.cmsitems.CMSItemFacade#updateItem(String, Map)} instead.
	 */
	@Deprecated
	NavigationNodeData updateNavigationNode(String navigationNodeUid, NavigationNodeData navigationNodeData)
			throws CMSItemNotFoundException;

	/**
	 * Creates a new navigation node represented by {@link NavigationNodeData}
	 *
	 * @param navigationNodeData
	 * 		the navigation node value data
	 * @return the navigation node created.
	 * @throws CMSItemNotFoundException
	 * 		if the parentUid does not exist
	 * @deprecated since 1811, please use {@link de.hybris.platform.cmsfacades.cmsitems.CMSItemFacade#createItem(Map)} instead.
	 */
	@Deprecated
	NavigationNodeData addNavigationNode(NavigationNodeData navigationNodeData) throws CMSItemNotFoundException;

	/**
	 * Deletes a navigation node by item uid.
	 *
	 * @param uid
	 * 		the navigation node item Uid to be removed.
	 * @throws CMSItemNotFoundException
	 * 		if the item does not exist
	 * @deprecated since 1811, please use {@link de.hybris.platform.cmsfacades.cmsitems.CMSItemFacade#deleteCMSItemByUuid(String)} instead.
	 */
	@Deprecated
	void deleteNavigationNode(String uid) throws CMSItemNotFoundException;

	/**
	 * Return the node and all of its ancestors. If the node in question is the {@code root} node, then it returns an
	 * empty list.
	 *
	 * @param navigationNodeUid
	 * 		the navigation node uid to be returned
	 * @return a list of {@link NavigationNodeData} where the first element is the node represented by navigationNodeUid
	 * @throws CMSItemNotFoundException
	 * 		- when the navigationNodeUid does not exist.
	 */
	List<NavigationNodeData> getNavigationAncestorsAndSelf(final String navigationNodeUid) throws CMSItemNotFoundException;
}

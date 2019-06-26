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
package de.hybris.platform.cmsfacades.pagesrestrictions;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.data.PageRestrictionData;

import java.util.List;


/**
 * Facade for managing page-restriction relations.
 */
public interface PageRestrictionFacade
{

	/**
	 * Get a list of restrictions for a given page.
	 *
	 * @param pageId
	 *           the id of the page to which the restrictions are applied
	 * @return list of page id - restriction id pairs; never <tt>null</tt>
	 * @throws CMSItemNotFoundException
	 *            when the page cannot be found
	 */
	List<PageRestrictionData> getRestrictionsByPage(String pageId) throws CMSItemNotFoundException;

	/**
	 * Updates the list of restrictions associated with the pageId based according to the provided
	 * pageRestrictionsListData
	 *
	 * @param pageId
	 *           the id of the page to which the restrictions are applied
	 * @param pageRestrictionsListData
	 *           the list of page-restriction relation
	 * @throws CMSItemNotFoundException
	 *            when cannot find a page based on the provided pageId
	 */
	void updateRestrictionRelationsByPage(String pageId, List<PageRestrictionData> pageRestrictionsListData)
			throws CMSItemNotFoundException;

	/**
	 * Get a list of restrictions for all pages.
	 *
	 * @return a list of restrictions for all pages.
	 */
	List<PageRestrictionData> getAllPagesRestrictions();
}

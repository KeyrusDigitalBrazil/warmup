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
package de.hybris.platform.cmsfacades.pagescontentslotstyperestrictions;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.data.ContentSlotTypeRestrictionsData;


/**
 * CMS Content slot facade used to fetch content slot details (ie: positions), and reposition components from slot to
 * slot.
 */
public interface PageContentSlotTypeRestrictionsFacade
{
	/**
	 * Fetches type restrictions for the given content slot on a given page. The content slot is searched on the current catalog
	 * version and all the active versions of each of the parent catalogs.
	 *
	 * @param pageUid
	 *           Page UID
	 * @param contentSlotUid
	 *           Content slot UID
	 * @return Type restrictions for the given content slot on the page; never <tt>null</tt>
	 * @throws CMSItemNotFoundException
	 *            Thrown in case not find type restrictions passing pageUID
	 */
	ContentSlotTypeRestrictionsData getTypeRestrictionsForContentSlotUID(String pageUid, String contentSlotUid)
			throws CMSItemNotFoundException;

}

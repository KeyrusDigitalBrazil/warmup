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
package de.hybris.platform.cmsfacades.pages;

import de.hybris.platform.cms2.common.annotations.HybrisDeprecation;
import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.data.AbstractPageData;
import de.hybris.platform.cmsfacades.data.PageTypeData;

import java.util.List;


/**
 * Component facade interface which deals with methods related to page operations.
 */
public interface PageFacade
{

	/**
	 * Find all pages.
	 *
	 * @return list of {@link AbstractPageData} ordered by title ascending; never <tt>null</tt>
	 *
	 * @deprecated since 6.6. Please use
	 *             {@link de.hybris.platform.cmsfacades.cmsitems.CMSItemFacade#findCMSItems(de.hybris.platform.cmsfacades.data.CMSItemSearchData, PageableData)}
	 *             instead.
	 */
	@HybrisDeprecation(sinceVersion = "6.6")
	@Deprecated
	List<AbstractPageData> findAllPages();

	/**
	 * Find all page types.
	 *
	 * @return list of all {@link PageTypeData}; never <code>null</code>
	 */
	List<PageTypeData> findAllPageTypes();

	/**
	 * Find all default or variant pages for a given page type.
	 *
	 * @param typeCode
	 *           - the page typecode
	 * @param isDefaultPage
	 *           - set to true to find all default pages; set to false to find all variant pages
	 * @return list of default or variant {@link AbstractPageData} ordered by name ascending; never <tt>null</tt>
	 *
	 * @deprecated since 6.6. Please use
	 *             {@link de.hybris.platform.cmsfacades.cmsitems.CMSItemFacade#findCMSItems(de.hybris.platform.cmsfacades.data.CMSItemSearchData, PageableData)}
	 *             instead.
	 */
	@HybrisDeprecation(sinceVersion = "6.6")
	@Deprecated
	List<AbstractPageData> findPagesByType(String typeCode, Boolean isDefaultPage);


	/**
	 * Find all variant pages for a given page.
	 *
	 * @param pageId
	 *           - the page identifier
	 * @return list of variation page uids; empty if the given page is already a variation page; never <tt>null</tt>
	 * @throws CMSItemNotFoundException
	 *            when the page could not be found
	 */
	List<String> findVariationPages(String pageId) throws CMSItemNotFoundException;

	/**
	 * Find all default pages for a given page.
	 *
	 * @param pageId
	 *           - the page identifier
	 * @return list of default page uids; empty if the given page is already a default page; never <tt>null</tt>
	 * @throws CMSItemNotFoundException
	 *            when the page could not be found
	 */
	List<String> findFallbackPages(String pageId) throws CMSItemNotFoundException;

	/**
	 * Find a single page by its uid.
	 *
	 * @param uid
	 *           - the uid of the page to retrieve.
	 * @return the page matching the given uid
	 * @throws CMSItemNotFoundException
	 *            when the page could not be found
	 *
	 * @deprecated since 6.6. Please use
	 *             {@link de.hybris.platform.cmsfacades.cmsitems.CMSItemFacade#getCMSItemByUuid(String)} instead.
	 */
	@HybrisDeprecation(sinceVersion = "6.6")
	@Deprecated
	AbstractPageData getPageByUid(String uid) throws CMSItemNotFoundException;

	/**
	 * Returns {@link AbstractPageData} object based on pageLabelOrId or code.
	 *
	 * @param pageType
	 *           the page type
	 * @param pageLabelOrId
	 *           the page label or id. This field is used only when the page type is ContentPage.
	 * @param code
	 *           the code depends on the page type. If the page type is ProductPage then the code should be a product
	 *           code. If the page type is CategoryPage then the code should be a category code. If the page type is
	 *           CatalogPage then the code should be a catalog page.
	 * @return the {@link AbstractPageData} object.
	 * @throws CMSItemNotFoundException
	 *            if the page does not exists.
	 */
	AbstractPageData getPageData(String pageType, String pageLabelOrId, String code) throws CMSItemNotFoundException;
}

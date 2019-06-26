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
package de.hybris.platform.cmsfacades.pages.service;

import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cmsfacades.data.OptionData;

import java.util.List;


/**
 * Provide methods for retrieving default and variation page information for a given page type and/or for a given CMS
 * page model.
 * @param <T> the type parameter which extends the {@link AbstractPageModel} type
 */
public interface PageVariationResolver<T extends AbstractPageModel>
{
	/**
	 * Find all default or variation pages for a given page type.
	 *
	 * @param typeCode
	 *           the page type
	 * @param isDefaultPage
	 *           <tt>true</tt> to retrieve default pages; <tt>false</tt> to retrieve variation pages
	 * @return all default or variation pages
	 */
	List<T> findPagesByType(String typeCode, boolean isDefaultPage);

	/**
	 * Find default page for a given page.
	 *
	 * @param pageModel
	 *           the page
	 * @return default page (the collection should contain at most one item); <br>
	 *         <tt>empty</tt> if the given page is a default page; <br>
	 *         never <tt>null</tt>
	 */
	List<T> findDefaultPages(T pageModel);

	/**
	 * Find variation pages for a given page.
	 *
	 * @param pageModel
	 *           the page
	 * @return variation pages associated to the given page; <br>
	 *         <tt>empty</tt> if the given page is a variation page; <br>
	 *         never <tt>null</tt>
	 */
	List<T> findVariationPages(T pageModel);

	/**
	 * Determines if a given page is a default page or a variation page.
	 *
	 * @param pageModel
	 *           the page
	 * @return <tt>true</tt> if the given page is a default page; <tt>false</tt> otherwise
	 */
	boolean isDefaultPage(T pageModel);

	/**
	 * Finds all display conditions available for a given page type. It is used to determine if a fallback and/or
	 * variation page can be created.
	 *
	 * @param typeCode
	 *           the page type
	 * @return all display conditions
	 */
	List<OptionData> findDisplayConditions(String typeCode);
}

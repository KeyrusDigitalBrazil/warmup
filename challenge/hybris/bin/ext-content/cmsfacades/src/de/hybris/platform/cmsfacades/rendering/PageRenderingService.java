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
package de.hybris.platform.cmsfacades.rendering;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.data.AbstractPageData;


/**
 * Interface responsible for retrieving page for rendering purposes.
 */
public interface PageRenderingService
{

	/**
	 * Returns {@link AbstractPageData} object based on pageLabelOrId or code.
	 * @param pageTypeCode the page type
	 * @param pageLabelOrId the page label or id. This field is used only when the page type is ContentPage.
	 * @param code the code depends on the page type. If the page type is ProductPage then the code should be a product code.
	 *             If the page type is CategoryPage then the code should be a category code.
	 *             If the page type is CatalogPage then the code should be a catalog page.
	 * @return the {@link AbstractPageData} object.
	 * @throws CMSItemNotFoundException if the page does not exists.
	 */
	AbstractPageData getPageRenderingData(final String pageTypeCode, final String pageLabelOrId, final String code) throws
			CMSItemNotFoundException;

}

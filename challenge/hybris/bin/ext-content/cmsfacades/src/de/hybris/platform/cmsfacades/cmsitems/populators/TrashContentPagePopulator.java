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
package de.hybris.platform.cmsfacades.cmsitems.populators;

import de.hybris.platform.cms2.enums.CmsPageStatus;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;


/**
 * TrashContentPagePopulator populator for cmsfacades is used to ensure that a page that is being moved to the trash bin contains
 * the right information (e.g., cannot have a homepage flag).
 */
public class TrashContentPagePopulator implements Populator<Map<String, Object>, ItemModel>
{

	@Override
	public void populate(final Map<String, Object> source, final ItemModel itemModel) throws ConversionException
	{
		if (itemModel == null)
		{
			throw new ConversionException("Item Model used in the populator should not be null.");
		}

		final ContentPageModel pageModel = (ContentPageModel) itemModel;

		if (isPageInTrash(pageModel) && pageModel.isHomepage())
		{
			pageModel.setHomepage(false);
		}
	}

	protected boolean isPageInTrash(final ContentPageModel contentPageModel)
	{
		return contentPageModel.getPageStatus().equals(CmsPageStatus.DELETED);
	}
}

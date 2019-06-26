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

import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cmsfacades.pages.service.PageInitializer;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

import static de.hybris.platform.cms2.enums.CmsApprovalStatus.APPROVED;


/**
 * performs the necessary initialization that a newly created {@code AbstractPageModel} may require before saving.
 */
public class PageInitializerPopulator implements Populator<Map<String, Object>, ItemModel>
{

	private PageInitializer pageInitializer;
	private ModelService modelService;

	@Override
	public void populate(final Map<String, Object> source, final ItemModel itemModel) throws ConversionException
	{
		if (getModelService().isNew(itemModel))
		{
			((AbstractPageModel) itemModel).setApprovalStatus(APPROVED);
			getModelService().save(itemModel);
			getPageInitializer().initialize((AbstractPageModel) itemModel);
		}
	}

	@Required
	public void setPageInitializer(PageInitializer pageInitializer)
	{
		this.pageInitializer = pageInitializer;
	}

	protected PageInitializer getPageInitializer()
	{
		return pageInitializer;
	}

	@Required
	public void setModelService(ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}
}

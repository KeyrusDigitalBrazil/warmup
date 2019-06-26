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
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.navigation.CMSNavigationEntryModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.services.CMSNavigationService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminItemService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;


/**
 * TrashPagePopulator, when an <code>AbstractPage</code>'s pageStatus attribute is changed from "Active" to "Deleted"
 * then any <code>CMSNavigationEntry</code> in any <code>CMSNavigationNode</code> associated with this page will be deleted.
 */
public class TrashPagePopulator implements Populator<Map<String, Object>, ItemModel>
{
	private CMSNavigationService cmsNavigationService;

	private CMSAdminItemService cmsAdminItemService;

	private ModelService modelService;

	private EnumerationService enumerationService;


	/**
	 * {@inheritDoc}
	 * <p>
	 * Removes one to many <code>CMSNavigationEntry</code> in any <code>CMSNavigationNode</code>
	 * that references the <code>AbstractPageModel</code> passed as a parameter
	 *
	 * @param itemModel
	 * 		The <code>AbstractPageModel</code> being populated.
	 * @param map
	 * 		A map with at least two properties:
	 * 		1. <code>AbstractPageModel.PAGESTATUS</code> The required pageStatus.
	 * 		2. <code>AbstractPageMode.UID</code> The uid of the page
	 * @throws ConversionException
	 */
	@Override
	public void populate(final Map<String, Object> map, final ItemModel itemModel) throws ConversionException
	{
		if (itemModel == null)
		{
			throw new ConversionException("Item Model used in the populator should not be null.");
		}
		if (map == null)
		{
			throw new ConversionException("Source map used in the populator should not be null.");
		}
		if (itemModel instanceof AbstractPageModel && isPageBeingTrashed((AbstractPageModel) itemModel, map))
		{

			final String pageUid = ((String) map.get(AbstractPageModel.UID));
			final AbstractPageModel pageModel;
			try
			{
				pageModel = (AbstractPageModel) getCmsAdminItemService().findByUid(pageUid);
			}
			catch (final CMSItemNotFoundException e)
			{
				throw new ConversionException("AbstractPage not found in persistence with id: " + pageUid, e);
			}
			final List<CMSNavigationEntryModel> navigationEntries = getCmsNavigationService()
					.getNavigationEntriesByPage(pageModel);
			getModelService().removeAll(navigationEntries);
		}
	}

	/**
	 * Determines if a page is being trashed by checking the original pageStatus against the required pageStatus
	 * If the pageStatus will change from ACTIVE to DELETED, then the page is considered being trashed.
	 *
	 * @param page
	 * 		The <code>AbstractPageModel</code> being checked for pageStatus change
	 * @param map
	 * 		A map with at least one properties:
	 * 		1. <code>AbstractPageModel.PAGESTATUS</code> The required pageStatus.
	 * @return boolean confirming if the page is being trashed.
	 */
	protected boolean isPageBeingTrashed(final AbstractPageModel page, final Map<String, Object> map)
	{
		final Optional<String> mapPageStatus = Optional.ofNullable((String) map.get(AbstractPageModel.PAGESTATUS));
		boolean status = false;
		if (mapPageStatus.isPresent()) {
			final CmsPageStatus requiredPageStatus = getEnumerationService().getEnumerationValue(CmsPageStatus.class, mapPageStatus.get().toLowerCase());
			final CmsPageStatus originalPageStatus = page.getItemModelContext().getOriginalValue(AbstractPageModel.PAGESTATUS);
			if (requiredPageStatus == CmsPageStatus.DELETED && originalPageStatus == CmsPageStatus.ACTIVE)
			{
				status = true;
			}
		}
		return status;
	}

	public CMSNavigationService getCmsNavigationService()
	{
		return cmsNavigationService;
	}

	@Required
	public void setCmsNavigationService(final CMSNavigationService cmsNavigationService)
	{
		this.cmsNavigationService = cmsNavigationService;
	}

	protected CMSAdminItemService getCmsAdminItemService()
	{
		return cmsAdminItemService;
	}

	@Required
	public void setCmsAdminItemService(final CMSAdminItemService cmsAdminItemService)
	{
		this.cmsAdminItemService = cmsAdminItemService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}


	protected EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	@Required
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}
}

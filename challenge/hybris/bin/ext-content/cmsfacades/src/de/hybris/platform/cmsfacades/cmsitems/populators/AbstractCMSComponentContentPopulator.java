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

import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminContentSlotService;
import de.hybris.platform.cmsfacades.common.validator.ValidationDtoFactory;
import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.cmsfacades.dto.ComponentTypeAndContentSlotValidationDto;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Strings;


/***
 * Default implementation to populate an Abstract CMS Component Model.
 * This populator is specifically defining assigning the AbstractCMSComponent into a ContentSlot in its position.
 * This is valid for any case where the {@code slotId} and the {@code position} attributes are present in the source Map.
 */
public class AbstractCMSComponentContentPopulator implements Populator<Map<String, Object>, ItemModel>
{

	public static final String POSITION = "position";
	public static final String SLOT_ID = "slotId";
	public static final String PAGE_ID = "pageId";

	private CMSAdminContentSlotService contentSlotAdminService;

	private UniqueItemIdentifierService uniqueItemIdentifierService;

	private ValidationDtoFactory validationDtoFactory;
	private Predicate<ComponentTypeAndContentSlotValidationDto> componentTypeAllowedForContentSlotPredicate;

	/*
	 * Suppress sonar warning (squid:S2583 | Conditions should not unconditionally evaluate to "TRUE" or to "FALSE") :
	 * The condition is does not always evaluate to "TRUE" or to "FALSE".
	 */
	@SuppressWarnings("squid:S2583")
	@Override
	public void populate(final Map<String, Object> source, final ItemModel itemModel) throws ConversionException
	{
		if (itemModel == null)
		{
			throw new ConversionException("Item Model used in the populator should not be null.");
		}
		if (source == null)
		{
			throw new ConversionException("Source map used in the populator should not be null.");
		}
		final String slotUuid = (String) source.get(SLOT_ID);
		final Integer position = (Integer) source.get(POSITION);
		final String pageUuid = (String) source.get(PAGE_ID);
		if (position == null ^ slotUuid == null)
		{
			throw new ConversionException("Cannot add/modify component position when either position or slotUuid parameter is empty.");
		}
		
		// if position and slotUuid are not present, then nothing to do after. 
		if (position == null && slotUuid == null)
		{
			return;
		}

		if (!(itemModel instanceof AbstractCMSComponentModel))
		{
			throw new ConversionException("Invalid Item Model. Should be an instance of AbstractCMSComponentModel, but was " + itemModel.getClass() + ".");
		}
		final ContentSlotModel contentSlotModel = getUniqueItemIdentifierService() //
				.getItemModel(slotUuid, ContentSlotModel.class) //
				.orElseThrow(() -> new ConversionException("Content Slot could not be found."));

		getContentSlotAdminService() //
		.addCMSComponentToContentSlot((AbstractCMSComponentModel) itemModel, contentSlotModel, position);
		
		// if pageUuid is not present, then nothing to do after
		if (pageUuid == null)
		{
			return;
		}

		final AbstractPageModel pageModel = getUniqueItemIdentifierService() //
				.getItemModel(pageUuid, AbstractPageModel.class).orElseThrow(
						() -> new ConversionException("Page [" + pageUuid + "] could not be found."));
		
		final ComponentTypeAndContentSlotValidationDto validationDto = getValidationDtoFactory()
				.buildComponentTypeAndContentSlotValidationDto(itemModel.getItemtype(), 
						contentSlotModel.getUid(), 
						pageModel.getUid());

		if (!getComponentTypeAllowedForContentSlotPredicate().test(validationDto))
		{
			throw new ConversionException("Component not allowed to be in this slot and page.");
		}
	}

	protected CMSAdminContentSlotService getContentSlotAdminService()
	{
		return contentSlotAdminService;
	}

	@Required
	public void setContentSlotAdminService(final CMSAdminContentSlotService contentSlotAdminService)
	{
		this.contentSlotAdminService = contentSlotAdminService;
	}

	protected UniqueItemIdentifierService getUniqueItemIdentifierService()
	{
		return uniqueItemIdentifierService;
	}

	@Required
	public void setUniqueItemIdentifierService(final UniqueItemIdentifierService uniqueItemIdentifierService)
	{
		this.uniqueItemIdentifierService = uniqueItemIdentifierService;
	}
	
	protected ValidationDtoFactory getValidationDtoFactory()
	{
		return validationDtoFactory;
	}

	@Required
	public void setValidationDtoFactory(final ValidationDtoFactory validationDtoFactory)
	{
		this.validationDtoFactory = validationDtoFactory;
	}

	protected Predicate<ComponentTypeAndContentSlotValidationDto> getComponentTypeAllowedForContentSlotPredicate()
	{
		return componentTypeAllowedForContentSlotPredicate;
	}

	@Required
	public void setComponentTypeAllowedForContentSlotPredicate(
			final Predicate<ComponentTypeAndContentSlotValidationDto> componentTypeAllowedForContentSlotPredicate)
	{
		this.componentTypeAllowedForContentSlotPredicate = componentTypeAllowedForContentSlotPredicate;
	}

}

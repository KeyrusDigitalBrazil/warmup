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
package de.hybris.platform.cmsfacades.rendering.populators;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.servicelayer.data.ContentSlotData;
import de.hybris.platform.cmsfacades.cmsitems.attributeconverters.UniqueIdentifierAttributeToDataContentConverter;
import de.hybris.platform.cmsfacades.data.AbstractCMSComponentData;
import de.hybris.platform.cmsfacades.data.PageContentSlotData;
import de.hybris.platform.cmsfacades.rendering.visibility.RenderingVisibilityService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Required;

import java.util.stream.Collectors;



/**
 * Populator used to add all the information required to render a content slot.
 */
public class ContentSlotModelToDataRenderingPopulator implements Populator<ContentSlotData, PageContentSlotData>
{
	// --------------------------------------------------------------------------
	// Variables
	// --------------------------------------------------------------------------
	private RenderingVisibilityService renderingVisibilityService;
	private Converter<AbstractCMSComponentModel, AbstractCMSComponentData> cmsComponentModelToDataRenderingConverter;
	private UniqueIdentifierAttributeToDataContentConverter<CatalogVersionModel> uniqueIdentifierAttributeToDataContentConverter;

	// --------------------------------------------------------------------------
	// Public API
	// --------------------------------------------------------------------------
	@Override
	public void populate(final ContentSlotData contentSlotData, final PageContentSlotData targetData)
	{
		// Basic data
		final ContentSlotModel slotModel = contentSlotData.getContentSlot();
		targetData.setSlotId(contentSlotData.getUid());
		targetData.setPosition(contentSlotData.getPosition());
		targetData.setName(contentSlotData.getName());
		targetData.setSlotShared(contentSlotData.isFromMaster());
		targetData.setCatalogVersion(getUniqueIdentifierAttributeToDataContentConverter().convert(slotModel.getCatalogVersion()));

		// Components
		targetData.setComponents(
				slotModel.getCmsComponents() //
						.stream() //
						.filter(getRenderingVisibilityService()::isVisible) //
						.map(component -> getCmsComponentModelToDataRenderingConverter().convert(component)) //
						.collect(Collectors.toList())
		);
	}

	// --------------------------------------------------------------------------
	// Getters/Setters
	// --------------------------------------------------------------------------
	protected Converter<AbstractCMSComponentModel, AbstractCMSComponentData> getCmsComponentModelToDataRenderingConverter()
	{
		return cmsComponentModelToDataRenderingConverter;
	}

	@Required
	public void setCmsComponentModelToDataRenderingConverter(
			final Converter<AbstractCMSComponentModel, AbstractCMSComponentData> cmsComponentModelToDataRenderingConverter)
	{
		this.cmsComponentModelToDataRenderingConverter = cmsComponentModelToDataRenderingConverter;
	}

	@Required
	protected UniqueIdentifierAttributeToDataContentConverter<CatalogVersionModel> getUniqueIdentifierAttributeToDataContentConverter()
	{
		return uniqueIdentifierAttributeToDataContentConverter;
	}

	public void setUniqueIdentifierAttributeToDataContentConverter(
			UniqueIdentifierAttributeToDataContentConverter<CatalogVersionModel> uniqueIdentifierAttributeToDataContentConverter)
	{
		this.uniqueIdentifierAttributeToDataContentConverter = uniqueIdentifierAttributeToDataContentConverter;
	}

	protected RenderingVisibilityService getRenderingVisibilityService()
	{
		return renderingVisibilityService;
	}

	@Required
	public void setRenderingVisibilityService(
			RenderingVisibilityService renderingVisibilityService)
	{
		this.renderingVisibilityService = renderingVisibilityService;
	}
}

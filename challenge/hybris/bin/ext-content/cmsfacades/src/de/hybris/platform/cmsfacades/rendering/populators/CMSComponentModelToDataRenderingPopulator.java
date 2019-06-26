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
import de.hybris.platform.cmsfacades.cmsitems.CMSItemConverter;
import de.hybris.platform.cmsfacades.cmsitems.attributeconverters.UniqueIdentifierAttributeToDataContentConverter;
import de.hybris.platform.cmsfacades.data.AbstractCMSComponentData;
import de.hybris.platform.converters.Populator;
import org.springframework.beans.factory.annotation.Required;


/**
 * Populator used to add all the information required to render a CMS Component.
 */
public class CMSComponentModelToDataRenderingPopulator implements Populator<AbstractCMSComponentModel, AbstractCMSComponentData>
{

	// --------------------------------------------------------------------------
	// Variables
	// --------------------------------------------------------------------------
	private CMSItemConverter cmsItemConverter;
	private UniqueIdentifierAttributeToDataContentConverter<CatalogVersionModel> uniqueIdentifierAttributeToDataContentConverter;

	// --------------------------------------------------------------------------
	// Public API
	// --------------------------------------------------------------------------
	@Override
	public void populate(AbstractCMSComponentModel componentModel,
			AbstractCMSComponentData componentData)
	{
		componentData.setUid(componentModel.getUid());
		componentData.setTypeCode(componentModel.getItemtype());
		componentData.setModifiedtime(componentModel.getModifiedtime());
		componentData.setName(componentModel.getName());

		componentData.setCatalogVersion(getUniqueIdentifierAttributeToDataContentConverter()
				.convert(componentModel.getCatalogVersion()));
		componentData.setOtherProperties(getCmsItemConverter().convert(componentModel));
	}

	// --------------------------------------------------------------------------
	// Getters/Setters
	// --------------------------------------------------------------------------
	protected CMSItemConverter getCmsItemConverter()
	{
		return cmsItemConverter;
	}

	@Required
	public void setCmsItemConverter(CMSItemConverter cmsItemConverter)
	{
		this.cmsItemConverter = cmsItemConverter;
	}

	protected UniqueIdentifierAttributeToDataContentConverter<CatalogVersionModel> getUniqueIdentifierAttributeToDataContentConverter()
	{
		return uniqueIdentifierAttributeToDataContentConverter;
	}

	@Required
	public void setUniqueIdentifierAttributeToDataContentConverter(
			UniqueIdentifierAttributeToDataContentConverter<CatalogVersionModel> uniqueIdentifierAttributeToDataContentConverter)
	{
		this.uniqueIdentifierAttributeToDataContentConverter = uniqueIdentifierAttributeToDataContentConverter;
	}
}

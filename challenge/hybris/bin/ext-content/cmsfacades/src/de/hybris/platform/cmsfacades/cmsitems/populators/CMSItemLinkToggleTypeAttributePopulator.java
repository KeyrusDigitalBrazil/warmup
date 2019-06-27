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

import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.cmsfacades.data.ComponentTypeAttributeData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


/**
 * Populator that removes external and urlLink fields from structure and create a new field linkToggle.
 */
public class CMSItemLinkToggleTypeAttributePopulator implements Populator<AttributeDescriptorModel, ComponentTypeAttributeData>
{
	@Override
	public void populate(AttributeDescriptorModel source,
			ComponentTypeAttributeData target) throws ConversionException
	{
		if (source.getQualifier().equals(CmsfacadesConstants.FIELD_EXTERNAL_NAME))
		{
			target.setCmsStructureType(null);
		}

		if (source.getQualifier().equals(CmsfacadesConstants.FIELD_URL_LINK_NAME))
		{
			target.setQualifier(CmsfacadesConstants.FIELD_LINK_TOGGLE_NAME);
			target.setCmsStructureType("LinkToggle");
			target.setI18nKey("se.editor.linkto.label");
		}
	}
}

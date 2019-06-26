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
package de.hybris.platform.cmsfacades.types.populator;

import de.hybris.platform.cmsfacades.data.ComponentTypeAttributeData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.springframework.beans.factory.annotation.Required;

/**
 * Populator for restriction attributes. Restrictions are part of pages and component attributes, but 
 * they are edited in a different context than other properties (visibility tab). Thus, they must be present in the component 
 * API, but not in the structure API. This populator makes sure the property is not present in the structure
 * of the enclosing type. 
 */
public class RestrictionComponentTypeAttributePopulator implements Populator<AttributeDescriptorModel, ComponentTypeAttributeData>
{

    @Override
	public void populate(final AttributeDescriptorModel source, final ComponentTypeAttributeData target) throws ConversionException
	{
        // By setting the structure type to null the structure API make sure this attribute
        // is not part of the structure. 
        target.setCmsStructureType(null);
	}

}

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
package com.hybris.ymkt.segmentation.populators;

import de.hybris.platform.cmsfacades.data.ComponentTypeAttributeData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;


public class CampaignRestrictionDropdownComponentTypeAttributePopulator
		implements Populator<AttributeDescriptorModel, ComponentTypeAttributeData>
{
	
	/**
	 * The following method will set the URI of the seDropdown. 
	 * The seDropdown will call the following Uri to populate its values
	 */
	@Override
	public void populate(final AttributeDescriptorModel source, final ComponentTypeAttributeData target)
	{
		target.setUri("/sapymktsegmentationwebservices/v1/data/segmentation/" + source.getQualifier());
		target.setPaged(true);
		target.setEditable(true);
		target.setRequired(true);
	}
}
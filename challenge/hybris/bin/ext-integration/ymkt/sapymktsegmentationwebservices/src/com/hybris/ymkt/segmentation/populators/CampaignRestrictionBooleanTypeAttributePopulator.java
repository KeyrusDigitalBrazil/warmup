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

public class CampaignRestrictionBooleanTypeAttributePopulator
		implements Populator<AttributeDescriptorModel, ComponentTypeAttributeData>
{
	/**
	 * Set the default value to value defined in item type.
	 * This this case, set to TRUE.<br>
	 * This ensures the default behavior of "restriction applies when user is a member of campaign"
	 */
	@Override
	public void populate(final AttributeDescriptorModel source, final ComponentTypeAttributeData target) 
	{
		target.setDefaultValue((Boolean) source.getDefaultValue());
		target.setEditable(true);
	}
}
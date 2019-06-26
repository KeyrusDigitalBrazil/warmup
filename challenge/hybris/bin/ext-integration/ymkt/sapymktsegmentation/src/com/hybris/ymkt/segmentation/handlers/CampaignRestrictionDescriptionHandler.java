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
package com.hybris.ymkt.segmentation.handlers;

import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;
import de.hybris.platform.util.localization.Localization;

import com.hybris.ymkt.segmentation.model.CMSYmktCampaignRestrictionModel;



/**
 * 
 */
public class CampaignRestrictionDescriptionHandler implements DynamicAttributeHandler<String, CMSYmktCampaignRestrictionModel>
{
	@Override
	public String get(final CMSYmktCampaignRestrictionModel model)
	{
		return Localization.getLocalizedString("type.CMSYmktCampaignRestriction.description");
	}

	@Override
	public void set(final CMSYmktCampaignRestrictionModel model, final String value)
	{
		throw new UnsupportedOperationException();
	}
}

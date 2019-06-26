/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.acceleratorcms.services.impl;

import de.hybris.platform.acceleratorcms.services.CMSDynamicAttributeService;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;

import java.util.Collections;
import java.util.Map;

import javax.servlet.jsp.PageContext;


/**
 * Default implementation of {@link CMSDynamicAttributeService}.
 */
public class DefaultCMSDynamicAttributeService implements CMSDynamicAttributeService
{
	@Override
	public Map<String, String> getDynamicComponentAttributes(final AbstractCMSComponentModel component,
			final ContentSlotModel contentSlot)
	{
		return Collections.emptyMap();
	}

	@Override
	public Map<String, String> getDynamicContentSlotAttributes(final ContentSlotModel contentSlot, final PageContext pageContext,
			final Map<String, String> initialMaps)
	{
		return Collections.emptyMap();
	}

	@Override
	public void afterAllItems(final PageContext pageContext)
	{
		// no-op default implementation
	}

	@Override
	public String getFallbackElement(final CMSItemModel cmsItemModel)
	{
		return null;
	}
}

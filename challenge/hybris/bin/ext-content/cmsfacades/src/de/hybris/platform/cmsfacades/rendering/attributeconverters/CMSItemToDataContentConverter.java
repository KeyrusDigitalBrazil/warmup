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
package de.hybris.platform.cmsfacades.rendering.attributeconverters;

import de.hybris.platform.cms2.common.functions.Converter;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cmsfacades.rendering.visibility.RenderingVisibilityService;
import org.springframework.beans.factory.annotation.Required;

import java.util.Optional;


/**
 * Rendering Attribute Converter for {@link de.hybris.platform.cms2.model.contents.CMSItemModel}.
 * Converts the item into its UID (String) representation.
 */
public class CMSItemToDataContentConverter implements Converter<CMSItemModel, String>
{
	private RenderingVisibilityService renderingVisibilityService;

	@Override
	public String convert(CMSItemModel source)
	{
		return Optional.ofNullable(source)
				.filter(getRenderingVisibilityService()::isVisible)
				.map(CMSItemModel::getUid)
				.orElse(null);
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

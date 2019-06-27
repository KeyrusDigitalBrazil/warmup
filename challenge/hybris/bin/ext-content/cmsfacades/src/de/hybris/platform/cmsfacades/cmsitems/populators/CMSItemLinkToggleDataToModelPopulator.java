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

import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cmsfacades.cmsitems.predicates.ModelContainsLinkTogglePredicate;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.*;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.springframework.beans.factory.annotation.Required;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


/**
 * Populator that extracts two fields (urlLink, external) from linkToggle field and populates CMSItemModel.
 * CMSItemModel should contain both fields at the same time.
 */
public class CMSItemLinkToggleDataToModelPopulator implements Populator<Map<String, Object>, CMSItemModel>
{
	private ModelContainsLinkTogglePredicate cmsModelContainsLinkTogglePredicate;

	@Override
	public void populate(Map<String, Object> source, CMSItemModel itemModel)
			throws ConversionException
	{
		if (getCmsModelContainsLinkTogglePredicate().test(itemModel))
		{
			invokeMethod(source, itemModel, "setUrlLink", FIELD_URL_LINK_NAME, String.class);
			invokeMethod(source, itemModel, "setExternal", FIELD_EXTERNAL_NAME, boolean.class);
		}
	}

	protected void invokeMethod(Map<String, Object> source, final CMSItemModel itemModel, final String methodName,
			final String fieldName,
			final Class methodArgumentClass)
	{
		try
		{
			Map linkToggle = (HashMap) source.get(FIELD_LINK_TOGGLE_NAME);
			Method methodSetUrlLink = itemModel.getClass().getMethod(methodName, methodArgumentClass);
			methodSetUrlLink.invoke(itemModel, linkToggle.get(fieldName));
		}
		catch (NoSuchMethodException e)
		{
			throw new ConversionException("Can not extract method", e);
		}
		catch (IllegalAccessException | InvocationTargetException e)
		{
			throw new ConversionException("Can not invoke method", e);
		}
	}

	protected ModelContainsLinkTogglePredicate getCmsModelContainsLinkTogglePredicate()
	{
		return cmsModelContainsLinkTogglePredicate;
	}

	@Required
	public void setCmsModelContainsLinkTogglePredicate(
			ModelContainsLinkTogglePredicate cmsModelContainsLinkTogglePredicate)
	{
		this.cmsModelContainsLinkTogglePredicate = cmsModelContainsLinkTogglePredicate;
	}
}

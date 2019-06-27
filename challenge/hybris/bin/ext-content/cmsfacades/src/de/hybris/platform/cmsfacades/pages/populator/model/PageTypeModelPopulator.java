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
package de.hybris.platform.cmsfacades.pages.populator.model;

import de.hybris.platform.cms2.common.annotations.HybrisDeprecation;
import de.hybris.platform.cmsfacades.data.PageTypeData;
import de.hybris.platform.cmsfacades.pages.service.PageTypeMappingRegistry;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populator to populate the basic attribute from a <code>ComposedTypeModel</code> to a <code>PageTypeData</code>. <br>
 * <br>
 *
 * The type value is used when creating a page. This information is required by the HTTP message converter to determine
 * how to unmarshall the request object body correctly. If no type value is found in the request object body, the HTTP
 * message converter will convert the dto into an <code>AbstractPageModel</code> instead of a specific page type model
 * such as <code>ContentPageModel</code>, <code>ProductPageModel</code>, etc.
 * <p>
 * Will populate:</br>
 * <ul>
 * <li><code>type</code></li>
 * </ul>
 * </p>
 * 
 * @deprecated since 6.6
 */
@Deprecated
@HybrisDeprecation(sinceVersion = "6.6")
public class PageTypeModelPopulator implements Populator<ComposedTypeModel, PageTypeData>
{
	private PageTypeMappingRegistry pageTypeMappingRegistry;

	@Override
	public void populate(final ComposedTypeModel source, final PageTypeData target) throws ConversionException
	{
		Optional.ofNullable(source.getCode()) //
		.ifPresent(typeCode -> getPageTypeMappingRegistry().getPageTypeMapping(typeCode) //
				.ifPresent(typeMapping -> target.setType(createTypedataForClass(typeMapping.getTypedata()))));
	}

	/**
	 * Create a typedata value based on the given class name. The typedata must start with a lower case letter.
	 *
	 * @param clazz
	 *           - the class used to create a typedata
	 * @return a typedata
	 */
	protected String createTypedataForClass(final Class<?> clazz)
	{
		final String name = clazz.getSimpleName();
		return Character.toLowerCase(name.charAt(0)) + name.substring(1);
	}

	protected PageTypeMappingRegistry getPageTypeMappingRegistry()
	{
		return pageTypeMappingRegistry;
	}

	@Required
	public void setPageTypeMappingRegistry(final PageTypeMappingRegistry pageTypeMappingRegistry)
	{
		this.pageTypeMappingRegistry = pageTypeMappingRegistry;
	}
}

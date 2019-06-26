/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.integrationservices;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.servicelayer.type.TypeService;

import org.apache.commons.lang.StringUtils;

/**
 * A builder for {@link IntegrationObjectItemAttributeModel}
 */
public class IntegrationObjectItemAttributeBuilder
{
	private TypeService typeService;
	private ComposedTypeModel integrationObjectType;
	private String attributeName;
	private String attributeDescriptorName;
	private IntegrationObjectItemModel returnIntegrationObjectItem;
	private boolean unique;

	/**
	 * Creates new instance of this builder.
	 * @return new "empty" builder instance.
	 */
	public static IntegrationObjectItemAttributeBuilder attribute()
	{
		return new IntegrationObjectItemAttributeBuilder();
	}

	public IntegrationObjectItemAttributeBuilder named(final String name)
	{
		attributeName = name;
		return this;
	}

	public IntegrationObjectItemAttributeBuilder withDescriptorName(final String name)
	{
		attributeDescriptorName = name;
		return this;
	}

	public IntegrationObjectItemAttributeBuilder forObjectOfType(final ComposedTypeModel model)
	{
		integrationObjectType = model;
		return this;
	}

	public IntegrationObjectItemAttributeBuilder returnIntegrationObjectType(final IntegrationObjectItemModel referenceType)
	{
		this.returnIntegrationObjectItem = referenceType;
		return this;
	}

	public IntegrationObjectItemAttributeBuilder unique()
	{
		this.unique = true;
		return this;
	}

	/**
	 * Creates new instance of the attribute. Subsequent calls on the same builder instance result in different instance
	 * of the attribute returned, but the instances will have exactly same properties as they were specified by other
	 * method calls prior to calling {@code build()}.
	 * @return new {@code IntegrationObjectItemAttributeModel} instance.
	 */
	public IntegrationObjectItemAttributeModel build()
	{
		final IntegrationObjectItemAttributeModel attr = new IntegrationObjectItemAttributeModel();
		attr.setAttributeDescriptor(findAttributeDescriptor());
		attr.setAttributeName(attributeName);
		attr.setReturnIntegrationObjectItem(returnIntegrationObjectItem);
		attr.setUnique(unique);
		return attr;
	}

	private AttributeDescriptorModel findAttributeDescriptor()
	{
		return typeService().getAttributeDescriptor(integrationObjectType, StringUtils.defaultIfBlank(attributeDescriptorName, attributeName));
	}

	private TypeService typeService()
	{
		if (typeService == null)
		{
			typeService = Registry.getApplicationContext().getBean("typeService", TypeService.class);
		}
		return typeService;
	}
}

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
package de.hybris.platform.odata2services.odata.schema.property;

import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.model.impl.DefaultTypeAttributeDescriptor;
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.olingo.odata2.api.edm.provider.Property;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;

public abstract class AbstractPropertyListGenerator implements SchemaElementGenerator<List<Property>, IntegrationObjectItemModel>
{
	private SchemaElementGenerator<Property, IntegrationObjectItemAttributeModel> propertyGenerator;

	protected void validatePreconditions(final IntegrationObjectItemModel itemModel)
	{
		Preconditions.checkArgument(itemModel != null,
				"A Property list cannot be generated from a null parameter");
	}

	protected List<Property> generateProperties(final Set<IntegrationObjectItemAttributeModel> attributeModels)
	{
		return attributeModels.stream()
				.filter(this::isPrimitive)
				.map(getPropertyGenerator()::generate)
				.collect(Collectors.toList());
	}

	private boolean isPrimitive(final IntegrationObjectItemAttributeModel attr)
	{
		final TypeAttributeDescriptor descriptor = asDescriptor(attr);
		return descriptor.isPrimitive() && !descriptor.isCollection();
	}

	protected SchemaElementGenerator<Property, IntegrationObjectItemAttributeModel> getPropertyGenerator()
	{
		return propertyGenerator;
	}

	protected TypeAttributeDescriptor asDescriptor(final IntegrationObjectItemAttributeModel attributeModel)
	{
		return DefaultTypeAttributeDescriptor.create(attributeModel);
	}

	@Required
	public void setPropertyGenerator(final SchemaElementGenerator<Property, IntegrationObjectItemAttributeModel> propertyGenerator)
	{
		this.propertyGenerator = propertyGenerator;
	}
}

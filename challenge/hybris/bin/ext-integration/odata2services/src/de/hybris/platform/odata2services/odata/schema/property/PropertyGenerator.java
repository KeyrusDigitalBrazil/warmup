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

import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.model.impl.DefaultTypeAttributeDescriptor;
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator;
import de.hybris.platform.odata2services.odata.schema.attribute.AttributeAnnotationListGenerator;
import de.hybris.platform.odata2services.odata.schema.utils.EdmTypeUtils;

import java.util.List;

import org.apache.olingo.odata2.api.edm.provider.AnnotationAttribute;
import org.apache.olingo.odata2.api.edm.provider.Property;
import org.apache.olingo.odata2.api.edm.provider.SimpleProperty;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;


public class PropertyGenerator implements SchemaElementGenerator<Property, IntegrationObjectItemAttributeModel>
{
	private SchemaElementGenerator<List<AnnotationAttribute>, IntegrationObjectItemAttributeModel> attributeListGenerator;

	@Override
	public Property generate(final IntegrationObjectItemAttributeModel attributeModel)
	{
		Preconditions.checkArgument(attributeModel != null,
				"A Property cannot be generated from a null IntegrationObjectItemAttributeModel");
		final AttributeDescriptorModel attributeDescriptor = attributeModel.getAttributeDescriptor();
		Preconditions.checkArgument(attributeDescriptor != null,
				"A Property cannot be generated from a null AttributeDescriptorModel");

		final TypeAttributeDescriptor attribute = asDescriptor(attributeModel);
		return new SimpleProperty()
				.setName(attribute.getAttributeName())
				.setType(EdmTypeUtils.convert(attribute.getAttributeType().getTypeCode()))
				.setAnnotationAttributes(attributeListGenerator.generate(attributeModel));
	}

	protected TypeAttributeDescriptor asDescriptor(final IntegrationObjectItemAttributeModel attributeModel)
	{
		return DefaultTypeAttributeDescriptor.create(attributeModel);
	}

	@Required
	public void setAttributeListGenerator(final AttributeAnnotationListGenerator generator)
	{
		attributeListGenerator = generator;
	}
}

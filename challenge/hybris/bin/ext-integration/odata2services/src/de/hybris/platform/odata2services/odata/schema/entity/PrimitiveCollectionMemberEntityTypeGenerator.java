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
package de.hybris.platform.odata2services.odata.schema.entity;

import static de.hybris.platform.odata2services.constants.Odata2servicesConstants.PRIMITIVE_ENTITY_PROPERTY_NAME;
import static org.apache.commons.lang3.ClassUtils.getShortClassName;

import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator;
import de.hybris.platform.odata2services.odata.schema.utils.EdmTypeUtils;

import java.util.Collections;

import org.apache.olingo.odata2.api.edm.provider.AnnotationAttribute;
import org.apache.olingo.odata2.api.edm.provider.EntityType;
import org.apache.olingo.odata2.api.edm.provider.Key;
import org.apache.olingo.odata2.api.edm.provider.Property;
import org.apache.olingo.odata2.api.edm.provider.PropertyRef;
import org.apache.olingo.odata2.api.edm.provider.SimpleProperty;

/**
 * Generates an {@link EntityType} for the primitive collection type passed in.
 * The type passed in is assumed to be primitive, it is not checked.
 */
public class PrimitiveCollectionMemberEntityTypeGenerator implements SchemaElementGenerator<EntityType, String>
{
	@Override
	public EntityType generate(final String primitiveType)
	{
		return new EntityType()
				.setName(getShortClassName(primitiveType))
				.setProperties(Collections.singletonList(valueProperty(primitiveType)))
				.setKey(new Key().setKeys(Collections.singletonList(new PropertyRef().setName(PRIMITIVE_ENTITY_PROPERTY_NAME))));
	}

	private Property valueProperty(final String primitiveType)
	{
		return new SimpleProperty()
				.setName(PRIMITIVE_ENTITY_PROPERTY_NAME)
				.setType(EdmTypeUtils.convert(primitiveType))
				.setAnnotationAttributes(Collections.singletonList(nullableAnnotation()));
	}

	private AnnotationAttribute nullableAnnotation()
	{
		return new AnnotationAttribute().setName("Nullable").setText("false");
	}
}

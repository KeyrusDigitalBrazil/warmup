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

import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.model.TypeDescriptor;
import de.hybris.platform.integrationservices.model.impl.ItemTypeDescriptor;
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator;

import java.util.Set;
import java.util.stream.Collectors;

import org.apache.olingo.odata2.api.edm.provider.EntityType;
import org.springframework.beans.factory.annotation.Required;

public class PrimitiveEntityTypeGenerator implements SchemaElementGenerator<Set<EntityType>, IntegrationObjectItemModel>
{
	private SchemaElementGenerator<EntityType, String> primitiveCollectionMemberEntityTypeGenerator;

	@Override
	public Set<EntityType> generate(final IntegrationObjectItemModel itemModel)
	{
		final TypeDescriptor typeDescriptor = ItemTypeDescriptor.create(itemModel);

		final Set<String> simpleTypes = getTypesFromPrimitiveCollections(typeDescriptor);

		return simpleTypes.stream()
				.map(primitiveCollectionMemberEntityTypeGenerator::generate)
				.collect(Collectors.toSet());
	}

	private static Set<String> getTypesFromPrimitiveCollections(final TypeDescriptor itemTypeDescriptor)
	{
		return itemTypeDescriptor.getAttributes().stream()
				.filter(TypeAttributeDescriptor::isCollection)
				.map(TypeAttributeDescriptor::getAttributeType)
				.filter(TypeDescriptor::isPrimitive)
				.map(TypeDescriptor::getTypeCode)
				.collect(Collectors.toSet());
	}

	@Required
	public void setPrimitiveCollectionMemberEntityTypeGenerator(final SchemaElementGenerator<EntityType, String> primitiveCollectionMemberEntityTypeGenerator)
	{
		this.primitiveCollectionMemberEntityTypeGenerator = primitiveCollectionMemberEntityTypeGenerator;
	}
}

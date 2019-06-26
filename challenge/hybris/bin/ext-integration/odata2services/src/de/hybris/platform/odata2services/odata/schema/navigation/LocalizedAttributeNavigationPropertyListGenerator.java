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
package de.hybris.platform.odata2services.odata.schema.navigation;

import static de.hybris.platform.odata2services.constants.Odata2servicesConstants.LOCALIZED_ATTRIBUTE_NAME;
import static de.hybris.platform.odata2services.odata.schema.utils.SchemaUtils.buildAssociationName;
import static de.hybris.platform.odata2services.odata.schema.utils.SchemaUtils.localizedEntityName;
import static de.hybris.platform.odata2services.odata.schema.utils.SchemaUtils.toFullQualifiedName;

import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator;
import de.hybris.platform.odata2services.odata.schema.attribute.ImmutableAnnotationAttribute;
import de.hybris.platform.odata2services.odata.schema.utils.SchemaUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.olingo.odata2.api.edm.FullQualifiedName;
import org.apache.olingo.odata2.api.edm.provider.AnnotationAttribute;
import org.apache.olingo.odata2.api.edm.provider.NavigationProperty;

import com.google.common.base.Preconditions;

/**
 * The LocalizedAttributeNavigationPropertyListGenerator creates the navigation property that associates
 * the localized entity with this entity. Since there is only one localized navigation property
 * per entity, this generator returns a collection containing one navigation property.
 */
public class LocalizedAttributeNavigationPropertyListGenerator implements SchemaElementGenerator<List<NavigationProperty>, Collection<IntegrationObjectItemAttributeModel>>
{
	private static final AnnotationAttribute NULLABLE_ATTRIBUTE = new ImmutableAnnotationAttribute().setName("Nullable").setText("true");

	@Override
	public List<NavigationProperty> generate(final Collection<IntegrationObjectItemAttributeModel> attributeModels)
	{
		Preconditions.checkArgument(attributeModels != null,
				"A NavigationProperty list cannot be generated from a null parameter");

		final Optional<TypeAttributeDescriptor> descriptorOptional = findFirstLocalizedAttribute(attributeModels);
		if (descriptorOptional.isPresent())
		{
			return Collections.singletonList(createNavigationProperty(descriptorOptional.get()));
		}
		return Collections.emptyList();
	}

	private NavigationProperty createNavigationProperty(final TypeAttributeDescriptor descriptor)
	{
		final String typeCode = descriptor.getTypeDescriptor().getTypeCode();
		return new NavigationProperty()
				.setName(LOCALIZED_ATTRIBUTE_NAME)
				.setRelationship(associationName(typeCode))
				.setFromRole(typeCode)
				.setToRole(localizedEntityName(typeCode))
				.setAnnotationAttributes(Collections.singletonList(NULLABLE_ATTRIBUTE));
	}

	/**
	 * Finds the first localized attribute from the {@link Collection} of attributes
	 *
	 * @param attributeModels Collection of attributes
	 * @return An {@link Optional} containing the {@link TypeAttributeDescriptor} if found, otherwise empty
	 */
	protected Optional<TypeAttributeDescriptor> findFirstLocalizedAttribute(final Collection<IntegrationObjectItemAttributeModel> attributeModels)
	{
		return SchemaUtils.findFirstLocalizedAttribute(attributeModels);
	}

	private static FullQualifiedName associationName(final String typeCode)
	{
		return toFullQualifiedName(buildAssociationName(typeCode, LOCALIZED_ATTRIBUTE_NAME));
	}
}

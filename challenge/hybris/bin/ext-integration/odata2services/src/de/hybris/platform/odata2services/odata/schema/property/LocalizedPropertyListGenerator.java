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

import static de.hybris.platform.odata2services.constants.Odata2servicesConstants.LANGUAGE_KEY_PROPERTY_NAME;

import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.odata2services.odata.schema.attribute.ImmutableAnnotationAttribute;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.olingo.odata2.api.edm.EdmSimpleTypeKind;
import org.apache.olingo.odata2.api.edm.provider.AnnotationAttribute;
import org.apache.olingo.odata2.api.edm.provider.Property;
import org.apache.olingo.odata2.api.edm.provider.SimpleProperty;

public class LocalizedPropertyListGenerator extends AbstractPropertyListGenerator
{
	private static final AnnotationAttribute NULLABLE_ATTRIBUTE = new ImmutableAnnotationAttribute().setName("Nullable").setText("false");

	@Override
	public List<Property> generate(final IntegrationObjectItemModel itemModel)
	{
		validatePreconditions(itemModel);

		final Set<IntegrationObjectItemAttributeModel> localizedEntityAttributes = itemModel.getAttributes().stream()
				.filter(attr -> asDescriptor(attr).isLocalized())
				.collect(Collectors.toSet());

		final List<Property> properties = generateProperties(localizedEntityAttributes);
		properties.add(languageProperty());
		return properties;
	}

	public Property languageProperty()
	{
		return new SimpleProperty()
				.setName(LANGUAGE_KEY_PROPERTY_NAME)
				.setType(EdmSimpleTypeKind.String)
				.setAnnotationAttributes(Collections.singletonList(NULLABLE_ATTRIBUTE));
	}
}

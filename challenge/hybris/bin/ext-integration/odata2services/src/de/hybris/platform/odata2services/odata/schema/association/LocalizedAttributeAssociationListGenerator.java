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
package de.hybris.platform.odata2services.odata.schema.association;

import static de.hybris.platform.odata2services.constants.Odata2servicesConstants.LOCALIZED_ATTRIBUTE_NAME;
import static de.hybris.platform.odata2services.odata.schema.utils.SchemaUtils.toFullQualifiedName;

import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator;
import de.hybris.platform.odata2services.odata.schema.utils.SchemaUtils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.olingo.odata2.api.edm.EdmMultiplicity;
import org.apache.olingo.odata2.api.edm.provider.Association;
import org.apache.olingo.odata2.api.edm.provider.AssociationEnd;

import com.google.common.base.Preconditions;

public class LocalizedAttributeAssociationListGenerator implements SchemaElementGenerator<List<Association>, Collection<IntegrationObjectItemModel>>
{
	@Override
	public List<Association> generate(final Collection<IntegrationObjectItemModel> itemModels)
	{
		Preconditions.checkArgument(itemModels != null,
				"An Association list cannot be generated from a null parameter");

		return itemModels.stream()
				.map(IntegrationObjectItemModel::getAttributes)
				.map(this::findFirstLocalizedAttribute)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.map(this::createAssociation)
				.collect(Collectors.toList());
	}

	private Association createAssociation(final TypeAttributeDescriptor typeAttributeDescriptor)
	{
		final String sourceRole = typeAttributeDescriptor.getTypeDescriptor().getTypeCode();
		final String targetRole = SchemaUtils.localizedEntityName(sourceRole);
		return new Association()
				.setName(SchemaUtils.buildAssociationName(sourceRole, LOCALIZED_ATTRIBUTE_NAME))
				.setEnd1(new AssociationEnd()
						.setType(toFullQualifiedName(sourceRole))
						.setRole(sourceRole)
						.setMultiplicity(EdmMultiplicity.ONE))
				.setEnd2(new AssociationEnd()
						.setType(toFullQualifiedName(targetRole))
						.setRole(targetRole)
						.setMultiplicity(EdmMultiplicity.MANY));

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
}

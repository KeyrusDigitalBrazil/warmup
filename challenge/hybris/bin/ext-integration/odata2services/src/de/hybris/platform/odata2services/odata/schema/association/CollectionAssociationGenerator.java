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

import static de.hybris.platform.odata2services.odata.schema.utils.SchemaUtils.toFullQualifiedName;

import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;

import org.apache.olingo.odata2.api.edm.provider.Association;
import org.apache.olingo.odata2.api.edm.provider.AssociationEnd;

public class CollectionAssociationGenerator extends AbstractAssociationGenerator
{
	@Override
	public boolean isApplicable(final IntegrationObjectItemAttributeModel attributeDefinitionModel)
	{
		return asDescriptor(attributeDefinitionModel).isCollection();
	}

	@Override
	public Association generate(final IntegrationObjectItemAttributeModel attributeDefinitionModel)
	{
		final TypeAttributeDescriptor descriptor = asDescriptor(attributeDefinitionModel);
		final String sourceRole = getSourceRole(descriptor);
		return new Association()
				.setName(getAssociationName(descriptor))
				.setEnd1(new AssociationEnd()
						.setType(toFullQualifiedName(sourceRole))
						.setRole(sourceRole)
						.setMultiplicity(getSourceCardinality(descriptor)))
				.setEnd2(new AssociationEnd()
						.setType(toFullQualifiedName(getTargetType(descriptor)))
						.setRole(getTargetRole(descriptor))
						.setMultiplicity(getTargetCardinality(descriptor)));
	}

}

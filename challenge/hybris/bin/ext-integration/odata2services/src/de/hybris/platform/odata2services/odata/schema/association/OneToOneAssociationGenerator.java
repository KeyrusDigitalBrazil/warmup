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

import de.hybris.platform.core.model.enumeration.EnumerationMetaTypeModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;

import org.apache.olingo.odata2.api.edm.EdmMultiplicity;
import org.apache.olingo.odata2.api.edm.provider.Association;
import org.apache.olingo.odata2.api.edm.provider.AssociationEnd;

public class OneToOneAssociationGenerator extends AbstractAssociationGenerator
{
	@Override
	public boolean isApplicable(final IntegrationObjectItemAttributeModel attributeDefinitionModel)
	{
		checkIsApplicablePrecondition(attributeDefinitionModel);
		return ((attributeDefinitionModel.getAttributeDescriptor().getAttributeType() instanceof ComposedTypeModel)
				|| (attributeDefinitionModel.getAttributeDescriptor().getAttributeType() instanceof EnumerationMetaTypeModel));
	}

	@Override
	public Association generate(final IntegrationObjectItemAttributeModel attributeDefinition)
	{
		final String sourceTypeCode = getSourceRole(attributeDefinition);
		final String targetTypeCode = getTargetRole(attributeDefinition);
		return new Association()
				.setName(getAssociationName(attributeDefinition))
				.setEnd1(new AssociationEnd()
						.setType(toFullQualifiedName(sourceTypeCode))
						.setRole(sourceTypeCode)
						.setMultiplicity(EdmMultiplicity.ZERO_TO_ONE))
				.setEnd2(new AssociationEnd()
						.setType(toFullQualifiedName(getTargetType(attributeDefinition)))
						.setRole(targetTypeCode)
						.setMultiplicity(EdmMultiplicity.ZERO_TO_ONE));
	}
}

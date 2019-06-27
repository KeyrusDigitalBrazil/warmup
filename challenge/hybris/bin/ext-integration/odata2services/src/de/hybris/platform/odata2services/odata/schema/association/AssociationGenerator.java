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

import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;

import org.apache.olingo.odata2.api.edm.provider.Association;

public interface AssociationGenerator extends SchemaElementGenerator<Association, IntegrationObjectItemAttributeModel>
{
	/**
	 * Derives the source role from the given attribute definition model
	 * @param attributeDefinitionModel the integration object attribute definition model
	 * @return the source role
	 */
	String getSourceRole(IntegrationObjectItemAttributeModel attributeDefinitionModel);

	/**
	 * Derives the target role from the given attribute definition model
	 * @param attributeDefinitionModel the integration object attribute definition model
	 * @return the target role
	 */
	String getTargetRole(IntegrationObjectItemAttributeModel attributeDefinitionModel);

	/**
	 * Derives the association name from the given attribute definition model
	 * @param attributeDefinitionModel the integration object attribute definition model
	 * @return the association name
	 */
	String getAssociationName(IntegrationObjectItemAttributeModel attributeDefinitionModel);

	/**
	 * Determines if this association generator is applicable for the given attribute
	 * @param attributeDefinitionModel the attribute we are verifying
	 * @return true if applicable, otherwise false
	 */
	boolean isApplicable(IntegrationObjectItemAttributeModel attributeDefinitionModel);
}

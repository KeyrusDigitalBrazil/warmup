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

import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;

public class AssociationGeneratorRegistry
{
	private List<AssociationGenerator> associationGenerators;

	public Optional<AssociationGenerator> getAssociationGenerator(final IntegrationObjectItemAttributeModel attribute)
	{
		Preconditions.checkArgument(attribute != null, "An association generator cannot be created for a null IntegrationObjectItemAttributeModel.");

		return Optional.of(associationGenerators.stream()
				.filter(generator -> generator.isApplicable(attribute))
				.findFirst())
				.orElse(Optional.empty());
	}

	@Required
	public void setAssociationGenerators(final List<AssociationGenerator> associationGenerators)
	{
		this.associationGenerators = associationGenerators;
	}
}

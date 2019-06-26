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
package de.hybris.platform.odata2services.odata.schema.attribute;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.olingo.odata2.api.edm.provider.AnnotationAttribute;
import org.springframework.beans.factory.annotation.Required;

public class AttributeAnnotationListGenerator implements SchemaElementGenerator<List<AnnotationAttribute>, IntegrationObjectItemAttributeModel>
{
	private List<AnnotationGenerator<ItemModel>> annotationGenerators;

	/**
	 * Generates the list of AnnotationAttributes
	 * @param itemAttributeModel the attribute
	 * @return List of AnnotationAttributes that is guaranteed to be at least of size 1
	 */
	@Override
	public List<AnnotationAttribute> generate(final IntegrationObjectItemAttributeModel itemAttributeModel)
	{
		return annotationGenerators.stream()
				.filter(g -> g.isApplicable(itemAttributeModel))
				.map(g -> g.generate(itemAttributeModel))
				.collect(Collectors.toList());
	}

	@Required
	public void setAnnotationGenerators(final List<AnnotationGenerator<ItemModel>> annotationGenerators)
	{
		this.annotationGenerators = annotationGenerators;
	}
}

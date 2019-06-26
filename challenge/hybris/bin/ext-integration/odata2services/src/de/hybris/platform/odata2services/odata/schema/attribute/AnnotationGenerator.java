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
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator;

import org.apache.olingo.odata2.api.edm.provider.AnnotationAttribute;

/**
 *
 * @param <T> : IntegrationObjectItemAttributeModel or IntegrationObjectItemModel
 */
public interface AnnotationGenerator<T extends ItemModel> extends SchemaElementGenerator<AnnotationAttribute, T>
{
	/**
	 * Determines if this annotation generator is applicable for the given attribute or attributes
	 * @param model the IntegrationObjectItemAttributeModel OR IntegrationObjectItemModel we are verifying on
	 * @return true if applicable, otherwise false
	 */
	boolean isApplicable(final T model);
}

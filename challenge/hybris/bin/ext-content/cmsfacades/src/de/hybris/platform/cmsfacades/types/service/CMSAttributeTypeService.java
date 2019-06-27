/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cmsfacades.types.service;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.TypeModel;


/**
 * Helper service to retrieve item types and attribute types information
 */
public interface CMSAttributeTypeService
{
	/**
	 * Retrieves the {@code TypeModel} of the attribute. For primitive/atomic attributes, the attribute's type is
	 * returned. For collection or localized attributes, look into the data structure to inspect the containing type.
	 *
	 * @param attributeDescriptorModel
	 *           - The descriptor that specifies the attribute whose read permission to check.
	 * @return the type of the attribute or the contained type
	 */
	TypeModel getAttributeContainedType(AttributeDescriptorModel attributeDescriptorModel);
}

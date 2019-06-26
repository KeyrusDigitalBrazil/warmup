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
package de.hybris.platform.cmsfacades.cmsitems;

import de.hybris.platform.cmsfacades.validator.data.ValidationError;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import java.util.List;


/**
 * Interface to validate the value of the attributes. 
 * @param <T> type of the object being validated
 */
public interface AttributeContentValidator<T> 
{
	/**
	 * Performs validation on the given arguments.
	 *
	 * @param value the value object 
	 * @param attributeDescriptor the attribute descriptor of the given {@code value}. 
	 */
	List<ValidationError> validate(T value, AttributeDescriptorModel attributeDescriptor);

}

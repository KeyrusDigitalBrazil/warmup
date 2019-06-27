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

import de.hybris.platform.cmsfacades.common.function.Validator;
import de.hybris.platform.core.model.ItemModel;

/**
 * Interface that deals with Type Validation. 
 * The main purpose of this service is to validate an {@link ItemModel} after it has been successfully converted, 
 * allowing custom validation on Item Models when there is a need to validate perform business validation and 
 * cross attribute validation.  
 */
public interface CMSItemValidator<T extends ItemModel> extends Validator<T>
{
	// Intentionally left empty 
}

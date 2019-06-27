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
package de.hybris.platform.cmsfacades.types;

import de.hybris.platform.servicelayer.exceptions.BusinessException;


/**
 * Exception thrown when searching for a component type that could not be found or does not exist.
 */
public class ComponentTypeNotFoundException extends BusinessException
{
	private static final long serialVersionUID = -5978659209875421565L;

	public ComponentTypeNotFoundException(String message)
	{
		super(message);
	}

}

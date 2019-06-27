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
package de.hybris.platform.sap.core.test.property;

/**
 * Creates class for extended property handling for tests.
 */
public class PropertyAccessFactory
{

	/**
	 * Create instance or {@link PropertyAccess}.
	 * 
	 * @return instance
	 */
	public PropertyAccess createPropertyAccess()
	{
		return new PropertyAccessImpl();
	}

}

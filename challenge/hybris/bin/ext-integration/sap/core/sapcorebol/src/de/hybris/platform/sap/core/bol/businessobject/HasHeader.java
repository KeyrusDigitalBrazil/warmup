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
package de.hybris.platform.sap.core.bol.businessobject;



/**
 * Allows generic access to objects, which has an header object.
 */
public interface HasHeader
{

	/**
	 * Returns the header business object.
	 * 
	 * @return header business object
	 */
	public BusinessObjectBase getHeader();

}

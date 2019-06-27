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
package de.hybris.platform.sap.core.common.configurer;

import java.util.List;



/**
 * general Interface for holding the Entities of a Configurer.
 * 
 * @param <T>
 *           Type of the Configurer Class
 */
public interface ConfigurerEntitiesList<T>
{
	/**
	 * Add an Entity to the List.
	 * 
	 * @param entity
	 *           entity which will be added to the List
	 */
	public void addEntity(T entity);

	/**
	 * @return the list of entities
	 */
	public List<T> getEntities();

}

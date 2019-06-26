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
package de.hybris.platform.sap.core.jco.rec;


/**
 * This factory creates new instances of {@link RepositoryPlayback} implementations depending on the RepositoryVersion
 * in the repository-file.
 */
public interface RepositoryPlaybackFactory
{

	/**
	 * The actual factory method.
	 * 
	 * @return Returns a new instance of the {@link RepositoryPlayback} implementation.
	 */
	public RepositoryPlayback createRepositoryPlayback();
}

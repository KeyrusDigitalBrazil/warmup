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
package de.hybris.platform.sap.core.jco.mock.impl;

import de.hybris.platform.sap.core.jco.mock.JCoMockRepository;
import de.hybris.platform.sap.core.jco.mock.JCoMockRepositoryFactory;
import de.hybris.platform.sap.core.jco.rec.RepositoryPlayback;
import de.hybris.platform.sap.core.jco.rec.impl.RepositoryPlaybackFactoryImpl;

import java.io.File;


/**
 * Default factory providing parsing of JCo mock data files.
 */
public class DefaultJCoMockRepositoryFactory implements JCoMockRepositoryFactory
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.core.jco.rec.JCoMockRepositoryFactory#getMockRepository(java.lang.String)
	 */
	@Override
	public JCoMockRepository getMockRepository(final File file)
	{
		final RepositoryPlayback repositoryPlayback = new RepositoryPlaybackFactoryImpl(file).createRepositoryPlayback();
		return new JCoMockRepositoryDelegator(repositoryPlayback);
	}

}

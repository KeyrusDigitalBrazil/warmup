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
package de.hybris.platform.sap.core.jco.mock;

import java.io.File;


/**
 * Factory providing parsing of JCo mock data files.
 */
public interface JCoMockRepositoryFactory
{
	/**
	 * Returns a mocked JCo Repository filled with data from a xml file.
	 * 
	 * @param file
	 *           file with mocking data
	 * @return mocked JCo Repository.
	 */
	public JCoMockRepository getMockRepository(File file);
}

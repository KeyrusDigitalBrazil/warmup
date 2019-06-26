/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.personalizationservices;

import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.impex.impl.ClasspathImpExResource;

import org.junit.Before;
import org.junit.Ignore;


public abstract class AbstractCxServiceTest extends ServicelayerTransactionalTest
{

	protected final static String SEGMENT_CODE = "segment1";
	protected final static String CUSTOMIZATION_CODE = "customization1";
	protected final static String VARIATION_CODE = "variation1";
	protected final static String CUSTOMIZATION_CODE2 = "customization2";
	protected final static String VARIATION_CODE2 = "variation2";
	protected final static String VARIATION_CODE3 = "variation3";

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		importData(new ClasspathImpExResource("/personalizationservices/test/testdata_personalizationservices.impex", "UTF-8"));
	}
}

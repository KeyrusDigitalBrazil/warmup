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
package de.hybris.platform.personalizationfacades;

import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.impex.impl.ClasspathImpExResource;

import org.junit.Before;
import org.junit.Ignore;


public abstract class AbstractFacadeIntegrationTest extends ServicelayerTransactionalTest
{

	protected static final String SEGMENT_ID = "segment0";
	protected static final String SEGMENT_ID_1 = "segment1";
	protected static final String SEGMENT_ID_2 = "segment2";
	protected static final String NOTEXISTING_SEGMENT_ID = "nonExistSegment";
	protected static final String CATALOG_ID = "testCatalog";
	protected static final String CATALOG_VERSION_STAGE_ID = "Staged";
	protected static final String CATALOG_VERSION_ONLINE_ID = "Online";
	protected static final String NOTEXISTING_CATALOG_ID = "notExist";
	protected static final String CUSTOMIZATION_ID = "customization0";
	protected static final String CUSTOMIZATION_NAME = "customization0";
	protected static final String CUSTOMIZATION_ID_1 = "customization1";
	protected static final String CUSTOMIZATION_NAME_1 = "customization1";
	protected static final String NOTEXISTING_CUSTOMIZATION_ID = "notExistingCustomization";
	protected static final String VARIATION_ID = "variation0";
	protected static final String VARIATION_NAME = "variation0";
	protected static final String VARIATION_ID_1 = "variation1";
	protected static final String NEW_VARIATION_ID = "newVariation";
	protected static final String TRIGGER_ID = "trigger1";
	protected static final String NEW_TRIGGER_ID = "newTrigger";

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		importData(new ClasspathImpExResource("/personalizationfacades/test/testdata_personalizationfacades.impex", "UTF-8"));
	}
}

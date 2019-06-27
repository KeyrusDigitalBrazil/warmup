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
package de.hybris.platform.mediaconversion.integrationtest;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.servicelayer.model.ModelService;



/**
 * @author pohl
 * 
 */
final class TestDataFactory
{

	private TestDataFactory()
	{
		// no instances mate!
	}

	static CatalogVersionModel someCatalogVersion(final ModelService modelService)
	{
		final CatalogVersionModel ret = modelService.create(CatalogVersionModel.class);
		ret.setVersion("test");
		ret.setCatalog(TestDataFactory.someCatalog(modelService));
		modelService.save(ret);
		return ret;
	}

	static CatalogModel someCatalog(final ModelService modelService)
	{
		final CatalogModel ret = modelService.create(CatalogModel.class);
		ret.setId("testCatalog_" + System.currentTimeMillis());
		modelService.save(ret);
		return ret;
	}
}

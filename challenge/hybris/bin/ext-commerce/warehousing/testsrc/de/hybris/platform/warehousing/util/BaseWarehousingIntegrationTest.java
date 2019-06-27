/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.warehousing.util;

import de.hybris.platform.basecommerce.util.SpringCustomContextLoader;
import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.warehousing.util.models.BaseSites;
import de.hybris.platform.warehousing.util.models.BaseStores;

import javax.annotation.Resource;

import java.util.Arrays;

import org.junit.Before;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.ContextConfiguration;


@ContextConfiguration(locations = { "classpath:/warehousing-spring-test.xml" })
public class BaseWarehousingIntegrationTest extends ServicelayerTransactionalTest
{
	protected static SpringCustomContextLoader springCustomContextLoader = null;

	@Resource
	protected FlexibleSearchService flexibleSearchService;
	@Resource
	private BaseSites baseSites;
	@Resource
	protected BaseStores baseStores;
	@Resource
	protected SessionService sessionService;
	@Resource
	protected ModelService modelService;

	public BaseWarehousingIntegrationTest()
	{
		if (springCustomContextLoader == null)
		{
			try
			{
				springCustomContextLoader = new SpringCustomContextLoader(getClass());
				springCustomContextLoader.loadApplicationContexts((GenericApplicationContext) Registry.getCoreApplicationContext());
				springCustomContextLoader
						.loadApplicationContextByConvention((GenericApplicationContext) Registry.getCoreApplicationContext());
			}
			catch (final Exception e)
			{
				throw new RuntimeException(e.getMessage(), e);
			}
		}
	}

	@Before
	public void setupBaseSite()
	{
		baseSites.Americas().setStores(Arrays.asList(baseStores.NorthAmerica()));
		saveAll();
		sessionService.setAttribute("currentSite", baseSites.Americas());
	}

	/**
	 * Saves any unsaved models.
	 */
	protected void saveAll()
	{
		modelService.saveAll();
	}
}

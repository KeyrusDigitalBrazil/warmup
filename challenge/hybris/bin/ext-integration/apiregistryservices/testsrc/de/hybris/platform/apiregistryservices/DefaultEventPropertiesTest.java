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
package de.hybris.platform.apiregistryservices;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;

import java.util.List;

import javax.annotation.Resource;

import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.apiregistryservices.dao.EventConfigurationDao;
import org.junit.Before;
import org.junit.Test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.apiregistryservices.model.events.EventConfigurationModel;
import de.hybris.platform.apiregistryservices.model.events.EventPropertyConfigurationModel;


@IntegrationTest
public class DefaultEventPropertiesTest extends ServicelayerTest
{
	@Resource
	private EventConfigurationDao eventConfigurationDao;

	@Resource
	private ModelService modelService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Before
	public void setUp() throws Exception
	{
		importCsv("/test/eventConfigurations.impex", "UTF-8");
	}

	@Test(expected = ModelNotFoundException.class)
	public void findProperties()
	{
		final List<EventConfigurationModel> eventConfigsByClass = eventConfigurationDao
				.findActiveEventConfigsByClass("de.hybris.platform.order.events.SubmitOrderEvent");

		assertEquals("total number wrong", eventConfigsByClass.size(), 1);

		final EventConfigurationModel eventConfiguration = eventConfigsByClass.get(0);
		assertTrue(eventConfiguration.getEventPropertyConfigurations().size() == 1);
		assertEquals("orderCode", eventConfiguration.getEventPropertyConfigurations().get(0).getPropertyName());

		modelService.remove(eventConfiguration);

		final EventPropertyConfigurationModel modelByExample = new EventPropertyConfigurationModel();
		modelByExample.setPropertyName("orderCode");

		final EventPropertyConfigurationModel shouldBeDeleted = flexibleSearchService.getModelByExample(modelByExample);
		assertNull("Properties remain", shouldBeDeleted);
	}
}

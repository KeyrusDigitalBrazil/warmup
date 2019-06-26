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
package de.hybris.platform.kymaintegrationservices.populators;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.model.EndpointModel;
import de.hybris.platform.apiregistryservices.model.ExposedDestinationModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.kymaintegrationservices.dto.EventsSpecificationSourceData;
import de.hybris.platform.kymaintegrationservices.dto.InfoData;
import de.hybris.platform.kymaintegrationservices.dto.ServiceRegistrationData;
import de.hybris.platform.kymaintegrationservices.dto.SpecData;
import de.hybris.platform.kymaintegrationservices.utils.KymaApiExportHelper;
import de.hybris.platform.kymaintegrationservices.utils.KymaEventExportUtils;
import de.hybris.platform.util.Config;

import java.util.LinkedList;

import de.hybris.platform.util.Utilities;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;



@UnitTest
public class EventsSpecificationPopulatorTest
{
	private static final String TEST_VERSION = "TESTVERSION";
	private static final String TEST_DESCRIPTION = "testDescription";
	private static final String TEST_NAME = "testName";
	private static final String TEST_ID = "testId";
	private static final String TEST_SOURCE_URL = "testSourceUrl";
	private static final String TEST_PROVIDER = "SAP Hybris";
	private static final String PROVIDER_PROP = "kymaintegrationservices.kyma-specification-provider";
	private static final String ASYNCAPI_DEFAULT = "1.0.0";
	private static final String ASYNCAPI_PROP = "kymaintegrationservices.kyma-specification-asyncapi";

	private final EventsSpecificationPopulator populator = new EventsSpecificationPopulator();
	private final EventsSpecificationSourceData eventsSpecificationSourceData = new EventsSpecificationSourceData();

	@Mock
	private ExposedDestinationModel destinationModel;
	@Mock
	private EndpointModel endpointModel;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		Registry.activateStandaloneMode();
		Utilities.setJUnitTenant();

		Config.setParameter(PROVIDER_PROP, TEST_PROVIDER);
		Config.setParameter(ASYNCAPI_PROP, ASYNCAPI_DEFAULT);

		when(destinationModel.getEndpoint()).thenReturn(endpointModel);
		when(destinationModel.getId()).thenReturn(TEST_ID);
		when(destinationModel.getUrl()).thenReturn(TEST_SOURCE_URL);
		when(endpointModel.getDescription()).thenReturn(TEST_DESCRIPTION);
		when(endpointModel.getVersion()).thenReturn(TEST_VERSION);
		when(endpointModel.getName()).thenReturn(TEST_NAME);

		eventsSpecificationSourceData.setExposedDestination(destinationModel);
		eventsSpecificationSourceData.setEvents(new LinkedList<>());
	}

	@Test
	public void populateApiSpecificationWithSpecTextFromUrl()
	{
		final ServiceRegistrationData serviceRegistrationData = new ServiceRegistrationData();
		populator.populate(eventsSpecificationSourceData, serviceRegistrationData);

		assertEquals(serviceRegistrationData.getDescription(), TEST_DESCRIPTION);
		assertEquals(serviceRegistrationData.getName(), TEST_NAME);
		assertEquals(serviceRegistrationData.getIdentifier(), KymaApiExportHelper.getDestinationId(destinationModel));
		assertEquals(serviceRegistrationData.getProvider(), TEST_PROVIDER);

		final SpecData spec = serviceRegistrationData.getEvents().getSpec();
		assertEquals(ASYNCAPI_DEFAULT, spec.getAsyncapi());

		final InfoData info = spec.getInfo();

		assertEquals(info.getTitle(), TEST_ID);
		assertEquals(info.getVersion(), TEST_VERSION);
		assertEquals(info.getDescription(), TEST_DESCRIPTION);
	}
}

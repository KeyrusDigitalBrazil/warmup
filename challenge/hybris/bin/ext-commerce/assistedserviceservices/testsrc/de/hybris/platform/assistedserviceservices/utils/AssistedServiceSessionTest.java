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
package de.hybris.platform.assistedserviceservices.utils;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.assistedserviceservices.AssistedServiceService;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.user.UserService;

import javax.annotation.Resource;

import de.hybris.platform.storelocator.model.PointOfServiceModel;
import org.apache.commons.lang.SerializationUtils;
import org.junit.Before;
import org.junit.Test;


/**
 * AssistedServiceSession serialization test
 */
@IntegrationTest
public class AssistedServiceSessionTest extends ServicelayerTest
{
	@Resource
	private UserService userService;
	@Resource
	private AssistedServiceService assistedServiceService;

	@Before
	public void setup() throws Exception
	{
		importCsv("/assistedserviceservices/test/recent_data.impex", "UTF-8");
		importCsv("/assistedserviceservices/test/pos_data.impex", "UTF-8");
	}

	@Test
	public void serializationTest()
	{
		final String fwd = "fwd";

		final UserModel customer = userService.getUserForUID("user1");
		final EmployeeModel agent = userService.getUserForUID("asagent", EmployeeModel.class);

		final AssistedServiceSession assistedServiceSession = new AssistedServiceSession();

		assistedServiceSession.setAgent(agent);
		assistedServiceSession.setEmulatedCustomer(customer);
		assistedServiceSession.setForwardUrl(fwd);

		final AssistedServiceSession assistedServiceSessionCopy = (AssistedServiceSession) SerializationUtils
				.clone(assistedServiceSession);
		assertEquals(assistedServiceSessionCopy.getAgent().getUid(), assistedServiceSession.getAgent().getUid());
		assertEquals(assistedServiceSessionCopy.getEmulatedCustomer().getUid(),
				assistedServiceSession.getEmulatedCustomer().getUid());
		assertEquals(assistedServiceSessionCopy.getForwardUrl(), assistedServiceSession.getForwardUrl());
	}

	@Test
	public void getAssistedServiceAgentStoreTest()
	{

		final EmployeeModel nakano = userService.getUserForUID("customer.support@nakano.com", EmployeeModel.class);

		final PointOfServiceModel nakanoPOS = assistedServiceService.getAssistedServiceAgentStore(nakano);

		assertEquals(nakanoPOS.getName(), "Nakano");
	}
}
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
package de.hybris.platform.assistedserviceyprofilefacades.populator;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hybris.platform.yaasyprofileconnect.yaas.Profile;
import org.junit.Before;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.InputStream;


public abstract class AbstractProfileAffinityTest
{
	protected static String Y_PROFIL_JSON_PATH = "assistedserviceyprofilefacades/test/yprofile.json";

	protected Profile affinityProfile;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		affinityProfile = getDataFromJson(AbstractProfileAffinityTest.Y_PROFIL_JSON_PATH);
	}

	protected Profile getDataFromJson(final String path)
	{
		Profile parsedNeighbours = null;

		final ObjectMapper jacksonObjectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
				false);
		try
		{
			final InputStream in = getClass().getClassLoader().getResourceAsStream(path);

			parsedNeighbours = jacksonObjectMapper.readValue(in, Profile.class);
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}

		return parsedNeighbours;
	}
}

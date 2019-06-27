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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.assistedserviceyprofilefacades.data.TechnologyUsedData;
import de.hybris.platform.yaasyprofileconnect.yaas.UserAgent;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;


@UnitTest
public class DeviceAffinityPopulatorTest extends AbstractProfileAffinityTest
{
	protected DeviceAffinityPopulator deviceAffinityPopulator = new DeviceAffinityPopulator<>();

	@Test
	public void getAffinityTest()
	{
		final List<Map.Entry<String, UserAgent>> affinityList = affinityProfile.getUserAgents().entrySet().parallelStream().collect(Collectors.toList());


		assertEquals(1, affinityList.size());

		final Map.Entry<String, UserAgent> categoryAffinity = affinityList.get(0);

		final TechnologyUsedData categoryAffinityData = new TechnologyUsedData();

		deviceAffinityPopulator.populate(categoryAffinity, categoryAffinityData);

		assertEquals("Computer", categoryAffinityData.getDevice());
		assertEquals("Mac_OS_X", categoryAffinityData.getOperatingSystem());
		assertEquals("Safari", categoryAffinityData.getBrowser());
	}
}

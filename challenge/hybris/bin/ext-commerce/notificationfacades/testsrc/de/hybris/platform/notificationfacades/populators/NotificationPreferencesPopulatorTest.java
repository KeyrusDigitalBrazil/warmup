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
package de.hybris.platform.notificationfacades.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.notificationfacades.data.NotificationPreferenceData;
import de.hybris.platform.notificationfacades.data.NotificationPreferenceDataList;
import de.hybris.platform.notificationservices.enums.NotificationChannel;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class NotificationPreferencesPopulatorTest
{
	private NotificationPreferencesPopulator populator;

	@Mock
	private List<NotificationPreferenceData> source;

	private NotificationPreferenceDataList target;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		populator = new NotificationPreferencesPopulator();

	}

	@Test
	public void testPopulate()
	{
		target = new NotificationPreferenceDataList();
		final String mail = "test@hybris.com";
		final NotificationPreferenceData data = new NotificationPreferenceData();
		data.setChannel(NotificationChannel.EMAIL);
		data.setValue(mail);
		data.setEnabled(true);
		source.add(data);

		populator.populate(source, target);
		Assert.assertEquals(source, target.getPreferences());

	}

	@Test(expected = IllegalArgumentException.class)
	public void testPopulateSourceNull()
	{
		populator.populate(null, target);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testPopulateTargetNull()
	{
		populator.populate(source, null);

	}

}

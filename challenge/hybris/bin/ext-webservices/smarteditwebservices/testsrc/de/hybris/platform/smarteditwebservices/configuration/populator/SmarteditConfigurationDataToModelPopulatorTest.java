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
package de.hybris.platform.smarteditwebservices.configuration.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.smarteditwebservices.data.ConfigurationData;
import de.hybris.platform.smarteditwebservices.model.SmarteditConfigurationModel;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import org.junit.Test;


@UnitTest
public class SmarteditConfigurationDataToModelPopulatorTest
{

	private SmarteditConfigurationDataToModelPopulator populator = new SmarteditConfigurationDataToModelPopulator();
	@Test
	public void populateNonEmptyData()
	{
		final ConfigurationData data = new ConfigurationData();
		data.setKey("KEY");
		data.setValue("VALUE");
		final SmarteditConfigurationModel model = new SmarteditConfigurationModel();
		populator.populate(data, model);
		assertThat(data.getKey(), is(model.getKey()));
		assertThat(data.getValue(), is(model.getValue()));
	}
}

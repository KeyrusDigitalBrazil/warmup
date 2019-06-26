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
package de.hybris.platform.sap.productconfig.runtime.mock.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;

import java.util.List;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class CPQHomeTheaterPocConfigMockImplTest
{
	private CPQHomeTheaterPocConfigMockImpl classUnderTest;
	private ConfigModel configModel;

	@Before
	public void setup()
	{
		classUnderTest = (CPQHomeTheaterPocConfigMockImpl) new RunTimeConfigMockFactory()
				.createConfigMockForProductCode("CPQ_HOME_THEATER");
		configModel = classUnderTest.createDefaultConfiguration();
	}

	@Test
	public void testCreateDefaultConfiguration()
	{
		assertNotNull(configModel);

		final InstanceModel instance = configModel.getRootInstance();
		assertNotNull(instance);
		assertEquals(CPQHomeTheaterPocConfigMockImpl.ROOT_INSTANCE_NAME, instance.getName());

		final List<CsticModel> cstics = instance.getCstics();
		assertNotNull(cstics);
		assertEquals(6, cstics.size());
	}

	@Test
	public void testSpeaker()
	{
		CsticModel cstic = new CsticModelBuilder(). //
				withName(CPQHomeTheaterPocConfigMockImpl.CPQ_HT_SURROUND_MODE, ""). //
				addSelectedOption(CPQHomeTheaterPocConfigMockImpl.SURROUND, ""). //
				build();


		assertEquals(1, configModel.getRootInstance().getSubInstances().size());
		classUnderTest.checkCstic(configModel, null, cstic);

		List<InstanceModel> subInstances = configModel.getRootInstance().getSubInstances();
		assertEquals(3, subInstances.size());

		cstic = new CsticModelBuilder(). //
				withName(CPQHomeTheaterPocConfigMockImpl.CPQ_HT_SURROUND_MODE, ""). //
				addSelectedOption(CPQHomeTheaterPocConfigMockImpl.STEREO, ""). //
				build();

		classUnderTest.checkCstic(configModel, null, cstic);
		subInstances = configModel.getRootInstance().getSubInstances();
		assertEquals(2, subInstances.size());
	}

	@Test
	public void testSubwoofer()
	{
		CsticModel cstic = new CsticModelBuilder(). //
				withName(CPQHomeTheaterPocConfigMockImpl.CPQ_HT_SUBWOOFER, ""). //
				addSelectedOption(CPQHomeTheaterPocConfigMockImpl.X, ""). //
				build();


		assertEquals(1, configModel.getRootInstance().getSubInstances().size());
		classUnderTest.checkCstic(configModel, null, cstic);

		List<InstanceModel> subInstances = configModel.getRootInstance().getSubInstances();
		assertEquals(2, subInstances.size());

		cstic = new CsticModelBuilder(). //
				withName(CPQHomeTheaterPocConfigMockImpl.CPQ_HT_SUBWOOFER, ""). //
				build();

		classUnderTest.checkCstic(configModel, null, cstic);
		subInstances = configModel.getRootInstance().getSubInstances();
		assertEquals(1, subInstances.size());
	}

	@Test
	public void testBluRay()
	{
		CsticModel cstic = new CsticModelBuilder(). //
				withName(CPQHomeTheaterPocConfigMockImpl.CPQ_HT_INCLUDE_BR, ""). //
				addSelectedOption(CPQHomeTheaterPocConfigMockImpl.X, ""). //
				build();


		assertEquals(1, configModel.getRootInstance().getSubInstances().size());
		classUnderTest.checkCstic(configModel, null, cstic);

		List<InstanceModel> subInstances = configModel.getRootInstance().getSubInstances();
		assertEquals(2, subInstances.size());

		cstic = new CsticModelBuilder(). //
				withName(CPQHomeTheaterPocConfigMockImpl.CPQ_HT_INCLUDE_BR, ""). //
				build();

		classUnderTest.checkCstic(configModel, null, cstic);
		subInstances = configModel.getRootInstance().getSubInstances();
		assertEquals(1, subInstances.size());
	}
}

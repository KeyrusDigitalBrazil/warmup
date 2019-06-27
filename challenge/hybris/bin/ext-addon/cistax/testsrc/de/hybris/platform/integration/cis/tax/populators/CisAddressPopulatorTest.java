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
package de.hybris.platform.integration.cis.tax.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.user.AddressModel;

import com.hybris.cis.client.shared.models.CisAddress;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertEquals;
import static org.mockito.BDDMockito.given;


/**
 * 
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CisAddressPopulatorTest
{
	private CisAddressPopulator cisAddressPopulator;

	@Before
	public void setUp()
	{
		cisAddressPopulator = new CisAddressPopulator();
	}

	@Test
	public void shouldPopulateAddress()
	{
		final CisAddress cisAddress = new CisAddress();
		final AddressModel addressModel = Mockito.mock(AddressModel.class);
		final CountryModel countryModel = Mockito.mock(CountryModel.class);
		final RegionModel regionModel = Mockito.mock(RegionModel.class);

		given(regionModel.getIsocodeShort()).willReturn("NY");
		given(countryModel.getIsocode()).willReturn("US");
		given(addressModel.getCountry()).willReturn(countryModel);
		given(addressModel.getTown()).willReturn("New York");
		given(addressModel.getLine1()).willReturn("1700 Broadway");
		given(addressModel.getRegion()).willReturn(regionModel);
		given(addressModel.getPostalcode()).willReturn("10119");

		cisAddressPopulator.populate(addressModel, cisAddress);

		assertEquals("NY", cisAddress.getState());
		assertEquals("US", cisAddress.getCountry());
		assertEquals("New York", cisAddress.getCity());
		assertEquals("1700 Broadway", cisAddress.getAddressLine1());
		assertEquals("10119", cisAddress.getZipCode());
	}
}

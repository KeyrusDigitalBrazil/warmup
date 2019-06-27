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
package de.hybris.platform.yacceleratorordermanagement.converters;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.storelocator.data.AddressData;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

@UnitTest
public class AddressPopulatorTest
{
    AddressPopulator populator;

    @Before
    public void setup()
    {
        populator = new AddressPopulator();
    }

    @Test(expected = IllegalArgumentException.class)
    public void itShouldFailIfSourceParamIsNull()
    {
        populator.populate(null, new AddressData());
    }

    @Test(expected = IllegalArgumentException.class)
    public void itShouldFailIfTargetParamIsNull()
    {
        populator.populate(new AddressModel(), null);
    }

    @Test
    public void populateAllFields()
    {
        CountryModel unitedStates = new CountryModel();
        unitedStates.setIsocode("US");

        AddressModel broadwayNewYork = new AddressModel();
        broadwayNewYork.setCountry(unitedStates);
        broadwayNewYork.setTown("NewYorkCity");
        broadwayNewYork.setStreetname("Broadway avenue");
        broadwayNewYork.setPostalcode("100001");

        AddressData addressData = new AddressData();

        populator.populate(broadwayNewYork, addressData);

        assertTrue("US".equals(addressData.getCountryCode()));
        assertTrue("NewYorkCity".equals(addressData.getCity()));
        assertTrue("Broadway avenue".equals(addressData.getStreet()));
        assertTrue("100001".equals(addressData.getZip()));
    }

    @Test
    public void itShouldNotFailIfCountryCodeIsNotProvided()
    {
        AddressModel broadwayNewYork = new AddressModel();
        broadwayNewYork.setCountry(null);
        broadwayNewYork.setTown("NewYorkCity");
        broadwayNewYork.setStreetname("Broadway avenue");
        broadwayNewYork.setPostalcode("100001");

        AddressData addressData = new AddressData();

        populator.populate(broadwayNewYork, addressData);

        assertTrue(null == addressData.getCountryCode());
        assertTrue("NewYorkCity".equals(addressData.getCity()));
        assertTrue("Broadway avenue".equals(addressData.getStreet()));
        assertTrue("100001".equals(addressData.getZip()));
    }

}

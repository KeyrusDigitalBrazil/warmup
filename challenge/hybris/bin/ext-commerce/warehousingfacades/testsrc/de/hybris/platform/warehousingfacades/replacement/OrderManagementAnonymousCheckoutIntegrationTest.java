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
 */
package de.hybris.platform.warehousingfacades.replacement;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.acceleratorfacades.order.checkout.AnonymousCheckoutIntegrationTest;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.warehousing.constants.WarehousingTestConstants;


/**
 * Re-implements test {@link AnonymousCheckoutIntegrationTest} to provide missing information required when warehousing extensions is present
 */
@IntegrationTest(replaces = AnonymousCheckoutIntegrationTest.class)
public class OrderManagementAnonymousCheckoutIntegrationTest extends AnonymousCheckoutIntegrationTest
{
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		insertExtraInformation();
	}

	/**
	 * Import impex during setup to add relation between warehouse and delivery mode as well as the default ATP formula for the used basestore.
	 *
	 * @throws ImpExException
	 */
	private void insertExtraInformation() throws ImpExException
	{
		importCsv("/warehousingfacades/test/impex/replacement/replacement-add-formula-teststore.impex",
				WarehousingTestConstants.ENCODING);
		importCsv("/warehousingfacades/test/impex/replacement/replacement-us-regions.impex", WarehousingTestConstants.ENCODING);
	}

	@Override
	protected AddressData buildDeliveryAddress()
	{
		final AddressData address = new AddressData();
		address.setId("12345");
		address.setFirstName("First");
		address.setLastName("Last");
		address.setLine1("123 ABC St");
		address.setPostalCode("12345");
		address.setTown("New York");
		final CountryData countryData = new CountryData();
		countryData.setIsocode("US");
		final RegionData regionData = new RegionData();
		regionData.setIsocode("US-NY");
		address.setRegion(regionData);
		address.setCountry(countryData);
		address.setBillingAddress(true);
		address.setShippingAddress(true);
		address.setDefaultAddress(true);

		return address;
	}
}

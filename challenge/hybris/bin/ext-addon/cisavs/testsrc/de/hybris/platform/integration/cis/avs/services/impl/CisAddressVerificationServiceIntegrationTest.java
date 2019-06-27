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
package de.hybris.platform.integration.cis.avs.services.impl;

import javax.annotation.Resource;
import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.commerceservices.address.AddressErrorCode;
import de.hybris.platform.commerceservices.address.AddressFieldType;
import de.hybris.platform.commerceservices.address.AddressVerificationDecision;
import de.hybris.platform.commerceservices.address.AddressVerificationService;
import de.hybris.platform.commerceservices.address.data.AddressFieldErrorData;
import de.hybris.platform.commerceservices.address.data.AddressVerificationResultData;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@ManualTest
public class CisAddressVerificationServiceIntegrationTest extends ServicelayerTest
{
	private final static String TEST_NAME = "test";
	private final static String REVIEW = "review";
	private final static String STREET_ADDRESS = "1700 Broadway";
	private final static String CORRECTED = " corrected";


	@Resource
	private AddressVerificationService addressVerificationService;

	@Resource
	private CommonI18NService commonI18NService;

	@Resource
	private ModelService modelService;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
	}

	@Test
	public void shouldValidateUSAddressAccept()
	{
		final AddressModel addressModel = modelService.create(AddressModel.class);
		addressModel.setLine1(STREET_ADDRESS);
		addressModel.setTown("New York");
		final CountryModel country = commonI18NService.getCountry("US");
		addressModel.setCountry(country);
		addressModel.setFirstname(TEST_NAME);
		addressModel.setLastname(TEST_NAME);
		addressModel.setRegion(commonI18NService.getRegion(country, "NY"));
		addressModel.setPostalcode("10019");

		final AddressVerificationResultData<AddressVerificationDecision, AddressFieldErrorData<AddressFieldType, AddressErrorCode>> result = addressVerificationService
				.verifyAddress(addressModel);
		Assert.assertEquals(AddressVerificationDecision.ACCEPT, result.getDecision());
		Assert.assertNotNull("Suggested addressess should not be null", result.getSuggestedAddresses());
		// Address is accepted so no suggestion is made
		Assert.assertEquals(0, result.getSuggestedAddresses().size());
	}

	@Test
	public void shouldValidateUSAddressReview()
	{
		final AddressModel addressModel = modelService.create(AddressModel.class);
		addressModel.setLine1(STREET_ADDRESS);
		addressModel.setTown(REVIEW);
		final CountryModel country = commonI18NService.getCountry("US");
		addressModel.setCountry(country);
		addressModel.setFirstname(TEST_NAME);
		addressModel.setLastname(TEST_NAME);
		addressModel.setRegion(commonI18NService.getRegion(country, "NY"));
		addressModel.setPostalcode("11222");

		final AddressVerificationResultData<AddressVerificationDecision, AddressFieldErrorData<AddressFieldType, AddressErrorCode>> result = addressVerificationService
				.verifyAddress(addressModel);

		Assert.assertEquals(AddressVerificationDecision.REVIEW, result.getDecision());
		Assert.assertNotNull("Suggested addressess should not be null", result.getSuggestedAddresses());
		Assert.assertEquals(1, result.getSuggestedAddresses().size());
		Assert.assertEquals(STREET_ADDRESS + CORRECTED, result.getSuggestedAddresses().get(0).getLine1());
	}

	@Test
	public void shouldValidateUSAddressUnknown()
	{
		final AddressModel addressModel = modelService.create(AddressModel.class);
		addressModel.setLine1("1700 Brooooooadwayyyyy");
		addressModel.setLine2("26th floor");
		final CountryModel country = commonI18NService.getCountry("US");
		addressModel.setCountry(country);
		addressModel.setFirstname(TEST_NAME);
		addressModel.setLastname(TEST_NAME);
		addressModel.setRegion(commonI18NService.getRegion(country, "MA"));
		addressModel.setPostalcode("11222");

		final AddressVerificationResultData<AddressVerificationDecision, AddressFieldErrorData<AddressFieldType, AddressErrorCode>> result = addressVerificationService
				.verifyAddress(addressModel);
		Assert.assertEquals(AddressVerificationDecision.UNKNOWN, result.getDecision());
		Assert.assertEquals(0, result.getSuggestedAddresses().size());
	}
}

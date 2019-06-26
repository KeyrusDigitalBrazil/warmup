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


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.hybris.cis.client.avs.models.AvsResult;
import com.hybris.cis.client.shared.models.CisAddress;
import com.hybris.cis.client.shared.models.CisDecision;
import com.hybris.cis.service.CisClientAvsService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.address.AddressErrorCode;
import de.hybris.platform.commerceservices.address.AddressFieldType;
import de.hybris.platform.commerceservices.address.AddressVerificationDecision;
import de.hybris.platform.commerceservices.address.data.AddressFieldErrorData;
import de.hybris.platform.commerceservices.address.data.AddressVerificationResultData;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.integration.cis.avs.populators.CisAvsAddressMatchingPopulator;
import de.hybris.platform.integration.cis.avs.strategies.CheckVerificationRequiredStrategy;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.tenant.TenantService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.testframework.TestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;


@UnitTest
public class DefaultCisAddressVerificationServiceTest
{
	private DefaultCisAddressVerificationService avs;
	@Mock
	private CheckVerificationRequiredStrategy checkVerificationRequiredStrategy;
	@Mock
	private CisClientAvsService cisClientAvsService;
	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private SessionService sessionService;
	@Mock
	private TenantService tenantService;
	@Mock
	private Converter<AvsResult, AddressVerificationResultData> avrConverter;
	@Mock
	private Converter<AddressModel, CisAddress> cisAddressConverter;
	@Mock
	private CartService cartService;
	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private Configuration configuration;

	@Before
	public void init()
	{
		MockitoAnnotations.initMocks(this);

		avs = new DefaultCisAddressVerificationService();
		avs.setCisAvsAddressMatchingPopulator(new CisAvsAddressMatchingPopulator());
		avs.setApplyVerificationStrategy(checkVerificationRequiredStrategy);
		avs.setCisAvsResultAddressVerificationResultDataConverter(avrConverter);
		avs.setCisAvsAddressConverter(cisAddressConverter);
		avs.setCisClientAvsService(cisClientAvsService);
		avs.setBaseStoreService(baseStoreService);
		avs.setCartService(cartService);
	}

	@Test
	public void shouldReturnAcceptIfNoValidationRequired()
	{
		final AddressModel address = new AddressModel();

		when(Boolean.valueOf(checkVerificationRequiredStrategy.isVerificationRequired(address))).thenReturn(Boolean.FALSE);
		final AvsResult avsResult = Mockito.mock(AvsResult.class);
		when(avsResult.getDecision()).thenReturn(CisDecision.ACCEPT);
		when(avsResult.getSuggestedAddresses()).thenReturn(Collections.singletonList(new CisAddress()));
		when(cisClientAvsService.verifyAddress(anyString(),anyString(), any(CisAddress.class))).thenReturn(avsResult);

		final AddressVerificationResultData<AddressVerificationDecision, AddressFieldErrorData<AddressFieldType, AddressErrorCode>> result = avs
				.verifyAddress(address);
		assertEquals(AddressVerificationDecision.ACCEPT, result.getDecision());
		assertTrue(CollectionUtils.isEmpty(result.getSuggestedAddresses()));
	}

	@Test
	public void shouldReturnUnknownIfClientFails()
	{
		final AddressModel address = new AddressModel();
		when(Boolean.valueOf(checkVerificationRequiredStrategy.isVerificationRequired(address))).thenReturn(Boolean.TRUE);

		final AvsResult avsResult = Mockito.mock(AvsResult.class);
		when(avsResult.getDecision()).thenReturn(CisDecision.ERROR);
		when(cisClientAvsService.verifyAddress(anyString(),anyString(), any(CisAddress.class))).thenReturn(avsResult);

		try
		{
			TestUtils.disableFileAnalyzer("Expected exception from avs client.");
			final AddressVerificationResultData<AddressVerificationDecision, AddressFieldErrorData<AddressFieldType, AddressErrorCode>> result = avs
					.verifyAddress(address);
			assertEquals(AddressVerificationDecision.UNKNOWN, result.getDecision());
		}
		finally
		{
			TestUtils.enableFileAnalyzer();
		}
	}

	@Test
	public void shouldReturnAcceptIfClientReturnAccept()
	{
		final AddressModel address = new AddressModel();
		final CountryModel countryModel = new CountryModel();
		countryModel.setIsocode("US");
		address.setCountry(countryModel);
		address.setFirstname("Der");
		address.setLastname("Buck");
		address.setTown("New York");
		final RegionModel region = new RegionModel();
		region.setIsocode("NY");
		when(Boolean.valueOf(checkVerificationRequiredStrategy.isVerificationRequired(address))).thenReturn(Boolean.TRUE);

		final AvsResult avsResult = Mockito.mock(AvsResult.class);
		when(avsResult.getDecision()).thenReturn(CisDecision.ACCEPT);

		final CisAddress suggestedCisAddress = new CisAddress();
		suggestedCisAddress.setCity("New York");
		suggestedCisAddress.setFirstName("Der");
		suggestedCisAddress.setLastName("Buck");
		suggestedCisAddress.setCountry("US");
		suggestedCisAddress.setState("NY");

		when(cisAddressConverter.convert(address)).thenReturn(suggestedCisAddress);

		final AddressVerificationResultData addressVerificationResultData = new AddressVerificationResultData();
		addressVerificationResultData.setSuggestedAddresses(null);
		addressVerificationResultData.setDecision(AddressVerificationDecision.ACCEPT);
		when(avrConverter.convert(avsResult)).thenReturn(addressVerificationResultData);

		when(avsResult.getSuggestedAddresses()).thenReturn(new ArrayList(Collections.singletonList(suggestedCisAddress)));
		when(cisClientAvsService.verifyAddress(anyString(),anyString(), any(CisAddress.class))).thenReturn(avsResult);

		final AddressVerificationResultData<AddressVerificationDecision, AddressFieldErrorData<AddressFieldType, AddressErrorCode>> result = avs
				.verifyAddress(address);
		assertEquals(AddressVerificationDecision.ACCEPT, result.getDecision());
		assertTrue(CollectionUtils.isEmpty(result.getSuggestedAddresses()));
	}


	@Test
	public void checkAddressEqualityForMissingState()
	{
		final AddressModel address = new AddressModel();
		final CountryModel countryModel = new CountryModel();
		countryModel.setIsocode("US");
		address.setCountry(countryModel);
		address.setFirstname("Der");
		address.setLastname("Buck");
		address.setTown("New York");
		address.setRegion(null);

		final AddressModel suggestedAddressModel = new AddressModel();
		suggestedAddressModel.setCountry(countryModel);
		suggestedAddressModel.setFirstname("Der");
		suggestedAddressModel.setLastname("Buck");
		suggestedAddressModel.setTown("New York");
		final RegionModel region = new RegionModel();
		region.setIsocode("NY");
		suggestedAddressModel.setRegion(region);
		final List<AddressModel> suggestedAddressList = new ArrayList<AddressModel>(1);
		suggestedAddressList.add(suggestedAddressModel);

		when(Boolean.valueOf(checkVerificationRequiredStrategy.isVerificationRequired(address))).thenReturn(Boolean.TRUE);
		final AvsResult avsResult = new AvsResult();

		final CisAddress cisAddress = new CisAddress();
		cisAddress.setCity("New York");
		cisAddress.setFirstName("Der");
		cisAddress.setLastName("Buck");
		cisAddress.setCountry("US");
		cisAddress.setState(null);
		when(cisAddressConverter.convert(address)).thenReturn(cisAddress);
		final List<CisAddress> cisAddressList = new ArrayList<CisAddress>(1);
		cisAddressList.add(cisAddress);

		final CisAddress suggestedCisAddress = new CisAddress();
		suggestedCisAddress.setCity("New York");
		suggestedCisAddress.setFirstName("Der");
		suggestedCisAddress.setLastName("Buck");
		suggestedCisAddress.setCountry("US");
		suggestedCisAddress.setState("NY");

		final List<CisAddress> suggestedCisAddressList = new ArrayList<CisAddress>(1);
		suggestedCisAddressList.add(suggestedCisAddress);
		avsResult.setSuggestedAddresses(suggestedCisAddressList);
		avsResult.setDecision(CisDecision.ACCEPT);
		when(cisClientAvsService.verifyAddress(anyString(),anyString(), any(CisAddress.class))).thenReturn(avsResult);


		final AddressVerificationResultData addressVerificationResultData = new AddressVerificationResultData();
		addressVerificationResultData.setSuggestedAddresses(suggestedAddressList);
		addressVerificationResultData.setDecision(AddressVerificationDecision.ACCEPT);
		when(avrConverter.convert(avsResult)).thenReturn(addressVerificationResultData);

		final AddressVerificationResultData<AddressVerificationDecision, AddressFieldErrorData<AddressFieldType, AddressErrorCode>> result = avs
				.verifyAddress(address);
		assertEquals(AddressVerificationDecision.ACCEPT, result.getDecision());
		assertTrue(CollectionUtils.isNotEmpty(result.getSuggestedAddresses()));
		final AddressModel addressModel = result.getSuggestedAddresses().get(0);
		assertEquals(addressModel.getRegion().getIsocode(), "NY");
	}
}

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
package de.hybris.platform.accountsummaryaddon.converters.populators;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.accountsummaryaddon.data.AccountSummaryInfoData;
import de.hybris.platform.accountsummaryaddon.document.data.B2BAmountBalanceData;
import de.hybris.platform.accountsummaryaddon.formatters.AmountFormatter;
import de.hybris.platform.accountsummaryaddon.populators.AccountSummaryInfoPopulator;
import de.hybris.platform.b2b.model.B2BCreditLimitModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.user.UserService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class AccountSummaryInfoPopulatorTest
{

	@Mock
	private Converter<B2BUnitModel, B2BUnitData> b2bUnitConverter;
	@Mock
	private Converter<B2BUnitModel, B2BAmountBalanceData> b2bAmountBalanceConverter;
	@Mock
	protected UserService userService;
	@Mock
	private I18NService i18NService;
	@Mock
	private AmountFormatter amountFormatter;
	@InjectMocks
	private final AccountSummaryInfoPopulator accountSummaryInfoPopulator = new AccountSummaryInfoPopulator();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldPopulate()
	{
		final String formattedAmount = "$123456";
		final String uid = "test-uid";
		final String displayName = "test-name";
		final String email = "test-uid@test.com";
		final BigDecimal amount = new BigDecimal(1000);

		final B2BUnitModel b2bUnitModel = mock(B2BUnitModel.class);
		final B2BCreditLimitModel b2bCreditLimitModel = mock(B2BCreditLimitModel.class);
		final CurrencyModel currencyModel = mock(CurrencyModel.class);
		final Locale locale = new Locale("en");
		final EmployeeModel employeeModel = mock(EmployeeModel.class);
		final UserModel accountManager = mock(UserModel.class);
		final AddressModel addressModel1 = mock(AddressModel.class);

		final B2BUnitData unitData = new B2BUnitData();
		final AddressData billingAddress = new AddressData();
		final AddressData shippingAddress = new AddressData();

		billingAddress.setBillingAddress(true);
		billingAddress.setId("Billing");
		shippingAddress.setBillingAddress(true);
		shippingAddress.setId("Shipping");
		unitData.setAddresses(Arrays.asList(billingAddress, shippingAddress));

		given(b2bUnitConverter.convert(b2bUnitModel)).willReturn(unitData);
		given(b2bUnitModel.getCreditLimit()).willReturn(b2bCreditLimitModel);
		given(b2bCreditLimitModel.getAmount()).willReturn(amount);
		given(b2bCreditLimitModel.getCurrency()).willReturn(currencyModel);
		given(i18NService.getCurrentLocale()).willReturn(locale);
		given(amountFormatter.formatAmount(amount, currencyModel, locale)).willReturn(formattedAmount);
		given(b2bUnitModel.getAccountManager()).willReturn(employeeModel);
		given(employeeModel.getUid()).willReturn(uid);
		given(userService.getUserForUID(uid)).willReturn(accountManager);
		given(accountManager.getDisplayName()).willReturn(displayName);
		given(addressModel1.getEmail()).willReturn(email);
		given(accountManager.getAddresses()).willReturn(Collections.singleton(addressModel1));

		final AccountSummaryInfoData accountSummaryInfoData = new AccountSummaryInfoData();

		accountSummaryInfoPopulator.populate(b2bUnitModel, accountSummaryInfoData);

		Assert.assertEquals(accountSummaryInfoData.getBillingAddress().getId(), "Billing");
		Assert.assertEquals(accountSummaryInfoData.getFormattedCreditLimit(), formattedAmount);
		Assert.assertEquals(accountSummaryInfoData.getAccountManagerName(), displayName);
		Assert.assertEquals(accountSummaryInfoData.getAccountManagerEmail(), email);
	}
}

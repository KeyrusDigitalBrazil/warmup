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
package de.hybris.platform.integration.cis.payment.converter;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import com.hybris.cis.client.payment.models.CisPaymentRequest;
import com.hybris.cis.client.payment.models.CisPaymentTransactionResult;
import com.hybris.cis.client.shared.models.CisDecision;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.payment.commands.result.AuthorizationResult;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CisAuthorizationResultConverterTest
{
	private static final String CLIENT_AUHT_ID = "5678";
	private static final String AUTH_ID = "1234";
	private static final String AUTH_LOCATION = "http://location/of/the/authorization/in/cis";

	private CisAuthorizationResultConverter cisAuthorizationResultConverter;

	@Mock
	private CisPaymentTransactionResult cisPaymentTransactionResult;
	@Mock
	private CisPaymentRequest cisPaymentRequest;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this.getClass());
		cisAuthorizationResultConverter = new CisAuthorizationResultConverter();
	}

	@Test
	public void shouldConvertRestResponse() throws URISyntaxException
	{
		//BDDMockito.when(rawResponse.location()).thenReturn(Optional.of(new URL(AUTH_LOCATION)));
		when(cisPaymentTransactionResult.getId()).thenReturn(AUTH_ID);
		when(cisPaymentTransactionResult.getHref()).thenReturn(AUTH_LOCATION);
		when(cisPaymentTransactionResult.getClientAuthorizationId()).thenReturn(CLIENT_AUHT_ID);
		when(cisPaymentTransactionResult.getRequest()).thenReturn(cisPaymentRequest);
		when(cisPaymentTransactionResult.getAmount()).thenReturn(BigDecimal.TEN);
		when(cisPaymentTransactionResult.getDecision()).thenReturn(CisDecision.ACCEPT);
		when(cisPaymentRequest.getCurrency()).thenReturn("USD");

		final AuthorizationResult authorizationResult = cisAuthorizationResultConverter.convert(cisPaymentTransactionResult);

		assertEquals(AUTH_LOCATION, authorizationResult.getRequestId());
		assertNotNull(authorizationResult.getAuthorizationTime());
		assertEquals(AUTH_ID, authorizationResult.getAuthorizationCode());
		assertNull(authorizationResult.getAvsStatus());
		assertEquals("USD", authorizationResult.getCurrency().getCurrencyCode());
		assertNull(authorizationResult.getCvnStatus());
		assertEquals(CLIENT_AUHT_ID, authorizationResult.getMerchantTransactionCode());
		assertEquals("cisCybersource", authorizationResult.getPaymentProvider());
		assertNull(authorizationResult.getReconciliationId());
		assertNull(authorizationResult.getRequestToken());
		assertEquals(BigDecimal.TEN, authorizationResult.getTotalAmount());
		assertEquals(TransactionStatus.ACCEPTED, authorizationResult.getTransactionStatus());
		assertEquals(TransactionStatusDetails.UNKNOWN_CODE, authorizationResult.getTransactionStatusDetails());

	}
}

package com.hybris.cis.service.impl;

import javax.ws.rs.core.Response.Status;
import java.net.URI;
import com.hybris.charon.RawResponse;
import com.hybris.cis.client.payment.PaymentClient;
import com.hybris.cis.client.payment.models.CisExternalPaymentRequest;
import com.hybris.cis.client.payment.models.CisPaymentAuthorization;
import com.hybris.cis.client.payment.models.CisPaymentProfileRequest;
import com.hybris.cis.client.payment.models.CisPaymentProfileResult;
import com.hybris.cis.client.payment.models.CisPaymentRequest;
import com.hybris.cis.client.payment.models.CisPaymentSessionInitRequest;
import com.hybris.cis.client.payment.models.CisPaymentTransactionResult;
import com.hybris.cis.client.payment.models.CisTokenizedPaymentAuthorization;
import com.hybris.cis.client.payment.models.CisTokenizedPaymentCapture;
import com.hybris.cis.client.payment.models.CisTokenizedPaymentRefund;
import com.hybris.cis.client.payment.models.CisTokenizedPaymentReverse;
import com.hybris.cis.client.payment.models.CisTokenizedPaymentTransactionResult;
import com.hybris.cis.service.CisClientPaymentService;


/**
 * Default implementation of {@link CisClientPaymentService}
 */
public class DefaultCisClientPaymentService implements CisClientPaymentService
{
	private PaymentClient cisPaymentClient;

	@Override
	public RawResponse<String> pspUrl(final String xCisClientRef, final String tenantId)
	{
		return getCisPaymentClient().pspUrl(xCisClientRef, tenantId);
	}

	@Override
	public CisPaymentTransactionResult handleExternalAuthorization(final String xCisClientRef, final String tenantId,
			final CisExternalPaymentRequest cisExternalPayment)
	{
		return getCisPaymentClient().handleExternalAuthorization(xCisClientRef, tenantId, cisExternalPayment);
	}

	@Override
	public CisPaymentTransactionResult authorizeWithProfile(final String xCisClientRef, final String tenantId,
			final URI profileLocation, final CisPaymentAuthorization cisPaymentAuthorization)
	{
		return getCisPaymentClient()
				.authorizeWithProfile(xCisClientRef, tenantId, extractLastUriParam(profileLocation), cisPaymentAuthorization);
	}

	@Override
	public CisPaymentTransactionResult capture(final String xCisClientRef, final String tenantId, final URI authorizationLocation,
			final String transactionId, final CisPaymentRequest paymentRequest)
	{
		return getCisPaymentClient()
				.capture(xCisClientRef, tenantId, extractLastUriParam(authorizationLocation), transactionId, paymentRequest);
	}

	@Override
	public CisPaymentTransactionResult reverse(final String xCisClientRef, final String tenantId, final URI authorizationLocation,
			final String transactionId, final CisPaymentRequest paymentRequest)
	{
		return getCisPaymentClient()
				.reverse(xCisClientRef, tenantId, extractLastUriParam(authorizationLocation), transactionId, paymentRequest);
	}

	@Override
	public CisPaymentTransactionResult refund(final String xCisClientRef, final String tenantId, final URI captureLocation,
			final String transactionId, final CisPaymentRequest paymentRequest)
	{
		return getCisPaymentClient().refund(xCisClientRef, tenantId, extractLastUriParam(captureLocation), transactionId, paymentRequest);
	}

	@Override
	public RawResponse<CisPaymentProfileResult> addCustomerProfile(final String xCisClientRef, final String tenantId,
			final String documentId, final CisExternalPaymentRequest cisExternalPayment)
	{
		return getCisPaymentClient().addCustomerProfile(xCisClientRef, tenantId, documentId, cisExternalPayment);
	}

	@Override
	public CisPaymentProfileResult updateCustomerProfile(final String xCisClientRef, final String tenantId,
			final URI profileLocation, final CisPaymentProfileRequest cisPaymentProfileRequest)
	{
		return getCisPaymentClient()
				.updateCustomerProfile(xCisClientRef, tenantId, extractLastUriParam(profileLocation), cisPaymentProfileRequest);
	}

	@Override
	public String deleteCustomerProfile(final String xCisClientRef, final String tenantId, final URI profileLocation)
	{
		return getCisPaymentClient().deleteCustomerProfile(xCisClientRef, tenantId, extractLastUriParam(profileLocation));
	}

	@Override
	public CisTokenizedPaymentTransactionResult initPaymentSession(final String xCisClientRef, final String tenantId,
			final CisPaymentSessionInitRequest cisPaymentSessionInitRequest)
	{
		return getCisPaymentClient().initPaymentSession(xCisClientRef, tenantId, cisPaymentSessionInitRequest);
	}

	@Override
	public CisTokenizedPaymentTransactionResult paymentOrderSetup(final String xCisClientRef, final String tenantId,
			final URI authorizationLocation, final CisTokenizedPaymentAuthorization cisTokenizedPaymentAuthorization)
	{
		return getCisPaymentClient()
				.paymentOrderSetup(xCisClientRef, tenantId, extractLastUriParam(authorizationLocation), cisTokenizedPaymentAuthorization);
	}

	@Override
	public CisTokenizedPaymentTransactionResult tokenizedPaymentAuthorization(final String xCisClientRef, final String tenantId,
			final CisTokenizedPaymentAuthorization cisTokenizedPaymentAuthorization)
	{
		return getCisPaymentClient().tokenizedPaymentAuthorization(xCisClientRef, tenantId, cisTokenizedPaymentAuthorization);
	}

	@Override
	public CisTokenizedPaymentTransactionResult tokenizedPaymentCapture(final String xCisClientRef, final String tenantId,
			final String authGroupId, final String authId, final CisTokenizedPaymentCapture cisTokenizedPaymentCapture)
	{
		return getCisPaymentClient()
				.tokenizedPaymentCapture(xCisClientRef, tenantId, authGroupId, authId, cisTokenizedPaymentCapture);
	}

	@Override
	public CisTokenizedPaymentTransactionResult tokenizedPaymentRefund(final String xCisClientRef, final String tenantId,
			final String authGroupId, final String authId, final CisTokenizedPaymentRefund cisTokenizedPaymentRefund)
	{
		return getCisPaymentClient()
				.tokenizedPaymentRefund(xCisClientRef, tenantId, authGroupId, authId, cisTokenizedPaymentRefund.getCaptureRequestId(),
						cisTokenizedPaymentRefund);
	}

	@Override
	public CisTokenizedPaymentTransactionResult tokenizedPaymentReverse(final String xCisClientRef, final String tenantId,
			final String authGroupId, final String authId, final CisTokenizedPaymentReverse cisTokenizedPaymentReverse)
	{
		return getCisPaymentClient()
				.tokenizedPaymentReverse(xCisClientRef, tenantId, authGroupId, authId, cisTokenizedPaymentReverse);
	}

	@Override
	public boolean ping(final String xCisClientRef, final String tenantId)
	{
		return getCisPaymentClient().doPing(xCisClientRef, tenantId).status().equals(Status.CREATED) ? true : false;
	}

	/**
	 * Extracts the last part of the URI
	 *
	 * @param uri
	 * 		the URI for which we want to extract the last parameter
	 * @return the last parameter of the URI
	 */
	protected String extractLastUriParam(URI uri)
	{
		String strUri = uri.toString();
		if(strUri.contains("?")){
			strUri = strUri.substring(0, strUri.indexOf('?'));
		}
		final String[] uriSplit = strUri.split("/");
		return uriSplit[uriSplit.length - 1];
	}

	/**
	 * @return the cisPaymentClient
	 */
	protected PaymentClient getCisPaymentClient()
	{
		return cisPaymentClient;
	}

	/**
	 * @param cisPaymentClient
	 * 		the cisPaymentClient to set
	 */
	public void setCisPaymentClient(final PaymentClient cisPaymentClient)
	{
		this.cisPaymentClient = cisPaymentClient;
	}
}

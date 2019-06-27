/**
 *
 */
package com.hybris.cis.service;

import java.net.URI;
import com.hybris.charon.RawResponse;
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



/**
 * CIS service which exposes the payment functionalities
 */
public interface CisClientPaymentService extends CisClientService
{
	/**
	 * Retrieves the payment service url for a given client and tenant
	 *
	 * @param xCisClientRef
	 * 		the client reference
	 * @param tenantId
	 * 		the tenant identifier
	 * @return a raw response with the psp url in the location attribute of the header
	 */
	RawResponse<String> pspUrl(final String xCisClientRef, final String tenantId);

	/**
	 * Requests a payment authorization
	 *
	 * @param xCisClientRef
	 * 		the client reference
	 * @param tenantId
	 * 		the tenant identifier
	 * @param cisExternalPayment
	 * 		the external payment request
	 * @return the result of the payment transaction
	 */
	CisPaymentTransactionResult handleExternalAuthorization(final String xCisClientRef, final String tenantId,
			final CisExternalPaymentRequest cisExternalPayment);

	/**
	 * Requests an authorization of a payment
	 *
	 * @param xCisClientRef
	 * 		the client reference
	 * @param tenantId
	 * 		the tenant identifier
	 * @param profileLocation
	 * 		the URI of the profile to be authorized
	 * @param cisPaymentAuthorization
	 * 		the payment authorization to be authorized
	 * @return
	 */
	CisPaymentTransactionResult authorizeWithProfile(final String xCisClientRef, final String tenantId, final URI profileLocation,
			final CisPaymentAuthorization cisPaymentAuthorization);

	/**
	 * Captures a payment based on the given properties
	 *
	 * @param xCisClientRef
	 * 		the client reference
	 * @param tenantId
	 * 		the tenant identifier
	 * @param authorizationLocation
	 * @param transactionId
	 * 		the transaction identifier to be captured
	 * @param paymentRequest
	 * 		the corresponding payment request
	 * @return the payment transaction result
	 */
	CisPaymentTransactionResult capture(final String xCisClientRef, final String tenantId, final URI authorizationLocation,
			final String transactionId, final CisPaymentRequest paymentRequest);

	/**
	 * Reverses a payment based on the given properties
	 *
	 * @param xCisClientRef
	 * 		the client reference
	 * @param tenantId
	 * 		the tenant identifier
	 * @param authorizationLocation
	 * 		the location of the granted authorization
	 * @param transactionId
	 * 		the transaction identifier to be reversed
	 * @param paymentRequest
	 * 		the corresponding payment request
	 * @return the reversal payment transaction result
	 */
	CisPaymentTransactionResult reverse(final String xCisClientRef, final String tenantId, final URI authorizationLocation,
			final String transactionId, final CisPaymentRequest paymentRequest);

	/**
	 * Refunds a payment transaction
	 *
	 * @param xCisClientRef
	 * 		the client reference
	 * @param tenantId
	 * 		the tenant identifier
	 * @param captureLocation
	 * 		the URI of the captured location
	 * @param transactionId
	 * 		the transaction identifier to be reversed
	 * @param paymentRequest
	 * 		the corresponding payment request
	 * @return the result of the refund payment transaction
	 */
	CisPaymentTransactionResult refund(final String xCisClientRef, final String tenantId, final URI captureLocation,
			final String transactionId, final CisPaymentRequest paymentRequest);

	/**
	 * Add a customer profile
	 *
	 * @param xCisClientRef
	 * 		the client reference
	 * @param tenantId
	 * 		the tenant identifier
	 * @param documentId
	 * 		the document identifier
	 * @param cisExternalPayment
	 * 		the external payment request
	 * @return a raw response with the payment profile created
	 */
	RawResponse<CisPaymentProfileResult> addCustomerProfile(final String xCisClientRef, final String tenantId,
			final String documentId, final CisExternalPaymentRequest cisExternalPayment);

	/**
	 * Updates a customer profile
	 *
	 * @param xCisClientRef
	 * 		the client reference
	 * @param tenantId
	 * 		the tenant identifier
	 * @param profileLocation
	 * 		the URI of the profile location
	 * @param cisPaymentProfileRequest
	 * 		the payment profile request
	 * @return the profile payment updated
	 */
	CisPaymentProfileResult updateCustomerProfile(final String xCisClientRef, final String tenantId, final URI profileLocation,
			final CisPaymentProfileRequest cisPaymentProfileRequest);

	/**
	 * Deletes a given customer profile
	 *
	 * @param xCisClientRef
	 * 		the client reference
	 * @param tenantId
	 * 		the tenant identifier
	 * @param profileLocation
	 * 		the URI of the profile to delete
	 * @return
	 */
	String deleteCustomerProfile(final String xCisClientRef, final String tenantId, final URI profileLocation);

	/**
	 * Initialize the payment session
	 *
	 * @param xCisClientRef
	 * 		the client reference
	 * @param tenantId
	 * 		the tenant identifier
	 * @param cisPaymentSessionInitRequest
	 * 		the payment session request
	 * @return {@link CisTokenizedPaymentTransactionResult}
	 */
	CisTokenizedPaymentTransactionResult initPaymentSession(final String xCisClientRef, final String tenantId,
			final CisPaymentSessionInitRequest cisPaymentSessionInitRequest);

	/**
	 * Setup order payment
	 *
	 * @param xCisClientRef
	 * 		the client reference
	 * @param tenantId
	 * 		the tenant identifier
	 * @param authorizationLocation
	 * 		the URI of the authorization
	 * @param cisTokenizedPaymentAuthorization
	 * @return {@link CisTokenizedPaymentTransactionResult}
	 */
	CisTokenizedPaymentTransactionResult paymentOrderSetup(final String xCisClientRef, final String tenantId,
			final URI authorizationLocation, final CisTokenizedPaymentAuthorization cisTokenizedPaymentAuthorization);

	/**
	 * Tokenized Payment Authorization
	 *
	 * @param xCisClientRef
	 * 		the client reference
	 * @param tenantId
	 * 		the tenant identifier
	 * @param cisTokenizedPaymentAuthorization
	 * @return {@link CisTokenizedPaymentTransactionResult}
	 */
	CisTokenizedPaymentTransactionResult tokenizedPaymentAuthorization(final String xCisClientRef, final String tenantId,
			final CisTokenizedPaymentAuthorization cisTokenizedPaymentAuthorization);

	/**
	 * Tokenized Payment Capture
	 *
	 * @param xCisClientRef
	 * 		the client reference
	 * @param tenantId
	 * 		the tenant identifier
	 * @param authGroupId
	 * 		the authentication group identifier
	 * @param authId
	 * 		the authentication identifier
	 * @param cisTokenizedPaymentCapture
	 * @return {@link CisTokenizedPaymentTransactionResult}
	 */
	CisTokenizedPaymentTransactionResult tokenizedPaymentCapture(final String xCisClientRef, final String tenantId,
			final String authGroupId, final String authId, final CisTokenizedPaymentCapture cisTokenizedPaymentCapture);

	/**
	 * Tokenized Payment Refund
	 *
	 * @param xCisClientRef
	 * 		the client reference
	 * @param tenantId
	 * 		the tenant identifier
	 * @param authGroupId
	 * 		the authentication group identifier
	 * @param authId
	 * 		the authentication identifier
	 * @param cisTokenizedPaymentRefund
	 * @return {@link CisTokenizedPaymentTransactionResult}
	 */
	CisTokenizedPaymentTransactionResult tokenizedPaymentRefund(final String xCisClientRef, final String tenantId,
			final String authGroupId, final String authId, final CisTokenizedPaymentRefund cisTokenizedPaymentRefund);

	/**
	 * Tokenized Payment Reverse
	 *
	 * @param xCisClientRef
	 * 		the client reference
	 * @param tenantId
	 * 		the tenant identifier
	 * @param authGroupId
	 * 		the authentication group identifier
	 * @param authId
	 * 		the authentication identifier
	 * @param cisTokenizedPaymentReverse
	 * @return {@link CisTokenizedPaymentTransactionResult}
	 */
	CisTokenizedPaymentTransactionResult tokenizedPaymentReverse(final String xCisClientRef, final String tenantId,
			final String authGroupId, final String authId, final CisTokenizedPaymentReverse cisTokenizedPaymentReverse);

}

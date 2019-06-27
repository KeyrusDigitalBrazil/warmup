/**
 * TODO: description here!
 */
package com.hybris.cis.client.payment;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import com.hybris.charon.RawResponse;
import com.hybris.charon.annotations.Control;
import com.hybris.charon.annotations.Http;
import com.hybris.cis.client.CisClient;
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
 * Charon Client to CIS Payment API
 */
@Http("payment")
public interface PaymentClient extends CisClient
{
	@HEAD
	@Produces("application/json")
	@Consumes("application/json")
	@Path("/hpfurl")
	@Control(retries = "3", retriesInterval = "500")
	RawResponse<String> pspUrl(@HeaderParam(value = "X-CIS-Client-Ref") String xCisClientRef,
			@HeaderParam(value = "X-tenantId") String tenantId);

	@POST
	@Produces("application/json")
	@Consumes("application/json")
	@Path("/authorizations")
	@Control(retries = "3", retriesInterval = "500")
	CisPaymentTransactionResult handleExternalAuthorization(@HeaderParam(value = "X-CIS-Client-Ref") String xCisClientRef,
			@HeaderParam(value = "X-tenantId") String tenantId, CisExternalPaymentRequest cisExternalPayment);

	@POST
	@Produces("application/json")
	@Consumes("application/json")
	@Path("/profiles/{profileId}/authorizations")
	@Control(retries = "3", retriesInterval = "500")
	CisPaymentTransactionResult authorizeWithProfile(@HeaderParam(value = "X-CIS-Client-Ref") String xCisClientRef,
			@HeaderParam(value = "X-tenantId") String tenantId, @PathParam(value = "profileId") String profileId,
			CisPaymentAuthorization cisPaymentAuthorization);

	@POST
	@Produces("application/json")
	@Consumes("application/json")
	@Path("/authorizations/{authId}/{transactionId}/captures")
	@Control(retries = "3", retriesInterval = "500")
	CisPaymentTransactionResult capture(@HeaderParam(value = "X-CIS-Client-Ref") String xCisClientRef,
			@HeaderParam(value = "X-tenantId") String tenantId, @PathParam(value = "authId") String authId,
			@PathParam(value = "transactionId") String transactionId, CisPaymentRequest paymentRequest);

	@POST
	@Produces("application/json")
	@Consumes("application/json")
	@Path("/authorizations/{authId}/{transactionId}/reverses")
	@Control(retries = "3", retriesInterval = "500")
	CisPaymentTransactionResult reverse(@HeaderParam(value = "X-CIS-Client-Ref") String xCisClientRef,
			@HeaderParam(value = "X-tenantId") String tenantId, @PathParam(value = "authId") String authId,
			@PathParam(value = "transactionId") String transactionId, CisPaymentRequest paymentRequest);

	@POST
	@Produces("application/json")
	@Consumes("application/json")
	@Path("/authorizations/{authId}/{transactionId}/refunds")
	@Control(retries = "3", retriesInterval = "500")
	CisPaymentTransactionResult refund(@HeaderParam(value = "X-CIS-Client-Ref") String xCisClientRef,
			@HeaderParam(value = "X-tenantId") String tenantId, @PathParam(value = "authId") String authId,
			@PathParam(value = "transactionId") String transactionId, CisPaymentRequest paymentRequest);

	@POST
	@Produces("application/json")
	@Consumes("application/json")
	@Path("/profiles/{documentId}")
	@Control(retries = "3", retriesInterval = "500")
	RawResponse<CisPaymentProfileResult> addCustomerProfile(@HeaderParam(value = "X-CIS-Client-Ref") String xCisClientRef,
			@HeaderParam(value = "X-tenantId") String tenantId, @PathParam(value = "documentId") String documentId,
			CisExternalPaymentRequest cisExternalPayment);

	@PUT
	@Produces("application/json")
	@Consumes("application/json")
	@Path("/profiles/{profileId}")
	@Control(retries = "3", retriesInterval = "500")
	CisPaymentProfileResult updateCustomerProfile(@HeaderParam(value = "X-CIS-Client-Ref") String xCisClientRef,
			@HeaderParam(value = "X-tenantId") String tenantId, @PathParam(value = "profileId") String profileId,
			CisPaymentProfileRequest cisPaymentProfileRequest);

	@DELETE
	@Produces("application/json")
	@Consumes("application/json")
	@Path("/profiles/{profileId}")
	@Control(retries = "3", retriesInterval = "500")
	String deleteCustomerProfile(@HeaderParam(value = "X-CIS-Client-Ref") String xCisClientRef,
			@HeaderParam(value = "X-tenantId") String tenantId, @PathParam(value = "profileId") String profileId);

	@POST
	@Produces("application/json")
	@Consumes("application/json")
	@Path("/paymentsessions")
	@Control(retries = "3", retriesInterval = "500")
	CisTokenizedPaymentTransactionResult initPaymentSession(@HeaderParam(value = "X-CIS-Client-Ref") String xCisClientRef,
			@HeaderParam(value = "X-tenantId") String tenantId, CisPaymentSessionInitRequest cisPaymentSessionInitRequest);

	@POST
	@Produces("application/json")
	@Consumes("application/json")
	@Path("/authorizationsgroups/{authGroupId}/authorizations")
	@Control(retries = "3", retriesInterval = "500")
	CisTokenizedPaymentTransactionResult paymentOrderSetup(@HeaderParam(value = "X-CIS-Client-Ref") String xCisClientRef,
			@HeaderParam(value = "X-tenantId") String tenantId, @PathParam(value = "authGroupId") String authGroupId,
			CisTokenizedPaymentAuthorization cisTokenizedPaymentAuthorization);

	@POST
	@Produces("application/json")
	@Consumes("application/json")
	@Path("/authorizationsgroups")
	@Control(retries = "3", retriesInterval = "500")
	CisTokenizedPaymentTransactionResult tokenizedPaymentAuthorization(
			@HeaderParam(value = "X-CIS-Client-Ref") String xCisClientRef, @HeaderParam(value = "X-tenantId") String tenantId,
			CisTokenizedPaymentAuthorization cisTokenizedPaymentAuthorization);

	@POST
	@Produces("application/json")
	@Consumes("application/json")
	@Path("/authorizationsgroups/{authGroupId}/authorizations/{authId}/captures")
	@Control(retries = "3", retriesInterval = "500")
	CisTokenizedPaymentTransactionResult tokenizedPaymentCapture(@HeaderParam(value = "X-CIS-Client-Ref") String xCisClientRef,
			@HeaderParam(value = "X-tenantId") String tenantId, @PathParam(value = "authGroupId") String authGroupId,
			@PathParam(value = "authId") String authId, CisTokenizedPaymentCapture cisTokenizedPaymentCapture);

	@POST
	@Produces("application/json")
	@Consumes("application/json")
	@Path("/authorizationsgroups/{authGroupId}/authorizations/{authId}/captures/{transactionId}/refunds")
	@Control(retries = "3", retriesInterval = "500")
	CisTokenizedPaymentTransactionResult tokenizedPaymentRefund(@HeaderParam(value = "X-CIS-Client-Ref") String xCisClientRef,
			@HeaderParam(value = "X-tenantId") String tenantId, @PathParam(value = "authGroupId") String authGroupId,
			@PathParam(value = "authId") String authId, @PathParam(value = "transactionId") String transactionId,
			CisTokenizedPaymentRefund cisTokenizedPaymentRefund);

	@POST
	@Produces("application/json")
	@Consumes("application/json")
	@Path("/authorizationsgroups/{authGroupId}/authorizations/{authId}/reverses")
	@Control(retries = "3", retriesInterval = "500")
	CisTokenizedPaymentTransactionResult tokenizedPaymentReverse(@HeaderParam(value = "X-CIS-Client-Ref") String xCisClientRef,
			@HeaderParam(value = "X-tenantId") String tenantId, @PathParam(value = "authGroupId") String authGroupId,
			@PathParam(value = "authId") String authId, CisTokenizedPaymentReverse cisTokenizedPaymentReverse);
}

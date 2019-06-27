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
package de.hybris.platform.couponwebservices.test.webservices;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.couponwebservices.constants.CouponwebservicesConstants;
import de.hybris.platform.couponwebservices.dto.CouponValidationResponseWsDTO;
import de.hybris.platform.couponwebservices.dto.SingleCodeCouponWsDTO;
import de.hybris.platform.couponwebservices.dto.ws.SingleCodeCouponsSearchPageWsDTO;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.dto.PaginationWsDTO;
import de.hybris.platform.webservicescommons.dto.SortWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert;
import de.hybris.platform.webservicescommons.testsupport.client.WsRequestBuilder;
import de.hybris.platform.webservicescommons.testsupport.client.WsSecuredRequestBuilder;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;


@NeedsEmbeddedServer(webExtensions =
{ CouponwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class SingleCodeCouponWebServicesTest extends AbstractCouponWebServicesTest
{
	private static final String BASE_URI = "couponservices/v2";
	private static final String URI = BASE_URI + "/singlecodecoupon";

	@Override
	String getUri()
	{
		return URI;
	}

	@Before
	public void setUp() throws Exception
	{
		wsRequestBuilder = new WsRequestBuilder()//
				.extensionName(CouponwebservicesConstants.EXTENSIONNAME);

		wsSecuredRequestBuilder = new WsSecuredRequestBuilder()//
				.extensionName(CouponwebservicesConstants.EXTENSIONNAME)//
				.client(OAUTH_CLIENT_ID, OAUTH_CLIENT_PASS)//
				.grantClientCredentials();

		createCoreData();
		createDefaultUsers();
		importCsv("/couponwebservices/test/ws-user.impex", "utf-8");
		importCsv("/couponwebservices/test/coupon-test-data.impex", "utf-8");
		importCsv("/couponwebservices/test/coupon-redemption-test-data.impex", "utf-8");
	}

	@Test
	public void testGetSingleCodeCouponsWithoutAuthorization()
	{
		final Response result = wsRequestBuilder//
				.path(URI)//
				.path("list")//
				.build()//
				.accept(MediaType.APPLICATION_XML)//
				.get();
		result.bufferEntity();
		WebservicesAssert.assertResponse(Status.UNAUTHORIZED, Optional.empty(), result);
	}

	@Test
	public void shouldReturnSingleCodeCouponsSortedByNameWithDescOrder()
	{
		//given
		final Response result = wsSecuredRequestBuilder//
				.path(URI)//
				.path("list")//
				.queryParam("sort","name:desc")//
				.build()//
				.accept(MediaType.APPLICATION_JSON)//
				.get();
		//when
		final SingleCodeCouponsSearchPageWsDTO singleCodeCouponsSearchPageWsDTO = result.readEntity(SingleCodeCouponsSearchPageWsDTO.class);
		//then
		WebservicesAssert.assertResponse(Status.OK, Optional.empty(), result);
		final SortWsDTO sort = singleCodeCouponsSearchPageWsDTO.getSorts().iterator().next();
		assertSort(sort,"name",false);
	}

	@Test
	public void shouldReturnSingleCodeCouponsPaged()
	{
		//given
		final Response result = wsSecuredRequestBuilder//
				.path(URI)//
				.path("list")//
				.queryParam("currentPage",1).queryParam("pageSize",1)//
				.build()//
				.accept(MediaType.APPLICATION_JSON)//
				.get();
		//when
		final SingleCodeCouponsSearchPageWsDTO singleCodeCouponsSearchPageWsDTO = result.readEntity(SingleCodeCouponsSearchPageWsDTO.class);
		//then
		WebservicesAssert.assertResponse(Status.OK, Optional.empty(), result);
		final PaginationWsDTO pagination = singleCodeCouponsSearchPageWsDTO.getPagination();
		assertThat(pagination.getPage()).isEqualTo(1);
		assertThat(pagination.getCount()).isEqualTo(1);
	}


	private void assertSort(final SortWsDTO sort,String code, boolean asc)
	{
		assertThat(sort.getCode()).isEqualTo(code);
		assertThat(sort.isAsc()).isEqualTo(asc);
	}
	@Test
	public void shouldReturnSingleCodeCoupons()
	{
		//given
		final Response result = wsSecuredRequestBuilder//
				.path(URI)//
				.path("list")//
				.build()//
				.accept(MediaType.APPLICATION_JSON)//
				.get();
		//when
		final SingleCodeCouponsSearchPageWsDTO singleCodeCouponsSearchPageWsDTO = result.readEntity(SingleCodeCouponsSearchPageWsDTO.class);
		//then
		WebservicesAssert.assertResponse(Status.OK, Optional.empty(), result);
		Assertions.assertThat(singleCodeCouponsSearchPageWsDTO.getResults()).isNotEmpty();
	}

	@Test
	public void testGetSingleCodeCouponUsingClientCredentials()
	{
		final Response result = wsSecuredRequestBuilder//
				.path(URI)//
				.path("get")//
				.path("TEST_COUPON1")//
				.build()//
				.accept(MediaType.APPLICATION_XML)//
				.get();
		result.bufferEntity();
		WebservicesAssert.assertResponse(Status.OK, Optional.empty(), result);
	}

	@Test
	public void testPostSingleCodeCouponWsDTO()
	{
		final SingleCodeCouponWsDTO singleCodeCouponWsDTO = new SingleCodeCouponWsDTO();
		singleCodeCouponWsDTO.setCouponId("TEST_NEW_COUPON");
		final Response result = wsSecuredRequestBuilder//
				.path(URI)//
				.path("create")//
				.build()//
				.post(Entity.entity(singleCodeCouponWsDTO, MediaType.APPLICATION_JSON));
		final SingleCodeCouponWsDTO respSingleCodeCouponWsDTO = result.readEntity(SingleCodeCouponWsDTO.class);
		WebservicesAssert.assertResponse(Status.CREATED, Optional.empty(), result);
		assertNotNull(respSingleCodeCouponWsDTO);
		assertEquals("TEST_NEW_COUPON", respSingleCodeCouponWsDTO.getCouponId());
	}

	/**
	 * The test should detect the fail with 1 error:</br>
	 * errors" : [ {</br>
	 * "message" : "This field is required.",</br>
	 * "reason" : "missing",</br>
	 * "subject" : "couponId",</br>
	 * "subjectType" : "parameter",</br>
	 * "type" : "ValidationError"</br>
	 * } ]</br>
	 **/
	@Test
	public void testPostEmptySingleCodeCouponWsDTO()
	{
		final SingleCodeCouponWsDTO singleCodeCouponWsDTO = new SingleCodeCouponWsDTO();
		final Response response = wsSecuredRequestBuilder.path(URI).path("create").build()
				.post(Entity.entity(singleCodeCouponWsDTO, MediaType.APPLICATION_JSON));
		WebservicesAssert.assertResponse(Status.BAD_REQUEST, Optional.empty(), response);
		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(errors.getErrors().size(), 1);
		final ErrorWsDTO error = errors.getErrors().get(0);
		assertEquals("missing", error.getReason());
		assertEquals("couponId", error.getSubject());
		assertEquals("parameter", error.getSubjectType());
	}

	@Test
	public void testPostSingleCodeCouponWithDatesWsDTO()
	{
		final SingleCodeCouponWsDTO singleCodeCouponWsDTO = new SingleCodeCouponWsDTO();
		singleCodeCouponWsDTO.setCouponId("TEST_NEW_COUPON");
		singleCodeCouponWsDTO.setStartDate(LocalDateTime.now().minusDays(30).format(DateTimeFormatter.ISO_DATE_TIME));
		singleCodeCouponWsDTO.setEndDate(LocalDateTime.now().plusDays(60).format(DateTimeFormatter.ISO_DATE_TIME));
		final Response result = wsSecuredRequestBuilder//
				.path(URI).path("create")//
				.build()//
				.post(Entity.entity(singleCodeCouponWsDTO, MediaType.APPLICATION_JSON));
		WebservicesAssert.assertResponse(Status.CREATED, Optional.empty(), result);
		final SingleCodeCouponWsDTO respSingleCodeCouponWsDTO = result.readEntity(SingleCodeCouponWsDTO.class);
		assertNotNull(respSingleCodeCouponWsDTO);
		assertEquals("TEST_NEW_COUPON", respSingleCodeCouponWsDTO.getCouponId());
	}

	@Test
	public void testPostSingleCodeCouponWithInconsistentDatesWsDTO()
	{
		final SingleCodeCouponWsDTO singleCodeCouponWsDTO = new SingleCodeCouponWsDTO();
		singleCodeCouponWsDTO.setCouponId("TEST_NEW_COUPON");
		singleCodeCouponWsDTO.setEndDate(LocalDateTime.now().minusDays(30).format(DateTimeFormatter.ISO_DATE_TIME));
		singleCodeCouponWsDTO.setStartDate(LocalDateTime.now().plusDays(60).format(DateTimeFormatter.ISO_DATE_TIME));
		final Response result = wsSecuredRequestBuilder//
				.path(URI).path("create")//
				.build()//
				.post(Entity.entity(singleCodeCouponWsDTO, MediaType.APPLICATION_JSON));
		WebservicesAssert.assertResponse(Status.BAD_REQUEST, Optional.empty(), result);
		final ErrorListWsDTO errors = result.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(1, errors.getErrors().size());
		final ErrorWsDTO error = errors.getErrors().get(0);
		assertEquals("invalid", error.getReason());
		assertEquals("startDate", error.getSubject());
		assertEquals("parameter", error.getSubjectType());
	}


	protected Response putSingleCodeCouponWsDTO(final String couponId, final String couponName, final String startDateVal,
			final String endDateVal,
			final Integer maxRedemptionsPerCustomer, final Integer maxTotalRedemptions)
	{
		final SingleCodeCouponWsDTO singleCodeCouponWsDTO = new SingleCodeCouponWsDTO();
		singleCodeCouponWsDTO.setCouponId(couponId);
		singleCodeCouponWsDTO.setName(couponName);
		singleCodeCouponWsDTO.setStartDate(startDateVal);
		singleCodeCouponWsDTO.setEndDate(endDateVal);
		singleCodeCouponWsDTO.setMaxRedemptionsPerCustomer(maxRedemptionsPerCustomer);
		singleCodeCouponWsDTO.setMaxTotalRedemptions(maxTotalRedemptions);
		final Response putResult = wsSecuredRequestBuilder//
				.path(URI)//
				.path("update")//
				.build()//
				.put(Entity.entity(singleCodeCouponWsDTO, MediaType.APPLICATION_JSON));
		putResult.bufferEntity();

		if (putResult.getStatus() == Status.NO_CONTENT.getStatusCode())
		{

			final Response getResult = new WsSecuredRequestBuilder()//
					.extensionName(CouponwebservicesConstants.EXTENSIONNAME)//
					.client(OAUTH_CLIENT_ID, OAUTH_CLIENT_PASS)//
					.grantClientCredentials()//
					.path(URI)//
					.path("get")//
					.path(couponId)//
					.build()//
					.accept(MediaType.APPLICATION_JSON)//
					.get();
			WebservicesAssert.assertResponse(Status.OK, Optional.empty(), getResult);
			final SingleCodeCouponWsDTO respSingleCodeCouponWsDTO = getResult.readEntity(SingleCodeCouponWsDTO.class);
			assertNotNull(respSingleCodeCouponWsDTO);
			assertEquals(couponId, respSingleCodeCouponWsDTO.getCouponId());
			assertEquals(startDateVal, respSingleCodeCouponWsDTO.getStartDate());
			assertEquals(endDateVal, respSingleCodeCouponWsDTO.getEndDate());
			assertEquals(maxRedemptionsPerCustomer, respSingleCodeCouponWsDTO.getMaxRedemptionsPerCustomer());
			assertEquals(maxTotalRedemptions, respSingleCodeCouponWsDTO.getMaxTotalRedemptions());
		}
		return putResult;
	}

	@Test
	public void testPutSingleCodeCouponWsDTO()
	{
		final Response putResult = putSingleCodeCouponWsDTO("TEST_COUPON4", "name", null, null, Integer.valueOf(5),
				Integer.valueOf(15));
		WebservicesAssert.assertResponse(Status.NO_CONTENT, Optional.empty(), putResult);
	}

	@Test
	public void testPutSingleCodeActiveCouponWsDTO()
	{
		final Response putResult = putSingleCodeCouponWsDTO("TEST_COUPON7", "name", null, null, Integer.valueOf(5),
				Integer.valueOf(15));
		WebservicesAssert.assertResponse(Status.BAD_REQUEST, Optional.empty(), putResult);
		final ErrorListWsDTO errors = putResult.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(errors.getErrors().size(), 1);
		final ErrorWsDTO error = errors.getErrors().get(0);
		assertEquals("active", error.getReason());
		assertEquals("active", error.getSubject());
		assertEquals("state", error.getSubjectType());
	}

	/**
	 * The test should detect the fail with 1 error:</br>
	 * errors" : [ {</br>
	 * "message" : "This field is required.",</br>
	 * "reason" : "missing",</br>
	 * "subject" : "couponId",</br>
	 * "subjectType" : "parameter",</br>
	 * "type" : "ValidationError"</br>
	 * } ]</br>
	 **/
	@Test
	public void testPutEmptySingleCodeCouponWsDTO()
	{
		final Response putResult = putSingleCodeCouponWsDTO(null, "name", null, null, Integer.valueOf(5), Integer.valueOf(15));
		WebservicesAssert.assertResponse(Status.BAD_REQUEST, Optional.empty(), putResult);
		final ErrorListWsDTO errors = putResult.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(errors.getErrors().size(), 1);
		final ErrorWsDTO error = errors.getErrors().get(0);
		assertEquals("missing", error.getReason());
		assertEquals("couponId", error.getSubject());
		assertEquals("parameter", error.getSubjectType());
	}

	@Test
	public void testPutInvalidSingleCodeCouponWsDTO()
	{
		final Response putResult = putSingleCodeCouponWsDTO("not_existing_coupon_code", "name", null, null, Integer.valueOf(5),
				Integer.valueOf(15));
		WebservicesAssert.assertResponse(Status.NOT_FOUND, Optional.empty(), putResult);
		final ErrorListWsDTO errors = putResult.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(errors.getErrors().size(), 1);
		final ErrorWsDTO error = errors.getErrors().get(0);
		assertEquals("invalid", error.getReason());
		assertEquals("couponId", error.getSubject());
	}

	@Test
	public void testPutInvalidSingleCodeCouponWsDTO2()
	{
		final Response putResult = putSingleCodeCouponWsDTO("COUPON5", "name", null, null, Integer.valueOf(5), Integer.valueOf(15));
		WebservicesAssert.assertResponse(Status.NOT_FOUND, Optional.empty(), putResult);
		final ErrorListWsDTO errors = putResult.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(errors.getErrors().size(), 1);
		final ErrorWsDTO error = errors.getErrors().get(0);
		assertTrue(error.getMessage().contains("No single code coupon"));
	}

	@Test
	public void testPutSingleCodeCouponWithInvalidDatesWsDTO()
	{
		final String startDate = LocalDateTime.now().minusDays(30).format(DateTimeFormatter.ISO_DATE_TIME);
		final String endDate = "invalid date format";

		final Response putResult = putSingleCodeCouponWsDTO("TEST_COUPON5", "name", startDate, endDate, Integer.valueOf(5),
				Integer.valueOf(15));
		WebservicesAssert.assertResponse(Status.BAD_REQUEST, Optional.empty(), putResult);
		final ErrorListWsDTO errors = putResult.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(1, errors.getErrors().size());
		final ErrorWsDTO error = errors.getErrors().get(0);
		assertEquals("invalid", error.getReason());
		assertEquals("endDate", error.getSubject());
		assertEquals("parameter", error.getSubjectType());
	}

	@Test
	public void testPutSingleCodeCouponWithDatesWsDTO()
	{
		final String startDate = LocalDateTime.now().minusDays(30).format(DateTimeFormatter.ISO_DATE_TIME);
		final String endDate = LocalDateTime.now().plusDays(60).format(DateTimeFormatter.ISO_DATE_TIME);

		final Response putResult = putSingleCodeCouponWsDTO("TEST_COUPON5", "name", startDate, endDate, Integer.valueOf(5),
				Integer.valueOf(15));
		WebservicesAssert.assertResponse(Status.NO_CONTENT, Optional.empty(), putResult);
	}

	@Test
	public void testPutSingleCodeCouponWithInconsistentDatesWsDTO()
	{
		final String endDate = LocalDateTime.now().minusDays(30).format(DateTimeFormatter.ISO_DATE_TIME);
		final String startDate = LocalDateTime.now().plusDays(60).format(DateTimeFormatter.ISO_DATE_TIME);

		final Response putResult = putSingleCodeCouponWsDTO("TEST_COUPON6", "name", startDate, endDate, Integer.valueOf(5),
				Integer.valueOf(15));
		WebservicesAssert.assertResponse(Status.BAD_REQUEST, Optional.empty(), putResult);
		final ErrorListWsDTO errors = putResult.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(1, errors.getErrors().size());
		final ErrorWsDTO error = errors.getErrors().get(0);
		assertEquals("invalid", error.getReason());
		assertEquals("startDate", error.getSubject());
		assertEquals("parameter", error.getSubjectType());
	}

	@Test
	public void testPutCouponStatusWithoutAuthorization()
	{
		final Response result = updateCouponStatusWithoutAuthorization("COUPON8", Boolean.FALSE);
		WebservicesAssert.assertResponse(Status.UNAUTHORIZED, Optional.empty(), result);
	}

	@Test
	public void testPutCouponStatusWsDTO1()
	{
		final Response putResult = updateCouponStatus("TEST_COUPON8", Boolean.FALSE);
		WebservicesAssert.assertResponse(Status.NO_CONTENT, Optional.empty(), putResult);
	}

	@Test
	public void testPutCouponStatusWsDTO2()
	{
		final Response putResult = updateCouponStatus("TEST_COUPON9", Boolean.TRUE);
		WebservicesAssert.assertResponse(Status.NO_CONTENT, Optional.empty(), putResult);
	}

	@Test
	public void testPutEmptyStatusCouponStatusWsDTO()
	{
		final Response putResult = updateCouponStatus("TEST_COUPON8", null);
		WebservicesAssert.assertResponse(Status.BAD_REQUEST, Optional.empty(), putResult);
		final ErrorListWsDTO errors = putResult.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(errors.getErrors().size(), 1);
		final ErrorWsDTO error = errors.getErrors().get(0);
		assertEquals("missing", error.getReason());
		assertEquals("active", error.getSubject());
		assertEquals("parameter", error.getSubjectType());
	}

	@Test
	public void testValidateSingleCodeCouponWithoutAuthorization()
	{
		final Response result = validateCouponWithoutAuthorization("TEST_COUPON1");
		WebservicesAssert.assertResponse(Status.UNAUTHORIZED, Optional.empty(), result);
	}

	@Test
	public void testValidateSingleCodeCouponUsingClientCredentials()
	{
		final Response result = validateCouponUsingClientCredentials("TEST_COUPON1");
		WebservicesAssert.assertResponse(Status.OK, Optional.empty(), result);
		final CouponValidationResponseWsDTO validateCouponResponseWsDTO = result.readEntity(CouponValidationResponseWsDTO.class);
		assertNotNull(validateCouponResponseWsDTO);
		assertTrue(validateCouponResponseWsDTO.getValid());
	}

	@Test
	public void testValidateSingleCodeCouponWhenCouponIsInvalid()
	{
		final Response result = validateCouponUsingClientCredentials("TEST_COUPON4");
		WebservicesAssert.assertResponse(Status.OK, Optional.empty(), result);
		final CouponValidationResponseWsDTO validateCouponResponseWsDTO = result.readEntity(CouponValidationResponseWsDTO.class);
		assertNotNull(validateCouponResponseWsDTO);
		assertFalse(validateCouponResponseWsDTO.getValid());
	}

	@Test
	public void testValidateSingleCodeCouponWithCustomerId()
	{
		final Response result = validateCouponWithCustomerIdUsingClientCredentials("TEST_COUPON1", "ppetersonson");
		WebservicesAssert.assertResponse(Status.OK, Optional.empty(), result);
		final CouponValidationResponseWsDTO validateCouponResponseWsDTO = result.readEntity(CouponValidationResponseWsDTO.class);
		assertNotNull(validateCouponResponseWsDTO);
		assertTrue(validateCouponResponseWsDTO.getValid());
	}

	@Test
	public void testValidateSingleCodeCouponWithCustomerIdWhenCouponIsNotValid()
	{
		final Response result = validateCouponWithCustomerIdUsingClientCredentials("TEST_COUPON2", "demo");
		WebservicesAssert.assertResponse(Status.OK, Optional.empty(), result);
		final CouponValidationResponseWsDTO validateCouponResponseWsDTO = result.readEntity(CouponValidationResponseWsDTO.class);
		assertNotNull(validateCouponResponseWsDTO);
		assertFalse(validateCouponResponseWsDTO.getValid());
	}


	@Test
	public void testValidateSingleCodeCouponWhenCouponCodeIsNotFound()
	{
		final Response result = validateCouponUsingClientCredentials("not_existing_coupon_code");
		WebservicesAssert.assertResponse(Status.NOT_FOUND, Optional.empty(), result);
		final ErrorListWsDTO errors = result.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(errors.getErrors().size(), 1);
		final ErrorWsDTO error = errors.getErrors().get(0);
		assertEquals("invalid", error.getReason());
		assertEquals("couponId", error.getSubject());
	}
}

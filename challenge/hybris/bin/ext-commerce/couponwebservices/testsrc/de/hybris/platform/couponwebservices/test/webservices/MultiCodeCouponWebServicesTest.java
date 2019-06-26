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
import de.hybris.platform.couponwebservices.dto.MultiCodeCouponWsDTO;
import de.hybris.platform.couponwebservices.dto.ws.MultiCodeCouponsSearchPageWsDTO;
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
public class MultiCodeCouponWebServicesTest extends AbstractCouponWebServicesTest
{
	private static final String BASE_URI = "couponservices/v2";
	private static final String URI = BASE_URI + "/multicodecoupon";

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
	}

	@Test
	public void testGetCouponsWithoutAuthorization()
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
	public void testGetCouponUsingClientCredentials()
	{
		final Response result = wsSecuredRequestBuilder//
				.path(URI)//
				.path("get")//
				.path("COUPON1")//
				.build()//
				.accept(MediaType.APPLICATION_XML)//
				.get();
		result.bufferEntity();
		WebservicesAssert.assertResponse(Status.OK, Optional.empty(), result);
	}

	@Test
	public void shouldReturnMultiCodeCoupons()
	{
		//given
		final Response result = wsSecuredRequestBuilder//
				.path(URI)//
				.path("list")//
				.build()//
				.accept(MediaType.APPLICATION_JSON)//
				.get();
		//when
		final MultiCodeCouponsSearchPageWsDTO multiCodeCouponsSearchPageWsDTO = result.readEntity(MultiCodeCouponsSearchPageWsDTO.class);
		//then
		WebservicesAssert.assertResponse(Status.OK, Optional.empty(), result);
		assertThat(multiCodeCouponsSearchPageWsDTO.getResults()).isNotEmpty();
	}

	@Test
	public void shouldReturnMultiCodeCouponsSortedByNameWithDescOrder()
	{
		//given
		final Response result = wsSecuredRequestBuilder//
				.path(URI)//
				.path("list")//
				.queryParam("sort","name:desc")
				.build()//
				.accept(MediaType.APPLICATION_JSON)//
				.get();
		//when
		final MultiCodeCouponsSearchPageWsDTO multiCodeCouponsSearchPageWsDTO = result.readEntity(MultiCodeCouponsSearchPageWsDTO.class);
		//then
		WebservicesAssert.assertResponse(Status.OK, Optional.empty(), result);
		final SortWsDTO sort = multiCodeCouponsSearchPageWsDTO.getSorts().iterator().next();
		assertSort(sort,"name",false);
	}


	@Test
	public void shouldReturnMultiCodeCouponsPaged()
	{
		//given
		final Response result = wsSecuredRequestBuilder//
				.path(URI)//
				.path("list")//
				.queryParam("currentPage",1).queryParam("pageSize",1)
				.build()//
				.accept(MediaType.APPLICATION_JSON)//
				.get();
		//when
		final MultiCodeCouponsSearchPageWsDTO multiCodeCouponsSearchPageWsDTO = result.readEntity(MultiCodeCouponsSearchPageWsDTO.class);
		//then
		WebservicesAssert.assertResponse(Status.OK, Optional.empty(), result);
		final PaginationWsDTO pagination = multiCodeCouponsSearchPageWsDTO.getPagination();
		assertThat(pagination.getPage()).isEqualTo(1);
		assertThat(pagination.getCount()).isEqualTo(1);
	}


	private void assertSort(final SortWsDTO sort,String code, boolean asc)
	{
		assertThat(sort.getCode()).isEqualTo(code);
		assertThat(sort.isAsc()).isEqualTo(asc);
	}

	@Test
	public void testPostMultiCodeCouponWsDTO()
	{
		final MultiCodeCouponWsDTO multiCodeCouponWsDTO = new MultiCodeCouponWsDTO();
		multiCodeCouponWsDTO.setCouponId("COUPON123");
		multiCodeCouponWsDTO.setCodeGenerationConfiguration("default-configuration");
		final Response result = wsSecuredRequestBuilder//
				.path(URI)//
				.path("create")//
				.build()//
				.post(Entity.entity(multiCodeCouponWsDTO, MediaType.APPLICATION_JSON));
		WebservicesAssert.assertResponse(Status.CREATED, Optional.empty(), result);
		final MultiCodeCouponWsDTO respMultiCodeCouponWsDTO = result.readEntity(MultiCodeCouponWsDTO.class);
		assertNotNull(respMultiCodeCouponWsDTO);
		assertEquals("COUPON123", respMultiCodeCouponWsDTO.getCouponId());
	}

	/**
	 * The test should detect the fail with 2 errors:</br>
	 * errors" : [ {</br>
	 * "message" : "This field is required.",</br>
	 * "reason" : "missing",</br>
	 * "subject" : "couponId",</br>
	 * "subjectType" : "parameter",</br>
	 * "type" : "ValidationError"</br>
	 * }, {</br>
	 * "message" : "This field is required.",</br>
	 * "reason" : "missing",</br>
	 * "subject" : "codeGenerationConfiguration",</br>
	 * "subjectType" : "parameter",</br>
	 * "type" : "ValidationError"</br>
	 * }
	 */
	@Test
	public void testPostEmptyMultiCodeCouponWsDTO()
	{
		final MultiCodeCouponWsDTO multiCodeCouponWsDTO = new MultiCodeCouponWsDTO();
		final Response response = wsSecuredRequestBuilder//
				.path(URI)//
				.path("create")//
				.build().post(Entity.entity(multiCodeCouponWsDTO, MediaType.APPLICATION_JSON));
		WebservicesAssert.assertResponse(Status.BAD_REQUEST, Optional.empty(), response);
		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(2, errors.getErrors().size());
		ErrorWsDTO error = errors.getErrors().get(0);
		assertEquals("missing", error.getReason());
		assertEquals("couponId", error.getSubject());
		assertEquals("parameter", error.getSubjectType());
		error = errors.getErrors().get(1);
		assertEquals("missing", error.getReason());
		assertEquals("codeGenerationConfiguration", error.getSubject());
		assertEquals("parameter", error.getSubjectType());
	}

	protected Response putMultiCodeCouponWsDTO(final String couponId, final String couponName, final String startDateVal,
			final String endDateVal,
			final String genConfigName)
	{
		final MultiCodeCouponWsDTO multiCodeCouponWsDTO = new MultiCodeCouponWsDTO();
		multiCodeCouponWsDTO.setCouponId(couponId);
		multiCodeCouponWsDTO.setName(couponName);
		multiCodeCouponWsDTO.setStartDate(startDateVal);
		multiCodeCouponWsDTO.setEndDate(endDateVal);
		multiCodeCouponWsDTO.setCodeGenerationConfiguration(genConfigName);
		final Response putResult = wsSecuredRequestBuilder//
				.path(URI)//
				.path("update")//
				.build()//
				.put(Entity.entity(multiCodeCouponWsDTO, MediaType.APPLICATION_JSON));
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
					.queryParam("fields", "FULL")//
					.build()//
					.accept(MediaType.APPLICATION_JSON)//
					.get();
			WebservicesAssert.assertResponse(Status.OK, Optional.empty(), getResult);
			final MultiCodeCouponWsDTO respMultiCodeCouponWsDTO = getResult.readEntity(MultiCodeCouponWsDTO.class);
			assertNotNull(respMultiCodeCouponWsDTO);
			assertEquals(couponId, respMultiCodeCouponWsDTO.getCouponId());
			assertEquals(startDateVal, respMultiCodeCouponWsDTO.getStartDate());
			assertEquals(endDateVal, respMultiCodeCouponWsDTO.getEndDate());
			assertEquals(genConfigName, respMultiCodeCouponWsDTO.getCodeGenerationConfiguration());
		}
		return putResult;
	}

	@Test
	public void testPutMultiCodeCouponWsDTO()
	{
		final Response putResult = putMultiCodeCouponWsDTO("COUPON4", "name", null, null, "optional-configuration");
		WebservicesAssert.assertResponse(Status.NO_CONTENT, Optional.empty(), putResult);
	}

	@Test
	public void testPutMultiCodeActiveCouponWsDTO()
	{
		final Response putResult = putMultiCodeCouponWsDTO("COUPON7", "name", null, null, "optional-configuration");
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
	public void testPutEmptyMultiCodeCouponWsDTO()
	{
		final Response putResult = putMultiCodeCouponWsDTO(null, "name", null, null, "optional-configuration");
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
	public void testPutInvalidMultiCodeCouponWsDTO()
	{
		final Response putResult = putMultiCodeCouponWsDTO("not_existing_coupon_code", "name", null, null,
				"optional-configuration");
		WebservicesAssert.assertResponse(Status.NOT_FOUND, Optional.empty(), putResult);
		final ErrorListWsDTO errors = putResult.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(errors.getErrors().size(), 1);
		final ErrorWsDTO error = errors.getErrors().get(0);
		assertEquals("invalid", error.getReason());
		assertEquals("couponId", error.getSubject());
	}

	public void testPutInvalidMultiCodeCouponWsDTO2()
	{
		final Response putResult = putMultiCodeCouponWsDTO("TEST_COUPON4", "name", null, null, "optional-configuration");
		WebservicesAssert.assertResponse(Status.NOT_FOUND, Optional.empty(), putResult);
		final ErrorListWsDTO errors = putResult.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(errors.getErrors().size(), 1);
		final ErrorWsDTO error = errors.getErrors().get(0);
		assertTrue(error.getMessage().contains("No multi-code coupon"));
	}

	@Test
	public void testPutInvalidConfigMultiCodeCouponWsDTO()
	{
		final Response putResult = putMultiCodeCouponWsDTO("COUPON4", "name", null, null, "invalid-configuration");
		WebservicesAssert.assertResponse(Status.NOT_FOUND, Optional.empty(), putResult);
		final ErrorListWsDTO errors = putResult.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(errors.getErrors().size(), 1);
		final ErrorWsDTO error = errors.getErrors().get(0);
		assertEquals("invalid", error.getReason());
		assertEquals("codeGenerationConfiguration", error.getSubject());
	}

	@Test
	public void testPutUsedConfigMultiCodeCouponWsDTO()
	{
		final Response putResult = putMultiCodeCouponWsDTO("COUPON8", "name", null, null, "default-configuration");
		WebservicesAssert.assertResponse(Status.BAD_REQUEST, Optional.empty(), putResult);
		final ErrorListWsDTO errors = putResult.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(errors.getErrors().size(), 1);
		final ErrorWsDTO error = errors.getErrors().get(0);
		assertEquals("invalid", error.getReason());
		assertEquals("generatedCodes", error.getSubject());
	}

	@Test
	public void testPutMultiCodeCouponWithDatesWsDTO()
	{
		final String startDate = LocalDateTime.now().minusDays(30).format(DateTimeFormatter.ISO_DATE_TIME);
		final String endDate = LocalDateTime.now().plusDays(60).format(DateTimeFormatter.ISO_DATE_TIME);

		final Response putResult = putMultiCodeCouponWsDTO("COUPON5", "name", startDate, endDate, "optional-configuration");
		WebservicesAssert.assertResponse(Status.NO_CONTENT, Optional.empty(), putResult);
	}

	@Test
	public void testPutMultiCodeCouponWithInconsistentDatesWsDTO()
	{
		final String startDate = LocalDateTime.now().plusDays(60).format(DateTimeFormatter.ISO_DATE_TIME);
		final String endDate = LocalDateTime.now().minusDays(30).format(DateTimeFormatter.ISO_DATE_TIME);

		final Response putResult = putMultiCodeCouponWsDTO("COUPON6", "name", startDate, endDate, "optional-configuration");

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
		final Response result = updateCouponStatusWithoutAuthorization("COUPON9", Boolean.FALSE);
		WebservicesAssert.assertResponse(Status.UNAUTHORIZED, Optional.empty(), result);
	}

	@Test
	public void testPutCouponStatusWsDTO1()
	{
		final Response putResult = updateCouponStatus("COUPON9", Boolean.FALSE);
		WebservicesAssert.assertResponse(Status.NO_CONTENT, Optional.empty(), putResult);
	}

	@Test
	public void testPutCouponStatusWsDTO2()
	{
		final Response putResult = updateCouponStatus("COUPON10", Boolean.TRUE);
		WebservicesAssert.assertResponse(Status.NO_CONTENT, Optional.empty(), putResult);
	}

	@Test
	public void testPutEmptyStatusCouponStatusWsDTO()
	{
		final Response putResult = updateCouponStatus("COUPON9", null);
		assertThat(putResult.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
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
	public void testValidateMultiCodeCouponWithoutAuthorization()
	{
		final Response result = validateCouponWithoutAuthorization("COUPON2");
		WebservicesAssert.assertResponse(Status.UNAUTHORIZED, Optional.empty(), result);
	}

	@Test
	public void testValidateMultiCodeCouponUsingClientCredentials()
	{
		final Response result = validateMultiCodeCouponUsingClientCredentials("COUPON3");
		WebservicesAssert.assertResponse(Status.OK, Optional.empty(), result);
	}

	@Test
	public void testValidateMultiCodeCouponWhenCouponIsNotActive()
	{
		final Response result = validateMultiCodeCouponUsingClientCredentials("COUPON1");
		WebservicesAssert.assertResponse(Status.OK, Optional.empty(), result);
		final CouponValidationResponseWsDTO validateCouponResponseWsDTO = result.readEntity(CouponValidationResponseWsDTO.class);
		assertNotNull(validateCouponResponseWsDTO);
		assertFalse(validateCouponResponseWsDTO.getValid());
	}

	@Test
	public void testValidateMultiCodeCouponWhenCouponIdIsUsed()
	{
		final Response result = validateCouponUsingClientCredentials("COUPON3");
		WebservicesAssert.assertResponse(Status.INTERNAL_SERVER_ERROR, Optional.empty(), result);
		final ErrorListWsDTO errors = result.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(errors.getErrors().size(), 1);
		final ErrorWsDTO error = errors.getErrors().get(0);
		assertEquals("invalid", error.getReason());
	}
}

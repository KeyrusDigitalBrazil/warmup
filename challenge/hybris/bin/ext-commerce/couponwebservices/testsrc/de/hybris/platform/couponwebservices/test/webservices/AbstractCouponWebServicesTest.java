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
import static org.junit.Assert.assertNotNull;

import de.hybris.platform.couponservices.model.AbstractCouponModel;
import de.hybris.platform.couponservices.model.MultiCodeCouponModel;
import de.hybris.platform.couponservices.services.CouponCodeGenerationService;
import de.hybris.platform.couponwebservices.constants.CouponwebservicesConstants;
import de.hybris.platform.couponwebservices.dto.CouponStatusWsDTO;
import de.hybris.platform.couponwebservices.dto.SingleCodeCouponWsDTO;
import de.hybris.platform.couponwebservices.util.CouponWsUtils;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert;
import de.hybris.platform.webservicescommons.testsupport.client.WsRequestBuilder;
import de.hybris.platform.webservicescommons.testsupport.client.WsSecuredRequestBuilder;

import java.util.Optional;

import javax.annotation.Resource;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Ignore;
import org.junit.Test;


@Ignore
public abstract class AbstractCouponWebServicesTest extends ServicelayerTest
{
	public static final String OAUTH_CLIENT_ID = "coupon_user";
	public static final String OAUTH_CLIENT_PASS = "secret";

	protected WsRequestBuilder wsRequestBuilder;
	protected WsSecuredRequestBuilder wsSecuredRequestBuilder;
	@Resource
	protected CouponWsUtils couponWsUtils;
	@Resource
	protected CouponCodeGenerationService couponCodeGenerationService;

	abstract String getUri();

	protected Response updateCouponStatus(final String couponId, final Boolean active)
	{
		final CouponStatusWsDTO couponStatusWsDTO = new CouponStatusWsDTO();
		couponStatusWsDTO.setCouponId(couponId);
		couponStatusWsDTO.setActive(active);
		final Response putResult = wsSecuredRequestBuilder//
				.path(getUri())//
				.path("update")//
				.path("status")//
				.build()//
				.put(Entity.entity(couponStatusWsDTO, MediaType.APPLICATION_JSON));
		putResult.bufferEntity();

		if (putResult.getStatus() == Status.NO_CONTENT.getStatusCode())
		{

			final Response getResult = new WsSecuredRequestBuilder()//
					.extensionName(CouponwebservicesConstants.EXTENSIONNAME)//
					.client(OAUTH_CLIENT_ID, OAUTH_CLIENT_PASS)//
					.grantClientCredentials()//
					.path(getUri())//
					.path("get")//
					.path(couponId)//
					.queryParam("fields", "FULL")//
					.build()//
					.accept(MediaType.APPLICATION_JSON)//
					.get();

			WebservicesAssert.assertResponse(Status.OK, Optional.empty(), getResult);
			final SingleCodeCouponWsDTO respSingleCodeCouponWsDTO = getResult.readEntity(SingleCodeCouponWsDTO.class);
			assertNotNull(respSingleCodeCouponWsDTO);
			assertEquals(couponId, respSingleCodeCouponWsDTO.getCouponId());
			assertEquals(active, respSingleCodeCouponWsDTO.getActive());
		}
		return putResult;
	}

	protected Response updateCouponStatusWithoutAuthorization(final String couponId, final Boolean active)
	{
		final CouponStatusWsDTO couponStatusWsDTO = new CouponStatusWsDTO();
		couponStatusWsDTO.setCouponId(couponId);
		couponStatusWsDTO.setActive(active);
		final Response putResult = wsRequestBuilder//
				.path(getUri())//
				.path("update")//
				.path("status")//
				.build()//
				.put(Entity.entity(couponStatusWsDTO, MediaType.APPLICATION_JSON));
		putResult.bufferEntity();

		if (putResult.getStatus() == Status.NO_CONTENT.getStatusCode())
		{


			final Response getResult = new WsSecuredRequestBuilder()//
					.extensionName(CouponwebservicesConstants.EXTENSIONNAME)//
					.client(OAUTH_CLIENT_ID, OAUTH_CLIENT_PASS)//
					.grantClientCredentials()//
					.path(getUri())//
					.path("get")//
					.path(couponId)//
					.queryParam("fields", "FULL")//
					.build()//
					.accept(MediaType.APPLICATION_JSON)//
					.get();

			WebservicesAssert.assertResponse(Status.OK, Optional.empty(), getResult);
			final SingleCodeCouponWsDTO respSingleCodeCouponWsDTO = getResult.readEntity(SingleCodeCouponWsDTO.class);
			assertNotNull(respSingleCodeCouponWsDTO);
			assertEquals(couponId, respSingleCodeCouponWsDTO.getCouponId());
			assertEquals(active, respSingleCodeCouponWsDTO.getActive());
		}
		return putResult;
	}

	@Test
	public void testPutEmptyCouponStatusWsDTO()
	{
		final Response putResult = updateCouponStatus(null, Boolean.TRUE);
		assertThat(putResult.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
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
	public void testPutInvalidCouponStatusWsDTO()
	{
		final Response putResult = updateCouponStatus("not_existing_coupon_code", Boolean.TRUE);
		assertThat(putResult.getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
		final ErrorListWsDTO errors = putResult.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(errors.getErrors().size(), 1);
		final ErrorWsDTO error = errors.getErrors().get(0);
		assertEquals("invalid", error.getReason());
		assertEquals("couponId", error.getSubject());
	}


	protected Response validateCouponWithoutAuthorization(final String couponId)
	{
		final Response result = wsRequestBuilder//
				.path(getUri())//
				.path("validate")//
				.path(couponId)//
				.build()//
				.accept(MediaType.APPLICATION_JSON)//
				.get();
		result.bufferEntity();
		return result;
	}


	protected Response validateMultiCodeCouponUsingClientCredentials(final String couponId)
	{
		final AbstractCouponModel couponModel = getCouponWsUtils().getCouponById(couponId);
		getCouponWsUtils().assertValidMultiCodeCoupon(couponModel, couponId);
		final String couponCode = getCouponCodeGenerationService().generateCouponCode((MultiCodeCouponModel) couponModel);

		final Response result = wsSecuredRequestBuilder//
				.path(getUri())//
				.path("validate")//
				.path(couponCode)//
				.build()//
				.accept(MediaType.APPLICATION_JSON)//
				.get();
		result.bufferEntity();
		return result;
	}

	protected Response validateCouponUsingClientCredentials(final String couponId)
	{
		final Response result = wsSecuredRequestBuilder//
				.path(getUri())//
				.path("validate")//
				.path(couponId)//
				.build()//
				.accept(MediaType.APPLICATION_JSON)//
				.get();
		result.bufferEntity();
		return result;
	}

	protected Response validateCouponWithCustomerIdUsingClientCredentials(final String couponId, final String customerId)
	{
		final Response result = wsSecuredRequestBuilder//
				.path(getUri())//
				.path("validate")//
				.path(couponId)//
				.queryParam("customerId", customerId).build()//
				.accept(MediaType.APPLICATION_JSON)//
				.get();
		result.bufferEntity();
		return result;
	}

	protected CouponWsUtils getCouponWsUtils()
	{
		return couponWsUtils;
	}

	protected CouponCodeGenerationService getCouponCodeGenerationService()
	{
		return couponCodeGenerationService;
	}
}

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
package de.hybris.platform.personalizationwebservices.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.personalizationfacades.customersegmentation.CustomerSegmentationFacade;
import de.hybris.platform.personalizationfacades.data.CustomerData;
import de.hybris.platform.personalizationfacades.data.CustomerSegmentationData;
import de.hybris.platform.personalizationfacades.data.SegmentData;
import de.hybris.platform.personalizationfacades.exceptions.AlreadyExistsException;
import de.hybris.platform.personalizationfacades.segmentation.SegmentationHelper;
import de.hybris.platform.personalizationwebservices.data.CustomerSegmentationListWsDTO;
import de.hybris.platform.personalizationwebservices.validator.SegmentationIdValidator;
import de.hybris.platform.webservicescommons.errors.exceptions.NotFoundException;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.pagination.WebPaginationUtils;

import java.math.BigDecimal;
import java.util.Collections;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;


@IntegrationTest
public class CustomerSegmentationControllerTest extends BaseControllerTest
{
	private static final String CUSTOMER = "customer1@hybris.com";
	private static final String NONEXISTING_CUSTOMER = "customer1000000@hybris.com";
	private static final String NOTRELATED_CUSTOMER = "customer4@hybris.com";
	private static final String SEGMENT = "segment1";
	private static final String NONEXISTING_SEGMENT = "segment10000";
	private static final String NOTRELATED_SEGMENT = "segment2";
	private static final String BASESITE = "testSite";
	private static final String NONEXISTING_BASESITE = "nonExistsBaseSite";
	private static final String NOTRELATED_BASESITE = "notRelatedBaseSite";
	private static final String DEFAULT_PROVIDER = "DEFAULT";
	private static final String CUSTOMER_WITH_SEGMENT_FOR_PROVIDER = "customer5@hybris.com";

	@Resource
	WebPaginationUtils webPaginationUtils;
	private String SEGMENTATION;
	private String NONEXISTING_SEGMENTATION;
	private CustomerSegmentationController controller;
	@Resource
	private CustomerSegmentationFacade cxCustomerSegmentationFacade;
	@Resource
	private SegmentationHelper cxSegmentationHelper;

	@Before
	public void setup()
	{
		final SegmentationIdValidator idValidator = new SegmentationIdValidator(cxSegmentationHelper);

		controller = new CustomerSegmentationController(cxCustomerSegmentationFacade, idValidator);
		controller.setWebPaginationUtils(webPaginationUtils);

		SEGMENTATION = cxSegmentationHelper.getSegmentationCode(SEGMENT, CUSTOMER, BASESITE, null);
		NONEXISTING_SEGMENTATION = cxSegmentationHelper.getSegmentationCode(NOTRELATED_SEGMENT, NOTRELATED_CUSTOMER,
				NOTRELATED_BASESITE, null);
	}

	@Test(expected = WebserviceValidationException.class)
	public void getAllCustomerSegmentationNullParamsTest()
	{
		// when
		controller.getCustomerSegmentations(null, null, null, Collections.emptyMap());
	}

	@Test
	public void getCustomerSegmentationFromCustomerTest()
	{
		// when
		final CustomerSegmentationListWsDTO customerSegmentations = controller.getCustomerSegmentations(CUSTOMER, null, null,
				Collections.emptyMap());

		// then
		assertNotNull(customerSegmentations);
		assertNotNull(customerSegmentations.getCustomerSegmentations());
		assertEquals(4, customerSegmentations.getCustomerSegmentations().size());
	}

	@Test
	public void getCustomerSegmentationFromCustomerNoneExistingBaseSiteTest()
	{
		// when
		final CustomerSegmentationListWsDTO customerSegmentations = controller.getCustomerSegmentations(CUSTOMER, null,
				NONEXISTING_BASESITE, Collections.emptyMap());

		// then
		assertNotNull(customerSegmentations);
		assertNotNull(customerSegmentations.getCustomerSegmentations());
		assertEquals(0, customerSegmentations.getCustomerSegmentations().size());
	}

	@Test
	public void getCustomerSegmentationFromNonexistingCustomerTest()
	{
		// when
		final CustomerSegmentationListWsDTO customerSegmentations = controller.getCustomerSegmentations(NONEXISTING_CUSTOMER, null,
				null, Collections.emptyMap());

		// then
		assertNotNull(customerSegmentations);
		assertNotNull(customerSegmentations.getCustomerSegmentations());
		assertEquals(0, customerSegmentations.getCustomerSegmentations().size());
	}

	@Test
	public void getCustomerSegmentationFromSegmentTest()
	{
		// when
		final CustomerSegmentationListWsDTO customerSegmentations = controller.getCustomerSegmentations(null, SEGMENT, null,
				Collections.emptyMap());

		// then
		assertNotNull(customerSegmentations);
		assertNotNull(customerSegmentations.getCustomerSegmentations());
		assertEquals(4, customerSegmentations.getCustomerSegmentations().size());
	}

	@Test
	public void getCustomerSegmentationFromSegmentNoneExistingBaseSiteTest()
	{
		// when
		final CustomerSegmentationListWsDTO customerSegmentations = controller.getCustomerSegmentations(null, SEGMENT,
				NONEXISTING_BASESITE, Collections.emptyMap());

		// then
		assertNotNull(customerSegmentations);
		assertNotNull(customerSegmentations.getCustomerSegmentations());
		assertEquals(0, customerSegmentations.getCustomerSegmentations().size());
	}

	@Test
	public void getCustomerSegmentationFromNonexistingSegmentTest()
	{
		// when
		final CustomerSegmentationListWsDTO customerSegmentations = controller.getCustomerSegmentations(null, NONEXISTING_SEGMENT,
				null, Collections.emptyMap());

		// then
		assertNotNull(customerSegmentations);
		assertNotNull(customerSegmentations.getCustomerSegmentations());
		assertEquals(0, customerSegmentations.getCustomerSegmentations().size());
	}

	@Test
	public void getCustomerSegmentationFromExisitngCustomerNonexistingSegmentTest()
	{
		// when
		final CustomerSegmentationListWsDTO customerSegmentations = controller.getCustomerSegmentations(CUSTOMER,
				NONEXISTING_SEGMENT, null, Collections.emptyMap());

		// then
		assertNotNull(customerSegmentations);
		assertNotNull(customerSegmentations.getCustomerSegmentations());
		assertEquals(0, customerSegmentations.getCustomerSegmentations().size());
	}

	@Test
	public void getCustomerSegmentationFromExisitngCustomerExistingSegmentNoneExistingBaseSiteTest()
	{
		// when
		final CustomerSegmentationListWsDTO customerSegmentations = controller.getCustomerSegmentations(CUSTOMER, SEGMENT,
				NONEXISTING_BASESITE, Collections.emptyMap());

		// then
		assertNotNull(customerSegmentations);
		assertNotNull(customerSegmentations.getCustomerSegmentations());
		assertEquals(0, customerSegmentations.getCustomerSegmentations().size());
	}

	@Test
	public void getCustomerSegmentationFromNonexisitngCustomerExistingSegmentTest()
	{
		// when
		final CustomerSegmentationListWsDTO customerSegmentations = controller.getCustomerSegmentations(NONEXISTING_CUSTOMER,
				SEGMENT, null, Collections.emptyMap());

		// then
		assertNotNull(customerSegmentations);
		assertNotNull(customerSegmentations.getCustomerSegmentations());
		assertEquals(0, customerSegmentations.getCustomerSegmentations().size());
	}

	@Test
	public void getCustomerSegmentationFromNonexisitngCustomerNonExistingSegmentTest()
	{
		// when
		final CustomerSegmentationListWsDTO customerSegmentations = controller.getCustomerSegmentations(NONEXISTING_CUSTOMER,
				NONEXISTING_SEGMENT, null, Collections.emptyMap());

		// then
		assertNotNull(customerSegmentations);
		assertNotNull(customerSegmentations.getCustomerSegmentations());
		assertEquals(0, customerSegmentations.getCustomerSegmentations().size());
	}

	@Test
	public void getCustomerSegmentationFromExistingCustomerAndExistingSegmentTest()
	{
		// when
		final CustomerSegmentationListWsDTO customerSegmentations = controller.getCustomerSegmentations(CUSTOMER, SEGMENT, null,
				Collections.emptyMap());

		// then
		assertNotNull(customerSegmentations);
		assertNotNull(customerSegmentations.getCustomerSegmentations());
		assertEquals(1, customerSegmentations.getCustomerSegmentations().size());
	}

	@Test
	public void getCustomerSegmentationByIdTest()
	{
		// when
		final CustomerSegmentationData segmentation = controller.getCustomerSegmentation(SEGMENTATION);

		// then
		assertNotNull(segmentation);
		assertEquals(SEGMENTATION, segmentation.getCode());
		assertEquals(BigDecimal.ONE, segmentation.getAffinity());
		assertNotNull(segmentation.getSegment());
		assertEquals(SEGMENT, segmentation.getSegment().getCode());
		assertNotNull(segmentation.getCustomer());
		assertEquals(CUSTOMER, segmentation.getCustomer().getUid());
	}

	@Test(expected = NotFoundException.class)
	public void getCustomerSegmentationByNonexistingIdTest()
	{
		// when
		controller.getCustomerSegmentation(NONEXISTING_SEGMENTATION);
	}

	@Test
	public void createCustomerSegmentationWithAffinityTest()
	{
		// given
		final CustomerSegmentationData dto = createSegmentationDTO(NOTRELATED_CUSTOMER, NOTRELATED_SEGMENT, NOTRELATED_BASESITE,
				"1.9");
		final String id = NONEXISTING_SEGMENTATION;

		// when
		final ResponseEntity<CustomerSegmentationData> response = controller.create(dto, getUriComponentsBuilder());

		// then
		assertLocation(VERSION + "/customersegmentations/" + id, response);
		final CustomerSegmentationData body = response.getBody();
		assertNotNull(body);
		assertEquals(id, body.getCode());
		assertEquals(dto.getAffinity(), body.getAffinity());
		assertEquals(dto.getBaseSite(), body.getBaseSite());

		final CustomerSegmentationData segmentation = controller.getCustomerSegmentation(id);
		assertNotNull(segmentation);
		assertEquals(id, segmentation.getCode());
		assertEquals(NOTRELATED_CUSTOMER, segmentation.getCustomer().getUid());
		assertEquals(NOTRELATED_SEGMENT, segmentation.getSegment().getCode());
		assertEquals(NOTRELATED_BASESITE, segmentation.getBaseSite());
	}

	@Test
	public void createCustomerSegmentationWithDifferentBasesiteTest()
	{
		// given
		final CustomerSegmentationData dto = createSegmentationDTO(CUSTOMER, SEGMENT, NOTRELATED_BASESITE, "0.9");
		final String id = cxSegmentationHelper.getSegmentationCode(SEGMENT, CUSTOMER, NOTRELATED_BASESITE);

		// when
		final ResponseEntity<CustomerSegmentationData> response = controller.create(dto, getUriComponentsBuilder());

		// then
		assertLocation(VERSION + "/customersegmentations/" + id, response);
		final CustomerSegmentationData body = response.getBody();
		assertNotNull(body);
		assertEquals(id, body.getCode());
		assertEquals(dto.getAffinity(), body.getAffinity());
		assertEquals(dto.getBaseSite(), body.getBaseSite());

		final CustomerSegmentationData segmentation = controller.getCustomerSegmentation(id);
		assertNotNull(segmentation);
		assertEquals(id, segmentation.getCode());
		assertEquals(CUSTOMER, segmentation.getCustomer().getUid());
		assertEquals(SEGMENT, segmentation.getSegment().getCode());
		assertEquals(NOTRELATED_BASESITE, segmentation.getBaseSite());
	}

	@Test
	public void createCustomerSegmentationWithNoBasesiteTest()
	{
		// given
		final CustomerSegmentationData dto = createSegmentationDTO(CUSTOMER, SEGMENT, null, "0.9");
		final String id = cxSegmentationHelper.getSegmentationCode(SEGMENT, CUSTOMER, null, null);

		// when
		final ResponseEntity<CustomerSegmentationData> response = controller.create(dto, getUriComponentsBuilder());

		// then
		assertLocation(VERSION + "/customersegmentations/" + id, response);
		final CustomerSegmentationData body = response.getBody();
		assertNotNull(body);
		assertEquals(id, body.getCode());
		assertEquals(dto.getAffinity(), body.getAffinity());
		assertEquals(dto.getBaseSite(), body.getBaseSite());

		final CustomerSegmentationData segmentation = controller.getCustomerSegmentation(id);
		assertNotNull(segmentation);
		assertEquals(id, segmentation.getCode());
		assertEquals(CUSTOMER, segmentation.getCustomer().getUid());
		assertEquals(SEGMENT, segmentation.getSegment().getCode());
		assertEquals(null, segmentation.getBaseSite());
	}

	@Test(expected = WebserviceValidationException.class)
	public void createCustomerSegmentationNoAffinityTest()
	{
		// given
		final CustomerSegmentationData dto = createSegmentationDTO(NOTRELATED_CUSTOMER, NOTRELATED_SEGMENT, null, null);

		// when
		controller.create(dto, getUriComponentsBuilder());
	}

	@Test(expected = NotFoundException.class)
	public void createCustomerSegmentationForNonexistingCustomerTest()
	{
		// given
		final CustomerSegmentationData dto = createSegmentationDTO(NONEXISTING_CUSTOMER, NOTRELATED_SEGMENT, null, "1");

		// when
		controller.create(dto, getUriComponentsBuilder());
	}

	@Test(expected = NotFoundException.class)
	public void createCustomerSegmentationForNonexistingSegmentTest()
	{
		// given
		final CustomerSegmentationData dto = createSegmentationDTO(NOTRELATED_CUSTOMER, NONEXISTING_SEGMENT, null, "1");

		// when
		controller.create(dto, getUriComponentsBuilder());
	}

	@Test(expected = AlreadyExistsException.class)
	public void createAlreadyExistingCustomerSegmentationTest()
	{
		// given
		final CustomerSegmentationData dto = createSegmentationDTO(CUSTOMER, SEGMENT, BASESITE, "0.9");

		// when
		controller.create(dto, getUriComponentsBuilder());
	}

	@Test
	public void createCustomerSegmentationWithProviderTest()
	{
		// given
		final CustomerSegmentationData dto = createSegmentationDTO(CUSTOMER, SEGMENT, BASESITE, "1.9", DEFAULT_PROVIDER);
		final String id = cxSegmentationHelper.getSegmentationCode(SEGMENT, CUSTOMER, BASESITE, DEFAULT_PROVIDER);

		// when
		final ResponseEntity<CustomerSegmentationData> response = controller.create(dto, getUriComponentsBuilder());

		// then
		assertLocation(VERSION + "/customersegmentations/" + id, response);
		final CustomerSegmentationData body = response.getBody();
		assertNotNull(body);
		assertEquals(id, body.getCode());
		assertEquals(dto.getAffinity(), body.getAffinity());
		assertEquals(dto.getBaseSite(), body.getBaseSite());
		assertEquals(dto.getProvider(), body.getProvider());

		final CustomerSegmentationData segmentation = controller.getCustomerSegmentation(id);
		assertNotNull(segmentation);
		assertEquals(id, segmentation.getCode());
		assertEquals(CUSTOMER, segmentation.getCustomer().getUid());
		assertEquals(SEGMENT, segmentation.getSegment().getCode());
		assertEquals(BASESITE, segmentation.getBaseSite());
		assertEquals(DEFAULT_PROVIDER, segmentation.getProvider());
	}

	@Test
	public void createCustomerSegmentationForDifferentProvidersTest()
	{
		// given
		final String providerId = "differentProvider";
		final CustomerSegmentationData dto = createSegmentationDTO(CUSTOMER_WITH_SEGMENT_FOR_PROVIDER, SEGMENT, BASESITE, "1.9",
				providerId);
		final String id = cxSegmentationHelper.getSegmentationCode(SEGMENT, CUSTOMER_WITH_SEGMENT_FOR_PROVIDER, BASESITE,
				providerId);

		// when
		final ResponseEntity<CustomerSegmentationData> response = controller.create(dto, getUriComponentsBuilder());

		// then
		assertLocation(VERSION + "/customersegmentations/" + id, response);
		final CustomerSegmentationData body = response.getBody();
		assertNotNull(body);
		assertEquals(id, body.getCode());
		assertEquals(dto.getAffinity(), body.getAffinity());
		assertEquals(dto.getBaseSite(), body.getBaseSite());
		assertEquals(dto.getProvider(), body.getProvider());

		final CustomerSegmentationData segmentation = controller.getCustomerSegmentation(id);
		assertNotNull(segmentation);
		assertEquals(id, segmentation.getCode());
		assertEquals(CUSTOMER_WITH_SEGMENT_FOR_PROVIDER, segmentation.getCustomer().getUid());
		assertEquals(SEGMENT, segmentation.getSegment().getCode());
		assertEquals(BASESITE, segmentation.getBaseSite());
		assertEquals(providerId, segmentation.getProvider());
	}


	@Test
	public void updateCustomerSegmentationTest()
	{
		// given
		final CustomerSegmentationData dto = createSegmentationDTO(CUSTOMER, SEGMENT, null, "1.5");
		final String id = SEGMENTATION;

		// when
		final CustomerSegmentationData update = controller.update(id, dto);

		// then
		assertNotNull(update);
		assertEquals(id, update.getCode());
		assertEquals(new BigDecimal("1.5"), update.getAffinity());

		final CustomerSegmentationData segmentation = controller.getCustomerSegmentation(id);
		assertNotNull(segmentation);
		assertEquals(id, segmentation.getCode());
		assertEquals(new BigDecimal("1.5"), segmentation.getAffinity());
	}

	@Test(expected = WebserviceValidationException.class)
	public void updateCustomerSegmentationWithInvalidAffinityTest()
	{
		// given
		final CustomerSegmentationData dto = createSegmentationDTO(CUSTOMER, SEGMENT, null, "-0.5");
		final String id = SEGMENTATION;

		// when
		controller.update(id, dto);
	}

	@Test(expected = NotFoundException.class)
	public void updateNonexistingCustomerSegmentationTest()
	{
		// given
		final CustomerSegmentationData dto = createSegmentationDTO(NOTRELATED_CUSTOMER, NOTRELATED_SEGMENT, null, "0.5");
		final String id = NONEXISTING_SEGMENTATION;

		// when
		controller.update(id, dto);
	}

	@Test
	public void deleteCustomerSegmentationTest()
	{
		// when
		controller.delete(SEGMENTATION);

		// then
		try
		{
			controller.getCustomerSegmentation(SEGMENTATION);
			fail();
		}
		catch (final NotFoundException e)
		{
			// OK
		}
	}

	@Test(expected = NotFoundException.class)
	public void deleteNonexistingCustomerSegmentationTest()
	{
		// when
		controller.delete(NONEXISTING_SEGMENTATION);
	}

	private CustomerSegmentationData createSegmentationDTO(final String customerCode, final String segmentCode,
			final String baseSite, final String affinity)
	{
		final CustomerSegmentationData dto = new CustomerSegmentationData();
		dto.setCustomer(new CustomerData());
		dto.setSegment(new SegmentData());
		dto.getCustomer().setUid(customerCode);
		dto.getSegment().setCode(segmentCode);
		if (baseSite != null)
		{
			dto.setBaseSite(baseSite);
		}
		if (affinity != null)
		{
			dto.setAffinity(new BigDecimal(affinity));
		}
		return dto;
	}

	private CustomerSegmentationData createSegmentationDTO(final String customerCode, final String segmentCode,
			final String baseSite, final String affinity, final String provider)
	{
		final CustomerSegmentationData dto = createSegmentationDTO(customerCode, segmentCode, baseSite, affinity);
		dto.setProvider(provider);
		return dto;
	}

}

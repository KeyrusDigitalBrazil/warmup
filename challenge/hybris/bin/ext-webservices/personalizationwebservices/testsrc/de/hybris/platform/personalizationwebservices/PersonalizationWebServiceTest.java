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
package de.hybris.platform.personalizationwebservices;

import static de.hybris.platform.personalizationfacades.customization.CustomizationTestUtils.assertVariationsEquals;
import static de.hybris.platform.personalizationfacades.customization.CustomizationTestUtils.creteCustomizationData;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.personalizationfacades.data.CustomizationData;
import de.hybris.platform.personalizationfacades.data.DefaultTriggerData;
import de.hybris.platform.personalizationfacades.data.ExpressionTriggerData;
import de.hybris.platform.personalizationfacades.data.GroupExpressionData;
import de.hybris.platform.personalizationfacades.data.NegationExpressionData;
import de.hybris.platform.personalizationfacades.data.SegmentData;
import de.hybris.platform.personalizationfacades.data.SegmentExpressionData;
import de.hybris.platform.personalizationfacades.data.SegmentTriggerData;
import de.hybris.platform.personalizationfacades.data.VariationData;
import de.hybris.platform.personalizationfacades.enums.ItemStatus;
import de.hybris.platform.personalizationservices.enums.CxGroupingOperator;
import de.hybris.platform.personalizationservices.enums.CxItemStatus;
import de.hybris.platform.personalizationwebservices.constants.PersonalizationwebservicesConstants;
import de.hybris.platform.personalizationwebservices.data.CustomerSegmentationListWsDTO;
import de.hybris.platform.personalizationwebservices.data.CustomizationListWsDTO;
import de.hybris.platform.personalizationwebservices.data.SegmentListWsDTO;
import de.hybris.platform.personalizationwebservices.data.TriggerListWsDTO;
import de.hybris.platform.personalizationwebservices.data.VariationListWsDTO;
import de.hybris.platform.webservicescommons.constants.WebservicescommonsConstants;
import de.hybris.platform.webservicescommons.dto.PaginationWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Sets;


@IntegrationTest
@NeedsEmbeddedServer(webExtensions =
{ PersonalizationwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
public class PersonalizationWebServiceTest extends BaseWebServiceTest
{
	private static final String SEGMENT_ENDPOINT = VERSION + "/segments";
	private static final String CUSTOMIZATION_ENDPOINT = VERSION + "/catalogs/testCatalog/catalogVersions/Online/customizations";
	private static final String INVALID_CUSTOMIZATION_ENDPOINT = VERSION
			+ "/catalogs/missingCatalog/catalogVersions/Online/customizations";
	private static final String CUSTOMIZATIONPACKAGE_ENDPOINT = VERSION
			+ "/catalogs/testCatalog/catalogVersions/Online/customizationpackages";

	private static final String VARIATION_ENDPOINT = "variations";
	private static final String CUSTOMERSEGMENTATION_ENDPOINT = VERSION + "/customersegmentations";

	private static final String SEGMENT = "segment1";
	private static final String CUSTOMIZATION = "customization1";
	private static final String CUSTOMIZATION_2 = "customization2";
	private static final String CUSTOMIZATION_NAME = "customization1";
	private static final String NEW_CUSTOMIZATION = "newCustomization";
	private static final String NEW_CUSTOMIZATION_NAME = "newCustomizationName";
	private static final String VARIATION = "variation1";
	private static final String DEFAULT_VARIATION = "defaultVariation";
	private static final String VARIATION_WITHOUT_TRIGGER = "variation10";
	private static final String NEW_VARIATION = "newVariation";
	private static final String NEW_VARIATION_NAME = "newVariationName";
	private static final String NEW_TRIGGER = "newTrigger";
	private static final String DEFAULT_TRIGGER = "defaultTrigger";
	private static final String SEGMENT_WITH_NO_REL_TRIGGER = "segment5";

	@Test
	public void getAllSegmentsForCmsManager() throws IOException
	{
		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//.
				.path(SEGMENT_ENDPOINT)//
				.build()//
				.get();

		//then
		WebservicesAssert.assertResponse(Status.OK, response);

		final SegmentListWsDTO segments = response.readEntity(SegmentListWsDTO.class);
		assertNotNull(segments.getSegments());
		assertEquals(5, segments.getSegments().size());

		final SegmentData segment = segments.getSegments().get(0);
		assertNotNull(segment);
		assertNotNull(segment.getCode());
	}

	@Test
	public void getSegmentsByCode() throws IOException
	{
		//when
		final Response response = getWsSecuredRequestBuilderForAdmin()//
				.path(SEGMENT_ENDPOINT)//
				.queryParam("code", "1").build()//
				.get();

		//then
		WebservicesAssert.assertResponse(Status.OK, response);

		final SegmentListWsDTO segments = response.readEntity(SegmentListWsDTO.class);
		assertNotNull(segments.getSegments());
		assertEquals(1, segments.getSegments().size());

		final SegmentData segment = segments.getSegments().get(0);
		assertNotNull(segment);
		assertNotNull(segment.getCode());
	}

	@Test
	public void getAllSegmentsForAdmin() throws IOException
	{
		//when
		final Response response = getWsSecuredRequestBuilderForAdmin()//
				.path(SEGMENT_ENDPOINT)//
				.build()//
				.get();

		//then
		WebservicesAssert.assertResponse(Status.OK, response);
		final SegmentListWsDTO segments = response.readEntity(SegmentListWsDTO.class);
		assertNotNull(segments.getSegments());
		assertEquals(5, segments.getSegments().size());

		final SegmentData segment = segments.getSegments().get(0);
		assertNotNull(segment);
		assertNotNull(segment.getCode());
	}


	@Test
	public void getAllSegmentsWithoutAuthorization() throws IOException
	{
		//when
		final Response response = getWsRequestBuilder()//
				.path(SEGMENT_ENDPOINT)//
				.build()//
				.get();

		//then
		WebservicesAssert.assertResponse(Status.UNAUTHORIZED, response);
		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(1, errors.getErrors().size());
		final ErrorWsDTO error1 = errors.getErrors().get(0);
		assertEquals("UnauthorizedError", error1.getType());
	}


	@Test
	public void getCustomerSegmentation() throws IOException
	{
		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMERSEGMENTATION_ENDPOINT)//
				.queryParam("segmentId", SEGMENT)//
				.build()//
				.get();

		//then
		WebservicesAssert.assertResponse(Status.OK, response);

		final CustomerSegmentationListWsDTO segmentation = response.readEntity(CustomerSegmentationListWsDTO.class);
		assertNotNull(segmentation);
		assertNotNull(segmentation.getCustomerSegmentations());
		assertEquals(4, segmentation.getCustomerSegmentations().size());
	}

	@Test
	public void getCustomerSegmentationsWithPagination() throws IOException
	{
		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMERSEGMENTATION_ENDPOINT)//
				.queryParam("segmentId", SEGMENT)//
				.queryParam(WebservicescommonsConstants.PAGE_SIZE, Integer.valueOf(3))//
				.queryParam(WebservicescommonsConstants.CURRENT_PAGE, Integer.valueOf(1))//
				.build()//
				.get();

		//then
		WebservicesAssert.assertResponse(Status.OK, response);

		final CustomerSegmentationListWsDTO result = response.readEntity(CustomerSegmentationListWsDTO.class);
		assertNotNull(result);
		assertNotNull(result.getCustomerSegmentations());
		assertNotNull(result.getPagination());

		final PaginationWsDTO pagination = result.getPagination();

		assertEquals(Integer.valueOf(1), pagination.getCount());
		assertEquals(Integer.valueOf(1), pagination.getPage());
		assertEquals(Long.valueOf(4), pagination.getTotalCount());
		assertEquals(Integer.valueOf(2), pagination.getTotalPages());
	}

	@Test
	public void getCustomerSegmentationShouldReturn400WithLocalizedErrorMessageInGerman() throws IOException
	{
		getCustomerSegmentationShouldReturn400WithLocalizedErrorMessage(Locale.GERMAN,
				"Mindestens ein Parameter ist erforderlich: customerId, segmentId");
	}

	@Test
	public void getCustomerSegmentationShouldReturn400WithLocalizedErrorMessageInEnglish() throws IOException
	{
		getCustomerSegmentationShouldReturn400WithLocalizedErrorMessage(null,
				"At least one of parameters  is required: customerId, segmentId");
	}


	@Test
	public void deleteSegmentWithoutRelTrigger() throws IOException
	{
		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(SEGMENT_ENDPOINT)//
				.path(SEGMENT_WITH_NO_REL_TRIGGER)//
				.build()//
				.delete();

		//then
		WebservicesAssert.assertResponse(Status.NO_CONTENT, response);
	}

	@Test
	public void deleteSegmentWithRelTrigger() throws IOException
	{
		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(SEGMENT_ENDPOINT)//
				.path(SEGMENT)//
				.build()//
				.delete();

		//then
		WebservicesAssert.assertResponse(Status.BAD_REQUEST, response);
	}

	@Test
	public void getCustmizationById() throws IOException
	{
		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.build()//
				.get();

		//then
		WebservicesAssert.assertResponse(Status.OK, response);

		final CustomizationData customization = response.readEntity(CustomizationData.class);
		assertNotNull(customization);
		assertEquals(CUSTOMIZATION, customization.getCode());
		assertEquals(CUSTOMIZATION, customization.getName());
	}

	@Test
	public void getAllCustmization() throws IOException
	{
		//having
		final Set<String> statuses = Sets.newHashSet(CxItemStatus.ENABLED.getCode(), CxItemStatus.DISABLED.getCode(),
				CxItemStatus.DELETED.getCode());
		final String statusesParam = statuses.stream().collect(Collectors.joining(", "));
		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.queryParam("statuses", statusesParam).build()//
				.get();

		//then
		WebservicesAssert.assertResponse(Status.OK, response);

		final CustomizationListWsDTO customizations = response.readEntity(CustomizationListWsDTO.class);
		assertNotNull(customizations);
		assertNotNull(customizations.getCustomizations());
		assertEquals(5, customizations.getCustomizations().size());
	}

	@Test
	public void getEnabledAndDisabledCustmization() throws IOException
	{
		//having
		final Set<String> statuses = Sets.newHashSet(CxItemStatus.ENABLED.getCode(), CxItemStatus.DISABLED.getCode());
		final String statusesParam = statuses.stream().collect(Collectors.joining(", "));
		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.queryParam("statuses", statusesParam).build()//
				.get();

		//then
		WebservicesAssert.assertResponse(Status.OK, response);

		final CustomizationListWsDTO customizations = response.readEntity(CustomizationListWsDTO.class);
		assertNotNull(customizations);
		assertNotNull(customizations.getCustomizations());
		assertEquals(4, customizations.getCustomizations().size());
	}

	@Test
	public void getEnabledCustmization() throws IOException
	{
		//having
		final Set<String> statuses = Sets.newHashSet(CxItemStatus.ENABLED.getCode());
		final String statusesParam = statuses.stream().collect(Collectors.joining(", "));
		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.queryParam("statuses", statusesParam).build()//
				.get();

		//then
		WebservicesAssert.assertResponse(Status.OK, response);

		final CustomizationListWsDTO customizations = response.readEntity(CustomizationListWsDTO.class);
		assertNotNull(customizations);
		assertNotNull(customizations.getCustomizations());
		assertEquals(3, customizations.getCustomizations().size());
	}

	@Test
	public void getDisabledCustmization() throws IOException
	{
		//having
		final Set<String> statuses = Sets.newHashSet(CxItemStatus.DISABLED.getCode());
		final String statusesParam = statuses.stream().collect(Collectors.joining(", "));

		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.queryParam("statuses", statusesParam).build()//
				.get();

		//then
		WebservicesAssert.assertResponse(Status.OK, response);

		final CustomizationListWsDTO customizations = response.readEntity(CustomizationListWsDTO.class);
		assertNotNull(customizations);
		assertNotNull(customizations.getCustomizations());
		assertEquals(1, customizations.getCustomizations().size());
	}

	@Test
	public void getDeletedCustmization() throws IOException
	{
		//having
		final Set<String> statuses = Sets.newHashSet(CxItemStatus.DELETED.getCode());
		final String statusesParam = statuses.stream().collect(Collectors.joining(", "));

		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.queryParam("statuses", statusesParam).build()//
				.get();

		//then
		WebservicesAssert.assertResponse(Status.OK, response);

		final CustomizationListWsDTO customizations = response.readEntity(CustomizationListWsDTO.class);
		assertNotNull(customizations);
		assertNotNull(customizations.getCustomizations());
		assertEquals(1, customizations.getCustomizations().size());
	}

	@Test
	public void getCustmizationWithPagination() throws IOException
	{
		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.queryParam("code", CUSTOMIZATION.substring(1, 5))//
				.queryParam(WebservicescommonsConstants.PAGE_SIZE, Integer.valueOf(2))//
				.queryParam(WebservicescommonsConstants.CURRENT_PAGE, Integer.valueOf(1)).build()//
				.get();

		//then
		WebservicesAssert.assertResponse(Status.OK, response);

		final CustomizationListWsDTO result = response.readEntity(CustomizationListWsDTO.class);
		assertNotNull(result);
		assertNotNull(result.getCustomizations());
		assertNotNull(result.getPagination());

		final PaginationWsDTO pagination = result.getPagination();

		assertEquals(Integer.valueOf(2), pagination.getCount());
		assertEquals(Integer.valueOf(1), pagination.getPage());
		assertEquals(Long.valueOf(5), pagination.getTotalCount());
		assertEquals(Integer.valueOf(3), pagination.getTotalPages());
	}

	@Test
	public void getCustmizationByNameWithPagination() throws IOException
	{
		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.queryParam("name", CUSTOMIZATION.substring(0, 5))//
				.queryParam(WebservicescommonsConstants.PAGE_SIZE, Integer.valueOf(2))//
				.queryParam(WebservicescommonsConstants.CURRENT_PAGE, Integer.valueOf(1)).build()//
				.get();

		//then
		WebservicesAssert.assertResponse(Status.OK, response);

		final CustomizationListWsDTO result = response.readEntity(CustomizationListWsDTO.class);
		assertNotNull(result);
		assertNotNull(result.getCustomizations());
		assertNotNull(result.getPagination());

		final PaginationWsDTO pagination = result.getPagination();

		assertEquals(Integer.valueOf(2), pagination.getCount());
		assertEquals(Integer.valueOf(1), pagination.getPage());
		assertEquals(Long.valueOf(5), pagination.getTotalCount());
		assertEquals(Integer.valueOf(3), pagination.getTotalPages());
	}

	@Test
	public void createCustomizationWithCode() throws IOException, JAXBException
	{
		//given
		final Date data = new Date();
		final CustomizationData input = new CustomizationData();
		input.setCode(NEW_CUSTOMIZATION);
		input.setName(NEW_CUSTOMIZATION_NAME);
		input.setDescription("desc");
		input.setRank(Integer.valueOf(3));
		input.setEnabledStartDate(data);

		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.build()//
				.post(Entity.json(marshallDto(input, CustomizationData.class)));// entity(input, MediaType.APPLICATION_JSON));

		//then
		WebservicesAssert.assertResponse(Status.CREATED, response);

		final String location = response.getHeaderString("Location");
		assertTrue(location.contains(input.getCode()));

		final CustomizationData readEntity = unmarshallResult(response, CustomizationData.class);
		assertEquals(input.getCode(), readEntity.getCode());
		assertEquals(input.getName(), readEntity.getName());
		assertEquals(input.getRank(), readEntity.getRank());
		assertEquals(input.getDescription(), readEntity.getDescription());
		assertDateTimeAlmostEqual(input.getEnabledStartDate(), readEntity.getEnabledStartDate());



		final Response resultResponse = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(readEntity.getCode())//
				.build()//
				.get();

		final CustomizationData result = unmarshallResult(resultResponse, CustomizationData.class);

		assertEquals(input.getCode(), result.getCode());
		assertEquals(input.getName(), result.getName());
		assertEquals(input.getRank(), result.getRank());
		assertEquals(input.getDescription(), result.getDescription());
		assertDateTimeAlmostEqual(input.getEnabledStartDate(), result.getEnabledStartDate());
	}

	@Test
	public void createCustomization() throws IOException, JAXBException
	{
		//given
		final Date data = new Date();
		final CustomizationData input = new CustomizationData();
		input.setName(NEW_CUSTOMIZATION_NAME);
		input.setDescription("desc");
		input.setRank(Integer.valueOf(3));
		input.setEnabledStartDate(data);



		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.build()//
				.post(Entity.json(marshallDto(input, CustomizationData.class)));// entity(input, MediaType.APPLICATION_JSON));

		//then
		WebservicesAssert.assertResponse(Status.CREATED, response);
		final CustomizationData readEntity = unmarshallResult(response, CustomizationData.class);
		assertNotNull(readEntity.getCode());
		assertEquals(input.getName(), readEntity.getName());
		assertEquals(input.getRank(), readEntity.getRank());
		assertEquals(input.getDescription(), readEntity.getDescription());
		assertDateTimeAlmostEqual(input.getEnabledStartDate(), readEntity.getEnabledStartDate());


		final String location = response.getHeaderString("Location");
		assertTrue(location.contains(readEntity.getCode()));

		final Response resultResponse = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(readEntity.getCode())//
				.build()//
				.get();

		final CustomizationData result = unmarshallResult(resultResponse, CustomizationData.class);

		assertEquals(readEntity.getCode(), result.getCode());
		assertEquals(input.getName(), result.getName());
		assertEquals(input.getRank(), result.getRank());
		assertEquals(input.getDescription(), result.getDescription());
		assertDateTimeAlmostEqual(input.getEnabledStartDate(), result.getEnabledStartDate());
	}

	@Test
	public void updateCustomizationEnabledDates() throws IOException, JAXBException
	{
		//given
		final CustomizationData existing = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.build()//
				.get(CustomizationData.class);

		assertEquals(Boolean.TRUE, existing.getActive());

		final Date data = new Date(0L);
		existing.setEnabledStartDate(data);
		existing.setEnabledEndDate(data);

		//when
		final Response read = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION).build()//
				.put(Entity.json(marshallDto(existing, CustomizationData.class)));

		final CustomizationData readEntity = unmarshallResult(read, CustomizationData.class);

		//then
		assertEquals(existing.getCode(), readEntity.getCode());
		assertEquals(existing.getName(), readEntity.getName());
		assertEquals(existing.getRank(), readEntity.getRank());
		assertEquals(existing.getDescription(), readEntity.getDescription());
		assertDateTimeAlmostEqual(existing.getEnabledStartDate(), readEntity.getEnabledStartDate());
		assertDateTimeAlmostEqual(existing.getEnabledEndDate(), readEntity.getEnabledEndDate());
		assertEquals(Boolean.FALSE, readEntity.getActive());

		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.build()//
				.get();

		final CustomizationData result = unmarshallResult(response, CustomizationData.class);

		assertEquals(existing.getCode(), result.getCode());
		assertEquals(existing.getName(), result.getName());
		assertEquals(existing.getRank(), result.getRank());
		assertEquals(existing.getDescription(), result.getDescription());
		assertDateTimeAlmostEqual(existing.getEnabledStartDate(), result.getEnabledStartDate());
		assertDateTimeAlmostEqual(existing.getEnabledEndDate(), result.getEnabledEndDate());
		assertEquals(Boolean.FALSE, result.getActive());
	}

	@Test
	public void updateCustomizationStatus() throws IOException, JAXBException
	{
		//given
		final CustomizationData existing = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.build()//
				.get(CustomizationData.class);

		assertEquals(Boolean.TRUE, existing.getActive());

		existing.setStatus(ItemStatus.DISABLED);

		//when
		final Response read = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION).build()//
				.put(Entity.json(marshallDto(existing, CustomizationData.class)));

		final CustomizationData readEntity = unmarshallResult(read, CustomizationData.class);

		//then
		assertEquals(existing.getCode(), readEntity.getCode());
		assertEquals(existing.getName(), readEntity.getName());
		assertEquals(existing.getRank(), readEntity.getRank());
		assertEquals(existing.getDescription(), readEntity.getDescription());
		assertEquals(Boolean.FALSE, readEntity.getActive());
		assertEquals(existing.getStatus(), readEntity.getStatus());

		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.build()//
				.get();

		final CustomizationData result = unmarshallResult(response, CustomizationData.class);

		assertEquals(existing.getCode(), result.getCode());
		assertEquals(existing.getName(), result.getName());
		assertEquals(existing.getRank(), result.getRank());
		assertEquals(existing.getDescription(), result.getDescription());
		assertEquals(Boolean.FALSE, readEntity.getActive());
		assertEquals(existing.getStatus(), result.getStatus());

	}

	@Test
	public void getInvalidCatalog() throws IOException
	{
		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(INVALID_CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.build()//
				.get();

		//then
		WebservicesAssert.assertResponse(Status.FORBIDDEN, response);
	}



	@Test
	public void getVariations() throws IOException
	{
		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.build()//
				.get();

		//then
		WebservicesAssert.assertResponse(Status.OK, response);

		final VariationListWsDTO variations = response.readEntity(VariationListWsDTO.class);
		assertNotNull(variations);
		assertNotNull(variations.getVariations());
		assertEquals(11, variations.getVariations().size());
	}

	@Test
	public void updateVariation() throws IOException
	{
		//given
		final VariationData input = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.build()//
				.get(VariationData.class);
		input.setRank(Integer.valueOf(4));

		input.setName(NEW_VARIATION_NAME);

		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.build()//
				.put(Entity.entity(input, MediaType.APPLICATION_JSON));

		//then
		WebservicesAssert.assertResponse(Status.OK, response);
		final VariationData output = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.build()//
				.get(VariationData.class);

		assertEquals(input.getCode(), output.getCode());
		assertEquals(input.getName(), output.getName());
		assertEquals(input.getRank(), output.getRank());
	}

	@Test
	public void getTriggers() throws IOException
	{
		//when
		final TriggerListWsDTO list = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(TRIGGER_ENDPOINT)//
				.build()//
				.get(TriggerListWsDTO.class);

		//then
		assertNotNull(list.getTriggers());
		assertEquals(1, list.getTriggers().size());
	}

	@Test
	public void getSegmentTrigger() throws IOException, JAXBException
	{
		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(TRIGGER_ENDPOINT)//
				.path(TRIGGER).build()//
				.get();

		//then
		final SegmentTriggerData trigger = unmarshallResult(response, SegmentTriggerData.class);
		assertEquals(TRIGGER, trigger.getCode());
	}

	@Test
	public void getExpressionTriggerFull() throws IOException, JAXBException
	{
		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path("customization2")//
				.path(VARIATION_ENDPOINT)//
				.path("exvariation0")//
				.path(TRIGGER_ENDPOINT)//
				.path("exTrigger1").queryParam("fields", "FULL")//
				.build()//
				.get();

		final ExpressionTriggerData trigger = unmarshallResult(response, ExpressionTriggerData.class);

		//then
		assertEquals("exTrigger1", trigger.getCode());
		assertNotNull(trigger.getExpression());
		assertTrue(trigger.getExpression() instanceof GroupExpressionData);
		final GroupExpressionData groupExpression = (GroupExpressionData) trigger.getExpression();
		assertNotNull(groupExpression.getElements());
		assertEquals(Integer.valueOf(groupExpression.getElements().size()), Integer.valueOf(2));
		assertTrue(groupExpression.getElements().get(0) instanceof SegmentExpressionData);
		assertEquals(((SegmentExpressionData) groupExpression.getElements().get(0)).getCode(), "segment1");
		assertTrue(groupExpression.getElements().get(1) instanceof NegationExpressionData);
		final NegationExpressionData negationExpression = (NegationExpressionData) groupExpression.getElements().get(1);
		assertTrue(negationExpression.getElement() instanceof SegmentExpressionData);
		assertEquals(((SegmentExpressionData) negationExpression.getElement()).getCode(), "segment2");

	}

	@Test
	public void getDefaultTrigger() throws IOException, JAXBException
	{
		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION_2)//
				.path(VARIATION_ENDPOINT)//
				.path(DEFAULT_VARIATION)//
				.path(TRIGGER_ENDPOINT)//
				.path(DEFAULT_TRIGGER)//
				.queryParam("fields", "BASIC")//
				.build()//
				.get();

		//then
		final DefaultTriggerData trigger = unmarshallResult(response, DefaultTriggerData.class);
		assertEquals(DEFAULT_TRIGGER, trigger.getCode());
	}

	@Test
	public void createDefaultTrigger() throws IOException, JAXBException
	{
		//given
		final DefaultTriggerData triggerData = new DefaultTriggerData();
		triggerData.setCode(DEFAULT_TRIGGER);

		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION_WITHOUT_TRIGGER)//
				.path(TRIGGER_ENDPOINT)//
				.build()//
				.post(Entity.json(marshallDto(triggerData, DefaultTriggerData.class)));

		//then
		final DefaultTriggerData trigger = unmarshallResult(response, DefaultTriggerData.class);
		assertEquals(DEFAULT_TRIGGER, trigger.getCode());
	}

	@Test
	public void updateTrigger() throws IOException, JAXBException
	{
		final SegmentTriggerData input = new SegmentTriggerData();
		input.setGroupBy("AND");
		input.setSegments(new ArrayList<>());

		final SegmentData s1 = new SegmentData();
		s1.setCode("segment1");
		input.getSegments().add(s1);

		final SegmentData s2 = new SegmentData();
		s2.setCode("segment2");
		input.getSegments().add(s2);

		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(TRIGGER_ENDPOINT)//
				.path(TRIGGER)//
				.build()//
				.put(Entity.entity(marshallDto(input, SegmentTriggerData.class), MediaType.APPLICATION_JSON));

		//then
		WebservicesAssert.assertResponse(Status.OK, response);

		final SegmentTriggerData result = response.readEntity(SegmentTriggerData.class);

		//then
		assertNotNull(result);
		assertEquals(TRIGGER, result.getCode());
		assertEquals(input.getGroupBy(), result.getGroupBy());
		assertNotNull(result.getSegments());
		assertEquals(2, result.getSegments().size());
	}

	@Test
	public void updateExpressionTrigger() throws IOException, JAXBException
	{
		final SegmentExpressionData segment = new SegmentExpressionData();
		segment.setCode(SEGMENT);

		final ExpressionTriggerData trigger = new ExpressionTriggerData();
		trigger.setExpression(segment);


		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path("customization2")//
				.path(VARIATION_ENDPOINT)//
				.path("exvariation0")//
				.path(TRIGGER_ENDPOINT)//
				.path("exTrigger1")//
				.build()//
				.put(Entity.entity(marshallDto(trigger, ExpressionTriggerData.class), MediaType.APPLICATION_JSON));

		//then
		WebservicesAssert.assertResponse(Status.OK, response);

		final ExpressionTriggerData result = unmarshallResult(response, ExpressionTriggerData.class);

		//then
		assertNotNull(result);
		assertEquals("exTrigger1", result.getCode());
		assertNotNull(result.getExpression());
	}

	@Test
	public void createCustomizationPackage() throws IOException, JAXBException
	{
		//given
		final CustomizationData input = creteCustomizationData(NEW_CUSTOMIZATION, NEW_CUSTOMIZATION_NAME, NEW_VARIATION,
				NEW_VARIATION_NAME, () -> createSegmentTriggerData(NEW_TRIGGER, SEGMENT));
		input.setDescription("desc");
		input.setRank(Integer.valueOf(3));

		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATIONPACKAGE_ENDPOINT)//
				.build()//
				.post(Entity.entity(marshallDto(input, CustomizationData.class), MediaType.APPLICATION_JSON));

		//then
		WebservicesAssert.assertResponse(Status.CREATED, response);
		final String location = response.getHeaderString("Location");
		assertTrue(location.contains(NEW_CUSTOMIZATION));

		final CustomizationData result = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(NEW_CUSTOMIZATION)//
				.build()//
				.get(CustomizationData.class);

		assertEquals("Invalid customization code", input.getCode(), result.getCode());
		assertEquals("Invalid customization rank", input.getRank(), result.getRank());
		assertEquals("Invalid customization description", input.getDescription(), result.getDescription());

		final VariationListWsDTO variationListDTO = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(NEW_CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.queryParam("fields", "FULL").build()//
				.get(VariationListWsDTO.class);

		Assert.assertNotNull("Variations should not be null", variationListDTO.getVariations());
		Assert.assertEquals("Variations size should be 1", 1, variationListDTO.getVariations().size());
		assertVariationsEquals(input.getVariations().get(0), variationListDTO.getVariations().get(0), null);
	}

	@Test
	public void updateCustomizationPackage() throws IOException, JAXBException
	{
		//given
		final CustomizationData input = creteCustomizationData(CUSTOMIZATION, CUSTOMIZATION_NAME, NEW_VARIATION, NEW_VARIATION_NAME,
				() -> createSegmentTriggerData(NEW_TRIGGER, SEGMENT));
		input.setDescription("newDescription");
		input.setRank(Integer.valueOf(1));

		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATIONPACKAGE_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.build()//
				.put(Entity.entity(marshallDto(input, CustomizationData.class), MediaType.APPLICATION_JSON));

		//then
		WebservicesAssert.assertResponse(Status.OK, response);

		final CustomizationData result = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.build()//
				.get(CustomizationData.class);

		assertEquals("Invalid customization code", input.getCode(), result.getCode());
		assertEquals("Invalid customization rank", input.getRank(), result.getRank());
		assertEquals("Invalid customization description", input.getDescription(), result.getDescription());

		final VariationListWsDTO variationListDTO = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.queryParam("fields", "FULL").build()//
				.get(VariationListWsDTO.class);

		Assert.assertNotNull("Variations should not be null", variationListDTO.getVariations());
		Assert.assertEquals("Variations size should be 1", 1, variationListDTO.getVariations().size());
		assertVariationsEquals(input.getVariations().get(0), variationListDTO.getVariations().get(0), null);
	}

	protected SegmentTriggerData createSegmentTriggerData(final String triggerCode, final String segmentCode)
	{
		final SegmentTriggerData trigger = new SegmentTriggerData();
		trigger.setCode(triggerCode);
		trigger.setGroupBy(CxGroupingOperator.AND.getCode());
		if (segmentCode != null)
		{
			final SegmentData segment = new SegmentData();
			segment.setCode(segmentCode);
			trigger.setSegments(Collections.singletonList(segment));
		}
		return trigger;
	}

	protected void getCustomerSegmentationShouldReturn400WithLocalizedErrorMessage(final Locale language,
			final String errorMessage) throws IOException
	{
		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMERSEGMENTATION_ENDPOINT)//
				.build()//
				.accept(MediaType.APPLICATION_JSON)//
				.acceptLanguage(language)//
				.get();

		response.bufferEntity();

		//then
		WebservicesAssert.assertResponse(Status.BAD_REQUEST, response);
		final ErrorListWsDTO errorsList = response.readEntity(ErrorListWsDTO.class);
		Assert.assertNotNull(errorsList);
		Assert.assertEquals(1, errorsList.getErrors().size());
		Assert.assertEquals(errorMessage, errorsList.getErrors().get(0).getMessage());
	}

}

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

import static de.hybris.platform.personalizationfacades.customization.CustomizationTestUtils.creteCustomizationData;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.personalizationfacades.data.CustomizationData;
import de.hybris.platform.personalizationfacades.data.SegmentData;
import de.hybris.platform.personalizationfacades.data.SegmentTriggerData;
import de.hybris.platform.personalizationfacades.data.VariationData;
import de.hybris.platform.personalizationfacades.enums.ItemStatus;
import de.hybris.platform.personalizationservices.enums.CxGroupingOperator;
import de.hybris.platform.personalizationwebservices.constants.PersonalizationwebservicesConstants;
import de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBException;

import org.junit.Test;


@IntegrationTest
@NeedsEmbeddedServer(webExtensions =
{ PersonalizationwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
public class PersonalizationWebServiceSecurityTest extends BaseWebServiceTest
{
	private static final String SEGMENT_ENDPOINT = VERSION + "/segments";
	private static final String CUSTOMIZATION_ENDPOINT = VERSION + "/catalogs/testCatalog/catalogVersions/Online/customizations";
	private static final String CUSTOMIZATIONPACKAGE_ENDPOINT = VERSION
			+ "/catalogs/testCatalog/catalogVersions/Online/customizationpackages";
	private static final String VARIATION_ENDPOINT = "variations";
	private static final String CUSTOMERSEGMENTATION_ENDPOINT = VERSION + "/customersegmentations";
	private static final String SEGMENT = "segment1";
	private static final String CUSTOMIZATION = "customization1";
	private static final String NEW_CUSTOMIZATION = "newCustomization";
	private static final String NEW_CUSTOMIZATION_NAME = "newCustomizationName";
	private static final String VARIATION = "variation1";
	private static final String VARIATION_WITHOUT_TRIGGER = "variation10";
	private static final String NEW_VARIATION = "newVariation";
	private static final String NEW_VARIATION_NAME = "newVariationName";
	private static final String NEW_TRIGGER = "newTrigger";

	private static final String NOT_EXISTING_SCOPE = "not_existing_scope";
	private static final String BASIC_SCOPE = "basic";
	private static final String PERSONALIZATIONWEBSERVICES_SCOPE = "personalizationwebservices";

	@Test
	public void getAllSegmentsPermissionsCheck() throws IOException
	{
		WebservicesAssert.assertResponse(Status.OK, getAllSegments(ADMIN_USERNAME, ADMIN_PASSWORD));
		WebservicesAssert.assertResponse(Status.OK, getAllSegments(CMSMANAGER_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertResponse(Status.OK, getAllSegments(CMSMANAGER_READ_ONLY_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertResponse(Status.OK, getAllSegments(CMSMANAGER_NOACCESS_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertForbiddenError(getAllSegments(CUSTOMER_USERNAME, CUSTOMER_PASSWORD));
	}

	@Test
	public void getAllSegmentsPermissionsCheckWithNotExistingScope() throws IOException
	{
		WebservicesAssert.assertResponse(Status.UNAUTHORIZED, getAllSegments(CMSMANAGER_USERNAME, CMSMANAGER_PASSWORD, NOT_EXISTING_SCOPE));
		WebservicesAssert.assertResponse(Status.UNAUTHORIZED, getAllSegments(CUSTOMER_USERNAME, CUSTOMER_PASSWORD, NOT_EXISTING_SCOPE));
	}

	@Test
	public void getAllSegmentsPermissionsCheckWithPersonalizationWebservicesScope() throws IOException
	{
		WebservicesAssert.assertResponse(Status.OK, getAllSegments(CMSMANAGER_USERNAME, CMSMANAGER_PASSWORD, PERSONALIZATIONWEBSERVICES_SCOPE));
		WebservicesAssert.assertForbiddenError(getAllSegments(CUSTOMER_USERNAME, CUSTOMER_PASSWORD, PERSONALIZATIONWEBSERVICES_SCOPE));
	}

	@Test
	public void getAllSegmentsPermissionsCheckWithBasicScope() throws IOException
	{
		WebservicesAssert.assertForbiddenError(getAllSegments(CMSMANAGER_USERNAME, CMSMANAGER_PASSWORD, BASIC_SCOPE));
		WebservicesAssert.assertForbiddenError(getAllSegments(CUSTOMER_USERNAME, CUSTOMER_PASSWORD, BASIC_SCOPE));
	}


	protected Response getAllSegments(final String user, final String pwd, final String... scope)
	{
		return getWsSecuredRequestBuilder(user, pwd)//
				.scope(scope)
				.path(SEGMENT_ENDPOINT)//
				.build()//
				.get();
	}


	@Test
	public void getSegmentsByCodePermissionsCheck() throws IOException
	{
		WebservicesAssert.assertResponse(Status.OK, getSegmentsByCode(ADMIN_USERNAME, ADMIN_PASSWORD));
		WebservicesAssert.assertResponse(Status.OK, getSegmentsByCode(CMSMANAGER_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertResponse(Status.OK, getSegmentsByCode(CMSMANAGER_READ_ONLY_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertResponse(Status.OK, getSegmentsByCode(CMSMANAGER_NOACCESS_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertForbiddenError(getSegmentsByCode(CUSTOMER_USERNAME, CUSTOMER_PASSWORD));
	}

	protected Response getSegmentsByCode(final String user, final String pwd)
	{
		return getWsSecuredRequestBuilder(user, pwd)//
				.path(SEGMENT_ENDPOINT)//
				.queryParam("code", "1").build()//
				.get();
	}

	@Test
	public void updateSegmentPermissionsCheck() throws IOException, JAXBException
	{
		WebservicesAssert.assertResponse(Status.OK, updateSegment(ADMIN_USERNAME, ADMIN_PASSWORD));
		WebservicesAssert.assertResponse(Status.OK, updateSegment(CMSMANAGER_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertResponse(Status.OK, updateSegment(CMSMANAGER_READ_ONLY_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertResponse(Status.OK, updateSegment(CMSMANAGER_NOACCESS_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertForbiddenError(updateSegment(CUSTOMER_USERNAME, CUSTOMER_PASSWORD));
	}

	protected Response updateSegment(final String user, final String pwd) throws JAXBException
	{
		final SegmentData data = new SegmentData();
		data.setCode(SEGMENT);
		return getWsSecuredRequestBuilder(user, pwd)//
				.path(SEGMENT_ENDPOINT)//
				.path(SEGMENT)//
				.build()//
				.put(Entity.entity(marshallDto(data, SegmentData.class), MediaType.APPLICATION_JSON));
	}

	@Test
	public void createSegmentPermissionsCheck() throws IOException, JAXBException
	{
		WebservicesAssert.assertResponse(Status.CREATED, createSegment(ADMIN_USERNAME, ADMIN_PASSWORD));
		WebservicesAssert.assertResponse(Status.CREATED, createSegment(CMSMANAGER_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertResponse(Status.CREATED, createSegment(CMSMANAGER_READ_ONLY_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertResponse(Status.CREATED, createSegment(CMSMANAGER_NOACCESS_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertForbiddenError(updateSegment(CUSTOMER_USERNAME, CUSTOMER_PASSWORD));
	}

	protected Response createSegment(final String user, final String pwd) throws JAXBException
	{
		final SegmentData data = new SegmentData();
		data.setCode(UUID.randomUUID().toString());
		return getWsSecuredRequestBuilder(user, pwd)//
				.path(SEGMENT_ENDPOINT)//
				.queryParam("code", "1").build()//
				.post(Entity.entity(marshallDto(data, SegmentData.class), MediaType.APPLICATION_JSON));
	}

	@Test
	public void deleteSegmentPermissionsCheck() throws IOException, JAXBException
	{
		WebservicesAssert.assertResponse(Status.NO_CONTENT, deleteSegment(ADMIN_USERNAME, ADMIN_PASSWORD));
		WebservicesAssert.assertResponse(Status.NO_CONTENT, deleteSegment(CMSMANAGER_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertResponse(Status.NO_CONTENT, deleteSegment(CMSMANAGER_READ_ONLY_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertResponse(Status.NO_CONTENT, deleteSegment(CMSMANAGER_NOACCESS_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertForbiddenError(deleteSegment(CUSTOMER_USERNAME, CUSTOMER_PASSWORD));

	}

	protected Response deleteSegment(final String user, final String pwd) throws JAXBException
	{
		final SegmentData segment = unmarshallResult(createSegment(ADMIN_USERNAME, ADMIN_PASSWORD), SegmentData.class);
		return getWsSecuredRequestBuilder(user, pwd)//
				.path(SEGMENT_ENDPOINT)//
				.path(segment.getCode())//
				.build()//
				.delete();
	}

	@Test
	public void getCustomerSegmentationPermissionsCheck() throws IOException
	{
		WebservicesAssert.assertResponse(Status.OK, getCustomerSegmentation(ADMIN_USERNAME, ADMIN_PASSWORD));
		WebservicesAssert.assertResponse(Status.OK, getCustomerSegmentation(CMSMANAGER_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertResponse(Status.OK, getCustomerSegmentation(CMSMANAGER_READ_ONLY_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertResponse(Status.OK, getCustomerSegmentation(CMSMANAGER_NOACCESS_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertForbiddenError(getCustomerSegmentation(CUSTOMER_USERNAME, CUSTOMER_PASSWORD));
	}

	protected Response getCustomerSegmentation(final String user, final String pwd)
	{
		return getWsSecuredRequestBuilder(user, pwd)//
				.path(CUSTOMERSEGMENTATION_ENDPOINT)//
				.queryParam("segmentId", SEGMENT)//
				.build()//
				.get();
	}

	@Test
	public void getCustmizationByIdPermissionsCheck() throws IOException
	{
		WebservicesAssert.assertResponse(Status.OK, getCustomization(ADMIN_USERNAME, ADMIN_PASSWORD));
		WebservicesAssert.assertResponse(Status.OK, getCustomization(CMSMANAGER_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertResponse(Status.OK, getCustomization(CMSMANAGER_READ_ONLY_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertForbiddenError(getCustomization(CMSMANAGER_NOACCESS_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertForbiddenError(getCustomization(CUSTOMER_USERNAME, CUSTOMER_PASSWORD));
	}

	protected Response getCustomization(final String user, final String pwd)
	{
		return getWsSecuredRequestBuilder(user, pwd)//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.build()//
				.get();
	}

	@Test
	public void getAllCustmizationPermissionsCheck() throws IOException
	{
		WebservicesAssert.assertResponse(Status.OK, getAllCustomizations(ADMIN_USERNAME, ADMIN_PASSWORD));
		WebservicesAssert.assertResponse(Status.OK, getAllCustomizations(CMSMANAGER_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertResponse(Status.OK, getAllCustomizations(CMSMANAGER_READ_ONLY_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertForbiddenError(getAllCustomizations(CMSMANAGER_NOACCESS_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertForbiddenError(getAllCustomizations(CUSTOMER_USERNAME, CUSTOMER_PASSWORD));

	}

	@Test
	public void getAllCustmizationPermissionsCheckWithNotExistingScope() throws IOException
	{
		WebservicesAssert.assertResponse(Status.UNAUTHORIZED, getAllCustomizations(CMSMANAGER_USERNAME, CMSMANAGER_PASSWORD,NOT_EXISTING_SCOPE));
		WebservicesAssert.assertResponse(Status.UNAUTHORIZED, getAllCustomizations(CUSTOMER_USERNAME, CUSTOMER_PASSWORD,NOT_EXISTING_SCOPE));
	}

	@Test
	public void getAllCustmizationPermissionsCheckWithPersonalizationWebservicesScope() throws IOException
	{
		WebservicesAssert.assertResponse(Status.OK, getAllCustomizations(CMSMANAGER_USERNAME, CMSMANAGER_PASSWORD,PERSONALIZATIONWEBSERVICES_SCOPE));
		WebservicesAssert.assertForbiddenError(getAllCustomizations(CUSTOMER_USERNAME, CUSTOMER_PASSWORD,PERSONALIZATIONWEBSERVICES_SCOPE));
	}

	@Test
	public void getAllCustmizationPermissionsCheckWithBasicScope() throws IOException
	{
		WebservicesAssert.assertForbiddenError(getAllCustomizations(CMSMANAGER_USERNAME, CMSMANAGER_PASSWORD, BASIC_SCOPE));
		WebservicesAssert.assertForbiddenError(getAllCustomizations(CUSTOMER_USERNAME, CUSTOMER_PASSWORD, BASIC_SCOPE));
	}

	protected Response getAllCustomizations(final String user, final String pwd, final String... scope)
	{
		return getWsSecuredRequestBuilder(user, pwd)//
				.path(CUSTOMIZATION_ENDPOINT)//
				.scope(scope)//
				.build()//
				.get();
	}


	@Test
	public void createCustomizationPermissionsCheck() throws IOException, JAXBException
	{
		WebservicesAssert.assertResponse(Status.CREATED, createCustomization(ADMIN_USERNAME, ADMIN_PASSWORD));
		WebservicesAssert.assertResponse(Status.CREATED, createCustomization(CMSMANAGER_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertForbiddenError(createCustomization(CMSMANAGER_READ_ONLY_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertForbiddenError(createCustomization(CMSMANAGER_NOACCESS_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertForbiddenError(createCustomization(CUSTOMER_USERNAME, CUSTOMER_PASSWORD));
	}

	protected Response createCustomization(final String user, final String pwd) throws JAXBException
	{
		final Date data = new Date();
		final CustomizationData input = new CustomizationData();
		input.setCode(UUID.randomUUID().toString());
		input.setName(NEW_CUSTOMIZATION_NAME);
		input.setDescription("desc");
		input.setRank(Integer.valueOf(3));
		input.setEnabledStartDate(data);

		//when
		final Response response = getWsSecuredRequestBuilder(user, pwd)//
				.path(CUSTOMIZATION_ENDPOINT)//
				.build()//
				.post(Entity.json(marshallDto(input, CustomizationData.class)));// entity(input, MediaType.APPLICATION_JSON));
		return response;
	}


	@Test
	public void updateCustomizationStatusPermissionsCheck() throws IOException, JAXBException
	{
		WebservicesAssert.assertResponse(Status.OK, updateCustomization(ADMIN_USERNAME, ADMIN_PASSWORD));
		WebservicesAssert.assertResponse(Status.OK, updateCustomization(CMSMANAGER_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertForbiddenError(updateCustomization(CMSMANAGER_READ_ONLY_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertForbiddenError(updateCustomization(CMSMANAGER_NOACCESS_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertForbiddenError(updateCustomization(CUSTOMER_USERNAME, CUSTOMER_PASSWORD));
	}

	protected Response updateCustomization(final String user, final String pwd) throws JAXBException
	{
		//given
		final CustomizationData existing = getWsSecuredRequestBuilderForCmsManager()//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.build()//
				.get(CustomizationData.class);

		existing.setStatus(ItemStatus.DISABLED);

		//when
		final Response response = getWsSecuredRequestBuilder(user, pwd)//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION).build()//
				.put(Entity.json(marshallDto(existing, CustomizationData.class)));
		return response;
	}


	@Test
	public void getVariationsPermissionsCheck() throws IOException
	{
		WebservicesAssert.assertResponse(Status.OK, getVariations(ADMIN_USERNAME, ADMIN_PASSWORD));
		WebservicesAssert.assertResponse(Status.OK, getVariations(CMSMANAGER_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertResponse(Status.OK, getVariations(CMSMANAGER_READ_ONLY_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertForbiddenError(getVariations(CMSMANAGER_NOACCESS_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertForbiddenError(getVariations(CUSTOMER_USERNAME, CUSTOMER_PASSWORD));

	}

	protected Response getVariations(final String user, final String pwd)
	{
		return getWsSecuredRequestBuilder(user, pwd)//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.build()//
				.get();
	}


	@Test
	public void updateVariationPermissionsCheck() throws IOException
	{
		WebservicesAssert.assertResponse(Status.OK, updateVariation(ADMIN_USERNAME, ADMIN_PASSWORD));
		WebservicesAssert.assertResponse(Status.OK, updateVariation(CMSMANAGER_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertForbiddenError(updateVariation(CMSMANAGER_READ_ONLY_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertForbiddenError(updateVariation(CMSMANAGER_NOACCESS_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertForbiddenError(updateVariation(CUSTOMER_USERNAME, CUSTOMER_PASSWORD));
	}

	protected Response updateVariation(final String user, final String pwd)
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
		final Response response = getWsSecuredRequestBuilder(user, pwd)//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.build()//
				.put(Entity.entity(input, MediaType.APPLICATION_JSON));
		return response;
	}


	@Test
	public void getTriggersPermissionsCheck() throws IOException
	{
		WebservicesAssert.assertResponse(Status.OK, getTriggers(ADMIN_USERNAME, ADMIN_PASSWORD));
		WebservicesAssert.assertResponse(Status.OK, getTriggers(CMSMANAGER_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertResponse(Status.OK, getTriggers(CMSMANAGER_READ_ONLY_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertForbiddenError(getTriggers(CMSMANAGER_NOACCESS_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertForbiddenError(getTriggers(CUSTOMER_USERNAME, CUSTOMER_PASSWORD));
	}

	protected Response getTriggers(final String user, final String pwd)
	{
		return getWsSecuredRequestBuilder(user, pwd)//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(TRIGGER_ENDPOINT)//
				.build()//
				.get();
	}


	@Test
	public void getSegmentTriggerPermissionsCheck() throws IOException, JAXBException
	{
		WebservicesAssert.assertResponse(Status.OK, getSegmentTrigger(ADMIN_USERNAME, ADMIN_PASSWORD));
		WebservicesAssert.assertResponse(Status.OK, getSegmentTrigger(CMSMANAGER_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertResponse(Status.OK, getSegmentTrigger(CMSMANAGER_READ_ONLY_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertForbiddenError(getSegmentTrigger(CMSMANAGER_NOACCESS_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertForbiddenError(getSegmentTrigger(CUSTOMER_USERNAME, CUSTOMER_PASSWORD));
	}

	protected Response getSegmentTrigger(final String user, final String pwd)
	{
		return getWsSecuredRequestBuilder(user, pwd)//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(TRIGGER_ENDPOINT)//
				.path(TRIGGER).build()//
				.get();
	}


	@Test
	public void createDefaultTriggerPermissionsCheck() throws IOException, JAXBException
	{
		WebservicesAssert.assertResponse(Status.CREATED, createTrigger(ADMIN_USERNAME, ADMIN_PASSWORD));
		WebservicesAssert.assertResponse(Status.CREATED, createTrigger(CMSMANAGER_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertForbiddenError(createTrigger(CMSMANAGER_READ_ONLY_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertForbiddenError(createTrigger(CMSMANAGER_NOACCESS_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertForbiddenError(createTrigger(CUSTOMER_USERNAME, CUSTOMER_PASSWORD));
	}

	protected Response createTrigger(final String user, final String pwd) throws JAXBException
	{

		//given
		final SegmentData segmentData = new SegmentData();
		segmentData.setCode(SEGMENT);

		final SegmentTriggerData triggerData = new SegmentTriggerData();
		triggerData.setCode("triggercode");
		triggerData.setGroupBy("OR");
		triggerData.setSegments(Arrays.asList(segmentData));


		//when
		final Response response = getWsSecuredRequestBuilder(user, pwd)//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION_WITHOUT_TRIGGER)//
				.path(TRIGGER_ENDPOINT)//
				.build()//
				.post(Entity.json(marshallDto(triggerData, SegmentTriggerData.class)));

		getWsSecuredRequestBuilder(ADMIN_USERNAME, ADMIN_PASSWORD)//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION_WITHOUT_TRIGGER)//
				.path(TRIGGER_ENDPOINT)//
				.path("triggercode")//
				.build().delete();

		//then
		return response;
	}



	@Test
	public void updateTriggerPermissionsCheck() throws IOException, JAXBException
	{
		WebservicesAssert.assertResponse(Status.OK, updateTrigger(ADMIN_USERNAME, ADMIN_PASSWORD));
		WebservicesAssert.assertResponse(Status.OK, updateTrigger(CMSMANAGER_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertForbiddenError(updateTrigger(CMSMANAGER_READ_ONLY_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertForbiddenError(updateTrigger(CMSMANAGER_NOACCESS_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertForbiddenError(updateTrigger(CUSTOMER_USERNAME, CUSTOMER_PASSWORD));
	}

	protected Response updateTrigger(final String user, final String pwd) throws JAXBException
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
		final Response response = getWsSecuredRequestBuilder(user, pwd)//
				.path(CUSTOMIZATION_ENDPOINT)//
				.path(CUSTOMIZATION)//
				.path(VARIATION_ENDPOINT)//
				.path(VARIATION)//
				.path(TRIGGER_ENDPOINT)//
				.path(TRIGGER)//
				.build()//
				.put(Entity.entity(marshallDto(input, SegmentTriggerData.class), MediaType.APPLICATION_JSON));

		return response;
	}


	@Test
	public void createCustomizationPackagePermissionsCheck() throws IOException, JAXBException
	{
		WebservicesAssert.assertResponse(Status.CREATED, crearteCustomizationPackage(ADMIN_USERNAME, ADMIN_PASSWORD));
		WebservicesAssert.assertResponse(Status.CREATED, crearteCustomizationPackage(CMSMANAGER_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertForbiddenError(crearteCustomizationPackage(CMSMANAGER_READ_ONLY_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertForbiddenError(crearteCustomizationPackage(CMSMANAGER_NOACCESS_USERNAME, CMSMANAGER_PASSWORD));
		WebservicesAssert.assertForbiddenError(crearteCustomizationPackage(CUSTOMER_USERNAME, CUSTOMER_PASSWORD));
	}

	protected Response crearteCustomizationPackage(final String user, final String pwd) throws JAXBException
	{
		//given
		final CustomizationData input = creteCustomizationData(UUID.randomUUID().toString(), NEW_CUSTOMIZATION_NAME, NEW_VARIATION,
				NEW_VARIATION_NAME, () -> createSegmentTriggerData(NEW_TRIGGER, SEGMENT));
		input.setDescription("desc");
		input.setRank(Integer.valueOf(3));

		//when
		final Response response = getWsSecuredRequestBuilder(user, pwd)//
				.path(CUSTOMIZATIONPACKAGE_ENDPOINT)//
				.build()//
				.post(Entity.entity(marshallDto(input, CustomizationData.class), MediaType.APPLICATION_JSON));

		//then
		return response;
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


}

/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.warehousingwebservices.warehousingwebservices;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.warehousing.enums.AsnStatus;
import de.hybris.platform.warehousingwebservices.constants.WarehousingwebservicesConstants;
import de.hybris.platform.warehousingwebservices.dto.asn.AsnEntryWsDTO;
import de.hybris.platform.warehousingwebservices.dto.asn.AsnWsDTO;
import de.hybris.platform.warehousingwebservices.warehousingwebservices.util.BaseWarehousingWebservicesIntegrationTest;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Integration test for testing {@link de.hybris.platform.warehousingwebservices.controllers.asn.WarehousingAsnsController}
 */
@NeedsEmbeddedServer(webExtensions = { WarehousingwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class AsnsControllerIntegrationTest extends BaseWarehousingWebservicesIntegrationTest
{
	private static final String PRODUCT_CODE1 = "1111";
	private static final String PRODUCT_CODE2 = "2222";
	private static final String EXTERNAL_ID = "EXT123";
	private static final String NOTES = "some notes";
	private static final String REASON = "missing";
	private static final String SUBJECT_TYPE = "parameter";

	private static final Integer QUANTITY = 125;

	private List<AsnEntryWsDTO> asnEntries;
	private Date date;
	private String pointOfServiceName;
	private String warehouseCode;

	@Override
	@Before
	public void setup()
	{
		//Given
		final AsnEntryWsDTO newEntry1 = createAsnEntryWsDTO(PRODUCT_CODE1, QUANTITY);
		final AsnEntryWsDTO newEntry2 = createAsnEntryWsDTO(PRODUCT_CODE2, QUANTITY);
		asnEntries = new ArrayList<>();
		asnEntries.add(newEntry1);
		asnEntries.add(newEntry2);
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, 1);
		date = calendar.getTime();
	}

	/**
	 * Prepares data required for the test
	 */
	@Before
	public void prepareTestData()
	{
		pointOfServiceName = pointsOfService.Boston().getName();
		warehouseCode = warehouses.Boston().getCode();
	}

	/**
	 * Tests the controller behavior when obtaining REST call without WarehouseCode specified
	 */
	@Test
	public void testPostAsnEmptyWarehouseCode()
	{
		//Given
		final AsnWsDTO newAsn = createAsnRequest(null, pointOfServiceName, asnEntries, EXTERNAL_ID, date, NOTES);

		//When
		final Response response = postAsnByDefault(newAsn);
		final AsnWsDTO asn = response.readEntity(AsnWsDTO.class);

		//Then
		assertEquals(asn.getWarehouseCode(), warehouseCode);
	}

	/**
	 * Tests the controller behavior when obtaining REST call without PointOfService name specified
	 */
	@Test
	public void testPostAsnEmptyPosName()
	{
		//Given
		final AsnWsDTO newAsn = createAsnRequest(warehouseCode, null, asnEntries, EXTERNAL_ID, date, NOTES);

		//When
		final Response response = postAsnByDefault(newAsn);

		//Then
		assertBadRequestWithContent(response, REASON, "pointOfServiceName", SUBJECT_TYPE);
	}

	/**
	 * Tests the controller behavior when obtaining REST call without ExternalId specified
	 */
	@Test
	public void testPostAsnEmptyExternalId()
	{
		//Given
		final AsnWsDTO newAsn = createAsnRequest(warehouseCode, pointOfServiceName, asnEntries, null, date, NOTES);

		//When
		final Response response = postAsnByDefault(newAsn);

		//Then
		assertBadRequestWithContent(response, REASON, "externalId", SUBJECT_TYPE);
	}

	/**
	 * Tests the controller behavior when obtaining REST call without releaseDate specified
	 */
	@Test
	public void testPostAsnEmptyReleaseDate()
	{
		//Given
		final AsnWsDTO newAsn = createAsnRequest(warehouseCode, pointOfServiceName, asnEntries, EXTERNAL_ID, null, NOTES);

		//When
		final Response response = postAsnByDefault(newAsn);

		//Then
		assertBadRequestWithContent(response, REASON, "releaseDate", SUBJECT_TYPE);
	}

	/**
	 * Tests the InternalId generation for imported Asn
	 */
	@Test
	public void testInternalIdGeneration()
	{
		//Given
		final AsnWsDTO newAsn = createAsnRequest(warehouseCode, pointOfServiceName, asnEntries, EXTERNAL_ID, date, NOTES);

		//When
		final Response response = postAsnByDefault(newAsn);
		final AsnWsDTO asn = response.readEntity(AsnWsDTO.class);

		//Then
		assertNotNull(asn.getInternalId());
	}

	/**
	 * Tests the cancellation of Asn
	 */
	@Test
	public void testConfirmAsnReceipt()
	{
		//Given
		final AsnWsDTO newAsn = createAsnRequest(warehouseCode, pointOfServiceName, asnEntries, EXTERNAL_ID, date, NOTES);
		final Response response = postAsnByDefault(newAsn);
		final AsnWsDTO asn = response.readEntity(AsnWsDTO.class);

		//When
		final Response confirmedAsnResponse = postConfirmAsnReceiptByDefault(asn.getInternalId());
		final AsnWsDTO confirmedAsnWsDTO = confirmedAsnResponse.readEntity(AsnWsDTO.class);

		//Then
		assertEquals(AsnStatus.RECEIVED.toString(), confirmedAsnWsDTO.getStatus());
	}

	/**
	 * Populates a {@link AsnWsDTO} for a POST call, to add an ASN in the system
	 *
	 * @param warehouseCode
	 * @param pointOfServiceName
	 * @param asnEntries
	 * @param extId
	 * @param releaseDate
	 * @param comment
	 * @return asnWsDTO
	 */
	protected AsnWsDTO createAsnRequest(final String warehouseCode, final String pointOfServiceName,
			final List<AsnEntryWsDTO> asnEntries, final String extId, final Date releaseDate, final String comment)
	{
		final AsnWsDTO asnWsDTO = new AsnWsDTO();
		if (warehouseCode != null)
		{
			asnWsDTO.setWarehouseCode(warehouseCode);
		}
		if (pointOfServiceName != null)
		{
			asnWsDTO.setPointOfServiceName(pointOfServiceName);
		}

		asnWsDTO.setExternalId(extId);
		asnWsDTO.setComment(comment);
		asnWsDTO.setAsnEntries(asnEntries);
		asnWsDTO.setReleaseDate(releaseDate);

		return asnWsDTO;
	}

	/**
	 * Populates a {@link AsnEntryWsDTO}
	 *
	 * @param productCode
	 * @param quantity
	 * @return asnEntryWsDTO
	 */
	protected AsnEntryWsDTO createAsnEntryWsDTO(final String productCode, final Integer quantity)
	{
		final AsnEntryWsDTO asnEntryWsDTO = new AsnEntryWsDTO();
		asnEntryWsDTO.setProductCode(productCode);
		asnEntryWsDTO.setQuantity(quantity);
		return asnEntryWsDTO;
	}
}

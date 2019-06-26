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
 */
package de.hybris.platform.warehousingwebservices.warehousingwebservices;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.warehousing.enums.AsnStatus;
import de.hybris.platform.warehousing.model.AdvancedShippingNoticeEntryModel;
import de.hybris.platform.warehousing.model.AdvancedShippingNoticeModel;
import de.hybris.platform.warehousing.util.models.Products;
import de.hybris.platform.warehousing.util.models.Warehouses;
import de.hybris.platform.warehousingwebservices.constants.WarehousingwebservicesConstants;
import de.hybris.platform.warehousingwebservices.dto.product.StockLevelSearchPageWsDto;
import de.hybris.platform.warehousingwebservices.dto.product.StockLevelWsDto;
import de.hybris.platform.warehousingwebservices.dto.stocklevel.StockLevelAdjustmentReasonsWsDTO;
import de.hybris.platform.warehousingwebservices.dto.stocklevel.StockLevelAdjustmentWsDTO;
import de.hybris.platform.warehousingwebservices.dto.stocklevel.StockLevelAdjustmentsWsDTO;
import de.hybris.platform.warehousingwebservices.dto.store.WarehouseWsDto;
import de.hybris.platform.warehousingwebservices.warehousingwebservices.util.BaseWarehousingWebservicesIntegrationTest;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


@NeedsEmbeddedServer(webExtensions = { WarehousingwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class StockLevelsControllersIntegrationTest extends BaseWarehousingWebservicesIntegrationTest
{
	private static final String INCREASE_REASON = "INCREASE";
	private static final String SHRINKAGE_REASON = "SHRINKAGE";
	private static final String WASTAGE_REASON = "WASTAGE";
	private static final String VALID_LOWERCASE_REASON = "wastage";
	private static final String INVALID_REASON = "INVALIDREASON1";

	private static final String COMMENT_TEXT = "test comment";
	private StockLevelModel stockLevels_Montreal_Camera;
	private AdvancedShippingNoticeEntryModel advancedShippingNoticeEntry = new AdvancedShippingNoticeEntryModel();
	private AdvancedShippingNoticeModel advancedShippingNotice = new AdvancedShippingNoticeModel();

	@Before
	public void setup()
	{
		super.setup();
		stockLevels.Lens(warehouses.Boston(), 5);
		stockLevels.Camera(warehouses.Boston(), 4);
		stockLevels_Montreal_Camera = stockLevels.Camera(warehouses.Montreal(), 5);
		components.warehousingComponent();
		commentTypes.adjustmentNote();
		users.Bob();

		final Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		final Date tomorrow = calendar.getTime();

		advancedShippingNotice.setExternalId("123");
		advancedShippingNotice.setWarehouse(warehouses.Montreal());
		advancedShippingNotice.setPointOfService(pointsOfService.Montreal_Downtown());
		advancedShippingNotice.setReleaseDate(tomorrow);
		advancedShippingNoticeEntry.setProductCode(Products.CODE_CAMERA);
		advancedShippingNoticeEntry.setAsn(advancedShippingNotice);
		advancedShippingNotice.setAsnEntries(Arrays.asList(advancedShippingNoticeEntry));
	}

	@Test
	public void testGetStockLevelsForWarehouseCode()
	{
		//When
		final StockLevelSearchPageWsDto result = getStockLevelsForWarehouseCodeByDefault(Warehouses.CODE_BOSTON);
		//then
		assertEquals(2, result.getStockLevels().size());
	}

	@Test
	public void testPostStockLevel()
	{
		//When
		final StockLevelWsDto newStock = createStockLevelRequest(Products.CODE_CAMERA, Warehouses.CODE_BOSTON, 10);

		final StockLevelWsDto newStockCreated = postStockLevelByDefault_WithReturnType_StockLevelWsDto(newStock);

		//then
		assertEquals(10L, newStockCreated.getInitialQuantityOnHand().longValue());
	}

	@Test
	public void testPostStockLevelEmptyProductCode()
	{
		//When
		final StockLevelWsDto newStock = createStockLevelRequest(null, Warehouses.CODE_BOSTON, 10);

		final Response response = postStockLevelByDefault(newStock);

		//then
		assertBadRequestWithContent(response, "missing", "productCode", "parameter");
	}

	@Test
	public void testPostStockLevelNegativeInitialOnHandQuantity()
	{
		//When
		final StockLevelWsDto newStock = createStockLevelRequest(Products.CODE_CAMERA, Warehouses.CODE_BOSTON, -5);

		final Response response = postStockLevelByDefault(newStock);

		//then
		assertBadRequestWithContent(response, "invalid", "initialQuantityOnHand", "parameter");
	}

	@Test
	public void testPostStockLevelEmptyWarehouseCode()
	{
		//When
		final StockLevelWsDto newStock = createStockLevelRequest(Products.CODE_CAMERA, null, 10);

		final Response response = postStockLevelByDefault(newStock);

		//then
		assertBadRequestWithContent(response, "missing", "warehouse.code", "parameter");
	}

	@Test
	public void getAllStockLevelAdjustmentReasons()
	{
		//When
		final StockLevelAdjustmentReasonsWsDTO result = getStockLevelAdjustmentReasons();
		//then
		assertEquals(3, result.getReasons().size());
	}

	@Test
	public void postCreateStockLevelAdjustment()
	{
		//When
		final StockLevelAdjustmentWsDTO stockLevelAdjustmentWsDTO = createStockLevelAdjustmentWsDTO(INCREASE_REASON, 3);

		final StockLevelAdjustmentsWsDTO response = postStockLevelAdjustmentByDefault(Products.CODE_CAMERA,
				Warehouses.CODE_MONTREAL, createStockLevelAdjustmentsWsDTO(stockLevelAdjustmentWsDTO));

		//then
		assertEquals(3L, response.getStockLevelAdjustments().iterator().next().getQuantity().longValue());
		assertEquals(INCREASE_REASON, response.getStockLevelAdjustments().iterator().next().getReason());
	}

	@Test
	public void postCreateStockLevelAdjustment_invalid_Reason()
	{
		//When
		final StockLevelAdjustmentWsDTO stockLevelAdjustmentWsDTO = createStockLevelAdjustmentWsDTO(INVALID_REASON, 1);

		final Response response = postStockLevelAdjustmentByDefault_Reponse(Products.CODE_CAMERA, Warehouses.CODE_MONTREAL,
				createStockLevelAdjustmentsWsDTO(stockLevelAdjustmentWsDTO));

		//then
		assertBadRequestWithContent(response, "invalid", "reason", "parameter");
	}

	@Test
	public void postCreateStockLevelAdjustment_valid_LowercaseReason()
	{
		//When
		final StockLevelAdjustmentWsDTO stockLevelAdjustmentWsDTO = createStockLevelAdjustmentWsDTO(VALID_LOWERCASE_REASON, 1);

		final StockLevelAdjustmentsWsDTO response = postStockLevelAdjustmentByDefault(Products.CODE_CAMERA,
				Warehouses.CODE_MONTREAL, createStockLevelAdjustmentsWsDTO(stockLevelAdjustmentWsDTO));

		//then
		assertEquals(VALID_LOWERCASE_REASON.toUpperCase(), response.getStockLevelAdjustments().iterator().next().getReason());
	}

	@Test
	public void postCreateMultiStockLevelAdjustment()
	{
		//When
		final StockLevelAdjustmentWsDTO stockLevelAdjustmentWsDTO = createStockLevelAdjustmentWsDTO(INCREASE_REASON, 3);
		final StockLevelAdjustmentWsDTO stockLevelAdjustmentWsDTO2 = createStockLevelAdjustmentWsDTO(SHRINKAGE_REASON, 2);
		final StockLevelAdjustmentWsDTO stockLevelAdjustmentWsDTO3 = createStockLevelAdjustmentWsDTO(WASTAGE_REASON, 1);

		final StockLevelAdjustmentsWsDTO response = postStockLevelAdjustmentByDefault(Products.CODE_CAMERA,
				Warehouses.CODE_MONTREAL,
				createStockLevelAdjustmentsWsDTO(stockLevelAdjustmentWsDTO, stockLevelAdjustmentWsDTO2, stockLevelAdjustmentWsDTO3));

		//then
		response.getStockLevelAdjustments().stream()
				.anyMatch(e -> 3L == e.getQuantity().longValue() && INCREASE_REASON.equals(e.getReason()));
		response.getStockLevelAdjustments().stream()
				.anyMatch(e -> 1L == e.getQuantity().longValue() && WASTAGE_REASON.equals(e.getReason()));
	}

	@Test
	public void postCreateStockLevelAdjustment_WithComment()
	{
		//When
		final StockLevelAdjustmentWsDTO stockLevelAdjustmentWsDTO = createStockLevelAdjustmentWsDTO(INCREASE_REASON, 3,
				COMMENT_TEXT);

		final StockLevelAdjustmentsWsDTO response = postStockLevelAdjustmentByDefault(Products.CODE_CAMERA,
				Warehouses.CODE_MONTREAL, createStockLevelAdjustmentsWsDTO(stockLevelAdjustmentWsDTO));

		//then
		assertEquals(3L, response.getStockLevelAdjustments().iterator().next().getQuantity().longValue());
		assertEquals(INCREASE_REASON, response.getStockLevelAdjustments().iterator().next().getReason());
		assertEquals(COMMENT_TEXT, response.getStockLevelAdjustments().iterator().next().getComment());
	}

	@Test
	public void postCreateStockLevelAdjustment_WithBin()
	{
		//When
		stockLevels_Montreal_Camera.setBin("4");
		modelService.save(stockLevels_Montreal_Camera);
		final StockLevelAdjustmentWsDTO stockLevelAdjustmentWsDTO = createStockLevelAdjustmentWsDTO(INCREASE_REASON, 3,
				COMMENT_TEXT);
		final StockLevelAdjustmentsWsDTO stockLevelAdjustmentsWsDTO = createStockLevelAdjustmentsWsDTO(stockLevelAdjustmentWsDTO);

		final StockLevelAdjustmentsWsDTO response = getWsRequestBuilder()
				.path(STOCKLEVELS + "/product/" + Products.CODE_CAMERA + "/warehouse/" + Warehouses.CODE_MONTREAL + "/adjustment")
				.queryParam("binCode", 4).build().accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(stockLevelAdjustmentsWsDTO, MediaType.APPLICATION_JSON), StockLevelAdjustmentsWsDTO.class);
		//then
		assertEquals(3L, response.getStockLevelAdjustments().iterator().next().getQuantity().longValue());
		assertEquals(INCREASE_REASON, response.getStockLevelAdjustments().iterator().next().getReason());
		assertEquals(COMMENT_TEXT, response.getStockLevelAdjustments().iterator().next().getComment());
	}

	@Test
	public void postCreateStockLevelAdjustment_WithReleaseDate()
	{
		//given
		final Date date = new Date();
		final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		stockLevels_Montreal_Camera.setReleaseDate(date);
		modelService.save(stockLevels_Montreal_Camera);
		//when
		final StockLevelAdjustmentWsDTO stockLevelAdjustmentWsDTO = createStockLevelAdjustmentWsDTO(INCREASE_REASON, 3,
				COMMENT_TEXT);
		final StockLevelAdjustmentsWsDTO stockLevelAdjustmentsWsDTO = createStockLevelAdjustmentsWsDTO(stockLevelAdjustmentWsDTO);

		final StockLevelAdjustmentsWsDTO response = getWsRequestBuilder()
				.path(STOCKLEVELS + "/product/" + Products.CODE_CAMERA + "/warehouse/" + Warehouses.CODE_MONTREAL + "/adjustment")
				.queryParam("releaseDate", dateFormat.format(date)).build().accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(stockLevelAdjustmentsWsDTO, MediaType.APPLICATION_JSON), StockLevelAdjustmentsWsDTO.class);
		//then
		assertEquals(3L, response.getStockLevelAdjustments().iterator().next().getQuantity().longValue());
		assertEquals(INCREASE_REASON, response.getStockLevelAdjustments().iterator().next().getReason());
		assertEquals(COMMENT_TEXT, response.getStockLevelAdjustments().iterator().next().getComment());
	}

	@Test(expected = InternalServerErrorException.class)
	public void postCreateStockLevelAdjustment_WithBin_ForAsnNotReceived()
	{
		//When
		advancedShippingNotice.setStatus(AsnStatus.CREATED);
		modelService.saveAll(advancedShippingNotice);
		stockLevels_Montreal_Camera.setBin("asn-2");
		stockLevels_Montreal_Camera.setAsnEntry(advancedShippingNoticeEntry);
		modelService.save(stockLevels_Montreal_Camera);
		final StockLevelAdjustmentWsDTO stockLevelAdjustmentWsDTO = createStockLevelAdjustmentWsDTO(INCREASE_REASON, 3,
				COMMENT_TEXT);
		final StockLevelAdjustmentsWsDTO stockLevelAdjustmentsWsDTO = createStockLevelAdjustmentsWsDTO(stockLevelAdjustmentWsDTO);

		final StockLevelAdjustmentsWsDTO response = getWsRequestBuilder()
				.path(STOCKLEVELS + "/product/" + Products.CODE_CAMERA + "/warehouse/" + Warehouses.CODE_MONTREAL + "/adjustment")
				.queryParam("binCode", "asn-2").build().accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(stockLevelAdjustmentsWsDTO, MediaType.APPLICATION_JSON), StockLevelAdjustmentsWsDTO.class);
	}

	@Test
	public void postCreateStockLevelAdjustment_WithBin_ForAsnReceived()
	{
		//When
		advancedShippingNotice.setStatus(AsnStatus.RECEIVED);
		modelService.save(advancedShippingNotice);
		stockLevels_Montreal_Camera.setBin("asn-1");
		stockLevels_Montreal_Camera.setAsnEntry(advancedShippingNoticeEntry);
		modelService.save(stockLevels_Montreal_Camera);
		final StockLevelAdjustmentWsDTO stockLevelAdjustmentWsDTO = createStockLevelAdjustmentWsDTO(INCREASE_REASON, 3,
				COMMENT_TEXT);
		final StockLevelAdjustmentsWsDTO stockLevelAdjustmentsWsDTO = createStockLevelAdjustmentsWsDTO(stockLevelAdjustmentWsDTO);

		final StockLevelAdjustmentsWsDTO response = getWsRequestBuilder()
				.path(STOCKLEVELS + "/product/" + Products.CODE_CAMERA + "/warehouse/" + Warehouses.CODE_MONTREAL + "/adjustment")
				.queryParam("binCode", "asn-1").build().accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(stockLevelAdjustmentsWsDTO, MediaType.APPLICATION_JSON), StockLevelAdjustmentsWsDTO.class);
		//then
		assertEquals(3L, response.getStockLevelAdjustments().iterator().next().getQuantity().longValue());
		assertEquals(INCREASE_REASON, response.getStockLevelAdjustments().iterator().next().getReason());
		assertEquals(COMMENT_TEXT, response.getStockLevelAdjustments().iterator().next().getComment());
	}

	/**
	 * Populates a {@link StockLevelWsDto} for a POST call, to add a StockLevel in the system
	 *
	 * @param productCode
	 * @param warehouseCode
	 * @param initialQuantityOnHand
	 * @return
	 */
	protected StockLevelWsDto createStockLevelRequest(final String productCode, final String warehouseCode,
			final Integer initialQuantityOnHand)
	{
		final WarehouseWsDto warehouseWsDto = new WarehouseWsDto();
		warehouseWsDto.setCode(warehouseCode);

		final StockLevelWsDto stockLevelWsDto = new StockLevelWsDto();
		stockLevelWsDto.setProductCode(productCode);
		stockLevelWsDto.setInitialQuantityOnHand(initialQuantityOnHand);
		stockLevelWsDto.setWarehouse(warehouseWsDto);

		return stockLevelWsDto;
	}

	/**
	 * Populates a {@link StockLevelAdjustmentWsDTO} for a POST call, to add a StockLevel adjustment in the system
	 *
	 * @param reason
	 * @param quantity
	 * @param comment
	 * @return StockLevelAdjustmentWsDTO
	 */
	protected StockLevelAdjustmentWsDTO createStockLevelAdjustmentWsDTO(final String reason, final long quantity,
			final String comment)
	{

		final StockLevelAdjustmentWsDTO stockLevelAdjustmentWsDTO = new StockLevelAdjustmentWsDTO();
		stockLevelAdjustmentWsDTO.setComment(comment);
		stockLevelAdjustmentWsDTO.setReason(reason);
		stockLevelAdjustmentWsDTO.setQuantity(quantity);
		return stockLevelAdjustmentWsDTO;
	}

	/**
	 * Populates a {@link StockLevelAdjustmentWsDTO} for a POST call, to add a StockLevel adjustment in the system
	 *
	 * @param reason
	 * @param quantity
	 * @return StockLevelAdjustmentWsDTO
	 */
	protected StockLevelAdjustmentWsDTO createStockLevelAdjustmentWsDTO(final String reason, final long quantity)
	{

		final StockLevelAdjustmentWsDTO stockLevelAdjustmentWsDTO = new StockLevelAdjustmentWsDTO();
		stockLevelAdjustmentWsDTO.setReason(reason);
		stockLevelAdjustmentWsDTO.setQuantity(quantity);
		return stockLevelAdjustmentWsDTO;
	}

	/**
	 * Populates a {@link StockLevelAdjustmentsWsDTO} for a POST call, to add a list of StockLevelAdjustmentWsDTO
	 *
	 * @param stockLevelAdjustmentWsDTO
	 * @return
	 */
	protected StockLevelAdjustmentsWsDTO createStockLevelAdjustmentsWsDTO(
			final StockLevelAdjustmentWsDTO... stockLevelAdjustmentWsDTO)
	{
		final List<StockLevelAdjustmentWsDTO> stockLevelAdjustmentWsDTOs = new ArrayList<>();
		Collections.addAll(stockLevelAdjustmentWsDTOs, stockLevelAdjustmentWsDTO);
		final StockLevelAdjustmentsWsDTO stockLevelAdjustmentsWsDTO = new StockLevelAdjustmentsWsDTO();
		stockLevelAdjustmentsWsDTO.setStockLevelAdjustments(stockLevelAdjustmentWsDTOs);
		return stockLevelAdjustmentsWsDTO;
	}
}

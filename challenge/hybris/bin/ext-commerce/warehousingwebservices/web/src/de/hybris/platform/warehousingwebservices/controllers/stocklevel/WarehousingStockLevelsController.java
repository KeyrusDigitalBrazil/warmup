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
package de.hybris.platform.warehousingwebservices.controllers.stocklevel;


import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.util.localization.Localization;
import de.hybris.platform.warehousing.enums.StockLevelAdjustmentReason;
import de.hybris.platform.warehousingfacades.product.data.StockLevelData;
import de.hybris.platform.warehousingfacades.stocklevel.WarehousingStockLevelFacade;
import de.hybris.platform.warehousingfacades.stocklevel.data.StockLevelAdjustmentData;
import de.hybris.platform.warehousingfacades.stocklevel.data.StockLevelAdjustmentReasonDataList;
import de.hybris.platform.warehousingfacades.warehouse.WarehousingWarehouseFacade;
import de.hybris.platform.warehousingwebservices.controllers.WarehousingBaseController;
import de.hybris.platform.warehousingwebservices.dto.product.StockLevelSearchPageWsDto;
import de.hybris.platform.warehousingwebservices.dto.product.StockLevelWsDto;
import de.hybris.platform.warehousingwebservices.dto.stocklevel.StockLevelAdjustmentReasonsWsDTO;
import de.hybris.platform.warehousingwebservices.dto.stocklevel.StockLevelAdjustmentWsDTO;
import de.hybris.platform.warehousingwebservices.dto.stocklevel.StockLevelAdjustmentsWsDTO;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * WebResource exposing {@link WarehousingStockLevelFacade}
 * http://host:port/warehousingwebservices/stocklevels
 */
@Controller
@RequestMapping(value = "/stocklevels")
@Api(value = "/stocklevels", description = "Stock Level's Operations")
public class WarehousingStockLevelsController extends WarehousingBaseController
{
	@Resource
	private WarehousingStockLevelFacade warehousingStockLevelFacade;
	@Resource
	private WarehousingWarehouseFacade warehousingWarehouseFacade;
	@Resource(name = "warehousingStockLevelValidator")
	private Validator warehousingStockLevelValidator;
	@Resource(name = "stockLevelAdjustmentValidator")
	private Validator stockLevelAdjustmentValidator;
	@Resource(name = "stockLevelAdjustmentReasonValidator")
	private Validator stockLevelAdjustmentReasonValidator;

	/**
	 * Request to get a {@link de.hybris.platform.ordersplitting.model.StockLevelModel} for its {@value de.hybris.platform.ordersplitting.model.WarehouseModel#CODE}
	 *
	 * @param code
	 * 		the code of the requested {@link de.hybris.platform.ordersplitting.model.WarehouseModel}
	 * @param fields
	 * 		defaulted to DEFAULT but can be FULL or BASIC
	 * @param currentPage
	 * 		number of the current page
	 * @param pageSize
	 * 		number of items in a page
	 * @param sort
	 * 		sorting the results ascending or descending
	 * @return the list of stocklevels
	 */
	@RequestMapping(value = "/warehouses/{code}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Finds a paginated list of stock levels by a given warehouse code", response = StockLevelSearchPageWsDto.class)
	public StockLevelSearchPageWsDto getStockLevelsForWarehouseCode(
			@ApiParam(value = "The code for the warehouse", required = true) @PathVariable final String code,
			@ApiParam(value = "Fields mapping level") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields,
			@ApiParam(value = "Current page") @RequestParam(required = false, defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
			@ApiParam(value = "Page size") @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
			@ApiParam(value = "Sort parameter") @RequestParam(required = false, defaultValue = DEFAULT_SORT) final String sort)
	{
		final PageableData pageableData = createPageable(currentPage, pageSize, sort);
		final SearchPageData<StockLevelData> stockLevelSearchPageData = warehousingStockLevelFacade
				.getStockLevelsForWarehouseCode(code, pageableData);
		return dataMapper.map(stockLevelSearchPageData, StockLevelSearchPageWsDto.class, fields);
	}

	/**
	 * Request to create a {@link de.hybris.platform.ordersplitting.model.StockLevelModel} in the system
	 *
	 * @param fields
	 * 		defaulted to DEFAULT but can be FULL or BASIC
	 * @param stockLevelWsDto
	 * 		object representing {@link StockLevelWsDto}
	 * @return created stockLevel
	 */
	@RequestMapping(method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	@ApiOperation(value = "Creates a stocklevel", response = StockLevelWsDto.class)
	public StockLevelWsDto createStockLevel(
			@ApiParam(value = "The stocklevel object to be created", required = true) @RequestBody final StockLevelWsDto stockLevelWsDto,
			@ApiParam(value = "Fields mapping level") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
			throws WebserviceValidationException
	{
		validate(stockLevelWsDto, "stockLevelWsDto", warehousingStockLevelValidator);
		final StockLevelData stockLevelData = dataMapper.map(stockLevelWsDto, StockLevelData.class);
		final StockLevelData createdStockLevelData = warehousingStockLevelFacade.createStockLevel(stockLevelData);

		return dataMapper.map(createdStockLevelData, StockLevelWsDto.class, fields);
	}

	/**
	 * Request to get return stock level adjustment reasons
	 *
	 * @return list of stock level adjustment reason
	 */
	@RequestMapping(value = "/adjustment-reasons", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Finds all adjustment reasons", response = StockLevelAdjustmentReasonsWsDTO.class)
	public StockLevelAdjustmentReasonsWsDTO getStockLevelAdjustmentReasons()
	{
		final List<StockLevelAdjustmentReason> stockLevelAdjustmentReasons = warehousingStockLevelFacade
				.getStockLevelAdjustmentReasons();
		final StockLevelAdjustmentReasonDataList stockLevelAdjustmentReasonDataList = new StockLevelAdjustmentReasonDataList();
		stockLevelAdjustmentReasonDataList.setReasons(stockLevelAdjustmentReasons);
		return dataMapper.map(stockLevelAdjustmentReasonDataList, StockLevelAdjustmentReasonsWsDTO.class);
	}

	/**
	 * Request to create a {@link de.hybris.platform.warehousing.model.InventoryEventModel} in the system to adjust a specific {@link de.hybris.platform.ordersplitting.model.StockLevelModel}
	 *
	 * @param productCode
	 * 		the product code for which an adjustment is required
	 * @param warehouseCode
	 * 		the warehouse code for which an adjustment is required
	 * @param binCode
	 * 		the bin code of the stock level to adjust (optional)
	 * @param releaseDate
	 * 		the release date of the stock level to adjust (optional)
	 * @param stockLevelAdjustmentsWsDTO
	 * 		list of stock level adjustment to be created
	 * @return created stockLevel
	 */
	@RequestMapping(value = "/product/{productCode}/warehouse/{warehouseCode}/adjustment", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	@ApiOperation(value = "Creates an inventoryEvent to adjust a specific stocklevel", response = StockLevelAdjustmentsWsDTO.class)
	public StockLevelAdjustmentsWsDTO createStockLevelAdjustment(
			@ApiParam(value = "Product Code", required = true) @PathVariable final String productCode,
			@ApiParam(value = "Warehouse Code", required = true) @PathVariable final String warehouseCode,
			@ApiParam(value = "Bin Code") @RequestParam(required = false) final String binCode,
			@ApiParam(value = "Release Date") @RequestParam(required = false) final String releaseDate,
			@ApiParam(value = "List of stockLevel Adjustments", required = true) @RequestBody final StockLevelAdjustmentsWsDTO stockLevelAdjustmentsWsDTO)
			throws WebserviceValidationException
	{
		if (warehousingWarehouseFacade.getWarehouseForCode(warehouseCode).isExternal())
		{
			throw new WebserviceValidationException(
					Localization.getLocalizedString("warehousingwebservices.stocklevels.error.externalwarehouse"));
		}

		final List<StockLevelAdjustmentData> stockLevelAdjustmentDatas = new ArrayList<>();
		stockLevelAdjustmentsWsDTO.getStockLevelAdjustments().stream().forEach(stockLevelAdjustmentWsDto -> {
			validate(stockLevelAdjustmentWsDto, "stockLevelAdjustmentWsDto", stockLevelAdjustmentValidator);
			if (stockLevelAdjustmentWsDto.getReason() != null)
			{
				stockLevelAdjustmentWsDto.setReason(stockLevelAdjustmentWsDto.getReason().toUpperCase());
				validate(new String[] { stockLevelAdjustmentWsDto.getReason() }, "reason", stockLevelAdjustmentReasonValidator);
			}
			stockLevelAdjustmentDatas.add(dataMapper.map(stockLevelAdjustmentWsDto, StockLevelAdjustmentData.class));
		});

		final List<StockLevelAdjustmentData> createdStockLevelAdjustmentsData = warehousingStockLevelFacade
				.createStockLevelAdjustements(productCode, warehouseCode, binCode, releaseDate, stockLevelAdjustmentDatas);

		final List<StockLevelAdjustmentWsDTO> stockLevelAdjustmentWsDTOs = new ArrayList<>();
		createdStockLevelAdjustmentsData.stream().forEach(stockLevelAdjustmentData -> stockLevelAdjustmentWsDTOs
				.add(dataMapper.map(stockLevelAdjustmentData, StockLevelAdjustmentWsDTO.class)));

		final StockLevelAdjustmentsWsDTO returnedStockLevelAdjustmentsWsDTO = new StockLevelAdjustmentsWsDTO();
		returnedStockLevelAdjustmentsWsDTO.setStockLevelAdjustments(stockLevelAdjustmentWsDTOs);
		return returnedStockLevelAdjustmentsWsDTO;
	}
}

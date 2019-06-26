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
package de.hybris.platform.warehousingwebservices.controllers.pointofservice;

import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commercewebservicescommons.dto.store.PointOfServiceWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;
import de.hybris.platform.warehousingfacades.pointofservice.WarehousingPointOfServiceFacade;
import de.hybris.platform.warehousingfacades.storelocator.data.WarehouseCodesDataList;
import de.hybris.platform.warehousingfacades.storelocator.data.WarehouseData;
import de.hybris.platform.warehousingwebservices.controllers.WarehousingBaseController;
import de.hybris.platform.warehousingwebservices.dto.store.WarehouseCodesWsDto;
import de.hybris.platform.warehousingwebservices.dto.store.WarehouseSearchPageWsDto;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

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
 * WebResource exposing {@link de.hybris.platform.warehousingfacades.pointofservice.WarehousingPointOfServiceFacade}
 * http://host:port/warehousingwebservices/pointofservices
 */
@Controller
@RequestMapping(value = "/pointofservices")
@Api(value = "/pointofservices", description = "Point of Service Operations")
public class WarehousingPointOfServicesController extends WarehousingBaseController
{
	@Resource
	private WarehousingPointOfServiceFacade warehousingPointOfServiceFacade;

	@Resource(name = "addressDTOValidator")
	private Validator addressDTOValidator;

	/**
	 * Request to get a point of service by name
	 *
	 * @param name
	 * 		the name of the requested point of service
	 * @param fields
	 * 		defaulted to DEFAULT but can be FULL or BASIC
	 * @return the point of service
	 */
	@RequestMapping(value = "/{name}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Finds one point of service by name", response = PointOfServiceWsDTO.class)
	public PointOfServiceWsDTO getPointOfServiceByName(
			@ApiParam(value = "The name of the point of service to be fetched", required = true) @PathVariable final String name,
			@ApiParam(value = "Fields mapping level") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final PointOfServiceData pos = warehousingPointOfServiceFacade.getPointOfServiceByName(name);
		return dataMapper.map(pos, PointOfServiceWsDTO.class, fields);
	}

	/**
	 * Request to get all warehouses for the given {@link de.hybris.platform.storelocator.model.PointOfServiceModel} in
	 * the system
	 *
	 * @param pointOfServiceName
	 * 		the name of the {@link PointOfServiceData}
	 * @param fields
	 * 		defaulted to DEFAULT but can be FULL or BASIC
	 * @param currentPage
	 * 		number of the current page
	 * @param pageSize
	 * 		number of items in a page
	 * @param sort
	 * 		sorting the results ascending or descending
	 * @return list of warehouses
	 */
	@RequestMapping(value = "/{pointOfServiceName}/warehouses", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Finds a paginated list of warehouses per given point of service", response = WarehouseSearchPageWsDto.class)
	public WarehouseSearchPageWsDto getWarehousesForPointOfService(
			@ApiParam(value = "The name of the point of service", required = true) @PathVariable final String pointOfServiceName,
			@ApiParam(value = "Fields mapping level") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields,
			@ApiParam(value = "Current page") @RequestParam(required = false, defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
			@ApiParam(value = "Page size") @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
			@ApiParam(value = "Sort parameter") @RequestParam(required = false, defaultValue = DEFAULT_SORT) final String sort)
	{
		final PageableData pageableData = createPageable(currentPage, pageSize, sort);
		final SearchPageData<WarehouseData> warehouses = warehousingPointOfServiceFacade
				.getWarehousesForPointOfService(pageableData, pointOfServiceName);

		return dataMapper.map(warehouses, WarehouseSearchPageWsDto.class, fields);
	}

	/**
	 * Request to update a {@link de.hybris.platform.storelocator.model.PointOfServiceModel} in the system
	 * Make sure to pass a valid WarehouseCode to update the Point Of Service with
	 *
	 * @param pointOfServiceName
	 * 		The name of the {@link de.hybris.platform.storelocator.model.PointOfServiceModel}
	 * @param fields
	 * 		defaulted to DEFAULT but can be FULL or BASIC
	 * @param warehouseCodes
	 * 		contains a list of warehouse codes which will be removed from the current point of service
	 * @return updated pointOfService
	 */
	@RequestMapping(value = "/{pointOfServiceName}/warehouses", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiOperation(value = "Updates a point of service with a list of warehouse codes", notes = "It takes one or more valid warehouse code to update the given point of service with", response = PointOfServiceWsDTO.class)
	public PointOfServiceWsDTO updatePointOfServiceWithWarehouses(
			@ApiParam(value = "The WarehouseCodesWsDto that contains a list of valid warehouse codes", required = true) @RequestBody WarehouseCodesWsDto warehouseCodes,
			@ApiParam(value = "The name of the point of service", required = true) @NotNull @PathVariable final String pointOfServiceName,
			@ApiParam(value = "Fields mapping level") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
			throws WebserviceValidationException
	{
		final WarehouseCodesDataList warehouseCodesDataList = dataMapper.map(warehouseCodes, WarehouseCodesDataList.class, fields);
		final PointOfServiceData updatedPointOfService = warehousingPointOfServiceFacade
				.updatePointOfServiceWithWarehouses(pointOfServiceName, warehouseCodesDataList);
		return dataMapper.map(updatedPointOfService, PointOfServiceWsDTO.class, fields);
	}

	/**
	 * Request to delete warehouses from {@link de.hybris.platform.storelocator.model.PointOfServiceModel}.
	 *
	 * @param pointOfServiceName
	 * 		The name of the {@link de.hybris.platform.storelocator.model.PointOfServiceModel}
	 * @param fields
	 * 		defaulted to DEFAULT but can be FULL or BASIC
	 * @param warehouseCode
	 * 		the warehouse code which will be removed from the current point of service
	 * @return updated Point of Service
	 */
	@RequestMapping(value = "/{pointOfServiceName}/warehouses/{warehouseCode}", method = RequestMethod.DELETE, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ResponseBody
	@ApiOperation(value = "Request to delete a warehouse from point of service", response = PointOfServiceWsDTO.class)
	public PointOfServiceWsDTO deleteWarehousesFromPointOfService(
			@ApiParam(value = "The name of the point of service", required = true) @NotNull @PathVariable final String pointOfServiceName,
			@ApiParam(value = "The code of the warehouse to be deleted from the point of service", required = true) @NotNull @PathVariable final String warehouseCode,
			@ApiParam(value = "Fields mapping level") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
			throws WebserviceValidationException
	{
		final PointOfServiceData updatedPointOfService = warehousingPointOfServiceFacade
				.deleteWarehouseFromPointOfService(pointOfServiceName, warehouseCode);
		return dataMapper.map(updatedPointOfService, PointOfServiceWsDTO.class, fields);
	}

	/**
	 * Request to update a {@link de.hybris.platform.storelocator.model.PointOfServiceModel} in the system
	 * Make sure to pass a valid WarehouseCode to update the Point Of Service with
	 *
	 * @param fields
	 * 		defaulted to DEFAULT but can be FULL or BASIC
	 * @param pointOfServiceName
	 * 		The name of the {@link de.hybris.platform.storelocator.model.PointOfServiceModel}  that we are trying to update
	 * @param address
	 * 		the address to update the POS with
	 * @return updated pointOfService
	 */
	@RequestMapping(value = "/{pointOfServiceName}/address", method = RequestMethod.PUT, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiOperation(value = "Updates point of service with an address", response = PointOfServiceWsDTO.class)
	public PointOfServiceWsDTO updatePointOfServiceWithAddress(
			@ApiParam(value = "The AddressWsDTO object to update the point of service with", required = true) @RequestBody final AddressWsDTO address,
			@ApiParam(value = "The name of the point of service", required = true) @NotNull @PathVariable final String pointOfServiceName,
			@ApiParam(value = "Fields mapping level") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
			throws WebserviceValidationException
	{
		validate(address, "address", getAddressDTOValidator());
		final AddressData addressData = dataMapper.map(address, AddressData.class, fields);
		final PointOfServiceData updatedPointOfService = warehousingPointOfServiceFacade
				.updatePointOfServiceWithAddress(pointOfServiceName, addressData);
		return dataMapper.map(updatedPointOfService, PointOfServiceWsDTO.class, fields);
	}

	protected Validator getAddressDTOValidator()
	{
		return addressDTOValidator;
	}

	protected void setAddressDTOValidator(final Validator addressDTOValidator)
	{
		this.addressDTOValidator = addressDTOValidator;
	}
}

/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cmsoccaddon.controllers;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.data.AbstractCMSComponentData;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.items.ComponentItemFacade;
import de.hybris.platform.cmsoccaddon.data.ComponentIDListWsDTO;
import de.hybris.platform.cmsoccaddon.data.ComponentListWsDTO;
import de.hybris.platform.cmsoccaddon.data.ComponentWsDTO;
import de.hybris.platform.cmsoccaddon.jaxb.adapters.ComponentAdapterUtil.ComponentAdaptedData;
import de.hybris.platform.cmsoccaddon.jaxb.adapters.ComponentListWsDTOAdapter;
import de.hybris.platform.cmsoccaddon.jaxb.adapters.ComponentListWsDTOAdapter.ListAdaptedComponents;
import de.hybris.platform.cmsoccaddon.jaxb.adapters.ComponentWsDTOAdapter;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.webservicescommons.dto.PaginationWsDTO;
import de.hybris.platform.webservicescommons.dto.SortWsDTO;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.pagination.WebPaginationUtils;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


/**
 * Default Controller for CMS Component. This controller is used for all CMS components that don\"t have a specific
 * controller to handle them.
 */
@Controller
@RequestMapping(value = "/{baseSiteId}/cms")
@ApiVersion("v2")
@Api(tags = "Component")
public class ComponentController
{
	public static final String DEFAULT_CURRENT_PAGE = "0";
	public static final String DEFAULT_PAGE_SIZE = "10";
	private static Logger LOGGER = LoggerFactory.getLogger(ComponentController.class);

	@Resource(name = "componentItemFacade")
	private ComponentItemFacade componentItemFacade;

	@Resource(name = "cmsDataMapper")
	private DataMapper dataMapper;

	@Resource(name = "webPaginationUtils")
	private WebPaginationUtils webPaginationUtils;

	@RequestMapping(value = "/components/{componentId}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Get component data", notes = "Given a component identifier, return cms component data.")
	@ApiBaseSiteIdParam
	public ComponentAdaptedData getComponentById(
			@ApiParam(value = "Component identifier", required = true) @PathVariable final String componentId,
			@ApiParam(value = "Catalog code") @RequestParam(required = false) final String catalogCode,
			@ApiParam(value = "Product code") @RequestParam(required = false) final String productCode,
			@ApiParam(value = "Category code") @RequestParam(required = false) final String categoryCode,
			@ApiParam(value = "Response configuration (list of fields, which should be returned in response)", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(defaultValue = "DEFAULT") final String fields)
			throws CMSItemNotFoundException, WebserviceValidationException
	{
		try
		{
			final AbstractCMSComponentData componentData = getComponentItemFacade().getComponentById(componentId, categoryCode,
					productCode, catalogCode);
			final ComponentWsDTO componentDTO = getDataMapper().map(componentData, ComponentWsDTO.class, fields);

			final ComponentWsDTOAdapter adapter = new ComponentWsDTOAdapter();
			return adapter.marshal(componentDTO);
		}
		catch (final ValidationException e)
		{
			LOGGER.info("Validation exception", e);
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	@RequestMapping(value = "/components", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "Get components' data by id given in body", notes = "Given a list of component identifiers in body, return cms component data.")
	@ApiBaseSiteIdParam
	@SuppressWarnings("squid:S00107")
	public ListAdaptedComponents getComponentByIdList(
			@ApiParam(value = "List of Component identifiers", required = true) @RequestBody final ComponentIDListWsDTO componentIdList,
			@ApiParam(value = "Catalog code") @RequestParam(required = false) final String catalogCode,
			@ApiParam(value = "Product code") @RequestParam(required = false) final String productCode,
			@ApiParam(value = "Category code") @RequestParam(required = false) final String categoryCode,
			@ApiParam(value = "Response configuration (list of fields, which should be returned in response)", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(defaultValue = "DEFAULT") final String fields,
			@ApiParam(value = "Optional pagination parameter. Default value 0.") @RequestParam(required = false, defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
			@ApiParam(value = "Optional pagination parameter. Default value 10.") @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
			@ApiParam(value = "Optional sort criterion. No default value.") @RequestParam(required = false) final String sort)
			throws WebserviceValidationException
	{
		// Validate for componentIdList, if componentIdList from body is null or empty, throw an IllegalArgumentException
		if (componentIdList == null || CollectionUtils.isEmpty(componentIdList.getIdList()))
		{
			throw new IllegalArgumentException("idList in the request body should contain component ID(s)");
		}

		try
		{
			// Creates a SearchPageData object contains requested pagination and sorting information
			final SearchPageData<AbstractCMSComponentData> searchPageDataInput = getWebPaginationUtils().buildSearchPageData(sort,
					currentPage, pageSize, true);

			// Get search result in a SearchPageData object contains search results with applied pagination and sorting information
			final SearchPageData<AbstractCMSComponentData> searchPageDataResult = getComponentItemFacade()
					.getComponentsByIds(componentIdList.getIdList(), categoryCode, productCode, catalogCode, searchPageDataInput);

			// Map the results into a ComponentListWsDTO which is an intermediate WsDTO
			final ComponentListWsDTO componentListWsDTO = new ComponentListWsDTO();
			final List<ComponentWsDTO> componentWsDTOList = getDataMapper().mapAsList(searchPageDataResult.getResults(),
					ComponentWsDTO.class, fields);
			componentListWsDTO.setComponent(componentWsDTOList);

			// Marshal the ComponentListWsDTO into ListAdaptedComponents
			final ComponentListWsDTOAdapter adapter = new ComponentListWsDTOAdapter();
			final ListAdaptedComponents listAdaptedComponent = adapter.marshal(componentListWsDTO);

			// Convert pagination and sorting data into ListAdaptedComponents' WsDTO
			final PaginationWsDTO paginationWsDTO = getWebPaginationUtils()
					.buildPaginationWsDto(searchPageDataResult.getPagination());
			final List<SortWsDTO> sortWsDTOList = getWebPaginationUtils().buildSortWsDto(searchPageDataResult.getSorts());
			listAdaptedComponent.setPagination(paginationWsDTO);
			listAdaptedComponent.setSorts(sortWsDTOList);

			return listAdaptedComponent;
		}
		catch (final ValidationException e)
		{
			LOGGER.info("Validation exception", e);
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	protected ComponentItemFacade getComponentItemFacade()
	{
		return componentItemFacade;
	}

	public void setComponentItemFacade(ComponentItemFacade componentItemFacade)
	{
		this.componentItemFacade = componentItemFacade;
	}

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}

	public void setDataMapper(final DataMapper dataMapper)
	{
		this.dataMapper = dataMapper;
	}

	protected WebPaginationUtils getWebPaginationUtils()
	{
		return webPaginationUtils;
	}

	public void setWebPaginationUtils(final WebPaginationUtils webPaginationUtils)
	{
		this.webPaginationUtils = webPaginationUtils;
	}


}

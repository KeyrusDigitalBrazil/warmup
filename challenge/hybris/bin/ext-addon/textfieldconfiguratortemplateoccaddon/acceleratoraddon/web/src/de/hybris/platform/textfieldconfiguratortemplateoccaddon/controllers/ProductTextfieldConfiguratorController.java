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
package de.hybris.platform.textfieldconfiguratortemplateoccaddon.controllers;

import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.catalog.enums.ProductInfoStatus;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commercewebservicescommons.dto.order.CartModificationWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.ConfigurationInfoListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.ConfigurationInfoWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderEntryWsDTO;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdUserIdAndCartIdParam;
import de.hybris.platform.ycommercewebservices.v2.controller.BaseCommerceController;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


@Controller
@Api(tags = "Textfield Conifgurator Products")
@RequestMapping(value = "/{baseSiteId}")
public class ProductTextfieldConfiguratorController extends BaseCommerceController
{
	public static final String TEXTFIELDCONFIGURATOR_TYPE = "textfield";
	public static final String PAGE_LABEL = "configure" + TEXTFIELDCONFIGURATOR_TYPE;
	protected static final String DEFAULT_FIELD_SET = FieldSetLevelHelper.FULL_LEVEL;
	private static final Logger LOG = Logger.getLogger(ProductTextfieldConfiguratorController.class);
	@Resource(name = "cwsProductFacade")
	private ProductFacade productFacade;

	@RequestMapping(value = "/products/{productCode}/configurator/" + TEXTFIELDCONFIGURATOR_TYPE, method =
	{ RequestMethod.GET })
	@ResponseBody
	@ApiOperation(value = "Get textfield configuration", notes = "Returns list of textfield configuration elements.")
	public ConfigurationInfoListWsDTO getConfigurationByProductCode(
			@ApiParam(value = "Product identifier", required = true) @PathVariable final String productCode)
	{
		final List<ConfigurationInfoData> configInfoList = this.getProductFacade().getConfiguratorSettingsForCode(productCode);

		return mapToConfigurationInfoListWs(configInfoList);
	}

	@RequestMapping(value = "/users/{userId}/carts/{cartId}/entries/configurator/"
			+ TEXTFIELDCONFIGURATOR_TYPE, method = RequestMethod.POST, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseBody
	@ApiOperation(value = "Adds a product to the cart.", notes = "Adds a textfield configurator product to the cart.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public CartModificationWsDTO addCartEntry(@ApiParam(value = "Base site identifier.") @PathVariable final String baseSiteId,
			@ApiParam(value = "Request body parameter (DTO in xml or json format) which contains details like : "
					+ "product code (product.code), quantity of product (quantity), pickup store name (deliveryPointOfService.name)", required = true) @RequestBody final OrderEntryWsDTO entry,
			@ApiParam(value = "Response configuration (list of fields, which should be returned in response)", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
			throws CommerceCartModificationException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug(
					"addCartEntry: " + logParam("code", entry.getProduct().getCode()) + ", " + logParam("qty", entry.getQuantity()));
		}

		final CartModificationData cartModification = this.getCartFacade().addToCart(entry.getProduct().getCode(),
				entry.getQuantity());

		if (cartModification == null)
		{
			throw new CommerceCartModificationException("Null cart modification");
		}
		if (cartModification.getQuantityAdded() > 0)
		{
			this.getCartFacade().updateCartEntry(
					this.enrichOrderEntryWithConfigurationData(entry.getConfigurationInfos(), cartModification.getEntry()));
		}

		return this.getDataMapper().map(cartModification, CartModificationWsDTO.class, fields);
	}

	@RequestMapping(value = "/users/{userId}/carts/{cartId}/entries/{entryNumber}/configurator/"
			+ TEXTFIELDCONFIGURATOR_TYPE, method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Get configuration of cart entry.", notes = "Get the textfield configurtion for a cart entry.")
	public ConfigurationInfoListWsDTO getConfigurationInEntry(
			@ApiParam(value = "Cart entry number.") @PathVariable final int entryNumber) throws CommerceCartModificationException
	{
		final CartData cart = getCartFacade().getSessionCart();
		final OrderEntryData entry = getAbstractOrderEntry(entryNumber, cart);

		return mapToConfigurationInfoListWs(entry.getConfigurationInfos());
	}


	@RequestMapping(value = "/users/{userId}/carts/{cartId}/entries/{entryNumber}/configurator/"
			+ TEXTFIELDCONFIGURATOR_TYPE, method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "Update configuration of cart entry.", notes = "Update the textfield configurtion for a cart entry.")
	public CartModificationWsDTO getConfigurationInEntry(
			@ApiParam(value = "Cart entry number.") @PathVariable final int entryNumber,
			@ApiParam(value = "Request body parameter (DTO in xml or json format) which contains details like : "
					+ "product code (product.code), quantity of product (quantity), pickup store name (deliveryPointOfService.name)", required = true) @RequestBody final ConfigurationInfoListWsDTO configInfoList)
			throws CommerceCartModificationException
	{
		final CartData cart = getCartFacade().getSessionCart();
		final OrderEntryData entry = getAbstractOrderEntry(entryNumber, cart);
		final CartModificationData cartModification = getCartFacade()
				.updateCartEntry(enrichOrderEntryWithConfigurationData(configInfoList.getConfigurationInfos(), entry));

		return this.getDataMapper().map(cartModification, CartModificationWsDTO.class, "DEFAULT");
	}


	protected OrderEntryData enrichOrderEntryWithConfigurationData(final List<ConfigurationInfoWsDTO> configInfoListWsDto,
			final OrderEntryData orderEntry)
	{
		orderEntry.setConfigurationInfos(mapToConfigurationInfoList(configInfoListWsDto));

		return orderEntry;
	}

	protected ConfigurationInfoListWsDTO mapToConfigurationInfoListWs(final List<ConfigurationInfoData> configInfoList)
	{
		final List<ConfigurationInfoWsDTO> configInfoWsList = configInfoList.stream()
				.map(infoItem -> this.getDataMapper().map(infoItem, ConfigurationInfoWsDTO.class)).collect(Collectors.toList());

		final ConfigurationInfoListWsDTO configInfoListWsDto = new ConfigurationInfoListWsDTO();
		configInfoListWsDto.setConfigurationInfos(configInfoWsList);
		return configInfoListWsDto;
	}

	protected List<ConfigurationInfoData> mapToConfigurationInfoList(final List<ConfigurationInfoWsDTO> configInfoListWsDto)
	{
		return configInfoListWsDto.stream().map(infoData -> mapConfigInfo(infoData)).collect(Collectors.toList());
	}

	protected ConfigurationInfoData mapConfigInfo(final ConfigurationInfoWsDTO infoData)
	{
		final ConfigurationInfoData configInfoData = new ConfigurationInfoData();

		configInfoData.setConfigurationLabel(infoData.getConfigurationLabel());
		configInfoData.setConfigurationValue(infoData.getConfigurationValue());
		configInfoData.setConfiguratorType(ConfiguratorType.valueOf(infoData.getConfiguratorType()));
		configInfoData.setStatus(ProductInfoStatus.valueOf(infoData.getStatus()));

		return configInfoData;
	}


	protected OrderEntryData getAbstractOrderEntry(final int entryNumber, final AbstractOrderData abstractOrder)
			throws CommerceCartModificationException
	{
		final List<OrderEntryData> entries = abstractOrder.getEntries();
		if (entries == null)
		{
			throw new CommerceCartModificationException("Cart is empty");
		}
		try
		{
			return entries.stream().filter(e -> e != null).filter(e -> e.getEntryNumber() == entryNumber).findAny().get();
		}
		catch (final NoSuchElementException e)
		{
			throw new CommerceCartModificationException("Cart entry #" + entryNumber + " does not exist", e);
		}
	}

	public ProductFacade getProductFacade()
	{
		return productFacade;
	}

	@Autowired
	public void setProductFacade(final ProductFacade productFacade)
	{
		this.productFacade = productFacade;
	}
}

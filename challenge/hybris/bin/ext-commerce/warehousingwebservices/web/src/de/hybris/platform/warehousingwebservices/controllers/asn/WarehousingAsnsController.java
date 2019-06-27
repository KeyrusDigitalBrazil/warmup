/*
 * [y] hybris Platform
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.warehousingwebservices.controllers.asn;

import de.hybris.platform.warehousingfacades.asn.WarehousingAsnFacade;
import de.hybris.platform.warehousingfacades.asn.data.AsnData;
import de.hybris.platform.warehousingwebservices.controllers.WarehousingBaseController;
import de.hybris.platform.warehousingwebservices.dto.asn.AsnWsDTO;
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
 * WebResource exposing {@link WarehousingAsnFacade} http://host:port/warehousingwebservices/asns
 */
@Controller
@RequestMapping(value = "/asns")
@Api(value = "/asns", description = "Advanced Shipping Notice's Operations")
public class WarehousingAsnsController extends WarehousingBaseController
{
	@Resource
	private WarehousingAsnFacade warehousingAsnFacade;
	@Resource
	private Validator asnValidator;
	@Resource
	private Validator asnEntryValidator;

	/**
	 * Request to create a {@link de.hybris.platform.warehousing.model.AdvancedShippingNoticeModel} in the system
	 *
	 * @param fields
	 * 		defaulted to DEFAULT but can be FULL or BASIC
	 * @param asnWsDTO
	 * 		object representing {@link AsnWsDTO}
	 * @return created {@link AsnData}
	 * @throws {@link
	 * 		WebserviceValidationException}
	 */
	@RequestMapping(method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	@ApiOperation(value = "Creates an advanced shipping notice", response = AsnWsDTO.class)
	public AsnWsDTO createAsn(
			@ApiParam(value = "AsnWsDTO containing information about the asn to be created", required = true) @RequestBody final AsnWsDTO asnWsDTO,
			@ApiParam(value = "Fields mapping level") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
			throws WebserviceValidationException
	{
		validate(asnWsDTO, "asnWsDTO", asnValidator);
		asnWsDTO.getAsnEntries().forEach(entry -> validate(entry, "asnEntryWsDTO", asnEntryValidator));
		final AsnData asnData = dataMapper.map(asnWsDTO, AsnData.class);
		final AsnData createdAsnData = warehousingAsnFacade.createAsn(asnData);

		return dataMapper.map(createdAsnData, AsnWsDTO.class, fields);
	}


	/**
	 * Request to confirm receipt of {@link AsnWsDTO}
	 *
	 * @param internalId
	 * 		{@link de.hybris.platform.warehousing.model.AdvancedShippingNoticeModel#INTERNALID}
	 * 		for the {@link de.hybris.platform.warehousing.model.AdvancedShippingNoticeModel} to be confirmed
	 */
	@RequestMapping(value = "{internalId}/confirm-receipt", method = RequestMethod.POST, consumes = {
			MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiOperation(value = "Confirms the receipt of an advanced shipping notice", response = AsnWsDTO.class)
	public AsnWsDTO confirmAsnReceipt(
			@ApiParam(value = "Internal Id for the advanced shipping notice to be confirmed", required = true) @PathVariable @NotNull final String internalId,
			@ApiParam(value = "Fields mapping level") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final AsnData updatedAsnData = warehousingAsnFacade.confirmAsnReceipt(internalId);
		return dataMapper.map(updatedAsnData, AsnWsDTO.class, fields);
	}



	/**
	 * Request to cancel an {@link AsnWsDTO}
	 *
	 * @param internalId
	 * 		{@link de.hybris.platform.warehousing.model.AdvancedShippingNoticeModel#INTERNALID}
	 * 		for the {@link de.hybris.platform.warehousing.model.AdvancedShippingNoticeModel} to be cancelled
	 */
	@RequestMapping(value = "{internalId}/cancel", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.ACCEPTED)
	@ResponseBody
	@ApiOperation(value = "Cancels an advanced shipping notice", response = AsnWsDTO.class)
	public AsnWsDTO cancelAsn(
			@ApiParam(value = "Internal Id for the advanced shipping notice to be cancelled", required = true) @PathVariable @NotNull final String internalId,
			@ApiParam(value = "Fields mapping level") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final AsnData cancelledAsn = warehousingAsnFacade.cancelAsn(internalId);
		return dataMapper.map(cancelledAsn, AsnWsDTO.class, fields);
	}
}

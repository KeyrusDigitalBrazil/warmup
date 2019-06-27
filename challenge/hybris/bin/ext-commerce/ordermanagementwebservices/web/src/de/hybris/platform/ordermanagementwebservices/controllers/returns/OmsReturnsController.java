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
package de.hybris.platform.ordermanagementwebservices.controllers.returns;

import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.basecommerce.enums.RefundReason;
import de.hybris.platform.basecommerce.enums.ReturnAction;
import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.ordermanagementfacades.constants.OrdermanagementfacadesConstants;
import de.hybris.platform.ordermanagementfacades.order.data.CancelReasonDataList;
import de.hybris.platform.ordermanagementfacades.order.data.ReturnStatusDataList;
import de.hybris.platform.ordermanagementfacades.returns.OmsReturnFacade;
import de.hybris.platform.ordermanagementfacades.returns.data.CancelReturnRequestData;
import de.hybris.platform.ordermanagementfacades.returns.data.RefundReasonDataList;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnActionDataList;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnEntryData;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnRequestData;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnRequestModificationData;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnRequestModificationWsDTO;
import de.hybris.platform.ordermanagementwebservices.controllers.OmsBaseController;
import de.hybris.platform.ordermanagementwebservices.dto.order.CancelReasonListWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.returns.CancelReturnRequestWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.returns.RefundReasonListWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.returns.ReturnActionListWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.returns.ReturnEntrySearchPageWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.returns.ReturnRequestWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.returns.ReturnSearchPageWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.returns.ReturnStatusListWsDTO;
import de.hybris.platform.util.localization.Localization;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
 * WebResource exposing {@link de.hybris.platform.ordermanagementfacades.returns.OmsReturnFacade}
 * http://host:port/ordermanagementwebservices/returns
 */
@Controller
@RequestMapping(value = "/returns")
@Api(value = "/returns", description = "Returns Operations")
public class OmsReturnsController extends OmsBaseController
{
	@Resource
	private OmsReturnFacade omsReturnFacade;

	@Resource(name = "cancelReturnRequestValidator")
	private Validator cancelReturnRequestValidator;

	@Resource(name = "returnRequestValidator")
	private Validator returnRequestValidator;

	@Resource(name = "returnEntryValidator")
	private Validator returnEntryValidator;

	@Resource(name = "returnActionValidator")
	private Validator returnActionValidator;

	@Resource(name = "refundReasonValidator")
	private Validator refundReasonValidator;

	@Resource(name = "cancelReasonValidator")
	private Validator cancelReasonValidator;

	@Resource(name = "priceValidator")
	private Validator priceValidator;

	/**
	 * Request to get paged returns in the system
	 *
	 * @param fields
	 * 		defaulted to DEFAULT but can be FULL or BASIC
	 * @param currentPage
	 * 		number of the current page
	 * @param pageSize
	 * 		number of items in a page
	 * @param sort
	 * 		sorting the results ascending or descending
	 * @return list of returns
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Finds a paginated list of all the returns in the system", response = ReturnSearchPageWsDTO.class)
	public ReturnSearchPageWsDTO getReturns(
			@ApiParam(value = "Fields mapping level") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields,
			@ApiParam(value = "Current page number") @RequestParam(required = false, defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
			@ApiParam(value = "Number of items to be displayed per page") @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
			@ApiParam(value = "Method in which to sort results") @RequestParam(required = false, defaultValue = DEFAULT_SORT) final String sort)
	{

		final PageableData pageableData = createPageable(currentPage, pageSize, sort);
		final SearchPageData<ReturnRequestData> returns = omsReturnFacade.getReturns(pageableData);
		return dataMapper.map(returns, ReturnSearchPageWsDTO.class, fields);
	}

	/**
	 * Request to get paged returns with certain return status(s)
	 *
	 * @param returnStatuses
	 * 		a list of valid return statuses separated by ","
	 * @param fields
	 * 		defaulted to DEFAULT but can be FULL or BASIC
	 * @param currentPage
	 * 		number of the current page
	 * @param pageSize
	 * 		number of items in a page
	 * @param sort
	 * 		sorting the results ascending or descending
	 * @return list of returns that complies with conditions above
	 * @throws de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException
	 * 		in case of passing a wrong return status validation exception will be thrown
	 */
	@RequestMapping(value = "status/{returnStatuses}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Finds a paginated list of returns with one of a set of desired statuses", response = ReturnSearchPageWsDTO.class)
	public ReturnSearchPageWsDTO getReturnsByStatus(
			@ApiParam(value = "Set of desired return statuses", required = true) @PathVariable final String returnStatuses,
			@ApiParam(value = "Fields mapping level") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields,
			@ApiParam(value = "Current page number") @RequestParam(required = false, defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
			@ApiParam(value = "Number of items to be displayed per page") @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
			@ApiParam(value = "Method in which to sort results") @RequestParam(required = false, defaultValue = DEFAULT_SORT) final String sort)
			throws WebserviceValidationException
	{
		final Set<ReturnStatus> statusSet = extractReturnStatuses(returnStatuses);
		final PageableData pageableData = createPageable(currentPage, pageSize, sort);
		final SearchPageData<ReturnRequestData> returns = omsReturnFacade.getReturnsByStatuses(pageableData, statusSet);
		return dataMapper.map(returns, ReturnSearchPageWsDTO.class, fields);
	}

	/**
	 * Request to get ReturnRequest by its code
	 *
	 * @param code
	 * 		the code of the requested return
	 * @param fields
	 * 		defaulted to DEFAULT but can be FULL or BASIC
	 * @return the requested returnRequest that complies with conditions above
	 */
	@RequestMapping(value = "{code}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Finds a specific return request by its code", response = ReturnRequestWsDTO.class)
	public ReturnRequestWsDTO getReturnForReturnCode(
			@ApiParam(value = "Return request code", required = true) @PathVariable final String code,
			@ApiParam(value = "Fields mapping level") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final ReturnRequestData returns = omsReturnFacade.getReturnForReturnCode(code);
		return dataMapper.map(returns, ReturnRequestWsDTO.class, fields);
	}

	/**
	 * Request to update ReturnRequest by its code
	 *
	 * @param code
	 * 		the code of the requested return
	 * @param fields
	 * 		defaulted to DEFAULT but can be FULL or BASIC
	 * @return the requested returnRequest that complies with conditions above
	 */
	@RequestMapping(value = "{code}", method = RequestMethod.PUT, produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiOperation(value = "Updates a return request corresponding to the given code with the modifications provided", response = ReturnRequestWsDTO.class)
	public ReturnRequestWsDTO updateReturnByReturnCode(
			@ApiParam(value = "The ReturnRequestModificationWsDTO containing the desired modifications to be applied", required = true) @NotNull @RequestBody final ReturnRequestModificationWsDTO returnRequestModificationWsDTO,
			@ApiParam(value = "Return request code", required = true) @NotNull @PathVariable final String code,
			@ApiParam(value = "Fields mapping level") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final ReturnRequestModificationData returnRequestModificationData = dataMapper
				.map(returnRequestModificationWsDTO, ReturnRequestModificationData.class, fields);
		final ReturnRequestData returnRequest = omsReturnFacade.updateReturnRequest(code, returnRequestModificationData);
		return dataMapper.map(returnRequest, ReturnRequestWsDTO.class, fields);
	}


	/**
	 * Request to get all {@link ReturnStatus} in the system
	 *
	 * @return list of return statuses
	 */
	@RequestMapping(value = "/statuses", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Finds a list of all return possible return statuses", response = ReturnStatusListWsDTO.class)
	public ReturnStatusListWsDTO getReturnStatuses()
	{
		final List<ReturnStatus> returnStatuses = omsReturnFacade.getReturnStatuses();
		final ReturnStatusDataList returnStatusList = new ReturnStatusDataList();
		returnStatusList.setStatuses(returnStatuses);
		return dataMapper.map(returnStatusList, ReturnStatusListWsDTO.class);
	}

	/**
	 * Request to get returnEntries for the given {@link de.hybris.platform.returns.model.ReturnRequestModel#CODE}
	 *
	 * @param code
	 * 		return's code for the requested return entries
	 * @param fields
	 * 		defaulted to DEFAULT but can be FULL or BASIC
	 * @param currentPage
	 * 		number of the current page
	 * @param pageSize
	 * 		number of items in a page
	 * @param sort
	 * 		sorting the results ascending or descending
	 * @return the list of returnEntries fulfilling the above conditions
	 */
	@RequestMapping(value = "/{code}/entries", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Finds a paginated list of return entries for a return request corresponding to the given code", response = ReturnEntrySearchPageWsDTO.class)
	public ReturnEntrySearchPageWsDTO getReturnEntriesForOrderCode(
			@ApiParam(value = "Return request code", required = true) @PathVariable final String code,
			@ApiParam(value = "Fields mapping level") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields,
			@ApiParam(value = "Current page number") @RequestParam(required = false, defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
			@ApiParam(value = "Number of items to display per page") @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
			@ApiParam(value = "Method in which to sort results") @RequestParam(required = false, defaultValue = DEFAULT_SORT) final String sort)
	{
		final PageableData pageableData = createPageable(currentPage, pageSize, sort);
		final SearchPageData<ReturnEntryData> returnEntrySearchPageData = omsReturnFacade
				.getReturnEntriesForReturnCode(code, pageableData);
		return dataMapper.map(returnEntrySearchPageData, ReturnEntrySearchPageWsDTO.class, fields);
	}

	/**
	 * Request to create return in the system
	 *
	 * @param fields
	 * 		defaulted to DEFAULT but can be FULL or BASIC
	 * @param returnRequestWsDTO
	 * 		object representing {@link ReturnRequestWsDTO}
	 * @return created return
	 */
	@RequestMapping(method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	@ApiOperation(value = "Creates a return request", response = ReturnRequestWsDTO.class)
	public ReturnRequestWsDTO createReturnRequest(
			@ApiParam(value = "The ReturnRequestWsDTO holding all required information to create a return request", required = true) @RequestBody final ReturnRequestWsDTO returnRequestWsDTO,
			@ApiParam(value = "Fields mapping level") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		validate(returnRequestWsDTO, "returnRequestWsDTO", returnRequestValidator);
		final Set<Integer> entryNumbers = new HashSet<>();
		returnRequestWsDTO.getReturnEntries().forEach(returnEntryWsDTO -> {
			validate(returnEntryWsDTO, "returnEntryWsDTO", returnEntryValidator);
			validate(returnEntryWsDTO.getRefundAmount(), "PriceWsDTO", priceValidator);
			validate(new String[] { returnEntryWsDTO.getAction() }, "action", returnActionValidator);
			validate(new String[] { returnEntryWsDTO.getRefundReason() }, "refundReason", refundReasonValidator);
			if (entryNumbers.contains(returnEntryWsDTO.getOrderEntry().getEntryNumber()))
			{
				throw new IllegalArgumentException(
						String.format(Localization.getLocalizedString("ordermanagementwebservices.returns.error.duplicateorderentry"),
								returnEntryWsDTO.getOrderEntry().getEntryNumber()));
			}
			entryNumbers.add(returnEntryWsDTO.getOrderEntry().getEntryNumber());
		});
		final ReturnRequestData returnRequestData = dataMapper.map(returnRequestWsDTO, ReturnRequestData.class);
		final ReturnRequestData createdReturnRequestData = omsReturnFacade.createReturnRequest(returnRequestData);

		return dataMapper.map(createdReturnRequestData, ReturnRequestWsDTO.class);
	}

	/**
	 * Request to get return cancellation reasons
	 *
	 * @return list of cancel reasons
	 */
	@RequestMapping(value = "/cancel-reasons", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Finds a list of all possible return cancellation reasons", response = CancelReasonListWsDTO.class)
	public CancelReasonListWsDTO getReturnsCancellationReasons()
	{
		final List<CancelReason> cancelReasons = omsReturnFacade.getCancelReasons();
		final CancelReasonDataList cancelReasonList = new CancelReasonDataList();
		cancelReasonList.setReasons(cancelReasons);
		return dataMapper.map(cancelReasonList, CancelReasonListWsDTO.class);
	}

	/**
	 * Request to get refund reasons
	 *
	 * @return list of refund reasons
	 */
	@RequestMapping(value = "/refund-reasons", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Finds a list of all possible refund reasons", response = RefundReasonListWsDTO.class)
	public RefundReasonListWsDTO getRefundReasons()
	{
		final List<RefundReason> refundReasons = omsReturnFacade.getRefundReasons();
		final RefundReasonDataList refundReasonList = new RefundReasonDataList();
		refundReasonList.setRefundReasons(refundReasons);
		return dataMapper.map(refundReasonList, RefundReasonListWsDTO.class);
	}

	/**
	 * Request to get return actions
	 *
	 * @return list of return actions
	 */
	@RequestMapping(value = "/actions", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Finds a list of all possble return actions", response = ReturnActionListWsDTO.class)
	public ReturnActionListWsDTO getReturnActions()
	{
		final List<ReturnAction> returnActions = omsReturnFacade.getReturnActions();
		final ReturnActionDataList returnActionList = new ReturnActionDataList();
		returnActionList.setReturnActions(returnActions);
		return dataMapper.map(returnActionList, ReturnActionListWsDTO.class);
	}

	/**
	 * Request to approve Return Request
	 *
	 * @param code
	 * 		code for the requested returnRequest
	 */
	@RequestMapping(value = "{code}/approve", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Approves a return request corresponding to the given code")
	public void approveReturnRequest(@ApiParam(value = "Return request code", required = true) @PathVariable final String code)
	{
		omsReturnFacade.approveReturnRequest(code);
	}

	/**
	 * Request to cancel a {@link de.hybris.platform.returns.model.ReturnRequestModel}.
	 *
	 * @param cancelReturnRequestWsDTO
	 * 		contains information about the cancellation of the return request.
	 * @throws WebserviceValidationException
	 * 		in case the request body has erroneous fields.
	 */
	@RequestMapping(value = "/cancel", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Cancels a return request")
	public void cancelReturnRequest(
			@ApiParam(value = "The CancelReturnRequestWsDTO containing information about the return request cancellation", required = true) @RequestBody final CancelReturnRequestWsDTO cancelReturnRequestWsDTO)
			throws WebserviceValidationException
	{
		validate(cancelReturnRequestWsDTO, "cancelReturnRequestWsDto", cancelReturnRequestValidator);
		validate(new String[] { cancelReturnRequestWsDTO.getCancelReason() }, "cancelReason", cancelReasonValidator);

		final CancelReturnRequestData cancelReturnRequestData = dataMapper
				.map(cancelReturnRequestWsDTO, CancelReturnRequestData.class);
		omsReturnFacade.cancelReturnRequest(cancelReturnRequestData);
	}

	/**
	 * Request to reverse payment manually.
	 *
	 * @param code
	 * 		code for the requested returnRequest
	 * @deprecated Since 6.7. Use {@link OmsReturnsController#manuallyReversePayment(String)}
	 */
	@Deprecated
	@RequestMapping(value = "{code}/reverse-payment", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Requests manual reversal of the payment for a return")
	public void requestManualPaymentReversalForReturnRequest(
			@ApiParam(value = "Return request code", required = true) @NotNull @PathVariable final String code)
	{
		omsReturnFacade.requestManualPaymentReversalForReturnRequest(code);
	}

	/**
	 * Request to reverse tax manually.
	 *
	 * @param code
	 * 		code for the requested returnRequest
	 * @deprecated Since 6.7. Use {@link OmsReturnsController#manuallyReverseTax(String)}
	 */
	@Deprecated
	@RequestMapping(value = "{code}/reverse-tax", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Requests manual reversal of the taxes for a return")
	public void requestManualTaxReversalForReturnRequest(
			@ApiParam(value = "Return request code", required = true) @NotNull @PathVariable final String code)
	{
		omsReturnFacade.requestManualTaxReversalForReturnRequest(code);
	}

	/**
	 * Request to reverse payment manually.
	 *
	 * @param code
	 * 		code for the requested returnRequest
	 */
	@RequestMapping(value = "{code}/manual/reverse-payment", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Requests manual reversal of the payment for a return")
	public void manuallyReversePayment(
			@ApiParam(value = "Return request code", required = true) @NotNull @PathVariable final String code)
	{
		omsReturnFacade.requestManualPaymentReversalForReturnRequest(code);
	}

	/**
	 * Request to reverse tax manually.
	 *
	 * @param code
	 * 		code for the requested returnRequest
	 */
	@RequestMapping(value = "{code}/manual/reverse-tax", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Requests manual reversal of the taxes for a return")
	public void manuallyReverseTax(
			@ApiParam(value = "Return request code", required = true) @NotNull @PathVariable final String code)
	{
		omsReturnFacade.requestManualTaxReversalForReturnRequest(code);
	}

	/**
	 * Extracts the {@link ReturnStatus} from the provided String representation
	 *
	 * @param statuses
	 * 		a comma-separated string that represent {@link ReturnStatus}
	 * @return the newly extracted {@link Set<ReturnStatus>}
	 */
	protected Set<ReturnStatus> extractReturnStatuses(final String statuses)
	{
		final String statusesStrings[] = statuses.split(OrdermanagementfacadesConstants.OPTIONS_SEPARATOR);

		final Set<ReturnStatus> statusesEnum = new HashSet<>();
		try
		{
			for (final String status : statusesStrings)
			{
				statusesEnum.add(ReturnStatus.valueOf(status));
			}
		}
		catch (final IllegalArgumentException e) //NOSONAR
		{
			throw new WebserviceValidationException(e.getMessage()); //NOSONAR
		}
		return statusesEnum;
	}
}

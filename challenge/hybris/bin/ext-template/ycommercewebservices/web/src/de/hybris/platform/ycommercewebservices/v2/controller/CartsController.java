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
package de.hybris.platform.ycommercewebservices.v2.controller;

import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.order.SaveCartFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.DeliveryModesData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.PromotionResultData;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commercefacades.promotion.CommercePromotionRestrictionFacade;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.voucher.VoucherFacade;
import de.hybris.platform.commercefacades.voucher.exceptions.VoucherOperationException;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.order.CommerceCartMergingException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.commerceservices.promotion.CommercePromotionRestrictionException;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commercewebservicescommons.dto.order.CartListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.CartModificationWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.CartWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.DeliveryModeListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.DeliveryModeWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderEntryListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderEntryWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.PaymentDetailsWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.PromotionResultListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.voucher.VoucherListWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.CartEntryException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.CartException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.LowStockException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.ProductLowStockException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.StockSystemException;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdUserIdAndCartIdParam;
import de.hybris.platform.ycommercewebservices.cart.impl.CommerceWebServicesCartFacade;
import de.hybris.platform.ycommercewebservices.exceptions.InvalidPaymentInfoException;
import de.hybris.platform.ycommercewebservices.exceptions.NoCheckoutCartException;
import de.hybris.platform.ycommercewebservices.exceptions.UnsupportedDeliveryModeException;
import de.hybris.platform.ycommercewebservices.exceptions.UnsupportedRequestException;
import de.hybris.platform.ycommercewebservices.order.data.CartDataList;
import de.hybris.platform.ycommercewebservices.order.data.OrderEntryDataList;
import de.hybris.platform.ycommercewebservices.product.data.PromotionResultDataList;
import de.hybris.platform.ycommercewebservices.request.support.impl.PaymentProviderRequestSupportedStrategy;
import de.hybris.platform.ycommercewebservices.stock.CommerceStockFacade;
import de.hybris.platform.ycommercewebservices.voucher.data.VoucherDataList;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;


@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}/carts")
@CacheControl(directive = CacheControlDirective.NO_CACHE)
@Api(tags = "Carts")
public class CartsController extends BaseCommerceController
{
	private static final Logger LOG = Logger.getLogger(BaseCommerceController.class);

	private static final long DEFAULT_PRODUCT_QUANTITY = 1;

	@Resource(name = "commercePromotionRestrictionFacade")
	private CommercePromotionRestrictionFacade commercePromotionRestrictionFacade;
	@Resource(name = "commerceStockFacade")
	private CommerceStockFacade commerceStockFacade;
	@Resource(name = "customerFacade")
	private CustomerFacade customerFacade;
	@Resource(name = "pointOfServiceValidator")
	private Validator pointOfServiceValidator;
	@Resource(name = "orderEntryCreateValidator")
	private Validator orderEntryCreateValidator;
	@Resource(name = "orderEntryUpdateValidator")
	private Validator orderEntryUpdateValidator;
	@Resource(name = "orderEntryReplaceValidator")
	private Validator orderEntryReplaceValidator;
	@Resource(name = "greaterThanZeroValidator")
	private Validator greaterThanZeroValidator;
	@Resource(name = "paymentProviderRequestSupportedStrategy")
	private PaymentProviderRequestSupportedStrategy paymentProviderRequestSupportedStrategy;
	@Resource(name = "saveCartFacade")
	private SaveCartFacade saveCartFacade;
	@Resource(name = "voucherFacade")
	private VoucherFacade voucherFacade;

	protected static CartModificationData mergeCartModificationData(final CartModificationData cmd1,
			final CartModificationData cmd2)
	{
		if ((cmd1 == null) && (cmd2 == null))
		{
			return new CartModificationData();
		}
		if (cmd1 == null)
		{
			return cmd2;
		}
		if (cmd2 == null)
		{
			return cmd1;
		}
		final CartModificationData cmd = new CartModificationData();
		cmd.setDeliveryModeChanged(Boolean
				.valueOf(Boolean.TRUE.equals(cmd1.getDeliveryModeChanged()) || Boolean.TRUE.equals(cmd2.getDeliveryModeChanged())));
		cmd.setEntry(cmd2.getEntry());
		cmd.setQuantity(cmd2.getQuantity());
		cmd.setQuantityAdded(cmd1.getQuantityAdded() + cmd2.getQuantityAdded());
		cmd.setStatusCode(cmd2.getStatusCode());
		return cmd;
	}

	protected static OrderEntryData getCartEntryForNumber(final CartData cart, final long number) throws CartEntryException //NOSONAR
	{
		final List<OrderEntryData> entries = cart.getEntries();
		if (entries != null && !entries.isEmpty())
		{
			final Integer requestedEntryNumber = Integer.valueOf((int) number);
			for (final OrderEntryData entry : entries)
			{
				if (entry != null && requestedEntryNumber.equals(entry.getEntryNumber()))
				{
					return entry;
				}
			}
		}
		throw new CartEntryException("Entry not found", CartEntryException.NOT_FOUND, String.valueOf(number));
	}

	protected static OrderEntryData getCartEntry(final CartData cart, final String productCode, final String pickupStore)
	{
		for (final OrderEntryData oed : cart.getEntries())
		{
			if (oed.getProduct().getCode().equals(productCode))
			{
				if (pickupStore == null && oed.getDeliveryPointOfService() == null)
				{
					return oed;
				}
				else if (pickupStore != null && oed.getDeliveryPointOfService() != null
						&& pickupStore.equals(oed.getDeliveryPointOfService().getName()))
				{
					return oed;
				}
			}
		}
		return null;
	}

	protected static void validateForAmbiguousPositions(final CartData currentCart, final OrderEntryData currentEntry,
			final String newPickupStore) throws CommerceCartModificationException
	{
		final OrderEntryData entryToBeModified = getCartEntry(currentCart, currentEntry.getProduct().getCode(), newPickupStore);
		if (entryToBeModified != null && !entryToBeModified.getEntryNumber().equals(currentEntry.getEntryNumber()))
		{
			throw new CartEntryException(
					"Ambiguous cart entries! Entry number " + currentEntry.getEntryNumber()
							+ " after change would be the same as entry " + entryToBeModified.getEntryNumber(),
					CartEntryException.AMBIGIOUS_ENTRY, entryToBeModified.getEntryNumber().toString());
		}
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(nickname = "getCarts", value = "Get all customer carts.", notes = "Lists all customer carts.")
	@ApiBaseSiteIdAndUserIdParam
	public CartListWsDTO getCarts(
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields,
			@ApiParam(value = "Optional parameter. If the parameter is provided and its value is true, only saved carts are returned.") @RequestParam(required = false, defaultValue = "false") final boolean savedCartsOnly,
			@ApiParam(value = "Optional pagination parameter in case of savedCartsOnly == true. Default value 0.") @RequestParam(required = false, defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
			@ApiParam(value = "Optional {@link PaginationData} parameter in case of savedCartsOnly == true. Default value 20.") @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
			@ApiParam(value = "Optional sort criterion in case of savedCartsOnly == true. No default value.") @RequestParam(required = false) final String sort)
	{
		if (getUserFacade().isAnonymousUser())
		{
			throw new AccessDeniedException("Access is denied");
		}

		final CartDataList cartDataList = new CartDataList();

		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(currentPage);
		pageableData.setPageSize(pageSize);
		pageableData.setSort(sort);
		final List<CartData> allCarts = new ArrayList<>(
				saveCartFacade.getSavedCartsForCurrentUser(pageableData, null).getResults());
		if (!savedCartsOnly)
		{
			allCarts.addAll(getCartFacade().getCartsForCurrentUser());
		}
		cartDataList.setCarts(allCarts);

		return getDataMapper().map(cartDataList, CartListWsDTO.class, fields);
	}

	@RequestMapping(value = "/{cartId}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(nickname = "getCart", value = "Get a cart with a given identifier.", notes = "Returns the cart with a given identifier.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public CartWsDTO getCart(
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		// CartMatchingFilter sets current cart based on cartId, so we can return cart from the session
		return getDataMapper().map(getSessionCart(), CartWsDTO.class, fields);
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	@ApiOperation(nickname = "createCart", value = "Creates or restore a cart for a user.", notes = "Creates a new cart or restores an anonymous cart as a user's cart (if an old Cart Id is given in the request).")
	@ApiBaseSiteIdAndUserIdParam
	public CartWsDTO createCart(@ApiParam(value = "Anonymous cart GUID.") @RequestParam(required = false) final String oldCartId,
			@ApiParam(value = "The GUID of the user's cart that will be merged with the anonymous cart.") @RequestParam(required = false) final String toMergeCartGuid,
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("createCart");
		}

		String evaluatedToMergeCartGuid = toMergeCartGuid;

		if (StringUtils.isNotEmpty(oldCartId))
		{
			if (getUserFacade().isAnonymousUser())
			{
				throw new CartException("Anonymous user is not allowed to copy cart!");
			}

			if (!isCartAnonymous(oldCartId))
			{
				throw new CartException("Cart is not anonymous", CartException.CANNOT_RESTORE, oldCartId);
			}

			if (StringUtils.isEmpty(evaluatedToMergeCartGuid))
			{
				evaluatedToMergeCartGuid = getSessionCart().getGuid();
			}
			else
			{
				if (!isUserCart(evaluatedToMergeCartGuid))
				{
					throw new CartException("Cart is not current user's cart", CartException.CANNOT_RESTORE, evaluatedToMergeCartGuid);
				}
			}

			try
			{
				getCartFacade().restoreAnonymousCartAndMerge(oldCartId, evaluatedToMergeCartGuid);
				return getDataMapper().map(getSessionCart(), CartWsDTO.class, fields);
			}
			catch (final CommerceCartMergingException e)
			{
				throw new CartException("Couldn't merge carts", CartException.CANNOT_MERGE, e);
			}
			catch (final CommerceCartRestorationException e)
			{
				throw new CartException("Couldn't restore cart", CartException.CANNOT_RESTORE, e);
			}
		}
		else
		{
			if (StringUtils.isNotEmpty(evaluatedToMergeCartGuid))
			{
				if (!isUserCart(evaluatedToMergeCartGuid))
				{
					throw new CartException("Cart is not current user's cart", CartException.CANNOT_RESTORE, evaluatedToMergeCartGuid);
				}

				try
				{
					getCartFacade().restoreSavedCart(evaluatedToMergeCartGuid);
					return getDataMapper().map(getSessionCart(), CartWsDTO.class, fields);
				}
				catch (final CommerceCartRestorationException e)
				{
					throw new CartException("Couldn't restore cart", CartException.CANNOT_RESTORE, oldCartId, e);
				}

			}
			return getDataMapper().map(getSessionCart(), CartWsDTO.class, fields);
		}
	}

	protected boolean isUserCart(final String toMergeCartGuid)
	{
		if (getCartFacade() instanceof CommerceWebServicesCartFacade)
		{
			final CommerceWebServicesCartFacade commerceWebServicesCartFacade = (CommerceWebServicesCartFacade) getCartFacade();
			return commerceWebServicesCartFacade.isCurrentUserCart(toMergeCartGuid);
		}
		return true;
	}

	protected boolean isCartAnonymous(final String cartGuid)
	{
		if (getCartFacade() instanceof CommerceWebServicesCartFacade)
		{
			final CommerceWebServicesCartFacade commerceWebServicesCartFacade = (CommerceWebServicesCartFacade) getCartFacade();
			return commerceWebServicesCartFacade.isAnonymousUserCart(cartGuid);
		}
		return true;
	}

	@RequestMapping(value = "/{cartId}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(nickname = "removeCart", value = "Deletes a cart with a given cart id.", notes = "Deletes a cart with a given cart id.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public void removeCart()
	{
		getCartFacade().removeSessionCart();
	}

	@Secured(
	{ "ROLE_CLIENT", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/{cartId}/email", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(nickname = "replaceCartGuestUser", value = "Assigns an email to the cart.", notes = "Assigns an email to the cart. This step is required to make a guest checkout.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public void replaceCartGuestUser(
			@ApiParam(value = "Email of the guest user. It will be used during the checkout process.", required = true) @RequestParam final String email)
			throws DuplicateUidException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("replaceCartGuestUser: email=" + sanitize(email));
		}

		if (!EmailValidator.getInstance().isValid(email))
		{
			throw new RequestParameterException("Email [" + sanitize(email) + "] is not a valid e-mail address!",
					RequestParameterException.INVALID, "login");
		}

		customerFacade.createGuestUserForAnonymousCheckout(email, "guest");
	}

	@RequestMapping(value = "/{cartId}/entries", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(nickname = "getCartEntries", value = "Get cart entries.", notes = "Returns cart entries.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public OrderEntryListWsDTO getCartEntries(
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("getCartEntries");
		}
		final OrderEntryDataList dataList = new OrderEntryDataList();
		dataList.setOrderEntries(getSessionCart().getEntries());
		return getDataMapper().map(dataList, OrderEntryListWsDTO.class, fields);
	}

	@RequestMapping(value = "/{cartId}/entries", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(hidden = true, value = "Adds a product to the cart.", notes = "Adds a product to the cart.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public CartModificationWsDTO createCartEntry(@ApiParam(value = "Base site identifier.") @PathVariable final String baseSiteId,
			@ApiParam(value = "Code of the product to be added to cart. Product look-up is performed for the current product catalog version.") @RequestParam(required = true) final String code,
			@ApiParam(value = "Quantity of product.") @RequestParam(required = false, defaultValue = "1") final long qty,
			@ApiParam(value = "Name of the store where product will be picked. Set only if want to pick up from a store.") @RequestParam(required = false) final String pickupStore,
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
			throws CommerceCartModificationException, WebserviceValidationException, ProductLowStockException, StockSystemException //NOSONAR
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("createCartEntry: " + logParam("code", code) + ", " + logParam("qty", qty) + ", "
					+ logParam("pickupStore", pickupStore));
		}

		if (StringUtils.isNotEmpty(pickupStore))
		{
			validate(pickupStore, "pickupStore", pointOfServiceValidator);
		}

		return addCartEntryInternal(baseSiteId, code, qty, pickupStore, fields);
	}

	protected CartModificationWsDTO addCartEntryInternal(final String baseSiteId, final String code, final long qty,
			final String pickupStore, final String fields) throws CommerceCartModificationException
	{
		final CartModificationData cartModificationData;
		if (StringUtils.isNotEmpty(pickupStore))
		{
			validateIfProductIsInStockInPOS(baseSiteId, code, pickupStore, null);
			cartModificationData = getCartFacade().addToCart(code, qty, pickupStore);
		}
		else
		{
			validateIfProductIsInStockOnline(baseSiteId, code, null);
			cartModificationData = getCartFacade().addToCart(code, qty);
		}
		return getDataMapper().map(cartModificationData, CartModificationWsDTO.class, fields);
	}

	@RequestMapping(value = "/{cartId}/entries", method = RequestMethod.POST, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseBody
	@ApiOperation(nickname = "createCartEntry", value = "Adds a product to the cart.", notes = "Adds a product to the cart.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public CartModificationWsDTO createCartEntry(@ApiParam(value = "Base site identifier") @PathVariable final String baseSiteId,
			@ApiParam(value = "Request body parameter that contains details such as the product code (product.code), the quantity of product (quantity), and the pickup store name (deliveryPointOfService.name).\n\nThe DTO is in XML or .json format.", required = true) @RequestBody final OrderEntryWsDTO entry,
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
			throws CommerceCartModificationException, WebserviceValidationException, ProductLowStockException, StockSystemException //NOSONAR
	{
		if (entry.getQuantity() == null)
		{
			entry.setQuantity(Long.valueOf(DEFAULT_PRODUCT_QUANTITY));
		}

		validate(entry, "entry", orderEntryCreateValidator);

		final String pickupStore = entry.getDeliveryPointOfService() == null ? null : entry.getDeliveryPointOfService().getName();
		return addCartEntryInternal(baseSiteId, entry.getProduct().getCode(), entry.getQuantity().longValue(), pickupStore, fields);
	}

	@RequestMapping(value = "/{cartId}/entries/{entryNumber}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(nickname = "getCartEntry", value = "Get the details of the cart entries.", notes = "Returns the details of the cart entries.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public OrderEntryWsDTO getCartEntry(
			@ApiParam(value = "The entry number. Each entry in a cart has an entry number. Cart entries are numbered in ascending order, starting with zero (0).", required = true) @PathVariable final long entryNumber,
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("getCartEntry: " + logParam("entryNumber", entryNumber)); //NOSONAR
		}
		final OrderEntryData orderEntry = getCartEntryForNumber(getSessionCart(), entryNumber);
		return getDataMapper().map(orderEntry, OrderEntryWsDTO.class, fields);
	}

	@RequestMapping(value = "/{cartId}/entries/{entryNumber}", method = RequestMethod.PUT)
	@ResponseBody
	@ApiOperation(hidden = true, value = "Set quantity and store details of a cart entry.", notes = "Updates the quantity of a single cart entry and the details of the store where the cart entry will be picked up. Attributes not provided in request will be defined again (set to null or default)")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public CartModificationWsDTO replaceCartEntry(@ApiParam(value = "Base site identifier.") @PathVariable final String baseSiteId,
			@ApiParam(value = "The entry number. Each entry in a cart has an entry number. Cart entries are numbered in ascending order, starting with zero (0).") @PathVariable final long entryNumber,
			@ApiParam(value = "Quantity of product.") @RequestParam(required = true) final Long qty,
			@ApiParam(value = "Name of the store where product will be picked. Set only if want to pick up from a store.") @RequestParam(required = false) final String pickupStore,
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
			throws CommerceCartModificationException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("replaceCartEntry: " + logParam("entryNumber", entryNumber) + ", " + logParam("qty", qty) + ", "
					+ logParam("pickupStore", pickupStore));
		}
		final CartData cart = getSessionCart();
		final OrderEntryData orderEntry = getCartEntryForNumber(cart, entryNumber);
		if (!StringUtils.isEmpty(pickupStore))
		{
			validate(pickupStore, "pickupStore", pointOfServiceValidator);
		}

		return updateCartEntryInternal(baseSiteId, cart, orderEntry, qty, pickupStore, fields, true);
	}

	protected CartModificationWsDTO updateCartEntryInternal(final String baseSiteId, final CartData cart,
			final OrderEntryData orderEntry, final Long qty, final String pickupStore, final String fields, final boolean putMode)
			throws CommerceCartModificationException
	{
		final long entryNumber = orderEntry.getEntryNumber().longValue();
		final String productCode = orderEntry.getProduct().getCode();
		final PointOfServiceData currentPointOfService = orderEntry.getDeliveryPointOfService();

		CartModificationData cartModificationData1 = null;
		CartModificationData cartModificationData2 = null;

		if (!StringUtils.isEmpty(pickupStore))
		{
			if (currentPointOfService == null || !currentPointOfService.getName().equals(pickupStore))
			{
				//was 'shipping mode' or store is changed
				validateForAmbiguousPositions(cart, orderEntry, pickupStore);
				validateIfProductIsInStockInPOS(baseSiteId, productCode, pickupStore, Long.valueOf(entryNumber));
				cartModificationData1 = getCartFacade().updateCartEntry(entryNumber, pickupStore);
			}
		}
		else if (putMode && currentPointOfService != null)
		{
			//was 'pickup in store', now switch to 'shipping mode'
			validateForAmbiguousPositions(cart, orderEntry, pickupStore);
			validateIfProductIsInStockOnline(baseSiteId, productCode, Long.valueOf(entryNumber));
			cartModificationData1 = getCartFacade().updateCartEntry(entryNumber, pickupStore);
		}

		if (qty != null)
		{
			cartModificationData2 = getCartFacade().updateCartEntry(entryNumber, qty.longValue());
		}

		return getDataMapper().map(mergeCartModificationData(cartModificationData1, cartModificationData2),
				CartModificationWsDTO.class, fields);
	}

	@RequestMapping(value = "/{cartId}/entries/{entryNumber}", method = RequestMethod.PUT, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseBody
	@ApiOperation(nickname = "replaceCartEntry", value = "Set quantity and store details of a cart entry.", notes =
			"Updates the quantity of a single cart entry and the details of the store where the cart entry will be picked up. "
					+ "Attributes not provided in request will be defined again (set to null or default)")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public CartModificationWsDTO replaceCartEntry(@ApiParam(value = "Base site identifier.") @PathVariable final String baseSiteId,
			@ApiParam(value = "The entry number. Each entry in a cart has an entry number. Cart entries are numbered in ascending order, starting with zero (0).", required = true) @PathVariable final long entryNumber,
			@ApiParam(value = "Request body parameter that contains details such as the quantity of product (quantity), and the pickup store name (deliveryPointOfService.name)\n\nThe DTO is in XML or .json format.", required = true) @RequestBody final OrderEntryWsDTO entry,
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
			throws CommerceCartModificationException
	{
		final CartData cart = getSessionCart();
		final OrderEntryData orderEntry = getCartEntryForNumber(cart, entryNumber);
		final String pickupStore = entry.getDeliveryPointOfService() == null ? null : entry.getDeliveryPointOfService().getName();

		validateCartEntryForReplace(orderEntry, entry);

		return updateCartEntryInternal(baseSiteId, cart, orderEntry, entry.getQuantity(), pickupStore, fields, true);
	}

	protected void validateCartEntryForReplace(final OrderEntryData oryginalEntry, final OrderEntryWsDTO entry)
	{
		final String productCode = oryginalEntry.getProduct().getCode();
		final Errors errors = new BeanPropertyBindingResult(entry, "entry");
		if (entry.getProduct() != null && entry.getProduct().getCode() != null && !entry.getProduct().getCode().equals(productCode))
		{
			errors.reject("cartEntry.productCodeNotMatch");
			throw new WebserviceValidationException(errors);
		}

		validate(entry, "entry", orderEntryReplaceValidator);
	}

	@RequestMapping(value = "/{cartId}/entries/{entryNumber}", method = RequestMethod.PATCH)
	@ResponseBody
	@ApiOperation(hidden = true, value = "Update quantity and store details of a cart entry.", notes = "Updates the quantity of a single cart entry and the details of the store where the cart entry will be picked up.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public CartModificationWsDTO updateCartEntry(@ApiParam(value = "Base site identifier.") @PathVariable final String baseSiteId,
			@ApiParam(value = "The entry number. Each entry in a cart has an entry number. Cart entries are numbered in ascending order, starting with zero (0).", required = true) @PathVariable final long entryNumber,
			@ApiParam(value = "Quantity of product.") @RequestParam(required = false) final Long qty,
			@ApiParam(value = "Name of the store where product will be picked. Set only if want to pick up from a store.") @RequestParam(required = false) final String pickupStore,
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
			throws CommerceCartModificationException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("updateCartEntry: " + logParam("entryNumber", entryNumber) + ", " + logParam("qty", qty) + ", "
					+ logParam("pickupStore", pickupStore));
		}

		final CartData cart = getSessionCart();
		final OrderEntryData orderEntry = getCartEntryForNumber(cart, entryNumber);

		if (qty == null && StringUtils.isEmpty(pickupStore))
		{
			throw new RequestParameterException("At least one parameter (qty,pickupStore) should be set!",
					RequestParameterException.MISSING);
		}

		if (qty != null)
		{
			validate(qty, "quantity", greaterThanZeroValidator);
		}

		if (pickupStore != null)
		{
			validate(pickupStore, "pickupStore", pointOfServiceValidator);
		}

		return updateCartEntryInternal(baseSiteId, cart, orderEntry, qty, pickupStore, fields, false);
	}

	@RequestMapping(value = "/{cartId}/entries/{entryNumber}", method = RequestMethod.PATCH, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseBody
	@ApiOperation(nickname = "updateCartEntry", value = "Update quantity and store details of a cart entry.", notes = "Updates the quantity of a single cart entry and the details of the store where the cart entry will be picked up.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public CartModificationWsDTO updateCartEntry(@ApiParam(value = "Base site identifier.") @PathVariable final String baseSiteId,
			@ApiParam(value = "The entry number. Each entry in a cart has an entry number. Cart entries are numbered in ascending order, starting with zero (0).", required = true) @PathVariable final long entryNumber,
			@ApiParam(value = "Request body parameter that contains details such as the quantity of product (quantity), and the pickup store name (deliveryPointOfService.name)\n\nThe DTO is in XML or .json format.", required = true) @RequestBody final OrderEntryWsDTO entry,
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
			throws CommerceCartModificationException
	{
		final CartData cart = getSessionCart();
		final OrderEntryData orderEntry = getCartEntryForNumber(cart, entryNumber);

		final String productCode = orderEntry.getProduct().getCode();
		final Errors errors = new BeanPropertyBindingResult(entry, "entry");
		if (entry.getProduct() != null && entry.getProduct().getCode() != null && !entry.getProduct().getCode().equals(productCode))
		{
			errors.reject("cartEntry.productCodeNotMatch");
			throw new WebserviceValidationException(errors);
		}

		if (entry.getQuantity() == null)
		{
			entry.setQuantity(orderEntry.getQuantity());
		}

		validate(entry, "entry", orderEntryUpdateValidator);

		final String pickupStore = entry.getDeliveryPointOfService() == null ? null : entry.getDeliveryPointOfService().getName();
		return updateCartEntryInternal(baseSiteId, cart, orderEntry, entry.getQuantity(), pickupStore, fields, false);
	}

	@RequestMapping(value = "/{cartId}/entries/{entryNumber}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(nickname = "removeCartEntry", value = "Deletes cart entry.", notes = "Deletes cart entry.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public void removeCartEntry(
			@ApiParam(value = "The entry number. Each entry in a cart has an entry number. Cart entries are numbered in ascending order, starting with zero (0).", required = true) @PathVariable final long entryNumber)
			throws CommerceCartModificationException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("removeCartEntry: " + logParam("entryNumber", entryNumber));
		}

		final CartData cart = getSessionCart();
		getCartEntryForNumber(cart, entryNumber);
		getCartFacade().updateCartEntry(entryNumber, 0);
	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_GUEST", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/{cartId}/addresses/delivery", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	@ApiOperation(hidden = true, value = "Creates a delivery address for the cart.", notes = "Creates an address and assigns it to the cart as the delivery address.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public AddressWsDTO createCartDeliveryAddress(final HttpServletRequest request,
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
			throws WebserviceValidationException, NoCheckoutCartException //NOSONAR
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("createCartDeliveryAddress");
		}
		final AddressData addressData = super.createAddressInternal(request);
		final String addressId = addressData.getId();
		super.setCartDeliveryAddressInternal(addressId);
		return getDataMapper().map(addressData, AddressWsDTO.class, fields);
	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_GUEST", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/{cartId}/addresses/delivery", method = RequestMethod.POST, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	@ApiOperation(nickname = "createCartDeliveryAddress", value = "Creates a delivery address for the cart.", notes = "Creates an address and assigns it to the cart as the delivery address.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public AddressWsDTO createCartDeliveryAddress(
			@ApiParam(value = "Request body parameter that contains details such as the customer's first name (firstName), the customer's last name (lastName), the customer's title (titleCode), "
					+ "the country (country.isocode), the first part of the address (line1), the second part of the address (line2), the town (town), the postal code (postalCode), and the region (region.isocode).\n\nThe DTO is in XML or .json format.", required = true) @RequestBody final AddressWsDTO address,
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
			throws WebserviceValidationException, NoCheckoutCartException //NOSONAR
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("createCartDeliveryAddress");
		}
		validate(address, "address", getAddressDTOValidator());
		AddressData addressData = getDataMapper().map(address, AddressData.class,
				"titleCode,firstName,lastName,line1,line2,town,postalCode,country(isocode),region(isocode),defaultAddress");
		addressData = createAddressInternal(addressData);
		setCartDeliveryAddressInternal(addressData.getId());
		return getDataMapper().map(addressData, AddressWsDTO.class, fields);
	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/{cartId}/addresses/delivery", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(nickname = "replaceCartDeliveryAddress", value = "Sets a delivery address for the cart.", notes = "Sets a delivery address for the cart. The address country must be placed among the delivery countries of the current base store.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public void replaceCartDeliveryAddress(
			@ApiParam(value = "Address identifier", required = true) @RequestParam(required = true) final String addressId)
			throws NoCheckoutCartException
	{
		super.setCartDeliveryAddressInternal(addressId);
	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/{cartId}/addresses/delivery", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(nickname = "removeCartDeliveryAddress", value = "Deletes the delivery address from the cart.", notes = "Deletes the delivery address from the cart.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public void removeCartDeliveryAddress()
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("removeCartDeliveryAddress");
		}
		if (!getCheckoutFacade().removeDeliveryAddress())
		{
			throw new CartException("Cannot reset address!", CartException.CANNOT_RESET_ADDRESS);
		}
	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/{cartId}/deliverymode", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(nickname = "getCartDeliveryMode", value = "Get the delivery mode selected for the cart.", notes = "Returns the delivery mode selected for the cart.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public DeliveryModeWsDTO getCartDeliveryMode(
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("getCartDeliveryMode");
		}
		return getDataMapper().map(getSessionCart().getDeliveryMode(), DeliveryModeWsDTO.class, fields);
	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/{cartId}/deliverymode", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(nickname = "replaceCartDeliveryMode", value = "Sets the delivery mode for a cart.", notes = "Sets the delivery mode with a given identifier for the cart.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public void replaceCartDeliveryMode(
			@ApiParam(value = "Delivery mode identifier (code)", required = true) @RequestParam(required = true) final String deliveryModeId)
			throws UnsupportedDeliveryModeException
	{
		super.setCartDeliveryModeInternal(deliveryModeId);
	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/{cartId}/deliverymode", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(nickname = "removeCartDeliveryMode", value = "Deletes the delivery mode from the cart.", notes = "Deletes the delivery mode from the cart.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public void removeCartDeliveryMode()
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("removeCartDeliveryMode");
		}
		if (!getCheckoutFacade().removeDeliveryMode())
		{
			throw new CartException("Cannot reset delivery mode!", CartException.CANNOT_RESET_DELIVERYMODE);
		}
	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/{cartId}/deliverymodes", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(nickname = "getCartDeliveryModes", value = "Get all delivery modes for the current store and delivery address.", notes =
			"Returns all delivery modes supported for the "
					+ "current base store and cart delivery address. A delivery address must be set for the cart, otherwise an empty list will be returned.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public DeliveryModeListWsDTO getCartDeliveryModes(
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("getCartDeliveryModes");
		}
		final DeliveryModesData deliveryModesData = new DeliveryModesData();
		deliveryModesData.setDeliveryModes(getCheckoutFacade().getSupportedDeliveryModes());

		return getDataMapper().map(deliveryModesData, DeliveryModeListWsDTO.class, fields);
	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/{cartId}/paymentdetails", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	@ApiOperation(hidden = true, value = "Defines and assigns details of a new credit card payment to the cart.", notes = "Defines the details of a new credit card, and assigns this payment option to the cart.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public PaymentDetailsWsDTO createCartPaymentDetails(final HttpServletRequest request, //NOSONAR
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
			throws WebserviceValidationException, InvalidPaymentInfoException, NoCheckoutCartException, UnsupportedRequestException //NOSONAR
	{
		paymentProviderRequestSupportedStrategy.checkIfRequestSupported("addPaymentDetails");
		final CCPaymentInfoData paymentInfoData = super.addPaymentDetailsInternal(request).getPaymentInfo();
		return getDataMapper().map(paymentInfoData, PaymentDetailsWsDTO.class, fields);
	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/{cartId}/paymentdetails", method = RequestMethod.POST, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	@ApiOperation(nickname = "createCartPaymentDetails", value = "Defines and assigns details of a new credit card payment to the cart.", notes = "Defines the details of a new credit card, and assigns this payment option to the cart.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public PaymentDetailsWsDTO createCartPaymentDetails(
			@ApiParam(value = "Request body parameter that contains details such as the name on the card (accountHolderName), the card number (cardNumber), the card type (cardType.code), "
					+ "the month of the expiry date (expiryMonth), the year of the expiry date (expiryYear), whether the payment details should be saved (saved), whether the payment details "
					+ "should be set as default (defaultPaymentInfo), and the billing address (billingAddress.firstName, billingAddress.lastName, billingAddress.titleCode, billingAddress.country.isocode, "
					+ "billingAddress.line1, billingAddress.line2, billingAddress.town, billingAddress.postalCode, billingAddress.region.isocode)\n\nThe DTO is in XML or .json format.", required = true) @RequestBody final PaymentDetailsWsDTO paymentDetails, //NOSONAR
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
			throws WebserviceValidationException, InvalidPaymentInfoException, NoCheckoutCartException, UnsupportedRequestException //NOSONAR
	{
		paymentProviderRequestSupportedStrategy.checkIfRequestSupported("addPaymentDetails");
		validatePayment(paymentDetails);
		final String copiedfields = "accountHolderName,cardNumber,cardType,cardTypeData(code),expiryMonth,expiryYear,issueNumber,startMonth,startYear,subscriptionId,defaultPaymentInfo,saved,billingAddress(titleCode,firstName,lastName,line1,line2,town,postalCode,country(isocode),region(isocode),defaultAddress)";
		CCPaymentInfoData paymentInfoData = getDataMapper().map(paymentDetails, CCPaymentInfoData.class, copiedfields);
		paymentInfoData = addPaymentDetailsInternal(paymentInfoData).getPaymentInfo();
		return getDataMapper().map(paymentInfoData, PaymentDetailsWsDTO.class, fields);
	}

	protected void validatePayment(final PaymentDetailsWsDTO paymentDetails) throws NoCheckoutCartException
	{
		if (!getCheckoutFacade().hasCheckoutCart())
		{
			throw new NoCheckoutCartException("Cannot add PaymentInfo. There was no checkout cart created yet!");
		}
		validate(paymentDetails, "paymentDetails", getPaymentDetailsDTOValidator());
	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/{cartId}/paymentdetails", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(nickname = "replaceCartPaymentDetails", value = "Sets credit card payment details for the cart.", notes = "Sets credit card payment details for the cart.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public void replaceCartPaymentDetails(
			@ApiParam(value = "Payment details identifier.", required = true) @RequestParam(required = true) final String paymentDetailsId)
			throws InvalidPaymentInfoException
	{
		super.setPaymentDetailsInternal(paymentDetailsId);
	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/{cartId}/promotions", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(nickname = "getCartPromotions", value = "Get information about promotions applied on cart.", notes =
			"Returns information about the promotions applied on the cart. "
					+ "Requests pertaining to promotions have been developed for the previous version of promotions and vouchers, and as a result, some of them "
					+ "are currently not compatible with the new promotions engine.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public PromotionResultListWsDTO getCartPromotions(
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("getCartPromotions");
		}
		final List<PromotionResultData> appliedPromotions = new ArrayList<>();
		final List<PromotionResultData> orderPromotions = getSessionCart().getAppliedOrderPromotions();
		final List<PromotionResultData> productPromotions = getSessionCart().getAppliedProductPromotions();
		appliedPromotions.addAll(orderPromotions);
		appliedPromotions.addAll(productPromotions);

		final PromotionResultDataList dataList = new PromotionResultDataList();
		dataList.setPromotions(appliedPromotions);
		return getDataMapper().map(dataList, PromotionResultListWsDTO.class, fields);
	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/{cartId}/promotions/{promotionId}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(nickname = "getCartPromotion", value = "Get information about promotion applied on cart.", notes =
			"Returns information about a promotion (with a specific promotionId), that has "
			+ "been applied on the cart. Requests pertaining to promotions have been developed for the previous version of promotions and vouchers, and as a result, some "
					+ "of them are currently not compatible with the new promotions engine.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public PromotionResultListWsDTO getCartPromotion(
			@ApiParam(value = "Promotion identifier (code)", required = true) @PathVariable final String promotionId,
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("getCartPromotion: promotionId = " + sanitize(promotionId));
		}
		final List<PromotionResultData> appliedPromotions = new ArrayList<PromotionResultData>();
		final List<PromotionResultData> orderPromotions = getSessionCart().getAppliedOrderPromotions();
		final List<PromotionResultData> productPromotions = getSessionCart().getAppliedProductPromotions();
		for (final PromotionResultData prd : orderPromotions)
		{
			if (prd.getPromotionData().getCode().equals(promotionId))
			{
				appliedPromotions.add(prd);
			}
		}
		for (final PromotionResultData prd : productPromotions)
		{
			if (prd.getPromotionData().getCode().equals(promotionId))
			{
				appliedPromotions.add(prd);
			}
		}

		final PromotionResultDataList dataList = new PromotionResultDataList();
		dataList.setPromotions(appliedPromotions);
		return getDataMapper().map(dataList, PromotionResultListWsDTO.class, fields);
	}

	@Secured(
	{ "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/{cartId}/promotions", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(nickname = "doApplyCartPromotion", value = "Enables promotions based on the promotionsId of the cart.", notes =
			"Enables a promotion for the order based on the promotionId defined for the cart. "
			+ "Requests pertaining to promotions have been developed for the previous version of promotions and vouchers, and as a result, some of them are currently not compatible "
					+ "with the new promotions engine.", authorizations = { @Authorization(value = "oauth2_client_credentials") })
	@ApiBaseSiteIdUserIdAndCartIdParam
	public void doApplyCartPromotion(
			@ApiParam(value = "Promotion identifier (code)", required = true) @RequestParam(required = true) final String promotionId)
			throws CommercePromotionRestrictionException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("doApplyCartPromotion: promotionId = " + sanitize(promotionId));
		}
		commercePromotionRestrictionFacade.enablePromotionForCurrentCart(promotionId);
	}

	@Secured(
	{ "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/{cartId}/promotions/{promotionId}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(nickname = "removeCartPromotion", value = "Disables the promotion based on the promotionsId of the cart.", notes =
			"Disables the promotion for the order based on the promotionId defined for the cart. "
			+ "Requests pertaining to promotions have been developed for the previous version of promotions and vouchers, and as a result, some of them are currently not compatible with "
					+ "the new promotions engine.", authorizations = { @Authorization(value = "oauth2_client_credentials") })
	@ApiBaseSiteIdUserIdAndCartIdParam
	@SuppressWarnings("squid:S1160")
	public void removeCartPromotion(
			@ApiParam(value = "Promotion identifier (code)", required = true) @PathVariable final String promotionId) //NOSONAR
			throws CommercePromotionRestrictionException, NoCheckoutCartException //NOSONAR
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("removeCartPromotion: promotionId = " + sanitize(promotionId));
		}
		commercePromotionRestrictionFacade.disablePromotionForCurrentCart(promotionId);
	}

	@Secured(
	{ "ROLE_CLIENT", "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_GUEST" })
	@RequestMapping(value = "/{cartId}/vouchers", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(nickname = "getCartVouchers", value = "Get a list of vouchers applied to the cart.", notes = "Returns a list of vouchers applied to the cart.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public VoucherListWsDTO getCartVouchers(
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("getVouchers");
		}
		final VoucherDataList dataList = new VoucherDataList();
		dataList.setVouchers(voucherFacade.getVouchersForCart());
		return getDataMapper().map(dataList, VoucherListWsDTO.class, fields);
	}

	@Secured(
	{ "ROLE_CLIENT", "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_GUEST" })
	@RequestMapping(value = "/{cartId}/vouchers", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(nickname = "doApplyCartVoucher", value = "Applies a voucher based on the voucherId defined for the cart.", notes = "Applies a voucher based on the voucherId defined for the cart.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	@SuppressWarnings("squid:S1160")
	public void doApplyCartVoucher(
			@ApiParam(value = "Voucher identifier (code)", required = true) @RequestParam(required = true) final String voucherId) //NOSONAR
			throws NoCheckoutCartException, VoucherOperationException //NOSONAR
	{
		super.applyVoucherForCartInternal(voucherId);
	}

	@Secured(
	{ "ROLE_CLIENT", "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_GUEST" })
	@RequestMapping(value = "/{cartId}/vouchers/{voucherId}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(nickname = "removeCartVoucher", value = "Deletes a voucher defined for the current cart.", notes = "Deletes a voucher based on the voucherId defined for the current cart.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	@SuppressWarnings("squid:S1160")
	public void removeCartVoucher(
			@ApiParam(value = "Voucher identifier (code)", required = true) @PathVariable final String voucherId) //NOSONAR
			throws NoCheckoutCartException, VoucherOperationException //NOSONAR
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("release voucher : voucherCode = " + sanitize(voucherId));
		}
		if (!getCheckoutFacade().hasCheckoutCart())
		{
			throw new NoCheckoutCartException("Cannot realese voucher. There was no checkout cart created yet!");
		}
		getVoucherFacade().releaseVoucher(voucherId);
	}

	protected void validateIfProductIsInStockInPOS(final String baseSiteId, final String productCode, final String storeName,
			final Long entryNumber)
	{
		if (!commerceStockFacade.isStockSystemEnabled(baseSiteId))
		{
			throw new StockSystemException("Stock system is not enabled on this site", StockSystemException.NOT_ENABLED, baseSiteId);
		}
		final StockData stock = commerceStockFacade.getStockDataForProductAndPointOfService(productCode, storeName);
		if (stock != null && stock.getStockLevelStatus().equals(StockLevelStatus.OUTOFSTOCK))
		{
			if (entryNumber != null)
			{
				throw new LowStockException("Product [" + sanitize(productCode) + "] is currently out of stock", //NOSONAR
						LowStockException.NO_STOCK, String.valueOf(entryNumber));
			}
			else
			{
				throw new ProductLowStockException("Product [" + sanitize(productCode) + "] is currently out of stock",
						LowStockException.NO_STOCK, productCode);
			}
		}
		else if (stock != null && stock.getStockLevelStatus().equals(StockLevelStatus.LOWSTOCK))
		{
			if (entryNumber != null)
			{
				throw new LowStockException("Not enough product in stock", LowStockException.LOW_STOCK, String.valueOf(entryNumber));
			}
			else
			{
				throw new ProductLowStockException("Not enough product in stock", LowStockException.LOW_STOCK, productCode);
			}
		}
	}

	protected void validateIfProductIsInStockOnline(final String baseSiteId, final String productCode, final Long entryNumber)
	{
		if (!commerceStockFacade.isStockSystemEnabled(baseSiteId))
		{
			throw new StockSystemException("Stock system is not enabled on this site", StockSystemException.NOT_ENABLED, baseSiteId);
		}
		final StockData stock = commerceStockFacade.getStockDataForProductAndBaseSite(productCode, baseSiteId);
		if (stock != null && stock.getStockLevelStatus().equals(StockLevelStatus.OUTOFSTOCK))
		{
			if (entryNumber != null)
			{
				throw new LowStockException("Product [" + sanitize(productCode) + "] cannot be shipped - out of stock online",
						LowStockException.NO_STOCK, String.valueOf(entryNumber));
			}
			else
			{
				throw new ProductLowStockException("Product [" + sanitize(productCode) + "] cannot be shipped - out of stock online",
						LowStockException.NO_STOCK, productCode);
			}
		}
	}

}

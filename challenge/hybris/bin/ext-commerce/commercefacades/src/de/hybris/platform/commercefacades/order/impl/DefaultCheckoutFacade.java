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
package de.hybris.platform.commercefacades.order.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commercefacades.i18n.comparators.CountryComparator;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CardTypeData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.ZoneDeliveryModeData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.delivery.DeliveryService;
import de.hybris.platform.commerceservices.enums.CountryType;
import de.hybris.platform.commerceservices.enums.SalesApplication;
import de.hybris.platform.commerceservices.order.CommerceCardTypeService;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.payment.dto.BillingInfo;
import de.hybris.platform.payment.dto.CardInfo;
import de.hybris.platform.payment.dto.CardType;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.PriceValue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Default implementation of {@link CheckoutFacade}
 */
public class DefaultCheckoutFacade implements CheckoutFacade
{
	private static final Logger LOG = Logger.getLogger(DefaultCheckoutFacade.class);
	private CartFacade cartFacade;
	private CartService cartService;
	private DeliveryService deliveryService;
	private UserService userService;
	private PriceDataFactory priceDataFactory;
	private CommerceCheckoutService commerceCheckoutService;
	private EnumerationService enumerationService;
	private CommerceCardTypeService commerceCardTypeService;
	private CustomerAccountService customerAccountService;
	private ModelService modelService;
	private CommonI18NService commonI18NService;
	private BaseStoreService baseStoreService;
	private CheckoutCustomerStrategy checkoutCustomerStrategy;
	private Populator<AddressData, AddressModel> addressReversePopulator;
	private Converter<CreditCardPaymentInfoModel, CCPaymentInfoData> creditCardPaymentInfoConverter;
	private Converter<DeliveryModeModel, DeliveryModeData> deliveryModeConverter;
	private Converter<ZoneDeliveryModeModel, ZoneDeliveryModeData> zoneDeliveryModeConverter;
	private Converter<OrderModel, OrderData> orderConverter;
	private Converter<AddressModel, AddressData> addressConverter;
	private Converter<CardType, CardTypeData> cardTypeConverter;
	private Converter<CountryModel, CountryData> countryConverter;

	@Override
	public boolean hasCheckoutCart()
	{
		return getCartFacade().hasSessionCart();
	}

	@Override
	public CartData getCheckoutCart()
	{
		final CartData cartData = getCartFacade().getSessionCart();
		if (cartData != null)
		{
			cartData.setDeliveryAddress(getDeliveryAddress());
			cartData.setDeliveryMode(getDeliveryMode());
			cartData.setPaymentInfo(getPaymentDetails());
		}
		return cartData;
	}

	protected CartModel getCart()
	{
		return hasCheckoutCart() ? getCartService().getSessionCart() : null;
	}

	@Override
	public List<AddressData> getSupportedDeliveryAddresses(final boolean visibleAddressesOnly)
	{
		final CartModel cartModel = getCart();
		return cartModel == null ? Collections.emptyList()
				: getAddressConverter()
						.convertAll(getDeliveryService().getSupportedDeliveryAddressesForOrder(cartModel, visibleAddressesOnly));
	}

	@Override
	public AddressData getDeliveryAddressForCode(final String code)
	{
		Assert.notNull(code, "Parameter code cannot be null.");
		for (final AddressData address : getSupportedDeliveryAddresses(false))
		{
			if (code.equals(address.getId()))
			{
				return address;
			}
		}
		return null;
	}

	protected AddressModel getDeliveryAddressModelForCode(final String code)
	{
		Assert.notNull(code, "Parameter code cannot be null.");
		final CartModel cartModel = getCart();
		if (cartModel != null)
		{
			for (final AddressModel address : getDeliveryService().getSupportedDeliveryAddressesForOrder(cartModel, false))
			{
				if (code.equals(address.getPk().toString()))
				{
					return address;
				}
			}
		}
		return null;
	}

	protected AddressModel createDeliveryAddressModel(final AddressData addressData, final CartModel cartModel)
	{
		final AddressModel addressModel = getModelService().create(AddressModel.class);
		getAddressReversePopulator().populate(addressData, addressModel);
		addressModel.setOwner(cartModel);
		return addressModel;
	}

	protected AddressData getDeliveryAddress()
	{
		final CartModel cart = getCart();
		if (cart != null)
		{
			final AddressModel deliveryAddress = cart.getDeliveryAddress();
			if (deliveryAddress != null)
			{
				// Ensure that the delivery address is in the set of supported addresses
				final AddressModel supportedAddress = getDeliveryAddressModelForCode(deliveryAddress.getPk().toString());
				if (supportedAddress != null)
				{
					return getAddressConverter().convert(supportedAddress);
				}
			}
		}
		return null;
	}

	@Override
	public boolean setDeliveryAddress(final AddressData addressData)
	{
		final CartModel cartModel = getCart();
		if (cartModel != null)
		{
			AddressModel addressModel = null;
			if (addressData != null)
			{
				addressModel = addressData.getId() == null ? createDeliveryAddressModel(addressData, cartModel)
						: getDeliveryAddressModelForCode(addressData.getId());
			}

			final CommerceCheckoutParameter parameter = createCommerceCheckoutParameter(cartModel, true);
			parameter.setAddress(addressModel);
			parameter.setIsDeliveryAddress(false);
			return getCommerceCheckoutService().setDeliveryAddress(parameter);
		}
		return false;
	}

	@Override
	public List<? extends DeliveryModeData> getSupportedDeliveryModes()
	{
		final List<DeliveryModeData> result = new ArrayList<DeliveryModeData>();
		final CartModel cartModel = getCart();
		if (cartModel != null)
		{
			for (final DeliveryModeModel deliveryModeModel : getDeliveryService().getSupportedDeliveryModeListForOrder(cartModel))
			{
				result.add(convert(deliveryModeModel));
			}
		}
		return result;
	}

	@Override
	public boolean setDeliveryAddressIfAvailable()
	{
		final CartModel cartModel = getCart();

		if (cartModel == null || cartModel.getDeliveryAddress() != null)
		{
			return false;
		}

		final UserModel currentUser = getCurrentUserForCheckout();
		if (cartModel.getUser().equals(currentUser))
		{
			final AddressModel currentUserDefaultShipmentAddress = currentUser.getDefaultShipmentAddress();
			if (currentUserDefaultShipmentAddress != null)
			{
				final AddressModel supportedDeliveryAddress = getDeliveryAddressModelForCode(
						currentUserDefaultShipmentAddress.getPk().toString());
				if (supportedDeliveryAddress != null)
				{
					final CommerceCheckoutParameter parameter = createCommerceCheckoutParameter(cartModel, true);
					parameter.setAddress(supportedDeliveryAddress);
					parameter.setIsDeliveryAddress(false);
					return getCommerceCheckoutService().setDeliveryAddress(parameter);
				}
			}
		}

		// Could not use default address, try any address
		final List<AddressModel> supportedDeliveryAddresses = getDeliveryService().getSupportedDeliveryAddressesForOrder(cartModel,
				true);
		if (supportedDeliveryAddresses != null && !supportedDeliveryAddresses.isEmpty())
		{
			final CommerceCheckoutParameter parameter = createCommerceCheckoutParameter(cartModel, true);
			parameter.setAddress(supportedDeliveryAddresses.get(0));
			parameter.setIsDeliveryAddress(false);
			return getCommerceCheckoutService().setDeliveryAddress(parameter);
		}
		return false;
	}

	@Override
	public boolean setDeliveryModeIfAvailable()
	{
		final CartModel cartModel = getCart();
		if (cartModel != null)
		{
			// validate delivery mode if already exists
			getCommerceCheckoutService().validateDeliveryMode(createCommerceCheckoutParameter(cartModel, true));

			if (cartModel.getDeliveryMode() == null)
			{
				final List<? extends DeliveryModeData> availableDeliveryModes = getSupportedDeliveryModes();
				if (!availableDeliveryModes.isEmpty())
				{
					return setDeliveryMode(availableDeliveryModes.get(0).getCode());
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean setPaymentInfoIfAvailable()
	{
		final CartModel cartModel = getCart();

		if (cartModel == null || cartModel.getPaymentInfo() != null)
		{
			return false;
		}

		final UserModel currentUser = getCurrentUserForCheckout();
		if (cartModel.getUser().equals(currentUser))
		{
			// Try the default payment info
			final PaymentInfoModel defaultPaymentInfo = ((CustomerModel) currentUser).getDefaultPaymentInfo();
			if (defaultPaymentInfo != null)
			{
				final CommerceCheckoutParameter parameter = createCommerceCheckoutParameter(cartModel, true);
				parameter.setPaymentInfo(defaultPaymentInfo);
				return getCommerceCheckoutService().setPaymentInfo(parameter);
			}

			// Fallback to the first available stored card
			final List<CreditCardPaymentInfoModel> creditCardPaymentInfos = getCustomerAccountService()
					.getCreditCardPaymentInfos((CustomerModel) currentUser, true);
			if (creditCardPaymentInfos != null && !creditCardPaymentInfos.isEmpty())
			{
				final CommerceCheckoutParameter parameter = createCommerceCheckoutParameter(cartModel, true);
				parameter.setPaymentInfo(creditCardPaymentInfos.get(0));
				return getCommerceCheckoutService().setPaymentInfo(parameter);
			}
		}
		return false;
	}

	protected DeliveryModeData getDeliveryMode()
	{
		final CartModel cart = getCart();
		return cart == null || cart.getDeliveryMode() == null ? null : convert(cart.getDeliveryMode());
	}

	@Override
	public boolean setDeliveryMode(final String deliveryModeCode)
	{
		validateParameterNotNullStandardMessage("deliveryModeCode", deliveryModeCode);

		final CartModel cartModel = getCart();
		if (cartModel != null && isSupportedDeliveryMode(deliveryModeCode, cartModel))
		{
			final DeliveryModeModel deliveryModeModel = getDeliveryService().getDeliveryModeForCode(deliveryModeCode);
			if (deliveryModeModel != null)
			{
				final CommerceCheckoutParameter parameter = createCommerceCheckoutParameter(cartModel, true);
				parameter.setDeliveryMode(deliveryModeModel);
				return getCommerceCheckoutService().setDeliveryMode(parameter);
			}
		}
		return false;
	}

	protected boolean isSupportedDeliveryMode(final String deliveryModeCode, final CartModel cartModel)
	{
		for (final DeliveryModeModel supportedDeliveryMode : getDeliveryService().getSupportedDeliveryModeListForOrder(cartModel))
		{
			if (deliveryModeCode.equals(supportedDeliveryMode.getCode()))
			{
				return true;
			}
		}
		return false;
	}

	protected DeliveryModeData convert(final DeliveryModeModel deliveryModeModel)
	{
		if (deliveryModeModel instanceof ZoneDeliveryModeModel)
		{
			final ZoneDeliveryModeModel zoneDeliveryModeModel = (ZoneDeliveryModeModel) deliveryModeModel;
			final CartModel cartModel = getCart();
			if (cartModel != null)
			{
				final ZoneDeliveryModeData zoneDeliveryModeData = getZoneDeliveryModeConverter().convert(zoneDeliveryModeModel);
				final PriceValue deliveryCost = getDeliveryService().getDeliveryCostForDeliveryModeAndAbstractOrder(deliveryModeModel,
						cartModel);
				if (deliveryCost != null)
				{
					zoneDeliveryModeData.setDeliveryCost(getPriceDataFactory().create(PriceDataType.BUY,
							BigDecimal.valueOf(deliveryCost.getValue()), deliveryCost.getCurrencyIso()));
				}
				return zoneDeliveryModeData;
			}
			return null;
		}
		return getDeliveryModeConverter().convert(deliveryModeModel);
	}

	/**
	 * @deprecated since 1808. Please use {@link CheckoutFacade#getCountries(CountryType)} instead.
	 */
	@Deprecated
	@Override
	public List<CountryData> getDeliveryCountries()
	{
		return getCartFacade().getDeliveryCountries();
	}

	/**
	 * @deprecated since 1808. Please use {@link CheckoutFacade#getCountries(CountryType)} instead.
	 */
	@Deprecated
	@Override
	public List<CountryData> getBillingCountries()
	{
		final List<CountryData> countries = getCountryConverter().convertAll(getCommonI18NService().getAllCountries());
		Collections.sort(countries, CountryComparator.INSTANCE);
		return countries;
	}

	@Override
	public List<CountryData> getCountries(final CountryType countryType)
	{
		final List<CountryData> countries = getCountryConverter()
				.convertAll(getCommerceCheckoutService().getCountries(countryType));
		Collections.sort(countries, CountryComparator.INSTANCE);
		return countries;
	}

	@Override
	public boolean containsTaxValues()
	{
		final CartModel cartModel = getCart();

		if (cartModel == null)
		{
			return false;
		}

		if (cartModel.getTotalTaxValues() != null && !cartModel.getTotalTaxValues().isEmpty())
		{
			return true;
		}
		for (final Iterator<AbstractOrderEntryModel> orderEntryModelIterator = cartModel.getEntries()
				.iterator(); orderEntryModelIterator.hasNext();)
		{
			final AbstractOrderEntryModel entryModel = orderEntryModelIterator.next();
			if (entryModel.getTaxValues() != null && !entryModel.getTaxValues().isEmpty())
			{
				return true;
			}
		}
		return false;
	}

	protected CCPaymentInfoData getPaymentDetails()
	{
		final CartModel cart = getCart();
		if (cart != null)
		{
			final PaymentInfoModel paymentInfo = cart.getPaymentInfo();
			if (paymentInfo instanceof CreditCardPaymentInfoModel)
			{
				return getCreditCardPaymentInfoConverter().convert((CreditCardPaymentInfoModel) paymentInfo);
			}
		}
		return null;
	}

	@Override
	public boolean setPaymentDetails(final String paymentInfoId)
	{
		validateParameterNotNullStandardMessage("paymentInfoId", paymentInfoId);

		if (checkIfCurrentUserIsTheCartUser() && StringUtils.isNotBlank(paymentInfoId))
		{
			final CustomerModel currentUserForCheckout = getCurrentUserForCheckout();
			final CreditCardPaymentInfoModel ccPaymentInfoModel = getCustomerAccountService()
					.getCreditCardPaymentInfoForCode(currentUserForCheckout, paymentInfoId);
			final CartModel cartModel = getCart();
			if (ccPaymentInfoModel != null)
			{
				final CommerceCheckoutParameter parameter = createCommerceCheckoutParameter(cartModel, true);
				parameter.setPaymentInfo(ccPaymentInfoModel);
				return getCommerceCheckoutService().setPaymentInfo(parameter);
			}
			LOG.warn(String.format(
					"Did not find CreditCardPaymentInfoModel for user: %s, cart: %s &  paymentInfoId: %s. PaymentInfo Will not get set.",
					currentUserForCheckout, cartModel, paymentInfoId));
		}
		return false;
	}

	@Override
	public List<CardTypeData> getSupportedCardTypes()
	{
		return getCardTypeConverter().convertAll(getCommerceCardTypeService().getCardTypes());
	}

	@Override
	public CCPaymentInfoData createPaymentSubscription(final CCPaymentInfoData paymentInfoData)
	{
		validateParameterNotNullStandardMessage("paymentInfoData", paymentInfoData);
		final AddressData billingAddressData = paymentInfoData.getBillingAddress();
		validateParameterNotNullStandardMessage("billingAddress", billingAddressData);

		if (checkIfCurrentUserIsTheCartUser())
		{
			final CardInfo cardInfo = new CardInfo();
			cardInfo.setCardHolderFullName(paymentInfoData.getAccountHolderName());
			cardInfo.setCardNumber(paymentInfoData.getCardNumber());
			final CardType cardType = getCommerceCardTypeService().getCardTypeForCode(paymentInfoData.getCardType());
			cardInfo.setCardType(cardType == null ? null : cardType.getCode());
			cardInfo.setExpirationMonth(Integer.valueOf(paymentInfoData.getExpiryMonth()));
			cardInfo.setExpirationYear(Integer.valueOf(paymentInfoData.getExpiryYear()));
			cardInfo.setIssueNumber(paymentInfoData.getIssueNumber());

			final BillingInfo billingInfo = new BillingInfo();
			billingInfo.setCity(billingAddressData.getTown());
			billingInfo.setCountry(billingAddressData.getCountry() == null ? null : billingAddressData.getCountry().getIsocode());
			billingInfo.setRegion(billingAddressData.getRegion() == null ? null : billingAddressData.getRegion().getIsocode());
			billingInfo.setFirstName(billingAddressData.getFirstName());
			billingInfo.setLastName(billingAddressData.getLastName());
			billingInfo.setEmail(billingAddressData.getEmail());
			billingInfo.setPhoneNumber(billingAddressData.getPhone());
			billingInfo.setPostalCode(billingAddressData.getPostalCode());
			billingInfo.setStreet1(billingAddressData.getLine1());
			billingInfo.setStreet2(billingAddressData.getLine2());

			final CreditCardPaymentInfoModel ccPaymentInfoModel = getCustomerAccountService().createPaymentSubscription(
					getCurrentUserForCheckout(), cardInfo, billingInfo, billingAddressData.getTitleCode(), getPaymentProvider(),
					paymentInfoData.isSaved());
			return ccPaymentInfoModel == null ? null : getCreditCardPaymentInfoConverter().convert(ccPaymentInfoModel);
		}
		return null;
	}

	@Override
	public boolean authorizePayment(final String securityCode)
	{
		final CartModel cartModel = getCart();
		final CreditCardPaymentInfoModel creditCardPaymentInfoModel = cartModel == null ? null
				: (CreditCardPaymentInfoModel) cartModel.getPaymentInfo();
		if (checkIfCurrentUserIsTheCartUser() && creditCardPaymentInfoModel != null
				&& StringUtils.isNotBlank(creditCardPaymentInfoModel.getSubscriptionId()))
		{
			final CommerceCheckoutParameter parameter = createCommerceCheckoutParameter(cartModel, true);
			parameter.setSecurityCode(securityCode);
			parameter.setPaymentProvider(getPaymentProvider());
			final PaymentTransactionEntryModel paymentTransactionEntryModel = getCommerceCheckoutService()
					.authorizePayment(parameter);

			return paymentTransactionEntryModel != null
					&& (TransactionStatus.ACCEPTED.name().equals(paymentTransactionEntryModel.getTransactionStatus())
							|| TransactionStatus.REVIEW.name().equals(paymentTransactionEntryModel.getTransactionStatus()));
		}
		return false;
	}

	@Override
	public OrderData placeOrder() throws InvalidCartException
	{
		final CartModel cartModel = getCart();
		if (cartModel != null)
		{
			if (cartModel.getUser().equals(getCurrentUserForCheckout()) || getCheckoutCustomerStrategy().isAnonymousCheckout())
			{
				beforePlaceOrder(cartModel);
				final OrderModel orderModel = placeOrder(cartModel);
				afterPlaceOrder(cartModel, orderModel);
				if (orderModel != null)
				{
					return getOrderConverter().convert(orderModel);
				}
			}
		}
		return null;
	}

	protected void beforePlaceOrder(@SuppressWarnings("unused") final CartModel cartModel) // NOSONAR
	{
		// Do nothing, extension point
	}

	protected OrderModel placeOrder(final CartModel cartModel) throws InvalidCartException
	{
		final CommerceCheckoutParameter parameter = createCommerceCheckoutParameter(cartModel, true);
		parameter.setSalesApplication(SalesApplication.WEB);
		return getCommerceCheckoutService().placeOrder(parameter).getOrder();
	}

	protected void afterPlaceOrder(@SuppressWarnings("unused") final CartModel cartModel, final OrderModel orderModel) //NOSONAR
	{
		if (orderModel != null)
		{
			getCartService().removeSessionCart();
			getModelService().refresh(orderModel);
		}
	}

	@Override
	public boolean removeDeliveryAddress()
	{
		final CartModel cartModel = getCart();
		if (cartModel != null)
		{
			final CommerceCheckoutParameter parameter = createCommerceCheckoutParameter(cartModel, true);
			parameter.setAddress(null);
			parameter.setIsDeliveryAddress(false);
			return getCommerceCheckoutService().setDeliveryAddress(parameter);
		}
		return false;
	}

	@Override
	public boolean removeDeliveryMode()
	{
		final CartModel cartModel = getCart();
		if (cartModel != null)
		{
			return getCommerceCheckoutService().removeDeliveryMode(createCommerceCheckoutParameter(cartModel, true));
		}
		return false;
	}

	@Override
	public AddressData getAddressDataForId(final String addressId, final boolean visibleAddressesOnly)
	{
		validateParameterNotNullStandardMessage("addressId", addressId);
		for (final AddressData deliveryAddress : getSupportedDeliveryAddresses(visibleAddressesOnly))
		{
			if (deliveryAddress.getId().equals(addressId))
			{
				return deliveryAddress;
			}
		}
		return null;
	}

	@Override
	public void prepareCartForCheckout()
	{
		final CartModel cartModel = getCart();
		if (cartModel != null)
		{
			getCommerceCheckoutService().calculateCart(createCommerceCheckoutParameter(cartModel, true));
		}
	}

	@Override
	public boolean setDefaultDeliveryAddressForCheckout()
	{
		final AddressModel defaultAddress = getCurrentUserForCheckout().getDefaultShipmentAddress();
		if (checkIfCurrentUserIsTheCartUser() && defaultAddress != null)
		{
			final CommerceCheckoutParameter parameter = createCommerceCheckoutParameter(getCart(), true);
			parameter.setAddress(defaultAddress);
			parameter.setIsDeliveryAddress(false);
			return getCommerceCheckoutService().setDeliveryAddress(parameter);
		}
		return false;
	}

	@Override
	public boolean setDefaultPaymentInfoForCheckout()
	{
		if (checkIfCurrentUserIsTheCartUser())
		{
			final PaymentInfoModel defaultPaymentInfo = getCurrentUserForCheckout().getDefaultPaymentInfo();
			final CommerceCheckoutParameter parameter = createCommerceCheckoutParameter(getCart(), true);
			parameter.setPaymentInfo(defaultPaymentInfo);
			return (defaultPaymentInfo != null && defaultPaymentInfo.isSaved())
					? getCommerceCheckoutService().setPaymentInfo(parameter) : false;
		}
		return false;
	}

	@Override
	public boolean setCheapestDeliveryModeForCheckout()
	{
		final List<? extends DeliveryModeData> availableDeliveryModes = getSupportedDeliveryModes();
		return CollectionUtils.isEmpty(availableDeliveryModes) ? false : setDeliveryMode(availableDeliveryModes.get(0).getCode());
	}

	@Override
	public boolean hasShippingItems()
	{
		return hasItemsMatchingPredicate(e -> e.getDeliveryPointOfService() == null);
	}

	@Override
	public boolean hasPickUpItems()
	{
		return hasItemsMatchingPredicate(e -> e.getDeliveryPointOfService() != null);
	}

	protected boolean hasItemsMatchingPredicate(final Predicate<AbstractOrderEntryModel> predicate)
	{
		final CartModel cart = getCart();
		if (cart != null && CollectionUtils.isNotEmpty(cart.getEntries()))
		{
			for (final AbstractOrderEntryModel entry : cart.getEntries())
			{
				if (predicate.test(entry))
				{
					return true;
				}
			}
		}
		return false;
	}

	protected boolean checkIfCurrentUserIsTheCartUser()
	{
		final CartModel cartModel = getCart();
		return cartModel == null ? false : cartModel.getUser().equals(getCurrentUserForCheckout());
	}

	protected String getPaymentProvider()
	{
		return getCommerceCheckoutService().getPaymentProvider();
	}

	protected CustomerModel getCurrentUserForCheckout()
	{
		return getCheckoutCustomerStrategy().getCurrentUserForCheckout();
	}

	protected CommerceCheckoutParameter createCommerceCheckoutParameter(final CartModel cart, final boolean enableHooks)
	{
		final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
		parameter.setEnableHooks(enableHooks);
		parameter.setCart(cart);
		return parameter;
	}

	protected CartFacade getCartFacade()
	{
		return cartFacade;
	}

	@Required
	public void setCartFacade(final CartFacade cartFacade)
	{
		this.cartFacade = cartFacade;
	}

	protected <T extends CartService> T getCartService()
	{
		return (T) cartService;
	}

	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	protected Converter<AddressModel, AddressData> getAddressConverter()
	{
		return addressConverter;
	}

	@Required
	public void setAddressConverter(final Converter<AddressModel, AddressData> addressConverter)
	{
		this.addressConverter = addressConverter;
	}

	protected Populator<AddressData, AddressModel> getAddressReversePopulator()
	{
		return addressReversePopulator;
	}

	@Required
	public void setAddressReversePopulator(final Populator<AddressData, AddressModel> addressReversePopulator)
	{
		this.addressReversePopulator = addressReversePopulator;
	}

	protected DeliveryService getDeliveryService()
	{
		return deliveryService;
	}

	@Required
	public void setDeliveryService(final DeliveryService deliveryService)
	{
		this.deliveryService = deliveryService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	@Required
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}

	protected CommerceCheckoutService getCommerceCheckoutService()
	{
		return commerceCheckoutService;
	}

	@Required
	public void setCommerceCheckoutService(final CommerceCheckoutService commerceCheckoutService)
	{
		this.commerceCheckoutService = commerceCheckoutService;
	}

	protected EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	@Required
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}

	protected CommerceCardTypeService getCommerceCardTypeService()
	{
		return commerceCardTypeService;
	}

	@Required
	public void setCommerceCardTypeService(final CommerceCardTypeService commerceCardTypeService)
	{
		this.commerceCardTypeService = commerceCardTypeService;
	}

	protected CustomerAccountService getCustomerAccountService()
	{
		return customerAccountService;
	}

	@Required
	public void setCustomerAccountService(final CustomerAccountService customerAccountService)
	{
		this.customerAccountService = customerAccountService;
	}

	protected Converter<CreditCardPaymentInfoModel, CCPaymentInfoData> getCreditCardPaymentInfoConverter()
	{
		return creditCardPaymentInfoConverter;
	}

	@Required
	public void setCreditCardPaymentInfoConverter(
			final Converter<CreditCardPaymentInfoModel, CCPaymentInfoData> creditCardPaymentInfoConverter)
	{
		this.creditCardPaymentInfoConverter = creditCardPaymentInfoConverter;
	}

	protected Converter<DeliveryModeModel, DeliveryModeData> getDeliveryModeConverter()
	{
		return deliveryModeConverter;
	}

	@Required
	public void setDeliveryModeConverter(final Converter<DeliveryModeModel, DeliveryModeData> deliveryModeConverter)
	{
		this.deliveryModeConverter = deliveryModeConverter;
	}

	protected Converter<ZoneDeliveryModeModel, ZoneDeliveryModeData> getZoneDeliveryModeConverter()
	{
		return zoneDeliveryModeConverter;
	}

	@Required
	public void setZoneDeliveryModeConverter(
			final Converter<ZoneDeliveryModeModel, ZoneDeliveryModeData> zoneDeliveryModeConverter)
	{
		this.zoneDeliveryModeConverter = zoneDeliveryModeConverter;
	}

	protected Converter<OrderModel, OrderData> getOrderConverter()
	{
		return orderConverter;
	}

	@Required
	public void setOrderConverter(final Converter<OrderModel, OrderData> orderConverter)
	{
		this.orderConverter = orderConverter;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	protected Converter<CardType, CardTypeData> getCardTypeConverter()
	{
		return cardTypeConverter;
	}

	@Required
	public void setCardTypeConverter(final Converter<CardType, CardTypeData> cardTypeConverter)
	{
		this.cardTypeConverter = cardTypeConverter;
	}

	protected CheckoutCustomerStrategy getCheckoutCustomerStrategy()
	{
		return checkoutCustomerStrategy;
	}

	@Required
	public void setCheckoutCustomerStrategy(final CheckoutCustomerStrategy checkoutCustomerStrategy)
	{
		this.checkoutCustomerStrategy = checkoutCustomerStrategy;
	}

	protected Converter<CountryModel, CountryData> getCountryConverter()
	{
		return countryConverter;
	}

	@Required
	public void setCountryConverter(final Converter<CountryModel, CountryData> countryConverter)
	{
		this.countryConverter = countryConverter;
	}
}

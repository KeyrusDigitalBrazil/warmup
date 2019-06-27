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
package de.hybris.platform.ordermanagementfacades.order.converters.populator;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.impersonation.ImpersonationContext;
import de.hybris.platform.commerceservices.impersonation.ImpersonationService;
import de.hybris.platform.commerceservices.storefinder.StoreFinderService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.DeliveryModeService;
import de.hybris.platform.ordermanagementfacades.order.data.OrderEntryRequestData;
import de.hybris.platform.ordermanagementfacades.order.data.OrderRequestData;
import de.hybris.platform.ordermanagementfacades.payment.data.PaymentTransactionData;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;


/**
 * Ordermanagementfacade populator for converting {@link OrderRequestData}
 */
public class OrderRequestReversePopulator implements Populator<OrderRequestData, OrderModel>
{
	protected static final Logger LOGGER = LoggerFactory.getLogger(OrderRequestReversePopulator.class);
	private Converter<OrderEntryRequestData, OrderEntryModel> orderEntryRequestReverseConverter;
	private Converter<AddressData, AddressModel> addressReverseConverter;
	private Converter<PaymentTransactionData, PaymentTransactionModel> paymentTransactionReverseConverter;
	private BaseSiteService baseSiteService;
	private BaseStoreService baseStoreService;
	private CommonI18NService commonI18NService;
	private DeliveryModeService deliveryModeService;
	private UserService userService;
	private ImpersonationService impersonationService;
	private ProductService productService;
	private StoreFinderService storeFinderService;
	private ModelService modelService;

	@Override
	public void populate(final OrderRequestData source, final OrderModel target) throws ConversionException
	{
		if (source != null && target != null)
		{
			addCommon(source, target);
			addEntries(source, target);
			addDeliveryAddress(source.getDeliveryAddress(), target);
			if (source.getDeliveryAddress() != null)
			{
				addPaymentAddress(source.getPaymentAddress(), target);
			}
			addDeliveryMethod(source.getDeliveryModeCode(), target);
			addDeliveryStatus(source, target);
			addTotals(source, target);
			addCurrency(source, target);
			addPaymentInformation(source, target);
			target.setLanguage(getCommonI18NService().getLanguage(source.getLanguageIsocode()));
		}
	}

	/**
	 * Converts the basic properties of the {@link OrderRequestData}
	 *
	 * @param source
	 * 		the {@link OrderRequestData} to be converted
	 * @param target
	 * 		the converted {@link OrderModel} from {@link OrderRequestData}
	 */
	protected void addCommon(final OrderRequestData source, final OrderModel target)
	{
		target.setCode(source.getExternalOrderCode());
		target.setName(source.getName());
		target.setUser(getUserService().getUserForUID(source.getUser().getUid()));
		target.setDescription(source.getDescription());
		target.setExpirationTime(source.getExpirationTime());
		target.setSite(getBaseSiteService().getBaseSiteForUID(source.getSiteUid()));
		target.setStore(getBaseStoreService().getBaseStoreForUid(source.getStoreUid()));
		target.setNet(source.isNet());
		target.setGuid(source.getGuid());
		target.setCalculated(source.isCalculated());
		target.setDate(new Date());
	}

	/**
	 * Extracts payment information from the {@link OrderRequestData} and assigns it to {@link OrderModel}
	 *
	 * @param source
	 * 		the {@link OrderRequestData}
	 * @param target
	 * 		the {@link OrderModel}
	 */
	protected void addPaymentInformation(final OrderRequestData source, final OrderModel target)
	{
		final List<PaymentTransactionModel> paymentTransactions = new ArrayList<>();
		source.getPaymentTransactions().forEach(paymentTransactionData -> {
			validateParameterNotNullStandardMessage("paymentTransactionData.getPaymentInfo()",
					paymentTransactionData.getPaymentInfo());
			final PaymentTransactionModel paymentTransaction = getModelService().create(PaymentTransactionModel.class);
			getPaymentTransactionReverseConverter().convert(paymentTransactionData, paymentTransaction);
			paymentTransaction.setCode(target.getUser().getUid() + UUID.randomUUID());
			paymentTransaction.getInfo().setCode(target.getUser().getUid() + UUID.randomUUID());
			paymentTransaction.getInfo().setUser(target.getUser());
			paymentTransactions.add(paymentTransaction);
		});
		target.setPaymentTransactions(paymentTransactions);
		target.setPaymentInfo(target.getPaymentTransactions().iterator().next().getInfo());

	}

	/**
	 * Converts {@link OrderEntryRequestData} into {@link OrderEntryModel} and assigns it to the passed {@link OrderModel}
	 *
	 * @param source
	 * 		the {@link OrderRequestData} containing {@link OrderEntryRequestData}(s), to be converted
	 * @param target
	 * 		the final {@link OrderModel}, containing the converted {@link OrderEntryModel}
	 */
	protected void addEntries(final OrderRequestData source, final OrderModel target)
	{
		final List<AbstractOrderEntryModel> orderEntries = new ArrayList<>();
		source.getEntries().forEach(sourceEntry -> {
			final OrderEntryModel targetEntry = getOrderEntryRequestReverseConverter().convert(sourceEntry);
			orderEntries.add(targetEntry);

			targetEntry.setOrder(target);
			addDeliveryPoSForOrderEntry(sourceEntry, targetEntry);
			addProductForOrderEntry(sourceEntry, targetEntry);
		});

		target.setEntries(orderEntries);
	}

	/**
	 * Converts {@link AddressData} into {@link AddressModel} and assigns it to the passed {@link OrderModel}
	 *
	 * @param source
	 * 		the requested delivery address
	 * @param target
	 * 		the {@link OrderModel}, to which this deliveryAddress is assigned
	 */
	protected void addDeliveryAddress(final AddressData source, final OrderModel target)
	{
		final AddressModel deliveryAddress = addCommonAddress(source);
		deliveryAddress.setOwner(target);
		deliveryAddress.setShippingAddress(true);
		target.setDeliveryAddress(deliveryAddress);
	}

	/**
	 * Converts {@link AddressData} into {@link AddressModel} and assigns it to the passed {@link OrderModel}
	 *
	 * @param source
	 * 		the requested payment address
	 * @param target
	 * 		the {@link OrderModel}, to which this payment is assigned
	 */
	protected void addPaymentAddress(final AddressData source, final OrderModel target)
	{
		final AddressModel paymentAddress = addCommonAddress(source);
		paymentAddress.setOwner(target);
		paymentAddress.setBillingAddress(true);
		target.setPaymentAddress(paymentAddress);
	}

	/**
	 * Converts common attribute for {@link AddressData}
	 *
	 * @param source
	 * 		the requested address
	 * @return {@link AddressData}
	 */
	protected AddressModel addCommonAddress(final AddressData source)
	{
		final AddressModel address = getAddressReverseConverter().convert(source);
		address.setStreetname(source.getLine1());
		address.setStreetnumber(source.getLine2());
		return address;
	}

	/**
	 * Extracts {@link OrderModel#DELIVERYMODE} from the passed deliveryModeCode and assigns it to the {@link OrderModel}
	 *
	 * @param deliveryModeCode
	 * 		the string equivalent of {@link de.hybris.platform.core.model.order.delivery.DeliveryModeModel#CODE}
	 * @param target
	 * 		the {@link OrderModel}, to which this deliveryMode is assigned
	 */
	protected void addDeliveryMethod(final String deliveryModeCode, final OrderModel target)
	{
		target.setDeliveryMode(getDeliveryModeService().getDeliveryModeForCode(deliveryModeCode));
	}

	/**
	 * Assigns {@link OrderModel#DELIVERYSTATUS} to the {@link OrderModel}, from the passed {@link OrderRequestData}
	 *
	 * @param source
	 * 		the {@link OrderRequestData}
	 * @param target
	 * 		the {@link OrderModel}
	 */
	protected void addDeliveryStatus(final OrderRequestData source, final OrderModel target)
	{
		target.setDeliveryStatus(source.getDeliveryStatus());
	}

	/**
	 * Extracts {@link OrderModel#TOTALPRICE}, {@link OrderModel#TOTALTAX}, {@link OrderModel#DELIVERYCOST} from t{@link OrderRequestData}, and
	 * assign it to the {@link OrderModel}.
	 *
	 * @param source
	 * 		the {@link OrderRequestData}
	 * @param target
	 * 		the {@link OrderModel}
	 */
	protected void addTotals(final OrderRequestData source, final OrderModel target)
	{

		target.setTotalPrice(source.getTotalPrice());
		target.setSubtotal(source.getSubtotal());
		target.setTotalTax(source.getTotalTax());
		target.setDeliveryCost(source.getDeliveryModeCode() != null ? source.getDeliveryCost() : null);
	}

	/**
	 * Assigns {@link OrderModel#CURRENCY} to the {@link OrderModel}, from the passed {@link OrderRequestData}
	 *
	 * @param source
	 * 		the {@link OrderRequestData}
	 * @param target
	 * 		the {@link OrderModel}
	 */
	protected void addCurrency(final OrderRequestData source, final OrderModel target)
	{
		final CurrencyModel currency = getCommonI18NService().getCurrency(source.getCurrencyIsocode());
		if (currency == null)
		{
			throw new IllegalArgumentException("Order currency must not be null");
		}
		target.setCurrency(currency);
	}

	/**
	 * Extracts {@link OrderEntryModel#PRODUCT} from the passed source({@link OrderEntryRequestData}), and assigns it to the target({@link OrderEntryModel})
	 *
	 * @param source
	 * 		the {@link OrderEntryRequestData}
	 * @param target
	 * 		the {@link OrderEntryModel}
	 */
	protected void addProductForOrderEntry(final OrderEntryRequestData source, final OrderEntryModel target)
	{
		final ImpersonationContext context = new ImpersonationContext();
		context.setUser(target.getOrder().getUser());
		context.setSite(target.getOrder().getSite());
		final ProductModel product = getImpersonationService()
				.executeInContext(context, () -> getProductService().getProductForCode(source.getProductCode()));
		target.setProduct(product);
	}

	/**
	 * Extracts {@link OrderEntryModel#DELIVERYPOINTOFSERVICE} from the passed source({@link OrderEntryRequestData}), and assigns it to the target({@link OrderEntryModel})
	 *
	 * @param source
	 * 		the {@link OrderEntryRequestData}
	 * @param target
	 * 		the {@link OrderEntryModel}
	 */
	protected void addDeliveryPoSForOrderEntry(final OrderEntryRequestData source, final OrderEntryModel target)
	{
		if (source.getDeliveryPointOfService() != null)
		{
			target.setDeliveryPointOfService(getStoreFinderService()
					.getPointOfServiceForName(target.getOrder().getStore(), source.getDeliveryPointOfService()));
		}
	}

	protected Converter<OrderEntryRequestData, OrderEntryModel> getOrderEntryRequestReverseConverter()
	{
		return this.orderEntryRequestReverseConverter;
	}

	@Required
	public void setOrderEntryRequestReverseConverter(
			final Converter<OrderEntryRequestData, OrderEntryModel> orderEntryRequestReverseConverter)
	{
		this.orderEntryRequestReverseConverter = orderEntryRequestReverseConverter;
	}

	protected Converter<AddressData, AddressModel> getAddressReverseConverter()
	{
		return addressReverseConverter;
	}

	@Required
	public void setAddressReverseConverter(final Converter<AddressData, AddressModel> addressReverseConverter)
	{
		this.addressReverseConverter = addressReverseConverter;
	}

	protected Converter<PaymentTransactionData, PaymentTransactionModel> getPaymentTransactionReverseConverter()
	{
		return paymentTransactionReverseConverter;
	}

	@Required
	public void setPaymentTransactionReverseConverter(
			final Converter<PaymentTransactionData, PaymentTransactionModel> paymentTransactionReverseConverter)
	{
		this.paymentTransactionReverseConverter = paymentTransactionReverseConverter;
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
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

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	protected DeliveryModeService getDeliveryModeService()
	{
		return deliveryModeService;
	}

	@Required
	public void setDeliveryModeService(final DeliveryModeService deliveryModeService)
	{
		this.deliveryModeService = deliveryModeService;
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

	protected ImpersonationService getImpersonationService()
	{
		return impersonationService;
	}

	@Required
	public void setImpersonationService(final ImpersonationService impersonationService)
	{
		this.impersonationService = impersonationService;
	}

	protected ProductService getProductService()
	{
		return productService;
	}

	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	protected StoreFinderService getStoreFinderService()
	{
		return storeFinderService;
	}

	@Required
	public void setStoreFinderService(StoreFinderService storeFinderService)
	{
		this.storeFinderService = storeFinderService;
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
}

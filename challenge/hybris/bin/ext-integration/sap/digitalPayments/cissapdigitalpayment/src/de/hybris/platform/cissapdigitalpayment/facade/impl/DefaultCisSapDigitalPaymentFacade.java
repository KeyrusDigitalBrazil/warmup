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
package de.hybris.platform.cissapdigitalpayment.facade.impl;

import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentPollRegisteredCardResult;
import de.hybris.platform.cissapdigitalpayment.constants.CisSapDigitalPaymentConstant;
import de.hybris.platform.cissapdigitalpayment.facade.CisSapDigitalPaymentFacade;
import de.hybris.platform.cissapdigitalpayment.service.CisSapDigitalPaymentService;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Default implementation of {@link CisSapDigitalPaymentFacade}
 */
public class DefaultCisSapDigitalPaymentFacade implements CisSapDigitalPaymentFacade
{

	private static final Logger LOG = LoggerFactory.getLogger(DefaultCisSapDigitalPaymentFacade.class);

	private CisSapDigitalPaymentService cisSapDigitalPaymentService;
	private SessionService sessionService;
	private Converter<CisSapDigitalPaymentPollRegisteredCardResult, CCPaymentInfoData> cisSapDigitalPaymentPaymentInfoConverter;


	private CartService cartService;
	private Converter<AddressData, AddressModel> paymentAddressReverseConverter;

	private Converter<AddressModel, AddressData> paymentAddressConverter;
	private ModelService modelService;
	private UserService userService;
	private CalculationService calculationService;

	@Override
	public String getSapDigitalPaymentRegisterCardSession()
	{
		return getSessionService().getAttribute(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_REG_CARD_SESSION_ID);
	}

	@Override
	public void removeSapDigitalPaymentRegisterCardSession()
	{
		getSessionService().removeAttribute(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_REG_CARD_SESSION_ID);

	}

	@Override
	public void addPaymentAddressToCart(final AddressData paymentAddress)
	{
		if (null != paymentAddress)
		{
			final CartModel cart = getCartService().getSessionCart();

			final AddressModel paymentAddressModel = getPaymentAddressReverseConverter().convert(paymentAddress);
			if (null != paymentAddressModel && getModelService().isNew(paymentAddressModel))
			{
				paymentAddressModel.setOwner(getUserService().getCurrentUser());
				//Do not show the payment address to the address book.
				paymentAddressModel.setVisibleInAddressBook(Boolean.FALSE);
			}
			cart.setPaymentAddress(paymentAddressModel);
			getModelService().save(cart);
			try
			{
				getCalculationService().recalculate(cart);
			}
			catch (final CalculationException e)
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug(String.format("Error while recalculating the cart [%s]", e));
				}
				LOG.error(String.format("Error while recalculating the cart [%s]", e.getMessage()));

			}

		}
	}




	/**
	 * @return the sessionService
	 */
	public SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * @param sessionService
	 *           the sessionService to set
	 */
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	/**
	 * @return the cisSapDigitalPaymentService
	 */
	public CisSapDigitalPaymentService getCisSapDigitalPaymentService()
	{
		return cisSapDigitalPaymentService;
	}

	/**
	 * @param cisSapDigitalPaymentService
	 *           the cisSapDigitalPaymentService to set
	 */
	public void setCisSapDigitalPaymentService(final CisSapDigitalPaymentService cisSapDigitalPaymentService)
	{
		this.cisSapDigitalPaymentService = cisSapDigitalPaymentService;
	}

	/**
	 * @return the cisSapDigitalPaymentPaymentInfoConverter
	 */
	public Converter<CisSapDigitalPaymentPollRegisteredCardResult, CCPaymentInfoData> getCisSapDigitalPaymentPaymentInfoConverter()
	{
		return cisSapDigitalPaymentPaymentInfoConverter;
	}

	/**
	 * @param cisSapDigitalPaymentPaymentInfoConverter
	 *           the cisSapDigitalPaymentPaymentInfoConverter to set
	 */
	public void setCisSapDigitalPaymentPaymentInfoConverter(
			final Converter<CisSapDigitalPaymentPollRegisteredCardResult, CCPaymentInfoData> cisSapDigitalPaymentPaymentInfoConverter)
	{
		this.cisSapDigitalPaymentPaymentInfoConverter = cisSapDigitalPaymentPaymentInfoConverter;
	}



	/**
	 * @return the cartService
	 */
	public CartService getCartService()
	{
		return cartService;
	}

	/**
	 * @param cartService
	 *           the cartService to set
	 */
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	/**
	 * @return the paymentAddressReverseConverter
	 */
	public Converter<AddressData, AddressModel> getPaymentAddressReverseConverter()
	{
		return paymentAddressReverseConverter;
	}

	/**
	 * @param paymentAddressReverseConverter
	 *           the paymentAddressReverseConverter to set
	 */
	public void setPaymentAddressReverseConverter(final Converter<AddressData, AddressModel> paymentAddressReverseConverter)
	{
		this.paymentAddressReverseConverter = paymentAddressReverseConverter;
	}

	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return the paymentAddressConverter
	 */
	public Converter<AddressModel, AddressData> getPaymentAddressConverter()
	{
		return paymentAddressConverter;
	}

	/**
	 * @param paymentAddressConverter
	 *           the paymentAddressConverter to set
	 */
	public void setPaymentAddressConverter(final Converter<AddressModel, AddressData> paymentAddressConverter)
	{
		this.paymentAddressConverter = paymentAddressConverter;
	}

	/**
	 * @return the userService
	 */
	public UserService getUserService()
	{
		return userService;
	}

	/**
	 * @param userService
	 *           the userService to set
	 */
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	/**
	 * @return the calculationService
	 */
	public CalculationService getCalculationService()
	{
		return calculationService;
	}

	/**
	 * @param calculationService
	 *           the calculationService to set
	 */
	public void setCalculationService(final CalculationService calculationService)
	{
		this.calculationService = calculationService;
	}


}

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
package de.hybris.platform.savedorderforms.api.orderform;

import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.savedorderforms.orderform.data.OrderFormData;

import java.util.List;


/**
 * Order form facade interface. Service is responsible for getting all necessary information for order forms.
 *
 */
public interface OrderFormFacade
{

	/**
	 * Creates an Order Form
	 *
	 * @param orderForm
	 *           the Order Form Data
	 * @return {@link OrderFormData}
	 */
	OrderFormData createOrderForm(OrderFormData orderForm);

	/**
	 * Updates an Order Form
	 *
	 * @param code
	 *           the Order Form code
	 * @param orderForm
	 *           the Order Form Data
	 * @return {@link OrderFormData}
	 */
	OrderFormData updateOrderForm(String code, OrderFormData orderForm);

	/**
	 * Finds an Order Form using its code.
	 *
	 * @param code
	 *           order form code
	 * @return {@link OrderFormData}
	 * @throws {@link
	 *            de.hybris.platform.savedorderforms.exception.DomainException} if order form is not found.
	 */
	OrderFormData getOrderFormForCode(String code);

	/**
	 * Gets a list of Order Forms for the current user.
	 *
	 * @return {@link List} of type {@link OrderFormData}.
	 */
	List<OrderFormData> getOrderFormsForCurrentUser();

	/**
	 * Deletes an Order Form using its code.
	 *
	 * @param code
	 *           order code
	 */
	void removeOrderForm(String code);

	/**
	 * Adds a saved order form to a given cart.
	 *
	 * @param orderFormCode
	 *           order form code
	 * @param cartId
	 *           Id of the cart
	 *
	 * @throws CommerceCartModificationException
	 */
	void addOrderFormToCart(String orderFormCode, String cartId) throws CommerceCartModificationException;
}

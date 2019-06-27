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
package de.hybris.platform.sap.c4c.quote.order.strategies.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCartCalculationStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.exceptions.CalculationException;

public class DefaultC4CCommerceCartCalculationStrategy extends DefaultCommerceCartCalculationStrategy {

        private C4CQuoteRequiresCalculationStrategy quoteRequiresCalculationStrategy;
        /**
         * @deprecated Since 5.2.
         */
        @Override
        @Deprecated
        public boolean calculateCart(final CartModel cartModel) {
                final CommerceCartParameter parameter = new CommerceCartParameter();
                parameter.setEnableHooks(true);
                parameter.setCart(cartModel);
                return this.calculateCart(parameter);
        }

        @Override
        public boolean calculateCart(final CommerceCartParameter parameter) {
                final CartModel cartModel = parameter.getCart();

                validateParameterNotNull(cartModel, "Cart model cannot be null");

                final CalculationService calcService = getCalculationService();
                boolean recalculated = false;
                if (getQuoteRequiresCalculationStrategy().shouldCalculateAllValues(cartModel)) {
                        super.calculateCart(parameter);
                } else if (calcService.requiresCalculation(cartModel)) {

                        try {
                                calcService.calculate(cartModel);
                        } catch (final CalculationException calculationException) {
                                throw new IllegalStateException("Cart model " + cartModel.getCode() + " was not calculated due to: "
                                                + calculationException.getMessage(), calculationException);
                        } 
                        recalculated = true;
                }

                if (isCalculateExternalTaxes()) {
                        getExternalTaxesService().calculateExternalTaxes(cartModel);
                }
                return recalculated;
        }

        public C4CQuoteRequiresCalculationStrategy getQuoteRequiresCalculationStrategy() {
                return quoteRequiresCalculationStrategy;
        }

        public void setQuoteRequiresCalculationStrategy(C4CQuoteRequiresCalculationStrategy quoteRequiresCalculationStrategy) {
                this.quoteRequiresCalculationStrategy = quoteRequiresCalculationStrategy;
        }
}
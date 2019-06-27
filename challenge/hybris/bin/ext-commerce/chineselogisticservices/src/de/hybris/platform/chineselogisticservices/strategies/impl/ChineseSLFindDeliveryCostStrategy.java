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
package de.hybris.platform.chineselogisticservices.strategies.impl;

import de.hybris.platform.chineselogisticservices.delivery.impl.ChineseDeliveryService;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.order.strategies.calculation.impl.servicelayer.DefaultSLFindDeliveryCostStrategy;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.util.PriceValue;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * This strategy defines the process of looking up the delivery cost for SL.
 */
public class ChineseSLFindDeliveryCostStrategy extends DefaultSLFindDeliveryCostStrategy {

    private static final Logger LOG = Logger.getLogger(ChineseSLFindDeliveryCostStrategy.class);

    private ChineseDeliveryService chineseDeliveryService;
    private ChineseDeliveryModeLookupStrategy chineseDeliveryModeLookupStrategy;

    @Override
    protected PriceValue getDeliveryCostValue(final AbstractOrderModel order) throws CalculationException
    {
        ServicesUtil.validateParameterNotNullStandardMessage("order", order);
        if (order.getStore() == null || order.getStore().getPickupInStoreMode() == null)
        {
                return super.getDeliveryCostValue(order);
        }

        final List<DeliveryModeModel> deliveryModeModels = getChineseDeliveryModeLookupStrategy().getSelectableDeliveryModesForOrder(order);

        if (deliveryModeModels != null && !deliveryModeModels.isEmpty())
        {
            try
            {
                final DeliveryModeModel deliveryModeModel = order.getDeliveryMode();
                if (deliveryModeModel instanceof ZoneDeliveryModeModel)
                {
                    return getChineseDeliveryService().getDeliveryCostForDeliveryModeAndAbstractOrder(deliveryModeModel, order);
                }
                else
                {
                    return new PriceValue(order.getCurrency().getIsocode(), 0.0, order.getNet().booleanValue());
                }

            }
            //Catch root exception as the super implementation
            catch (final Exception e)//NOSONAR
            {
                LOG.warn("Could not find deliveryCost for order [" + order.getCode() + "] due to : " + e.getMessage()
                        + "... skipping!");
                return new PriceValue(order.getCurrency().getIsocode(), 0.0, order.getNet().booleanValue());
            }
        }

        if (order.getCurrency() == null)
        {
                throw new CalculationException("getCost(): currency was NULL in order " + order);
        }
        return new PriceValue(order.getCurrency().getIsocode(), 0.0, order.getNet().booleanValue());

    }

    protected ChineseDeliveryService getChineseDeliveryService() {
        return chineseDeliveryService;
    }

    @Required
    public void setChineseDeliveryService(final ChineseDeliveryService chineseDeliveryService) {
        this.chineseDeliveryService = chineseDeliveryService;
    }

    protected ChineseDeliveryModeLookupStrategy getChineseDeliveryModeLookupStrategy()
    {
        return this.chineseDeliveryModeLookupStrategy;
    }

    @Required
    public void setChineseDeliveryModeLookupStrategy(final ChineseDeliveryModeLookupStrategy chineseDeliveryModeLookupStrategy) {
        this.chineseDeliveryModeLookupStrategy = chineseDeliveryModeLookupStrategy;
    }
}

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
package de.hybris.platform.sap.saporderexchangeoms.datahub.inbound.events;

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.sap.orderexchange.inbound.events.DataHubTranslator;
import de.hybris.platform.sap.saporderexchangeoms.datahub.inbound.SapDataHubInboundStockLevelHelper;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Translator to update the stock level in the platform after the stock value is replicated from ERP.
 * After the stock replication from ERP, we have one of the following scenarios:
 * 1. Not shipped allocations + hybris ATP >  ERP Stock Level  -> Increase inventory event (-the difference)
 * 2. Not shipped allocations + hybris ATP <  ERP Stock Level  -> Increase inventory event (the difference)
 * 3. Not shipped allocations + hybris ATP =  ERP Stock Level  -> Do not do anything
 */
public class SapOmsDataHubStockLevelTranslator extends DataHubTranslator<SapDataHubInboundStockLevelHelper> {

    public static final String HELPER_BEAN = "sapDataHubInboundStockLevelHelper";

    public SapOmsDataHubStockLevelTranslator() {
        super(HELPER_BEAN);
    }

    @Override
    public void performImport(String stockLevelQuantity, Item stockLevelItem) throws ImpExException {

        validateParameterNotNull(stockLevelItem, " The imported stockLevelItem must not be null!");
        validateParameterNotNull(stockLevelQuantity, " The imported stockLevelQuantity must not be null!");
        getInboundHelper().processStockLevelNotification(stockLevelQuantity,stockLevelItem);

    }
}
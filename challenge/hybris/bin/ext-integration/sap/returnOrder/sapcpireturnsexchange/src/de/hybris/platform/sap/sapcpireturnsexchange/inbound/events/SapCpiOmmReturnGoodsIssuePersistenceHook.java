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
package de.hybris.platform.sap.sapcpireturnsexchange.inbound.events;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.sap.sapcpireturnsexchange.constants.SapcpireturnsexchangeConstants;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.returnsexchange.inbound.DataHubInboundOrderHelper;

public class SapCpiOmmReturnGoodsIssuePersistenceHook implements PrePersistHook {

    private DataHubInboundOrderHelper sapDataHubInboundReturnOrderHelper;

    @Override
    public Optional<ItemModel> execute(final ItemModel item) {
        if (item instanceof ReturnRequestModel) {
            final ReturnRequestModel retrunModel = (ReturnRequestModel) item;
            final String[] orderCodeAndDocInfo = getRetrunOrderNumnerAndDocInfo(retrunModel.getCode());
            if (orderCodeAndDocInfo != null && orderCodeAndDocInfo.length > 1) {
                getSapDataHubInboundReturnOrderHelper().processOrderDeliveryNotififcationFromDataHub(
                        orderCodeAndDocInfo[0].trim(), retrunModel.getCode());
                return Optional.empty();
            }

        }
        return Optional.of(item);
    }

    /**
     *
     */
    private String[] getRetrunOrderNumnerAndDocInfo(final String code) {
        return code.split(SapcpireturnsexchangeConstants.SEPERATING_SYMBOL);

    }

    /**
     * @return the sapDataHubInboundReturnOrderHelper
     */
    public DataHubInboundOrderHelper getSapDataHubInboundReturnOrderHelper() {
        return sapDataHubInboundReturnOrderHelper;
    }

    /**
     * @param sapDataHubInboundReturnOrderHelper
     *            the sapDataHubInboundReturnOrderHelper to set
     */
    @Required
    public void setSapDataHubInboundReturnOrderHelper(
            final DataHubInboundOrderHelper sapDataHubInboundReturnOrderHelper) {
        this.sapDataHubInboundReturnOrderHelper = sapDataHubInboundReturnOrderHelper;
    }

}

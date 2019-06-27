package com.sap.hybris.sec.eventpublisher.b2b.handler.impl;

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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.sap.hybris.sec.eventpublisher.b2b.constants.Eventpublisherb2bConstants;
import com.sap.hybris.sec.eventpublisher.data.ResponseData;
import com.sap.hybris.sec.eventpublisher.handler.impl.DefaultSaveEventHandler;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.tx.AfterSaveEvent;

public class AfterB2BUnitSaveEventHandler extends DefaultSaveEventHandler{
	
	@Override
	public ResponseData handle(AfterSaveEvent event, ItemModel model) throws Exception{
		ResponseData resData = null;
		if ((event.getType() == AfterSaveEvent.CREATE || event.getType() == AfterSaveEvent.UPDATE)
				&& (model instanceof B2BUnitModel)) {
			B2BUnitModel unitModel = (B2BUnitModel) model;
			Map<String, Object> map = new HashMap<String, Object>();
			Set<PrincipalGroupModel> groups = unitModel.getGroups();
			for(PrincipalGroupModel group : groups) {
				if(group instanceof B2BUnitModel) {
					map.put(Eventpublisherb2bConstants.PARENT_B2B_UNIT_ID, ((B2BUnitModel) group).getUid());
					map.put(Eventpublisherb2bConstants.PARENT_B2B_UNIT_PK, ((B2BUnitModel) group).getPk().toString());
				}
			}
			String json = getFinalJson(model, map);
			resData = publish(json, Eventpublisherb2bConstants.B2BUNIT);
		}
		return resData;
	}

}

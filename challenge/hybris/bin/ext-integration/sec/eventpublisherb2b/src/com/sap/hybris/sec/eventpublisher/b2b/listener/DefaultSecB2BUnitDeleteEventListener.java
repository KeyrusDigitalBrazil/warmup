package com.sap.hybris.sec.eventpublisher.b2b.listener;

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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.hybris.sec.eventpublisher.b2b.constants.Eventpublisherb2bConstants;
import com.sap.hybris.sec.eventpublisher.b2b.event.DefaultSecDeleteB2BUnitEvent;
import com.sap.hybris.sec.eventpublisher.publisher.Publisher;

import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;

public class DefaultSecB2BUnitDeleteEventListener extends AbstractEventListener<DefaultSecDeleteB2BUnitEvent>{

	private static final Logger LOGGER = LogManager.getLogger(DefaultSecB2BUnitDeleteEventListener.class);

	private Publisher hciPublisher;

	@Override
	protected void onEvent(DefaultSecDeleteB2BUnitEvent event) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", event.getB2bUnitUid());
		map.put("pk", event.getB2bUnitPk());
		map.put("isDelete", "true");
		LOGGER.info("SEC Deleting B2BUnit ID : "+event.getB2bUnitUid());
		try {
			getHciPublisher().publishJson(convertMapToJson(map), Eventpublisherb2bConstants.B2BUNIT);
		} catch (Exception e) {
			LOGGER.error("Failed to replicate B2BUnit", e);
		}
		
	}


	public String convertMapToJson(Map<String, Object> map) throws IOException{
		ObjectMapper mapperObj = new ObjectMapper();
		return mapperObj.writeValueAsString(map);
	}

	public Publisher getHciPublisher() {
		return hciPublisher;
	}

	public void setHciPublisher(Publisher hciPublisher) {
		this.hciPublisher = hciPublisher;
	}

	
}

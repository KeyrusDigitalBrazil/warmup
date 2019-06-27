package com.sap.hybris.sec.eventpublisher.b2b.event;

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

import de.hybris.platform.servicelayer.event.ClusterAwareEvent;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;

public class DefaultSecDeleteB2BUnitEvent extends AbstractEvent implements ClusterAwareEvent {
	private String b2bUnitUid;
	private String b2bUnitPk;
	
	@Override
	public boolean publish(int sourceNodeId, int targetNodeId) {
		return sourceNodeId == targetNodeId;
	}
	
	public String getB2bUnitUid() {
		return b2bUnitUid;
	}

	public void setB2bUnitUid(String b2bUnitUid) {
		this.b2bUnitUid = b2bUnitUid;
	}

	public String getB2bUnitPk() {
		return b2bUnitPk;
	}

	public void setB2bUnitPk(String b2bUnitPk) {
		this.b2bUnitPk = b2bUnitPk;
	}

}

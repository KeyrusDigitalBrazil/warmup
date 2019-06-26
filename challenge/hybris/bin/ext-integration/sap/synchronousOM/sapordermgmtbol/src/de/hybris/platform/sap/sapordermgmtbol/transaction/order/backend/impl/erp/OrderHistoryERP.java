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
package de.hybris.platform.sap.sapordermgmtbol.transaction.order.backend.impl.erp;

import de.hybris.platform.sap.core.bol.backend.BackendType;
import de.hybris.platform.sap.sapordermgmtbol.transaction.order.backend.interf.OrderHistoryBackend;


/**
 * Back end Object representing an persisted ERP Order document used in order history (different connection compared to
 * checkout)
 */
@BackendType("ERP")
public class OrderHistoryERP extends OrderBaseERP implements OrderHistoryBackend
{
	//
}

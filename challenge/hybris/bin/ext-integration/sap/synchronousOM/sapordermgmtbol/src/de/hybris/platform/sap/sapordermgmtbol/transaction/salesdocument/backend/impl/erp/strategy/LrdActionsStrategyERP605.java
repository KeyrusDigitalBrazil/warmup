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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy;

import java.util.List;


/**
 * In case of ECC 605, we also request payment service provider information from the EPR backend
 *
 *
 */
public class LrdActionsStrategyERP605 extends LrdActionsStrategyERP
{

	/**
	 * Default constructor
	 */
	public LrdActionsStrategyERP605()
	{
		super();
		setActiveFieldsListCreateChange605(activeFieldsListCreateChange);
	}


	private static void setActiveFieldsListCreateChange605(final List<SetActiveFieldsListEntry> activeFieldsListCreateChange)
	{
		// HEAD
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("HEAD", "SPPAYM"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("LPAYSP", "PS_PROVIDER"));
	}

}

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
package de.hybris.platform.sap.sapordermgmtbol.hook;

import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;


/**
 * Hook interface for SalesDocumentERP
 */
public interface SalesDocumentERPHook
{

	/**
	 * @param salesDocument
	 * @param aJCoCon
	 */
	void afterWriteDocument(SalesDocument salesDocument, JCoConnection aJCoCon);

	/**
	 * @param salesDocument
	 * @param aJCoCon
	 */
	void afterReadFromBackend(SalesDocument salesDocument, JCoConnection aJCoCon);

}

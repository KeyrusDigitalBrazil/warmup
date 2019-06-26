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
package de.hybris.platform.sap.core.bol.businessobject.test.be;

import de.hybris.platform.sap.core.bol.backend.BackendBusinessObjectBase;
import de.hybris.platform.sap.core.bol.backend.BackendType;


/**
 * Test BackendBusinedssObjectBase implementation - for backend type determination test.
 */
@BackendType("CRM")
public class TestBackendBusinessObjectBaseBENotUniqueDeterminationCRMImpl extends BackendBusinessObjectBase implements
		TestBackendInterfaceBENotUniqueDetermination
{
	// only for testing
}

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
package de.hybris.platform.assistedserviceservices.constants;

/**
 * Global class for all Assistedserviceservices constants. You can add global constants for your extension into this
 * class.
 */
public final class AssistedserviceservicesConstants extends GeneratedAssistedserviceservicesConstants
{
	public static final String EXTENSIONNAME = "assistedserviceservices";

	// Key to session parameters map for AsmSession object
	public static final String ASM_SESSION_PARAMETER = "ASM";

	// Default parent group id for all AS agents
	public static final String AS_AGENT_GROUP_UID = "asagentgroup";

	// Default group id for all AS sales manager agents
	public static final String AS_MANAGER_AGENT_GROUP_UID = "asagentsalesmanagergroup";

	// Default customer group prefix
	public static final String DEFAULT_CUSTOMER_GROUP_PREFIX = "POS_";

	// Default customer group prefix key
	public static final String DEFAULT_CUSTOMER_GROUP_PREFIX_KEY = "assistedserviceservices.instore.customergroup.prefix";

	// Recent Sessions limit key
	public static final String DEFAULT_RECENT_SESSIONS_LIMIT_KEY = "assistedserviceservices.recent.sessions.limit";

	// Suggested Customers limit
	public static final String DEFAULT_SUGGESTED_CUSTOMERS_LIMIT = "assistedserviceservices.customers.list.limit";

	public static final int DEFAULT_RECENT_SESSIONS_LIMIT = 20;

	// property key for default consignment status
	public static final String DEFAULT_BOPIS_STATUS = "assistedserviceservices.bopis.default.status";

	//  <jsp model property keys>
	public static final String AGENT = "agent";
	public static final String EMULATED_CUSTOMER = "emulatedUser";
	//  </jsp model property keys>

	// ASM sorting options
	public static final String SORT_BY_UID_ASC = "byUidAsc";
	public static final String SORT_BY_UID_DESC = "byUidDesc";
	public static final String SORT_BY_NAME_ASC = "byNameAsc";
	public static final String SORT_BY_NAME_DESC = "byNameDesc";

	private AssistedserviceservicesConstants()
	{
		//empty to avoid instantiating this constant class
	}
}
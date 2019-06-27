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
package de.hybris.platform.cmswebservices.constants;

/**
 * Global class for all Cmswebservices constants. You can add global constants for your extension into this class.
 */
public final class CmswebservicesConstants extends GeneratedCmswebservicesConstants
{
	public static final String EXTENSIONNAME = "cmswebservices";
	public static final String API_VERSION = "/v1";

	public static final String URI_CATALOG_ID = "catalogId";
	public static final String URI_CATALOG_VERSION = "catalogVersion";
	public static final String URI_VERSION_ID = "versionId";
	public static final String URI_UUID = "uuid";
	public static final String URI_ITEM_UUID = "itemUUID";
	public static final String URI_SITE_ID = "siteId";
	public static final String URI_SLOT_ID = "slotId";
	public static final String URI_PAGE_ID = "pageId";
	public static final String URI_PAGE_IDS = "pageIds";
	public static final String URI_PAGE_SIZE = "pageSize";
	public static final String URI_CURRENT_PAGE = "currentPage";
	public static final String QUERY_PARAM_MODE = "mode";
	public static final String MODE_CLONEABLE_TO = "cloneableTo";
	public static final String URI_TYPECODE = "typeCode";
	public static final String URI_SORT = "sort";

	public static final String HEADER_LOCATION = "Location";

	public static final String DOCUMENTATION_TITLE_PROPERTY = EXTENSIONNAME + ".documentation.title";
	public static final String DOCUMENTATION_DESC_PROPERTY = EXTENSIONNAME + ".documentation.desc";
	public static final String TERMS_OF_SERVICE_URL_PROPERTY = EXTENSIONNAME + ".terms.of.service.url";
	public static final String LICENSE_PROPERTY = EXTENSIONNAME + ".licence";
	public static final String LICENSE_URL_PROPERTY = EXTENSIONNAME + ".license.url";
	public static final String DOCUMENTATION_API_VERSION = "1.0";
	public static final String AUTHORIZATION_SCOPE_PROPERTY = EXTENSIONNAME + ".oauth.scope";
	public static final String AUTHORIZATION_URL = "/authorizationserver/oauth/token";
	public static final String PASSWORD_AUTHORIZATION_NAME = "oauth2_password";

	/**
	 * To solve a serialization problem, CMSItemController get Collections returns a map instead of a WsDto So these
	 * define the map keys, which would normally be Dto properties
	 */
	public static final String WSDTO_RESPONSE_PARAM_RESULTS = "response";
	public static final String WSDTO_RESPONSE_PARAM_PAGINATION = "pagination";

	private CmswebservicesConstants()
	{
		//empty to avoid instantiating this constant class
	}

}

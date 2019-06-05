/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package br.com.keyrus.warmup.core.constants;

import de.hybris.platform.util.Config;
import org.apache.commons.lang.Validate;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Global class for all KeyruswarmupCore constants. You can add global constants for your extension into this class.
 */
public final class KeyruswarmupCoreConstants extends GeneratedKeyruswarmupCoreConstants
{
	public static final String EXTENSIONNAME = "keyruswarmupcore";


	private KeyruswarmupCoreConstants()
	{
		//empty
	}

	// implement here constants used by this extension
	public static final String QUOTE_BUYER_PROCESS = "quote-buyer-process";
	public static final String QUOTE_SALES_REP_PROCESS = "quote-salesrep-process";
	public static final String QUOTE_USER_TYPE = "QUOTE_USER_TYPE";
	public static final String QUOTE_SELLER_APPROVER_PROCESS = "quote-seller-approval-process";
	public static final String QUOTE_TO_EXPIRE_SOON_EMAIL_PROCESS = "quote-to-expire-soon-email-process";
	public static final String QUOTE_EXPIRED_EMAIL_PROCESS = "quote-expired-email-process";
	public static final String QUOTE_POST_CANCELLATION_PROCESS = "quote-post-cancellation-process";


	// Hybris challenge constants
	public static final String DEFAULT_PRODUCT_CATALOG_NAME = "electronicsProductCatalog";
	public static final String DEFAULT_PRODUCT_CATALOG_VERSION = "Online";
	public static final String DEFAULT_MEDIA_FOLDER = "images";
	public static final String DEFAULT_MEDIA_FORMAT = "desktop";
	public static final String DEFAULT_MEDIA_MIME_TYPE = "image/jpeg";
	public static final String DEFAULT_STAMP_SEPARATOR = "#";
	public static final List<String> VALID_MEDIA_EXTENSIONS = new ArrayList<>(
			Arrays.asList("jpg", "png"));

	public static File getFolder(final String property) {
		final String folder = Config.getString(property, null);
		Validate.notEmpty(folder, "Parameter " + property + " cannot be empty!");
		return new File(folder);
	}

}

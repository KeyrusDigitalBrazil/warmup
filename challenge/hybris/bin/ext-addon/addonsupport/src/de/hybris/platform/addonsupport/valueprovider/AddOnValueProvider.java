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
package de.hybris.platform.addonsupport.valueprovider;

import java.util.Optional;


/**
 * Interface to provide values between independent AddOns via the {@link AddOnValueProviderRegistry}.
 */
public interface AddOnValueProvider
{
	/**
	 * Returns an {@link Optional} for the given key that contains the value if present.
	 *
	 * @param key
	 *           the key to retrieve the value for
	 * @param type
	 *           the expected type of the value
	 * @return an {@link Optional} that either contains the value or is empty if key or type don't match
	 */
	<T> Optional<T> getValue(String key, Class<T> type);
}

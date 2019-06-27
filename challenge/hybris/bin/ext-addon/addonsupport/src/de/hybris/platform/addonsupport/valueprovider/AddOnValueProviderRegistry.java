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
 * Registry to access value providers by the name of the AddOn that defines them.
 */
public interface AddOnValueProviderRegistry
{
	/**
	 * Returns the value provider for the given AddOn name.
	 *
	 * @param addOnName
	 *           the name of the AddOn extension
	 * @return an {@link Optional} that either contains the value provider or is empty if no provider exists for the
	 *         given AddOn name
	 */
	Optional<AddOnValueProvider> get(String addOnName);
}

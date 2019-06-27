/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.integrationservices.integrationkey;

/**
 * Generates an integrationKey based on a type and an element
 * @param <T> The type
 * @param <E> The element
 */
public interface IntegrationKeyGenerator<T, E>
{
	/**
	 * Generates a string representing the actual string values that the {@code s:Alias}
	 * property references.
	 *
	 * @param type - EntitySet that is being posted to
	 * @param entry - represents the entry information to be used to generate the key.
	 *
	 * @return integrationKey string to set on the oDataEntry
	 */
	String generate(T type, E entry);
}

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
package de.hybris.platform.cmsfacades.common.service;

import java.util.Optional;


/**
 * Interface used to decapitalize Java class names for the purpose of enabling correct unmarshalling in Rest Controllers.
 *
 */
public interface StringDecapitalizer
{
	/**
	 * Decapitalize the string the same way it expects to be used in the deserialization process, for unmarshalling correctness.
	 * @param theClass the java class used to decapitalize the name
	 * @return an {@code Optional} String with the decapitalized java class string name
	 */
	Optional<String> decapitalize(Class<?> theClass);
}

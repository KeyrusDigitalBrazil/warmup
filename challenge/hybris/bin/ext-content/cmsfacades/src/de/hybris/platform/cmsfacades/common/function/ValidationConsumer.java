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
package de.hybris.platform.cmsfacades.common.function;

import java.util.function.BiConsumer;

import org.springframework.validation.Errors;


/**
 * ValidationConsumer is an interface that works along with other Validators to help with the validation work.
 * It extends the Consumer interface and it should perform the validation work.
 * @param <T> the type of the object to be validated
 */
@FunctionalInterface
public interface ValidationConsumer<T> extends BiConsumer<T, Errors>
{
	// Intentionally left empty.
}

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
package de.hybris.platform.acceleratorservices.dataimport.batch.aop;

import de.hybris.platform.acceleratorservices.dataimport.batch.BatchException;
import de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader;

import org.aspectj.lang.ProceedingJoinPoint;


/**
 * Aspect to wrap all exceptions in a {@link BatchException} to preserve the header.
 */
public class ExceptionHandlerAspect
{
	/**
	 * Invokes a method and wraps all Exceptions in a {@link BatchException}.
	 * 
	 * @param pjp
	 * @return result of the invocation
	 * @throws Throwable
	 */
	public Object execute(final ProceedingJoinPoint pjp) throws Throwable // NOSONAR
	{
		final BatchHeader header = AspectUtils.getHeader(pjp.getArgs());
		try
		{
			return pjp.proceed();
		}
		catch (final Exception e)
		{
			throw new BatchException(e.getMessage(), header, e);
		}
	}

}

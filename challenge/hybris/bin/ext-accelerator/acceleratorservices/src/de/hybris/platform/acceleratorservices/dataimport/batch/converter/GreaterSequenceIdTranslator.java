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
package de.hybris.platform.acceleratorservices.dataimport.batch.converter;

/**
 * SequenceId Translator which tolerates equal sequence ids e.g. for localized attributes for which a sequence id
 * attribute cannot be added.
 */
public class GreaterSequenceIdTranslator extends SequenceIdTranslator
{

	@Override
	protected boolean isInValidSequenceId(final Long sequenceId, final Long curSeqId)
	{
		return curSeqId.compareTo(sequenceId) > 0;
	}
}

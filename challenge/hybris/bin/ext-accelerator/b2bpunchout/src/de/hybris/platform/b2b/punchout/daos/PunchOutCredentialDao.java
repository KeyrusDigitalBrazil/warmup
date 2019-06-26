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
package de.hybris.platform.b2b.punchout.daos;

import de.hybris.platform.b2b.punchout.jalo.PunchOutCredential;
import de.hybris.platform.b2b.punchout.model.PunchOutCredentialModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;


/**
 * DAO for entity {@link PunchOutCredential}.
 */
public interface PunchOutCredentialDao
{
	/**
	 * Get a {@link PunchOutCredentialModel} for a given domain-identity pair.
	 * 
	 * @param domain
	 *           The PunchOut domain used for the identity (e.g.: DUNS)
	 * @param identity
	 *           The identity value.
	 * @return The {@link PunchOutCredentialModel}, or null, if there is no matching pair.
	 * @throws AmbiguousIdentifierException
	 *            If there is more the one {@link PunchOutCredentialModel} for the given pair.
	 */
	PunchOutCredentialModel getPunchOutCredential(final String domain, final String identity) throws AmbiguousIdentifierException;
}

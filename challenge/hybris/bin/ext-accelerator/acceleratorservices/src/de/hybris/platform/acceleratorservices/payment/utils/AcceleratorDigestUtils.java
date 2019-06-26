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
package de.hybris.platform.acceleratorservices.payment.utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


/**
 *
 *
 */
public interface AcceleratorDigestUtils
{
	/**
	 * Utility method used for encrypting data used to secure communication with the Payment Provider's server utilizing HmacSHA1 mac algorithm
	 * 
	 * @param customValues
	 *           - a String representation of all the data that requires securing.
	 * @param key
	 *           - a security key provided by PSP used to ensure each transaction is protected during it's
	 *           transmission across the Internet.
	 * @return - an encrypted String that is deemed secure for communication with PSP
	 * @throws java.security.InvalidKeyException
	 *            if the given key is inappropriate for initializing this MAC.
	 * @throws java.security.NoSuchAlgorithmException
	 *            when attempting to get a Message Authentication Code algorithm.
	 */
	String getPublicDigest(String customValues, String key) throws NoSuchAlgorithmException, InvalidKeyException; // NOSONAR
	// It is possible for this class to be extended or for methods to be used in other extensions - so no sonar added
}

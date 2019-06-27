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
import * as CryptoJS from 'crypto-js';
import {SeInjectable} from 'smarteditcommons/services/dependencyInjection/di';

/**
 * @ngdoc service
 * @name functionsModule.service:CryptographicUtils
 *
 * @description
 * utility service around Cryptographic operations.
 */
@SeInjectable()
export class CryptographicUtils {

    /**
     * @ngdoc method
     * @name functionsModule.service:CryptographicUtils#sha1Hash
     * @methodOf functionsModule.service:CryptographicUtils
     *
     * @description
     * A utility function that takes an input string and provides a cryptographic SHA1 hash value.
     * 
     * @param {String} data The input string to be encrypted.
     * @returns {String} the encrypted hashed result.
     */
	sha1Hash(data: string): string {
		return CryptoJS.SHA1(data).toString();
	}

}
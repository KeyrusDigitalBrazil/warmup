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

/** @internal */
export abstract class StorageNamespaceConverter {

    /**
     * Given:
     *  namespace = nmsp
     *  storageId = stoid
     *
     * Produces:
     *  newStorageId = nmsp<ns:id>stoid
     *
     * Fastest implementation I could think of that (most likely) will not clash with weird storageIds
     *
     * This algorithm is a bit overly simple, and assumes that neither storageId nor namespace contains "<ns:id>"
     * I think this is a fairly safe assumption, but if we have time in the future, we should escape any existing
     * matches of the string.
     */

	static ERR_INVALID_NAMESPACED_ID(id: string): Error {
		return new Error(`StorageNamespaceConverter - Invalid namespaced id [${id}]`);
	}

	static getNamespacedStorageId(namespace: string, storageId: string): string {
		return `${namespace}${this.separator}${storageId}`;
	}

	static getStorageIdFromNamespacedId(namespacedId: string): string {
		const matches: RegExpMatchArray = namespacedId.match(new RegExp(this.namespaceDecoderRegexStr));
		if (matches && matches[2].length > 0) {
			return matches[2];
		}
		throw StorageNamespaceConverter.ERR_INVALID_NAMESPACED_ID(namespacedId);
	}

	static getNamespaceFromNamespacedId(namespacedId: string): string {
		const matches: RegExpMatchArray = namespacedId.match(new RegExp(this.namespaceDecoderRegexStr));
		if (matches && matches[1].length > 0) {
			return matches[1];
		}
		throw StorageNamespaceConverter.ERR_INVALID_NAMESPACED_ID(namespacedId);
	}

	private static readonly separator = '<ns:id>';

	private static readonly namespaceDecoderRegexStr = "(.*)" + StorageNamespaceConverter.separator + "(.*)";

}
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

import * as lo from 'lodash';
import {SeInjectable} from 'smarteditcommons/services/dependencyInjection/di';

/** @internal */
interface Store {
	urlRegex: RegExp;
	operationContext: string;
}

/**
 * @ngdoc service
 * @name smarteditCommonsModule.service:OperationContextService
 * @description
 * This service provides the functionality to register a url with its associated operation contexts and also finds operation context given an url.
 */


@SeInjectable()
export class OperationContextService {
	private store: Store[];
	/** @internal */
	constructor(private lodash: lo.LoDashStatic) {
		this.store = [];
	}
    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:OperationContextService#register
     * @methodOf smarteditCommonsModule.service:OperationContextService
     * 
     * @description
     * Register a new url with it's associated operationContext.
     * 
     * @param {String} url The url that is associated to the operation context.
     * @param {String} operationContext The operation context name that is associated to the given url.
     * 
     * @return {Object} operationContextService The operationContextService service
     */
	register(url: string, operationContext: string): this {
		if (typeof url !== 'string' || this.lodash.isEmpty(url)) {
			throw new Error('operationContextService.register error: url is invalid');
		}
		if (typeof operationContext !== 'string' || this.lodash.isEmpty(operationContext)) {
			throw new Error('operationContextService.register error: operationContext is invalid');
		}
		const regexIndex = this.store.findIndex((store) => store.urlRegex.test(url) === true && store.operationContext === operationContext);

		if (regexIndex !== -1) {
			return null;
		}
		const urlRegex = new RegExp(url.replace(/\/:[^\/]*/g, '/.*'));
		this.store.push({
			urlRegex,
			operationContext
		});
		return this;
	}
    /**
     * @ngdoc method
     * @name smarteditCommonsModule.service:OperationContextService#findOperationContext
     * @methodOf smarteditCommonsModule.service:OperationContextService
     * 
     * @description
     * Find the first matching operation context for the given url.
     * 
     * @param {String} url The request url.
     * 
     * @return {String} operationContext
     */
	findOperationContext(url: string): string {
		const regexIndex = this.store.findIndex((store) => store.urlRegex.test(url) === true);
		return ~regexIndex ? this.store[regexIndex].operationContext : null;
	}
}

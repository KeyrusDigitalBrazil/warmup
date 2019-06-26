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
import {OperationContextService} from "smarteditcommons";

describe('operation context service', function() {

	const lodash: lo.LoDashStatic = (window as any).smarteditLodash;
	let operationContextService: OperationContextService;

	beforeEach(() => {
		operationContextService = new OperationContextService(lodash);
	});

	it('should be able to find an operation context for a given url', function() {
		operationContextService.register('/cmswebservices/v1/sites/:siteUID/catalogversiondetails', 'ANY_OPERATION_CONTEXT');
		const oc = operationContextService.findOperationContext('/cmswebservices/v1/sites/123/catalogversiondetails');
		expect(oc).toBe('ANY_OPERATION_CONTEXT');
	});

	it('should return null if there is no operation context registered', function() {
		const oc = operationContextService.findOperationContext('/any_url');
		expect(oc).toBeNull();
	});

	it('should be able to chain the register function', function() {
		expect(operationContextService
			.register('/any_url', 'ANY_OPERATION_CONTEXT')
			.register('/another_url', 'ANOTHER_CONTEXT'))
			.toEqual(operationContextService);
	});

	it('should throw an error if trying to register without passing a url', function() {
		const expectedErrorFunction = function() {
			operationContextService.register(null, null);
		};
		expect(expectedErrorFunction).toThrowError('operationContextService.register error: url is invalid');
	});

	it('should throw an error if trying to register with an invalid url', function() {
		const expectedErrorFunction = function() {
			operationContextService.register('', null);
		};
		expect(expectedErrorFunction).toThrowError('operationContextService.register error: url is invalid');
	});

	it('should throw an error if trying to register with an invalid url', function() {
		const expectedErrorFunction = function() {
			operationContextService.register(123 as any, null);
		};
		expect(expectedErrorFunction).toThrowError('operationContextService.register error: url is invalid');
	});

	it('should throw an error if trying to register without passing an operationContext', function() {
		const expectedErrorFunction = function() {
			operationContextService.register('test', null);
		};
		expect(expectedErrorFunction).toThrowError('operationContextService.register error: operationContext is invalid');
	});

	it('should throw an error if trying to register with an invalid operationContext', function() {
		const expectedErrorFunction = function() {
			operationContextService.register('test', '');
		};
		expect(expectedErrorFunction).toThrowError('operationContextService.register error: operationContext is invalid');
	});

	it('should throw an error if trying to register with an invalid operationContext', function() {
		const expectedErrorFunction = function() {
			operationContextService.register('test', {} as any);
		};
		expect(expectedErrorFunction).toThrowError('operationContextService.register error: operationContext is invalid');
	});
});

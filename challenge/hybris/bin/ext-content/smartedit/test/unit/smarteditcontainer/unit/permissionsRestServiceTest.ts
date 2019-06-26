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
import {PermissionsRestService} from "smarteditcommons";
import {IRestService, IRestServiceFactory} from "smarteditcommons/services";
import {promiseHelper, PromiseType} from "testhelpers";
import {IPermissionsRestServiceResult} from "smarteditcommons/dtos/IPermissionsDto";

describe('PermisionsRestService', function() {

	// Service Under Test
	let permissionsRestService: PermissionsRestService;

	// MOCKS
	const mockResource: jasmine.SpyObj<IRestService<any>> = jasmine.createSpyObj<IRestService<any>>('mockResource', ['get']);
	const mockRestServiceFactory: jasmine.SpyObj<IRestServiceFactory> = jasmine.createSpyObj<IRestServiceFactory>('mockRestServiceFactory', ['get']);

	beforeEach(() => {
		mockRestServiceFactory.get.and.returnValue(mockResource);
		permissionsRestService = new PermissionsRestService(mockRestServiceFactory);
	});

	it('Successfully returns permissions', () => {
		const results: IPermissionsRestServiceResult = {
			permissions: [
				{
					key: 'k1',
					value: 'v1'
				},
				{
					key: 'k2',
					value: 'v2'
				}
			]
		};
		mockResource.get.and.returnValue(promiseHelper.buildPromise("success", PromiseType.RESOLVES, results));

		const result = permissionsRestService.get({
			user: "",
			permissionNames: ""
		});

		expect(result).toBeResolvedWithData({
			permissions: results.permissions
		});
	});

	it('Failed resource to be rejected with reason', () => {
		const failureReason = "42";
		mockResource.get.and.returnValue(promiseHelper.buildPromise("fail", PromiseType.REJECTS, failureReason));

		const result = permissionsRestService.get({
			user: "",
			permissionNames: ""
		});

		expect(result).toBeRejectedWithData(failureReason);
	});

});

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
import 'jasmine';
import {DelegateRestService, RestServiceFactory} from 'smartedit/services';

describe('test RestServiceFactory ', () => {

	class DTO {
	}
	let delegateRestServiceMock: DelegateRestService;
	let restServiceFactory: RestServiceFactory;
	const uri: string = "theuri";
	const identifier = "theidentifier";

	beforeEach(() => {

		delegateRestServiceMock = jasmine.createSpyObj<DelegateRestService>('delegateRestService', ['delegateForSingleInstance', 'delegateForArray', 'delegateForVoid']);
		restServiceFactory = new RestServiceFactory(delegateRestServiceMock);
	});

	it('get return a RestService instance', function() {
		expect(restServiceFactory.get<DTO>(uri, identifier)).toBeTruthy();
	});

});

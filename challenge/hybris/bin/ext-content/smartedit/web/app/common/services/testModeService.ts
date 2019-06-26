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
import * as angular from 'angular';
import {SeInjectable} from './dependencyInjection/di';

/**
 * @ngdoc service
 * @name smarteditServicesModule.service:TestModeService
 *
 * @description
 * Used to determine whether smartedit is running in a e2e (test) mode
 */
/** @internal */
@SeInjectable()
export class TestModeService {

	// Constants
	private readonly TEST_KEY: string = 'e2eMode';

	constructor(private $injector: angular.auto.IInjectorService) {
	}

	/** 
	 * @ngdoc method
	 * @name smarteditServicesModule.service:TestModeService#isE2EMode
	 * @methodOf smarteditServicesModule.service:TestModeService
	 *
	 * @description
	 * returns true if smartedit is running in e2e (test) mode
	 *
	 * @returns {Boolean} true/false
	 */
	public isE2EMode(): boolean {
		return this.$injector.has(this.TEST_KEY) && this.$injector.get(this.TEST_KEY);
	}

}

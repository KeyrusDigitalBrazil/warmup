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

/**
 * @ngdoc service
 * @name cmsSmarteditServicesModule.service:AssetsService
 *
 * @description
 * Determines the root of the production and test assets
 */
export class AssetsService {

	static $inject: any = ['$injector'];

	// Constants
	private readonly TEST_ASSETS_KEY: string = 'testAssets';
	private readonly TEST_ASSETS_SRC: string = '/web/webroot';
	private readonly PROD_ASSETS_SRC: string = '/cmssmartedit';

	// Variables
	private $injector: angular.auto.IInjectorService;

	constructor(injector: angular.auto.IInjectorService) {
		this.$injector = injector;
	}

    /** 
     * @ngdoc method
     * @name cmsSmarteditServicesModule.service:AssetsService#getAssetsRoot
     * @methodOf cmsSmarteditServicesModule.service:AssetsService
     *
     * @description
     * Set data for the given key.
     * 
     * @returns {String} the assets root folder
     */
	getAssetsRoot(): string {
		let useTestAssets = false;

		if (this.getInjector().has(this.TEST_ASSETS_KEY)) {
			const result = this.getInjector().get(this.TEST_ASSETS_KEY);
			useTestAssets = (typeof result === 'boolean') ? result as boolean : false;
		}

		return useTestAssets ? this.TEST_ASSETS_SRC : this.PROD_ASSETS_SRC;
	}

    /** 
     * @ngdoc method
     * @name cmsSmarteditServicesModule.service:AssetsService#getInjector
     * @methodOf cmsSmarteditServicesModule.service:AssetsService
     *
     * @description
     * Returns an injector
     * 
     * @returns {Object} an injector 
     */
	getInjector(): angular.auto.IInjectorService {
		return this.$injector;
	}
}

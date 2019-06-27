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
import * as lo from 'lodash';
import {CloneableUtils, GatewayProxied, InternalFeature, IFeatureService, IToolbarItem, SeInjectable} from 'smarteditcommons';


/** @internal */
@GatewayProxied('_registerAliases', 'addToolbarItem', 'register',
	'enable', 'disable', '_remoteEnablingFromInner', '_remoteDisablingFromInner',
	'addDecorator', 'getFeatureProperty', 'addContextualMenuButton')
@SeInjectable()
export class FeatureService extends IFeatureService {

	private features: InternalFeature[];

	constructor(
		private toolbarServiceFactory: any,
		lodash: lo.LoDashStatic,
		cloneableUtils: CloneableUtils,
		private $q: angular.IQService
	) {
		super(lodash, cloneableUtils);
		this.features = [];
	}

	getFeatureProperty(featureKey: string, propertyName: keyof InternalFeature): angular.IPromise<string | string[] | (() => void)> {
		const feature = this._getFeatureByKey(featureKey);
		return this.$q.when(feature ? feature[propertyName] : null);
	}

	getFeatureKeys(): string[] {
		return this.features.map((feature) => feature.key);
	}

	addToolbarItem(configuration: IToolbarItem): angular.IPromise<void> {

		const toolbar = this.toolbarServiceFactory.getToolbarService(configuration.toolbarId);
		configuration.enablingCallback = function() {
			this.addItems([configuration]);
		}.bind(toolbar);

		configuration.disablingCallback = function() {
			this.removeItemByKey(configuration.key);
		}.bind(toolbar);

		return this.register(configuration);
	}

	protected _registerAliases(configuration: InternalFeature): angular.IPromise<void> {
		const feature = this._getFeatureByKey(configuration.key);
		if (!feature) {
			(configuration as any).id = btoa(configuration.key);
			this.features.push(configuration);
		}
		return this.$q.when();
	}

	private _getFeatureByKey(key: string): InternalFeature {
		return this.features.find((feature: InternalFeature) => {
			return feature.key === key;
		});
	}
}
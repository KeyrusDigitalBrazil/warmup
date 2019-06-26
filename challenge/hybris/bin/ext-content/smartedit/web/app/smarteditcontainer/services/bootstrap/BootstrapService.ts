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
/* forbiddenNameSpaces angular.module:false */
import * as angular from 'angular';
import * as lo from 'lodash';
import {ISharedDataService, SeInjectable} from 'smarteditcommons';
import {ConfigurationModules, Module} from 'smarteditcontainer/services/bootstrap/ConfigurationModules';
import {ConfigurationObject} from 'smarteditcontainer/services/bootstrap/Configuration';
import {SmarteditBundle} from 'smarteditcontainer/services/bootstrap/SmarteditBundle';
import {ConfigurationExtractorService} from 'smarteditcontainer/services';

/** @internal */
@SeInjectable()
export class BootstrapService {

	constructor(
		private configurationExtractorService: ConfigurationExtractorService,
		private sharedDataService: ISharedDataService,
		private injectJS: any,
		private $log: angular.ILogService,
		private $http: angular.IHttpService,
		private $q: angular.IQService,
		private WHITE_LISTED_STOREFRONTS_CONFIGURATION_KEY: string,
		private smarteditBootstrapGateway: any,
		private lodash: lo.LoDashStatic
	) {
	}

	bootstrapSmartEditContainer() {
		angular.bootstrap(document, ["smarteditcontainer"]);
	}

	addDependencyToSmartEditContainer(app: string) {
		try {
			angular.module(app);
			this.$log.debug('Adding app: "' + app + '" to smarteditcontainer');
			angular.module('smarteditcontainer').requires.push(app);
		} catch (ex) {
			this.$log.error('Failed to load outer module ' + app + '; SmartEdit functionality may be compromised.');
		}
	}

	bootstrapContainerModules(configurations: ConfigurationObject) {

		const seContainerModules: ConfigurationModules = this.configurationExtractorService.extractSEContainerModules(configurations);

		const orderedApplications = this._orderApplications(seContainerModules.applications);

		this.$log.debug("outerAppLocations are:", orderedApplications);

		this.sharedDataService.set('authenticationMap', seContainerModules.authenticationMap);
		this.sharedDataService.set('credentialsMap', configurations['authentication.credentials']);

		angular.module('smarteditcontainer')
			.constant('domain', configurations.domain)
			.constant('smarteditroot', configurations.smarteditroot)
			.constant(this.WHITE_LISTED_STOREFRONTS_CONFIGURATION_KEY, configurations[this.WHITE_LISTED_STOREFRONTS_CONFIGURATION_KEY] || []);

		this.injectJS.execute({
			srcs: orderedApplications.map((app) => app.location),
			callback: () => {
				orderedApplications.forEach((app) => {
					this.addDependencyToSmartEditContainer(app.name);
				});
				this.bootstrapSmartEditContainer();
			}
		});
	}

	bootstrapSEApp(configurations: ConfigurationObject) {

		const seModules: ConfigurationModules = this.configurationExtractorService.extractSEModules(configurations);
		const orderedApplications = this._orderApplications(seModules.applications);

		this.sharedDataService.set('authenticationMap', seModules.authenticationMap);
		this.sharedDataService.set('credentialsMap', configurations['authentication.credentials']);

		const resources = {
			properties: {
				domain: configurations.domain,
				smarteditroot: configurations.smarteditroot
			},
			js: [
				configurations.smarteditroot + '/static-resources/dist/smartedit/js/prelibraries.js',
				configurations.smarteditroot + '/static-resources/thirdparties/ckeditor/ckeditor.js',
				configurations.smarteditroot + '/static-resources/dist/smartedit/js/smartedit.js'
			],
			css: [
				configurations.smarteditroot + '/static-resources/dist/smartedit/css/inner-styling.css'
			]
		} as SmarteditBundle;

		resources.properties[this.WHITE_LISTED_STOREFRONTS_CONFIGURATION_KEY] = configurations[this.WHITE_LISTED_STOREFRONTS_CONFIGURATION_KEY];

		const validApplications: Module[] = [];

		/*
		 * applications are considered valid if they can be retrieved over the wire
		 */
		return this.$q.all(orderedApplications.map((application, index) => {
			const deferred = this.$q.defer();
			this.$http.get(application.location).then(() => {
				validApplications.push(application);
				deferred.resolve();
			}, (e) => {
				this.$log.error(`Failed to load application '${application.name}' from location ${application.location}; SmartEdit functionality may be compromised.`);
				deferred.resolve();
			});
			return deferred.promise;
		})).then(() => {
			resources.js = resources.js.concat(validApplications.map((app) => app.location));
			resources.js.push(configurations.smarteditroot + '/static-resources/dist/smartedit/js/smarteditbootstrap.js');
			resources.properties.applications = validApplications.map((app) => app.name);

			this.smarteditBootstrapGateway.publish("bundle", {resources});
		});

	}


	private _orderApplications(applications: Module[]) {

		const simpleApps = applications.filter((item: Module) => {
			return !item.extends;
		});
		const extendingApps = applications
			.filter((item: Module) => {
				return !!item.extends;
			})
			/* 
			 * filer out extendingApps thata do extend an unknown app
			 * other recursive _addExtendingAppsInOrder will never end
			 */
			.filter((extendingApp) => {

				const index = this.lodash.findIndex(applications, (item: Module) => {
					return item.name === extendingApp.extends;
				});

				if (index === -1) {
					this.$log.error(`Application ${extendingApp.name} located at ${extendingApp.location} is ignored because it extends an unknown application '${extendingApp.extends}'; SmartEdit functionality may be compromised.`);
				}
				return index > -1;
			});

		return this._addExtendingAppsInOrder(simpleApps, extendingApps);
	}

	private _addExtendingAppsInOrder(simpleApps: Module[], extendingApps: Module[], pass?: number): Module[] {

		pass = pass || 1;

		const remainingApps: Module[] = [];

		extendingApps.forEach((extendingApp) => {

			const index = this.lodash.findIndex(simpleApps, (item: Module) => {
				return item.name === extendingApp.extends;
			});
			if (index > -1) {
				console.debug(`pass ${pass}, ${extendingApp.name} requiring ${extendingApp.extends} found it at index ${index} (${simpleApps.map((app) => app.name)})`);
				simpleApps.splice(index + 1, 0, extendingApp);
			} else {
				console.debug(`pass ${pass}, ${extendingApp.name} requiring ${extendingApp.extends} did not find it  (${simpleApps.map((app) => app.name)})`);
				remainingApps.push(extendingApp);
			}
		});

		if (remainingApps.length) {
			return this._addExtendingAppsInOrder(simpleApps, remainingApps, ++pass);
		} else {
			return simpleApps;
		}

	}

}
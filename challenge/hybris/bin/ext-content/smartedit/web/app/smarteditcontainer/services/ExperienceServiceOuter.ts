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
import {
	CrossFrameEventService, GatewayProxied, IBaseCatalog, IBaseCatalogVersion, ICatalogService, ICatalogVersion,
	IDefaultExperienceParams, IExperience, IExperienceCatalogDescriptor, IExperienceCatalogVersion, IExperienceParams, IExperienceService, ILanguage,
	IPreviewData, IPreviewResponse, IPreviewService, ISharedDataService, ISite, IStorage, IStorageManager, IStoragePropertiesService, LanguageService, Payload, SeInjectable
} from 'smarteditcommons';
import {IframeManagerService, SiteService} from 'smarteditcontainer/services';

/** @internal */
@GatewayProxied('loadExperience', 'updateExperiencePageContext', 'getCurrentExperience', 'hasCatalogVersionChanged', 'buildRefreshedPreviewUrl')
@SeInjectable()
export class ExperienceService extends IExperienceService {

	static EXPERIENCE_STORAGE_KEY = 'experience';

	private previousExperience: IExperience;

	private experienceStorage: IStorage<string, IExperience>;

	constructor(
		private $q: angular.IQService,
		private $location: angular.ILocationService,
		private $log: angular.ILogService,
		private $route: angular.route.IRouteService,
		lodash: lo.LoDashStatic,
		private crossFrameEventService: CrossFrameEventService,
		private siteService: SiteService,
		private catalogService: ICatalogService,
		private languageService: LanguageService,
		private previewService: IPreviewService,
		private sharedDataService: ISharedDataService,
		seStorageManager: IStorageManager,
		private iframeManagerService: IframeManagerService,
		private EVENTS: any,
		private LANDING_PAGE_PATH: string,
		private STORE_FRONT_CONTEXT: string,
		storagePropertiesService: IStoragePropertiesService
	) {
		super(lodash);

		seStorageManager.getStorage({
			storageId: ExperienceService.EXPERIENCE_STORAGE_KEY,
			storageType: storagePropertiesService.getProperty("STORAGE_TYPE_SESSION_STORAGE")
		}).then((_storage: IStorage<string, IExperience>) => {
			this.experienceStorage = _storage;
		});

	}

    /**
     * @ngdoc method
     * @name smarteditServicesModule.service:ExperienceService#buildAndSetExperience
     * @methodOf smarteditServicesModule.service:ExperienceService
     *
     * @description
     * Given an object containing a siteId, catalogId, catalogVersion and catalogVersions (array of product catalog version uuid's), will return a reconstructed experience
     *
     * @param {IExperienceParams} params 
     * @returns {angular.IPromise<IExperience>} an experience
     */
	buildAndSetExperience(params: IExperienceParams): angular.IPromise<IExperience> {
		const siteId = params.siteId;
		const catalogId = params.catalogId;
		const catalogVersion = params.catalogVersion;
		const productCatalogVersions = params.productCatalogVersions;

		return this.$q.all<ISite, IBaseCatalog[], IBaseCatalog[], ILanguage[]>([
			this.siteService.getSiteById(siteId),
			this.catalogService.getContentCatalogsForSite(siteId),
			this.catalogService.getProductCatalogsForSite(siteId),
			this.languageService.getLanguagesForSite(siteId)
		]).then(([siteDescriptor, catalogs, productCatalogs, languages]) => {

			const currentCatalog: IBaseCatalog = catalogs.find((catalog) => catalog.catalogId === catalogId);
			const currentCatalogVersion: IBaseCatalogVersion = (currentCatalog) ? currentCatalog.versions.find((result: IBaseCatalogVersion) => result.version === catalogVersion) : null;

			if (!currentCatalogVersion) {
				return this.$q.reject(`no catalogVersionDescriptor found for ${catalogId} catalogId and ${catalogVersion} catalogVersion`);
			}

			const currentExperienceProductCatalogVersions: IExperienceCatalogVersion[] = [];

			productCatalogs.forEach((productCatalog: IBaseCatalog) => {

				// for each product catalog either choose the version already present in the params or choose the active version.
				const currentProductCatalogVersion: IBaseCatalogVersion = productCatalog.versions.find((version: IBaseCatalogVersion) => {
					return productCatalogVersions ? productCatalogVersions.indexOf(version.uuid) > -1 : version.active === true;
				});
				currentExperienceProductCatalogVersions.push({
					catalog: productCatalog.catalogId,
					catalogName: productCatalog.name,
					catalogVersion: currentProductCatalogVersion.version,
					active: currentProductCatalogVersion.active,
					uuid: currentProductCatalogVersion.uuid
				});
			});

			const languageDescriptor: ILanguage = params.language ? languages.find((lang: ILanguage) => lang.isocode === params.language) : languages[0];

			const defaultExperience: any = this.lodash.cloneDeep(params);

			delete defaultExperience.siteId;
			delete defaultExperience.catalogId;
			delete defaultExperience.catalogVersion;

			defaultExperience.siteDescriptor = siteDescriptor;
			defaultExperience.catalogDescriptor = {
				catalogId,
				catalogVersion: currentCatalogVersion.version,
				catalogVersionUuid: currentCatalogVersion.uuid,
				name: currentCatalog.name,
				siteId,
				active: currentCatalogVersion.active
			} as IExperienceCatalogDescriptor;
			defaultExperience.languageDescriptor = languageDescriptor;
			defaultExperience.time = defaultExperience.time || null;

			defaultExperience.productCatalogVersions = currentExperienceProductCatalogVersions;

			return this.setCurrentExperience(defaultExperience);
		});
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:ExperienceService#updateExperiencePageId
	 * @methodOf smarteditServicesModule.service:ExperienceService
	 * 
	 * @description
	 * Used to update the page ID stored in the current experience and reloads the page to make the changes visible.
	 *
	 * @param {String} newPageID the ID of the page that must be stored in the current experience.
	 * 
	 */
	updateExperiencePageId(newPageID: string) {
		this.getCurrentExperience().then((currentExperience: IExperience) => {
			if (!currentExperience) {
				// Experience haven't been set. Thus, the experience hasn't been loaded. 
				// No need to update the experience then.
				return null;
			}

			currentExperience.pageId = newPageID;
			this.setCurrentExperience(currentExperience);
			this.reloadPage();
		});
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:ExperienceService#loadExperience
	 * @methodOf smarteditServicesModule.service:ExperienceService
	 *
	 * @description
	 * Used to update the experience with the parameters provided and reloads the page to make the changes visible. 
	 *
	 * @param {IDefaultExperienceParams} params The object containing the paratements for the experience to be loaded.
	 * @param {String} params.siteId the ID of the site that must be stored in the current experience.
	 * @param {String} params.catalogId the ID of the catalog that must be stored in the current experience.
	 * @param {String} params.catalogVersion the version of the catalog that must be stored in the current experience.
	 * @param {? String} params.pageId the ID of the page that must be stored in the current experience.
	 *
	 */
	loadExperience(params: IDefaultExperienceParams): angular.IPromise<angular.ILocationService | void> {
		return this.buildAndSetExperience(params).then(() => {
			return this.reloadPage();
		});
	}

	reloadPage(): angular.ILocationService | void {
		return this.$location.path() === this.STORE_FRONT_CONTEXT ? this.$route.reload() : this.$location.path(this.STORE_FRONT_CONTEXT).replace();
	}

	updateExperiencePageContext(pageCatalogVersionUuid: string, pageId: string): angular.IPromise<IExperience> {
		return this.getCurrentExperience().then((currentExperience: IExperience) => {
			return this.catalogService.getContentCatalogsForSite(currentExperience.catalogDescriptor.siteId).then((catalogs: IBaseCatalog[]) => {
				if (!currentExperience) {
					// Experience haven't been set. Thus, the experience hasn't been loaded. No need to update the
					// experience then.
					return null;
				}

				const pageCatalogVersion: ICatalogVersion = this.lodash.flatten(catalogs.map((catalog: IBaseCatalog) => {
					return this.lodash.cloneDeep(catalog.versions).map((version: ICatalogVersion) => {
						version.catalogName = catalog.name;
						version.catalogId = catalog.catalogId;
						return version;
					});

				})).filter((version: ICatalogVersion) => version.uuid === pageCatalogVersionUuid)[0];

				return this.catalogService.getDefaultSiteForContentCatalog(pageCatalogVersion.catalogId).then((siteDescriptor: ISite) => {

					currentExperience.pageId = pageId;
					currentExperience.pageContext = {
						catalogId: pageCatalogVersion.catalogId,
						catalogName: pageCatalogVersion.catalogName,
						catalogVersion: pageCatalogVersion.version,
						catalogVersionUuid: pageCatalogVersion.uuid,
						siteId: siteDescriptor.uid,
						active: pageCatalogVersion.active
					};

					return this.setCurrentExperience(currentExperience);

				});
			});
		}).then((experience: IExperience) => {
			this.crossFrameEventService.publish(this.EVENTS.PAGE_CHANGE, experience);
			return experience;
		});
	}

	getCurrentExperience(): angular.IPromise<IExperience> {
		return this.experienceStorage.get(ExperienceService.EXPERIENCE_STORAGE_KEY);
	}

	setCurrentExperience(experience: IExperience): angular.IPromise<IExperience> {
		return this.getCurrentExperience().then((previousExperience: IExperience) => {
			this.previousExperience = previousExperience;

			return this.experienceStorage.put(experience, ExperienceService.EXPERIENCE_STORAGE_KEY).then(() => {
				this.sharedDataService.set(ExperienceService.EXPERIENCE_STORAGE_KEY, experience);
				return this.$q.when(experience);
			});
		});
	}

	hasCatalogVersionChanged(): angular.IPromise<boolean> {
		return this.getCurrentExperience().then((currentExperience: IExperience) => {
			return (this.previousExperience === undefined ||
				(currentExperience.catalogDescriptor.catalogId !== this.previousExperience.catalogDescriptor.catalogId) ||
				(currentExperience.catalogDescriptor.catalogVersion !== this.previousExperience.catalogDescriptor.catalogVersion));
		});
	}

	/**
	 * @ngdoc method
	 * @name smarteditServicesModule.service:ExperienceService#initializeExperience
	 * @methodOf smarteditServicesModule.service:ExperienceService
	 *
	 * @description
	 * If an experience is set in the shared data service, this method will load the preview for this experience (such as Catalog, language, date and time).
	 * Otherwise, the user will be redirected to the landing page to select an experience.
	 * To load a preview, we need to get a preview ticket from an API.
	 * Here we set current location to null initially so that the iframe manager loads the provided url and set the location.
	 * 
	 * @returns {angular.IPromise<IExperience>} a promise returning the experience
	 */
	initializeExperience(): angular.IPromise<IExperience> {
		this.iframeManagerService.setCurrentLocation(null);
		return this.getCurrentExperience().then((experience: IExperience) => {
			if (!experience) {
				this.$location.url(this.LANDING_PAGE_PATH);
				return null;
			}
			return this.updateExperience();
		}, (err: any) => {
			this.$log.error('ExperienceService.initializeExperience() - failed to retrieve experience');
			return this.$q.reject(err);
		});
	}

	updateExperience(newExperience?: Payload): angular.IPromise<IExperience> {
		return this.getCurrentExperience().then((experience: IExperience) => {
			// create a deep copy of the current experience
			experience = this.lodash.cloneDeep(experience);

			// merge the new experience into the copy of the current experience
			this.lodash.merge(experience, newExperience);

			return this.previewService.getResourcePathFromPreviewUrl(experience.siteDescriptor.previewUrl).then((resourcePath: string) => {

				const previewData: IPreviewData = this._convertExperienceToPreviewData(experience, resourcePath);

				return this.previewService.createPreview(previewData).then((previewResponse: IPreviewResponse) => {
					(window as any).smartEditBootstrapped = {};
					this.iframeManagerService.loadPreview(previewResponse.resourcePath, previewResponse.previewTicketId);
					return this.setCurrentExperience(experience);
				}, (err: any) => {
					this.$log.error('iframeManagerService.updateExperience() - failed to update experience');
					return this.$q.reject(err);
				});
			}, (err: any) => {
				this.$log.error('ExperienceService.updateExperience() - failed to retrieve resource path');
				return this.$q.reject(err);
			});
		}, (err: any) => {
			this.$log.error('ExperienceService.updateExperience() - failed to retrieve current experience');
			return this.$q.reject(err);
		});
	}

}
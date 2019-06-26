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
	IBaseCatalog, IBaseCatalogVersion, ICatalogService, IConfiguration, IExperience,
	IExperienceCatalogVersion, IExperienceParams, IPreviewCatalogVersionData,
	IPreviewData, IPreviewResponse, ISeComponent, ISharedDataService, ISite, Payload, SeComponent, SystemEventService
} from 'smarteditcommons';
import {ExperienceService, IframeManagerService, SiteService} from 'smarteditcontainer/services';

/** @internal */
export interface IExperienceContent extends Payload {
	language: string;
	pageId: string;
	previewCatalog: string;
	productCatalogVersions: string[];
	time: string;
}

/** @internal */
@SeComponent({
	templateUrl: 'experienceSelectorTemplate.html',
	inputs: ['experience', 'dropdownStatus', 'resetExperienceSelector: =']
})
export class ExperienceSelectorComponent implements ISeComponent {

	public resetExperienceSelector: () => void;
	public smarteditComponentType: string;
	public structureApi: string;
	public contentApi: string;
	public content: IExperienceContent;
	public modalHeaderTitle: string = 'se.experience.selector.header';
	public recompile: () => void;

	private siteCatalogs: {
		siteId: string;
		catalogId: string;
		catalogVersion: string;
		productCatalogs: IBaseCatalog[];
		productCatalogVersions: string[];
		language: string;
	};

	private smarteditComponentId: string;

	private isReady: boolean;
	private dropdownStatus: {
		isopen: boolean;
	};

	private unRegCloseExperienceFn: () => void;
	private unRegFn: () => void;

	constructor(
		private $q: angular.IQService,
		private lodash: lo.LoDashStatic,
		private systemEventService: SystemEventService,
		private siteService: SiteService,
		private sharedDataService: ISharedDataService,
		private iframeClickDetectionService: any,
		private iframeManagerService: IframeManagerService,
		private experienceService: ExperienceService,
		private catalogService: ICatalogService,
		private getAbsoluteURL: any,
		private formatDateAsUtc: any,
		private EVENTS: any,
		private TYPES_RESOURCE_URI: string,
		private PREVIEW_RESOURCE_URI: string
	) {
		this.siteCatalogs = {} as any;
	}

	$onInit() {
		this.resetExperienceSelector = () => {
			this.$q.all([
				this.sharedDataService.get('experience'),
				this.sharedDataService.get('configuration')
			]).then(([experience, configuration]: [IExperience, IConfiguration]) => {

				const experienceContent = this.lodash.cloneDeep(experience) as any;

				delete experienceContent.catalogDescriptor;
				delete experienceContent.siteDescriptor;
				delete experienceContent.languageDescriptor;
				delete experienceContent.pageContext;

				experienceContent.previewCatalog = `${experience.siteDescriptor.uid}_${experience.catalogDescriptor.catalogId}_${experience.catalogDescriptor.catalogVersion}`;
				experienceContent.language = experience.languageDescriptor.isocode;
				experienceContent.productCatalogVersions = experience.productCatalogVersions.map((productCatalogVersion: IExperienceCatalogVersion) => productCatalogVersion.uuid);

				this.smarteditComponentType = 'PreviewData';
				this.smarteditComponentId = null;
				this.structureApi = this.TYPES_RESOURCE_URI + '?code=:smarteditComponentType&mode=DEFAULT';
				this.contentApi = configuration && configuration.previewTicketURI || this.PREVIEW_RESOURCE_URI;

				this.content = experienceContent as IExperienceContent;

				if (!this.isReady) {
					this.isReady = true;
				} else {
					this.recompile();
				}
			});
		};
	}

	$postLink() {

		this.unRegCloseExperienceFn = this.iframeClickDetectionService.registerCallback('closeExperienceSelector', () => {
			if (this.dropdownStatus && this.dropdownStatus.isopen) {
				this.dropdownStatus.isopen = false;
			}
		});

		this.unRegFn = this.systemEventService.subscribe('OVERLAY_DISABLED', () => {
			if (this.dropdownStatus && this.dropdownStatus.isopen) {
				this.dropdownStatus.isopen = false;
			}
		});
	}

	$onDestroy() {
		if (this.unRegFn) {
			this.unRegFn();
		}
		if (this.unRegCloseExperienceFn) {
			this.unRegCloseExperienceFn();
		}
	}

	preparePayload(experienceContent: IExperienceContent): angular.IPromise<IPreviewData> {

		[this.siteCatalogs.siteId, this.siteCatalogs.catalogId, this.siteCatalogs.catalogVersion] = experienceContent.previewCatalog.split('_');

		return this.catalogService.getProductCatalogsForSite(this.siteCatalogs.siteId).then((productCatalogs: IBaseCatalog[]) => {
			this.siteCatalogs.productCatalogs = productCatalogs;
			this.siteCatalogs.productCatalogVersions = experienceContent.productCatalogVersions;

			return this.$q.all([
				this.sharedDataService.get('configuration'),
				this.siteService.getSiteById(this.siteCatalogs.siteId)
			]).then(([configuration, siteDescriptor]: [IConfiguration, ISite]) => {
				const transformedPayload = this.lodash.cloneDeep(experienceContent) as any;

				delete transformedPayload.previewCatalog;
				delete transformedPayload.productCatalogVersions;

				transformedPayload.resourcePath = this.getAbsoluteURL(configuration.domain, siteDescriptor.previewUrl);
				transformedPayload.catalogVersions = this._getProductCatalogsByUuids(experienceContent.productCatalogVersions);
				transformedPayload.catalogVersions.push({
					catalog: this.siteCatalogs.catalogId,
					catalogVersion: this.siteCatalogs.catalogVersion
				});
				transformedPayload.evaluateRestrictions = true;

				return transformedPayload as IPreviewData;
			});
		});
	}

	updateCallback(payload: IExperienceContent, response: IPreviewResponse) {
		delete this.smarteditComponentId; // to force a permanent POST
		this.dropdownStatus.isopen = false;

		// Then perform the actual update.
		const experienceParams = this.lodash.cloneDeep(response) as any;
		delete experienceParams.catalog;
		delete experienceParams.time;

		experienceParams.siteId = this.siteCatalogs.siteId;
		experienceParams.catalogId = this.siteCatalogs.catalogId;
		experienceParams.catalogVersion = this.siteCatalogs.catalogVersion;
		experienceParams.pageId = response.pageId;
		experienceParams.time = this.formatDateAsUtc(payload.time);
		experienceParams.productCatalogVersions = this.siteCatalogs.productCatalogVersions;
		this.experienceService.buildAndSetExperience(experienceParams as IExperienceParams).then((experience: IExperience) => {
			this.sharedDataService.set('experience', experience).then(() => {
				this.systemEventService.publishAsync(this.EVENTS.EXPERIENCE_UPDATE);
				this.iframeManagerService.loadPreview(experience.siteDescriptor.previewUrl, response.ticketId as string);
			});
		});
	}

	getApi($api: any) {
		$api.setPreparePayload(this.preparePayload.bind(this));
		$api.setUpdateCallback(this.updateCallback.bind(this));
		$api.setAlwaysShowSubmit(true);
		$api.setAlwaysShowReset(true);
		$api.setSubmitButtonText('se.componentform.actions.apply');
		$api.setCancelButtonText('se.componentform.actions.cancel');
		$api.setOnReset(() => {
			this.dropdownStatus.isopen = false;
		});
	}

	private _getProductCatalogsByUuids(versionUuids: string[]) {
		const versions: IPreviewCatalogVersionData[] = [];
		this.siteCatalogs.productCatalogs.forEach(function(catalog: IBaseCatalog) {
			if (catalog.versions) {
				const versionMatch = catalog.versions.find(function(version: IBaseCatalogVersion) {
					return versionUuids.indexOf(version.uuid) > -1;
				});
				versions.push({
					catalog: catalog.catalogId,
					catalogVersion: versionMatch.version
				});
			}
		});
		return versions;
	}
}

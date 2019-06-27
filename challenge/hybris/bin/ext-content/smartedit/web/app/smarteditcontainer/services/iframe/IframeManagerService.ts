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
import {SeInjectable, TypedMap} from 'smarteditcommons';
import {DeviceSupport} from './DeviceSupportsValue';
import {DeviceOrientation} from './DeviceOrientationsValue';

/**
 * @ngdoc service
 * @name smarteditServicesModule.service:IframeManagerService
 *
 * @description
 * The iFrame Manager service provides methods to load the storefront into an iframe. The preview of the storefront can be loaded for a specified input homepage and a specified preview ticket. The iframe src attribute is updated with that information in order to display the storefront in SmartEdit.
 */
@SeInjectable()
export class IframeManagerService {
	private currentLocation: string;

	/** @internal */
	constructor(
		private $q: angular.IQService,
		private $log: angular.ILogService,
		private $http: angular.IHttpService,
		private heartBeatService: any,
		private DEVICE_SUPPORTS: DeviceSupport[],
		private DEVICE_ORIENTATIONS: DeviceOrientation[],
		private getOrigin: (url?: string) => string,
		private getURI: (url: string) => string,
		private parseQuery: (url: string) => TypedMap<string>,
		private SMARTEDIT_IFRAME_ID: string,
		private yjQuery: JQueryStatic) {}

    /**
     * @ngdoc method
     * @name smarteditServicesModule.service:IframeManagerService#setCurrentLocation
     * @methodOf smarteditServicesModule.service:IframeManagerService
     *
     * @description
     * This method sets the current page location and stores it in the service. The storefront will be loaded with this location.
     *
     * @param {String} URL Location to be stored
     */
	setCurrentLocation(location: string): void {
		this.currentLocation = location;
	}

	getIframe(): JQuery {
		return this.yjQuery('iframe#' + this.SMARTEDIT_IFRAME_ID);
	}

	getIFrameSrc(): string {
		return this.getIframe().attr("src");
	}

	isCrossOrigin(): boolean {
		return this.getOrigin() !== this.getOrigin(this.currentLocation);
	}

    /**
     * @ngdoc method
     * @name smarteditServicesModule.service:IframeManagerService#load
     * @methodOf smarteditServicesModule.service:IframeManagerService
     *
     * @description
     * This method loads the storefront within an iframe by setting the src attribute to the specified input URL.
     * If this method is called within the context of a new or updated experience, prior to the loading, it will check if the page exists.
     * If the pages does not exist (the server returns a 404 and a content-type:text/html), the user will be redirected to the homepage of the storefront. Otherwise,
     * the user will be redirected to the requested page for the experience.
     *
     * @param {String} URL The URL of the storefront.
     * @param {Boolean =} checkIfFailingHTML Boolean indicating if we need to check if the page call returns a 404
     * @param {String =} homepageInPreviewMode URL of the storefront homepage in preview mode if it's a new experience
     *
     */
	load(url: string, checkIfFailingHTML?: boolean, pageInPreviewMode?: string): void {
		if (checkIfFailingHTML) {
			this._getPageAsync(url).then(() => {
				this.getIframe().attr('src', url);
				this.heartBeatService.resetTimer(true);
			}, (error: any) => {
				if (error.status === 404) {
					this.getIframe().attr('src', pageInPreviewMode);
					this.heartBeatService.resetTimer(true);
				}
			});
		} else {
			this.$log.debug("iframeManagerService::load - loading storefront url:", url);
			this.getIframe().attr('src', url);
			this.heartBeatService.resetTimer(true);
		}
	}

    /**
     * @ngdoc method
     * @name smarteditServicesModule.service:IframeManagerService#loadPreview
     * @methodOf smarteditServicesModule.service:IframeManagerService
     *
     * @description
     * This method loads the preview of the storefront for a specified input homepage URL or a page from the page list, and for a specified preview ticket.
     * This method will add '/previewServlet' to the URI and append the preview ticket in the query string.
     * <br/>If it is an initial load,  {@link smarteditServicesModule.service:IframeManagerService#load load} will be called with this modified homepage or page from page list.
     * <br/>If it is a subsequent call, the modified homepage will be called through Ajax to initialize the preview (storefront constraint) and then
     * {@link smarteditServicesModule.service:IframeManagerService#load load} will be called with the current location.
     *
     * @param {String} homePageOrPageFromPageList The URL of the storefront homepage or a page from the page list for a given experience context.
     * @param {String} previewTicket The preview ticket.
     */
	loadPreview(homePageOrPageFromPageList: string, previewTicket: string): void {
		this.$log.debug("loading storefront iframe with preview ticket:", previewTicket);

		let previewURL = homePageOrPageFromPageList;
		if (!/.+\.html/.test(previewURL)) { // for testing purposes
			previewURL = this._appendURISuffix(previewURL);
		}

		const pageInPreviewMode = previewURL + (previewURL.indexOf("?") === -1 ? "?" : "&") + "cmsTicketId=" + previewTicket;

		// If we don't have a current location, or the current location is the homePage or a page from page list, or the current location has a cmsTicketID
		if (this._mustLoadAsSuch(homePageOrPageFromPageList)) {
			this.load(pageInPreviewMode);
		} else {
			const isCrossOrigin = this.isCrossOrigin();

            /*
             * check failing HTML only if same origin to prevent CORS errors.
             * if location to reload in new experience context is different from homepage, one will have to
             * first load the home page in preview mode and then access the location without preview mode 
             */
			(isCrossOrigin ? this.$q.when({}) : this._getPageAsync(pageInPreviewMode)).then(() => {
				// FIXME: use gatewayProxy to load url from the inner
				this.load(this.currentLocation, !isCrossOrigin, pageInPreviewMode);
			}, (error: any) => this.$log.error("failed to load preview", error));
		}
	}

    /**
     * @ngdoc method
     * @name smarteditServicesModule.service:IframeManagerService#initializeCatalogPreview
     * @methodOf smarteditServicesModule.service:IframeManagerService
     * @deprecated since 1808, use {@link smarteditServicesModule.service:ExperienceService#initializeExperience initializeExperience}
     *
     * @description
     * Deprecated since 1808, use {@link smarteditServicesModule.service:ExperienceService#initializeExperience initializeExperience}
     */
	initializeCatalogPreview(): Error {
		throw new Error("smarteditServicesModule.service:IframeManagerServicealogPreview() is deprecated - Please use ExperienceService.initializeExperience() instead.");
	}

	apply(deviceSupport?: DeviceSupport, deviceOrientation?: DeviceOrientation): void {
		let width;
		let height;
		let isVertical = true;

		if (deviceOrientation && deviceOrientation.orientation) {
			isVertical = deviceOrientation.orientation === "vertical";
		}

		if (deviceSupport) {
			width = isVertical ? deviceSupport.width : deviceSupport.height;
			height = isVertical ? deviceSupport.height : deviceSupport.width;

			// hardcoded the name to default to remove the device skin
			this.getIframe().removeClass().addClass("device-" + (isVertical ? "vertical" : "horizontal") + " device-default");
		} else {
			this.getIframe().removeClass();
		}
		this.getIframe().css({
			width: width || "100%",
			height: height || "100%",
			display: "block",
			margin: "auto"
		});
	}

	applyDefault(): void {
		const defaultDeviceSupport = this.DEVICE_SUPPORTS.find((deviceSupport: DeviceSupport) => {
			return deviceSupport.default;
		});
		const defaultDeviceOrientation = this.DEVICE_ORIENTATIONS.find((deviceOrientation: DeviceOrientation) => {
			return deviceOrientation.default;
		});
		this.apply(defaultDeviceSupport, defaultDeviceOrientation);
	}

    /*
    * if currentLocation is not set yet, it means that this is a first loading and we are trying to load the homepage,
    * or if the page has a ticket ID but is not the homepage, it means that we try to load a page from the page list.
    * For those scenarios, we want to load the page as such in preview mode.
    */
	private _mustLoadAsSuch(homePageOrPageFromPageList: string): boolean {
		return !this.currentLocation || this.getURI(homePageOrPageFromPageList) === this.getURI(this.currentLocation) || 'cmsTicketId' in this.parseQuery(this.currentLocation);
	}

	private _getPageAsync(url: string): angular.IPromise<{}> {
		return this.$http.get(url);
	}

	private _appendURISuffix(url: string): string {
		const pair = url.split('?');
		return pair[0]
			.replace(/(.+)([^\/])$/g, "$1$2/previewServlet")
			.replace(/(.+)\/$/g, "$1/previewServlet") + (pair.length === 2 ? "?" + pair[1] : "");
	}

}

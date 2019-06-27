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
import {CMSModesService} from 'cmscommons';
import {IPageVersion} from 'cmssmarteditcontainer/services';
import {PageVersioningService} from 'cmssmarteditcontainer/services/pageVersioning/PageVersioningService';
import {CrossFrameEventService, IAlertService, IExperience, IExperienceService, IPageInfoService, SeInjectable, TypedMap} from 'smarteditcommons';

/**
 * This service is meant to be used internally by the page versions menu.
 * It allows selecting and deselecting a page version to be rendered in the Versioning
 * mode.
 */
@SeInjectable()
export class PageVersionSelectionService {

	// ------------------------------------------------------------------------
	// Properties
	// ------------------------------------------------------------------------
	private selectedPageVersion: IPageVersion;
	private PAGE_VERSIONS_TOOLBAR_ITEM_KEY: string = 'se.cms.pageVersionsMenu';
	private PAGE_VERSION_UNSELECTED_MSG_KEY: string = 'se.cms.versions.unselect.version';

	// ------------------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------------------
	constructor(
		private crossFrameEventService: CrossFrameEventService,
		private alertService: IAlertService,
		private experienceService: IExperienceService,
		private cMSModesService: CMSModesService,
		private pageInfoService: IPageInfoService,
		private pageVersioningService: PageVersioningService,
		private $translate: angular.translate.ITranslateService,
		private SHOW_TOOLBAR_ITEM_CONTEXT: string,
		private HIDE_TOOLBAR_ITEM_CONTEXT: string,
		EVENT_PERSPECTIVE_CHANGED: string,
		EVENT_PERSPECTIVE_REFRESHED: string,
		private EVENTS: TypedMap<string>
	) {
		this.selectedPageVersion = null;
		this.crossFrameEventService.subscribe(EVENT_PERSPECTIVE_CHANGED, this.removePageVersionOnPerspectiveChange.bind(this));
		this.crossFrameEventService.subscribe(EVENT_PERSPECTIVE_REFRESHED, this.resetPageVersionContext.bind(this));

		// Required especially when a version is selected and you refresh the browser.
		this.crossFrameEventService.subscribe(this.EVENTS.PAGE_CHANGE, (eventId: string, experience: IExperience) => {
			if (experience.versionId && !this.selectedPageVersion) {
				this.pageInfoService.getPageUUID().then((pageUuid: string) => {
					this.pageVersioningService.getPageVersionForId(pageUuid, String(experience.versionId)).then((version: IPageVersion) => {
						this.selectedPageVersion = version;
						this.showToolbarContextIfNeeded();
						this.crossFrameEventService.publish(this.EVENTS.PAGE_SELECTED);
					});
				});
			}
		});
	}

	// ------------------------------------------------------------------------
	// Public API
	// ------------------------------------------------------------------------
	public hideToolbarContextIfNotNeeded(): void {
		if (!this.selectedPageVersion) {
			this.crossFrameEventService.publish(this.HIDE_TOOLBAR_ITEM_CONTEXT, this.PAGE_VERSIONS_TOOLBAR_ITEM_KEY);
		}
	}

	public showToolbarContextIfNeeded(): void {
		if (this.selectedPageVersion) {
			this.crossFrameEventService.publish(this.SHOW_TOOLBAR_ITEM_CONTEXT, this.PAGE_VERSIONS_TOOLBAR_ITEM_KEY);
		}
	}

	public selectPageVersion(version: IPageVersion): void {
		if (!this.isSameVersion(this.selectedPageVersion, version)) {
			this.selectedPageVersion = version;
			this.experienceService.updateExperience({
				versionId: version.uid
			});

			this.showToolbarContextIfNeeded();
			this.crossFrameEventService.publish(this.EVENTS.PAGE_SELECTED);
		}
	}

	public updatePageVersionDetails(version: IPageVersion): void {
		this.selectedPageVersion = version;
	}

	public deselectPageVersion(showAlert: boolean = true): void {
		if (this.selectedPageVersion && showAlert) {
			this.$translate(this.PAGE_VERSION_UNSELECTED_MSG_KEY).then((msgTranslated: string) => {
				this.alertService.showInfo(msgTranslated);
			});
		}

		this.selectedPageVersion = null;
		this.experienceService.updateExperience({
			versionId: null
		});

		this.crossFrameEventService.publish(
			this.HIDE_TOOLBAR_ITEM_CONTEXT, this.PAGE_VERSIONS_TOOLBAR_ITEM_KEY);
	}

	public getSelectedPageVersion(): IPageVersion {
		return this.selectedPageVersion;
	}

	private isSameVersion(selectedPageVersion: IPageVersion, newVersion: IPageVersion): boolean {
		return selectedPageVersion !== null && newVersion !== null ? this.selectedPageVersion.uid === newVersion.uid : false;
	}

	private removePageVersionOnPerspectiveChange(): void {
		this.cMSModesService.isVersioningPerspectiveActive().then((isVersioningModeActive: boolean) => {
			if (this.selectedPageVersion) {
				this.pageInfoService.getPageUUID().then((pageUuid: string) => {
					if (!isVersioningModeActive || this.selectedPageVersion.itemUUID !== pageUuid) {
						this.deselectPageVersion();
					}
				});
			}
		});
	}

	private resetPageVersionContext(): void {
		this.experienceService.getCurrentExperience().then((experience: IExperience) => {
			if (!experience.versionId && this.selectedPageVersion) {
				this.selectedPageVersion = null;
				this.hideToolbarContextIfNotNeeded();
			} else {
				this.showToolbarContextIfNeeded();
			}
		});
	}
}

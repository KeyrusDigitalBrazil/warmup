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
import {IAlertService, IExperienceService, IPageInfoService, Payload, SeInjectable} from 'smarteditcommons';
import {IPageVersion, PageVersioningService} from 'cmssmarteditcontainer/services';
import {PageVersionSelectionService} from 'cmssmarteditcontainer/components/versioning/services/PageVersionSelectionService';

/**
 * This service is used to manage a page version.
 */
@SeInjectable()
export class ManagePageVersionService {

	constructor(
		private alertService: IAlertService,
		private experienceService: IExperienceService,
		private confirmationModalService: any,
		private genericEditorModalService: any,
		private pageInfoService: IPageInfoService,
		private pageVersioningService: PageVersioningService,
		private pageVersionSelectionService: PageVersionSelectionService) {}

	public createPageVersion(): void {
		this.pageInfoService.getPageUUID().then((pageUuid: string) => {
			const componentData = this.getComponentDataForEditor(pageUuid, null);

			return this.genericEditorModalService.open(componentData, null, (result: IPageVersion) => {
				const experience: Payload = {
					versionId: result.uid
				};
				this.experienceService.updateExperience(experience).then(() => {
					this.alertService.showSuccess('se.cms.versions.create.alert.success');
					this.pageVersionSelectionService.selectPageVersion(result);
				});
			});
		});
	}

	public editPageVersion(versionDetails: IPageVersion): void {
		this.pageInfoService.getPageUUID().then((pageUuid: string) => {
			const componentData = this.getComponentDataForEditor(pageUuid, versionDetails);

			return this.genericEditorModalService.open(componentData, null, (result: IPageVersion) => {
				this.pageVersionSelectionService.updatePageVersionDetails(result);
			});
		});
	}

	public deletePageVersion(versionId: string): void {
		this.pageInfoService.getPageUUID().then((pageUuid: string) => {
			this.confirmationModalService.confirm({
				title: "se.cms.actionitem.page.version.delete.confirmation.title",
				description: "se.cms.actionitem.page.version.delete.confirmation.description",
			}).then(() => {
				// call api to delete
				this.pageVersioningService.deletePageVersion(pageUuid, versionId).then(() => {
					// print success message
					this.alertService.showSuccess('se.cms.versions.delete.alert.success');
					// reload experience to display current page if deleting the current
					const selectedVersion = this.pageVersionSelectionService.getSelectedPageVersion();
					if (selectedVersion && selectedVersion.uid === versionId) {
						this.experienceService.updateExperience().then(() => {
							this.pageVersionSelectionService.deselectPageVersion();
						});
					}
				});
			});
		});
	}


	/**
	 * Returns an object that contains the information to be displayed and edited in the modal.
	 * 
	 * @param pageUuid the uuid of the page
	 * @param content the content to be populated in the editor, null for create mode.
	 * @returns {Object} the object to be passed to the genericEditorModalService.open method.
	 */
	private getComponentDataForEditor(pageUuid: string, content: IPageVersion): any {
		const componentData: any = {
			title: content ? 'se.cms.versions.edit' : 'se.cms.versions.create',
			cssClasses: 'yFrontModal',
			structure: {
				attributes: [{
					cmsStructureType: "ShortString",
					qualifier: "label",
					i18nKey: 'se.cms.versions.editor.label.name',
					required: true
				}, {
					cmsStructureType: "ShortString",
					qualifier: "description",
					i18nKey: "se.cms.versions.editor.description.name"
				}]
			},
			contentApi: this.pageVersioningService.getResourceURI().replace(':pageUuid', pageUuid),
		};

		if (content) {
			componentData.content = content;
			componentData.componentUuid = content.uid;
			componentData.componentType = 'versioning';
		}

		return componentData;
	}
}
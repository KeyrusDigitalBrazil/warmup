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

import {LoDashStatic} from 'lodash';
import {CMSItemStructure, CMSItemStructureField, ICMSPage} from 'cmscommons';
import {SeInjectable} from 'smarteditcommons';
import {PageVersionSelectionService} from 'cmssmarteditcontainer/components/versioning/services/PageVersionSelectionService';

export interface PageInfoForViewing extends ICMSPage {
	template: string;
	primaryPage?: string;
	displayCondition: string;
	content: ICMSPage;
	localizedType: string;
	restrictionsCriteria: string;
}

/**
 * This service is used to provide all the information necessary to properly display the PageInfoMenu. 
 */
@SeInjectable()
export class PageInfoMenuService {

	// ------------------------------------------------------------------------
	// Constants
	// ------------------------------------------------------------------------
	private ALL_RESTRICTIONS_CRITERIA_LABEL = "se.cms.restrictions.criteria.all";
	private ANY_RESTRICTIONS_CRITERIA_LABEL = "se.cms.restrictions.criteria.any";

	// ------------------------------------------------------------------------
	// Properties
	// ------------------------------------------------------------------------
	private isPageEditorOpened: boolean;

	// ------------------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------------------
	constructor(
		private pageService: any,
		private pageEditorModalService: any,
		private cmsitemsRestService: any,
		private typeStructureRestService: any,
		private displayConditionsFacade: any,
		private pageVersionSelectionService: PageVersionSelectionService,
		private lodash: LoDashStatic,
		private $translate: angular.translate.ITranslateService,
		private $q: angular.IQService,
		private $log: angular.ILogService,
	) {
		this.isPageEditorOpened = false;
	}

	// ------------------------------------------------------------------------
	// Public API
	// ------------------------------------------------------------------------
	public openPageEditor(pageInfo: ICMSPage): void {
		if (this.lodash.isNil(pageInfo)) {
			this.$log.warn("[pageInfoMenuService] - Cannot open page editor. Provided page is empty.");
			return;
		}

		if (!this.isPageEditorOpened) {
			this.isPageEditorOpened = true;

			this.pageEditorModalService.open(pageInfo).finally(() => {
				this.isPageEditorOpened = false;
			});
		}
	}

	/** 
	 * @internal 
	 * This method retrieves the information for the current page and prepares it to be displayed in the 
	 * page info menu. 
	 */
	public getCurrentPageInfo(): angular.IPromise<PageInfoForViewing> {
		return this.pageService.getCurrentPageInfoByVersion(this.getCurrentPageVersionId()).then((pageInfo: ICMSPage) => {
			return this.cmsitemsRestService.getById(pageInfo.masterTemplate).then((templateInfo: any) => {
				return this.getPrimaryPageName(pageInfo).then((primaryPageName: string) => {
					pageInfo.template = templateInfo.uid;

					const pageInfoForViewing = this.lodash.cloneDeep(pageInfo) as any;
					pageInfoForViewing.content = pageInfo;
					pageInfoForViewing.primaryPage = primaryPageName;
					pageInfoForViewing.localizedType = pageInfo.typeCode;
					pageInfoForViewing.displayCondition = this.getPageDisplayCondition(pageInfo);
					pageInfoForViewing.restrictionsCriteria = this.getPageRestrictionsCriteria(pageInfo);

					// Note: In the previous implementation of the page info, there was a call to the catalog service to retrieve
					// the URI context and assign it to the page content. However, that field doesn't seem to be in use anymore, so it 
					// was removed. 

					return pageInfoForViewing as PageInfoForViewing;
				});
			});
		}).catch(() => {
			this.$log.warn("[pageInfoMenuService] - Cannot retrieve page info. Please try again later.");
		});
	}

	/** 
	 * @internal 
	 * This method retrieves the page structure and adapts the fields to match the order expected in the page info menu. 
	 */
	public getPageStructureForViewing(pageTypeCode: string, isPrimaryPage: boolean): angular.IPromise<CMSItemStructure> {
		return this.typeStructureRestService.getStructureByType(pageTypeCode).then((structure: CMSItemStructureField[]) => {
			structure.forEach((field: CMSItemStructureField) => {
				field.editable = false;
			});

			// This method needs to ensure that the page structure contains only the fields needed for viewing and in the right order.
			// The final list of attributes will have our fields in the right order. Any other fields in the structure, will appear at the end. 
			this.removeFieldFromStructure(structure, "uid");
			this.removeFieldFromStructure(structure, "restrictions");

			const fieldsInRightOrder: CMSItemStructureField[] = [];
			this.addFieldToStructure(fieldsInRightOrder, this.removeFieldFromStructure(structure, "name"));
			this.addFieldToStructure(fieldsInRightOrder, this.removeFieldFromStructure(structure, "creationtime"));
			this.addFieldToStructure(fieldsInRightOrder, this.buildField("localizedType"));
			this.addFieldToStructure(fieldsInRightOrder, this.removeFieldFromStructure(structure, "modifiedtime"));
			this.addFieldToStructure(fieldsInRightOrder, this.buildField("template"));
			this.addFieldToStructure(fieldsInRightOrder, this.buildField("displayCondition"));
			this.addFieldToStructure(fieldsInRightOrder, this.removeFieldFromStructure(structure, "label"));

			if (!isPrimaryPage) {
				this.addFieldToStructure(fieldsInRightOrder, this.buildField("primaryPage"));
				this.addFieldToStructure(fieldsInRightOrder, this.buildField("restrictions", "RestrictionsList"));
			}

			this.addFieldToStructure(fieldsInRightOrder, this.removeFieldFromStructure(structure, "title"));

			return {
				attributes: this.lodash.concat(fieldsInRightOrder, structure),
				category: 'PAGE'
			} as CMSItemStructure;
		}).catch(() => {
			this.$log.warn("[pageInfoMenuService] - Cannot retrieve page info structure. Please try again later.");
		});
	}

	// ------------------------------------------------------------------------
	// Helper Methods
	// ------------------------------------------------------------------------
	private addFieldToStructure(structure: CMSItemStructureField[], field: CMSItemStructureField): void {
		if (field) {
			structure.push(field);
		}
	}

	private buildField(qualifier: string, cmsStructureType = "ShortString"): CMSItemStructureField {
		return {
			cmsStructureType,
			qualifier,
			i18nKey: "se.cms.pageinfo.page." + qualifier.toLocaleLowerCase(),
			editable: false
		};
	}

	private removeFieldFromStructure(structure: CMSItemStructureField[], fieldQualifier: string): CMSItemStructureField {
		return this.lodash.remove(structure, (field: CMSItemStructureField) => {
			return field.qualifier === fieldQualifier;
		})[0];
	}

	private getPrimaryPageName(pageInfo: ICMSPage): angular.IPromise<string> {
		if (this.isVariationPage(pageInfo)) {
			return this.displayConditionsFacade.getPrimaryPageForVariationPage(pageInfo.uid).then((primaryPageInfo: any) => {
				return primaryPageInfo.name;
			});
		}

		return this.$q.when(null);
	}

	private isVariationPage(pageInfo: ICMSPage): boolean {
		return !pageInfo.defaultPage;
	}

	private getPageDisplayCondition(pageInfo: ICMSPage): string {
		return this.$translate.instant(this.isVariationPage(pageInfo) ?
			"page.displaycondition.variation" : "page.displaycondition.primary");
	}

	private getPageRestrictionsCriteria(pageInfo: ICMSPage): string {
		let pageRestrictionsCriteria = null;
		if (this.isVariationPage(pageInfo) && pageInfo.onlyOneRestrictionMustApply !== undefined) {
			pageRestrictionsCriteria = this.$translate.instant((pageInfo.onlyOneRestrictionMustApply) ?
				this.ANY_RESTRICTIONS_CRITERIA_LABEL : this.ALL_RESTRICTIONS_CRITERIA_LABEL);
		}

		return pageRestrictionsCriteria;
	}

	private getCurrentPageVersionId(): string {
		const pageVersion = this.pageVersionSelectionService.getSelectedPageVersion();
		return (pageVersion) ? pageVersion.uid : null;
	}
}
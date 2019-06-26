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

import 'jasmine';

import * as lo from 'lodash';
import {promiseHelper, PromiseType} from 'testhelpers';
import {CMSItemStructure, CMSItemStructureField, CMSPageStatus, CMSPageTypes, ICMSPage} from 'cmscommons';
import {PageInfoForViewing, PageInfoMenuService} from 'cmssmarteditcontainer/components/pages/pageInfoMenu/services/PageInfoMenuService';
import {PageVersionSelectionService} from 'cmssmarteditcontainer/components/versioning/services/PageVersionSelectionService';
import {IPageVersion} from 'cmssmarteditcontainer/services';

describe('PageInfoMenuService', () => {

	// ---------------------------------------------------------------------------
	// Constants
	// ---------------------------------------------------------------------------
	const REJECTED_PROMISE = promiseHelper.buildPromise<any>('rejectedPromise', PromiseType.REJECTS);
	const TEMPLATE_UID = "SOME TEMPLATE UID";
	const PRIMARY_PAGE_NAME = "SOME PRIMARY PAGE NAME";
	const PAGE_TYPE_CODE = CMSPageTypes.ContentPage;
	const PAGE_VERSION_ID = "PAGE VERSION ID";
	const PAGE_UUID = "PAGE UUID";

	const ANY_RESTRICTION_CRITERIA = "se.cms.restrictions.criteria.any";
	const ALL_RESTRICTION_CRITERIA = "se.cms.restrictions.criteria.all";

	const VARIATION_DISPLAY_CONDITION = "page.displaycondition.variation";
	const PRIMARY_DISPLAY_CONDITION = "page.displaycondition.primary";

	const CREATION_TIME = new Date();
	const MODIFICATION_TIME = new Date();

	// ---------------------------------------------------------------------------
	// Variables
	// ---------------------------------------------------------------------------
	let pageInfoMenuService: PageInfoMenuService;

	let pageVersionSelectionService: jasmine.SpyObj<PageVersionSelectionService>;
	let pageEditorModalService: any;
	let pageService: any;
	let cmsitemsRestService: any;
	let typeStructureRestService: any;
	let displayConditionsFacade: any;
	let lodash: lo.LoDashStatic;
	let $translate: jasmine.SpyObj<angular.translate.ITranslateService>;
	let $q: jasmine.SpyObj<angular.IQService>;
	let $log: angular.ILogService;

	let pageInfo: ICMSPage = null;
	let primaryPageInfo: any;
	let templateInfo: any;

	// ---------------------------------------------------------------------------
	// Set-up
	// ---------------------------------------------------------------------------
	beforeEach(() => {
		lodash = (window as any).smarteditLodash;

		pageInfo = {
			uid: 'some uid',
			uuid: 'some uuid',
			name: 'some name',
			pageStatus: CMSPageStatus.ACTIVE,
			type: null,
			typeCode: PAGE_TYPE_CODE,
			label: 'some label',
			masterTemplate: 'some master template',
			title: null,
			defaultPage: true,
			creationtime: CREATION_TIME,
			modifiedtime: MODIFICATION_TIME,
			restrictions: [],
			onlyOneRestrictionMustApply: true,
			homepage: false,
			catalogVersion: 'some catalog version'
		};

		templateInfo = {
			uid: TEMPLATE_UID
		};

		primaryPageInfo = {
			name: PRIMARY_PAGE_NAME
		};
	});

	beforeEach(() => {
		pageEditorModalService = jasmine.createSpyObj('pageEditorModalService', ['open']);
		pageEditorModalService.open.and.returnValue(promiseHelper.buildPromise<string>('resolvedPromise', PromiseType.RESOLVES));

		pageService = jasmine.createSpyObj('pageService', ['getCurrentPageInfoByVersion']);
		pageService.getCurrentPageInfoByVersion.and.returnValue(promiseHelper.buildPromise<any>('resolvedPromise', PromiseType.RESOLVES, pageInfo));

		cmsitemsRestService = jasmine.createSpyObj('cmsitemsRestService', ['getById']);
		cmsitemsRestService.getById.and.returnValue(promiseHelper.buildPromise('cmsItemsRestServicePromise', PromiseType.RESOLVES, templateInfo));

		displayConditionsFacade = jasmine.createSpyObj('displayConditionsFacade', ['getPrimaryPageForVariationPage']);
		displayConditionsFacade.getPrimaryPageForVariationPage.and.returnValue(promiseHelper.buildPromise('primaryPageForVariationPromise', PromiseType.RESOLVES, primaryPageInfo));

		typeStructureRestService = jasmine.createSpyObj('typeStructureRestService', ['getStructureByType']);
		const structureRetrieved: CMSItemStructureField[] = [
			buildStructureField("restrictions"),
			buildStructureField("label"),
			buildStructureField("name"),
			buildStructureField("modifiedtime", "Date", false),
			buildStructureField("title"),
			buildStructureField("other", "Other"),
			buildStructureField("uid"),
			buildStructureField("creationtime", "Date")
		];
		typeStructureRestService.getStructureByType.and.returnValue(promiseHelper.buildPromise<CMSItemStructureField[]>('resolvedPromise', PromiseType.RESOLVES, structureRetrieved));

		const pageVersion: IPageVersion = {
			uid: PAGE_VERSION_ID,
			creationtime: null,
			itemUUID: PAGE_UUID,
			label: null
		};
		pageVersionSelectionService = jasmine.createSpyObj('pageVersionSelectionService', ['getSelectedPageVersion']);
		pageVersionSelectionService.getSelectedPageVersion.and.returnValue(pageVersion);

		$translate = jasmine.createSpyObj<angular.translate.ITranslateService>('$translate', ['instant']);
		$translate.instant.and.callFake((label: string) => {
			return label;
		});

		$q = promiseHelper.$q();

		$log = jasmine.createSpyObj<angular.ILogService>('$log', ['warn']);

		pageInfoMenuService = new PageInfoMenuService(pageService, pageEditorModalService, cmsitemsRestService,
			typeStructureRestService, displayConditionsFacade, pageVersionSelectionService, lodash, $translate, $q, $log);
	});

	beforeEach(() => {
		// Ensure that everything is properly clean. 
		pageService.getCurrentPageInfoByVersion.calls.reset();
		pageEditorModalService.open.calls.reset();
	});

	// ---------------------------------------------------------------------------
	// Tests
	// ---------------------------------------------------------------------------
	it('GIVEN provided pageInfo is null WHEN editPage is called THEN a warning message is displayed in the console and no editor is opened', () => {
		// GIVEN 
		expect($log.warn).not.toHaveBeenCalled();

		// WHEN 
		pageInfoMenuService.openPageEditor(null);

		// THEN 
		expect($log.warn).toHaveBeenCalledWith("[pageInfoMenuService] - Cannot open page editor. Provided page is empty.");
		expect(pageEditorModalService.open).not.toHaveBeenCalled();
	});

	it('GIVEN no page editor is opened WHEN editPage is called THEN it opens the page editor modal service', () => {
		// GIVEN 
		expect(pageEditorModalService.open).not.toHaveBeenCalled();

		// WHEN
		pageInfoMenuService.openPageEditor(pageInfo);

		// THEN     
		expect(pageEditorModalService.open).toHaveBeenCalled();
	});

	it('GIVEN a page editor is still opened WHEN editPage is called THEN no new modal is opened', () => {
		// GIVEN 
		(pageInfoMenuService as any).isPageEditorOpened = true;

		// WHEN 
		pageInfoMenuService.openPageEditor(pageInfo);

		// THEN 
		expect(pageEditorModalService.open).not.toHaveBeenCalled();
	});

	it('GIVEN current page info cannot be retrieved WHEN getCurrentPageInfo is called THEN a warning message is displayed in the console', () => {
		// GIVEN 
		cmsitemsRestService.getById.and.returnValue(REJECTED_PROMISE);

		// WHEN 
		pageInfoMenuService.getCurrentPageInfo();

		// THEN 
		expect($log.warn).toHaveBeenCalledWith('[pageInfoMenuService] - Cannot retrieve page info. Please try again later.');
	});

	it('GIVEN current page is a variation AND the primary page for the current variation cannot be retrieved WHEN getCurrentPageInfo is called THEN a warning message is displayed in the console', () => {
		// GIVEN 
		markPageAsVariation();
		displayConditionsFacade.getPrimaryPageForVariationPage.and.returnValue(REJECTED_PROMISE);

		// WHEN 
		pageInfoMenuService.getCurrentPageInfo();

		// THEN 
		expect($log.warn).toHaveBeenCalledWith('[pageInfoMenuService] - Cannot retrieve page info. Please try again later.');
	});

	it('GIVEN primary page WHEN getCurrentPageInfo is called THEN it returns a page with the appropriate information', () => {
		// GIVEN 

		// WHEN 
		pageInfoMenuService.getCurrentPageInfo().then((result: PageInfoForViewing) => {
			// THEN 
			expect(result).toEqual({
				uid: 'some uid',
				uuid: 'some uuid',
				name: 'some name',
				pageStatus: CMSPageStatus.ACTIVE,
				type: null,
				typeCode: PAGE_TYPE_CODE,
				label: 'some label',
				masterTemplate: 'some master template',
				title: null,
				defaultPage: true,
				creationtime: CREATION_TIME,
				modifiedtime: MODIFICATION_TIME,
				restrictions: [],
				onlyOneRestrictionMustApply: true,
				primaryPage: null,
				restrictionsCriteria: null,
				template: TEMPLATE_UID,
				displayCondition: PRIMARY_DISPLAY_CONDITION,
				content: pageInfo,
				localizedType: PAGE_TYPE_CODE,
				homepage: false,
				catalogVersion: 'some catalog version'
			} as any);
		});
	});

	it('GIVEN page is variation WHEN getCurrentPageInfo is called THEN it returns a page info with appropriate information', () => {
		// GIVEN 
		markPageAsVariation();

		// WHEN 
		pageInfoMenuService.getCurrentPageInfo().then((result: PageInfoForViewing) => {
			// THEN 
			expect(result).toEqual({
				uid: 'some uid',
				uuid: 'some uuid',
				name: 'some name',
				pageStatus: CMSPageStatus.ACTIVE,
				type: null,
				typeCode: PAGE_TYPE_CODE,
				label: 'some label',
				masterTemplate: 'some master template',
				title: null,
				defaultPage: false,
				creationtime: CREATION_TIME,
				modifiedtime: MODIFICATION_TIME,
				restrictions: [],
				onlyOneRestrictionMustApply: true,
				template: TEMPLATE_UID,
				displayCondition: VARIATION_DISPLAY_CONDITION,
				primaryPage: PRIMARY_PAGE_NAME,
				content: pageInfo,
				localizedType: PAGE_TYPE_CODE,
				restrictionsCriteria: ANY_RESTRICTION_CRITERIA,
				homepage: false,
				catalogVersion: 'some catalog version'
			} as any);
		});
	});

	it('GIVEN page is variation WHEN getCurrentPageInfo is called THEN it must return the right restrictions criteria', () => {
		// GIVEN 
		markPageAsVariation();
		markPageAsAllRestrictionsMustApply();

		// WHEN 
		pageInfoMenuService.getCurrentPageInfo().then((result: PageInfoForViewing) => {
			// THEN 
			expect(result).toEqual({
				uid: 'some uid',
				uuid: 'some uuid',
				name: 'some name',
				pageStatus: CMSPageStatus.ACTIVE,
				type: null,
				typeCode: PAGE_TYPE_CODE,
				label: 'some label',
				masterTemplate: 'some master template',
				title: null,
				defaultPage: false,
				creationtime: CREATION_TIME,
				modifiedtime: MODIFICATION_TIME,
				restrictions: [],
				onlyOneRestrictionMustApply: false,
				template: TEMPLATE_UID,
				displayCondition: VARIATION_DISPLAY_CONDITION,
				primaryPage: PRIMARY_PAGE_NAME,
				content: pageInfo,
				localizedType: PAGE_TYPE_CODE,
				restrictionsCriteria: ALL_RESTRICTION_CRITERIA,
				homepage: false,
				catalogVersion: 'some catalog version'
			} as any);
		});
	});

	it('GIVEN a page structure cannot be retrieved WHEN getPageStructureForViewing is called THEN a warning message is displayed in the console', () => {
		// GIVEN 
		typeStructureRestService.getStructureByType.and.returnValue(REJECTED_PROMISE);

		// WHEN 
		pageInfoMenuService.getPageStructureForViewing(PAGE_TYPE_CODE, true);

		// THEN 
		expect($log.warn).toHaveBeenCalledWith('[pageInfoMenuService] - Cannot retrieve page info structure. Please try again later.');
	});

	it('GIVEN variation page WHEN getPageStructureForViewing is called THEN it returns the structure in the proper format for viewing', () => {
		// GIVEN 
		const structureExpected: CMSItemStructureField[] = [
			buildStructureField("name", "ShortString", false),
			buildStructureField("creationtime", "Date", false),
			buildStructureField("localizedType", "ShortString", false),
			buildStructureField("modifiedtime", "Date", false),
			buildStructureField("template", "ShortString", false),
			buildStructureField("displayCondition", "ShortString", false),
			buildStructureField("label", "ShortString", false),
			buildStructureField("primaryPage", "ShortString", false),
			buildStructureField("restrictions", "RestrictionsList", false),
			buildStructureField("title", "ShortString", false),
			buildStructureField("other", "Other", false)
		];

		// WHEN 
		const result = pageInfoMenuService.getPageStructureForViewing(PAGE_TYPE_CODE, false);

		// THEN 
		result.then((structureReturned: CMSItemStructure) => {
			expect(structureReturned.attributes).toEqualData(structureExpected);
		});
	});

	it('GIVEN primary page WHEN getPageStructureForViewing is called THEN it returns the structure in the proper format for viewing', () => {
		// GIVEN 
		const structureExpected: CMSItemStructureField[] = [
			buildStructureField("name", "ShortString", false),
			buildStructureField("creationtime", "Date", false),
			buildStructureField("localizedType", "ShortString", false),
			buildStructureField("modifiedtime", "Date", false),
			buildStructureField("template", "ShortString", false),
			buildStructureField("displayCondition", "ShortString", false),
			buildStructureField("label", "ShortString", false),
			buildStructureField("title", "ShortString", false),
			buildStructureField("other", "Other", false)
		];

		// WHEN 
		const result = pageInfoMenuService.getPageStructureForViewing(PAGE_TYPE_CODE, true);

		// THEN 
		result.then((structureReturned: CMSItemStructure) => {
			expect(structureReturned.attributes).toEqualData(structureExpected);
		});
	});

	// ---------------------------------------------------------------------------
	// Helper Methods
	// ---------------------------------------------------------------------------
	const markPageAsVariation = () => {
		pageInfo.defaultPage = false;
	};

	const markPageAsAllRestrictionsMustApply = () => {
		pageInfo.onlyOneRestrictionMustApply = false;
	};

	const buildStructureField = (qualifier: string, cmsStructureType = "ShortString", editable = true): CMSItemStructureField => {
		return {
			cmsStructureType,
			qualifier,
			i18nKey: "se.cms.pageinfo.page." + qualifier.toLocaleLowerCase(),
			editable
		};
	};
}); 
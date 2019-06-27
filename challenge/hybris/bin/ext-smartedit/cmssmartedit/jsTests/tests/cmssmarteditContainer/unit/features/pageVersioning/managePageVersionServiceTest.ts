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
import {IAlertService, IExperienceService, IPageInfoService} from 'smarteditcommons';
import {IPageVersion, PageVersioningService} from 'cmssmarteditcontainer/services';
import {PageVersionSelectionService} from 'cmssmarteditcontainer/components/versioning/services/PageVersionSelectionService';
import {ManagePageVersionService} from 'cmssmarteditcontainer/components/versioning/services/ManagePageVersionService';
import {promiseHelper, PromiseType} from 'testhelpers';

describe('Test ManagePageVersionService', () => {

	// ---------------------------------------------------------------------------
	// Constants
	// ---------------------------------------------------------------------------
	const RESOURCE_URI = '/someUrl/:pageUuid/versions';
	const PAGE_UUID = '1234';
	const PAGE_VERSION = {
		uid: 'someVersion',
		itemUUID: 'someItem',
		label: 'label',
		description: 'description',
		creationtime: new Date()
	};

	const promisePageUUID = promiseHelper.buildPromise<string>('promisePageUUID', PromiseType.RESOLVES, PAGE_UUID);
	const promisePageVersion = promiseHelper.buildPromise<IPageVersion>('promisePageVersion', PromiseType.RESOLVES, PAGE_VERSION);
	const $q = promiseHelper.$q();

	// ---------------------------------------------------------------------------
	// Variables
	// ---------------------------------------------------------------------------
	let alertService: jasmine.SpyObj<IAlertService>;
	let experienceService: jasmine.SpyObj<IExperienceService>;
	let genericEditorModalService: any;
	let confirmationModalService: any;
	let pageInfoService: jasmine.SpyObj<IPageInfoService>;
	let pageVersioningService: jasmine.SpyObj<PageVersioningService>;
	let pageVersionSelectionService: jasmine.SpyObj<PageVersionSelectionService>;

	let managePageVersionService: ManagePageVersionService;
	// ---------------------------------------------------------------------------
	// SetUp
	// ---------------------------------------------------------------------------
	beforeEach(() => {
		alertService = jasmine.createSpyObj<IAlertService>('alertService', ['showSuccess']);
		experienceService = jasmine.createSpyObj('experienceService', ['updateExperience']);
		confirmationModalService = jasmine.createSpyObj('confirmationModalService', ['confirm']);
		genericEditorModalService = jasmine.createSpyObj('genericEditorModalService', ['open']);
		pageInfoService = jasmine.createSpyObj<IPageInfoService>('pageInfoService', ['getPageUUID']);
		pageVersioningService = jasmine.createSpyObj<PageVersioningService>('pageVersioningService', ['getResourceURI', 'deletePageVersion']);
		pageVersionSelectionService = jasmine.createSpyObj<PageVersionSelectionService>('pageVersionSelectionService', ['selectPageVersion', 'getSelectedPageVersion', 'deselectPageVersion']);

		pageVersioningService.getResourceURI.and.returnValue(RESOURCE_URI);

		managePageVersionService = new ManagePageVersionService(alertService, experienceService, confirmationModalService, genericEditorModalService,
			pageInfoService, pageVersioningService, pageVersionSelectionService);
	});

	it('WHEN createPageVersion is called THEN the generic editor is opened', () => {
		// GIVEN
		pageInfoService.getPageUUID.and.returnValue(promisePageUUID);
		genericEditorModalService.open.and.returnValue(promisePageVersion);
		const componentData = {
			title: 'se.cms.versions.create',
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
			contentApi: RESOURCE_URI.replace(':pageUuid', PAGE_UUID)
		};

		// WHEN
		managePageVersionService.createPageVersion();

		// THEN
		expect(genericEditorModalService.open).toHaveBeenCalledWith(componentData, null, jasmine.any(Function));
	});

	it('WHEN editPageVersion is called THEN the generic editor is opened with data prepoulated', () => {
		// GIVEN
		pageInfoService.getPageUUID.and.returnValue(promisePageUUID);
		genericEditorModalService.open.and.returnValue(promisePageVersion);
		const componentData = {
			title: 'se.cms.versions.edit',
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
			contentApi: RESOURCE_URI.replace(':pageUuid', PAGE_UUID),
			content: PAGE_VERSION,
			componentUuid: PAGE_VERSION.uid,
			componentType: 'versioning'
		};

		// WHEN
		managePageVersionService.editPageVersion(PAGE_VERSION);

		// THEN
		expect(genericEditorModalService.open).toHaveBeenCalledWith(componentData, null, jasmine.any(Function));
	});

	it('WHEN deletePageVersion is called then a confirmation is opened and a success alert is shown', () => {
		// GIVEN
		pageInfoService.getPageUUID.and.returnValue(promisePageUUID);
		confirmationModalService.confirm.and.returnValue($q.when({}));
		pageVersioningService.deletePageVersion.and.returnValue($q.when({}));
		pageVersionSelectionService.getSelectedPageVersion.and.returnValue({
			uid: 'someOtherVersion'
		});

		// WHEN
		managePageVersionService.deletePageVersion(PAGE_VERSION.uid);

		// THEN
		expect(confirmationModalService.confirm).toHaveBeenCalledWith({
			title: "se.cms.actionitem.page.version.delete.confirmation.title",
			description: "se.cms.actionitem.page.version.delete.confirmation.description",
		});
		expect(pageVersioningService.deletePageVersion).toHaveBeenCalledWith(PAGE_UUID, PAGE_VERSION.uid);
		expect(alertService.showSuccess).toHaveBeenCalled();
		expect(experienceService.updateExperience).not.toHaveBeenCalled();

	});

	it('WHEN deletePageVersion is called for version thats currently loaded THEN a confirmation is opened, a success alert is shown and expernce is updated to show current page', () => {
		// GIVEN
		pageInfoService.getPageUUID.and.returnValue(promisePageUUID);
		confirmationModalService.confirm.and.returnValue($q.when({}));
		pageVersioningService.deletePageVersion.and.returnValue($q.when({}));
		pageVersionSelectionService.getSelectedPageVersion.and.returnValue({
			uid: PAGE_VERSION.uid
		});
		experienceService.updateExperience.and.returnValue($q.when({}));

		// WHEN
		managePageVersionService.deletePageVersion(PAGE_VERSION.uid);

		// THEN
		expect(confirmationModalService.confirm).toHaveBeenCalledWith({
			title: "se.cms.actionitem.page.version.delete.confirmation.title",
			description: "se.cms.actionitem.page.version.delete.confirmation.description",
		});
		expect(pageVersioningService.deletePageVersion).toHaveBeenCalledWith(PAGE_UUID, PAGE_VERSION.uid);
		expect(alertService.showSuccess).toHaveBeenCalled();
		expect(experienceService.updateExperience).toHaveBeenCalled();
		expect(pageVersionSelectionService.deselectPageVersion).toHaveBeenCalled();

	});
});
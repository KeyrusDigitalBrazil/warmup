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

import {IPageVersion} from 'cmssmarteditcontainer/services';
import {CrossFrameEventService, IAlertService, IExperienceService, IPageInfoService} from 'smarteditcommons';
import {PageVersionSelectionService} from 'cmssmarteditcontainer/components/versioning/services/PageVersionSelectionService';
import {PageVersioningService} from 'cmssmarteditcontainer/services/pageVersioning/PageVersioningService';
import {promiseHelper, PromiseType} from 'testhelpers';
import {CMSModesService} from 'cmscommons';

describe('test PageVersionSelectionService', () => {

	// ---------------------------------------------------------------------------
	// Constants
	// ---------------------------------------------------------------------------
	const SHOW_TOOLBAR_ITEM_CONTEXT = 'some show toolbar item contex';
	const HIDE_TOOLBAR_ITEM_CONTEXT = 'some hide toolbar item contex';
	const PAGE_VERSIONS_TOOLBAR_ITEM_KEY: string = 'se.cms.pageVersionsMenu';
	const TRANSLATION = 'SOME TRANSLATION';
	const EVENT_PERSPECTIVE_CHANGED = 'EVENT_PERSPECTIVE_CHANGED';
	const EVENT_PERSPECTIVE_REFRESHED = 'EVENT_PERSPECTIVE_REFRESHED';

	const promise1 = promiseHelper.buildPromise<string>('promise1', PromiseType.RESOLVES, TRANSLATION);
	const $q = promiseHelper.$q();
	let pageChangedCallback: (eventId: string) => angular.IPromise<any>;

	// ---------------------------------------------------------------------------
	// Variables
	// ---------------------------------------------------------------------------
	let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;
	let alertService: jasmine.SpyObj<IAlertService>;
	let experienceService: jasmine.SpyObj<IExperienceService>;
	let cMSModesService: jasmine.SpyObj<CMSModesService>;
	let pageInfoService: jasmine.SpyObj<IPageInfoService>;
	let pageVersioningService: jasmine.SpyObj<PageVersioningService>;
	let $translate: any;
	let pageVersion1: IPageVersion;

	let pageVersionSelectionService: PageVersionSelectionService;

	const MOCKED_EVENTS = {
		PAGE_SELECTED: 'PAGE_SELECTED_EVENT',
		PAGE_CHANGE: 'PAGE_CHANGE'
	};


	// ---------------------------------------------------------------------------
	// SetUp
	// ---------------------------------------------------------------------------
	beforeEach(() => {
		pageVersion1 = {
			uid: 'someVersion',
			itemUUID: 'someItem',
			label: 'label',
			description: 'description',
			creationtime: new Date()
		};
		crossFrameEventService = jasmine.createSpyObj<CrossFrameEventService>('crossFrameEventService', ['subscribe', 'publish']);
		alertService = jasmine.createSpyObj<IAlertService>('crossFrameEventService', ['showInfo']);
		experienceService = jasmine.createSpyObj('experienceService', ['updateExperience']);
		cMSModesService = jasmine.createSpyObj<CMSModesService>('cMSModesService', ['isVersioningPerspectiveActive']);
		pageInfoService = jasmine.createSpyObj<IPageInfoService>('pageInfoService', ['getPageUUID']);
		pageVersioningService = jasmine.createSpyObj<PageVersioningService>('pageVersioningService', ['getPageVersionForId']);
		$translate = jasmine.createSpy('$translate');

		$translate.and.returnValue(promise1);

		pageVersionSelectionService = new PageVersionSelectionService(
			crossFrameEventService, alertService, experienceService, cMSModesService, pageInfoService, pageVersioningService,
			$translate, SHOW_TOOLBAR_ITEM_CONTEXT, HIDE_TOOLBAR_ITEM_CONTEXT, EVENT_PERSPECTIVE_CHANGED, EVENT_PERSPECTIVE_REFRESHED, MOCKED_EVENTS);
	});

	// ---------------------------------------------------------------------------
	// Tests
	// ---------------------------------------------------------------------------
	it('GIVEN no page version is selected WHEN hideToolbarContextIfNotNeeded is called THEN the toolbar context is hidden', () => {
		// WHEN
		pageVersionSelectionService.hideToolbarContextIfNotNeeded();

		// THEN
		expect(crossFrameEventService.publish).toHaveBeenCalledWith(HIDE_TOOLBAR_ITEM_CONTEXT, PAGE_VERSIONS_TOOLBAR_ITEM_KEY);
	});

	it('GIVEN no page version is selected WHEN getSelectedPageVersion is called THEN it returns a null page', () => {
		// WHEN
		const result = pageVersionSelectionService.getSelectedPageVersion();

		// THEN
		expect(result).toBeNull();
	});

	it('GIVEN a page version is selected THEN the same version can not be selected twice', () => {
		// GIVEN
		pageVersionSelectionService.selectPageVersion(pageVersion1);

		// WHEN
		pageVersionSelectionService.selectPageVersion(pageVersion1);

		// THEN
		expect(experienceService.updateExperience).toHaveBeenCalledTimes(1);
	});

	it('WHEN a page version is selected THEN it becomes the selected page AND the toolbar context is informed', () => {
		// GIVEN
		expect(crossFrameEventService.publish).not.toHaveBeenCalled();

		// WHEN
		pageVersionSelectionService.selectPageVersion(pageVersion1);

		// THEN
		expect(crossFrameEventService.publish).toHaveBeenCalledWith(SHOW_TOOLBAR_ITEM_CONTEXT, PAGE_VERSIONS_TOOLBAR_ITEM_KEY);
		expect(experienceService.updateExperience).toHaveBeenCalledWith({
			versionId: pageVersion1.uid
		});
		expect(pageVersionSelectionService.getSelectedPageVersion()).toBe(pageVersion1);
	});

	it('WHEN a page version is selected THEN the toolbar should get event to close all items', () => {
		// WHEN
		pageVersionSelectionService.selectPageVersion(pageVersion1);

		// THEN
		expect(crossFrameEventService.publish).toHaveBeenCalledWith(MOCKED_EVENTS.PAGE_SELECTED);
	});

	it('GIVEN a page version is selected WHEN the version is removed THEN the toolbar context is informed AND an alert is shown', () => {
		// GIVEN
		pageVersionSelectionService.selectPageVersion(pageVersion1);
		expect(alertService.showInfo).not.toHaveBeenCalled();

		// WHEN
		pageVersionSelectionService.deselectPageVersion();

		// THEN
		expect(crossFrameEventService.publish).toHaveBeenCalledWith(HIDE_TOOLBAR_ITEM_CONTEXT, PAGE_VERSIONS_TOOLBAR_ITEM_KEY);
		expect(experienceService.updateExperience).toHaveBeenCalledWith({
			versionId: null
		});
		expect(alertService.showInfo).toHaveBeenCalledWith(TRANSLATION);
	});

	it('GIVEN a page version is selected WHEN the version is removed and false is passed as a parameter THEN the toolbar context is informed AND no alert is shown', () => {
		// GIVEN
		pageVersionSelectionService.selectPageVersion(pageVersion1);
		expect(alertService.showInfo).not.toHaveBeenCalled();

		// WHEN
		pageVersionSelectionService.deselectPageVersion(false);

		// THEN
		expect(crossFrameEventService.publish).toHaveBeenCalledWith(HIDE_TOOLBAR_ITEM_CONTEXT, PAGE_VERSIONS_TOOLBAR_ITEM_KEY);
		expect(experienceService.updateExperience).toHaveBeenCalledWith({
			versionId: null
		});
		expect(alertService.showInfo).not.toHaveBeenCalledWith(TRANSLATION);
	});

	describe('callbacks', () => {

		beforeEach(() => {

			expect(crossFrameEventService.subscribe.calls.count()).toBe(3);
			expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(EVENT_PERSPECTIVE_CHANGED, jasmine.any(Function));
			expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(EVENT_PERSPECTIVE_REFRESHED, jasmine.any(Function));
			expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(MOCKED_EVENTS.PAGE_CHANGE, jasmine.any(Function));

			pageChangedCallback = crossFrameEventService.subscribe.calls.argsFor(0)[1];

		});

		it(`GIVEN a versioned page is already selected and the same page is previewed in the versioning perspective 
			WHEN page change event is called 
			THEN the version remains selected`, () => {
				// GIVEN
				pageInfoService.getPageUUID.and.returnValue($q.when('someItem'));
				cMSModesService.isVersioningPerspectiveActive.and.returnValue($q.when(true));
				pageVersionSelectionService.selectPageVersion(pageVersion1);

				// WHEN
				pageChangedCallback(EVENT_PERSPECTIVE_CHANGED);

				// THEN
				expect(pageVersionSelectionService.getSelectedPageVersion()).toBe(pageVersion1);

			});

		it(`GIVEN a versioned page is already selected and a different page is previewed in the versioning perspective 
			WHEN page change event is called 
			THEN the no version is selected`, () => {
				// GIVEN
				pageInfoService.getPageUUID.and.returnValue($q.when('differentItem'));
				cMSModesService.isVersioningPerspectiveActive.and.returnValue($q.when(true));
				pageVersionSelectionService.selectPageVersion(pageVersion1);

				// WHEN
				pageChangedCallback(EVENT_PERSPECTIVE_CHANGED);

				// THEN
				expect(pageVersionSelectionService.getSelectedPageVersion()).toBe(null);

			});

		it(`GIVEN a versioned page is already selected and a same page is previewed in a non-versioning perspective 
			WHEN page change event is called 
			THEN the no version is selected`, () => {
				// GIVEN
				pageInfoService.getPageUUID.and.returnValue($q.when('differentItem'));
				cMSModesService.isVersioningPerspectiveActive.and.returnValue($q.when(false));
				pageVersionSelectionService.selectPageVersion(pageVersion1);

				// WHEN
				pageChangedCallback(EVENT_PERSPECTIVE_CHANGED);

				// THEN
				expect(pageVersionSelectionService.getSelectedPageVersion()).toBe(null);

			});

	});


});

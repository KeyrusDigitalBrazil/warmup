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
import {TreeDndOptionFactory} from "smarteditcommons/components/tree/TreeDndOptionsFactory";
import {promiseHelper} from 'testhelpers';
import * as angular from "angular";
import {ITreeDndOptions, TreeDragOptions} from "smarteditcommons";

describe('treeDndOptions', function() {
	const $q = promiseHelper.$q();
	let _TreeDndOptions: {new(options: TreeDragOptions): ITreeDndOptions};
	let confirmationModalService: any;
	let $timeout: angular.ITimeoutService;

	beforeEach(angular.mock.inject(function(_$timeout_: angular.ITimeoutService) {
		$timeout = _$timeout_;
	}));

	beforeEach(() => {
		confirmationModalService = jasmine.createSpyObj('confirmationModalService', ['confirm']);
		const alertService = jasmine.createSpyObj('alertService', ['showDanger']);

		_TreeDndOptions = TreeDndOptionFactory(
			$timeout,
			{
				dragDelay: 200
			} as any,
			confirmationModalService,
			$q,
			alertService
		);
	});

	it('passing a map containing none of the DnD keys (beforeDropCallback, allowDropCallback and onDropCallback) SHOULD have dragEnabled set to False', function() {
		const options = {
			zzzzzz: 'zzzzzz'
		} as any;
		const treeDndOptions = new _TreeDndOptions(options);
		expect(treeDndOptions.dragEnabled).toBe(false);
	});

	it('passing a map containing at least one of the callback functions(beforeDropCallback, allowDropCallback and onDropCallback) SHOULD have dragEnabled set to True', function() {
		const options = {
			beforeDropCallback() {
				return;
			}
		} as any;
		const treeDndOptions = new _TreeDndOptions(options);
		expect(treeDndOptions.dragEnabled).toBe(true);
	});

	it('passing a map containing with the DnD keys (beforeDropCallback, allowDropCallback and onDropCallback) SHOULD have set the accept, beforeDrop and dropped callbacks', function() {
		const options = {
			beforeDropCallback() {
				return;
			},
			onDropCallback() {
				return;
			},
			allowDropCallback() {
				return;
			}
		} as any;
		const treeDndOptions = new _TreeDndOptions(options);
		expect((treeDndOptions as any).callbacks.accept).not.toBe(undefined);
		expect((treeDndOptions as any).callbacks.beforeDrop).not.toBe(undefined);
		expect((treeDndOptions as any).callbacks.dropped).not.toBe(undefined);
	});

	it('if dropped is called, onDropCallback SHOULD be called with the yTreeDndEvent', function() {
		const options = jasmine.createSpyObj('options', ['onDropCallback']);

		const event = {
			source: {
				nodeScope: {
					$modelValue: 'source'
				}
			},
			dest: {
				index: 'index',
				nodesScope: {
					$modelValue: ['dest']
				}
			}
		};
		const treeDndOptions = new _TreeDndOptions(options);

		(treeDndOptions as any).callbacks.dropped(event);
		$timeout.flush();
		expect(options.onDropCallback).toHaveBeenCalledWith(jasmine.objectContaining({
			sourceNode: 'source',
			destinationNodes: ['dest'],
			position: 'index'
		}));
	});

	it('if accept is called, allowDropCallback SHOULD be called with the yTreeDndEvent ', function() {
		const options = jasmine.createSpyObj('options', ['allowDropCallback']);

		const sourceNodeScope = {
			$modelValue: 'source'
		};
		const destNodesScope = {
			$modelValue: ['dest']
		};
		const destIndex = 'index';

		const treeDndOptions = new _TreeDndOptions(options);

		(treeDndOptions as any).callbacks.accept(sourceNodeScope, destNodesScope, destIndex);
		expect(options.allowDropCallback).toHaveBeenCalledWith(jasmine.objectContaining({
			sourceNode: 'source',
			destinationNodes: ['dest'],
			position: 'index'
		}));
	});

	it('if beforeDropCallback returns an object with key confirmDropI18nKey, a confirmation modal SHOULD open with the localized message', function() {
		const options = jasmine.createSpyObj('options', ['beforeDropCallback']);

		options.beforeDropCallback.and.returnValue({
			confirmDropI18nKey: 'description'
		});

		const event = {
			source: {
				nodeScope: {
					$modelValue: 'source'
				}
			},
			dest: {
				index: 'index',
				nodesScope: {
					$modelValue: ['dest']
				}
			}
		};

		const treeDndOptions = new _TreeDndOptions(options);
		(treeDndOptions as any).callbacks.beforeDrop(event);

		expect(options.beforeDropCallback).toHaveBeenCalledWith(jasmine.objectContaining({
			sourceNode: 'source',
			destinationNodes: ['dest'],
			position: 'index'
		}));
		expect(confirmationModalService.confirm).toHaveBeenCalledWith({
			description: 'description'
		});
	});

	it('if beforeDropCallback returns a promise, then result of that promise SHOULD be returned', function() {
		const options = jasmine.createSpyObj('options', ['beforeDropCallback']);
		options.beforeDropCallback.and.returnValue($q.when(false));

		const event = {
			source: {
				nodeScope: {
					$modelValue: 'source'
				}
			},
			dest: {
				index: 'index',
				nodesScope: {
					$modelValue: ['dest']
				}
			}
		};

		const treeDndOptions = new _TreeDndOptions(options);
		const result = (treeDndOptions as any).callbacks.beforeDrop(event);

		expect(options.beforeDropCallback).toHaveBeenCalledWith(jasmine.objectContaining({
			sourceNode: 'source',
			destinationNodes: ['dest'],
			position: 'index'
		}));
		expect(result).toBeResolvedWithData(false);
	});

});
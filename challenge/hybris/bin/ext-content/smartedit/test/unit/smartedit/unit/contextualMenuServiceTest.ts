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
import * as angular from 'angular';
import {ComponentAttributes, IContextualMenuButton, IContextualMenuConfiguration} from 'smarteditcommons';
import {ContextualMenu, ContextualMenuService} from 'smartedit/services';

describe('test smarteditServicesModule', () => {

	const regexpKeys: string[] = ['^((?!Slot).)*$'];
	const nameI18nKey: string = 'some.key';
	const componentAttributes = {} as ComponentAttributes;
	const element: HTMLElement = {} as HTMLElement;
	const slotId: string = 'slotId';
	const slotUuid: string = 'slotUuid';
	const isComponentHidden: boolean = false;

	const trueCondition = (configuration: IContextualMenuConfiguration) => {
		return true;
	};
	const falseCondition = (configuration: IContextualMenuConfiguration) => {
		return false;
	};
	const id1Condition = (configuration: IContextualMenuConfiguration) => {
		return configuration.componentId === 'ComponentId1';
	};
	const id2Condition = (configuration: IContextualMenuConfiguration) => {
		return configuration.componentId === 'ComponentId2';
	};
	const type1Condition = (configuration: IContextualMenuConfiguration) => {
		return configuration.componentType === 'ComponentType1';
	};

	let contextualMenuService: ContextualMenuService;
	let $rootScope: angular.IRootScopeService;

	beforeEach(angular.mock.module('smarteditServicesModule'));

	beforeEach(angular.mock.inject(function(_contextualMenuService_: ContextualMenuService, _$rootScope_: angular.IRootScopeService) {
		contextualMenuService = _contextualMenuService_;
		$rootScope = _$rootScope_;
	}));

	function getItem(key: string, priority?: number): IContextualMenuButton {

		return {
			key,
			action: {
				template: "dummyTemplate string"
			},
			priority
		} as IContextualMenuButton;
	}

	it('addItems WILL throw an error when item doesnt contain a valid key', () => {
		expect(function() {
			contextualMenuService.addItems({
				type1: [{
					action: {
						template: "dummyTemplate string"
					},
					key: null,
					regexpKeys,
					nameI18nKey
				}, {
					action: {
						template: "dummyTemplate string"
					},
					key: 'contextualMenuItem2',
					regexpKeys,
					nameI18nKey
				}]
			});
		}).toThrow(new Error("addItems() - Cannot add items. Error: Item doesn't have key."));
	});

	it('getContextualMenuByType will return an ordered unique array of contextual menu items when componenttype is given', () => {

		const item1 = getItem('contextualMenuItem1', 2);
		const item2 = getItem('contextualMenuItem2', 1);
		const item3 = getItem('contextualMenuItem3');
		const item4 = getItem('contextualMenuItem4');

		contextualMenuService.addItems({
			type1: [item1, item2]
		});

		contextualMenuService.addItems({
			type1: [item3],
			type2: [item3, item4]
		});

		expect(contextualMenuService.getContextualMenuByType('type1')).toEqual(
			[item2, item1, item3]);
		expect(contextualMenuService.getContextualMenuByType('type2')).toEqual(
			[item3, item4]);
	});

	it('getContextualMenuByType will return an unique array of contextual menu items when it matches the regexps', () => {

		contextualMenuService.addItems({
			'*Suffix': [getItem('element1'), getItem('element2')],
			'.*Suffix': [getItem('element2'), getItem('element3')],
			'TypeSuffix': [getItem('element3'), getItem('element4')],
			'^((?!Middle).)*$': [getItem('element4'), getItem('element5')],
			'PrefixType': [getItem('element5'), getItem('element6')]
		});

		expect(contextualMenuService.getContextualMenuByType('TypeSuffix')).toEqual(
			[getItem('element1'), getItem('element2'), getItem('element3'), getItem('element4'), getItem('element5')]);

		expect(contextualMenuService.getContextualMenuByType('TypeSuffixes')).toEqual(
			[getItem('element2'), getItem('element3'), getItem('element4'), getItem('element5')]);

		expect(contextualMenuService.getContextualMenuByType('MiddleTypeSuffix')).toEqual(
			[getItem('element1'), getItem('element2'), getItem('element3')]);
	});

	describe('getContextualMenuItems will return an array-of-array of contextual menu items based on condition', () => {

		it('will return those menu items which satisfy the condition or those that have no condition set (default condition to be true)', () => {

			// GIVEN
			contextualMenuService.addItems({
				ComponentType1: [{
					key: 'key1',
					action: {
						template: "dummy template string"
					},
					i18nKey: 'ICON1',
					iconIdle: 'icon1.png',
					regexpKeys,
					nameI18nKey
				}, {
					key: 'key2',
					action: {
						template: "dummy template string"
					},
					i18nKey: 'ICON2',
					condition: id2Condition,
					iconIdle: 'icon2.png',
					regexpKeys,
					nameI18nKey
				}, {
					key: 'key3',
					action: {
						template: "dummy template string"
					},
					i18nKey: 'ICON3',
					condition: trueCondition,
					iconIdle: 'icon3.png',
					regexpKeys,
					nameI18nKey
				}, {
					key: 'key4',
					action: {
						template: "dummy template string"
					},
					i18nKey: 'ICON4',
					condition: falseCondition,
					iconIdle: 'icon4.png',
					regexpKeys,
					nameI18nKey
				}, {
					key: 'key5',
					action: {
						template: "dummy template string"
					},
					i18nKey: 'ICON5',
					condition: id1Condition,
					iconIdle: 'icon5.png',
					regexpKeys,
					nameI18nKey
				}, {
					key: 'key6',
					action: {
						template: "dummy template string"
					},
					i18nKey: 'ICON6',
					condition: type1Condition,
					iconIdle: 'icon6.png',
					regexpKeys,
					nameI18nKey
				}]
			});

			// WHEN
			const promise: angular.IPromise<ContextualMenu> = contextualMenuService.getContextualMenuItems({
				componentId: 'ComponentId1',
				componentType: 'ComponentType1',
				iLeftBtns: 3,
				element,
				componentAttributes,
				slotId,
				slotUuid,
				isComponentHidden
			});

			promise.then(function(result) {
				// THEN
				expect(result).toEqual({
					leftMenuItems: [{
						key: 'key1',
						action: {
							template: "dummy template string"
						},
						i18nKey: 'ICON1',
						iconIdle: 'icon1.png',
						regexpKeys,
						nameI18nKey
					}, {
						key: 'key3',
						action: {
							template: "dummy template string"
						},
						i18nKey: 'ICON3',
						condition: trueCondition,
						iconIdle: 'icon3.png',
						regexpKeys,
						nameI18nKey
					}, {
						key: 'key5',
						action: {
							template: "dummy template string"
						},
						i18nKey: 'ICON5',
						condition: id1Condition,
						iconIdle: 'icon5.png',
						regexpKeys,
						nameI18nKey
					}],
					moreMenuItems: [{
						key: 'key6',
						action: {
							template: "dummy template string"
						},
						i18nKey: 'ICON6',
						condition: type1Condition,
						iconIdle: 'icon6.png',
						regexpKeys,
						nameI18nKey
					}]
				} as ContextualMenu);
			});
			$rootScope.$digest();
		});

		it('for iLeftBtns= 3, will set a maximum of 3 menu items to the left (1st element in the array) and the rest to the right (2nd element in the array)', () => {

			// GIVEN
			contextualMenuService.addItems({
				ComponentType1: [{
					key: 'key1',
					action: {
						template: "dummy template string"
					},
					i18nKey: 'ICON1',
					iconIdle: 'icon1.png',
					regexpKeys,
					nameI18nKey
				}, {
					key: 'key2',
					action: {
						template: "dummy template string"
					},
					i18nKey: 'ICON2',
					condition: id2Condition,
					iconIdle: 'icon2.png',
					regexpKeys,
					nameI18nKey
				}, {
					key: 'key3',
					action: {
						template: "dummy template string"
					},
					i18nKey: 'ICON3',
					condition: trueCondition,
					iconIdle: 'icon3.png',
					regexpKeys,
					nameI18nKey
				}, {
					key: 'key4',
					action: {
						template: "dummy template string"
					},
					i18nKey: 'ICON4',
					condition: falseCondition,
					iconIdle: 'icon4.png',
					regexpKeys,
					nameI18nKey
				}, {
					key: 'key5',
					action: {
						template: "dummy template string"
					},
					i18nKey: 'ICON5',
					condition: id1Condition,
					iconIdle: 'icon5.png',
					regexpKeys,
					nameI18nKey
				}, {
					key: 'key6',
					action: {
						template: "dummy template string"
					},
					i18nKey: 'ICON6',
					condition: type1Condition,
					iconIdle: 'icon6.png',
					regexpKeys,
					nameI18nKey
				}]
			});

			// WHEN
			const promise: angular.IPromise<ContextualMenu> = contextualMenuService.getContextualMenuItems({
				componentId: 'ComponentId1',
				componentType: 'ComponentType1',
				iLeftBtns: 3,
				element,
				componentAttributes,
				slotId,
				slotUuid,
				isComponentHidden
			});


			promise.then(function(result) {
				// THEN
				expect(result.leftMenuItems).toEqual([{
					key: 'key1',
					action: {
						template: "dummy template string"
					},
					i18nKey: 'ICON1',
					iconIdle: 'icon1.png',
					regexpKeys,
					nameI18nKey
				}, {
					key: 'key3',
					action: {
						template: "dummy template string"
					},
					i18nKey: 'ICON3',
					condition: trueCondition,
					iconIdle: 'icon3.png',
					regexpKeys,
					nameI18nKey
				}, {
					key: 'key5',
					action: {
						template: "dummy template string"
					},
					i18nKey: 'ICON5',
					condition: id1Condition,
					iconIdle: 'icon5.png',
					regexpKeys,
					nameI18nKey
				}]);

				// THEN
				expect(result.moreMenuItems).toEqual([{
					key: 'key6',
					action: {
						template: "dummy template string"
					},
					i18nKey: 'ICON6',
					condition: type1Condition,
					iconIdle: 'icon6.png',
					regexpKeys,
					nameI18nKey
				}]);
			});
		});
	});

	it('getContextualMenuItems will provide the dom element', () => {

		// GIVEN
		const contextualItemMock = jasmine.createSpyObj('contextualItemMock', ['condition']);
		contextualItemMock.key = 'key1';
		contextualItemMock.i18nKey = 'ICON1';
		contextualItemMock.condition.and.returnValue(true);
		contextualItemMock.icon = 'icon1.png';
		contextualItemMock.action = {
			template: "dummy template string"
		};

		const obj = {
			ComponentType1: [contextualItemMock]
		};
		contextualMenuService.addItems(obj);

		// WHEN
		const promise: angular.IPromise<ContextualMenu> = contextualMenuService.getContextualMenuItems({
			componentId: 'ComponentId1',
			componentType: 'ComponentType1',
			iLeftBtns: 1,
			element,
			componentAttributes,
			slotId,
			slotUuid,
			isComponentHidden
		});


		promise.then(function(result) {
			// THEN
			expect(result.leftMenuItems).toEqual(obj.ComponentType1);

			// THEN
			expect(contextualItemMock.condition).toHaveBeenCalledWith({
				componentId: 'ComponentId1',
				componentType: 'ComponentType1',
				element
			});
		});
	});

	it('removeItemByKey will remove all the items with the provided key when the condition is called', () => {

		// GIVEN
		contextualMenuService.addItems({
			ComponentType1: [{
				key: 'key1',
				action: {
					template: "dummy template string"
				},
				i18nKey: 'ICON1',
				iconIdle: 'icon1.png',
				regexpKeys,
				nameI18nKey
			}, {
				key: 'key2',
				action: {
					template: "dummy template string"
				},
				i18nKey: 'ICON2',
				iconIdle: 'icon2.png',
				regexpKeys,
				nameI18nKey
			}, {
				key: 'key3',
				action: {
					template: "dummy template string"
				},
				i18nKey: 'ICON3',
				iconIdle: 'icon3.png',
				regexpKeys,
				nameI18nKey
			}],
			ComponentType2: [{
				key: 'key1',
				action: {
					template: "dummy template string"
				},
				i18nKey: 'ICON1',
				iconIdle: 'icon1.png',
				regexpKeys,
				nameI18nKey
			}, {
				key: 'key2',
				action: {
					template: "dummy template string"
				},
				i18nKey: 'ICON2',
				iconIdle: 'icon2.png',
				regexpKeys,
				nameI18nKey
			}, {
				key: 'key5',
				action: {
					template: "dummy template string"
				},
				i18nKey: 'ICON5',
				iconIdle: 'icon5.png',
				regexpKeys,
				nameI18nKey
			}]
		});

		// WHEN
		contextualMenuService.removeItemByKey('key2');
		const promise: angular.IPromise<ContextualMenu> = contextualMenuService.getContextualMenuItems({
			componentId: 'ComponentId1',
			componentType: 'ComponentType1',
			iLeftBtns: 3,
			element,
			componentAttributes,
			slotId,
			slotUuid,
			isComponentHidden
		});


		promise.then(function(result) {

			// THEN
			expect(result.leftMenuItems).toEqual([{
				key: 'key1',
				action: {
					template: "dummy template string"
				},
				i18nKey: 'ICON1',
				iconIdle: 'icon1.png',
				regexpKeys,
				nameI18nKey
			}, {
				key: 'key3',
				action: {
					template: "dummy template string"
				},
				i18nKey: 'ICON3',
				iconIdle: 'icon3.png',
				regexpKeys,
				nameI18nKey
			}]);

			// THEN
			expect(result.leftMenuItems).toEqual([{
				key: 'key1',
				action: {
					template: "dummy template string"
				},
				i18nKey: 'ICON1',
				iconIdle: 'icon1.png',
				regexpKeys,
				nameI18nKey
			}, {
				key: 'key5',
				action: {
					template: "dummy template string"
				},
				i18nKey: 'ICON5',
				iconIdle: 'icon5.png',
				regexpKeys,
				nameI18nKey
			}]);
		});



	});

	it('removeItemByKey will not do anything when the provided key does not match an item', () => {

		// GIVEN
		contextualMenuService.addItems({
			ComponentType1: [{
				key: 'key1',
				action: {
					template: "dummy template string"
				},
				i18nKey: 'ICON1',
				iconIdle: 'icon1.png',
				regexpKeys,
				nameI18nKey
			}, {
				key: 'key2',
				action: {
					template: "dummy template string"
				},
				i18nKey: 'ICON2',
				iconIdle: 'icon2.png',
				regexpKeys,
				nameI18nKey
			}, {
				key: 'key3',
				action: {
					template: "dummy template string"
				},
				i18nKey: 'ICON3',
				iconIdle: 'icon3.png',
				regexpKeys,
				nameI18nKey
			}],
			ComponentType2: [{
				key: 'key1',
				action: {
					template: "dummy template string"
				},
				i18nKey: 'ICON1',
				iconIdle: 'icon1.png',
				regexpKeys,
				nameI18nKey
			}, {
				key: 'key2',
				action: {
					template: "dummy template string"
				},
				i18nKey: 'ICON2',
				iconIdle: 'icon2.png',
				regexpKeys,
				nameI18nKey
			}, {
				key: 'key5',
				action: {
					template: "dummy template string"
				},
				i18nKey: 'ICON5',
				iconIdle: 'icon5.png',
				regexpKeys,
				nameI18nKey
			}]
		});
		contextualMenuService.removeItemByKey('key10');

		// WHEN
		const promise: angular.IPromise<ContextualMenu> = contextualMenuService.getContextualMenuItems({
			componentId: 'ComponentId1',
			componentType: 'ComponentType1',
			iLeftBtns: 3,
			element,
			componentAttributes,
			slotId,
			slotUuid,
			isComponentHidden
		});


		promise.then(function(result) {

			// THEN
			expect(result.leftMenuItems).toEqual([{
				key: 'key1',
				action: {
					template: "dummy template string"
				},
				i18nKey: 'ICON1',
				iconIdle: 'icon1.png',
				regexpKeys,
				nameI18nKey
			}, {
				key: 'key2',
				action: {
					template: "dummy template string"
				},
				i18nKey: 'ICON2',
				iconIdle: 'icon2.png',
				regexpKeys,
				nameI18nKey
			}, {
				key: 'key3',
				action: {
					template: "dummy template string"
				},
				i18nKey: 'ICON3',
				iconIdle: 'icon3.png',
				regexpKeys,
				nameI18nKey
			}]);

			// THEN
			expect(result.leftMenuItems).toEqual([{
				key: 'key1',
				action: {
					template: "dummy template string"
				},
				i18nKey: 'ICON1',
				iconIdle: 'icon1.png',
				regexpKeys,
				nameI18nKey
			}, {
				key: 'key2',
				action: {
					template: "dummy template string"
				},
				i18nKey: 'ICON2',
				iconIdle: 'icon2.png',
				regexpKeys,
				nameI18nKey
			}, {
				key: 'key5',
				action: {
					template: "dummy template string"
				},
				i18nKey: 'ICON5',
				iconIdle: 'icon5.png',
				regexpKeys,
				nameI18nKey
			}]);
		});
	});
});

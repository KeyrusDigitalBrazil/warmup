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
import {
	IPositionRegistry,
	IResizeListener,
	PolyfillService,
	SystemEventService,
	TestModeService,
	TypedMap
} from "smarteditcommons";
import {ComponentHandlerService} from "smartedit/services";
import {
	SmartEditContractChangeListener
} from "smartedit/services/SmartEditContractChangeListener";
import * as angular from "angular";
import {promiseHelper, LogHelper} from "testhelpers";

describe('smartEditContractChangeListener in polyfill mode', () => {
	let $q: angular.IQService;
	let $rootScope: angular.IRootScopeService;
	let $interval: angular.IIntervalService;
	let systemEventService: SystemEventService;
	let componentHandlerService: jasmine.SpyObj<ComponentHandlerService>;
	let CONTRACT_CHANGE_LISTENER_PROCESS_EVENTS: TypedMap<string>;
	let CONTRACT_CHANGE_LISTENER_COMPONENT_PROCESS_STATUS: TypedMap<string>;
	const SMARTEDIT_COMPONENT_PROCESS_STATUS = "smartEditComponentProcessStatus";
	let yjQuery: any;
	let isInExtendedViewPort: jasmine.Spy;
	let smartEditContractChangeListener: SmartEditContractChangeListener;
	let testModeService: jasmine.SpyObj<TestModeService>;
	let mutationObserverMock: jasmine.SpyObj<MutationObserver>;
	let intersectionObserverMock: jasmine.SpyObj<IntersectionObserver>;
	let mutationObserverCallback: any;
	let intersectionObserverCallback: any;
	let onComponentRepositionedCallback: jasmine.Spy;
	let onComponentResizedCallback: (component: HTMLElement) => void;
	let onComponentsAddedCallback: jasmine.Spy;
	let onComponentsRemovedCallback: jasmine.Spy;
	let onComponentChangedCallback: jasmine.Spy;
	let resizeListener: jasmine.SpyObj<IResizeListener>;
	let positionRegistry: jasmine.SpyObj<IPositionRegistry>;
	let runIntersectionObserver: (queue: IntersectionObserverEntry[]) => void;
	let parent: jasmine.SpyObj<HTMLElement>;
	let directParent: jasmine.SpyObj<HTMLElement>;
	let component0: jasmine.SpyObj<HTMLElement>;
	let component1: jasmine.SpyObj<HTMLElement>;
	let component2: jasmine.SpyObj<HTMLElement>;
	let component21: jasmine.SpyObj<HTMLElement>;
	let component3: jasmine.SpyObj<HTMLElement>;
	let invisibleComponent: jasmine.SpyObj<HTMLElement>;
	let nonProcessableComponent: jasmine.SpyObj<HTMLElement>;
	let detachedComponent: jasmine.SpyObj<HTMLElement>;
	let polyfillService: jasmine.SpyObj<PolyfillService>;
	const holder: any = {};

	let SECOND_LEVEL_CHILDREN: jasmine.SpyObj<HTMLElement>[];
	let INTERSECTIONS_MAPPING: IntersectionObserverEntry[];
	let $document: angular.IDocumentService;

	let COMPONENT_CLASS: string;
	let UUID_ATTRIBUTE: string;
	let ID_ATTRIBUTE: string;
	const INITIAL_PAGE_UUID = 'INITIAL_PAGE_UUID';
	const ANY_PAGE_UUID = 'ANY_PAGE_UUID';
	const REPROCESS_TIMEOUT = 100;
	const BODY_TAG = 'body';
	const BODY = {};

	beforeEach(() => {
		(window as any).elementResizeDetectorMaker = () => {
			return {
				uninstall: angular.noop
			};
		};

		testModeService = jasmine.createSpyObj('testModeService', ['isE2EMode']);
		testModeService.isE2EMode.and.returnValue(true);

		polyfillService = jasmine.createSpyObj('polyfillService', ['isEligibleForExtendedView']);
		polyfillService.isEligibleForExtendedView.and.returnValue(true);

		isInExtendedViewPort = jasmine.createSpy('isInExtendedViewPort');

		// we here give isInExtendedViewPort the same beahviour as isIntersecting
		isInExtendedViewPort.and.callFake((element: HTMLElement) => {
			const obj = INTERSECTIONS_MAPPING.find((object) => {
				return object.target === element;
			});
			return obj ? obj.isIntersecting : false;
		});

		componentHandlerService = jasmine.createSpyObj('componentHandlerService', ['getFromSelector', 'getPageUUID', 'getClosestSmartEditComponent', 'isSmartEditComponent', 'getFirstSmartEditComponentChildren', 'getParent']);
		resizeListener = jasmine.createSpyObj('resizeListener', ['register', 'unregister', 'fix', 'dispose', 'init']);
		positionRegistry = jasmine.createSpyObj('positionRegistry', ['register', 'unregister', 'getRepositionedComponents', 'dispose']);

		yjQuery = jasmine.createSpyObj('yjQuery', ['contains']);
		yjQuery.contains.and.callFake((container: any, element: any) => {
			if (container !== $document[0]) {
				throw new Error("yjQuery.contains should have been the plain document object");
			}
			return element.name !== (detachedComponent as any).name;
		});
		yjQuery.fn = {
			extend() {
				return;
			}
		};
	});

	beforeEach(angular.mock.inject((
		_$document_: angular.IDocumentService,
		_$rootScope_: angular.IRootScopeService,
		_$interval_: angular.IIntervalService,
		_systemEventService_: SystemEventService,
	) => {
		$q = promiseHelper.$q();
		$document = _$document_;
		$rootScope = _$rootScope_;
		$interval = _$interval_;
		systemEventService = _systemEventService_;
		const isInDOM = (component: any) => yjQuery.contains($document[0], component);

		const TYPE_ATTRIBUTE = 'data-smartedit-component-type';
		CONTRACT_CHANGE_LISTENER_PROCESS_EVENTS = {
			PROCESS_COMPONENTS: 'contractChangeListenerProcessComponents',
			RESTART_PROCESS: 'contractChangeListenerRestartProcess'
		};
		CONTRACT_CHANGE_LISTENER_COMPONENT_PROCESS_STATUS = {
			PROCESS: "processComponent",
			REMOVE: "removeComponent",
			KEEP_VISIBLE: "keepComponentVisible"
		};
		const CONTRACT_CHANGE_LISTENER_INTERSECTION_OBSERVER_OPTIONS: IntersectionObserverInit = {
			root: null,
			rootMargin: '1000px',
			threshold: 0
		};
		COMPONENT_CLASS = 'smartEditComponent';
		UUID_ATTRIBUTE = 'data-smartedit-component-uuid';
		ID_ATTRIBUTE = 'data-smartedit-component-id';

		const compareHTMLElementsPosition = (key: string) => {
			return (a: any, b: any) => {
				if (key) {
					a = a[key];
					b = b[key];
				}
				if (a === b) {
					return 0;
				}
				if (!a.compareDocumentPosition) {
					return a.sourceIndex - b.sourceIndex;
				}
				if (a.compareDocumentPosition(b) & 2) {
					return 1;
				}
				return -1;
			};
		};

		smartEditContractChangeListener = new SmartEditContractChangeListener(
			isInDOM,
			componentHandlerService,
			resizeListener,
			positionRegistry,
			compareHTMLElementsPosition,
			isInExtendedViewPort,
			new LogHelper(),
			$q,
			$interval,
			$rootScope,
			(window as any).smarteditLodash,
			systemEventService,
			polyfillService,
			REPROCESS_TIMEOUT,
			TYPE_ATTRIBUTE,
			ID_ATTRIBUTE,
			UUID_ATTRIBUTE,
			CONTRACT_CHANGE_LISTENER_PROCESS_EVENTS,
			CONTRACT_CHANGE_LISTENER_COMPONENT_PROCESS_STATUS,
			CONTRACT_CHANGE_LISTENER_INTERSECTION_OBSERVER_OPTIONS,
			500,
			SMARTEDIT_COMPONENT_PROCESS_STATUS,
			250,
			testModeService
		);

		mutationObserverMock = jasmine.createSpyObj('MutationObserver', ['observe', 'disconnect']);
		spyOn(smartEditContractChangeListener, '_newMutationObserver').and.callFake(function(callback: jasmine.SpyObj<MutationCallback>) {
			mutationObserverCallback = callback;
			this.observe = angular.noop;
			this.disconnect = angular.noop;
			return mutationObserverMock;
		});

		intersectionObserverMock = jasmine.createSpyObj('IntersectionObserver', ['observe', 'unobserve', 'disconnect']);
		spyOn(smartEditContractChangeListener, '_newIntersectionObserver').and.callFake((callback: jasmine.SpyObj<IntersectionObserverCallback>) => {
			intersectionObserverCallback = callback;
			return intersectionObserverMock;
		});
		intersectionObserverMock.observe.and.callFake((comp: HTMLElement) => {
			// run time intersectionObserver would indeed trigger a callback immediately after observing
			intersectionObserverCallback(INTERSECTIONS_MAPPING.filter((intersection) => {
				return intersection.target === comp;
			}));
		});

		runIntersectionObserver = (queue) => {
			intersectionObserverCallback(queue);
			$rootScope.$digest();
		};

		onComponentRepositionedCallback = jasmine.createSpy('onComponentRepositioned');
		smartEditContractChangeListener.onComponentRepositioned(onComponentRepositionedCallback);

		onComponentResizedCallback = angular.noop;
		smartEditContractChangeListener.onComponentResized(onComponentResizedCallback);

		onComponentsAddedCallback = jasmine.createSpy('onComponentsAdded');
		smartEditContractChangeListener.onComponentsAdded(onComponentsAddedCallback);

		onComponentsRemovedCallback = jasmine.createSpy('onComponentsRemoved');
		smartEditContractChangeListener.onComponentsRemoved(onComponentsRemovedCallback);

		onComponentChangedCallback = jasmine.createSpy('onComponentChangedCallback');
		smartEditContractChangeListener.onComponentChanged(onComponentChangedCallback);
	}));

	beforeEach(() => {

		parent = jasmine.createSpyObj<HTMLElement>('parent', ['attr']);
		(parent as any).nodeType = 1;
		(parent as any).className = COMPONENT_CLASS;
		(parent as any).attr.and.returnValue('parent');
		(parent as any).name = 'parent';
		(parent as any).contains = () => {
			return true;
		};
		(parent as any).sourceIndex = 0;
		(parent as any).dataset = {};

		directParent = jasmine.createSpyObj('directParent', ['attr']);
		(directParent as any).nodeType = 1;
		(directParent as any).attr.and.returnValue('directParent');
		(directParent as any).name = 'directParent';
		(directParent as any).contains = () => {
			return false;
		};
		(directParent as any).sourceIndex = 1;
		(directParent as any).dataset = {};

		component0 = jasmine.createSpyObj('component0', ['attr']);
		(component0 as any).nodeType = 1;
		(component0 as any).className = "nonSmartEditComponent";
		(component0 as any).attr.and.returnValue('component0');
		(component0 as any).name = 'component0';
		(component0 as any).contains = () => {
			return false;
		};
		(component0 as any).sourceIndex = 2;
		(component0 as any).dataset = {};

		component1 = jasmine.createSpyObj('component1', ['attr']);
		(component1 as any).nodeType = 1;
		(component1 as any).className = COMPONENT_CLASS;
		(component1 as any).attr.and.returnValue('component1');
		(component1 as any).name = 'component1';
		(component1 as any).contains = () => {
			return true;
		};
		(component1 as any).sourceIndex = 3;
		(component1 as any).dataset = {};

		component21 = jasmine.createSpyObj('component2_1', ['attr']);
		(component21 as any).nodeType = 1;
		(component21 as any).className = COMPONENT_CLASS;
		(component21 as any).attr.and.returnValue('component2_1');
		(component21 as any).name = 'component2_1';
		(component21 as any).contains = () => {
			return false;
		};
		(component21 as any).sourceIndex = 5;
		(component21 as any).dataset = {};

		component2 = jasmine.createSpyObj('component2', ['attr']);
		(component2 as any).nodeType = 1;
		(component2 as any).className = COMPONENT_CLASS;
		(component2 as any).attr.and.returnValue('component2');
		(component2 as any).name = 'component2';
		(component2 as any).contains = (node: HTMLElement) => {
			return node === component21;
		};
		(component2 as any).sourceIndex = 4;
		(component2 as any).dataset = {};

		component3 = jasmine.createSpyObj('component3', ['attr']);
		(component3 as any).nodeType = 1;
		(component3 as any).className = COMPONENT_CLASS;
		(component3 as any).attr.and.returnValue('component3');
		(component3 as any).name = 'component3';
		(component3 as any).contains = () => {
			return false;
		};
		(component3 as any).sourceIndex = 6;
		(component3 as any).dataset = {};

		invisibleComponent = jasmine.createSpyObj('invisibleComponent', ['attr']);
		(invisibleComponent as any).nodeType = 1;
		(invisibleComponent as any).className = COMPONENT_CLASS;
		(invisibleComponent as any).attr.and.returnValue('invisibleComponent');
		(invisibleComponent as any).name = 'invisibleComponent';
		(invisibleComponent as any).contains = () => {
			return false;
		};
		(invisibleComponent as any).sourceIndex = 8;
		(invisibleComponent as any).dataset = {};

		nonProcessableComponent = jasmine.createSpyObj('nonProcessableComponent', ['attr']);
		(nonProcessableComponent as any).nodeType = 1;
		(nonProcessableComponent as any).className = COMPONENT_CLASS;
		(nonProcessableComponent as any).attr.and.returnValue('nonProcessableComponent');
		(nonProcessableComponent as any).name = 'nonProcessableComponent';
		(nonProcessableComponent as any).contains = () => {
			return false;
		};
		(nonProcessableComponent as any).sourceIndex = 8;
		(nonProcessableComponent as any).dataset = {};

		detachedComponent = jasmine.createSpyObj('detachedComponent', ['attr']);
		(detachedComponent as any).nodeType = 1;
		(detachedComponent as any).className = COMPONENT_CLASS;
		(detachedComponent as any).attr.and.returnValue('detachedComponent');
		(detachedComponent as any).name = 'detachedComponent';
		(detachedComponent as any).contains = () => {
			return false;
		};
		(detachedComponent as any).sourceIndex = 9;
		(detachedComponent as any).dataset = {};

		let pageUUIDCounter = 0;
		componentHandlerService.getPageUUID.and.callFake(() => {
			pageUUIDCounter++;
			if (pageUUIDCounter === 1) {
				return $q.when(INITIAL_PAGE_UUID);
			} else if (pageUUIDCounter === 2) {
				return $q.when(ANY_PAGE_UUID);
			}
			return $q.when(null);
		});

		componentHandlerService.getFromSelector.and.callFake((arg: string) => {
			if (arg === BODY_TAG) {
				return BODY;
			}
			return null;
		});

		componentHandlerService.isSmartEditComponent.and.callFake((node: HTMLElement) => {
			return node.className && node.className.split(/[\s]+/).indexOf(COMPONENT_CLASS) > -1;
		});

		componentHandlerService.getClosestSmartEditComponent.and.callFake((node: HTMLElement) => {
			if (node === parent || node === component1 || node === component2 || node === component21 || node === component3) {
				return [node];
			} else if (node === component0) {
				return [parent];
			} else {
				return [];
			}
		});

		componentHandlerService.getParent.and.callFake((node: HTMLElement) => {
			if (node === component21) {
				return [component2];
			} else if (node === component1 || node === component2 || node === component3) {
				return [parent];
			} else {
				return [];
			}
		});

		SECOND_LEVEL_CHILDREN = [component1];
		INTERSECTIONS_MAPPING = [{
			isIntersecting: true,
			target: component1 // child before 'parent'
		}, {
			isIntersecting: true,
			target: parent
		}, {
			isIntersecting: false,
			target: invisibleComponent
		}, {
			isIntersecting: true,
			target: nonProcessableComponent
		}] as any;

		componentHandlerService.getFirstSmartEditComponentChildren.and.callFake((node: HTMLElement) => {
			if (node === BODY) {
				return [parent];
			} else if (node === parent) {
				return SECOND_LEVEL_CHILDREN;
			} else if (node === component2) {
				return [component21];
			} else if (node === component0) {
				return [component2]; // ok to just return array, slice is applied on it
			} else {
				return [];
			}
		});

		holder.canProcess = (comp: HTMLElement) => {
			return comp !== nonProcessableComponent;
		};

		systemEventService.subscribe(CONTRACT_CHANGE_LISTENER_PROCESS_EVENTS.PROCESS_COMPONENTS, (eventId, components) => {
			const result = components.map((component: HTMLElement) => {
				component.dataset[SMARTEDIT_COMPONENT_PROCESS_STATUS] = holder.canProcess(component) ? CONTRACT_CHANGE_LISTENER_COMPONENT_PROCESS_STATUS.PROCESS : CONTRACT_CHANGE_LISTENER_COMPONENT_PROCESS_STATUS.REMOVE;
				return component;
			});
			return $q.when(result);
		});

	});

	beforeEach(() => {
		smartEditContractChangeListener.initListener();
		$rootScope.$digest();
	});

	describe('DOM intersections', () => {

		it('should register resize and position listeners on existing visible smartedit components that are processable', () => {
			expect(resizeListener.init).toHaveBeenCalled();

			expect(resizeListener.register.calls.count()).toEqual(2);
			expect(resizeListener.register.calls.argsFor(0)[0]).toBe(parent);
			expect(resizeListener.register.calls.argsFor(1)[0]).toBe(component1);

			expect(resizeListener.fix.calls.count()).toEqual(1);
			expect(resizeListener.fix.calls.argsFor(0)[0]).toBe(parent);

			expect(positionRegistry.register.calls.count()).toEqual(2);
			expect(positionRegistry.register.calls.argsFor(0)[0]).toBe(parent);
			expect(positionRegistry.register.calls.argsFor(1)[0]).toBe(component1);

			expect(onComponentsAddedCallback.calls.count()).toBe(2);
			expect(onComponentsAddedCallback.calls.argsFor(0)[0]).toEqual([parent]);
			expect(onComponentsAddedCallback.calls.argsFor(1)[0]).toEqual([component1]);
		});

		it('event with same intersections for components should not retrigger anything', () => {
			runIntersectionObserver(INTERSECTIONS_MAPPING);

			resizeListener.register.calls.reset();
			positionRegistry.register.calls.reset();
			onComponentsAddedCallback.calls.reset();
			onComponentsRemovedCallback.calls.reset();

			runIntersectionObserver(INTERSECTIONS_MAPPING);

			expect(resizeListener.register).not.toHaveBeenCalled();
			expect(positionRegistry.register).not.toHaveBeenCalled();
			expect(onComponentsAddedCallback).not.toHaveBeenCalled();
			expect(onComponentsRemovedCallback).not.toHaveBeenCalled();
		});

		it('when components are no longer visible, they are destroyed', () => {
			runIntersectionObserver(INTERSECTIONS_MAPPING);

			resizeListener.register.calls.reset();
			resizeListener.unregister.calls.reset();
			positionRegistry.register.calls.reset();
			positionRegistry.unregister.calls.reset();
			onComponentsAddedCallback.calls.reset();
			onComponentsRemovedCallback.calls.reset();
			resizeListener.fix.calls.reset();

			INTERSECTIONS_MAPPING.forEach((element: IntersectionObserverEntry) => {
				(element as any).isIntersecting = false;
			});

			runIntersectionObserver(INTERSECTIONS_MAPPING);

			expect(resizeListener.register).not.toHaveBeenCalled();
			expect(resizeListener.unregister).not.toHaveBeenCalled();

			expect(resizeListener.fix).not.toHaveBeenCalled();

			expect(positionRegistry.register).not.toHaveBeenCalled();
			expect(positionRegistry.unregister).not.toHaveBeenCalled();

			expect(onComponentsAddedCallback).not.toHaveBeenCalled();
			expect(onComponentsRemovedCallback.calls.count()).toBe(1);
			expect(onComponentsRemovedCallback.calls.argsFor(0)[0]).toEqual([{
				component: parent,
				parent: undefined
			}, {
				component: component1,
				parent
			}]);
		});

	});

	describe('DOM mutations', () => {
		beforeEach(() => {
			resizeListener.fix.calls.reset();
			resizeListener.unregister.calls.reset();
			resizeListener.register.calls.reset();
			positionRegistry.unregister.calls.reset();
			positionRegistry.register.calls.reset();
			onComponentsAddedCallback.calls.reset();
		});

		it('should init the Mutation Observer and observe on body element with the expected configuration', () => {
			const expectedConfig = {
				attributes: true,
				attributeOldValue: true,
				childList: true,
				characterData: false,
				subtree: true
			};
			expect(mutationObserverMock.observe).toHaveBeenCalledWith(document.getElementsByTagName('body')[0], expectedConfig);
		});

		it('should be able to observe a page change and execute a registered page change callback', () => {
			// GIVEN
			const pageChangedCallback = jasmine.createSpy('callback');
			smartEditContractChangeListener.onPageChanged(pageChangedCallback);

			// WHEN
			const mutations = [{
				attributeName: 'class',
				type: 'attributes',
				target: {
					tagName: 'BODY'
				}
			}];
			mutationObserverCallback(mutations);
			$rootScope.$digest();

			// THEN
			expect(pageChangedCallback.calls.argsFor(0)[0]).toEqual(ANY_PAGE_UUID);

			mutationObserverCallback(mutations);

			expect(pageChangedCallback.calls.count()).toBe(1);
		});

		it('when a parent and a child are in the same operation (can occur), the child is NOT ignored but is process AFTER the parent', () => {

			// WHEN
			Array.prototype.push.apply(INTERSECTIONS_MAPPING, [{
				isIntersecting: true,
				target: component21 // child before parent component2
			}, {
				isIntersecting: true,
				target: component2
			}, {
				isIntersecting: true,
				target: component3
			}, {
				isIntersecting: true,
				target: detachedComponent
			}]);

			SECOND_LEVEL_CHILDREN = [component1, component2, component3, invisibleComponent];
			const mutations = [{
				type: 'childList',
				addedNodes: [component21, component2, invisibleComponent, component3]
			}];
			mutationObserverCallback(mutations);
			$rootScope.$digest();

			// THEN
			expect(onComponentsAddedCallback.calls.count()).toBe(3);
			expect(onComponentsAddedCallback.calls.argsFor(0)[0]).toEqual([component2]);
			expect(onComponentsAddedCallback.calls.argsFor(1)[0]).toEqual([component21]);
			expect(onComponentsAddedCallback.calls.argsFor(2)[0]).toEqual([component3]);
		});

		it('should be able to observe sub tree of smartEditComponent component added', () => {

			// WHEN
			Array.prototype.push.apply(INTERSECTIONS_MAPPING, [{
				isIntersecting: true,
				target: component2
			}, {
				isIntersecting: true,
				target: component21
			}, {
				isIntersecting: true,
				target: component3
			}]);

			SECOND_LEVEL_CHILDREN = [component1, component2, component3, invisibleComponent];
			const mutations = [{
				type: 'childList',
				addedNodes: [component2, component3, invisibleComponent]
			}];
			mutationObserverCallback(mutations);
			$rootScope.$digest();

			// THEN
			expect(resizeListener.unregister.calls.count()).toEqual(0);

			expect(resizeListener.register.calls.count()).toEqual(3);
			expect(resizeListener.register.calls.argsFor(0)[0]).toBe(component2);
			expect(resizeListener.register.calls.argsFor(1)[0]).toBe(component21);
			expect(resizeListener.register.calls.argsFor(2)[0]).toBe(component3);

			expect(resizeListener.fix.calls.count()).toBe(3);
			expect(resizeListener.fix.calls.argsFor(0)[0]).toBe(parent);
			expect(resizeListener.fix.calls.argsFor(1)[0]).toBe(component2);
			expect(resizeListener.fix.calls.argsFor(2)[0]).toBe(parent);

			expect(positionRegistry.register.calls.count()).toEqual(3);
			expect(positionRegistry.register.calls.argsFor(0)[0]).toBe(component2);
			expect(positionRegistry.register.calls.argsFor(1)[0]).toBe(component21);
			expect(positionRegistry.register.calls.argsFor(2)[0]).toBe(component3);

			expect(onComponentsAddedCallback.calls.count()).toBe(3);
			expect(onComponentsAddedCallback.calls.argsFor(0)[0]).toEqual([component2]);
			expect(onComponentsAddedCallback.calls.argsFor(1)[0]).toEqual([component21]);
			expect(onComponentsAddedCallback.calls.argsFor(2)[0]).toEqual([component3]);
		});

		it('should be able to observe sub tree of non smartEditComponent component added', () => {

			// WHEN
			Array.prototype.push.apply(INTERSECTIONS_MAPPING, [{
				isIntersecting: true,
				target: component2
			}, {
				isIntersecting: true,
				target: component21
			}, {
				isIntersecting: true,
				target: component3
			}]);

			SECOND_LEVEL_CHILDREN = [component1, component3, invisibleComponent];
			const mutations = [{
				type: 'childList',
				addedNodes: [component0, component3, invisibleComponent]
			}];
			mutationObserverCallback(mutations);
			$rootScope.$digest();

			// THEN

			expect(resizeListener.unregister.calls.count()).toEqual(0);

			expect(resizeListener.register.calls.count()).toEqual(3);
			expect(resizeListener.register.calls.argsFor(0)[0]).toBe(component2);
			expect(resizeListener.register.calls.argsFor(1)[0]).toBe(component21);
			expect(resizeListener.register.calls.argsFor(2)[0]).toBe(component3);

			expect(resizeListener.fix.calls.count()).toBe(3);
			expect(resizeListener.fix.calls.argsFor(0)[0]).toBe(parent);
			expect(resizeListener.fix.calls.argsFor(1)[0]).toBe(component2);
			expect(resizeListener.fix.calls.argsFor(2)[0]).toBe(parent);

			expect(positionRegistry.register.calls.count()).toEqual(3);
			expect(positionRegistry.register.calls.argsFor(0)[0]).toBe(component2);
			expect(positionRegistry.register.calls.argsFor(1)[0]).toBe(component21);
			expect(positionRegistry.register.calls.argsFor(2)[0]).toBe(component3);

			expect(onComponentsAddedCallback.calls.count()).toBe(3);
			expect(onComponentsAddedCallback.calls.argsFor(0)[0]).toEqual([component2]);
			expect(onComponentsAddedCallback.calls.argsFor(1)[0]).toEqual([component21]);
			expect(onComponentsAddedCallback.calls.argsFor(2)[0]).toEqual([component3]);
		});

		it('should be able to observe sub tree of smartEditComponent (and parent) removed', () => {

			(smartEditContractChangeListener as any).componentsQueue.push({
				component: component2,
				isIntersecting: true,
				processed: 'added',
				parent
			} as any);
			(smartEditContractChangeListener as any).componentsQueue.push({
				component: component21,
				isIntersecting: true,
				processed: 'added',
				parent: component2
			} as any);
			(smartEditContractChangeListener as any).componentsQueue.push({
				component: component3,
				isIntersecting: true,
				processed: 'added',
				parent
			} as any);

			// WHEN
			Array.prototype.push.apply(INTERSECTIONS_MAPPING, [{
				isIntersecting: false,
				target: component2
			}, {
				isIntersecting: false,
				target: component21
			}, {
				isIntersecting: false,
				target: component3
			}]);
			SECOND_LEVEL_CHILDREN = [component1, component2, component3];

			intersectionObserverCallback(INTERSECTIONS_MAPPING);
			$rootScope.$digest();

			// THEN
			expect(resizeListener.register).not.toHaveBeenCalled();
			expect(resizeListener.unregister).not.toHaveBeenCalled();
			expect(positionRegistry.unregister).not.toHaveBeenCalled();

			expect(onComponentsRemovedCallback.calls.count()).toBe(1);
			expect(onComponentsRemovedCallback.calls.argsFor(0)[0]).toEqualData([{
				component: component2,
				parent
			}, {
				component: component21,
				parent: component2
			}, {
				component: component3,
				parent
			}]);
		});

		it('should be able to stop all the listeners', () => {
			smartEditContractChangeListener.stopListener();

			expect(mutationObserverMock.disconnect).toHaveBeenCalled();
			expect(intersectionObserverMock.disconnect).toHaveBeenCalled();
			expect(resizeListener.dispose).toHaveBeenCalled();
			expect(positionRegistry.dispose).toHaveBeenCalled();
		});

		it('should call the componentRepositionedCallback when a component is repositioned after updating the registry', () => {
			positionRegistry.getRepositionedComponents.and.returnValue([component1]);

			$interval.flush(REPROCESS_TIMEOUT);

			expect(onComponentRepositionedCallback.calls.count()).toBe(1);
			expect(onComponentRepositionedCallback).toHaveBeenCalledWith(component1);
		});

		it('should cancel the repositionListener interval when calling stopListener', () => {
			positionRegistry.getRepositionedComponents.and.returnValue([]);

			const cancelSpy = spyOn($interval, 'cancel');

			smartEditContractChangeListener.stopListener();

			expect(cancelSpy.calls.count()).toBe(1);
		});

		it('should be able to observe a component change', () => {
			// WHEN
			const mutations = [{
				type: 'attributes',
				attributeName: UUID_ATTRIBUTE,
				target: component1,
				oldValue: 'random_uuid'
			}, {
				type: 'attributes',
				attributeName: ID_ATTRIBUTE,
				target: component1,
				oldValue: 'random_id'
			}];
			mutationObserverCallback(mutations);
			$rootScope.$digest();

			// THEN
			const expectedOldAttributes: TypedMap<string> = {};
			expectedOldAttributes[UUID_ATTRIBUTE] = 'random_uuid';
			expectedOldAttributes[ID_ATTRIBUTE] = 'random_id';
			expect(onComponentChangedCallback.calls.argsFor(0)[0]).toEqual(component1, expectedOldAttributes);
		});

		it('should be able to observe a component remove', () => {
			// GIVEN
			const mutations = [{
				type: 'childList',
				removedNodes: [component1],
				target: parent
			}];

			// WHEN
			mutationObserverCallback(mutations);
			$rootScope.$digest();

			// THEN
			expect(onComponentsRemovedCallback.calls.count()).toBe(1);
			expect(onComponentsRemovedCallback.calls.argsFor(0)[0]).toEqual([{
				component: component1,
				parent
			}]);
		});
	});
});

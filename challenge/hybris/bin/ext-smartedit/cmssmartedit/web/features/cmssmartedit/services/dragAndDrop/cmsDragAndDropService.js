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
(function() {

    /**
     * @ngdoc overview
     * @name cmsDragAndDropServiceModule
     * @description
     * # The cmsDragAndDropServiceModule
     *
     * The cmsDragAndDropServiceModule contains a service that provides a rich drag and drop experience tailored for CMS operations.
     *
     */
    angular.module('cmsDragAndDropServiceModule', ['yjqueryModule', 'dragAndDropServiceModule', 'yLoDashModule', 'smarteditCommonsModule', 'slotRestrictionsServiceModule', 'componentEditingFacadeModule', 'translationServiceModule', 'removeComponentServiceModule',
            'cmsSmarteditServicesModule', 'browserServiceModule', 'seConstantsModule'
        ])
        /**
         * @ngdoc object
         * @name cmsDragAndDropServiceModule.object:DRAG_AND_DROP_EVENTS
         *
         * @description
         * Injectable angular constant<br/>
         * Constants identifying CMS drag and drop events.
         *
         */
        .constant('DRAG_AND_DROP_EVENTS', {
            /**
             * @ngdoc property
             * @name DRAG_STARTED
             * @propertyOf cmsDragAndDropServiceModule.object:DRAG_AND_DROP_EVENTS
             *
             * @description
             * Name of event executed when a drag and drop event starts.
             **/
            DRAG_STARTED: 'CMS_DRAG_STARTED',
            /**
             * @ngdoc property
             * @name DRAG_STOPPED
             * @propertyOf cmsDragAndDropServiceModule.object:DRAG_AND_DROP_EVENTS
             *
             * @description
             * Name of event executed when a drag and drop event stops.
             **/
            DRAG_STOPPED: 'CMS_DRAG_STOPPED',
            /**
             * @ngdoc property
             * @name DRAG_STOPPED
             * @propertyOf cmsDragAndDropServiceModule.object:DRAG_AND_DROP_EVENTS
             *
             * @description
             * Name of event executed when onDragOver is triggered.
             **/
            DRAG_OVER: 'CMS_DRAG_OVER',
            /**
             * @ngdoc property
             * @name DRAG_STOPPED
             * @propertyOf cmsDragAndDropServiceModule.object:DRAG_AND_DROP_EVENTS
             *
             * @description
             * Name of event executed when onDragLeave is triggered.
             **/
            DRAG_LEAVE: 'CMS_DRAG_LEAVE'
        })
        /**
         * @ngdoc service
         * @name cmsDragAndDropServiceModule.service:cmsDragAndDropService
         *
         * @description
         * This service provides a rich drag and drop experience tailored for CMS operations.
         */
        .service('cmsDragAndDropService', function($q, $window, $translate, $timeout, yjQuery, lodash, dragAndDropService, componentHandlerService, systemEventService, gatewayFactory, slotRestrictionsService, alertService, assetsService, browserService, componentEditingFacade, waitDialogService, DRAG_AND_DROP_EVENTS, COMPONENT_REMOVED_EVENT, CONTENT_SLOT_TYPE, OVERLAY_RERENDERED_EVENT, CONTRACT_CHANGE_LISTENER_PROCESS_EVENTS, SMARTEDIT_COMPONENT_PROCESS_STATUS, CONTRACT_CHANGE_LISTENER_COMPONENT_PROCESS_STATUS) {
            // Constants
            var CMS_DRAG_AND_DROP_ID = 'se.cms.dragAndDrop';

            var TARGET_SELECTOR = "#smarteditoverlay .smartEditComponentX[data-smartedit-component-type='ContentSlot']";
            var SOURCE_SELECTOR = "#smarteditoverlay .smartEditComponentX[data-smartedit-component-type!='ContentSlot'] .movebutton";
            var MORE_MENU_SOURCE_SELECTOR = ".movebutton";

            var SLOT_SELECTOR = ".smartEditComponentX[data-smartedit-component-type='ContentSlot']";
            var COMPONENT_SELECTOR = ".smartEditComponentX[data-smartedit-component-type!='ContentSlot']";
            var HINT_SELECTOR = '.overlayDropzone';

            var CSS_CLASSES = {
                UI_HELPER_OVERLAY: 'overlayDnd',
                DROPZONE: 'overlayDropzone',
                DROPZONE_FULL: 'overlayDropzone--full',
                DROPZONE_TOP: 'overlayDropzone--top',
                DROPZONE_BOTTOM: 'overlayDropzone--bottom',
                DROPZONE_LEFT: 'overlayDropzone--left',
                DROPZONE_RIGHT: 'overlayDropzone--right',
                DROPZONE_HOVERED: 'overlayDropzone--hovered',
                OVERLAY_IN_DRAG_DROP: 'smarteditoverlay_dndRendering',
                COMPONENT_DRAGGED: 'component_dragged',
                COMPONENT_DRAGGED_HOVERED: 'component_dragged_hovered',
                SLOTS_MARKED: 'slot-marked',
                SLOT_ALLOWED: 'over-slot-enabled',
                SLOT_NOT_ALLOWED: 'over-slot-disabled'
            };

            var DEFAULT_DRAG_IMG = '/images/contextualmenu_move_on.png';

            // Variables
            this._cachedSlots = {};
            this._highlightedSlot = null;
            this._highlightedComponent = null;
            this._highlightedHint = null;
            this._dragInfo = null;

            this._overlayRenderedUnSubscribeFn = null;
            this._componentRemovedUnSubscribeFn = null;

            this._gateway = gatewayFactory.createGateway("cmsDragAndDrop");

            /**
             * @ngdoc method
             * @name cmsDragAndDropServiceModule.service:cmsDragAndDropService#register
             * @methodOf cmsDragAndDropServiceModule.service:cmsDragAndDropService
             *
             * @description
             * This method registers this drag and drop instance in SmartEdit.
             */
            this.register = function() {
                dragAndDropService.register({
                    id: CMS_DRAG_AND_DROP_ID,
                    sourceSelector: [SOURCE_SELECTOR, MORE_MENU_SOURCE_SELECTOR], //the source selectors are DnD menus located both inside and outside the more options of the overlay
                    targetSelector: TARGET_SELECTOR,
                    startCallback: this.onStart,
                    dragEnterCallback: this.onDragEnter,
                    dragOverCallback: this.onDragOver,
                    dropCallback: this.onDrop,
                    outCallback: this.onDragLeave,
                    stopCallback: this.onStop,
                    enableScrolling: true,
                    helper: this._getDragImageSrc
                });
            };

            /**
             * @ngdoc method
             * @name cmsDragAndDropServiceModule.service:cmsDragAndDropService#unregister
             * @methodOf cmsDragAndDropServiceModule.service:cmsDragAndDropService
             *
             * @description
             * This method unregisters this drag and drop instance from SmartEdit.
             */
            this.unregister = function() {
                dragAndDropService.unregister([CMS_DRAG_AND_DROP_ID]);
                slotRestrictionsService.emptyCache();

                if (this._overlayRenderedUnSubscribeFn) {
                    this._overlayRenderedUnSubscribeFn();
                }
                if (this._componentRemovedUnSubscribeFn) {
                    this._componentRemovedUnSubscribeFn();
                }
            };

            /**
             * @ngdoc method
             * @name cmsDragAndDropServiceModule.service:cmsDragAndDropService#apply
             * @methodOf cmsDragAndDropServiceModule.service:cmsDragAndDropService
             *
             * @description
             * This method applies this drag and drop instance in the current page. After this method is executed,
             * the user can start a drag and drop operation.
             */
            this.apply = function() {
                dragAndDropService.apply(CMS_DRAG_AND_DROP_ID);
                this._addUIHelpers();

                // Register a listener for every time the overlay is updated.
                this._overlayRenderedUnSubscribeFn = systemEventService.subscribe(OVERLAY_RERENDERED_EVENT, this._onOverlayUpdate);
                this._componentRemovedUnSubscribeFn = systemEventService.subscribe(COMPONENT_REMOVED_EVENT, this._onOverlayUpdate);

                this._gateway.subscribe(DRAG_AND_DROP_EVENTS.DRAG_STARTED, function(eventId, data) {
                    dragAndDropService.markDragStarted();
                    this._initializeDragOperation(data);
                }.bind(this));

                this._gateway.subscribe(DRAG_AND_DROP_EVENTS.DRAG_STOPPED, function() {
                    dragAndDropService.markDragStopped();
                    this._cleanDragOperation();
                }.bind(this));
            };

            /**
             * @ngdoc method
             * @name cmsDragAndDropServiceModule.service:cmsDragAndDropService#update
             * @methodOf cmsDragAndDropServiceModule.service:cmsDragAndDropService
             *
             * @description
             * This method updates this drag and drop instance in the current page. It is important to execute
             * this method every time a draggable or droppable element is added or removed from the page DOM.
             */
            this.update = function() {
                dragAndDropService.update(CMS_DRAG_AND_DROP_ID);

                // Add UI helpers -> They identify the places where you can drop components.
                this._addUIHelpers();

                // Update cache elements AFTER adding UI Helpers
                this._cacheElements();
            };

            // Other Event Handlers
            this._onOverlayUpdate = function() {
                this.update();
                return $q.when();
            }.bind(this);

            // Drag and Drop Event Handlers
            this.onStart = function(event) {
                // Find element
                var targetElm = this._getSelector(event.target);
                // when the DnD icon is in the more option dropdown, the targetElm is a span and has no data-component-id. Here we get the closest element (i.e. <contextual-menu-item>)
                if (!targetElm.attr('data-component-id')) {
                    targetElm = yjQuery(targetElm).closest('[data-component-id]');
                }
                var component = targetElm.closest(COMPONENT_SELECTOR);
                var slot = component.closest(SLOT_SELECTOR);

                // Here if the component evaluated above exits that means the component has been located and we can fetch its attributes 
                // else it is not located as the DnD option is hidden inside the more option of the contextual menu in which case 
                // we find the component/slot info by accessing attributes of the DnD icon.
                var componentId = component.length > 0 ? componentHandlerService.getId(component) : targetElm.attr('data-component-id');
                var componentUuid = component.length > 0 ? componentHandlerService.getSlotOperationRelatedUuid(component) : targetElm.attr('data-component-uuid');
                var componentType = component.length > 0 ? componentHandlerService.getType(component) : targetElm.attr('data-component-type');
                var slotOperationRelatedId = component.length > 0 ? componentHandlerService.getSlotOperationRelatedId(component) : targetElm.attr('data-component-id');
                var slotOperationRelatedType = component.length > 0 ? componentHandlerService.getSlotOperationRelatedType(component) : targetElm.attr('data-component-type');

                var slotId = component.length > 0 ? componentHandlerService.getId(slot) : targetElm.attr('data-slot-id');
                var slotUuid = component.length > 0 ? componentHandlerService.getId(slot) : targetElm.attr('data-slot-uuid');

                var dragInfo = {
                    componentId: componentId,
                    componentUuid: componentUuid,
                    componentType: componentType,
                    slotUuid: slotUuid,
                    slotId: slotId,
                    slotOperationRelatedId: slotOperationRelatedId,
                    slotOperationRelatedType: slotOperationRelatedType
                };
                component.addClass(CSS_CLASSES.COMPONENT_DRAGGED);
                this._initializeDragOperation(dragInfo);
                this._toggleKeepVisibleComponentAndSlot(true);
            }.bind(this);

            this.onDragEnter = function(event) {
                this._highlightSlot(event);
            }.bind(this);

            this.onDragOver = function(event) {
                this._highlightSlot(event).then(function() {
                    if (!this._highlightedSlot || !this._highlightedSlot.isAllowed) {
                        return;
                    }

                    var slotId = componentHandlerService.getId(this._highlightedSlot.original);

                    // Check which component is highlighted
                    if (this._highlightedHint && this._isMouseInRegion(event, this._highlightedHint)) {
                        // If right hint is already highlighted don't do anything.
                        return;
                    } else if (this._highlightedHint) {
                        // Hint is not longer hovered.
                        this._clearHighlightedHint();
                    }

                    var cachedSlot = this._cachedSlots[slotId];
                    if (cachedSlot.components.length > 0) {
                        // Find the hovered component.
                        if (!this._highlightedComponent || !this._isMouseInRegion(event, this._highlightedComponent)) {
                            this._clearHighlightedComponent();

                            lodash.forEach(cachedSlot.components, function(component) {
                                if (this._isMouseInRegion(event, component)) {
                                    this._highlightedComponent = component;
                                    return false;
                                }
                            }.bind(this));
                        }

                        // Find the hint, if any, to highlight.
                        if (this._highlightedComponent) {
                            lodash.forEach(this._highlightedComponent.hints, function(hint) {
                                if (this._isMouseInRegion(event, hint)) {
                                    this._highlightedHint = hint;
                                    return false;
                                }
                            }.bind(this));
                        }
                    }

                    if (this._highlightedComponent && this._highlightedComponent.id === this._dragInfo.slotOperationRelatedId) {
                        this._highlightedComponent.original.addClass(CSS_CLASSES.COMPONENT_DRAGGED_HOVERED);
                    } else if (this._highlightedHint) {
                        this._highlightedHint.original.addClass(CSS_CLASSES.DROPZONE_HOVERED);
                    }

                }.bind(this));

            }.bind(this);

            this.onDrop = function(event) {
                if (this._highlightedSlot) {
                    var sourceSlotId = this._dragInfo.slotId;
                    var targetSlotId = componentHandlerService.getId(this._highlightedSlot.original);
                    var targetSlotUUId = componentHandlerService.getUuid(this._highlightedSlot.original);
                    var sourceComponentId = this._dragInfo.componentId;
                    // if component is dragged from component-menu, there is no slotOperationRelated(Id/Type) available.
                    var sourceSlotOperationRelatedId = this._dragInfo.slotOperationRelatedId || this._dragInfo.componentId;
                    var componentType = this._dragInfo.slotOperationRelatedType || this._dragInfo.componentType;

                    if (!this._highlightedSlot.isAllowed) {
                        var translation = $translate.instant("se.drag.and.drop.not.valid.component.type", {
                            slotUID: targetSlotId,
                            componentUID: sourceSlotOperationRelatedId
                        });
                        alertService.showDanger({
                            message: translation
                        });
                        return;
                    }
                    if (this._highlightedHint || this._highlightedSlot.components.length === 0) {
                        var position = (this._highlightedHint) ? this._highlightedHint.position : 0;
                        var result;

                        waitDialogService.showWaitModal();

                        if (!sourceSlotId) {
                            if (!sourceComponentId) {
                                var slotInfo = {
                                    targetSlotId: targetSlotId,
                                    targetSlotUUId: targetSlotUUId
                                };
                                var catalogVersionUuid = componentHandlerService.getCatalogVersionUuid(this._highlightedSlot.original);
                                result = componentEditingFacade.addNewComponentToSlot(slotInfo, catalogVersionUuid, componentType, position);
                            } else {

                                var dragInfo = {
                                    componentId: sourceComponentId,
                                    componentUuid: this._dragInfo.componentUuid,
                                    componentType: componentType
                                };
                                var componentProperties = {
                                    targetSlotId: targetSlotId,
                                    dragInfo: dragInfo,
                                    position: position
                                };

                                result = this._dragInfo.cloneOnDrop ? componentEditingFacade.cloneExistingComponentToSlot(componentProperties) : componentEditingFacade.addExistingComponentToSlot(targetSlotId, dragInfo, position);
                            }
                        } else {
                            if (sourceSlotId === targetSlotId) {
                                var currentComponentPos = componentHandlerService.getComponentPositionInSlot(sourceSlotId, sourceComponentId);
                                if (currentComponentPos < position) {
                                    // The current component will be removed from its current position, thus the target
                                    // position needs to take this into account. 
                                    position--;
                                }

                            }
                            result = componentEditingFacade.moveComponent(sourceSlotId, targetSlotId, sourceSlotOperationRelatedId, position);
                        }

                        result.then(function() {
                            this._scrollToModifiedSlot(targetSlotId);
                        }.bind(this), function() {
                            this.onStop(event);
                        }.bind(this)).finally(function() {
                            waitDialogService.hideWaitModal();
                        });
                    }
                }
            }.bind(this);

            this.onDragLeave = function(event) {
                if (this._highlightedSlot) {
                    var slotId = componentHandlerService.getId(this._highlightedSlot.original);
                    var cachedSlot = this._cachedSlots[slotId];

                    if (!this._isMouseInRegion(event, cachedSlot)) {
                        this._clearHighlightedSlot();
                    }

                }
            }.bind(this);

            this.onStop = function(event) {
                var component = this._getSelector(event.target).closest(COMPONENT_SELECTOR);
                this._toggleKeepVisibleComponentAndSlot(false);
                this._cleanDragOperation(component);
                systemEventService.publish(CONTRACT_CHANGE_LISTENER_PROCESS_EVENTS.RESTART_PROCESS);
            }.bind(this);

            // Helpers
            /**
             * This function returns the source of the image used as drag image. Currently, the 
             * image is only returned for Safari; all the other browsers display default images 
             * properly. 
             */
            this._getDragImageSrc = function() {
                var imagePath = '';
                if (browserService.isSafari()) {
                    imagePath = assetsService.getAssetsRoot() + DEFAULT_DRAG_IMG;
                }

                return imagePath;
            };


            this._initializeDragOperation = function(dragInfo) {
                this._dragInfo = dragInfo;
                this._cacheElements();

                // Prepare UI
                var overlay = componentHandlerService.getOverlay();
                overlay.addClass(CSS_CLASSES.OVERLAY_IN_DRAG_DROP);

                // Send an event to signal that the drag operation is started. Other pieces of SE, like contextual menus
                // need to be aware.
                systemEventService.publishAsync(DRAG_AND_DROP_EVENTS.DRAG_STARTED);
            };

            this._cleanDragOperation = function(draggedComponent) {
                this._clearHighlightedSlot();
                if (draggedComponent) {
                    draggedComponent.removeClass(CSS_CLASSES.COMPONENT_DRAGGED);
                }

                var overlay = componentHandlerService.getOverlay();
                overlay.removeClass(CSS_CLASSES.OVERLAY_IN_DRAG_DROP);
                systemEventService.publishAsync(DRAG_AND_DROP_EVENTS.DRAG_STOPPED);

                this._dragInfo = null;
                this._cachedSlots = {};
                this._highlightedSlot = null;
            };

            this._highlightSlot = function(event) {
                var slot = yjQuery(event.target).closest(SLOT_SELECTOR);
                var slotId = componentHandlerService.getId(slot);

                var oldSlotId;
                if (this._highlightedSlot) {
                    oldSlotId = componentHandlerService.getId(this._highlightedSlot.original);

                    if (oldSlotId !== slotId) {
                        this._clearHighlightedSlot();
                    }
                }

                if (!this._highlightedSlot || this._highlightedSlot.isAllowed === undefined) {
                    this._highlightedSlot = this._cachedSlots[slotId];

                    var _dragInfo = lodash.cloneDeep(this._dragInfo);
                    // if component is dragged from component-menu, there is no slotOperationRelated(Id/Type) available.
                    _dragInfo.componentId = this._dragInfo.slotOperationRelatedId || this._dragInfo.componentId;
                    _dragInfo.componentType = this._dragInfo.slotOperationRelatedType || this._dragInfo.componentType;
                    if (_dragInfo.cloneOnDrop) {
                        delete _dragInfo.componentId;
                    }

                    return slotRestrictionsService.isComponentAllowedInSlot(this._highlightedSlot, _dragInfo).then(function(componentIsAllowed) {
                        slotRestrictionsService.isSlotEditable(slotId).then(function(slotIsEditable) {
                            // The highlighted slot might have changed while waiting for the promise to be resolved.
                            if (this._highlightedSlot && this._highlightedSlot.id === slotId) {
                                this._highlightedSlot.isAllowed = (componentIsAllowed && slotIsEditable) || this._dragInfo.slotId === slotId;

                                if (this._highlightedSlot.isAllowed) {
                                    slot.addClass(CSS_CLASSES.SLOT_ALLOWED);
                                } else {
                                    slot.addClass(CSS_CLASSES.SLOT_NOT_ALLOWED);
                                }

                                if (event.type === "dragenter" && (!oldSlotId || oldSlotId !== slotId)) {
                                    if (this._highlightedSlot && this._highlightedSlot.id === slotId) {
                                        systemEventService.publish(slotId + '_SHOW_SLOT_MENU', slotId);
                                        systemEventService.publish(DRAG_AND_DROP_EVENTS.DRAG_OVER, slotId); // can be used to perform any actions on encountering a drag over event.
                                    }
                                }
                            }
                        }.bind(this));
                    }.bind(this));
                }

                return $q.when();
            };

            this._addUIHelpers = function() {
                var overlay = componentHandlerService.getOverlay();

                // First remove all dropzones.
                overlay.find('.' + CSS_CLASSES.UI_HELPER_OVERLAY).remove();

                overlay.find(SLOT_SELECTOR).each(function() {
                    var slot = yjQuery(this);
                    var slotHeight = slot[0].offsetHeight;
                    var slotWidth = slot[0].offsetWidth;

                    // Make a call to the restrictions service to cache the call if necessary.
                    // NOTE: This call only checks for the slot's restrictions, which don't change
                    // too often. So it makes sense to cache them.
                    var slotId = componentHandlerService.getId(slot);

                    slotRestrictionsService.getSlotRestrictions(slotId);

                    var components = slot.find(COMPONENT_SELECTOR);

                    if (components.length === 0) {
                        var uiHelperOverlay = yjQuery("<div></div>");
                        uiHelperOverlay.addClass(CSS_CLASSES.UI_HELPER_OVERLAY);

                        var uiHelper = yjQuery("<div></div>");
                        uiHelper.addClass(CSS_CLASSES.DROPZONE);
                        uiHelper.addClass(CSS_CLASSES.DROPZONE_FULL);

                        uiHelperOverlay.height(slotHeight);
                        uiHelperOverlay.width(slotWidth);

                        uiHelperOverlay.append(uiHelper);
                        slot.append(uiHelperOverlay);
                    } else {
                        components.each(function() {
                            var component = yjQuery(this);
                            var componentHeight = component[0].offsetHeight;
                            var componentWidth = component[0].offsetWidth;

                            var uiHelperOverlay = yjQuery("<div></div>");
                            uiHelperOverlay.addClass(CSS_CLASSES.UI_HELPER_OVERLAY);

                            uiHelperOverlay.height(componentHeight);
                            uiHelperOverlay.width(componentWidth);

                            var firstHelper = yjQuery('<div></div>');
                            var secondHelper = yjQuery('<div></div>');

                            firstHelper.addClass(CSS_CLASSES.DROPZONE);
                            secondHelper.addClass(CSS_CLASSES.DROPZONE);

                            if (componentWidth === slotWidth) {
                                firstHelper.addClass(CSS_CLASSES.DROPZONE_TOP);
                                secondHelper.addClass(CSS_CLASSES.DROPZONE_BOTTOM);
                            } else {
                                firstHelper.addClass(CSS_CLASSES.DROPZONE_LEFT);
                                secondHelper.addClass(CSS_CLASSES.DROPZONE_RIGHT);
                            }

                            uiHelperOverlay.append(firstHelper);
                            uiHelperOverlay.append(secondHelper);

                            component.append(uiHelperOverlay);
                        });
                    }
                });
            };

            this._cacheElements = function() {
                var overlay = componentHandlerService.getOverlay();
                if (!overlay) {
                    return;
                }

                var currentService = this;
                var scrollY = this._getWindowScrolling();

                overlay.find(SLOT_SELECTOR).each(function() {
                    var slot = yjQuery(this);
                    var slotId = componentHandlerService.getId(slot);
                    var slotUuid = componentHandlerService.getUuid(slot);

                    var cachedSlot = {
                        id: slotId,
                        uuid: slotUuid,
                        original: slot,
                        components: [],
                        rect: currentService._getElementRects(slot, scrollY)
                    };

                    var components = slot.children(COMPONENT_SELECTOR);
                    if (components.length === 0) {
                        var hint = slot.find(HINT_SELECTOR);
                        cachedSlot.hint = (hint.length > 0) ? {
                            original: hint,
                            rect: currentService._getElementRects(hint, scrollY)
                        } : null;
                    } else {

                        components.each(function() {
                            var component = yjQuery(this);
                            var componentId = componentHandlerService.getId(component);
                            var positionInSlot = componentHandlerService.getComponentPositionInSlot(slotId, componentId);
                            var cachedComponent = {
                                id: componentHandlerService.getSlotOperationRelatedId(component),
                                type: componentHandlerService.getSlotOperationRelatedType(component),
                                original: component,
                                position: positionInSlot,
                                hints: [],
                                rect: currentService._getElementRects(component, scrollY)
                            };

                            var positionInComponent = positionInSlot++;
                            component.find(HINT_SELECTOR).each(function() {
                                var hint = yjQuery(this);
                                var cachedHint = {
                                    original: hint,
                                    position: positionInComponent++,
                                    rect: currentService._getElementRects(hint, scrollY)
                                };

                                cachedComponent.hints.push(cachedHint);
                            });

                            cachedSlot.components.push(cachedComponent);
                        });
                    }

                    currentService._cachedSlots[cachedSlot.id] = cachedSlot;
                });
            };

            this._clearHighlightedHint = function() {
                if (this._highlightedHint) {
                    this._highlightedHint.original.removeClass(CSS_CLASSES.DROPZONE_HOVERED);
                    this._highlightedHint = null;
                }
            };

            this._clearHighlightedComponent = function() {
                this._clearHighlightedHint();
                if (this._highlightedComponent) {
                    this._highlightedComponent.original.removeClass(CSS_CLASSES.COMPONENT_DRAGGED_HOVERED);
                    this._highlightedComponent = null;
                }
            };

            this._clearHighlightedSlot = function() {
                this._clearHighlightedComponent();

                if (this._highlightedSlot) {
                    this._highlightedSlot.original.removeClass(CSS_CLASSES.SLOT_ALLOWED);
                    this._highlightedSlot.original.removeClass(CSS_CLASSES.SLOT_NOT_ALLOWED);

                    systemEventService.publish('HIDE_SLOT_MENU');
                    systemEventService.publish(DRAG_AND_DROP_EVENTS.DRAG_LEAVE); // can be used to perform any actions on encountering a drag leave event.
                }

                this._highlightedSlot = null;
            };

            this._isMouseInRegion = function(event, element) {
                var boundingRect = element.rect;

                return (event.pageX >= boundingRect.left && event.pageX <= boundingRect.right && event.pageY >= boundingRect.top && event.pageY <= boundingRect.bottom);
            };

            this._getElementRects = function(element, scrollY) {
                var baseRect = element[0].getBoundingClientRect();
                var rect = {
                    left: baseRect.left,
                    right: baseRect.right,
                    bottom: baseRect.bottom + scrollY,
                    top: baseRect.top + scrollY
                };

                return rect;
            };

            this._getWindowScrolling = function() {
                return ($window.scrollY || $window.pageYOffset);
            };

            this._scrollToModifiedSlot = function(componentId) {
                var component = componentHandlerService.getComponentInOverlay(componentId, CONTENT_SLOT_TYPE);
                if (component && component.length > 0) {
                    component[0].scrollIntoView();
                }
            };

            this._getSelector = function(selector) {
                return yjQuery(selector);
            };

            /**
             * when a PROCESS_COMPONENTS is occuring, it could remove the currently dragged component if this one is not in the viewport.
             * To avoid having the dragged component and it's slot removed we mark then as "KEEP_VISIBLE" when the drag and drop start.
             * On drag end, an event is sent to call a RESTART_PROCESS to add or remove the components according to their viewport visibility and the component and slot are marked as "PROCESS".
             * Using yjQuery.each() here because of MiniCart component (among other slots/compoents) that have multiple occurences in DOM.
             */
            this._toggleKeepVisibleComponentAndSlot = function(keepVisible) {
                if (this._dragInfo) {
                    var status = keepVisible ? CONTRACT_CHANGE_LISTENER_COMPONENT_PROCESS_STATUS.KEEP_VISIBLE : CONTRACT_CHANGE_LISTENER_COMPONENT_PROCESS_STATUS.PROCESS;
                    yjQuery.each(componentHandlerService.getComponentUnderSlot(this._dragInfo.componentId, this._dragInfo.componentType, this._dragInfo.slotId), function(idx, element) {
                        element.dataset[SMARTEDIT_COMPONENT_PROCESS_STATUS] = status;
                    });
                    yjQuery.each(componentHandlerService.getComponent(this._dragInfo.slotId, CONTENT_SLOT_TYPE), function(idx, element) {
                        element.dataset[SMARTEDIT_COMPONENT_PROCESS_STATUS] = status;
                    });
                }
            };
        });
})();

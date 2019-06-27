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
/**
 * @ngdoc overview
 * @name renderServiceModule
 * @description
 * This module provides the renderService, which is responsible for rendering the SmartEdit overlays used for providing
 * CMS functionality to the storefront within the context of SmartEdit.
 */
angular.module('renderServiceModule', [
        'alertServiceModule',
        'functionsModule',
        'renderServiceInterfaceModule',
        'seConstantsModule',
        'yLoDashModule',
        'smarteditServicesModule'
    ])

    /**
     * @ngdoc service
     * @name renderServiceModule.renderService
     * @description
     * The renderService is responsible for rendering and resizing component overlays, and re-rendering components and slots
     * from the storefront.
     */
    .service('renderService', function(
        $compile,
        $http,
        $location,
        $log,
        $q,
        $rootScope,
        $timeout,
        $window,
        alertService,
        CATALOG_VERSION_UUID_ATTRIBUTE,
        COMPONENT_CLASS,
        componentHandlerService,
        CONTAINER_ID_ATTRIBUTE,
        CONTRACT_CHANGE_LISTENER_COMPONENT_PROCESS_STATUS,
        CONTRACT_CHANGE_LISTENER_PROCESS_EVENTS,
        crossFrameEventService,
        ELEMENT_UUID_ATTRIBUTE,
        EVENT_PERSPECTIVE_CHANGED,
        extractFromElement,
        gatewayFactory,
        gatewayProxy,
        generateIdentifier,
        getAbsoluteURL,
        ID_ATTRIBUTE,
        isBlank,
        lodash,
        OVERLAY_COMPONENT_CLASS,
        OVERLAY_ID,
        OVERLAY_RERENDERED_EVENT,
        pageInfoService,
        perspectiveService,
        experienceService,
        RenderServiceInterface,
        seNamespaceService,
        sakExecutorService,
        sharedDataService,
        SMARTEDIT_ATTRIBUTE_PREFIX,
        SMARTEDIT_COMPONENT_PROCESS_STATUS,
        systemEventService,
        TYPE_ATTRIBUTE,
        unsafeParseHTML,
        UUID_ATTRIBUTE,
        EVENT_PERSPECTIVE_REFRESHED) {

        angular.extend(this, RenderServiceInterface.prototype);
        RenderServiceInterface.call(this);

        this.gatewayId = "Renderer";
        this._slotOriginalHeights = {};
        this._smartEditBootstrapGateway = gatewayFactory.createGateway('smartEditBootstrap');

        /**
         * @ngdoc function
         * @name renderServiceModule.renderService.toggleOverlay
         * @methodOf renderServiceModule.renderService
         * @description
         * Toggles the visibility of the overlay using CSS.
         *
         * @param {Boolean} isVisible Flag to show/hide the overlay.
         */
        this.toggleOverlay = function(isVisible) {
            var overlay = componentHandlerService.getOverlay();
            overlay.css('visibility', (isVisible ? 'visible' : 'hidden'));
        };

        /**
         * @ngdoc function
         * @name renderServiceModule.renderService.refreshOverlayDimensions
         * @methodOf renderServiceModule.renderService
         * @description
         * Refreshes the dimensions and positions of the SmartEdit overlays. The overlays need to remain in synced with the
         * dynamic resizing of their original elements. In particular, this method is bound to the window resizing event
         * to refresh overlay dimensions for responsive storefronts.
         *
         * The implementation itself will search for children SmartEdit components from the root element provided. If no root
         * element is provided, the method will default to using the body element. The overlay specific to this component
         * is then fetched and resized, according to the dimensions of the component.
         *
         * @param {Element} element The root element from which to traverse and discover SmartEdit components.
         */
        this.refreshOverlayDimensions = function(element) {
            element = element || componentHandlerService.getFromSelector('body');
            var children = componentHandlerService.getFirstSmartEditComponentChildren(element);

            children.each(function(index, childElement) {
                var wrappedChild = componentHandlerService.getFromSelector(childElement);
                this._updateComponentSizeAndPosition(wrappedChild);
                this.refreshOverlayDimensions(wrappedChild);
            }.bind(this));
        };

        /*
         * Updates the dimensions of the overlay component element given the original component element and the overlay component itself.
         * If no overlay component is provided, it will be fetched through {@link componentHandlerService.getOverlayComponent}

         * The overlay component is resized to be the same dimensions of the component for which it overlays, and positioned absolutely
         * on the page. Additionally, it is provided with a minimum height and width. The resizing takes into account both
         * the size of the component element, and the position based on iframe scrolling.
         *
         * @param {Element} componentElem The original CMS component element from the storefront.
         * @param {Element=} componentOverlayElem The overlay component. If none is provided
         */
        this._updateComponentSizeAndPosition = function(componentElem, componentOverlayElem) {
            componentElem = componentHandlerService.getFromSelector(componentElem);
            componentOverlayElem = componentOverlayElem ||
                componentHandlerService.getComponentCloneInOverlay(componentElem).get(0);

            if (componentOverlayElem) {
                var parentPos = this._getParentInOverlay(componentElem).get(0).getBoundingClientRect();

                var innerWidth = componentElem.get(0).offsetWidth;
                var innerHeight = componentElem.get(0).offsetHeight;

                // Update the position based on the IFrame Scrolling
                var pos = componentElem.get(0).getBoundingClientRect();
                var elementTopPos = pos.top - parentPos.top;
                var elementLeftPos = pos.left - parentPos.left;

                componentOverlayElem.style.position = "absolute";
                componentOverlayElem.style.top = elementTopPos + "px";
                componentOverlayElem.style.left = elementLeftPos + "px";
                componentOverlayElem.style.width = innerWidth + "px";
                componentOverlayElem.style.height = innerHeight + "px";
                componentOverlayElem.style.minWidth = "51px";
                componentOverlayElem.style.minHeight = "48px";

                var shallowCopy = componentHandlerService
                    .getFromSelector(componentOverlayElem)
                    .find('[id="' + this._buildShallowCloneId(componentElem.attr(ID_ATTRIBUTE), componentElem.attr(TYPE_ATTRIBUTE), componentElem.attr(CONTAINER_ID_ATTRIBUTE)) + '"]');
                shallowCopy.width(innerWidth);
                shallowCopy.height(innerHeight);
                shallowCopy.css('min-height', 49);
                shallowCopy.css('min-width', 51);
            }
        };

        this._getParentInOverlay = function(element) {
            var parent = componentHandlerService.getParent(element);
            if (parent.length) {
                return componentHandlerService.getOverlayComponent(parent);
            } else {
                return componentHandlerService.getOverlay();
            }
        };

        /*
         * Given a smartEdit component in the storefront layer. An empty clone of it will be created, sized and positioned in the smartEdit overlay
         * then compiled with all eligible decorators for the given perspective (see {@link smarteditServicesModule.interface:IPerspectiveService perspectiveService})
         * @param {Element} element The original CMS component element from the storefront.
         */
        this._createComponent = function(element) {
            if (componentHandlerService.isOverlayOn() && this._isComponentVisible(element)) {
                this._cloneAndCompileComponent(element);
            }
        };

        this._buildShallowCloneId = function(smarteditComponentId, smarteditComponentType, smarteditContainerId) {
            var containerSection = (smarteditContainerId) ? '_' + smarteditContainerId : '';
            return smarteditComponentId + "_" + smarteditComponentType + containerSection + "_overlay";
        };

        this._cloneAndCompileComponent = function(element) {
            if (componentHandlerService.getFromSelector(element).is(":visible")) {
                element = componentHandlerService.getFromSelector(element);
                var parentOverlay = this._getParentInOverlay(element);

                if (!parentOverlay.length) {
                    $log.error('renderService: parentOverlay empty for component:', element.attr(ID_ATTRIBUTE));
                    return;
                }
                if (!validateComponentAttributesContract(element)) {
                    return;
                }

                // FIXME: CMSX-6139: use dataset instead of attr(): ELEMENT_UUID_ATTRIBUTE value should not be exposed.
                var elementUUID = generateIdentifier();
                element.attr(ELEMENT_UUID_ATTRIBUTE, elementUUID);

                var smarteditComponentId = element.attr(ID_ATTRIBUTE);
                var smarteditComponentType = element.attr(TYPE_ATTRIBUTE);
                var smarteditContainerId = element.attr(CONTAINER_ID_ATTRIBUTE);

                var shallowCopy = this._getDocument().createElement("div");
                shallowCopy.id = this._buildShallowCloneId(smarteditComponentId, smarteditComponentType, smarteditContainerId);

                var smartEditWrapper = this._getDocument().createElement("smartedit-element");
                var componentDecorator = componentHandlerService.getFromSelector(smartEditWrapper);
                componentDecorator.append(shallowCopy);

                this._updateComponentSizeAndPosition(element, smartEditWrapper);

                if (smarteditComponentType === "NavigationBarCollectionComponent") {
                    // Make sure the Navigation Bar is on top of the navigation items
                    smartEditWrapper.style.zIndex = "7";
                }

                componentDecorator.addClass(OVERLAY_COMPONENT_CLASS);
                Array.prototype.slice.apply(element.get(0).attributes).forEach(function(node) {
                    if (node.nodeName.indexOf(SMARTEDIT_ATTRIBUTE_PREFIX) === 0) {
                        componentDecorator.attr(node.nodeName, node.nodeValue);
                    }
                });

                parentOverlay.append(this._compile(smartEditWrapper, $rootScope));
            }
        };

        function validateComponentAttributesContract(element) {
            var requiredAttributes = [ID_ATTRIBUTE, UUID_ATTRIBUTE, TYPE_ATTRIBUTE, CATALOG_VERSION_UUID_ATTRIBUTE];
            var valid = true;
            requiredAttributes.forEach(function(reqAttribute) {
                if (!element || !element.attr(reqAttribute)) {
                    valid = false;
                    $log.warn('RenderService - smarteditComponent element discovered with missing contract attribute: ' + reqAttribute);
                }
            });
            return valid;
        }

        this.renderPage = function(isRerender) {
            this._resizeSlots();
            componentHandlerService.getOverlay().hide();
            this.isRenderingBlocked().then(function(isRenderingBlocked) {
                this._markSmartEditAsReady();
                var overlay = this._createOverlay();
                if (isRerender && !isRenderingBlocked) {
                    overlay.show();
                }
                systemEventService.publish(CONTRACT_CHANGE_LISTENER_PROCESS_EVENTS.RESTART_PROCESS);
                crossFrameEventService.publish(OVERLAY_RERENDERED_EVENT);
            }.bind(this));
        };

        this._createOverlay = function() {
            var overlayWrapper = componentHandlerService.getOverlay();
            if (overlayWrapper.length) {
                return overlayWrapper;
            }
            var overlay = this._getDocument().createElement("div");
            overlay.id = OVERLAY_ID;
            overlay.style.position = "absolute";
            overlay.style.top = "0px";
            overlay.style.left = "0px";
            overlay.style.bottom = "0px";
            overlay.style.right = "0px";
            overlay.style.display = "none";
            document.body.appendChild(overlay);
            return componentHandlerService.getFromSelector(overlay);
        };

        /**
         * Resizes the height of all slots on the page based on the sizes of the components. The new height of the
         * slot is set to the minimum height encompassing its sub-components, calculated by comparing each of the
         * sub-components' top and bottom bounding rectangle values.
         *
         * Slots that do not have components inside still appear in the DOM. If the CMS manager is in a perspective in which
         * slot contextual menus are displayed, slots must have a height. Otherwise, overlays will overlap. Thus, empty slots
         * are given a minimum size so that overlays match.
         */
        this._resizeSlots = function() {
            componentHandlerService.getFirstSmartEditComponentChildren('body').each(function(index, slotComponent) {
                var slotComponentID = componentHandlerService.getFromSelector(slotComponent).attr(ID_ATTRIBUTE);
                var slotComponentType = componentHandlerService.getFromSelector(slotComponent).attr(TYPE_ATTRIBUTE);

                var newSlotTop = -1;
                var newSlotBottom = -1;

                componentHandlerService.getFromSelector(slotComponent)
                    .find("." + COMPONENT_CLASS)
                    .filter(function(index, componentInSlot) {
                        componentInSlot = componentHandlerService.getFromSelector(componentInSlot);
                        return (componentInSlot.attr(ID_ATTRIBUTE) !== slotComponentID && componentInSlot.attr(TYPE_ATTRIBUTE) !== slotComponentType) && componentInSlot.is(":visible");
                    })
                    .each(function(compIndex, component) {
                        var componentDimensions = component.getBoundingClientRect();
                        newSlotTop = newSlotTop === -1 ? componentDimensions.top : Math.min(newSlotTop, componentDimensions.top);
                        newSlotBottom = newSlotBottom === -1 ? componentDimensions.bottom : Math.max(newSlotBottom, componentDimensions.bottom);
                    });

                var newSlotHeight = newSlotBottom - newSlotTop;
                var currentSlotHeight = parseFloat(window.getComputedStyle(slotComponent).height) || 0;
                if (Math.abs(currentSlotHeight - newSlotHeight) > 0.001) {
                    var currentSlotVerticalPadding = parseFloat(window.getComputedStyle(slotComponent).paddingTop) + parseFloat(window.getComputedStyle(slotComponent).paddingBottom);
                    var slotUniqueKey = slotComponentID + "_" + slotComponentType;
                    var oldSlotHeight = this._slotOriginalHeights[slotUniqueKey];
                    if (!oldSlotHeight) {
                        oldSlotHeight = currentSlotHeight;
                        this._slotOriginalHeights[slotUniqueKey] = oldSlotHeight;
                    }
                    if (newSlotHeight + currentSlotVerticalPadding > oldSlotHeight) {
                        slotComponent.style.height = (newSlotHeight + currentSlotVerticalPadding) + "px";
                    } else {
                        slotComponent.style.height = oldSlotHeight + 'px';
                    }
                    componentHandlerService.getFromSelector(slotComponent).data('smartedit-resized-slot', true);
                }
            }.bind(this));
        };

        this.renderSlots = function(_slotIds) {
            if (isBlank(_slotIds) || (_slotIds instanceof Array && _slotIds.length === 0)) {
                return $q.reject("renderService.renderSlots.slotIds.required");
            }
            if (typeof _slotIds === 'string') {
                _slotIds = [_slotIds];
            }

            //need to retrieve unique set of slotIds, happens when moving a component within a slot
            var slotIds = lodash.uniqBy(_slotIds, function(slotId) {
                return slotId;
            });

            // see if storefront can handle the rerendering
            var slotsRemaining = slotIds.filter(function(id) {
                return !seNamespaceService.renderComponent(id);
            });

            if (slotsRemaining.length <= 0) {
                //all were handled by storefront
                return $q.when(true);
            } else {

                return experienceService.buildRefreshedPreviewUrl().then(function(storefrontUrl) {
                    return $http({
                        method: 'GET',
                        url: storefrontUrl,
                        headers: {
                            'Pragma': 'no-cache'
                        }
                    }).then(function(response) {
                        var root = unsafeParseHTML(response.data);
                        slotsRemaining.forEach(function(slotId) {
                            var slotSelector = "." + COMPONENT_CLASS + "[" + TYPE_ATTRIBUTE + "='ContentSlot'][" + ID_ATTRIBUTE + "='" + slotId + "']";
                            var slotToBeRerendered = extractFromElement(root, slotSelector);
                            var originalSlot = componentHandlerService.getFromSelector(slotSelector);
                            originalSlot.html(slotToBeRerendered.html());
                            if (originalSlot.data('smartedit-resized-slot')) {
                                // reset the slot height to auto because the originalSlot height could have been changed previously with a specific height.
                                originalSlot.css('height', 'auto');
                            }
                        });
                        this._reprocessPage();
                        return $q.when();
                    }.bind(this), function(errorResponse) {
                        alertService.showDanger({
                            message: errorResponse.message
                        });
                        return $q.reject(errorResponse.message);
                    });
                }.bind(this), function(err) {
                    $log.error("renderService.renderSlots() - error with buildRefreshedPreviewUrl");
                    return $q.reject(err);
                });
            }
        };

        this.renderComponent = function(componentId, componentType) {
            var component = componentHandlerService.getComponent(componentId, componentType);
            var slotId = componentHandlerService.getParent(component).attr(ID_ATTRIBUTE);
            if (seNamespaceService.renderComponent(componentId, componentType, slotId)) {
                return $q.when(true);
            } else {
                return this.renderSlots(slotId);
            }
        };

        this.renderRemoval = function(componentId, componentType, slotId) {
            var removedComponents = componentHandlerService.getComponentUnderSlot(componentId, componentType, slotId, null).remove();
            this.refreshOverlayDimensions();
            return removedComponents;
        };

        /*
         * Given a smartEdit component in the storefront layer, its clone in the smartEdit overlay is removed and the pertaining decorators destroyed.
         *
         * @param {Element} element The original CMS component element from the storefront.
         * @param {Element=} parent the closest smartEditComponent parent, expected to be null for the highest elements
         * @param {Object=} oldAttributes The map of former attributes of the element. necessary when the element has mutated since the last creation
         */
        this._destroyComponent = function(_component, _parent, oldAttributes) {
            var component = componentHandlerService.getFromSelector(_component);
            var parent = componentHandlerService.getFromSelector(_parent);

            sakExecutorService.destroyScope(component);
            var componentInOverlayId = oldAttributes && oldAttributes[ID_ATTRIBUTE] ? oldAttributes[ID_ATTRIBUTE] : component.attr(ID_ATTRIBUTE);
            var componentInOverlayType = oldAttributes && oldAttributes[TYPE_ATTRIBUTE] ? oldAttributes[TYPE_ATTRIBUTE] : component.attr(TYPE_ATTRIBUTE);

            //the node is no longer attached so can't find parent
            if (parent.attr(ID_ATTRIBUTE)) {
                componentHandlerService.getOverlayComponentWithinSlot(componentInOverlayId, componentInOverlayType, parent.attr(ID_ATTRIBUTE)).remove();
            } else {
                componentHandlerService.getComponentInOverlay(componentInOverlayId, componentInOverlayType).remove();
            }

        };

        this._markSmartEditAsReady = function() {
            this._smartEditBootstrapGateway.publish('smartEditReady');
        };

        this._isComponentVisible = function(component) {
            // NOTE: This might not work as expected for fixed positioned items. For those cases a more expensive
            // check must be performed (get the component style and check if it's visible or not).
            return (component.offsetParent !== null);
        };

        this._reprocessPage = function() {
            seNamespaceService.reprocessPage();
        };

        this._compile = function(component, scope) {
            return $compile(component)(scope);
        };

        this._getDocument = function() {
            return document;
        };

        crossFrameEventService.subscribe(EVENT_PERSPECTIVE_CHANGED, function(eventId, isNonEmptyPerspective) {
            this.renderPage(isNonEmptyPerspective);
        }.bind(this));

        crossFrameEventService.subscribe(EVENT_PERSPECTIVE_REFRESHED, function(eventId, isNonEmptyPerspective) {
            this.renderPage(isNonEmptyPerspective);
        }.bind(this));

        systemEventService.subscribe(CONTRACT_CHANGE_LISTENER_PROCESS_EVENTS.PROCESS_COMPONENTS, function(evtId, components) {
            var result = lodash.map(components, function(component) {
                if (component.dataset[SMARTEDIT_COMPONENT_PROCESS_STATUS] !== CONTRACT_CHANGE_LISTENER_COMPONENT_PROCESS_STATUS.KEEP_VISIBLE) {
                    component.dataset[SMARTEDIT_COMPONENT_PROCESS_STATUS] = componentHandlerService.isOverlayOn() ? CONTRACT_CHANGE_LISTENER_COMPONENT_PROCESS_STATUS.PROCESS : CONTRACT_CHANGE_LISTENER_COMPONENT_PROCESS_STATUS.REMOVE;
                }
                return component;
            });
            return $q.when(result);
        });

        gatewayProxy.initForService(this, ["blockRendering", "isRenderingBlocked", "renderSlots", "renderComponent", "renderRemoval", "toggleOverlay", "refreshOverlayDimensions", "renderPage"]);
    });

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
/* jshint unused:false, undef:false */
angular.module('productMocks', ['ngMockE2E', 'functionsModule', 'backendMocksUtilsModule'])
    .run(function($httpBackend, backendMocksUtils) {
        var productCatalogMock = $httpBackend.whenGET(/cmssmarteditwebservices\/v1\/sites\/apparel-uk\/productcatalogs/);

        function parse(type) {
            return typeof type === 'string' ? JSON.parse(type) : type;
        }

        productCatalogMock.respond(function() {
            var catalogDetails = {
                "catalogs": [{
                    "catalogId": "apparelProductCatalog",
                    "name": {
                        "en": "Apparel Product Catalog",
                        "de": "Produktkatalog Kleidung"
                    },
                    "versions": [{
                        active: false,
                        version: "Staged"
                    }, {
                        active: true,
                        version: "Online"
                    }]
                }]
            };
            var returnOneCatalog = parse(sessionStorage.getItem('returnOneCatalog')) !== false;
            if (!returnOneCatalog) {
                catalogDetails.catalogs.push({
                    "catalogId": "apparelProductCatalog_2",
                    "name": {
                        "en": "Another Product Catalog",
                        "de": "Produktkatalog Kleidung 2"
                    },
                    "versions": [{
                        active: false,
                        version: "Staged"
                    }, {
                        active: true,
                        version: "Online"
                    }]
                });
            }
            return [200, catalogDetails];
        });
        backendMocksUtils.storeBackendMock('productCatalogMock', productCatalogMock);

        $httpBackend.whenGET(/cmssmarteditwebservices\/v1\/sites\/apparel-uk\/products\/(.+)/, undefined, ['code']).respond(function(method, url, data, header, params) {
            var products = {
                '300608207': {
                    "catalogId": "apparelProductCatalog",
                    "catalogVersion": "Online",
                    "code": "300608207",
                    "uid": "300608207",
                    "description": {},
                    "name": {
                        "en": "Assortment Web Belt blue black Uni"
                    }
                },
                '122409_black': {
                    "catalogId": "apparelProductCatalog",
                    "catalogVersion": "Online",
                    "code": "122409_black",
                    "uid": "122409_black",
                    "description": {},
                    "name": {
                        "en": "Asterisk SS youth black"
                    },
                    "thumbnail": {
                        "catalogId": "apparelProductCatalog",
                        "catalogVersion": "Online",
                        "code": "/96Wx96H/122409_2.jpg",
                        "downloadUrl": "/medias/?context=bWFzdGVyfGltYWdlc3w0MjUxfGltYWdlL2pwZWd8aW1hZ2VzL2g2NS9oNTUvODc5NjcxMDE3NDc1MC5qcGd8MDg1Yzc0MWU1NmI5YmYyZDVjZjMyOThkZDAzYzVmNzc4NzI2YmEyNGM5MDQ5NWY0NzM5ZjRlNGNiNDNlY2I4ZA&attachment=true",
                        "mime": "image/jpeg",
                        "url": "/medias/?context=bWFzdGVyfGltYWdlc3w0MjUxfGltYWdlL2pwZWd8aW1hZ2VzL2g2NS9oNTUvODc5NjcxMDE3NDc1MC5qcGd8MDg1Yzc0MWU1NmI5YmYyZDVjZjMyOThkZDAzYzVmNzc4NzI2YmEyNGM5MDQ5NWY0NzM5ZjRlNGNiNDNlY2I4ZA"
                    }
                },
                '300738117': {
                    "catalogId": "apparelProductCatalog",
                    "catalogVersion": "Online",
                    "code": "300738117",
                    "uid": "300738117",
                    "description": {},
                    "name": {
                        "en": "Asterisk SS youth black L"
                    }
                },
                '300738116': {
                    "catalogId": "apparelProductCatalog",
                    "catalogVersion": "Online",
                    "code": "300738116",
                    "uid": "300738116",
                    "description": {},
                    "name": {
                        "en": "Asterisk SS youth black M"
                    }
                },
                '300738316': {
                    "catalogId": "apparelProductCatalog",
                    "catalogVersion": "Staged",
                    "code": "300738316",
                    "uid": "300738316",
                    "description": {},
                    "name": {
                        "en": "Asterisk SS youth black M"
                    }
                },
                '300738118': {
                    "catalogId": "apparelProductCatalog",
                    "catalogVersion": "Online",
                    "code": "300738118",
                    "uid": "300738118",
                    "description": {},
                    "name": {
                        "en": "Asterisk SS youth black XL"
                    }
                },
                '300738114': {
                    "catalogId": "apparelProductCatalog",
                    "catalogVersion": "Online",
                    "code": "300738114",
                    "uid": "300738114",
                    "description": {},
                    "name": {
                        "en": "Asterisk SS youth black XS"
                    }
                },
                '118514': {
                    "catalogId": "apparelProductCatalog",
                    "catalogVersion": "Online",
                    "code": "118514",
                    "uid": "118514",
                    "description": {},
                    "name": {
                        "en": "Avionics Shades Black"
                    },
                    "thumbnail": {
                        "catalogId": "apparelProductCatalog",
                        "catalogVersion": "Online",
                        "code": "/96Wx96H/118514_1.jpg",
                        "downloadUrl": "/medias/?context=bWFzdGVyfGltYWdlc3wyNzkyfGltYWdlL2pwZWd8aW1hZ2VzL2g5Zi9oMGEvODc5Njc1NDkwMzA3MC5qcGd8ZWY5MWY3Mzg0NDQyMWM2MDEyMWEyZmJlNjU4ZTMwYzQwYmE0YWIxMzNjNzhjMTA3ZDc2NzJmYTU0MDQwNDZkOQ&attachment=true",
                        "mime": "image/jpeg",
                        "url": "/medias/?context=bWFzdGVyfGltYWdlc3wyNzkyfGltYWdlL2pwZWd8aW1hZ2VzL2g5Zi9oMGEvODc5Njc1NDkwMzA3MC5qcGd8ZWY5MWY3Mzg0NDQyMWM2MDEyMWEyZmJlNjU4ZTMwYzQwYmE0YWIxMzNjNzhjMTA3ZDc2NzJmYTU0MDQwNDZkOQ"
                    }
                },
                '118514_grey': {
                    "catalogId": "apparelProductCatalog",
                    "catalogVersion": "Online",
                    "code": "118514_grey",
                    "uid": "118514_grey",
                    "description": {},
                    "name": {
                        "en": "Avionics Shades Black grey"
                    },
                    "thumbnail": {
                        "catalogId": "apparelProductCatalog",
                        "catalogVersion": "Online",
                        "code": "/96Wx96H/118514_1.jpg",
                        "downloadUrl": "/medias/?context=bWFzdGVyfGltYWdlc3wyNzkyfGltYWdlL2pwZWd8aW1hZ2VzL2g5Zi9oMGEvODc5Njc1NDkwMzA3MC5qcGd8ZWY5MWY3Mzg0NDQyMWM2MDEyMWEyZmJlNjU4ZTMwYzQwYmE0YWIxMzNjNzhjMTA3ZDc2NzJmYTU0MDQwNDZkOQ&attachment=true",
                        "mime": "image/jpeg",
                        "url": "/medias/?context=bWFzdGVyfGltYWdlc3wyNzkyfGltYWdlL2pwZWd8aW1hZ2VzL2g5Zi9oMGEvODc5Njc1NDkwMzA3MC5qcGd8ZWY5MWY3Mzg0NDQyMWM2MDEyMWEyZmJlNjU4ZTMwYzQwYmE0YWIxMzNjNzhjMTA3ZDc2NzJmYTU0MDQwNDZkOQ"
                    }
                },
                '111159_black': {
                    "catalogId": "apparelProductCatalog",
                    "catalogVersion": "Online",
                    "code": "111159_black",
                    "uid": "111159_black",
                    "description": {},
                    "name": {
                        "en": "BT Airhole Splatter Facemask black"
                    },
                    "thumbnail": {
                        "catalogId": "apparelProductCatalog",
                        "catalogVersion": "Online",
                        "code": "/96Wx96H/111159_3.jpg",
                        "downloadUrl": "/medias/?context=bWFzdGVyfGltYWdlc3w0NDc2fGltYWdlL2pwZWd8aW1hZ2VzL2g3Yy9oYWQvODc5NjU3NTgyNTk1MC5qcGd8NGVlOTkxNTc0NjcwMDk4ZGNlMTIzYmI0MGE5MTIwYTEwZTRmOTQ1ZmYzMWJiYWY4OThjNDBmZTQ4ZDE0YTNlNg&attachment=true",
                        "mime": "image/jpeg",
                        "url": "/medias/?context=bWFzdGVyfGltYWdlc3w0NDc2fGltYWdlL2pwZWd8aW1hZ2VzL2g3Yy9oYWQvODc5NjU3NTgyNTk1MC5qcGd8NGVlOTkxNTc0NjcwMDk4ZGNlMTIzYmI0MGE5MTIwYTEwZTRmOTQ1ZmYzMWJiYWY4OThjNDBmZTQ4ZDE0YTNlNg"
                    }
                },
                '300689173': {
                    "catalogId": "apparelProductCatalog",
                    "catalogVersion": "Online",
                    "code": "300689173",
                    "uid": "300689173",
                    "description": {},
                    "name": {
                        "en": "BT Airhole Splatter Facemask black LXL"
                    }
                }
            };

            return [200, products[params.code]];
        });

        $httpBackend.whenGET(/cmssmarteditwebservices\/v1\/productcatalogs\/apparelProductCatalog_2\/versions\/Online\/products\?.*/).respond(function() {
            var productsList = {
                "pagination": {
                    "count": 2,
                    "page": 1,
                    "totalCount": 2,
                    "totalPages": 1
                },
                "products": [{
                    "catalogId": "apparelProductCatalog_2",
                    "catalogVersion": "Online",
                    "code": "1234567890",
                    "uid": "1234567890",
                    "description": {},
                    "name": {
                        "en": "[product catalog 2] Assortment Web Belt blue black Uni"
                    }
                }, {
                    "catalogId": "apparelProductCatalog_2",
                    "catalogVersion": "Online",
                    "code": "0987654321",
                    "uid": "0987654321",
                    "description": {},
                    "name": {
                        "en": "[product catalog 2] Asterisk SS youth black"
                    },
                    "thumbnail": {
                        "catalogId": "apparelProductCatalog_2",
                        "catalogVersion": "Online",
                        "code": "/96Wx96H/122409_2.jpg",
                        "downloadUrl": "/medias/?context=bWFzdGVyfGltYWdlc3w0MjUxfGltYWdlL2pwZWd8aW1hZ2VzL2g2NS9oNTUvODc5NjcxMDE3NDc1MC5qcGd8MDg1Yzc0MWU1NmI5YmYyZDVjZjMyOThkZDAzYzVmNzc4NzI2YmEyNGM5MDQ5NWY0NzM5ZjRlNGNiNDNlY2I4ZA&attachment=true",
                        "mime": "image/jpeg",
                        "url": "/medias/?context=bWFzdGVyfGltYWdlc3w0MjUxfGltYWdlL2pwZWd8aW1hZ2VzL2g2NS9oNTUvODc5NjcxMDE3NDc1MC5qcGd8MDg1Yzc0MWU1NmI5YmYyZDVjZjMyOThkZDAzYzVmNzc4NzI2YmEyNGM5MDQ5NWY0NzM5ZjRlNGNiNDNlY2I4ZA"
                    }
                }]
            };
            return [200, productsList];
        });

        $httpBackend.whenGET(/cmssmarteditwebservices\/v1\/productcatalogs\/apparelProductCatalog\/versions\/Online\/products\?.*/).respond(function() {
            var productsList = {
                "pagination": {
                    "count": 10,
                    "page": 1,
                    "totalCount": 10,
                    "totalPages": 1
                },
                "products": [{
                    "catalogId": "apparelProductCatalog",
                    "catalogVersion": "Online",
                    "code": "300608207",
                    "uid": "300608207",
                    "description": {},
                    "name": {
                        "en": "Assortment Web Belt blue black Uni"
                    }
                }, {
                    "catalogId": "apparelProductCatalog",
                    "catalogVersion": "Online",
                    "code": "122409_black",
                    "uid": "122409_black",
                    "description": {},
                    "name": {
                        "en": "Asterisk SS youth black"
                    },
                    "thumbnail": {
                        "catalogId": "apparelProductCatalog",
                        "catalogVersion": "Online",
                        "code": "/96Wx96H/122409_2.jpg",
                        "downloadUrl": "/medias/?context=bWFzdGVyfGltYWdlc3w0MjUxfGltYWdlL2pwZWd8aW1hZ2VzL2g2NS9oNTUvODc5NjcxMDE3NDc1MC5qcGd8MDg1Yzc0MWU1NmI5YmYyZDVjZjMyOThkZDAzYzVmNzc4NzI2YmEyNGM5MDQ5NWY0NzM5ZjRlNGNiNDNlY2I4ZA&attachment=true",
                        "mime": "image/jpeg",
                        "url": "/medias/?context=bWFzdGVyfGltYWdlc3w0MjUxfGltYWdlL2pwZWd8aW1hZ2VzL2g2NS9oNTUvODc5NjcxMDE3NDc1MC5qcGd8MDg1Yzc0MWU1NmI5YmYyZDVjZjMyOThkZDAzYzVmNzc4NzI2YmEyNGM5MDQ5NWY0NzM5ZjRlNGNiNDNlY2I4ZA"
                    }
                }, {
                    "catalogId": "apparelProductCatalog",
                    "catalogVersion": "Online",
                    "code": "300738117",
                    "uid": "300738117",
                    "description": {},
                    "name": {
                        "en": "Asterisk SS youth black L"
                    }
                }, {
                    "catalogId": "apparelProductCatalog",
                    "catalogVersion": "Online",
                    "code": "300738116",
                    "uid": "300738116",
                    "description": {},
                    "name": {
                        "en": "Asterisk SS youth black M"
                    }
                }, {
                    "catalogId": "apparelProductCatalog",
                    "catalogVersion": "Online",
                    "code": "300738118",
                    "uid": "300738118",
                    "description": {},
                    "name": {
                        "en": "Asterisk SS youth black XL"
                    }
                }, {
                    "catalogId": "apparelProductCatalog",
                    "catalogVersion": "Online",
                    "code": "300738114",
                    "uid": "300738114",
                    "description": {},
                    "name": {
                        "en": "Asterisk SS youth black XS"
                    }
                }, {
                    "catalogId": "apparelProductCatalog",
                    "catalogVersion": "Online",
                    "code": "118514",
                    "uid": "118514",
                    "description": {},
                    "name": {
                        "en": "Avionics Shades Black"
                    },
                    "thumbnail": {
                        "catalogId": "apparelProductCatalog",
                        "catalogVersion": "Online",
                        "code": "/96Wx96H/118514_1.jpg",
                        "downloadUrl": "/medias/?context=bWFzdGVyfGltYWdlc3wyNzkyfGltYWdlL2pwZWd8aW1hZ2VzL2g5Zi9oMGEvODc5Njc1NDkwMzA3MC5qcGd8ZWY5MWY3Mzg0NDQyMWM2MDEyMWEyZmJlNjU4ZTMwYzQwYmE0YWIxMzNjNzhjMTA3ZDc2NzJmYTU0MDQwNDZkOQ&attachment=true",
                        "mime": "image/jpeg",
                        "url": "/medias/?context=bWFzdGVyfGltYWdlc3wyNzkyfGltYWdlL2pwZWd8aW1hZ2VzL2g5Zi9oMGEvODc5Njc1NDkwMzA3MC5qcGd8ZWY5MWY3Mzg0NDQyMWM2MDEyMWEyZmJlNjU4ZTMwYzQwYmE0YWIxMzNjNzhjMTA3ZDc2NzJmYTU0MDQwNDZkOQ"
                    }
                }, {
                    "catalogId": "apparelProductCatalog",
                    "catalogVersion": "Online",
                    "code": "118514_grey",
                    "uid": "118514_grey",
                    "description": {},
                    "name": {
                        "en": "Avionics Shades Black grey"
                    },
                    "thumbnail": {
                        "catalogId": "apparelProductCatalog",
                        "catalogVersion": "Online",
                        "code": "/96Wx96H/118514_1.jpg",
                        "downloadUrl": "/medias/?context=bWFzdGVyfGltYWdlc3wyNzkyfGltYWdlL2pwZWd8aW1hZ2VzL2g5Zi9oMGEvODc5Njc1NDkwMzA3MC5qcGd8ZWY5MWY3Mzg0NDQyMWM2MDEyMWEyZmJlNjU4ZTMwYzQwYmE0YWIxMzNjNzhjMTA3ZDc2NzJmYTU0MDQwNDZkOQ&attachment=true",
                        "mime": "image/jpeg",
                        "url": "/medias/?context=bWFzdGVyfGltYWdlc3wyNzkyfGltYWdlL2pwZWd8aW1hZ2VzL2g5Zi9oMGEvODc5Njc1NDkwMzA3MC5qcGd8ZWY5MWY3Mzg0NDQyMWM2MDEyMWEyZmJlNjU4ZTMwYzQwYmE0YWIxMzNjNzhjMTA3ZDc2NzJmYTU0MDQwNDZkOQ"
                    }
                }, {
                    "catalogId": "apparelProductCatalog",
                    "catalogVersion": "Online",
                    "code": "111159_black",
                    "uid": "111159_black",
                    "description": {},
                    "name": {
                        "en": "BT Airhole Splatter Facemask black"
                    },
                    "thumbnail": {
                        "catalogId": "apparelProductCatalog",
                        "catalogVersion": "Online",
                        "code": "/96Wx96H/111159_3.jpg",
                        "downloadUrl": "/medias/?context=bWFzdGVyfGltYWdlc3w0NDc2fGltYWdlL2pwZWd8aW1hZ2VzL2g3Yy9oYWQvODc5NjU3NTgyNTk1MC5qcGd8NGVlOTkxNTc0NjcwMDk4ZGNlMTIzYmI0MGE5MTIwYTEwZTRmOTQ1ZmYzMWJiYWY4OThjNDBmZTQ4ZDE0YTNlNg&attachment=true",
                        "mime": "image/jpeg",
                        "url": "/medias/?context=bWFzdGVyfGltYWdlc3w0NDc2fGltYWdlL2pwZWd8aW1hZ2VzL2g3Yy9oYWQvODc5NjU3NTgyNTk1MC5qcGd8NGVlOTkxNTc0NjcwMDk4ZGNlMTIzYmI0MGE5MTIwYTEwZTRmOTQ1ZmYzMWJiYWY4OThjNDBmZTQ4ZDE0YTNlNg"
                    }
                }, {
                    "catalogId": "apparelProductCatalog",
                    "catalogVersion": "Online",
                    "code": "300689173",
                    "uid": "300689173",
                    "description": {},
                    "name": {
                        "en": "BT Airhole Splatter Facemask black LXL"
                    }
                }]
            };

            return [200, productsList];
        });

        $httpBackend.whenGET(/cmssmarteditwebservices\/v1\/productcatalogs\/apparelProductCatalog\/versions\/Staged\/products\?.*/).respond(function() {
            var productsList = {
                "pagination": {
                    "count": 10,
                    "page": 1,
                    "totalCount": 10,
                    "totalPages": 1
                },
                "products": [{
                    "catalogId": "apparelProductCatalog",
                    "catalogVersion": "Staged",
                    "code": "300738316",
                    "uid": "300738316",
                    "description": {},
                    "name": {
                        "en": "Asterisk SS youth black M"
                    }
                }]
            };

            return [200, productsList];
        });

        var categoriesList = {
            "pagination": {
                "count": 10,
                "page": 1,
                "totalCount": 10,
                "totalPages": 1
            },
            "productCategories": [{
                "catalogId": "apparelProductCatalog",
                "catalogVersion": "Online",
                "code": "Aesthetiker",
                "description": {
                    "en": "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi dapibus convallis magna eu placerat. Cras non tortor nulla, quis pharetra dui. Sed nisl tortor, lacinia nec molestie in, pellentesque ut metus. Nunc ut sapien ut augue vestibulum luctus."
                },
                "name": {
                    "en": "Aesthetiker"
                },
                "uid": "eyJpdGVtSWQiOiJBZXN0aGV0aWtlciIsImNhdGFsb2dJZCI6ImFwcGFyZWxQcm9kdWN0Q2F0YWxvZyIsImNhdGFsb2dWZXJzaW9uIjoiT25saW5lIn0="
            }, {
                "catalogId": "apparelProductCatalog",
                "catalogVersion": "Online",
                "code": "Airhole",
                "description": {},
                "name": {
                    "en": "Airhole"
                },
                "uid": "eyJpdGVtSWQiOiJBaXJob2xlIiwiY2F0YWxvZ0lkIjoiYXBwYXJlbFByb2R1Y3RDYXRhbG9nIiwiY2F0YWxvZ1ZlcnNpb24iOiJPbmxpbmUifQ=="
            }, {
                "catalogId": "apparelProductCatalog",
                "catalogVersion": "Online",
                "code": "Al Merrick",
                "description": {},
                "name": {
                    "en": "Al Merrick"
                },
                "uid": "eyJpdGVtSWQiOiJBbCBNZXJyaWNrIiwiY2F0YWxvZ0lkIjoiYXBwYXJlbFByb2R1Y3RDYXRhbG9nIiwiY2F0YWxvZ1ZlcnNpb24iOiJPbmxpbmUifQ=="
            }, {
                "catalogId": "apparelProductCatalog",
                "catalogVersion": "Online",
                "code": "Alien Workshop",
                "description": {},
                "name": {
                    "en": "Alien Workshop"
                },
                "uid": "eyJpdGVtSWQiOiJBbGllbiBXb3Jrc2hvcCIsImNhdGFsb2dJZCI6ImFwcGFyZWxQcm9kdWN0Q2F0YWxvZyIsImNhdGFsb2dWZXJzaW9uIjoiT25saW5lIn0="
            }, {
                "catalogId": "apparelProductCatalog",
                "catalogVersion": "Online",
                "code": "Alpinestars",
                "description": {},
                "name": {
                    "en": "Alpinestars"
                },
                "uid": "eyJpdGVtSWQiOiJBbHBpbmVzdGFycyIsImNhdGFsb2dJZCI6ImFwcGFyZWxQcm9kdWN0Q2F0YWxvZyIsImNhdGFsb2dWZXJzaW9uIjoiT25saW5lIn0="
            }, {
                "catalogId": "apparelProductCatalog",
                "catalogVersion": "Online",
                "code": "Alptraum",
                "description": {},
                "name": {
                    "en": "Alptraum"
                },
                "uid": "eyJpdGVtSWQiOiJBbHB0cmF1bSIsImNhdGFsb2dJZCI6ImFwcGFyZWxQcm9kdWN0Q2F0YWxvZyIsImNhdGFsb2dWZXJzaW9uIjoiT25saW5lIn0="
            }, {
                "catalogId": "apparelProductCatalog",
                "catalogVersion": "Online",
                "code": "Amplid",
                "description": {},
                "name": {
                    "en": "Amplid"
                },
                "uid": "eyJpdGVtSWQiOiJBbXBsaWQiLCJjYXRhbG9nSWQiOiJhcHBhcmVsUHJvZHVjdENhdGFsb2ciLCJjYXRhbG9nVmVyc2lvbiI6Ik9ubGluZSJ9"
            }, {
                "catalogId": "apparelProductCatalog",
                "catalogVersion": "Online",
                "code": "Analog",
                "description": {},
                "name": {
                    "en": "Analog"
                },
                "uid": "eyJpdGVtSWQiOiJBbmFsb2ciLCJjYXRhbG9nSWQiOiJhcHBhcmVsUHJvZHVjdENhdGFsb2ciLCJjYXRhbG9nVmVyc2lvbiI6Ik9ubGluZSJ9"
            }, {
                "catalogId": "apparelProductCatalog",
                "catalogVersion": "Online",
                "code": "Anon",
                "description": {},
                "name": {
                    "en": "Anon"
                },
                "uid": "eyJpdGVtSWQiOiJBbm9uIiwiY2F0YWxvZ0lkIjoiYXBwYXJlbFByb2R1Y3RDYXRhbG9nIiwiY2F0YWxvZ1ZlcnNpb24iOiJPbmxpbmUifQ=="
            }, {
                "catalogId": "apparelProductCatalog",
                "catalogVersion": "Online",
                "code": "Apo",
                "description": {},
                "name": {
                    "en": "Apo"
                },
                "uid": "eyJpdGVtSWQiOiJBcG8iLCJjYXRhbG9nSWQiOiJhcHBhcmVsUHJvZHVjdENhdGFsb2ciLCJjYXRhbG9nVmVyc2lvbiI6Ik9ubGluZSJ9"
            }, {
                "catalogId": "apparelProductCatalog",
                "catalogVersion": "Staged",
                "code": "shirts",
                "description": {},
                "name": {
                    "en": "Shirts",
                    "de": "Hemden"
                },
                "uid": "eyJpdGVtSWQiOiJBcG8iLCJjYXRhbG9nSWQiOiJhcHBhcmVsUHJvZHVjdENhdGFsb2ciLCJjYXRhbG9nVmVyc2lvbiI6Ik9ubGluZSJ9Shirts_Staged"
            }, {
                "catalogId": "apparelProductCatalog",
                "catalogVersion": "Staged",
                "code": "pants",
                "description": {},
                "name": {
                    "en": "Pants",
                    "de": "Pants"
                },
                "uid": "eyJpdGVtSWQiOiJBcG8iLCJjYXRhbG9nSWQiOiJhcHBhcmVsUHJvZHVjdENhdGFsb2ciLCJjYXRhbG9nVmVyc2lvbiI6Ik9ubGluZSJ9Pants"
            }, {
                "catalogId": "apparelProductCatalog",
                "catalogVersion": "Online",
                "code": "shirts",
                "description": {},
                "name": {
                    "en": "Shirts",
                    "de": "Hemden"
                },
                "uid": "eyJpdGVtSWQiOiJBcG8iLCJjYXRhbG9nSWQiOiJhcHBhcmVsUHJvZHVjdENhdGFsb2ciLCJjYXRhbG9nVmVyc2lvbiI6Ik9ubGluZSJ9Shirts"
            }, {
                "catalogId": "apparelProductCatalog",
                "catalogVersion": "Online",
                "code": "shoes",
                "description": {},
                "name": {
                    "en": "Shoes",
                    "de": "Shoes"
                },
                "uid": "eyJpdGVtSWQiOiJBcG8iLCJjYXRhbG9nSWQiOiJhcHBhcmVsUHJvZHVjdENhdGFsb2ciLCJjYXRhbG9nVmVyc2lvbiI6Ik9ubGluZSJ9Shoes"
            }]
        };

        var categoriesMock = $httpBackend.whenGET(/cmssmarteditwebservices\/v1\/productcatalogs\/apparelProductCatalog\/versions\/(\w+)\/categories\?(\w*)/, undefined, ['catalogVersion', 'queryString']);
        categoriesMock.respond(function(method, url, data, header, params) {

            var catalogVersion = /.*versions\/(.*)\/categories.*/.exec(url)[1];
            var filteredList = categoriesList.productCategories.filter(function(category) {
                return category.catalogVersion === catalogVersion;
            });

            var result = {
                "pagination": {
                    "count": 10,
                    "page": 1,
                    "totalCount": filteredList.length,
                    "totalPages": 1
                },
                "productCategories": filteredList
            };

            return [200, result];
        });
        backendMocksUtils.storeBackendMock('categoriesMock', categoriesMock);

        $httpBackend.whenGET(/cmssmarteditwebservices\/v1\/sites\/apparel-uk\/categories\/(.+)/, undefined, ['categoryUID']).respond(function(method, url, data, header, params) {
            var result = categoriesList.productCategories.filter(function(category) {
                return category.uid === params.categoryUID;
            })[0];

            return [200, result];
        });
    });
angular.module('smarteditloader').requires.push('productMocks');
angular.module('smarteditcontainer').requires.push('productMocks');

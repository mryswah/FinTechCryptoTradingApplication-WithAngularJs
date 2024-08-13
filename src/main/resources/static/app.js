angular.module('cryptoTradingApp', ['ngRoute'])
    .factory('authResolver', ['$rootScope', 'authService', '$location', function($rootScope, authService, $location) {
        return {
            resolve: function() {
                if (!$rootScope.currentUser || $rootScope.currentUser === null) {
                    authService.logout();
                    $location.path("/login");
                    return false;
                }
                return true;
            }
        };
    }])
    .constant('API_URL', 'http://localhost:8080')
    .config(['$routeProvider', function($routeProvider) {
        $routeProvider
            .when('/coinPrice', {
                templateUrl: 'coin/coin-price.html',
                controller: 'coinPriceController',
                resolve: {
                    auth: ['authResolver', function(authResolver) {
                        return authResolver.resolve();
                    }]
                }
            })
            .when('/trade', {
                templateUrl: 'transaction/transaction.html',
                controller: 'transactionController',
                resolve: {
                    auth: ['authResolver', function(authResolver) {
                        return authResolver.resolve();
                    }]
                }
            })
            .when('/user', {
                templateUrl: 'user/user.html',
                controller: 'userController',
                resolve: {
                    auth: ['authResolver', function(authResolver) {
                        return authResolver.resolve();
                    }]
                }
            })
            .when('/login', {
                templateUrl: 'login/login.html',
                controller: 'loginController'
            })
            .otherwise({
                redirectTo: '/login'
            });
    }]);
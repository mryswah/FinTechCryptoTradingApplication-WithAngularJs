angular.module('cryptoTradingApp').service('authService', ['$rootScope', '$location', function($rootScope, $location) {
    this.logout = function() {
        console.log("logging out");
        $rootScope.currentUser = "";
        $location.path("/");
    };
}]);
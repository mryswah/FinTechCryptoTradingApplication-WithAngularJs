angular.module('cryptoTradingApp').controller('navBarController', function($scope, $route, $location, authService) {
	console.log("navBarController logging")
    $scope.reloadRoute = function(route) {
        if ($location.path() === route) {
            $route.reload();
        }
    };
    $scope.logout = authService.logout;
});
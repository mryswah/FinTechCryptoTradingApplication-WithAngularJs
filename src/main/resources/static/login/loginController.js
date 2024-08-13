angular.module('cryptoTradingApp').config(function($routeProvider) {
  $routeProvider.when('/user', {
    templateUrl: 'user/user.html',
    controller: 'userController'
  });
});

/* Create factory to Disable Browser Back Button only after Logout */
angular.module('cryptoTradingApp').factory("checkAuth", function($location,$rootScope){
    return {
        getUserInfo : function(){
			if($rootScope.isLoggedIn === undefined || $rootScope.isLoggedIn === null){
				$location.path('/');
			}
		}
    };
});

angular.module('cryptoTradingApp').controller('loginController', function($scope, $http, API_URL, $location, $rootScope, authService) {
	console.log("loginController logging")
	$scope.user = {};
	$scope.login = function() {
	$http.post('/login', $scope.user).then(function(response) {
		console.log("response : ", response)
		console.log("response.data : ", response.data)
		if (response.data) {
			// Login successful, redirect to dashboard or whatever
			$rootScope.currentUser = response.data;
            $location.path('/user');
			console.log("Login successful");
		}
		})
		.catch(function(error) {
		    $scope.error = "Invalid user or password"
		});
	};
angular.module('cryptoTradingApp').controller('userController', function($scope, $http, API_URL, $rootScope) {
	console.log("userController logging");
	console.log("$rootScope.currentUser : ", $rootScope.currentUser); // access the user's data

	$scope.userData = {};

	const headers = {
	    'Content-Type': 'application/json' // set the content type to JSON
	};

	// Helper function to make POST requests
	function postRequest(endpoint, data) {
	    return $http.post(API_URL + endpoint, data, { headers: headers })
		.then(function(response) {
		    return response.data;
		}, function(error) {
		    console.error(`Error getting data from ${endpoint}:`, error);
            throw error; // Rethrow to handle it where the function is called, if necessary
		});
	}

	// Prepare the request payload
	const jsonData = {
	customerInformation: {
		username: $rootScope.currentUser.username
	}
	};

	// Make the requests
	postRequest('/user/walletBalance', jsonData)
    .then(function(walletBalance) {
		$scope.userData.walletBalance = walletBalance;
	});

	postRequest('/user/cryptoWalletBalance', jsonData)
	.then(function(cryptoWalletBalance) {
		$scope.userData.cryptoWalletBalance = cryptoWalletBalance;
	});

	postRequest('/user/transactions', jsonData)
	.then(function(transactions) {
		$scope.userData.transactions = transactions;
	});
});

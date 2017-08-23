var config = {
    apiKey: "AIzaSyA-t9oeslDd3bJ1N7L_iHVgvFafZSPJkyE",
    authDomain: "fir-82b14.firebaseapp.com",
    databaseURL: "https://fir-82b14.firebaseio.com",
    projectId: "fir-82b14",
    storageBucket: "fir-82b14.appspot.com",
    messagingSenderId: "949170737956"
  };
  firebase.initializeApp(config);

const messaging = firebase.messaging();

messaging.requestPermission()
.then(function(){
	console.log('Have Permission');
	return messaging.getToken();
})
.then(function (token) {
        console.log(token);			
    })
.catch(function(err){
	console.log('Error Occured.');
})

messaging.onMessage(function(payload){
	console.log('onMessage: ', payload);
});
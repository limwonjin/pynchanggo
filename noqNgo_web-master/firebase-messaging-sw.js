importScripts('https://www.gstatic.com/firebasejs/3.6.2/firebase-app.js');
importScripts('https://www.gstatic.com/firebasejs/3.6.2/firebase-messaging.js');

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

 messaging.setBackgroundMessageHandler(function(payload){
	 const title = 'Hello world';
	 const options = {
		 body: payload.data.status
	 }
	 return self.registration.showNotification(title,options);
 });
 
 self.addEventListener('notificationclick', function(event) {
  console.log('[Service Worker] Notification click Received.');

  event.notification.close();

  event.waitUntil(
    clients.openWindow('https://fir-82b14.firebaseapp.com/customer.html')
  );
});

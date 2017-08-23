importScripts('https://www.gstatic.com/firebasejs/3.6.2/firebase-app.js');
importScripts('https://www.gstatic.com/firebasejs/3.6.2/firebase-messaging.js');

src = "script.js";

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

var body;
var storeName;
var sid;

messaging.setBackgroundMessageHandler(function(payload){

  for (i in payload){
    for (j in payload[i]){
      console.log(i+j+ " : "+ payload[i][j]);
      if (j=="body"){ body = payload[i][j];}
      if (j=="title"){ storeName = payload[i][j];}
      if (j=="sid"){ sid = payload[i][j];}
    }
  }

  console.log(" **body** :  " + body);
  console.log(" **title** :  " + storeName);
  console.log(" **uid** :  " + sid);
    
	const title = storeName;
	const options = {
		body: "호출"
	}
	return self.registration.showNotification(title,options);
});
 
self.addEventListener('notificationclick', function(event) {
  console.log('[Service Worker] Notification click Received.');
  event.notification.close();
  event.waitUntil(
    clients.openWindow("https://fir-82b14.firebaseapp.com/customer.html?sid="+sid)
  );
});

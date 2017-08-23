var doubleSubmitFlag = false; //중복 클릭 방지

//firebase auth
var config = {
  apiKey: "AIzaSyA-t9oeslDd3bJ1N7L_iHVgvFafZSPJkyE",
  authDomain: "fir-82b14.firebaseapp.com",
  databaseURL: "https://fir-82b14.firebaseio.com",
  projectId: "fir-82b14",
  storageBucket: "fir-82b14.appspot.com",
  messagingSenderId: "949170737956"
};
firebase.initializeApp(config);
///////////////////////////////////////////////auth end


//firebase messging
const messaging = firebase.messaging();

messaging.requestPermission()
.then(function(){
  console.log('messaging : Have Permission');
  return messaging.getToken();
})
.then(function (token) {
    console.log("token : "+token);
  })
.catch(function(err){
  console.log('Error Occured.');
})

messaging.onMessage(function(payload){
  console.log('onMessage: ', payload);
  window.navigator.vibrate([200,100,200]);
  alert("호출");

});

/////////////////////////////////////////messaging end

//주소값에서 가게 아이디 받아옴 $_GET[]
var getParam = function(paramName){
  var returnValue;
  var url = location.href;
  var parameters = (url.slice(url.indexOf('?') + 1, url.length)).split('&');
  for (var i = 0; i < parameters.length; i++) {
    var varName = parameters[i].split('=')[0];
    if (varName.toUpperCase() == paramName.toUpperCase()) {
        returnValue = parameters[i].split('=')[1];
        return decodeURIComponent(returnValue);
    }
  }
};
var sid = getParam('sid'); //store uid ==> sid 가게아이디
///////////////////////////////////////////////////id end



//firebase 시작, 초기화
document.addEventListener('DOMContentLoaded', function() { 
 
  function getremaining_person(keynum){ 
    var waiting = firebase.database().ref('/cnt/'+keynum+'/inqueue');
    waiting.on('value',function(snapshot){
      var a = snapshot.numChildren();
      document.getElementById('num').innerHTML = "총"+a+"명 대기중 입니다.";
    });
  }
  

  //가게 정보 불러와서 띄워주기
  var info = firebase.database().ref('/register/'+sid);
  info.on('value',function(snapshot){
    var email = snapshot.child("email").val();
    var keynum = snapshot.child("keynum").val();
    var name = snapshot.child("name").val();

    console.log("email : "+email);
    console.log("keynum : "+keynum);
    console.log("name : "+name);

    document.getElementById('store').innerHTML = name;
    getremaining_person(keynum);

  });
  ////////////////정보 end

//토큰이 db 안에 있는지 검사
var token = messaging.getToken().then(function(currentToken){
  if(currentToken){
    firebase.database().ref().once('value',function(snapshot){
      var existToken = false;
      var keynum = snapshot.child("register").child(sid).child("keynum").val();
      var inqueue = snapshot.child("cnt").child(keynum).child("inqueue").val();
      var qcount = snapshot.child("cnt").child(keynum).child("qcount").val();
      for (key in inqueue){
        if(key == currentToken){
          console.log("exist");
          existToken = true;
          doubleSubmitFlag = true;
          document.getElementById("link").innerHTML = "대기번호" +  inqueue[key] + "번 입니다";
          document.getElementById("link").style.backgroundColor = "#325160";
          remainPerson(keynum,inqueue[key],currentToken);
        }
      }
    });
  }
})
        
  console.log("db연결 try");
  try {
    let app = firebase.app();
    let features = ['auth', 'database', 'messaging', 'storage'].filter(feature => typeof app[feature] === 'function');
    document.getElementById('load').innerHTML = `Firebase SDK loaded with ${features.join(', ')}`;
  } catch (e) {
    console.error(e);
    document.getElementById('load').innerHTML = 'Error loading the Firebase SDK, check the console.';
  }
})
//////////////////////////////////////////////firebase end
/////////////////////////////////////////////////////초기화 끗 ><

var exist=false; //토큰이 db 안에 존재하는가
//내앞에 남은 사람
function remainPerson(keynum,mynum,currentToken){  //내앞에 남은사람 구하기
  var waiting = firebase.database().ref('/cnt/'+keynum);
  waiting.on('value',function(snapshot){
    var inqueue = snapshot.child("inqueue").val()
    var count = 0 ;
    exist = false;
    for(key in inqueue){
      if(inqueue[key]<mynum){  //큐 안에 나보다 작은 번호 가 있으면 count++
        count++;
      }
      if(key == currentToken){  // 내 토큰과 큐 안의 키가 같다면 exist
        exist = true;
      }
      document.getElementById('num').innerHTML = "내앞에 지금"+count+"명 대기중 ~.~";
      window.navigator.vibrate([200, 100, 200]);

    }
    if(exist == false){
      alert("서비스 종료"); //존재하지 않는다면 종료알람
      window.open("about:blank","_self").close(); //웹페이지 강제종료
    }
  });
}



function token_push(){ //토큰 db 안에 push , onclick 이벤트
  var mynum; //내 대기번호
  if(doubleSubmitFlag == false){
    var token = messaging.getToken().then(function(currentToken){
      if(currentToken){
        firebase.database().ref().once('value',function(snapshot){
          var keynum = snapshot.child("register").child(sid).child("keynum").val();//가게 번호
          var qcount = snapshot.child("cnt").child(keynum).child("qcount").val(); //큐카운트 불러오기
          var inqueue = snapshot.child("cnt").child(keynum).child("inqueue").val(); //대기 고객 토큰

          if(qcount == null){ firebase.database().ref('cnt/'+keynum+'/qcount/').set(0);}
          if(exist == false){ //토큰이 db에 존재하지 않으면
            console.log("qcount 증가");
            firebase.database().ref('cnt/'+keynum+'/qcount/').set(qcount+1);//qcount++
            firebase.database().ref('cnt/'+keynum+'/inqueue/'+currentToken).set(qcount); //토큰값 넣음
            firebase.database().ref('UserQueue/'+currentToken+'/'+keynum).set(qcount);
            mynum = qcount;
          }
          document.getElementById("link").innerHTML = "대기번호" +  mynum + "번 입니다";
          document.getElementById("link").style.backgroundColor = "#325160";
          remainPerson(keynum,mynum,currentToken);
        });
      }
    })
  }
doubleSubmitFlag = true;
}


///////////////////삭제시 코드 실행 ////////////////////////
//웹페이지 강제종료
// window.open("about:blank","_self").close();
//로컬스토리지 삭제 
//localStorage.clear();
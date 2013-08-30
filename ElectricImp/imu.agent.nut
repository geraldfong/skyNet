function readBlob(blob){
    local str = "";
    blob.seek(0);
    while(blob.eos() == null){
        str += format("%c", blob.readn('c'));
    }
    return str; 
}
function onHttpComplete(m)
{
  server.log("status was " + m.statuscode);
  if (m.statuscode == 200) { // "OK"
    server.log("Proudly using " + m.headers.server);
  }
}

device.on("DATA", function(data){
    server.log("GOT DATA at: " + time());
    server.log("EVENT: " + data); 
    if(data == "WAVE"){
        http.get("https://agent.electricimp.com/9JJrMpKA9pBk/toggle", {}).sendasync(onHttpComplete);
//        http.get("https://agent.electricimp.com/3zuteAE-YCQR/strobe", {}).sendasync(onHttpComplete);
    }
    http.post("http://54.215.14.147:8080/gesture", {}, data).sendasync(onHttpComplete);
});

device.on("GYRO", function(data){
//    server.log("GOT DATA at: " + time());c
//    server.log("data: " + readBlob(data)); 
    http.post("http://54.241.33.105:8080/imu", {}, data).sendasync(onHttpComplete);
});


const BUFFER_LEN = 2000;    // Number of characters to 
const FIFO_LEN = 200;
const GESTURE_WINDOW = 2;
//const STD_DIV_MULTIPLE = 2;
const GESTURE_TIME = 4;
const JERK_THRESHOLD = 3000;
enum GESTURES {
    LEFT = "LEFT"
    RIGHT = "RIGHT"
    UP = "UP"
    DOWN = "DOWN"
    WAVE = "WAVE"
    PUSH = "PUSH"
    PULL = "PULL"
}

DataTypes <- ["AccX","AccY","AccZ","RLL","PCH","YAW"];
DATAPTS <- blob(BUFFER_LEN);
FIFO <- blob(FIFO_LEN);
STR <- "";
IMUdata <- {
    AccX = []
    AccY = []
    AccZ = []
    RLL = []
    PCH = []
    YAW = []
}
Gestures <- {
    AccX = []
    AccY = []
    AccZ = []
    RLL = []
    PCH = []
    YAW = []
}
gestureTimeOffset <- {
    AccX = 0
    AccY = 0
    AccZ = 0
}
GestureCount <- {
    AccX = 0
    AccY = 0
    AccZ = 0
}
bufferCount <- 0;

imp.configure("ArduIMU", [], []);

function readArray(arr){
    local arrStr = "[ ";
    foreach(val in arr){
        arrStr += (val + " ");
    }
    arrStr += "]"
    return arrStr;
}

function readBlob(blob){
    local idx = blob.tell();
    local str = "";
    blob.seek(0);
    while(blob.eos() == null){
        str += format("%c", blob.readn('c'));
    }
    blob.seek(idx);
    return str; 
}

function parseData(str){
    local strArr = split(str,",");
    local offset = 0;
    foreach(val in strArr){
        local dataPair = split(val,":");
        if(dataPair.len()<2){
            return;
        }
        local type = dataPair[0];
        local datum = dataPair[1];
        if(type=="AccX" || type=="AccY" || type=="AccZ") {
            offset = 5;
        }
        else if(type=="RLL" || type=="PCH" || type=="YAW") {
            offset = 4;
        }
        else{
            //weird type, skip it
            return;
        }
        if(datum.find("$")){
            //weird data, skip it
            return;
        }
        local dataArr = IMUdata[type];
//        server.log(dataArr.len());
        if(dataArr.len() >= GESTURE_WINDOW){
//            server.log("HIT WINDOW!!!");
            dataArr.remove(0);
            dataArr.push(datum.tofloat());
        }else{
            dataArr.push(datum.tofloat());
        }       
    }
}

//only use this function for Acceleration!
function detectJerk(type){
    local dataArr = IMUdata[type];
    local gesturesSub = Gestures[type];
    local sum = 0;
    if(dataArr.len()==0){
        return;
    }
    foreach(val in dataArr){
        sum += val;
    }
    local mean = sum/dataArr.len();
    
//    local squareDiffSum = 0;
//    foreach(val in dataArr){
//        local diff = val - mean;
//        squareDiffSum += diff * diff;
//    }
//    local stdDiv = math.sqrt(squareDiffSum/dataArr.len());
    
    local val = dataArr[dataArr.len()-1];
//    server.log(readArray(IMUdata[type]));
//    server.log(readArray(Gestures[type]));
    if((val - mean) > JERK_THRESHOLD) {
        if(gesturesSub.len()==0 || gesturesSub[gesturesSub.len()-1]!="NEG"){
            gesturesSub.push("NEG");
            gestureTimeOffset[type]++;
            GestureCount[type]++;
        }
    }else if((val - mean) < -1*JERK_THRESHOLD) {
        if(gesturesSub.len()==0 || gesturesSub[gesturesSub.len()-1]!="POS"){
            gesturesSub.push("POS");
            gestureTimeOffset[type]++;
            GestureCount[type]++;
        }
    }else{
//        server.error(gesturecount);
        GestureCount[type]++;
        if(GestureCount[type] >= GESTURE_TIME + gestureTimeOffset[type]){
            
            local dataStr = readArray(gesturesSub);
            switch(type){
                case "AccX":
                    if(dataStr.find("[ POS ")!=null){
                        agent.send("DATA", GESTURES.PUSH);
                    }else if(dataStr.find("[ NEG ")!=null){
                        agent.send("DATA", GESTURES.PULL);
                    }
                    break;
                    
                case "AccY":
                    if(dataStr == "[ POS ]" || dataStr == "[ POS NEG ]"){
                        agent.send("DATA", GESTURES.LEFT);
                    }else if(dataStr == "[ NEG ]" || dataStr == "[ NEG POS ]"){
                        agent.send("DATA", GESTURES.RIGHT);
                    }else if((dataStr.find("[ NEG POS NEG")!=null) || (dataStr.find("[ POS NEG POS")!=null)){
                        agent.send("DATA", GESTURES.WAVE);
                    }
                    break;
                    
                case "AccZ":
                    if(dataStr.find("[ POS")!=null){
                        agent.send("DATA", GESTURES.UP);
                    }else if(dataStr.find("[ NEG")!=null){
                        agent.send("DATA", GESTURES.DOWN);
                    }
                    break;
            }
            
            
            gesturesSub.clear();
            GestureCount[type] = 0;
            gestureTimeOffset[type] = 0;
        }
    }    
}

//this is for the roational measurements
function detectSpin(type){
    local dataArr = IMUdata[type];
    local sum = 0;
    if(dataArr.len()==0){
        return;
    }
    foreach(val in dataArr){
        sum += val;
    }
    local mean = sum/dataArr.len();
    agent.send("GYRO", mean.tostring());
}

//function detectGesture(type){
//    local motions = Gestures[type];
//    switch(type){
//        case "AccX":
//        case "AccY":
//        case "AccZ":
//        case "RLL":
//        case "PCH":
//        case "YAW":
//    }
//}

function readserial(){
    local b = hardware.uart1289.read();
//    server.log("entering loop!");
	while(b >= 0){
//        server.log("got Data!");
//        STR += format("%c", b);
//	    server.log("Recieved from Lock: " + format("%c",b));
        if(b=='\n'){
            if(DATAPTS.tell() <= BUFFER_LEN){
                DATAPTS.writeblob(FIFO);
                parseData(readBlob(FIFO));
                detectJerk("AccX");
                detectJerk("AccY");
                detectJerk("AccZ");
                detectSpin("RLL");
            }else{
//                server.log("SENT DATA at: " + time());
//                agent.send("DATA", DATAPTS);
                DATAPTS.flush();
                DATAPTS.resize(BUFFER_LEN);
                DATAPTS.seek(0);
//                bufferCount = 0;
            }
            FIFO.flush();
            FIFO.resize(200);
            FIFO.seek(0);
            break;
        }
        FIFO.writen(b, 'c');
        
		b = hardware.uart1289.read();
	}
}

hardware.uart1289.configure(9600, 8, PARITY_NONE, 1, NO_CTSRTS, readserial);

//function poll(){
//    printArray(IMUdata.AccX); 
//    imp.wakeup(1, poll);
//}
//poll();
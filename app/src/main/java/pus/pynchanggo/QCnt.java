package pus.pynchanggo;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class QCnt {
    public int keynum;
    public int qcount =0;
    public Map<String , Integer> inqueue = new HashMap<>();

    public QCnt(){}
    public QCnt(int qkeynum){
        keynum=qkeynum;
    }
    public int getQcount(){return qcount;}
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("keynum", keynum);
        result.put("qcount", qcount);
        result.put("inqueue", inqueue);
        return result;
    }
}

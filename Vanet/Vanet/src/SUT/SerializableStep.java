package SUT;

import java.io.Serializable;

public class SerializableStep implements Serializable{
	String meth;
	Object instance;
    Object[] params;

    public SerializableStep(String _m, Object _i, Object[] _p) {
        meth = _m;
        instance = _i;
        params = _p;
    }
    public String getMethNameWithParams() {
    	return meth;
    }
    public String getMethNameWithoutParams() {
    	String[] array = meth.split("\\(");
    	return array[0];
    }
    public Object getInstance() {
    	return instance;
    }
    public Object[] getParams() {
    	return params;
    }

    public String toString() {
        String ret = /*instance + "." +*/ meth + "(";
        for (int i=0; i < params.length; i++) {
            if (i > 0) {
                ret += ",";
            }
            ret += params[i].toString();
        }
        return ret + ")";
    }
	public void setParams(Object[] p) {
	    	params = p;
	}
}
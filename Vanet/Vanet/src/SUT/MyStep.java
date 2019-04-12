package SUT;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MyStep implements Serializable{
    Method meth;
    Object instance;
    Object[] params;
    
    public MyStep() {
    	
    }
    public MyStep(Method _m, Object _i, Object[] _p) {
        meth = _m;
        instance = _i;
        params = _p;
    }
    public Method getMeth() {
    	return meth;
    }
    public Object getInstance() {
    	return instance;
    }
    public Object[] getParams() {
    	return params;
    }
   
    public void execute() throws InvocationTargetException, IllegalAccessException {
        meth.invoke(instance, params);
    }

    public String toString() {
        String ret = /*instance + "." +*/ meth.getName() + "(";
        for (int i=0; i < params.length; i++) {
            if (i > 0) {
                ret += ",";
            }
            ret += params[i].toString();
        }
        return ret + ")";
    }
}

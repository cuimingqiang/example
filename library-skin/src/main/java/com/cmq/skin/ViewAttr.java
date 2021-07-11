package com.cmq.skin;

import java.util.Map;
import java.util.TreeMap;

class ViewAttr {
    private Map<String,Integer> attrs = new TreeMap<>();

    public void addAttr(String name,int value){
        attrs.put(name,value);
    }

    public Map<String, Integer> getAttrs() {
        return attrs;
    }

    public boolean hasAttr(){
        return  attrs.size() > 0;
    }
}

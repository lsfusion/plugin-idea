package com.lsfusion.lang.classes;

public abstract class StaticFormatFileClass extends FileClass {

    @Override
    public DataClass op(DataClass compClass, boolean or, boolean string) {
        if (!(compClass instanceof StaticFormatFileClass)) return null;

        if(compClass.equals(this))
            return this;
        
        return RawClass.instance;
    }
}

package com.lsfusion.lang.classes.link;

import com.lsfusion.lang.classes.DataClass;
import com.lsfusion.lang.classes.RawClass;
import com.lsfusion.lang.classes.StaticFormatFileClass;

public abstract class StaticFormatLinkClass extends LinkClass {

    @Override
    public DataClass op(DataClass compClass, boolean or, boolean string) {
        if (!(compClass instanceof StaticFormatLinkClass)) return null;

        if(compClass.equals(this))
            return this;

        return RawLinkClass.instance;
    }
}
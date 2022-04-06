package com.github.orbyfied.carbon.item.behaviour;

import com.github.orbyfied.carbon.core.Service;
import com.github.orbyfied.carbon.core.ServiceManager;
import javassist.ClassPool;
import javassist.CtClass;

public class MixinEventBehaviourService extends Service {

    public MixinEventBehaviourService(ServiceManager manager) {
        super(manager);
    }

    @Override
    protected void start() {

        // TODO: mixin everything
        try {



        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    ///////////////////////////////////////////

}

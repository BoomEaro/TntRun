package ru.boomearo.tntrun.runnable;

import java.util.concurrent.TimeUnit;

public class RegenRunnable extends AbstractTimer {

    public RegenRunnable() {
        super("RegenRunnable", TimeUnit.SECONDS, 1);
    }

    @Override
    public void task() throws Throwable {

    }
    
    

}

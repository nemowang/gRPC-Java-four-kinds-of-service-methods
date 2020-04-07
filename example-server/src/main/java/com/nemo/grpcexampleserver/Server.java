package com.nemo.grpcexampleserver;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author nemo wang
 * @date 2020/04/07
 */
public class Server {
    public static void main(String[] args) {
        /*Injector injector = Guice.createInjector(
                new ServerModule("com.nemo.grpcexampleserver", "service.grpc.impl")
        );
        DefaultServerApplet applet = injector.getInstance(DefaultServerApplet.class);
        applet.withExceptionInterceptor().withValidator().run();*/

        Injector injector = Guice.createInjector(new AbstractModule() {
                    @Override
                    protected void configure() {

                    }
        });
    }
}

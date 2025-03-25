package com.seb.musicapp;

public class KeepAlive implements Runnable {

    private final Connector connector;

    public KeepAlive(Connector connector) {
        this.connector = connector;
    }
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(600000);
                connector.out.println("hello");
            } catch (InterruptedException ignored) {
            }
        }
    }
}
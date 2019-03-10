package io.linuxserver.fleet.thread;

public interface TaskListener {

    void onTaskStart(String message);

    void onTaskOutput(String output);

    void onTaskEnd(String message);
}

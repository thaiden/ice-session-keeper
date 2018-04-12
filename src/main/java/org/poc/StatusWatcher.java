package org.poc;


public interface StatusWatcher {
    void onSuccess(String id);
    void onFailure(String id, Exception e);
    void onTermination(String id);
}

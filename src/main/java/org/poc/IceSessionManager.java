package org.poc;

public interface IceSessionManager {
    String startSession(String remoteSdp);
    void stopSession(String sessionId);

    /**
     * implmented for cases when all sessions need to be stopped no matter what
     */
    void shutdown();
}

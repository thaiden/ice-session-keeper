package org.poc.session;

import java.util.*;
import java.util.concurrent.*;

import org.poc.IceSession;
import org.poc.IceSessionManager;
import org.poc.logger.Logger;
import org.poc.logger.LoggerFactory;

public class IceSessionManagerImpl implements IceSessionManager {

    private static Logger log = LoggerFactory.getLogger();
    private Map<String, IceSession> iceSessionMap;

    private ScheduledExecutorService executorService;

    public IceSessionManagerImpl() {
        this.iceSessionMap = new ConcurrentHashMap<>();
        this.executorService = Executors.newScheduledThreadPool(10);
    }

    @Override
    public String startSession(String remoteSdp) {

        try {
            IceSession iceSession = new IceSessionImpl(remoteSdp, executorService);

            if (iceSession.start()) {
                iceSessionMap.put(iceSession.id(), iceSession);

                return iceSession.id();
            }

        } catch (Exception e) {
            log.error("Failed to start session :", e);
        }

        return null;
    }

    @Override
    public void stopSession(String sessionId) {
        try {
            if (iceSessionMap.containsKey(sessionId)) {
                IceSession iceSession = iceSessionMap.get(sessionId);

                if (iceSession.started()) {
                    iceSession.stop();
                }

                iceSessionMap.remove(sessionId);
            }
        } catch (Exception e) {
            log.error("Failed to stop session :", e);
        }
    }

    @Override
    public void shutdown() {
        iceSessionMap.values().forEach(s -> {
            if (s.started()) s.stop();
        });

        iceSessionMap.clear();
    }
}

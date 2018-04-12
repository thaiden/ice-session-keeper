package org.poc.session;

import static org.poc.sdp.SdpParser.getIceCandidates;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.poc.IceSession;
import org.poc.StatusWatcher;
import org.poc.logger.Logger;
import org.poc.util.JsonUtil;
import org.poc.CircuitBreaker;
import org.poc.ice.IceCandidate;
import org.poc.logger.LoggerFactory;
import org.poc.sdp.MediaInfo;


public class IceSessionImpl implements IceSession, StatusWatcher {
    protected static final Logger log = LoggerFactory.getLogger();
    private static final int PERMITTED_CIRCUIT_BREAKER_COUNT = 20;
    private static final int REDUCED_CIRCUIT_BREAKER_COUNT = 10;
    private static final int REGULAR_HEALTH_CHECK_RATE_MILLIS = 12000;
    private static final int REDUCED_HEALTH_CHECK_RATE_MILLIS = REGULAR_HEALTH_CHECK_RATE_MILLIS * 3;

    private boolean started;

    private Map<String, IceSessionCandidate> iceSessionCandidateMap;
    private Map<String, Future> scheduledSessions;

    private final UUID id;
    private final String remoteSdp;
    private final ScheduledExecutorService executorService;


    public IceSessionImpl(String remoteSdp, ScheduledExecutorService executorService) {
        this.id = UUID.randomUUID();
        this.remoteSdp = remoteSdp;
        this.executorService = executorService;

        this.iceSessionCandidateMap = new ConcurrentHashMap<>();
        this.scheduledSessions = new ConcurrentHashMap<>();
    }

    @Override
    public String id() {
        return id.toString();
    }

    @Override
    public boolean started() {
        return started;
    }

    @Override
    public boolean start() {
        try {
            Collection<IceCandidate> candidates = getUniqueCandidates(remoteSdp);

            candidates.stream()
                      .filter(candidate -> !iceSessionCandidateMap.containsKey(candidate.getIceUfrag()))
                      .forEach(candidate -> {
                          IceSessionCandidate sessionCandidate = new IceSessionCandidate(candidate, this);

                          if (sessionCandidate.start()) {
                              updateStatus(true);

                              scheduleHealthCheck(candidate.getIceUfrag(), sessionCandidate, REGULAR_HEALTH_CHECK_RATE_MILLIS);
                          }
                      });
        } catch (Exception e) {
            log.error("exception", e);
        }

        return started();
    }

    @Override
    public void stop() {
        try {
            Collection<IceCandidate> candidates = getUniqueCandidates(remoteSdp);

            for (IceCandidate candidate : candidates) {

                IceSessionCandidate sessionCandidate = iceSessionCandidateMap.getOrDefault(candidate.getIceUfrag(), null);

                if (sessionCandidate != null) {
                    stopHealthCheck(sessionCandidate);
                    sessionCandidate.stop();
                }
            }

            iceSessionCandidateMap.clear();

            if (!scheduledSessions.isEmpty()) {
                log.error("Unexplained behavior, all scheduled health checks should've been removed");

                scheduledSessions.values().forEach(h -> {
                    try {
                        h.cancel(true);
                    } catch (Exception ignored) {}
                });
            }

            updateStatus(false);
        } catch (Exception e) {
            log.error("exception", e);
        }
    }

    @Override
    public void onSuccess(String id) {
        CircuitBreaker circuitBreaker = iceSessionCandidateMap.getOrDefault(id, null);

        if (circuitBreaker != null) {
            if (circuitBreaker.counter() >= REDUCED_CIRCUIT_BREAKER_COUNT) {
                rescheduleWithRate(id, REGULAR_HEALTH_CHECK_RATE_MILLIS);
            }
            circuitBreaker.reset();
        }
    }

    @Override
    public void onFailure(String id, Exception e) {
        if (e != null) {
            log.error("Error occurred", e);
        }

        CircuitBreaker circuitBreaker = iceSessionCandidateMap.getOrDefault(id, null);

        if (circuitBreaker != null) {
            if (circuitBreaker.counter() == REDUCED_CIRCUIT_BREAKER_COUNT) {
                rescheduleWithRate(id, REDUCED_HEALTH_CHECK_RATE_MILLIS);
                circuitBreaker.increment();

            } else if (circuitBreaker.counter() >= PERMITTED_CIRCUIT_BREAKER_COUNT) {
                onTermination(id);
                circuitBreaker.reset();

            } else {
                circuitBreaker.increment();
            }
        }
    }

    @Override
    public void onTermination(String id) {
        IceSessionCandidate sessionCandidate = iceSessionCandidateMap.getOrDefault(id, null);
        if (sessionCandidate != null) {
            log.info("Ice candidate asked to be terminated id: " + id);
            stopHealthCheck(sessionCandidate);
            sessionCandidate.stop();
            iceSessionCandidateMap.remove(id);
        }
    }

    private void rescheduleWithRate(String id, long period) {
         IceSessionCandidate sessionCandidate = iceSessionCandidateMap.getOrDefault(id, null);

        if (sessionCandidate != null) {
            stopHealthCheck(sessionCandidate);
            scheduleHealthCheck(id, sessionCandidate, period);
        }
    }

    private void scheduleHealthCheck(String id, IceSessionCandidate candidate, long period) {
        if (!iceSessionCandidateMap.containsKey(id)) {
            iceSessionCandidateMap.put(id, candidate);

            scheduledSessions.put(candidate.id(),
                                  executorService.scheduleAtFixedRate(candidate::sendKeepAlive, 1, period, TimeUnit.MILLISECONDS));
        }
    }

    private void stopHealthCheck(IceSessionCandidate sessionCandidate) {
        try {
            scheduledSessions.get(sessionCandidate.id()).cancel(true);
        } catch (Exception ignored) {
            log.error("Error stopping ice candidate", ignored);
        }

        scheduledSessions.remove(sessionCandidate.id());
    }

    private synchronized void updateStatus(boolean value) {
        started = value;
    }

    private Collection<IceCandidate> getUniqueCandidates(String remoteSdp) {
        List<IceCandidate> candidateList = getIceCandidates(JsonUtil.fromJson(remoteSdp, MediaInfo.class));

        Map<String, IceCandidate> uniqueCandidates = new HashMap<>();

        candidateList.forEach(c -> {
            uniqueCandidates.computeIfAbsent(c.getIceUfrag(), k -> c);
        });

        return uniqueCandidates.values();
    }

}

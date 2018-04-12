package org.poc.session;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.poc.CircuitBreaker;
import org.poc.StatusWatcher;
import org.poc.ice.IceCandidate;
import org.poc.logger.LoggerFactory;

import org.poc.logger.Logger;
import org.poc.stun.MessageHeader;
import org.poc.stun.SharedSecret;
import org.poc.stun.attribute.Attribute;
import org.poc.stun.attribute.AttributeFactory;

public class IceSessionCandidate implements CircuitBreaker {
    protected static final Logger log = LoggerFactory.getLogger();

    private static final String LOAD_TEST_ICE_UFRAG = "aaaaaaaaaaaaaaaaa";
    private static final String LOAD_TEST_ICE_PWD = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
    private static int SOCKET_READ_TIMEOUT = 750;

    private DatagramSocket clientSocket;
    private final IceCandidate candidate;
    private final StatusWatcher statusWatcher;


    private Integer failureCount = 0;
    private MessageHeader header;

    private final Lock lock = new ReentrantLock();

    public IceSessionCandidate(IceCandidate candidate, StatusWatcher statusWatcher) {
        this.candidate = candidate;
        this.statusWatcher = statusWatcher;
    }

    public boolean start() {
        try {
            clientSocket = establishConnection();

            return true;
        } catch (IOException e) {
            statusWatcher.onTermination(id());
        }

        return false;
    }

    public void sendKeepAlive() {
        lock.lock();

        try {
            header = new MessageHeader(MessageHeader.BINDING_REQUEST);
            SharedSecret sharedSecret = new SharedSecret(candidate.getIceUfrag() + ":" + LOAD_TEST_ICE_UFRAG,
                                                         candidate.getIcePasswd(),
                                                         SharedSecret.CredentialsType.SHORT_TERM);

            Attribute username = AttributeFactory.createUsernameAttribute(sharedSecret.getUsername());
            Attribute integrity = AttributeFactory.createIntegrityAttributes(sharedSecret);
            Attribute priority = AttributeFactory.createPriorityAttribute(candidate.getIceCandidateInfo().getPriority());

            header.addAttribute(priority);
            header.addAttribute(username);
            header.addAttribute(integrity);

            if (clientSocket != null && clientSocket.isConnected()) {
                log.debug(
                    String.format("udp user=%s, local password=%d bytes, remote user=%s, remote password=%d bytes, address=%s",
                                  LOAD_TEST_ICE_UFRAG,
                                  LOAD_TEST_ICE_PWD.getBytes().length,
                                  candidate.getIceUfrag(),
                                  candidate.getIcePasswd().getBytes().length,
                                  candidate.getAddress().toString()));

                final byte[] data = header.encode();
                clientSocket.send(new DatagramPacket(data, data.length, candidate.getAddress()));

                tryToReceiveResponse();
            }
        } catch (SocketException e) {
            log.info("Failed to connect to the socket, attempting reconnect");
            try {
                final byte[] data = header.encode();

                clientSocket = establishConnection();
                clientSocket.send(new DatagramPacket(data, data.length, candidate.getAddress()));

                tryToReceiveResponse();

            } catch (SocketTimeoutException se) {
                statusWatcher.onFailure(id(), null);

            } catch (Exception se) {
                // time to terminate candidate, since we cant establish connection anymore
                statusWatcher.onTermination(id());
            }
        } catch (SocketTimeoutException se) {
            statusWatcher.onFailure(id(), null);
        } catch (Exception e) {
            statusWatcher.onFailure(id(), e);
        }

        lock.unlock();
    }

    private DatagramSocket establishConnection() throws SocketException {
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(SOCKET_READ_TIMEOUT );
        socket.connect(candidate.getAddress());

        return socket;
    }

    private void tryToReceiveResponse() throws Exception {
        DatagramPacket receive = new DatagramPacket(new byte[200], 200);

        /**
         BINDING_REQUEST:
         BINDING_RESPONSE:
         BINDING_ERROR_RESPONSE;
         */
        clientSocket.receive(receive);
        MessageHeader response = MessageHeader.decode(receive.getData());

        if (Arrays.equals(response.getTransactionId(), header.getTransactionId())) {
            if (response.getMessageType() == MessageHeader.BINDING_ERROR_RESPONSE
                && (Objects.equals(response.getTransactionID(), header.getTransactionID()))) {

                log.error("Received error response from binding request");
                statusWatcher.onFailure(id(), null);

            } else if (response.getMessageType() == MessageHeader.BINDING_RESPONSE) {
                statusWatcher.onSuccess(id());

            } else {
                // TODO: for now just mark it as failure
                statusWatcher.onFailure(id(), new Exception("Strange reponse type"));
            }
        }
    }

    @Override
    public String id() {
        return candidate.getIceUfrag();
    }

    @Override
    public int counter() {
        return failureCount;
    }

    @Override
    public void increment() {
        setFailureCount(failureCount + 1);
    }

    @Override
    public void reset() {
        setFailureCount(0);
    }

    private synchronized void setFailureCount(int count) {
        this.failureCount = count;
    }

    public void stop() {
        lock.lock();
        try {
            if (clientSocket != null && clientSocket.isConnected()) {
                clientSocket.close();
            }
        } catch (Exception e) {
            log.error("Error occurred while stopping session", e);
        }
        lock.unlock();
    }
}

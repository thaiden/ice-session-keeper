package manual;

import java.util.concurrent.TimeUnit;

import org.poc.IceSessionManager;
import org.poc.session.IceSessionManagerImpl;
import org.poc.logger.Logger;
import org.poc.logger.LoggerFactory;

import com.google.common.util.concurrent.Uninterruptibles;

public class App {
    public static Logger logger = LoggerFactory.getLogger();

    public void main() {

        String remoteSdp = "{\"audioMuted\":false,\"videoMuted\":false,\"csis\":[3017129729,3082429184,3082429185],\"dtmfReceiveSupported\":true,\"sdp\":\"v=0\\r\\no=linus 0 1 IN IP4 173.37.38.38\\r\\ns=-\\r\\nc=IN IP4 173.37.38.38\\r\\nb=TIAS:1328000\\r\\nt=0 0\\r\\na=ice-lite\\r\\nm=audio 33434 RTP/AVP 98 99\\r\\nc=IN IP4 173.37.38.38\\r\\nb=TIAS:64000\\r\\na=content:main\\r\\na=sendrecv\\r\\na=rtpmap:98 opus/48000/2\\r\\na=fmtp:98 maxplaybackrate=48000;maxaveragebitrate=64000;stereo=1\\r\\na=rtpmap:99 telephone-event/8000\\r\\na=fmtp:99 0-15\\r\\na=rtcp-mux\\r\\na=label:100\\r\\na=ice-ufrag:JmkJBLPu\\r\\na=ice-pwd:qDAsVhVtpCOkDfh9buOzZiLrDU24K2P8\\r\\na=candidate:0 1 UDP 2130706431 173.37.38.38 33434 typ host\\r\\nm=audio 33434 RTP/AVP 98 99\\r\\nc=IN IP4 173.37.38.38\\r\\nb=TIAS:64000\\r\\na=content:main\\r\\na=sendrecv\\r\\na=rtpmap:98 opus/48000/2\\r\\na=fmtp:98 maxplaybackrate=48000;maxaveragebitrate=64000;stereo=1\\r\\na=rtpmap:99 telephone-event/8000\\r\\na=fmtp:99 0-15\\r\\na=rtcp-mux\\r\\na=label:100\\r\\na=ice-ufrag:JmkJBLPu\\r\\na=ice-pwd:qDAsVhVtpCOkDfh9buOzZiLrDU24K2P8\\r\\na=candidate:0 1 UDP 2130706431 173.37.38.38 33434 typ host\\r\\nm=video 33434 RTP/AVP 107\\r\\nc=IN IP4 173.37.38.38\\r\\nb=TIAS:600000\\r\\na=content:main\\r\\na=sendrecv\\r\\na=rtpmap:107 H264/90000\\r\\na=fmtp:107 profile-level-id=42000C;packetization-mode=0;max-mbps=27600;max-fs=920;max-fps=3000;max-br=600;max-dpb=891;level-asymmetry-allowed=1\\r\\na=rtcp-fb:* nack pli\\r\\na=rtcp-mux\\r\\na=label:200\\r\\na=ice-ufrag:JmkJBLPu\\r\\na=ice-pwd:qDAsVhVtpCOkDfh9buOzZiLrDU24K2P8\\r\\na=candidate:0 1 UDP 2130706431 173.37.38.38 33434 typ host\\r\\nm=video 33434 RTP/AVP 118\\r\\nc=IN IP4 173.37.38.38\\r\\nb=TIAS:600000\\r\\na=content:slides\\r\\na=sendrecv\\r\\na=rtpmap:118 H264/90000\\r\\na=fmtp:118 profile-level-id=42000C;packetization-mode=1;max-mbps=27600;max-fs=920;max-fps=3000;max-br=600;max-dpb=891;level-asymmetry-allowed=1\\r\\na=rtcp-fb:* nack pli\\r\\na=rtcp-mux\\r\\na=label:300\\r\\na=ice-ufrag:JmkJBLPu\\r\\na=ice-pwd:qDAsVhVtpCOkDfh9buOzZiLrDU24K2P8\\r\\na=candidate:0 1 UDP 2130706431 173.37.38.38 33434 typ host\\r\\n\",\"type\":\"SDP\"}";


        IceSessionManager manager = new IceSessionManagerImpl();

        String id = manager.startSession(remoteSdp);

        if (id != null) {
            Uninterruptibles.sleepUninterruptibly(10, TimeUnit.MINUTES);
            manager.stopSession(id);
        }

    }

    public static void main(String[] args) {
        LoggerFactory.initLogger();

        try {
            // only enables for test
            new App().main();
        } catch (Throwable t) {
            logger.error("Error while running app", t);
        }
    }
}

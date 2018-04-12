package org.poc;

import java.io.IOException;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.poc.stun.attribute.Attribute;
import org.poc.stun.attribute.AttributeFactory;
import org.poc.stun.util.UtilityException;
import org.poc.util.JsonUtil;

import org.poc.ice.IceCandidate;
import org.poc.sdp.MediaInfo;
import org.poc.sdp.SdpParser;
import org.poc.stun.MessageHeader;
import org.poc.stun.SharedSecret;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    private final String localUfrag = "aaaaaaaaaaaaaaaaa";
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    public void testApp()
    {
        assertTrue( true );
    }


    public void testHeaderIntegrity() {
        String remoteSdp = "{\"audioMuted\":false,\"videoMuted\":false,\"csis\":[29639937,584893696,584893697]," +
            "\"dtmfReceiveSupported\":true,\"sdp\":\"v=0\\r\\no=linus 0 1 IN IP4 173.37.38.37\\r\\ns=-\\r\\nc=IN IP4 173.37.38.37\\r\\nb=TIAS:1328000\\r\\nt=0 0\\r\\na=ice-lite\\r\\nm=audio 33434 RTP/AVP 98 99\\r\\nc=IN IP4 173.37.38.37\\r\\nb=TIAS:64000\\r\\na=content:main\\r\\na=sendrecv\\r\\na=rtpmap:98 opus/48000/2\\r\\na=fmtp:98 maxplaybackrate=48000;maxaveragebitrate=64000;stereo=1\\r\\na=rtpmap:99 telephone-event/8000\\r\\na=fmtp:99 0-15\\r\\na=rtcp-mux\\r\\na=label:100\\r\\na=ice-ufrag:JjBjSJLh\\r\\na=ice-pwd:VLhnm47ZazD63oSVS7gXqWlNbJ1Yp/8D\\r\\na=candidate:0 1 UDP 2130706431 173.37.38.37 33434 typ host\\r\\nm=audio 33434 RTP/AVP 98 99\\r\\nc=IN IP4 173.37.38.37\\r\\nb=TIAS:64000\\r\\na=content:main\\r\\na=sendrecv\\r\\na=rtpmap:98 opus/48000/2\\r\\na=fmtp:98 maxplaybackrate=48000;maxaveragebitrate=64000;stereo=1\\r\\na=rtpmap:99 telephone-event/8000\\r\\na=fmtp:99 0-15\\r\\na=rtcp-mux\\r\\na=label:100\\r\\na=ice-ufrag:JjBjSJLh\\r\\na=ice-pwd:VLhnm47ZazD63oSVS7gXqWlNbJ1Yp/8D\\r\\na=candidate:0 1 UDP 2130706431 173.37.38.37 33434 typ host\\r\\nm=video 33434 RTP/AVP 107\\r\\nc=IN IP4 173.37.38.37\\r\\nb=TIAS:600000\\r\\na=content:main\\r\\na=sendrecv\\r\\na=rtpmap:107 H264/90000\\r\\na=fmtp:107 profile-level-id=42000C;packetization-mode=0;max-mbps=27600;max-fs=920;max-fps=3000;max-br=600;max-dpb=891;level-asymmetry-allowed=1\\r\\na=rtcp-fb:* nack pli\\r\\na=rtcp-mux\\r\\na=label:200\\r\\na=ice-ufrag:JjBjSJLh\\r\\na=ice-pwd:VLhnm47ZazD63oSVS7gXqWlNbJ1Yp/8D\\r\\na=candidate:0 1 UDP 2130706431 173.37.38.37 33434 typ host\\r\\nm=video 33434 RTP/AVP 118\\r\\nc=IN IP4 173.37.38.37\\r\\nb=TIAS:600000\\r\\na=content:slides\\r\\na=sendrecv\\r\\na=rtpmap:118 H264/90000\\r\\na=fmtp:118 profile-level-id=42000C;packetization-mode=1;max-mbps=27600;max-fs=920;max-fps=3000;max-br=600;max-dpb=891;level-asymmetry-allowed=1\\r\\na=rtcp-fb:* nack pli\\r\\na=rtcp-mux\\r\\na=label:300\\r\\na=ice-ufrag:JjBjSJLh\\r\\na=ice-pwd:VLhnm47ZazD63oSVS7gXqWlNbJ1Yp/8D\\r\\na=candidate:0 1 UDP 2130706431 173.37.38.37 33434 typ host\\r\\n\",\"type\":\"SDP\"}";

        MediaInfo mediaInfo = JsonUtil.fromJson(remoteSdp, MediaInfo.class);

        assertTrue(mediaInfo != null);
        List<IceCandidate> candidateList = SdpParser.getIceCandidates(mediaInfo);

        candidateList.forEach(c -> System.out.println(c.toString()));

        assertTrue(candidateList.size() == 4);

        IceCandidate candidate = candidateList.get(0);

        MessageHeader header = new MessageHeader(MessageHeader.BINDING_REQUEST);
        SharedSecret sharedSecret = new SharedSecret(candidate.getIceUfrag() + ":" + localUfrag ,
                                                     candidate.getIcePasswd(),
                                                     SharedSecret.CredentialsType.SHORT_TERM);

        Attribute integrity = AttributeFactory.createIntegrityAttributes(sharedSecret);
        Attribute username = AttributeFactory.createUsernameAttribute(candidate.getIceUfrag(), localUfrag);
        Attribute priority = AttributeFactory.createPriorityAttribute(candidate.getIceCandidateInfo().getPriority());

        assertTrue(header.encode().length == 20);


        header.addAttribute(username);
        header.addAttribute(priority);
        header.addAttribute(integrity);

        header.encode();

        assertTrue(integrity.getEncodedContent() != null);
        assertEquals(integrity.getEncodedContent().length, integrity.getDataLength() + Attribute.HEADER_LENGTH);
    }
}

/*
      "audioMuted": false,
      "videoMuted": false,
      "csis": [
      29639937,
          584893696,
          584893697
      ],
      "dtmfReceiveSupported": true,
      "sdp": "v=0rno=linus 0 1 IN IP4 173.37.38.37rns=-rnc=IN IP4 173.37.38.37rnb=TIAS:1328000rnt=0 0rna=ice-liternm=audio 33434 RTP/AVP 98 99rnc=IN IP4 173.37.38.37rnb=TIAS:64000rna=content:mainrna=sendrecvrna=rtpmap:98 opus/48000/2rna=fmtp:98 maxplaybackrate=48000;maxaveragebitrate=64000;stereo=1rna=rtpmap:99 telephone-event/8000rna=fmtp:99 0-15rna=rtcp-muxrna=label:100rna=ice-ufrag:JjBjSJLhrna=ice-pwd:VLhnm47ZazD63oSVS7gXqWlNbJ1Yp/8Drna=candidate:0 1 UDP 2130706431 173.37.38.37 33434 typ hostrnm=audio 33434 RTP/AVP 98 99rnc=IN IP4 173.37.38.37rnb=TIAS:64000rna=content:mainrna=sendrecvrna=rtpmap:98 opus/48000/2rna=fmtp:98 maxplaybackrate=48000;maxaveragebitrate=64000;stereo=1rna=rtpmap:99 telephone-event/8000rna=fmtp:99 0-15rna=rtcp-muxrna=label:100rna=ice-ufrag:JjBjSJLhrna=ice-pwd:VLhnm47ZazD63oSVS7gXqWlNbJ1Yp/8Drna=candidate:0 1 UDP 2130706431 173.37.38.37 33434 typ hostrnm=video 33434 RTP/AVP 107rnc=IN IP4 173.37.38.37rnb=TIAS:600000rna=content:mainrna=sendrecvrna=rtpmap:107 H264/90000rna=fmtp:107 profile-level-id=42000C;packetization-mode=0;max-mbps=27600;max-fs=920;max-fps=3000;max-br=600;max-dpb=891;level-asymmetry-allowed=1rna=rtcp-fb:* nack plirna=rtcp-muxrna=label:200rna=ice-ufrag:JjBjSJLhrna=ice-pwd:VLhnm47ZazD63oSVS7gXqWlNbJ1Yp/8Drna=candidate:0 1 UDP 2130706431 173.37.38.37 33434 typ hostrnm=video 33434 RTP/AVP 118rnc=IN IP4 173.37.38.37rnb=TIAS:600000rna=content:slidesrna=sendrecvrna=rtpmap:118 H264/90000rna=fmtp:118 profile-level-id=42000C;packetization-mode=1;max-mbps=27600;max-fs=920;max-fps=3000;max-br=600;max-dpb=891;level-asymmetry-allowed=1rna=rtcp-fb:* nack plirna=rtcp-muxrna=label:300rna=ice-ufrag:JjBjSJLhrna=ice-pwd:VLhnm47ZazD63oSVS7gXqWlNbJ1Yp/8Drna=candidate:0 1 UDP 2130706431 173.37.38.37 33434 typ hostrn",
      "type": "SDP"
  }
  */

package org.poc.sdp;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class MediaInfo {
    enum MediaType {
        SDP,
        SIP
    }
    private final boolean audioMuted;
    private final boolean videoMuted;
    private final List<Long>  csis;
    private final boolean dtmfReceiveSupported;
    private final String sdp;
    private final MediaType type;

    @JsonCreator
    public MediaInfo(@JsonProperty("audioMuted") boolean audioMuted,
                     @JsonProperty("videoMuted") boolean videoMuted,
                     @JsonProperty("csis") List<Long> csis,
                     @JsonProperty("dtmfReceiveSupported") boolean dtmfReceiveSupported,
                     @JsonProperty("sdp") String sdp,
                     @JsonProperty("type") MediaType type) {
        this.audioMuted = audioMuted;
        this.videoMuted = videoMuted;
        this.csis = csis;
        this.dtmfReceiveSupported = dtmfReceiveSupported;
        this.sdp = sdp;
        this.type = type;
    }

    public boolean isAudioMuted() {
        return audioMuted;
    }

    public boolean isVideoMuted() {
        return videoMuted;
    }

    public List<Long> getCsis() {
        return csis;
    }

    public boolean isDtmfReceiveSupported() {
        return dtmfReceiveSupported;
    }

    public String getSdp() {
        return sdp;
    }

    public MediaType getType() {
        return type;
    }
}

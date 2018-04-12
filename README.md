#Overview

Purpose of POC is to perform basic keep alive server using ICE protocol based on :
 * [rfc5389](https://tools.ietf.org/html/rfc5389)
 
Classes which are implementing ICE and STUN are taken from https://github.com/jitsi/ice4j and some of them are slightly modified 

Important is to mention that candidate field in SDP needs to be in this format:
```
 a=candidate:0 1 UDP 2130706431 173.37.38.38 33434 typ host\\r\
```

####Local SDP
Provide this sdp in your JOIN/Call request
 
```
v=0\\r\
o=789 1255457702 1255457702 IN IP4 198.101.245.223\\r\
s=Audio/Video SDP\\r\
c=IN IP4 198.101.245.223\\r\
t=0 0\\r\
a=ice-ufrag:aaaaaaaaaaaaaaaaa\\r\
a=ice-pwd:aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\\r\
m=audio 2350 RTP/AVP 100 101\\r\
a=rtpmap:100 opus/48000/2\\r\
a=rtpmap:101 telephone-event/8000\\r\
a=inactive\\r\
a=rtcp-mux\\r\
a=candidate:1 1 udp 2113937151 10.92.114.14 2023 typ host generation 0\\r\
m=audio 2326 RTP/AVP 109\\r\
a=rtpmap:109 MP4A-LATM/90000\\r\
a=fmtp:109 bitrate=64000;profile-level-id=24;object=23\\r\
a=rtcp:2326\\r\
a=inactive\\r\
m=video 2328 RTP/AVP 101\\r\
b=TIAS:6000000\\r\
a=rtpmap:101 H264/90000\\r\
a=fmtp:101 profile-level-id=428016;max-br=6000;max-mbps=400000;max-fs=9000;max-smbps=400000;max-fps=6000\\r\
a=fmtp:101 profile-level-id=428016;max-br=6000;max-mbps=400000;max-fs=9000;max-smbps=400000;max-fps=6000\\r\
a=inactive\\r\
a=content:main\\r\
a=rtcp:2328\\r\
a=rtcp-fb:* nack pli\\r\
a=label:11\\r\
a=rtcp-mux\\r\
a=candidate:1 1 udp 2113937151 10.92.114.14 2023 typ host generation 0\\r\
m=video 40043 RTP/AVP 101\\r\
a=content:slides\\r\
b=TIAS:1000000\\r\
a=rtpmap:101 H264/90000\\r\
a=fmtp:101 profile-level-id=42002A;packetization-mode=1\\r\
a=rtcp-fb:* nack pli\\r\
a=rtcp-mux\\r\
a=candidate:1 1 udp 2113937151 10.92.114.14 2023 typ host generation 0\\r\
a=inactive\\r\
``` 

####Example of full remote SDP
```
v=0\\r\
o=linus 0 1 IN IP4 173.37.38.39\\r\
s=-\\r\
c=IN IP4 173.37.38.39\\r\
b=TIAS:1328000\\r\
t=0 0\\r\
a=ice-lite\\r\
m=audio 33434 RTP/AVP 98 99\\r\
c=IN IP4 173.37.38.39\\r\
b=TIAS:64000\\r\
a=content:main\\r\
a=inactive\\r\
a=rtpmap:98 opus/48000/2\\r\
a=fmtp:98 maxplaybackrate=48000;maxaveragebitrate=64000;stereo=1\\r\
a=rtpmap:99 telephone-event/8000\\r\
a=fmtp:99 0-15\\r\
a=rtcp-mux\\r\
a=label:100\\r\
a=ice-ufrag:FCpwyoOg\\r\
a=ice-pwd:OkMtUtC1SNJv5JJ0lalbApFzBz6s8OqU\\r\
a=candidate:0 1 UDP 2130706431 173.37.38.39 33434 typ host\\r\
m=audio 33434 RTP/AVP 98 99\\r\
c=IN IP4 173.37.38.39\\r\
b=TIAS:64000\\r\
a=content:main\\r\
a=inactive\\r\
a=rtpmap:98 opus/48000/2\\r\
a=fmtp:98 maxplaybackrate=48000;maxaveragebitrate=64000;stereo=1\\r\
a=rtpmap:99 telephone-event/8000\\r\
a=fmtp:99 0-15\\r\
a=rtcp-mux\\r\
a=label:100\\r\
a=ice-ufrag:FCpwyoOg\\r\
a=ice-pwd:OkMtUtC1SNJv5JJ0lalbApFzBz6s8OqU\\r\
a=candidate:0 1 UDP 2130706431 173.37.38.39 33434 typ host\\r\
m=video 33434 RTP/AVP 107\\r\
c=IN IP4 173.37.38.39\\r\
b=TIAS:600000\\r\
a=content:main\\r\
a=inactive\\r\
a=rtpmap:107 H264/90000\\r\
a=fmtp:107 profile-level-id=42000C;packetization-mode=0;max-mbps=27600;max-fs=920;max-fps=3000;max-br=600;max-dpb=891;level-asymmetry-allowed=1\\r\
a=rtcp-fb:* nack pli\\r\
a=rtcp-mux\\r\
a=label:200\\r\
a=ice-ufrag:FCpwyoOg\\r\
a=ice-pwd:OkMtUtC1SNJv5JJ0lalbApFzBz6s8OqU\\r\
a=candidate:0 1 UDP 2130706431 173.37.38.39 33434 typ host\\r\
m=video 33434 RTP/AVP 118\\r\
c=IN IP4 173.37.38.39\\r\
b=TIAS:600000\\r\
a=content:slides\\r\
a=inactive\\r\
a=rtpmap:118 H264/90000\\r\
a=fmtp:118 profile-level-id=42000C;packetization-mode=1;max-mbps=27600;max-fs=920;max-fps=3000;max-br=600;max-dpb=891;level-asymmetry-allowed=1\\r\
a=rtcp-fb:* nack pli\\r\
a=rtcp-mux\\r\
a=label:300\\r\
a=ice-ufrag:SMJ+8b51\\r\
a=ice-pwd:UWuInEOArheNWqjqWXYCsFp2zjCHmFcR\\r\
a=candidate:0 1 UDP 2130706431 173.37.38.38 33434 typ host\\r\
a=inactive\\r\
```

#Usage

###Step 1
This library does not come with a specific logger library included, instead by default it will output message into stdout.
If project which will use this library has a logger, what you can do is:
 * Create a class which implements Logger.java interface from ice-session-keeper
 * In overridden methods put code which calls to you default logger library.
 * Before using ice-session-keeper call ```LoggerFactory.setLogger()``` and pass the class you created in step above

Example

```java
public class CustomLogger implements Logger {
    void info(String message) { add code here to use your default logger }    
    void trace(String message) {}
    void debug(String message) {}
    void warn(String message) {}
    void error(String message) {}
    void error(String message, Throwable t) {}
}
....
....
....
public void someMethod() {
    LoggerFactory.setLogger(new CustomLogger());
    ....
}
```


###Step 2
Once we've received remote SDP pass it to:  
```java
IceSessionManagerImpl iceSessionManager = new IceSessionManagerImpl();
// sessionId will be null of failure or error occurred, please refer to logs for more info
String sessionId = iceSessionManager.startSession(REMOTE_SDP);
```
###Step 3
To terminate session call: 
```java
iceSessionManager.stopSession(sessionId);
```

## INFO
Default frequency of keep-alive is 12 seconds, if there are lot failures rate will be reduced to 36 sec, and if failure continue 
to occur session will be terminated.






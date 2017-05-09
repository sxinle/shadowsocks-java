/*
 *   Copyright 2016 Author:NU11 bestoapache@gmail.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package shadowsocks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import shadowsocks.util.Config;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.Arrays;

import static org.junit.Assert.*;

public class SystemTest{

    public static Logger log = LogManager.getLogger(SystemTest.class.getName());

    @Before
    public void setUp(){
        log.info("Set up");
        Config.get().setPassowrd("testkey");
        Config.get().setMethod("aes-128-cfb");
        Config.get().setServer("127.0.0.1");
        Config.get().setPort(1024);
        Config.get().setLocalPort(2048);
        Config.get().setOTAEnabled(true);
    }
    @After
    public void tearDown(){
        log.info("Tear down");
    }
    @Test
    public void testStartStop() {

        Shadowsocks server = new Shadowsocks(true);
        Shadowsocks local = new Shadowsocks(false);
        //Can't shutdown before boot.
        assertFalse(server.shutdown());
        //Boot and shutdown
        assertTrue(server.boot());
        assertTrue(local.boot());
        assertTrue(server.shutdown());
        assertTrue(local.shutdown());
        //Boot again
        assertTrue(server.boot());
        //Two instances is not allowed.
        assertFalse(server.boot());
        assertTrue(server.shutdown());

    }

    private void testSimpleHttp(boolean ota) {

        // Config.get().setOTAEnabled(ota);

        // Shadowsocks server = new Shadowsocks(true);
        // Shadowsocks local = new Shadowsocks(false);

        // assertTrue(server.boot());
        // assertTrue(local.boot());
        Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 8388));
        HttpURLConnection conn = null;
        try{
            URL url = new URL("http://baidu.com");
            conn = (HttpURLConnection)url.openConnection(proxy);
            conn.setRequestMethod("GET");
            DataInputStream in1 = new DataInputStream(conn.getInputStream());
            byte [] result = new byte[8192];
            in1.read(result);
            DataInputStream in2 = new DataInputStream(this.getClass().getClassLoader().getResourceAsStream("result-example-com"));
            byte [] expect = new byte[8192];
            in2.read(expect);
            assertTrue(Arrays.equals(result, expect));
        }catch(IOException e){
            log.error("Failed with exception.", e);
            fail();
        }finally{
            if (conn != null) {
                conn.disconnect();
            }
            // assertTrue(server.shutdown());
            // assertTrue(local.shutdown());
        }
    }
    @Test
    public void testHttp() {
        testSimpleHttp(true);
    }

    @Test
    public void testHttpWithoutOTA() {
        testSimpleHttp(false);
    }
}

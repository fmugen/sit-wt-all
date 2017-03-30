package org.sitoolkit.wt.util.domain.reflectproxy;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class ReflectProxyStdoutListenerTest {

    @Test
    public void test001() {
        String[] messages = {
                "ProxyEnable    REG_DWORD    0x1",
                "ProxyServer    REG_SZ    127.0.0.1:8080",
                "ProxyOverride    REG_SZ    192.168.0.*;127.0.0.1"
        };

        ReflectProxyStdoutListener listener = new ReflectProxyStdoutListener();
        for (String message : messages) {
            listener.parse(message);
        }

        UserProxy userProxy = listener.getUserProxy();
        assertThat(userProxy.getProxyActive(), is("true"));
        assertThat(userProxy.getProxyHost(), is("127.0.0.1"));
        assertThat(userProxy.getProxyPort(), is("8080"));
        assertThat(userProxy.getNonProxyHosts(), is("192.168.0.*|127.0.0.1"));
    }

    @Test
    public void test002() {
        String[] messages = {
                "ProxyEnable    REG_DWORD    0x1",
                "ProxyServer    REG_SZ    127.0.0.1"
        };

        ReflectProxyStdoutListener listener = new ReflectProxyStdoutListener();
        for (String message : messages) {
            listener.parse(message);
        }

        UserProxy userProxy = listener.getUserProxy();
        assertThat(userProxy.getProxyActive(), is("true"));
        assertThat(userProxy.getProxyHost(), is("127.0.0.1"));
        assertThat(userProxy.getProxyPort(), is("80"));
        assertThat(userProxy.getNonProxyHosts(), is(""));
    }

    @Test
    public void test003() {
        String[] messages = {
                "ProxyEnable    REG_DWORD    0x1",
                "ProxyServer    REG_SZ    http=127.0.0.1:8080;https=127.0.0.2:8081;ftp=127.0.0.3:8082;socks=127.0.0.4:8083"
        };

        ReflectProxyStdoutListener listener = new ReflectProxyStdoutListener();
        for (String message : messages) {
            listener.parse(message);
        }

        UserProxy userProxy = listener.getUserProxy();
        assertThat(userProxy.getProxyActive(), is("true"));
        assertThat(userProxy.getProxyHost(), is("127.0.0.1"));
        assertThat(userProxy.getProxyPort(), is("8080"));
        assertThat(userProxy.getNonProxyHosts(), is(""));
    }
}

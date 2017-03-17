package org.sitoolkit.wt.util.domain.proxy;

import java.util.List;

@FunctionalInterface
public interface ProxyReadCallback {

    void proxyRead(List<String> proxy);
}

package com.prokmodular.comms;

import java.util.List;
import java.util.Map;

public interface ModuleScanStatusListener {
    public void scanComplete(Map<String, List<ModuleSerialConnection>> modulePorts);
}

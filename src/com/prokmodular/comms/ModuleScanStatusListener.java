package com.prokmodular.comms;

import com.prokmodular.ModuleInfo;

import java.util.List;

public interface ModuleScanStatusListener {
    void scanComplete(List<ModuleInfo> modules);
}

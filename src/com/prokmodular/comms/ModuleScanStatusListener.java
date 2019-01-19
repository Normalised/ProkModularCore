package com.prokmodular.comms;

import com.prokmodular.ProkModule;

import java.util.List;

public interface ModuleScanStatusListener {
    void scanComplete(List<ProkModule> modules);
}

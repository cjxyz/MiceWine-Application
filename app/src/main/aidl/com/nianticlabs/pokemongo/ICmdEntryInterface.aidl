package com.nianticlabs.pokemongo;

// This interface is used by utility on termux side.
interface ICmdEntryInterface {
    void windowChanged(in Surface surface);
    ParcelFileDescriptor getXConnection();
    ParcelFileDescriptor getLogcatOutput();
}
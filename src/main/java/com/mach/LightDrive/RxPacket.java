package com.mach.LightDrive;

final class RxPacket
{
    byte I1;
    byte I2;
    byte I3;
    byte I4;
    byte VIN;
    Status status;
    byte PWMVals;
    byte FW;
    
    RxPacket() {
        this.status = new Status();
        this.I1 = 0;
        this.I2 = 0;
        this.I3 = 0;
        this.I4 = 0;
        this.VIN = 0;
        this.PWMVals = 0;
        this.FW = 0;
    }
    
    byte[] GetBytes() {
        final byte[] tempdata = { this.I1, this.I2, this.I3, this.I4, this.VIN, this.status.GetRaw(), this.PWMVals, this.FW };
        return tempdata;
    }
    
    void SetBytes(final byte[] data) {
        this.I1 = data[0];
        this.I2 = data[1];
        this.I3 = data[2];
        this.I4 = data[3];
        this.VIN = data[4];
        this.status.SetRaw(data[5]);
        this.PWMVals = data[6];
        this.FW = data[7];
    }
}
